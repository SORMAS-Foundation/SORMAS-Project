package de.symeda.sormas.rest;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.opencsv.CSVWriter;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.CampaignDto;
import de.symeda.sormas.api.campaign.data.CampaignAggregateDataDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.data.CampaignFormDataEntry;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaReferenceDto;


@Path("/apmisrestserver")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
//@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	"USER",
	"REST_USER",
	"ADMIN" })
public class ApmisCampaignResource extends EntityDtoResource {
	
	@GET
	@Path("/campaigns") //return a list of all active campaigns 
	public List<CampaignDto> getAllCampaigns() {
		return FacadeProvider.getCampaignFacade().getAllActive();
	}
	 
	@GET
	@Path("/campaigns/{uuid}") //return a campaign by its UUID
	public CampaignDto getByUuid(@PathParam("uuid") String uuid) {
		return FacadeProvider.getCampaignFacade().getByUuid(uuid); 
	}
	
	@GET
	@Path("/campaigns/{uuid}/forms") //return a campaign's forms //next: use the uuid of the form to get out all the data associated with that form and the campaign
	public Set<CampaignFormMetaReferenceDto> getCampaignForms(@PathParam("uuid") String uuid) {
		return FacadeProvider.getCampaignFacade().getByUuid(uuid).getCampaignFormMetas();
	}
	
	@GET
	@Path("/campaigns/{campaigns_uuid}/forms/{forms_uuid}") //should return the data for the specific form //I should write a custom query here that checks the campaign by meta and campaign
	public List<CampaignFormDataEntry> getCampaignFormData(@PathParam("campaigns_uuid") String campaign_uuid, @PathParam("forms_uuid") String form_uuid) {
		
		List<CampaignFormDataDto> lstdto = FacadeProvider.getCampaignFormDataFacade().getCampaignFormData(campaign_uuid, form_uuid);	
		
		List<CampaignFormDataEntry> lst =  new ArrayList<>();
		for(CampaignFormDataDto fs : lstdto) {
			lst.addAll(fs.getFormValues());
			}
		return lst;
	}
/*	
	
	@GET
	@Path("/campaignsall") //should return the data for the specific form //I should write a custom query here that checks the campaign by meta and campaign
	public void getallRecord() {
		List<CampaignFormDataDto> lstdto = FacadeProvider.getCampaignFormDataFacade().getCampaignFormData("60E13D4A-CA94-4AC1-8A28-C38011457E7C", "D944B8-7F0136-1F2D66-4901B050");
		List<CampaignFormDataEntry> lst =  new ArrayList<>();

		for(CampaignFormDataDto fs : lstdto) {
			lst.addAll(fs.getFormValues());
			}
		
		StreamingOutput entity = new StreamingOutput() {
		    @Override
		    public void write(OutputStream out) {
		        Writer writer = new BufferedWriter(
		                new OutputStreamWriter(out, StandardCharsets.UTF_8));
		        csvWriter(myArrList, writer);
		        out.flush();
		    }
		};
		
		  @Override
		    public void write(OutputStream out) {
		        Writer writer = new BufferedWriter(
		                new OutputStreamWriter(out, StandardCharsets.UTF_8));
		        csvWriter(FacadeProvider.getCampaignFormDataFacade().getCampaignFormData("60E13D4A-CA94-4AC1-8A28-C38011457E7C", "D944B8-7F0136-1F2D66-4901B050"), writer);
		        out.flush();
		    }
		}
		
	
/*
	
	interface StreamingOutput {
	    void write(OutputStream out);
	}
	
	
	
	return Response.ok(entity)
	        .header(HttpHeaders.CONTENT_DISPOSITION,
	                "attachment; filename=\"RadioObjectData.csv\"")
	        .build();
	

	
	public static void csvWriter( List<HashMap<String, String>> listOfMap, Writer writer) throws IOException
	{
	  CsvSchema schema = null;
	  CsvSchema.Builder schemaBuilder = CsvSchema.builder();
	  if (listOfMap != null && !listOfMap.isEmpty())
	  {
	     for (String col: listOfMap.get( 0).keySet())
	     {
	        schemaBuilder.addColumn( col);
	     }
	     schema = schemaBuilder.build().withLineSeparator( "\r").withHeader();
	  }
	  CsvMapper mapper = new CsvMapper();
	  mapper.writer( schema).writeValues( writer).writeAll( listOfMap);
	  writer.flush();
	}
	*/
	
	
	@GET
    @Path("/aggregate/{campaigns_uuid}")
    @Produces("text/csv")
	//@Path("/campaigns/{campaigns_uuid}/forms/{forms_uuid}") //should return the data for the specific form //I should write a custom query here that checks the campaign by meta and campaign
	//public List<CampaignFormDataEntry> getCampaignFormData(@PathParam("campaigns_uuid") String campaign_uuid, @PathParam("forms_uuid") String form_uuid) {
	
    public Response getDataAsCsv(@PathParam("campaigns_uuid") String campaign_uuid) {
		System.err.println("connecting to db");
		List<CampaignAggregateDataDto> resultsCSV = FacadeProvider.getCampaignFormDataFacade().getCampaignFormDataAggregatetoCSV(campaign_uuid);	
	System.err.println("runing result to CSV");
        String csv = toCsv(resultsCSV);

        return Response.ok(csv).header("Content-Disposition", "attachment; filename=data.csv").build();
    }
	
	
	 private String toCsv(List<CampaignAggregateDataDto> data) {

	        StringWriter stringWriter = new StringWriter();

	        try (CSVWriter writer = new CSVWriter(stringWriter)) {

	            // Define the header row
	        	//String formUuid, String formId, String formField, String formCaption, String area,String region, String district, Long sumValue
	        	
	            String[] header = {"FormUUID", "FormID", "FieldName", "Province", "Region", "District", "Aggregate"};
	            writer.writeNext(header);

	            // Add the data rows
	            for (CampaignAggregateDataDto formData : data) {
	                String[] row = {
	                		formData.getFormUuid(), formData.getFormId(), formData.getFormCaption(), formData.getArea(), formData.getRegion(), formData.getDistrict(), formData.getSumValue().toString()
	                		};
	                writer.writeNext(row);
	            }

	            // Flush the writer and return the CSV data
	            writer.flush();
	            return stringWriter.toString();

	        } catch (IOException e) {
	            throw new RuntimeException("Error converting data to CSV", e);
	        }
	    }
	





	
	
	
	
}
