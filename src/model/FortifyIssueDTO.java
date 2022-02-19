package model;

/**
 * A parsed fortify issue DTO into proper fields
 * 
 * @author Umut
 *
 */
public class FortifyIssueDTO {
	
	private String id;
	private String location;
	private String severity;
	private String information;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String getInformation() {
		return information;
	}
	public void setInformation(String information) {
		this.information = information;
	}	
}
