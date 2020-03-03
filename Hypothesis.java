import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;
import java.text.SimpleDateFormat;

public class Hypothesis extends Controller {

    public static void hypothesisTestLogic(int repeats, String parameter, String component, String test, String v1, String v2, String reconfPoint) {    
        myPrint("hypothesisTestLogic");
        List<String> testSet = new ArrayList<String>();
        testSet.add(test);
	int v1v2Repeats = repeats; 
	int v1v2FailedCount = 0; 
	int v1v1v2v2Repeats = repeats; 
	int v1v1v2v2FailedCount = 0; 
        int i = 0;
        for (i=0; i<v1v2Repeats; i++) {
            List<TestResult> failedList = testCore(parameter, component, "v1v2", v1, v2, testSet, reconfPoint);
            if (failedList.size() > 0) {
                myPrint("v1v2 test failed !!!");
                v1v2FailedCount ++;
            }
        }
        
        for (i=0; i<v1v1v2v2Repeats; i++) {
            List<TestResult> v1v1failedList = testCore(parameter, component, "v1v1", v1, v2, testSet, reconfPoint);
            List<TestResult> v2v2failedList = testCore(parameter, component, "v2v2", v1, v2, testSet, reconfPoint);
            if (v1v1failedList.size() > 0 || v2v2failedList.size() > 0) {
                myPrint("v1v1 or v2v2 test failed !!!");
                v1v1v2v2FailedCount ++;
            }
        }
        myPrint("v1v2 failed with probability " + v1v2FailedCount + " out of " + v1v2Repeats);
        myPrint("v1v1v2v2 failed with probability " + v1v1v2v2FailedCount + " out of " + v1v1v2v2Repeats);
	if (v1v2FailedCount == 0) {
            myPrint("result: v1v2 failure didn't occur");
	} else {
            if (v1v2FailedCount * 0.8 > v1v1v2v2FailedCount) {
                myPrint("result: might be true error");
            } else {
                myPrint("result: false positive !!!");
            }
        }

	return;
    }

    public static void main(String[] args) {
	int repeats = 0;
        String parameter = "";
        String component = "";
        String test = "";
        String v1 = "";
        String v2 = "";
        String reconfPoint = "";
        long startTime, endTime, timeElapsed;
        startTime = endTime = timeElapsed = 0; 

        //if (!(args.length >= onetestArgIndex && args.length <= onetestArgIndex+1)) {
        if (args.length != 7) {
            myPrint("Error: args length is " + args.length);
            myPrint("Hypothesis repeats parameter component test v1 v2 reconfPoint");
            System.exit(1);
        }
       
	repeats = Integer.valueOf(args[0]);
	parameter = args[1];
	component = args[2];
        test=args[3];
        v1=args[4];
        v2=args[5];
        reconfPoint=args[6];

	if (!component.equals("NameNode") && !component.equals("DataNode") && !component.equals("JournalNode") && !component.equals("None")) {
	    myPrint("Error: wrong component " + component);
	    System.exit(1);
	}
  	if (repeats <= 0) {
	    myPrint("Error: wrong repeats " + repeats);
	    System.exit(1);
	}

        String dummyParameterType = "Boolean";
        loadStaticTestData(dummyParameterType);
        
	/* set run log */
        Date date = new Date();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
	String dateTime = formatter.format(date);
        String runLogPath = Controller.controllerRootDir + parameter + "%" + component + "%" + test + "%" + v1 +
            "%" + v2 + "%" + reconfPoint + "_run_" + dateTime + ".txt";
        try {
            runLogWriter = new BufferedWriter(new FileWriter(new File(runLogPath), true)); 
        } catch(Exception e) {
            e.printStackTrace();
        }
	
	myPrint("repeats: " + repeats); 
	myPrint("parameter: " + parameter); 
        myPrint("component: " + component); 
        myPrint("test: " + test);
        myPrint("v1: " + v1);
        myPrint("v2: " + v2);
        myPrint("reconfPoint: " + reconfPoint);

	startTime = System.nanoTime();
        hypothesisTestLogic(repeats, parameter, component, test, v1, v2, reconfPoint);
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
