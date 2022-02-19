package components;

import model.FortifyScanResultDTO;

/** 
 * A utility that facilitates fortify scans and mapping results into proper objects
 * 
 * @author Umut
 *
 */
public class FortfiyScanUtility {
	
	/**
	 * Creates an on the fly scan (no file generation, logs of result are kept in a parseable string).
	 * 
	 * @return scan result hold in a string
	 */
	public static String scanOnTheFly() {
		return null;
	}
	
	/**
	 * Maps on the fly scan response to a FortifyScanResultDTO
	 * @param result log that holds all on the fly scan result
	 * @return mapped result in a DTO object
	 */
	public static FortifyScanResultDTO mapOnTheFlyScanToDTO(String result) {
		return null;
	}
}
