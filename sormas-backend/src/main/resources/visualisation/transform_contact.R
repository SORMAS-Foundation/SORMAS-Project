library(RPostgreSQL)
library(visNetwork)
library(dplyr)

envDefaults = c(
	"DB_USER" = "sormas_user", "DB_PASS" = "sormas_db",
	"DB_HOST" = "127.0.0.1", "DB_PORT" = "5432", "DB_NAME" = "sormas_db", 
	"CONTACT_IDS" = "",
	"OUTFILE" = "sormas_contact.html"
)
sysEnv = Sys.getenv(names(envDefaults), NA);
sysEnv = subset(sysEnv, sysEnv == sysEnv)


env = c(sysEnv, envDefaults)[names(envDefaults)]
	

DB_USER = env["DB_USER"]
DB_PASS = env["DB_PASS"]
DB_HOST = env["DB_HOST"]
DB_NAME = env["DB_NAME"]
DB_PORT = env["DB_PORT"]

OUTFILE = env["OUTFILE"]
CONTACT_IDS = env["CONTACT_IDS"]

defaultFont="font-family:'Open Sans', sans-serif, 'Source Sans Pro'"
mainStyle = paste(defaultFont, "color: #6591C4", ";font-weight: 600", "font-size: 1.6em", "text-align:center;", sep="; ")
submainStyle = paste(defaultFont, "text-align:center;", sep="; ")
footerStyle = defaultFont

#connection to db
con = dbConnect(PostgreSQL(), user=DB_USER, dbname=DB_NAME, password = DB_PASS, host=DB_HOST, port=DB_PORT)  # connect to load db

#query contact table and ratin only contacts parsed from Sys.getenv
if (CONTACT_IDS == "") {
	idCont = as.character(dbGetQuery(con," select id from public.contact")$id) # these are Sys.getenv("contact") parsed as character vector
	idContString = toString(idCont)
} else {
	idContString = CONTACT_IDS
}

sql_fmt_idCont = "select id, contactproximity, caze_id, person_id, contactstatus,resultingcase_id
                         from public.contact
                         where id in (%s) and deleted = FALSE and contactclassification != 'NO_CONTACT' and contactstatus != 'DROPPED' "
contactTable = dbGetQuery( con, sprintf(sql_fmt_idCont, idContString) )

#query case table selecting only cases thet have id in the contact set
idCase = unique(as.character(c(contactTable$caze_id, na.omit(contactTable$resultingcase_id)))) # unique id of cases that belong to the set of contacts and also contact that became ceses but have not contact
idCaseString = toString(idCase)
#guards against syntax exception due to empty id list
if (idCaseString == "") idCaseString = "NULL"
sql_fmt_idCase = "select  id, caseclassification, person_id from public.cases where id in (%s) and caseclassification != 'NO_CASE' and deleted = FALSE"
caseTable = dbGetQuery( con, sprintf(sql_fmt_idCase, idCaseString) )

## querying person table and keep only persons id that belong to either case or contact table
idPersonCase = unique(as.character(c(caseTable$person_id, contactTable$person_id))) # uniqur persons in either case or contact table
idPersonCaseString = toString(idPersonCase)
#guards against syntax exception due to empty id list
if (idPersonCaseString == "") idPersonCaseString = "NULL"
sql_fmt_idPerson = "select  id from public.person where id in (%s)"
personTable = dbGetQuery(con, sprintf(sql_fmt_idPerson, idPersonCaseString) )

dbDisconnect(con)


case_id = as.character(caseTable$id)
case_idCont = as.character(contactTable$caze_id)

#adding person id of cases in contact table
pIdCase = rep(NA, nrow(contactTable))
personIdCase = caseTable$person_id

for(i in 1:length(case_idCont))
{
  temp = case_idCont[i]
  for(j in 1:length(case_id))
  {
    if(temp == case_id[j] )
    {
      pIdCase[i] = personIdCase[j]
    }
    
  }
  
}
elist = data.frame(pIdCase, contactTable$person_id, contactTable$contactproximity)
colnames(elist) = c("from", "to", "contactproximity")

#status of each node or person in line list
Classification = rep("HEALTHY",nrow(personTable))
personId = personTable$id
casPersonId = caseTable$person_id
personClass = caseTable$caseclassification

for( i in 1:length(Classification))
{
  for (j in 1:nrow(caseTable))
  {
    if(personId[i] == casPersonId[j])
    {
      Classification[i] = personClass[j]
    }
  }
}

personTable = data.frame(personTable, Classification)

## defining contact categories based on proximity
elist$label = NA
elist$label[elist$contactproximity %in% c("FACE_TO_FACE_LONG","TOUCHED_FLUID","MEDICAL_UNSAVE","CLOTHES_OR_OTHER","PHYSICAL_CONTACT" )] = 1 
elist$label[!(elist$contactproximity %in% c("FACE_TO_FACE_LONG","TOUCHED_FLUID","MEDICAL_UNSAVE","CLOTHES_OR_OTHER","PHYSICAL_CONTACT" ))] = 2

## defining plotting parameters  

nodesS = personTable
edgesS = elist

#deleting duplicate edges
edgesS = distinct(edgesS, from, to, .keep_all = T)
# deleting edges linking a node to itselt
edgesS = edgesS[edgesS$from != edgesS$to,]

# defining edge attributes
#edgesS$label = edgesS$label
edgesS$smooth = TRUE
edgesS$dashes = TRUE
edgesS$dashes[edgesS$label == 1] = FALSE #  using broken lines for high risk contacts
edgesS$arrows = "to"

# defining node attributes
nodesS$group = nodesS$Classification
nodesS$label = nodesS$id
nodesS$value = 1
nodesS$shape = c("icon")
nodesS$code = c("f007")
#nodesS$shadow = F, 

# defining legend
addNodesS <- data.frame(label = c("Legend", "Healthy","Not_classified" ,"Suspected", "Probable", "Confirmed", "1 = High risk", "2 = Low risk"), shape = "icon",
                        icon.code = c("f0c0","f007", "f007", "f007", "f007", "f007", "f178", "f178"),
                        icon.size = c(0.1,25, 25, 25, 25, 25,25,25), icon.color = c("#0d0c0c","#17bd27", "#706c67", "#a88732", "#db890f", "#f70707", "#0d0c0c", "#0d0c0c"))
#plotting
g= visNetwork(nodesS, edgesS,  main = list(text = "Disease network diagram", style = mainStyle),
              submain = list(text = "The arrows indicate the direction of transmission", style = submainStyle), 
              footer = list(text = "Double click on the icon to open the associated case or contact data", style = footerStyle), 
              background = "white", annot = T, width = "100%" ) %>%
  visEdges(arrows = "to", color = "black") %>% 
  visOptions(selectedBy = "Classification",highlightNearest = TRUE,nodesIdSelection = TRUE) %>% 
  visGroups(groupname = "SUSPECT", size = 10, shape = "icon", icon = list( face ='FontAwesome', code = c( "f007"), color="#a88732")) %>%
  visGroups(groupname = "PROBABLE", size = 10, shape = "icon", icon = list( face ='FontAwesome', code = c( "f007"), color="#db890f")) %>%
  visGroups(groupname = "CONFIRMED", size = 10, shape = "icon", icon = list( face ='FontAwesome', code = c( "f007"), color="#f70707")) %>%
  visGroups(groupname = "NOT_CLASSIFIED", size = 10, shape = "icon", icon = list( face ='FontAwesome', code = c( "f007"), color="#706c67" )) %>%
  visGroups(groupname = "HEALTHY", size = 10, shape = "icon", icon = list( face ='FontAwesome', code = c( "f007"), color="#17bd27")) %>%
  addFontAwesome() %>%
  visLegend(addNodes = addNodesS, useGroups = F, position = "left", width = 0.2, ncol = 1, stepX = 100, stepY = 50) %>%  
  visPhysics(stabilization = F) %>%
  visInteraction(dragNodes = T, dragView = T, zoomView = T)


visNetwork::visSave(g, OUTFILE, selfcontained = FALSE)