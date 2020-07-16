import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Controller {

    private static String workingRootDir = "/root/hadoop-3.1.2-src/hadoop-hdfs-project/";
    protected static String systemRootDir = "/root/parameter_test_controller/";
    protected static BufferedWriter runLogWriter = null;
    protected static int RECHECK_TIMES = 5;

    /* shared files */
    private static String testResultDirName = systemRootDir + "shared/test_results";
    private static String vvModeFileName = systemRootDir + "shared/reconf_vvmode";
    private static String parameterFileName = systemRootDir + "shared/reconf_parameter";
    private static String componentFileName = systemRootDir + "shared/reconf_component";
    private static String v1FileName = systemRootDir + "shared/reconf_v1";
    private static String v2FileName = systemRootDir + "shared/reconf_v2";
    private static String reconfPointFileName = systemRootDir + "shared/reconf_point";
    
    private static void updateTestResult(List<TestResult> testResultList) {
        String SEPERATOR = "@@@";
        try {
            File testResultDir = new File(testResultDirName);
            List<String> updatedTests = new ArrayList<String>();
            for (File f : testResultDir.listFiles()) {
                if (f.isFile()) {
                    //myPrint("updating test result for " + f.getName());
                    BufferedReader reader = new BufferedReader(new FileReader(f));
                    String buffer = "";
                    StringBuilder sb = new StringBuilder("");  
                    buffer = reader.readLine();
	            while (buffer != null) {
                        sb.append(buffer + System.lineSeparator());
	                buffer = reader.readLine();
	            }
                    reader.close();
                    String[] contents = sb.toString().trim().split(SEPERATOR);
                    if (contents.length != TestResult.NumOfFieldsFromFile) {
                        myPrint("Error: the content for " + f.getName() + " is wrong, length = " + contents.length);
                        myPrint("Content: ");
                        myPrint(sb.toString());
                        continue;
                    } else {
                        String testName = contents[0];
                        TestResult testResult = TestResult.getTestResultByName(testResultList, testName);
                        if (testResult == null) {
                            myPrint("Error: cannot find testResult by test name " + testName);
                            continue;
                        } else {
                            testResult.result = contents[1];
                            testResult.failureMessage = contents[2];
                            testResult.stackTrace = contents[3];
                            updatedTests.add(testName);
                            //myPrint(testResult.completeInfo());
                        }
                    }
                }
            }
            List<String> allTestNames = TestResult.getTestNames(testResultList);
            for (String t : allTestNames) {
                boolean found = false;
                for (String updated : updatedTests) {
                    if (t.equals(updated)) {
                        found = true;
                    }
                    if (found)
                        break;
                }
                if (!found) {
                    myPrint("Warn: test " + t + " has not been updated !");
                    //System.exit(-1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void runMvnCmd(TestResult tr) {
        try {
            int exitCode = -1;
	    String systemLogSavingDir = "/root/reconf_test_gen/target";
            ProcessBuilder builder = new ProcessBuilder();
   	    builder.command("/root/reconf_test_gen/run_mvn_test.sh", tr.testProject, tr.unitTest, systemLogSavingDir);
	    Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	    String line = "";
	    while ((line = reader.readLine()) != null) {
		;
		//myPrint(line);
	    }
	    reader.close();
	    if(!process.waitFor(1200, TimeUnit.SECONDS)) { // timeout - kill the process.
		myPrint("Warn: wait process timeout!");
    		process.destroy(); // consider using destroyForcibly instead
	    }
            exitCode = process.exitValue();

	    // get exception set
	    myPrint("Exception set:");
	    ProcessBuilder builder1 = new ProcessBuilder();
	    builder1.command("/root/reconf_test_gen/extract_exception.sh");
	    Process process1 = builder1.start();
	    BufferedReader reader1 = new BufferedReader(new InputStreamReader(process1.getInputStream()));
	    String line1 = "";
	    while ((line1 = reader1.readLine()) != null) {
   	        myPrint(line1);
	    }
	    reader1.close();
           
            List<TestResult> testResultList = new ArrayList<TestResult>();
            testResultList.add(tr);
            updateTestResult(testResultList);

	    // override result with cmd exit code
	    if ((exitCode == 0 && tr.result.equals("-1")) || (exitCode != 0 && tr.result.equals("1"))) {
	        myPrint("Warn: conflict exitCode = " + exitCode + " but tr.result = " + tr.result);
	    }
	   
	    if (exitCode == 0)
		tr.result = "1";
	    else
		tr.result = "-1";

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // update directly at TestResult tr
    public static void testCore(String vvMode, TestResult tr) {
        if (tr.unitTest == null || tr.unitTest.equals("")) {
            System.exit(1);
        }
        
        myPrint("\nTest vvMode=" + vvMode); 
        cleanUpSharedFiles();
        setupTestTuple(vvMode, tr);
        runMvnCmd(tr);
        myPrint("tr.result is " + tr.result);
        return;
    }
     
    // if succeed, return null; otherwise, return v1v2 TestResult
    public static TestResult testLogic(TestResult tr) {
        // do v1v2 test
        TestResult v1v2Tr = new TestResult(tr);
        testCore("v1v2", v1v2Tr);
        if (v1v2Tr.result.equals("1")) {
            myPrint("succeed.");
            return null;
        } else {
            myPrint("fail. do " + Controller.RECHECK_TIMES + " v1v1 v2v2 tests to filter false alarm");
        }
      
        myPrint("failed v1v2 test: " + tr.unitTest + " v1 " + tr.v1 + " v2 " + tr.v2);
        int i = 0;
        for (; i<Controller.RECHECK_TIMES; i++) {
            TestResult v1v1Tr = new TestResult(tr);
            testCore("v1v1", v1v1Tr);
            if (v1v1Tr.result.equals("-1")) {
                myPrint("v1v1 failed with v1 " + v1v1Tr.v1 + ", no issue.");
                return null;
            }
            TestResult v2v2Tr = new TestResult(tr);
            testCore("v2v2", v2v2Tr);
            if (v2v2Tr.result.equals("-1")) {
                myPrint("v1v1 failed with v2 " + v2v2Tr.v2 + ", no issue.");
                return null;
            }
        }
        if (i == Controller.RECHECK_TIMES) {
           myPrint("v1v1 and v2v2 succeed for " + Controller.RECHECK_TIMES + " times, it is issue");
        }
	return v1v2Tr;
    }

    public static void setLogger(String logPath) {
        try {       
            runLogWriter = new BufferedWriter(new FileWriter(new File(logPath), true));
        } catch(Exception e) { 
            e.printStackTrace();
        } 
    }
    
    public static void stopLogger() {
        try {
            runLogWriter.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void myPrint(String str) {
        System.out.println(str);
        if (runLogWriter == null) {
            System.out.println("runLogWriter is still null");
            return;
        }
        try {
            runLogWriter.write(str + System.lineSeparator());
            runLogWriter.flush();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
  
    private static void setupTestTuple(String vvMode, TestResult tr) {
	if (!vvMode.equals("v1v1") && !vvMode.equals("v2v2") && !vvMode.equals("v1v2") && !vvMode.equals("none")) {
	    myPrint("Error, wrong mode " + vvMode);
	    System.exit(1);
	}
        try {
            BufferedWriter writer1 = new BufferedWriter(new FileWriter(new File(parameterFileName)));
            writer1.write(tr.parameter);
            writer1.close();
            
	    BufferedWriter writer2 = new BufferedWriter(new FileWriter(new File(componentFileName)));
            writer2.write(tr.component);
            writer2.close();
            
            BufferedWriter writer3 = new BufferedWriter(new FileWriter(new File(vvModeFileName)));
            writer3.write(vvMode);
            writer3.close();
            
            BufferedWriter writer4 = new BufferedWriter(new FileWriter(new File(v1FileName)));
            writer4.write(tr.v1);
            writer4.close();
            
            BufferedWriter writer5 = new BufferedWriter(new FileWriter(new File(v2FileName)));
            writer5.write(tr.v2);
            writer5.close();
            
            BufferedWriter writer6 = new BufferedWriter(new FileWriter(new File(reconfPointFileName)));
            writer6.write(tr.reconfPoint);
            writer6.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void cleanUpSharedFiles() {
        try {
            File testResultDir = new File(testResultDirName);
            for (File testResult : testResultDir.listFiles()) {
                if (testResult.isFile()) {
                    testResult.delete();
                }
            }
            TestResult empty = new TestResult("", "", "", "", "", "", "");
            setupTestTuple("none", empty);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
