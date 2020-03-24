import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;
import java.text.SimpleDateFormat;

public class Hypothesis extends Controller {

    public static void hypothesisTestLogic(int repeats, TestResult tr) {    
        int v1v2Repeats = repeats; 
	int v1v2FailedCount = 0; 
	int v1v1v2v2Repeats = repeats; 
	int v1v1v2v2FailedCount = 0; 
        int i = 0;
        
        myPrint(tr.toString());
        
        for (i=0; i<v1v2Repeats; i++) {
            TestResult v1v2Tr = new TestResult(tr);
            testCore("v1v2", v1v2Tr);
            if (v1v2Tr.result.equals("-1")) {
                myPrint("v1v2 test failed !!!");
                v1v2FailedCount ++;
            }
        }
        
        for (i=0; i<v1v1v2v2Repeats; i++) {
            TestResult v1v1Tr = new TestResult(tr);
            testCore("v1v1", v1v1Tr);
            TestResult v2v2Tr = new TestResult(tr);
            testCore("v2v2", v2v2Tr);
            if (v1v1Tr.result.equals("-1") || v2v2Tr.result.equals("-1")) {
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
        String v1 = "";
        String v2 = "";
        String reconfPoint = "";
        String unitTest = "";
        long startTime, endTime, timeElapsed;
        startTime = endTime = timeElapsed = 0; 

        if (args.length != 7) {
            myPrint("Error: args length is " + args.length);
            myPrint("Hypothesis repeats parameter component v1 v2 reconfPoint unitTest");
            System.exit(1);
        }
       
	repeats = Integer.valueOf(args[0]);
  	if (repeats <= 0) {
	    myPrint("Error: wrong repeats " + repeats);
	    System.exit(1);
	}
	parameter = args[1];
	component = args[2];
        v1=args[3];
        v2=args[4];
        reconfPoint=args[5];
        unitTest=args[6];

        TestResult tr = new TestResult(parameter, component, v1, v2, reconfPoint, unitTest);
        if (!TestResult.isValid(tr)) {
            myPrint("Error: wrong component " + component);
            System.exit(1);
        }

        /* set run log */
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        String dateTime = formatter.format(date);
        String runLogPath = Controller.systemRootDir + tr.shortName() + "_hypothesis_" + dateTime + ".txt";
        Controller.setLogger(runLogPath);
	
	startTime = System.nanoTime();
        hypothesisTestLogic(repeats, tr);
        endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        myPrint("Total execution time in seconds : " + timeElapsed / 1000000000);
         
        Controller.stopLogger();        
    }
}
