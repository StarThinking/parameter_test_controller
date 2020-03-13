import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;
import java.text.SimpleDateFormat;

public class Controller {

    public static String workingDir = "/root/hadoop-3.1.2-src/hadoop-hdfs-project";
    public static String controllerRootDir = "/root/parameter_test_controller/";
    public static StringBuilder runLogBuffer = new StringBuilder();
    public static BufferedWriter runLogWriter = null;
    public static int RECHECK_RUNTIMES=20;

    /* shared files */
    public static String testResultDirName = controllerRootDir + "shared/test_results";
    public static String parameterFileName = controllerRootDir + "shared/parameter";
    public static String reconfigModeFileName = controllerRootDir + "shared/reconfig_mode";
    public static String reconfigComponentFileName = controllerRootDir + "shared/reconfig_component";
    public static String v1FileName = controllerRootDir + "shared/v1";
    public static String v2FileName = controllerRootDir + "shared/v2";
    public static String reconfPointFileName = controllerRootDir + "shared/reconf_point";
   
    /* static test information */
    private static String beforeClassFileName = controllerRootDir + "test_for_component/hdfs/before_class.txt";
    private static List<String> beforeClassList = new ArrayList<String>();
    
    /* failed vanilla unit tests */
    //private static String vanillaFailedTestFileName = controllerRootDir + "test_for_component/hdfs/vanilla_failed_test.txt";
    private static List<String> vanillaFailedTestList = new ArrayList<String>();
    
    static class TestResult {
        public String testName = "";
        public String result = "";
        public String failureMessage = "";
        public String stackTrace = "";
        public String parameter = "";
        public String component = "";
        public String v1 = "";
        public String v2 = "";
        public String reconfPoint = "";
        public static int NumOfFieldsFromFile = 4;

        public TestResult(String testName, String parameter, String component, String v1, String v2, String reconfPoint) {
            this.testName = testName;
            this.parameter = parameter;
            this.component = component;
            this.reconfPoint = reconfPoint;
            this.v1 = v1;
            this.v2 = v2;
            // 1: succeed -1:failed
            this.result = "-1"; // some tests may not complete, so treat no result as failed
        }

        @Override
        public String toString() {
            return "parameter: " + parameter + "\n" +
                "component: " + component + "\n" +
                "reconfPoint: " + reconfPoint + "\n" +
                "v1: " + v1 + "\n" +
                "v2: " + v2 + "\n" +
                "testName: " + testName + "\n" +
                "result: " + result;
        }
        
        public String completeInfo() {
            return this.toString() + "\n" +
                "failureMessage: " + failureMessage + "\n" +
                "stackTrace: " + stackTrace + "\n";
        }

        public static TestResult getTestResultByName(List<TestResult> list, String name) {
            for (TestResult t : list) {
                if (t.testName.equals(name))
                    return t;
            }
            // error
            myPrint("Error: name " + name + " cannot be found in the list");
            return null;
        }

        public static List<String> getTestNames(List<TestResult> list) {
            List<String> names = new ArrayList<String>();
            for (TestResult t : list)
                names.add(t.testName);
            return names;
        } 
    }

    private static void updateTestResult(List<TestResult> testResultList) {
        String SEPERATOR = "@@@";
        try {
            File testResultDir = new File(testResultDirName);
            List<String> updatedTests = new ArrayList<String>();
            for (File f : testResultDir.listFiles()) {
                if (f.isFile()) {
                    myPrint("updating test result for " + f.getName());
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

    private static void runCombinedMvnCmd(List<String> testNameList, int numPerPart) throws Exception{
        // split into parts
        int numOfTestsPerPart = numPerPart;
        int indexOfParts = 0;
        int start = 0;
        int end = 0;
        List<List<String>> listOfParts = new ArrayList<List<String>>(); 
        do {
            start = indexOfParts * numOfTestsPerPart;
            end = (indexOfParts + 1) * numOfTestsPerPart;
            if (end >= testNameList.size())
                end = testNameList.size();
            listOfParts.add(testNameList.subList(start, end));
            indexOfParts ++;
        } while(end < testNameList.size());
        
        for (List<String> partOfTestNameList: listOfParts) { 
            // merge tests into a single command 
            String combinedMethods = "";
            int numOfTests = 0;
            for (String test : partOfTestNameList) {
                numOfTests ++;
                if (numOfTests == partOfTestNameList.size())
                    combinedMethods += test;
                else
                    combinedMethods += test + ",";
            }

            myPrint("Number of combined methods is " + numOfTests);
            String cmd = "mvn test -Dtest=" + combinedMethods;

	    myPrint(cmd);            
            Process process = Runtime.getRuntime().exec(cmd, null, new File(workingDir));
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
	    String buffer = "";
//            while ((buffer = reader.readLine()) != null)
//                myPrint(buffer);
            reader.close();
            process.waitFor();
            int ret = process.exitValue();
            process.destroy();
        }
    }

    private static void runMvnCmd(List<TestResult> testResultList) {
        try {
	    String buffer = "";
            long startTime, endTime, timeElapsed;
            startTime = endTime = timeElapsed = 0;
            startTime = System.nanoTime();
      
            List<String> allTests = TestResult.getTestNames(testResultList);
            List<String> nonBeforeClassTests = new ArrayList<String>();
            List<String> beforeClassTests = new ArrayList<String>();

            for (String t : allTests) {
                boolean hasBeforeClass = false;
                for (String c : beforeClassList) {
                    if (t.contains(c)) {
                        hasBeforeClass = true;
                        break;
                    }
                }
                if (hasBeforeClass)
                    beforeClassTests.add(t);
                else
                    nonBeforeClassTests.add(t);
            }
            myPrint("size of beforeClassTests = " + beforeClassTests.size());
            myPrint("size of nonBeforeClassTests = " + nonBeforeClassTests.size());

	    if (nonBeforeClassTests.size() > 0)       
                runCombinedMvnCmd(nonBeforeClassTests, 200);
            if (beforeClassTests.size() > 0)
		runCombinedMvnCmd(beforeClassTests, 1);
	    else
		myPrint("beforeClassTests size is 0, just skip");

	    updateTestResult(testResultList);
            endTime = System.nanoTime();
            timeElapsed = endTime - startTime;
            myPrint("Execution time (sec) " + timeElapsed / 1000000000);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static List<TestResult> testCore(String parameter, String component, String reconfigMode, String v1, String v2,
            List<String> thisTestSet, String reconfPoint) {
        List<TestResult> failedTests = new ArrayList<TestResult>();
        if (thisTestSet == null) {
            System.exit(1);
        }

        if (thisTestSet.size() == 0)
            return failedTests; // empty
        
        myPrint("Test reconfigMode=" + reconfigMode + " v1=" + v1 + " v2=" + v2 + " reconfPoint=" + reconfPoint); 
        List<TestResult> testResultList = new ArrayList<TestResult>();

	//myPrint("thisTestSet before removing vanilla failure: " + thisTestSet.size());
	// remove vanilla failed tests
        myPrint("# of vanilla failed tests is " + vanillaFailedTestList.size());
	thisTestSet.removeIf(test -> vanillaFailedTestList.contains(test));
	myPrint("thisTestSet after filter out vanilla failure: " + thisTestSet.size());
 
	// construct TestResult list
        for (String test : thisTestSet) {
            testResultList.add(new TestResult(test, parameter, component, v1, v2, reconfPoint));
        }
        cleanUpSharedFiles();
        setupTestTuple(reconfigMode, parameter, component, v1, v2, reconfPoint);
        runMvnCmd(testResultList);
        
        for (TestResult t : testResultList) {
            if (Integer.valueOf(t.result) < 0)
                failedTests.add(t);
        }
        return failedTests;
    }
      
    public static List<TestResult> testLogic(String parameter, String component, String v1, String v2, String reconfPoint, List<String> testSet) {    
        List<TestResult> failedListv1v2 = testCore(parameter, component, "v1v2", v1, v2, testSet, reconfPoint); // all
        myPrint("failed v1v2 list size " + failedListv1v2.size());
        for (TestResult t : failedListv1v2)
            myPrint(t.testName);
      
        List<TestResult> issueList = new ArrayList<TestResult>();
        int runs = Controller.RECHECK_RUNTIMES;
	int thresholdOfIssues = 10;
        myPrint("do " + runs + " v1v1 v2v2 tests to filter false alarm");
        for (TestResult t : failedListv1v2) {
            myPrint("failed v1v2 test: " + t.testName + " v1 " + v1 + " v2 " + v2);
            int i = 0;
            List<String> singleTest = new ArrayList<String>();
            singleTest.add(t.testName);
            for(; i<runs; i++) {
                List<TestResult> failedList1 = testCore(parameter, component, "v1v1", v1, "", singleTest, reconfPoint);
                List<TestResult> failedList2 = testCore(parameter, component, "v2v2", "", v2, singleTest, reconfPoint);
                if (failedList1.size() > 0) {
                    myPrint("v1v1 also failed for test " + t.testName + " with v1 " + v1 + ", no issue.");
                    break;
                }
                if (failedList2.size() > 0) {
                    myPrint("v2v2 also failed for test " + t.testName + " with v2 " + v2 + ", no issue.");
                    break;
                }
            }
            if (i == runs) {
                myPrint("v1v1 and v2v2 succeed for " + runs + " times, add into issueList");
                issueList.add(t);
            }

            // do not continue to test if we have enough issues
            if (issueList.size() >= thresholdOfIssues)
                break;
        }

	return issueList;
    }

    public static void myPrint(Object o) {
        String str = (String) o;
        System.out.println(str);
        if (runLogWriter == null) {
            System.out.println("=null");
            return;
        }
        try {
            runLogWriter.write(str + System.lineSeparator());
            runLogWriter.flush();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void loadStaticTestData(String paraType) {
        try {
            BufferedReader reader = null;
            String buffer = "";

            reader = new BufferedReader(new FileReader(new File(beforeClassFileName)));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                beforeClassList.add(buffer.trim());
            }
            reader.close();
            
	    /*reader = new BufferedReader(new FileReader(new File(vanillaFailedTestFileName)));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                vanillaFailedTestList.add(buffer.trim());
            }
            reader.close();*/
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
  
    public static void setupTestTuple(String mode, String parameter, String component, String v1, String v2, String reconfPoint) {
	if (!mode.equals("v1v1") && !mode.equals("v2v2") && !mode.equals("v1v2") && !mode.equals("none")) {
	    myPrint("Error, wrong mode " + mode);
	    System.exit(1);
	}
        try {
            BufferedWriter writer1 = new BufferedWriter(new FileWriter(new File(parameterFileName)));
            writer1.write(parameter);
            writer1.close();
            
	    BufferedWriter writer2 = new BufferedWriter(new FileWriter(new File(reconfigComponentFileName)));
            writer2.write(component);
            writer2.close();
            
            BufferedWriter writer3 = new BufferedWriter(new FileWriter(new File(reconfigModeFileName)));
            writer3.write(mode);
            writer3.close();
            
            BufferedWriter writer4 = new BufferedWriter(new FileWriter(new File(v1FileName)));
            writer4.write(v1);
            writer4.close();
            
            BufferedWriter writer5 = new BufferedWriter(new FileWriter(new File(v2FileName)));
            writer5.write(v2);
            writer5.close();
            
            BufferedWriter writer6 = new BufferedWriter(new FileWriter(new File(reconfPointFileName)));
            writer6.write(reconfPoint);
            writer6.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void cleanUpSharedFiles() {
        try {
            File testResultDir = new File(testResultDirName);
            for (File testResult : testResultDir.listFiles()) {
                if (testResult.isFile()) {
                    testResult.delete();
                }
            }
            setupTestTuple("none", "", "", "", "", "");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
