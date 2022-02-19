package model;

import java.util.List;

/** 
 * A parsed fortify scan result into proper fields.
 * @author Umut
 */
public class FortifyScanResultDTO {
	
	private String scannedPath;
	private List<FortifyIssueDTO> issues;
	
	public String getScannedPath() {
		return scannedPath;
	}
	public void setScannedPath(String scannedPath) {
		this.scannedPath = scannedPath;
	}
	public List<FortifyIssueDTO> getIssues() {
		return issues;
	}
	public void setIssues(List<FortifyIssueDTO> issues) {
		this.issues = issues;
	}
}
