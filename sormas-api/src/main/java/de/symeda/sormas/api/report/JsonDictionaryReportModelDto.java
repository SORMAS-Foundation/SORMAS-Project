package de.symeda.sormas.api.report;


import de.symeda.sormas.api.EntityDto;

public class JsonDictionaryReportModelDto extends EntityDto {

	public static final String I18N_PREFIX = "User";
	private static final long serialVersionUID = -8558187171374254398L;

	public static final String FORM_ID = "formid";
	public static final String CAPTION = "caption";
	public static final String ID = "id";
	public static final String FORM_TYPE = "formtype";
	public static final String MODALITY = "modality";
	public static final String DATATYPE = "datatype";

	private String formid;
	private String caption;
	private String id;
	private String formtype;
	private String modality;
	private String datatype;

	
	public JsonDictionaryReportModelDto(String formid, String caption, String id, String formtype, String modality, String datatype) {
		super();
		this.formid = formid;
		this.caption = caption;
		this.id = id;
		this.formtype = formtype;
		this.modality = modality;
		this.datatype = datatype;
	}

	public String getFormid() {
		return formid;
	}

	public void setFormid(String formid) {
		this.formid = formid;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFormtype() {
		return formtype;
	}

	public void setFormtype(String formtype) {
		this.formtype = formtype;
	}

	public String getModality() {
		return modality;
	}

	public void setModality(String modality) {
		this.modality = modality;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	
	
	
	

}
