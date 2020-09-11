import java.util.*;
import java.text.SimpleDateFormat;

public class HConfRunner extends Controller {

    /*public static class HUnit {
	String parameter = "";
        String component = "";
        String point = "";
        String v1 = "";
        String v2 = "";

        public HUnit(String _p, String _c, String _point, String _v1, String _v2) {
            this.parameter = _p;
            this.component = _c;
            this.point = _point;
            this.v1 = _v1;
            this.v2 = _v2;
        }

        public static List<HUnit> convertStr2List(List<String> str_list) {
            List<HUnit> h_list = new ArrayList<HUnit>();
            for (String one_str : str_list) {
                String[] fields = one_str.trim().split(",");
                if (fields.length != 5) {
                    System.out.println("ERROR: wrong length of hunit string");
                    System.exit(1);
                }
                HUnit unit = new HUnit(fields[0], fields[1], fields[2], fields[3], fields[4]);
                h_list.add(unit);
            }
            return h_list;
        }

        public static String

        public String toString() {
            return parameter + "," + component + "," + point + "," + v1 + "," + v2;
        }
    }*/

    // if is not an issue, return null;
    // if is an issue, return v1v2 TestResult
    public static TestResult testLogic(TestResult tr) {
        TestResult v1v2Tr = new TestResult(tr); // clone to create v1v2 test
        testCore("v1v2", v1v2Tr);
        if (v1v2Tr.result.equals("1")) {
            myPrint("v1v2 test succeeded.");
            return null;
        } else {
            myPrint("v1v2 test failed.");
            return v1v2Tr;
        }
    }

    public static void main(String[] args) {
        int rc = 0;
        long startTime, endTime, timeElapsed;
        startTime = endTime = timeElapsed = 0; 
        
        final int minArguments = 3;
        String proj = "";
        String u_test = "";
        String h_list = "";

        if (args.length < minArguments) {
            System.out.println("Error: args length is " + args.length);
            System.out.println("HConfRunner [testProject] [unitTest] [parameter,component,reconfPoint,v1,v2] ...");
            System.exit(1);
        }
      
        // assign arguments
        proj = args[0];
        u_test = args[1];
        StringBuilder sb = new StringBuilder();
        for (int j=2; j < args.length; j++) {
            sb.append(args[j]);
            if (j < (args.length - 1))
                sb.append(" ");
        }
        h_list = sb.toString();

        // display arguments
        //System.out.println("proj=" + proj + ", u_test=" + u_test);
        //System.out.println("h_list=" + h_list);
        //System.out.println("h_list size=" + h_list.split(" ").length);
       
        TestResult tr = new TestResult(proj, u_test, h_list);
	if (!TestResult.isValid(tr)) {
	    myPrint("ERROR: invalid TestResult");
	    System.exit(1);
	}

        // set run log
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String dateTime = formatter.format(date);
        String runLogPath = Controller.systemRootDir + "target/" + tr.getHashId() + 
	    "_run_" + dateTime + ".txt";
        Controller.setLogger(runLogPath);
 
	startTime = System.nanoTime();
        myPrint(tr.toString());
        TestResult issue = testLogic(tr);
        if (issue != null) {
            myPrint("---------------------------------------" +
                "full report---------------------------------------------");
            myPrint(issue.completeInfo());
        } else {
            myPrint("no issue");
        }
        endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        myPrint("Total execution time in seconds : " + timeElapsed / 1000000000);
	if (issue == null) { // no issue
	    myPrint("0");
	    rc = 0;
	} else {
	    myPrint("1");
	    rc = 1;
	}

        Controller.stopLogger();

	System.exit(rc);
    }
}
