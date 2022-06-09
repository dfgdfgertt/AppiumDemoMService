package verifier;

import com.automation.test.verifier.AbstractVerifier;

import java.util.List;

/**
 * Verify actual taillog contains expected given texts
 *
 */
public class MultiStringContainsVerifier extends AbstractVerifier<String,List<String>> {
	public boolean isMatched(List<String> expected, String actual) {
		this.explanation = "See detail in comment";

		boolean isMatched = false;

		if (actual == null || actual.length() == 0) {
			msg = "SapCpiTraceLogVerifier: Actual json is empty";
		} 
		else {
				boolean found = false;
				for (String expectedEle : expected) {
					String expectedText = expectedEle;
	                found = false;	                
	                if (actual.contains(expectedText)) {
                        found = true;
                    }
	                else if ((actual.replaceAll("\\s", "").contains(expectedText.replaceAll("\\s", "")))) {
	                	found = true;
	                }
	                else {
	                	
	                	found= false;
	                	this.explanation = msg = String.format("SapCpiMessageStatusVerifier: Could NOT found text '%s'", expectedText);
	                	break;
	                }	                
	            }
	            if (found) {
	                isMatched = true;
	                this.explanation = "SapCpiMessageStatusVerifier: Found expected text! See detail in comment";	              
	            }
			}		
		return isMatched;
	}

	@Override
	public boolean isOk(String actual) {
		return isMatched(getExpected(), actual);
	}
}

