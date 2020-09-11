import java.util.*;
import java.text.SimpleDateFormat;

public class HConfRunner extends Controller {
    // if is not an issue, return null;
    // if is an issue, return v1v2 TestResult
    public static TestResult testLogic(TestResult tr) {
        TestResult v1v2Tr = new TestResult(tr); // clone to create v1v2 test
        testCore("v1v2", v1v2Tr);
        if (v1v2Tr.result.equals("1")) {
            System.out.println("v1v2 test succeeded.");
            return null;
        } else {
            System.out.println("v1v2 test failed.");
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
        System.out.println("proj=" + proj);
        System.out.println("u_test=" + u_test);
        System.out.println("h_list=" + h_list);
        System.out.println("h_list size=" + h_list.split(" ").length + "\n");
       
        TestResult tr = new TestResult(proj, u_test, h_list);
	if (!TestResult.isValid(tr)) {
	    System.out.println("ERROR: invalid TestResult");
	    System.exit(1);
	}
 
	startTime = System.nanoTime();
        System.out.println(tr.toString());
        TestResult issue = testLogic(tr);
        if (issue != null) {
            System.out.println("---------------------------------------" +
                "run report---------------------------------------------");
            System.out.println(issue.completeInfo());
            rc = 1;
        } else {
            System.out.println("no issue");
            rc = 0;
        }
        endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        System.out.println("Total execution time in seconds : " + timeElapsed / 1000000000);
	
	System.exit(rc);
    }
}
