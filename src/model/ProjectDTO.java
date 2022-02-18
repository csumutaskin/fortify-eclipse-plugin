package model;

/**
 * DTO to hold project information
 * @author Umut
 *
 */
public class ProjectDTO {
	
	private String projectName;
	private String projectPath;
	
	public ProjectDTO() {		
	}
	
	public ProjectDTO(String projectName, String projectPath) {
		super();
		this.projectName = projectName;
		this.projectPath = projectPath;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectPath() {
		return projectPath;
	}
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}
	
	
}
