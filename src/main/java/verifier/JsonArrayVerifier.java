package verifier;


import com.automation.test.verifier.AbstractDataVerifier;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Verify actual json contains expected given texts
 * @author hayden.hai
 *
 */
public class JsonArrayVerifier extends AbstractDataVerifier<JsonArray> {

    public boolean isMatched(JsonArray expected, JsonArray actual) {
        this.explanation = "See detail in comment";

		boolean isMatched = false;
        
        if (actual == null || actual.size() == 0) {
        	msg = "SapCpiLogVerifier: Actual json is empty";
        	if(expected==null|| expected.size() == 0) isMatched=true;
        } else {
        	boolean isCheckingIfErrExisted = false;
	        for (JsonElement actualEle : actual) {
	            JsonObject message = actualEle.getAsJsonObject();
	            if (message.has("Status")) {
	                boolean found = false;
	                for (JsonElement expectedEle : expected) {
	                    found = false;
	                    String expectedString = expectedEle.getAsString();
						if (expectedString.equals("Iflow has No error")) {
							isCheckingIfErrExisted = true;
						}
	                    JsonArray logs = message.get("Status").getAsJsonArray();
	                    for (JsonElement logEle : logs) {
	                        String log = logEle.getAsString();
	                        if (log.contains(expectedString)) {
	                            found = true;
	                            break;
	                        }
	                    }
	                    if (!found) {
	                    	if (isCheckingIfErrExisted) {
								this.explanation = msg = String.format("SapCpiMessageStatusVerifier: Iflow still has error '%s'", logs);
							} else {
								this.explanation = msg = String.format("SapCpiMessageStatusVerifier: Could NOT found text '%s'", expectedString);
							}

	                        break; //Fail where there is 1 expected not found
	                    }
	                }
	                if (found) {
	                    isMatched = true;
						if (isCheckingIfErrExisted) {
							this.explanation = msg = "SapCpiMessageStatusVerifier: Iflow does NOT have any error";
						} else {
							this.explanation = "SapCpiMessageStatusVerifier: Found expected text! See detail in comment";
						}
	                    break; //Stop search when there is a message matched all expected string
	                }
	            }
	            //else continue (skip when there is no 'logs' fields)
	        }
        }

        return isMatched;
    }

	@Override
	public boolean isOk(JsonArray actual) {
		return isMatched(getExpected(), actual);
	}

}
