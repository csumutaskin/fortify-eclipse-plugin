package util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

import model.ProjectDto;

/**
 * Contains all operations that are related with current workspace (Where the developer is currently working using eclipse IDE).
 * e.g. Get all project names in this workspace.
 * e.g. Get the operating system full path of this workspace.
 * 
 * @author Umut
 *
 */
public class WorkspaceUtils {

	/**
	 * Retrieves all basic information about all projects that the developer is currently running on his/her eclipse IDE.
	 * 
	 * @return DTO list for project name and root path information that exist in a particular workspace
	 */
	public static List<ProjectDto> getAllProjectSummaryInfoInCurrentWorkspace() {
		
		List<ProjectDto> allWorkspaceProjects = new ArrayList<ProjectDto>();
	    IWorkspace workspace = ResourcesPlugin.getWorkspace();
	    IWorkspaceRoot root = workspace.getRoot();
	    IProject[] projects = root.getProjects();
	    String workspacePath = getCurrentWorkspaceOSFullPath();
	    for (IProject project : projects) {	    	
	    	allWorkspaceProjects.add(new ProjectDto(project.getName(), workspacePath + "/" + project.getName()));
	    }
	    return allWorkspaceProjects;
	}
	
	/**
	 * Returns current workspace operating system full path as a string.
	 * @return the full path of workspace
	 */
	public static String getCurrentWorkspaceOSFullPath() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
	}
	
}
