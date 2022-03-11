package fortifyscanner.model;

import java.util.List;

/** 
 * A DTO containing Bulk Information about:
 * The project root path (scan path = root path of the project in workspace).
 * issues: List of DTOs that contain issue information (See: {@link FortifyIssueDto})
 * @author Umut
 */
public class FortifyScanResultDto {
	
	//Root path of the project
	private String scannedPath;
	
	//Issues detected
	private List<FortifyIssueDto> issues;
	
	public String getScannedPath() {
		return scannedPath;
	}
	public void setScannedPath(String scannedPath) {
		this.scannedPath = scannedPath;
	}
	public List<FortifyIssueDto> getIssues() {
		return issues;
	}
	public void setIssues(List<FortifyIssueDto> issues) {
		this.issues = issues;
	}
}
