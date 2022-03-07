package model;

import java.util.List;

/**
 * A parsed fortify issue DTO into proper fields
 * 
 * @author Umut
 *
 */
public class FortifyIssueDto {
	
	private String id;
	private String severity;
	private String location;
	private List<String> locationTrace;
	private String reason;
	private String description;
	private String type;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<String> getLocationTrace() {
		return locationTrace;
	}
	public void setLocationTrace(List<String> locationTrace) {
		this.locationTrace = locationTrace;
	}
}
