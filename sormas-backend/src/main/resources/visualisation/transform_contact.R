library(RPostgreSQL)
library(epicontacts)
#library(threejs) #♦ only neede if we use 3D immages

DB_USER <- Sys.getenv("DB_USER")
DB_PASS <- Sys.getenv("DB_PASS")
DB_HOST <- Sys.getenv("DB_HOST")
DB_NAME <- Sys.getenv("DB_NAME")
DB_PORT <- Sys.getenv("DB_PORT")

#DATE_FROM <- Sys.getenv("DATE_FROM")
#DATE_TO <- Sys.getenv("DATE_TO")
DISEASES <- Sys.getenv("DISEASES")
OUTFILE <- Sys.getenv("OUTFILE")

diseases = unlist(strsplit(DISEASES, ",", TRUE))

con = dbConnect(PostgreSQL(), user=DB_USER, dbname=DB_NAME, password = DB_PASS, host=DB_HOST, port=DB_PORT)  # connect to loal db

caseTable = dbGetQuery(con," select * from public.cases")
personTable = dbGetQuery(con, " select * from public.person")
contactTable = dbGetQuery(con, " select * from public.contact")
symptomsTable = dbGetQuery(con, " select * from public.symptoms")
regionTable = dbGetQuery(con, " select * from public.region")
districtTable = dbGetQuery(con, " select * from public.district")
visitTable = dbGetQuery(con, " select * from public.visit")
dbDisconnect(con)


network_transform = function()
{
  fromDate = min(caseTable$reportdate)
  toDate = max(caseTable$reportdate)
  disease = diseases
  
  caseClass = c("CONFIRMED","NOT_CLASSIFIED","PROBABLE","SUSPECT", "NO_CASE") # "NO_CASE" are not included
  contactClass = c("CONFIRMED","NO_CONTACT","UNCONFIRMED")
  caseVar=c("id","creationdate","disease","investigateddate","reportdate","healthfacility_id","reportinguser_id",
            "person_id","symptoms_id","region_id","district_id","community_id", "caseclassification",
            "investigationstatus","hospitalization_id", "pregnant","epidata_id","epidnumber",
            "vaccinationdate","outcome","outcomedate","vaccination", "receptiondate","caseage",
            "classificationdate","therapy_id", "clinicalcourse_id","sequelae", "sequelaedetails")
  personVar = c("person_id","approximateage","approximateagetype", "burialdate","deathdate","occupationdetails",
                "occupationtype", "presentcondition","sex", "occupationfacility_id", "approximateagereferencedate")
  regionVar = c("region_id","name", "epidcode","population","growthrate")
  sympVar = c("symptoms_id", "onsetdate", "temperature")
  
  distVar = c("district_id","name", "epidcode")
  colnames(personTable)[1] = "person_id"
  colnames(regionTable)[1] = "region_id"
  colnames(districtTable)[1]= "district_id"
  colnames(symptomsTable)[1] = "symptoms_id"
  #selecting caces by case classfication
  caseTable = caseTable[caseTable$caseclassification %in% caseClass,]
  
  # extracting only cases within specified time interval and diseases of interest
  caseTable = caseTable[((((caseTable$reportdate >= fromDate) & (caseTable$reportdate <= toDate)))) & (caseTable$disease %in% disease), ]
  
  ## merging data  #########################
  
  caseTable = caseTable[,colnames(caseTable) %in% caseVar]
  personTable = personTable[,colnames(personTable) %in% personVar]
  regionTable = regionTable[,colnames(regionTable) %in% regionVar]
  districtTable = districtTable[,colnames(districtTable) %in% distVar]
  symptomsTable = symptomsTable[,colnames(symptomsTable) %in% sympVar]
  
  personCase = merge(caseTable,personTable, by = "person_id")
  personCaseRegion = merge(personCase,regionTable, by = "region_id")
  personCaseRegionDist = merge(personCaseRegion,districtTable, by = "district_id")
  personCaseRegionDistSymp = merge(personCaseRegionDist,symptomsTable, by = "symptoms_id")
  
  ##### contact statistics  #########
  # extracting useful contacts
  contPerClassNet = contactTable[contactTable$contactclassification %in% contactClass,]
  
  # extracting the case person id and contact person id for each contact
  #caze_id=as.character(contPerClassNet$caze_id)
  #person_id=as.character(contPerClassNet$person_id)
  elist = data.frame(contPerClassNet$caze_id, contPerClassNet$person_id,contPerClassNet$resultingcase_id,contPerClassNet$contactproximity  )
  #colnames(elist) = data.frame(caze_id,person_id) # edge list with person id and case id. This is the edgelist for all contacts in the systems.
  #elist = apply(elist,2,as.character) # converting ids to character types in order to plot latter
  colnames(elist) = c("caze_id", "contactPerson_id", "resultingcase_id", "contactproximity")
  
  # Merging contact and case tables  to know the coresponding person_id of the cases incilved in the contacts in elist dataframe
  
  casesRealNew =  data.frame(caze_id = personCaseRegionDistSymp$id, casePerson_id = personCaseRegionDistSymp$person_id, 
                             caseClass = personCaseRegionDistSymp$caseclassification)
  casesRealNew$caseClass = as.character(casesRealNew$caseClass)
  casesRealelist = merge(elist,casesRealNew, by = "caze_id") # these are the contact of only the cases we need
  
  ## number of cases with and without contacts
  nCasesReal = length(unique(casesRealNew$caze_id))
  #uniqueId_allCases = unique(na.omit(c(casesRealelist$caze_id, casesRealelist$resultingcase_id))) # uniqeu person id of all cases and contacts that bejcame cases
  uniqueId_CasesWithContact = unique(casesRealelist$caze_id) 
  
  nCasNoCont = nrow(casesRealNew[!(casesRealNew$caze_id %in% uniqueId_CasesWithContact),]) # number of cases without contacts
  nCasNoContPro = round(nCasNoCont/nCasesReal*100)
  nCasCont = nrow(casesRealNew[casesRealNew$caze_id %in% uniqueId_CasesWithContact,]) # number of cases with contacts
  nCasContPro = round(nCasCont/nCasesReal*100)
  
  
  ### Definitng classification status for all persons involved in contacts in elist
  pIdCon = as.character(casesRealelist[,2])  # contactPerson_id
  statusCon = rep("HEALTHY",nrow(casesRealelist)) # initialise all contacts persons as healthy ans update latter using case table
  pIdCase = as.character(casesRealelist[,5])  # casePerson_id
  statusCase = as.character(casesRealelist[,6])  # caseClass
  contactproximity =  as.character(casesRealelist[,4]) 
  resultingcase_id =  as.character(casesRealelist[,3])
  
  
  # Checking for contact persons that are also in case table and update their classification
  # Useing personCaseRegionDistSymp data
  tempCasPerId = as.character(personCaseRegionDistSymp$person_id) # we ased the data having all case persons including those that do not have contacts because contacts persons that become cases without contacts wil not appear in the contact table
  tempCasPerClass = as.character(personCaseRegionDistSymp$caseclassification)
  
  for(i in 1:length(pIdCon))
  {
    temp = pIdCon[i]
    for(j in 1:length(tempCasPerId))
    {
      if(temp == tempCasPerId[j])
      {
        statusCon[i] = tempCasPerClass[j] 
      }
      
    }
    
  }
  conData = data.frame(pIdCon,pIdCase,statusCase,statusCon, contactproximity, resultingcase_id) # contact data with classificatiion for each person incoled in teh contact
  conData[, ] = lapply(conData[, ], as.character)
  
  #defining contact cartegories
  #Category I , contact follow u is  needed here
  # ContactProximity.TOUCHED_FLUID = Touched fluid of source case
  #ContactProximity.FACE_TO_FACE_LONG = Face-to-face contact of at least 15 minutes
  #Used by other diseases
  # ContactProximity.CLOTHES_OR_OTHER = Manipulation of clothes or other objects of source case
  
  #ContactProximity.CLOSE_CONTACT = Was in close proximity (1 meter) with source case
  #ContactProximity.PHYSICAL_CONTACT = Direct physical contact with source case
  # ContactProximity.SAME_ROOM = Was in same room or house with source case
  
  # ContactProximity.MEDICAL_UNSAVE = Medical personal at close proximity (2 meter) without protective equipment
  # ContactProximity.AIRPLANE = Airplane, sitting up to two rows in front or behind the source case 
  
  # Category II, follow up not needed
  #ContactProximity.FACE_TO_FACE_SHORT = Face-to-face contact of less than 15 minutes
  # ContactProximity.MEDICAL_SAVE = Medical personal at save proximity (> 2 meter) or with protective equipment
  

conData$conProxCat = 0
conData$conProxCat[conData$contactproximity %in% c("FACE_TO_FACE_SHORT","MEDICAL_SAVE" )] = 2 
conData$conProxCat[!(conData$contactproximity %in% c("FACE_TO_FACE_SHORT","MEDICAL_SAVE" ))] = 1
 
 
  # node class
  personStatus = data.frame(nodes = c(pIdCase,pIdCon),status = c(statusCase,statusCon)) # concatenating all persons and their classification 
  personStatus = personStatus[!duplicated(personStatus[c("nodes")]),]
  #dat[!duplicated(dat[,c('id','id2')]),]
  personStatus$nodes = as.character(personStatus$nodes)
  personStatus$status = as.character(personStatus$status)
  rownames(personStatus) = personStatus$nodes
  
  # adding sex and morve vatriabnles to person status
  temp = personStatus
  colnames(temp)[1] = "person_id"
  temp = merge(temp, personTable, by = "person_id" )
  sex = as.character(temp$sex)
  sex[is.na(sex)==T]="UNK"
  #sex[is.na(sex)==T]="F"
  sex[sex == "FEMALE"]="F"
  sex[sex == "MALE"]="M"
  
  personStatus$sex = as.character(sex)
  
  # adding case persons that do not have contacts
  #tempMoreCases =  data.frame(casesRealNew$casePerson_id, casesRealNew$caseClass)
  # colnames(tempMoreCases) = c("nodes","status") # all cases
  # CasesNoCon = tempMoreCases[!(tempMoreCases$nodes %in% personStatus$nodes),] # cases without contacts
  # CasesNoCon = apply(CasesNoCon, 2, as.character)
  # nCasesNoCon = nrow(CasesNoCon)
  # nCasesNoConPro = round(nCasesNoCon/nrow(casesRealNew)*100)  # proportion of cases without contacts
  
  #personStatus = rbind(personStatus,CasesNoCon) # stacking  cases with contacts and non-contacts
  # personStatus = personStatus[!duplicated(personStatus[c('nodes')]),]   # deleting duplicated nodes to be used to design networks node attributes
  #rownames(personStatus) = personStatus$nodes
  
  # designing the edjeList
  # tempElist = data.frame(CasesNoCon$nodes,CasesNoCon$nodes ) # edje from same node
  # colnames(tempElist) = c("pIdCon","pIdCase")
  #eListPerson = rbind(conData[,1:2], tempElist )
  
  eListPerson = conData =  conData[,c(2,1,5,7)]
  #eListPerson = data.frame(eListPerson$pIdCase,eListPerson$pIdCon)
  #colnames(eListPerson) = c("pIdCase","pIdCon")
  
  ## Contact person stats ie converted to case
  conDataUniqPId = conData[!duplicated(conData[c("pIdCon")]),] # deleting duplicated person to know unique number of contacts
  nConPer = nrow(conDataUniqPId) # number if unique persons in the the list of contacts
  nConToCase = nrow(conDataUniqPId[conDataUniqPId$statusCon !="HEALTHY",]) # contacts persons that are not healthy contacts converted to case
  nConToCasePro = round(nConToCase/nConPer*100) # proportion of contact persons that converted to case
  # Person with max contacts
  tempMaxedje = data.frame(table(conData$pIdCase))
  temp = tempMaxedje[tempMaxedje$Freq == max(tempMaxedje$Freq),]
  maxEdge = temp$Freq
  maxEdgePerson= as.character(temp$Var1)
  
  image_info <- list(personStatus = personStatus, eListPerson = eListPerson)
  
  return(image_info)
  
}
image_info = network_transform() 
sormas_contact = make_epicontacts(linelist = image_info$personStatus,
                                  contacts = image_info$eListPerson,
                                  from = "pIdCase", to = "pIdCon",
                                  directed = TRUE)
# summary(sormas_contact) 
#contacts_ids <- get_id(sormas_contact, "contacts")
#head(contacts_ids, n = 10)
#subsetting
#subset(sormas_contact, node_attribute = list("status" = "PROBABLE", "sex" = "F"))  
        # edge_attribute = list("exposure" = "Emergency room")) add latter using contact proxomity

# subsetting based on one node only
#nodes <- c("123580")                  
#sub = subset(sormas_contact, cluster_id = nodes)

g = plot(sormas_contact, node_color = "status", node_shape = "sex", 
         shapes = c(M = "male", F = "female", UNK = "circle"), edge_label = "conProxCat",
         legend = TRUE,
         main = list(text = "Disease transmission chain", style = "font-family:'Open Sans', sans-serif, 'Source Sans Pro';color: #6591C4;font-weight: 600; font-size: 1.6em; text-align:center;"),
         submain = list(text = "The arrows indicate the direction of transmission", style= "font-family:'Open Sans', sans-serif, 'Source Sans Pro' text-align:center;"), 
		 footer = list(text = "1 = High risk contact, 2 = Low risk contact", style= "font-family:'Open Sans', sans-serif, 'Source Sans Pro'"), 
         background = "white")

library(visNetwork)
library(dplyr)
visNetwork::visSave(g, OUTFILE, selfcontained = FALSE)
