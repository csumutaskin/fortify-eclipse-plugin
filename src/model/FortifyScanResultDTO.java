package model;

import java.util.List;

/** 
 * A parsed fortify scan result into proper fields.
 * @author Umut
 */
public class FortifyScanResultDto {
	
	private String scannedPath;
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
