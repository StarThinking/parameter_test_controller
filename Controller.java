import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;
import java.time.LocalDate;

public class Controller {

    public static String workingDir = "/root/hadoop-3.1.2-src-reconf/hadoop-hdfs-project";
    public static String controllerRootDir = "/root/parameter_test_controller/";
    
    /* shared files*/
    public static String testResultDirName = controllerRootDir + "shared/test_results";
    public static String parameterFileName = controllerRootDir + "shared/parameter";
    public static String reconfigModeFileName = controllerRootDir + "shared/reconfig_mode";
    public static String reconfigComponentFileName = controllerRootDir + "shared/reconfig_component";
    public static String v1FileName = controllerRootDir + "shared/v1";
    public static String v2FileName = controllerRootDir + "shared/v2";
   
    /* static test and parameter per component */
    public static String testForNameNodeFileName = controllerRootDir + "test_for_component/hdfs/restart_namenode.txt";
    public static String testForDataNodeFileName = controllerRootDir + "test_for_component/hdfs/restart_datanode.txt";
    public static String testForJournalNodeFileName = controllerRootDir + "test_for_component/hdfs/restart_journalnode.txt";
    public static List<String> nameNodeTestList = new ArrayList<String>();
    public static List<String> dataNodeTestList = new ArrayList<String>();
    public static List<String> journalNodeTestList = new ArrayList<String>();
    
    static class TestResult {
        public String testName = "";
        public String result = "";
        public String failureMessage = "";
        public String stackTrace = "";
        public String parameter = "";
        public String component = "";
        public String v1 = "";
        public String v2 = "";
        public static int NumOfFieldsFromFile = 4;
        public static String fileName = "";

        public TestResult(String testName, String parameter, String component, String v1, String v2) {
            this.testName = testName;
            this.parameter = parameter;
            this.component = component;
            this.v1 = v1;
            this.v2 = v2;
            this.result = "1"; // some tests may not complete, so let's set it as success by default
        }

        @Override
        public String toString() {
            return "parameter: " + parameter + "\n" +
                "component: " +component + "\n" +
                "v1 v2: " + v1 + " " + v2 + "\n" +
                "testName: " + testName + "\n" +
                "result: " + result;
        }
        
        public String completeInfo() {
            return "parameter: " + parameter + "\n" +
                "component: " +component + "\n" +
                "v1 v2: " + v1 + " " + v2 + "\n" +
                "testName: " + testName + "\n" +
                "result: " + result + "\n" +
                "failureMessage: " + failureMessage + "\n" +
                "stackTrace: " + stackTrace + "\n";
        }

        public static TestResult getTestResultByName(List<TestResult> list, String name) {
            for (TestResult t : list) {
                if (t.testName.equals(name))
                    return t;
            }
            // error
            System.out.println("Error: name " + name + " cannot be found in the list");
            return null;
        }

        public static List<String> getTestNames(List<TestResult> list) {
            List<String> names = new ArrayList<String>();
            for (TestResult t : list)
                names.add(t.testName);
            return names;
        } 

        public static void setFileName(String name) {
            TestResult.fileName = name;
        }

        public static void writeIntoFile(List<TestResult> list) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(TestResult.fileName, true)); // append;
                for (TestResult t : list) {
                    writer.write(t.completeInfo());
                    writer.newLine();
                }
                writer.close();
            } catch(Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static void updateTestResult(List<TestResult> testResultList) {
        String SEPERATOR = "@@@";
        try {
            File testResultDir = new File(testResultDirName);
            List<String> updatedTests = new ArrayList<String>();
            for (File f : testResultDir.listFiles()) {
                if (f.isFile()) {
                    System.out.println("updating test result for " + f.getName());
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
                        System.out.println("Error: the content for " + f.getName() + " is wrong, length = " + contents.length);
                        System.out.println("Content: ");
                        System.out.println(sb.toString());
                        continue;
                    } else {
                        String testName = contents[0];
                        TestResult testResult = TestResult.getTestResultByName(testResultList, testName);
                        if (testResult == null) {
                            System.out.println("Error: cannot find testResult by test name " + testName);
                            continue;
                        } else {
                            testResult.result = contents[1];
                            testResult.failureMessage = contents[2];
                            testResult.stackTrace = contents[3];
                            updatedTests.add(testName);
                            //System.out.println(testResult.completeInfo());
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
                    System.out.println("Warn: test " + t + " has not been updated !");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void runMvnCmd(List<TestResult> testResultList) {
        try {
	    String buffer = "";
            long startTime, endTime, timeElapsed;
            startTime = endTime = timeElapsed = 0;
        
            // merge tests into a single command 
            String combinedMethods = "";
            List<String> testNameList = TestResult.getTestNames(testResultList);
            int numOfTests = 0;
            for (String test : testNameList) {
                numOfTests ++;
                if (numOfTests == testNameList.size())
                    combinedMethods += test;
                else
                    combinedMethods += test + ",";
            }
            System.out.println("Number of combined methods is " + numOfTests);
            String cmd = "mvn test -Dtest=" + combinedMethods;
            
            startTime = System.nanoTime();
            Process process = Runtime.getRuntime().exec(cmd, null, new File(workingDir));
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
//            while ((buffer = reader.readLine()) != null)
//                System.out.println(buffer);
            reader.close();
            process.waitFor();
            int ret = process.exitValue();
	    updateTestResult(testResultList);
            process.destroy();
            endTime = System.nanoTime();

            timeElapsed = endTime - startTime;
            System.out.println("Execution time (sec) " + timeElapsed / 1000000000);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static List<TestResult> testForTupleWithGivenTests(String parameter, String component, String reconfigMode, String v1, String v2, List<String> thisTestSet) {
        List<TestResult> failedTests = new ArrayList<TestResult>();
        if (thisTestSet == null) {
            System.exit(1);
        }

        if (thisTestSet.size() == 0)
            return failedTests; // empty
        
        System.out.println("Test reconfigMode=" + reconfigMode + " v1=" + v1 + " v2=" + v2); 
        List<TestResult> testResultList = new ArrayList<TestResult>();
        // construct TestResult list
        for (String test : thisTestSet) {
            testResultList.add(new TestResult(test, parameter, component, v1, v2));
        }
        cleanUpSharedFiles();
        setupTestTuple(parameter, component, reconfigMode, v1, v2);
        runMvnCmd(testResultList);
        
        for (TestResult t : testResultList) {
            //System.out.println(t);
            if (Integer.valueOf(t.result) < 0)
                failedTests.add(t);
        }
        return failedTests;
    }

    public static void testCorrectness(String parameter, String component, String v1, String v2, List<String> testSet) {
	List<TestResult> failedList = testForTupleWithGivenTests(parameter, component, "none", "", "", testSet); // all
    }
      
    public static List<TestResult> testV1V2Pair(String parameter, String component, String v1, String v2, List<String> testSet) {    
        List<TestResult> failedListv1v2 = testForTupleWithGivenTests(parameter, component, "v1v2", v1, v2, testSet); // all
        List<TestResult> failedListv1v1 = testForTupleWithGivenTests(parameter, component, "v1v1", v1, "", TestResult.getTestNames(failedListv1v2)); 
        List<TestResult> failedListv2v2 = testForTupleWithGivenTests(parameter, component, "v2v2", "", v2, TestResult.getTestNames(failedListv1v2));
	int thresholdOfIssues = 10;
        List<TestResult> suspicousList = new ArrayList<TestResult>();
        List<TestResult> issueList = new ArrayList<TestResult>();

        System.out.println("failed v1v2 list size " + failedListv1v2.size());
        
        for (TestResult v1v2Test : failedListv1v2) {
            boolean v1v1Failed, v2v2Failed;
            v1v1Failed = v2v2Failed = false;
            for (TestResult v1v1Test : failedListv1v1) {
                if (v1v2Test.testName.equals(v1v1Test.testName)) {
                    v1v1Failed = true;
                    break;
                }
            }
            for (TestResult v2v2Test : failedListv2v2) {
                if (v1v2Test.testName.equals(v2v2Test.testName)) {
                    v2v2Failed = true;
                    break;
                }
            }
        
            if (v1v1Failed == false && v2v2Failed == false) 
                suspicousList.add(v1v2Test);
        }

        System.out.println("suspicous list size " + suspicousList.size() + " :");
        for (TestResult t : suspicousList)
            System.out.println(t.testName);
    
        System.out.println("test v1v1 v2v2 to filter suspicous");
        for (TestResult t : suspicousList) {
            System.out.println("suspicious " + t.testName + " v1 " + v1 + " v2 " + v2);
            int runs = 3;
            int i = 0;
            List<String> singleTest = new ArrayList<String>();
            singleTest.add(t.testName);
            for(; i<runs; i++) {
                List<TestResult> failedList1 = testForTupleWithGivenTests(parameter, component, "v1v1", v1, "", singleTest);
                List<TestResult> failedList2 = testForTupleWithGivenTests(parameter, component, "v2v2", "", v2, singleTest);
                if (failedList1.size() > 0 || failedList2.size() > 0) {
                    System.out.println("v1v1 or v2v2 also failed, no issue");
                    break;
                }
            }
            if (i == runs) {
                System.out.println("v1v1 and v2v2 succeed for " + runs + " times, add into issueList");
                issueList.add(t);
            }

            // do not continue to test if we have enough issues
            if (issueList.size() >= thresholdOfIssues)
                break;
        }

        System.out.println("issue list size " + issueList.size() + " :");
        for (TestResult t : issueList)
            System.out.println(t);

        TestResult.writeIntoFile(issueList);

	return issueList;
    }
    
    public static void main(String[] args) {
	String componentFocused = "";
        String parameterToTest = "";
        String oneTest = "";
        List<String> testSet = null;
        long startTime, endTime, timeElapsed;
        startTime = endTime = timeElapsed = 0; 
        
        int onetestArgIndex = 2; 
        if (!(args.length >= onetestArgIndex && args.length <= onetestArgIndex+1)) {
            System.out.println("Error: args length is " + args.length);
            System.out.println("Controller parameterToTest componentFocused [optional: one_test]");
            System.exit(1);
        }

        /* load static data first */
        loadStaticData();
        
	parameterToTest = args[0];
	componentFocused = args[1];
        if (args.length == onetestArgIndex+1) { // only use a single test
            oneTest = args[onetestArgIndex];
	    System.out.println("onetest is " + oneTest);
            testSet = new ArrayList<String>();
            testSet.add(oneTest);
        } else {
	    if (componentFocused.equals("namenode")) 
		testSet = nameNodeTestList;
	    else if (componentFocused.equals("datanode"))  
		testSet = dataNodeTestList;   
	    else if (componentFocused.equals("journalnode"))
		testSet = journalNodeTestList;
	    else {
		System.out.println("Error: wrong component " + componentFocused);
		System.exit(1);
	    }
        }
        System.out.println("parameter to test: " + parameterToTest); 
        System.out.println("component to reconfig: " + componentFocused); 
	if (!componentFocused.equals("namenode") && !componentFocused.equals("datanode") && !componentFocused.equals("journalnode")) {
	    System.out.println("Error: wrong component " + componentFocused);
	    System.exit(1);
	}
        System.out.println("size of testSet: " + testSet.size()); 
        TestResult.setFileName(Controller.controllerRootDir + parameterToTest + "_" + componentFocused + "_issue_" +
                LocalDate.now() + ".txt");

	startTime = System.nanoTime();
//	testCorrectness(parameterToTest, componentFocused, "", "", testSet);
	List<TestResult> list1 = testV1V2Pair(parameterToTest, componentFocused, "true", "false", testSet);
        List<TestResult> list2 = testV1V2Pair(parameterToTest, componentFocused, "false", "true", testSet);
        endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        System.out.println("Total execution time in seconds : " + timeElapsed / 1000000000);
        System.out.println();
    }
    
    public static void loadStaticData() {
        try {
            BufferedReader reader0 = new BufferedReader(new FileReader(new File(testForNameNodeFileName)));
            String buffer0 = "";
            while ((buffer0 = reader0.readLine()) != null) {
                nameNodeTestList.add(buffer0.trim());
            }
            reader0.close();
            
	    BufferedReader reader1 = new BufferedReader(new FileReader(new File(testForDataNodeFileName)));
            String buffer1 = "";
            while ((buffer1 = reader1.readLine()) != null) {
                dataNodeTestList.add(buffer1.trim());
            }
            reader1.close();
            
	    BufferedReader reader2 = new BufferedReader(new FileReader(new File(testForJournalNodeFileName)));
            String buffer2 = "";
            while ((buffer2 = reader2.readLine()) != null) {
                journalNodeTestList.add(buffer2.trim());
            }
            reader2.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
  
    public static void setupTestTuple(String parameter, String component, String mode, String v1, String v2) {
	if (!mode.equals("v1v1") && !mode.equals("v2v2") && !mode.equals("v1v2")) {
	    System.out.println("Error, wrong mode " + mode);
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
            
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(new File(parameterFileName)));
            writer2.write("");
            writer2.close();
            
            BufferedWriter writer3 = new BufferedWriter(new FileWriter(new File(reconfigModeFileName)));
            writer3.write("");
            writer3.close();
            
	    BufferedWriter writer4 = new BufferedWriter(new FileWriter(new File(reconfigComponentFileName)));
            writer4.write("");
            writer4.close();
            
            BufferedWriter writer5 = new BufferedWriter(new FileWriter(new File(v1FileName)));
            writer5.write("");
            writer5.close();
            
            BufferedWriter writer6 = new BufferedWriter(new FileWriter(new File(v2FileName)));
            writer6.write("");
            writer6.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
