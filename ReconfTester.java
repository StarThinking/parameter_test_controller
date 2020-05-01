import java.util.*;
import java.text.SimpleDateFormat;

public class ReconfTester extends Controller {

    public static void main(String[] args) {
        String parameter = "";
        String component = "";
        String v1 = "";
        String v2 = "";
	String testProject = "";
        String unitTest = "";
        String reconfPoint = "";
        final int parameterNum = 7;
        long startTime, endTime, timeElapsed;
        startTime = endTime = timeElapsed = 0; 

        if (args.length != parameterNum) {
            myPrint("Error: args length is " + args.length);
            myPrint("ReconfTryer [parameter] [component] [v1] [v2] [testProject] [unitTest] [reconfPoint]");
            System.exit(1);
        }
       
	parameter = args[0];
	component = args[1];
        v1 = args[2];
        v2 = args[3];
	testProject = args[4];
        unitTest = args[5];
        reconfPoint = args[6];
        
        TestResult tr = new TestResult(parameter, component, v1, v2, testProject, unitTest, reconfPoint);
	if (!TestResult.isValid(tr)) {
	    myPrint("Error: wrong component " + component);
	    System.exit(1);
	}

        /* set run log */
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        String dateTime = formatter.format(date);
        String runLogPath = Controller.systemRootDir + tr.shortName() + "_run_" + dateTime + ".txt";
        Controller.setLogger(runLogPath);
 
	startTime = System.nanoTime();
        myPrint(tr.toString());
        TestResult issue = testLogic(tr);
        if (issue != null) {
            myPrint("---------------------------------------full report---------------------------------------------");
            myPrint(issue.completeInfo());
        } else {
            myPrint("no issue");
        }
        endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        myPrint("Total execution time in seconds : " + timeElapsed / 1000000000);
	if (issue == null) // no issue
	    myPrint("0");
	else 
	    myPrint("-1");
        
        Controller.stopLogger();
    }
}
