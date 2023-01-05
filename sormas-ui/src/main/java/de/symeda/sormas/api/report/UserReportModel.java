package de.symeda.sormas.api.report;

import de.symeda.sormas.api.ReferenceDto;

public class UserReportModel extends ReferenceDto {
	
	
	private static final long serialVersionUID = -8558187171374254398L;

	public static final String ACTIVE = "active";
	
	public static final  String REP_NO = "no";
	public static final  String REP_REGION = "region";
	public static final  String REP_PROVINCE = "province";
	public static final  String REP_DISTRICT = "district";
	public static final  String REP_POSITION = "position";
	public static final  String REP_CLUSTER = "cluster";
	public static final  String REP_USERNAME = "username";
	public static final  String REP_MSG = "message";
	
	
	private Long no;
	private String region;
	private String province;
	private String district;
	private String position;
	private String cluster;
	private String username;
	private String message;
	
	public UserReportModel() {
	}
	
	public UserReportModel(Long no, String region, String province, String district, String position, String cluster,
			String username, String message) {
		super();
		this.no = no;
		this.region = region;
		this.province = province;
		this.district = district;
		this.position = position;
		this.cluster = cluster;
		this.username = username;
		this.message = message;
	}
	
	
	
	public Long getNo() {
		return no;
	}
	public void setNo(Long no) {
		this.no = no;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getCluster() {
		return cluster;
	}
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
