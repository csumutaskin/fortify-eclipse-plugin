package model;

/**
 * Contains a particular issue's Class Name and Line Number information.
 * Currently <b>.java</b> files can be meaningfully parsed and mapped to this object.
 * </br>
 * Because the parser uses .java token to distinguish class name and the issue line number.
 * 
 * @author Umut
 *
 */
public class ParsedLocationInfo {
	
	private String className;
	private String lineNumber;
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}		
}