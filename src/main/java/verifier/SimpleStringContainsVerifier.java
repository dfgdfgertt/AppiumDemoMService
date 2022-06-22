package verifier;


import com.automation.test.verifier.AbstractDataVerifier;

/**
 *
 */
public class SimpleStringContainsVerifier extends AbstractDataVerifier<String> {

    public boolean isMatched(String expected, String actual) {
        this.explanation = "See detailed actual result in comment";

        boolean isMatched = false;

        if (actual == null || actual.length() == 0) {
            msg = "SapCpiTraceLogVerifier: Actual json is empty";
        } else if (expected.length() != expected.length()) {
            msg = String.format("SapCpiMessageIDVerifier: Expected messageId %d != Actual num messageId %d", expected.length(), actual.length());
        } else {
            boolean found = false;
            if (actual.contains(expected)) {
                found = true;
            }
            else if ((actual.replaceAll("\\s", "").contains(expected.replaceAll("\\s", "")))) {
                found = true;
            }else{
                this.explanation = msg = String.format("SapCpiMessageIDVerifier: Could NOT found text '%s'", expected);
                isMatched = false;
            }
            if (found) {
                isMatched = true;
                this.explanation = "SapCpiTraceLogVerifier: Found expected text! See detail in comment";
            }
        }
        //else continue (skip when there is no 'logs' fields)
        return isMatched;
    }

    @Override
    public boolean isOk(String actual) {
        return isMatched(getExpected(), actual);
    }
}
