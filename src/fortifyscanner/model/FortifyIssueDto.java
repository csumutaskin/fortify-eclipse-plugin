package fortifyscanner.model;

import java.util.List;

/**
 * A Fortify Issue DTO each containing necessary information of a Fortify Issue.
 * Attributes indicate:
 * </br>
 * <ul>
 * 	<li>id: Fortify SCA detected Issue ID.</li>
 * 	<li>severity: Criticality of Fortify Issue. Low, Medium, High, Critical.</li>
 * 	<li>reason: Category of Fortify Vulncat Taxonomy (Main Reason).</li>
 * 	<li>location: Path Trace of the issue.</li>
 * 	<li>locationTrace: Path Trace of the issue line by line (with nested occurrences path of the main issue).</li>
 * 	<li>description: Sub Category Actually, can sometimes contain null info.</li>
 * 	<li>type: analyzer e.g.: structural, dataflow....</li>
 * </ul>
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
		//Needed this not to override a comparator over a standard string comparator. null values are giving exception otherwise.
		if(description == null) {
			return "";
		}
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
