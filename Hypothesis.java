import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;
import java.text.SimpleDateFormat;

public class Hypothesis extends Controller {

    public static void hypothesisTestLogic(String parameter, String component, String test, String v1, String v2, String reconfPoint) {    
        myPrint("testLogic1");
        List<String> testSet = new ArrayList<String>();
        testSet.add(test);
        List<TestResult> failedListv1v2 = testCore(parameter, component, "v1v2", v1, v2, testSet, reconfPoint);
        for (TestResult t : failedListv1v2)
            myPrint(t.testName);
	return;
    }

    public static void main(String[] args) {
        String parameter = "";
        String component = "";
        String test = "";
        String v1 = "";
        String v2 = "";
        String reconfPoint = "";
        long startTime, endTime, timeElapsed;
        startTime = endTime = timeElapsed = 0; 

        //if (!(args.length >= onetestArgIndex && args.length <= onetestArgIndex+1)) {
        if (args.length != 6) {
            myPrint("Error: args length is " + args.length);
            myPrint("Hypothesis parameter component test v1 v2 reconfPoint");
            System.exit(1);
        }
       
	parameter = args[0];
	component = args[1];
        test=args[2];
        v1=args[3];
        v2=args[4];
        reconfPoint=args[5];

	if (!component.equals("NameNode") && !component.equals("DataNode") && !component.equals("JournalNode") && !component.equals("None")) {
	    myPrint("Error: wrong component " + component);
	    System.exit(1);
	}
  	
        String dummyParameterType = "Boolean";
        loadStaticTestData(dummyParameterType);
        
	/* set run log */
        Date date = new Date();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
	String dateTime = formatter.format(date);
        String runLogPath = Controller.controllerRootDir + parameter + "_run_" + component + "_" + dateTime + ".txt";
        try {
            runLogWriter = new BufferedWriter(new FileWriter(new File(runLogPath), true)); 
        } catch(Exception e) {
            e.printStackTrace();
        }
	
	myPrint("parameter: " + parameter); 
        myPrint("component: " + component); 
        myPrint("test: " + test);
        myPrint("v1: " + v1);
        myPrint("v2: " + v2);
        myPrint("reconfPoint: " + reconfPoint);

	startTime = System.nanoTime();
        hypothesisTestLogic(parameter, component, test, v1, v2, reconfPoint);
        endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        myPrint("Total execution time in seconds : " + timeElapsed / 1000000000);
        
        try {
            runLogWriter.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
