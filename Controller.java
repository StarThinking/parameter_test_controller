import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;
import java.text.SimpleDateFormat;

public class Controller {

    public static String workingDir = "/root/hadoop-3.1.2-src-reconf/hadoop-hdfs-project";
    public static String controllerRootDir = "/root/parameter_test_controller/";
    public static StringBuilder runLogBuffer = new StringBuilder();
    public static BufferedWriter runLogWriter = null;

    /* shared files*/
    public static String testResultDirName = controllerRootDir + "shared/test_results";
    public static String parameterFileName = controllerRootDir + "shared/parameter";
    public static String reconfigModeFileName = controllerRootDir + "shared/reconfig_mode";
    public static String reconfigComponentFileName = controllerRootDir + "shared/reconfig_component";
    public static String v1FileName = controllerRootDir + "shared/v1";
    public static String v2FileName = controllerRootDir + "shared/v2";
    public static String componentHasStoppedFileName = controllerRootDir + "shared/componentHasStopped";
   
    /* static test and parameter per component */
    public static String restartNameNodeTestFileName = controllerRootDir + "test_for_component/hdfs/restart_namenode.txt";
    public static String restartDataNodeTestFileName = controllerRootDir + "test_for_component/hdfs/restart_datanode.txt";
    public static String restartJournalNodeTestFileName = controllerRootDir + "test_for_component/hdfs/restart_journalnode.txt";
    public static List<String> restartNameNodeTestList = new ArrayList<String>();
    public static List<String> restartDataNodeTestList = new ArrayList<String>();
    public static List<String> restartJournalNodeTestList = new ArrayList<String>();
    
    public static String startNameNodeTestFileName = controllerRootDir + "test_for_component/hdfs/start_namenode.txt";
    public static String startDataNodeTestFileName = controllerRootDir + "test_for_component/hdfs/start_datanode.txt";
    public static String startJournalNodeTestFileName = controllerRootDir + "test_for_component/hdfs/start_journalnode.txt";
    public static List<String> startNameNodeTestList = new ArrayList<String>();
    public static List<String> startDataNodeTestList = new ArrayList<String>();
    public static List<String> startJournalNodeTestList = new ArrayList<String>();
    
    static {
        loadStaticTestData();
    }

    static class TestResult {
        public String testName = "";
        public String result = "";
        public String failureMessage = "";
        public String stackTrace = "";
        public String parameter = "";
        public String component = "";
        public String v1 = "";
        public String v2 = "";
        public String componentHasStopped = "";
        public static int NumOfFieldsFromFile = 4;
        public static String fileName = "";

        public TestResult(String testName, String parameter, String component, String v1, String v2, String componentHasStopped) {
            this.testName = testName;
            this.parameter = parameter;
            this.component = component;
            this.componentHasStopped = componentHasStopped;
            this.v1 = v1;
            this.v2 = v2;
            this.result = "1"; // some tests may not complete, so let's set it as success by default
        }

        @Override
        public String toString() {
            return "parameter: " + parameter + "\n" +
                "component: " + component + "\n" +
                "componentHasStopped: " + componentHasStopped + "\n" +
                "v1 v2: " + v1 + " " + v2 + "\n" +
                "testName: " + testName + "\n" +
                "result: " + result;
        }
        
        public String completeInfo() {
            return "parameter: " + parameter + "\n" +
                "component: " +component + "\n" +
                "componentHasStopped: " + componentHasStopped + "\n" +
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
            myPrint("Error: name " + name + " cannot be found in the list");
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
                writer.write("---------------------------------------short report---------------------------------------------");
                for (TestResult t : list) {
                    writer.write(t.toString());
                    writer.newLine();
                }
                writer.write("---------------------------------------full report---------------------------------------------");
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
            startTime = System.nanoTime();

            List<String> testNameList = TestResult.getTestNames(testResultList);
            
            // split into parts
            int numOfTestsPerPart = 100;
            //int numOfParts = testNameList.size() / numOfTestsPerPart;
            int indexOfParts = 0;
            int start = 0;
            int end = 0;
            List<List<String>> listOfParts = new ArrayList<List<String>>(); 
            do {
                start = indexOfParts * numOfTestsPerPart;
                end = (indexOfParts + 1) * numOfTestsPerPart;
                if (end >= testNameList.size())
                    end = testNameList.size();
                myPrint("indexOfParts = " + indexOfParts + " start = " + start + " end = " + end);
                listOfParts.add(testNameList.subList(start, end));
                indexOfParts ++;
            } while(end < testNameList.size());
            
            for (List<String> partOfTestNameList: listOfParts) { 
                // merge tests into a single command 
                String combinedMethods = "";
                int numOfTests = 0;
                myPrint("part size = " + partOfTestNameList.size());
                for (String test : partOfTestNameList) {
                    numOfTests ++;
                    if (numOfTests == partOfTestNameList.size())
                        combinedMethods += test;
                    else
                        combinedMethods += test + ",";
                }
                myPrint("Number of combined methods is " + numOfTests);
                String cmd = "mvn test -Dtest=" + combinedMethods;
                
                Process process = Runtime.getRuntime().exec(cmd, null, new File(workingDir));
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
//                while ((buffer = reader.readLine()) != null)
//                    myPrint(buffer);
                reader.close();
                process.waitFor();
                int ret = process.exitValue();
                process.destroy();
            }
	    updateTestResult(testResultList);
            endTime = System.nanoTime();
            timeElapsed = endTime - startTime;
            myPrint("Execution time (sec) " + timeElapsed / 1000000000);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static List<TestResult> testForTupleWithGivenTests(String parameter, String component, String reconfigMode, String v1, String v2,
            List<String> thisTestSet, String componentHasStopped) {
        List<TestResult> failedTests = new ArrayList<TestResult>();
        if (thisTestSet == null) {
            System.exit(1);
        }

        if (thisTestSet.size() == 0)
            return failedTests; // empty
        
        myPrint("Test reconfigMode=" + reconfigMode + " v1=" + v1 + " v2=" + v2 + " componentHasStopped=" + componentHasStopped); 
        List<TestResult> testResultList = new ArrayList<TestResult>();
        // construct TestResult list
        for (String test : thisTestSet) {
            testResultList.add(new TestResult(test, parameter, component, v1, v2, componentHasStopped));
        }
        cleanUpSharedFiles();
        setupTestTuple(parameter, component, reconfigMode, v1, v2, componentHasStopped);
        runMvnCmd(testResultList);
        
        for (TestResult t : testResultList) {
            //myPrint(t);
            if (Integer.valueOf(t.result) < 0)
                failedTests.add(t);
        }
        return failedTests;
    }

    //public static void testCorrectness(String parameter, String component, String v1, String v2, List<String> testSet) {
	//List<TestResult> failedList = testForTupleWithGivenTests(parameter, component, "none", "", "", testSet); // all
    //}
      
    public static List<TestResult> testV1V2Pair(String parameter, String component, String v1, String v2, List<String> testSet, String componentHasStopped) {    
        List<TestResult> failedListv1v2 = testForTupleWithGivenTests(parameter, component, "v1v2", v1, v2, testSet, componentHasStopped); // all
        List<TestResult> failedListv1v1 = testForTupleWithGivenTests(parameter, component, "v1v1", v1, "", TestResult.getTestNames(failedListv1v2), componentHasStopped); 
        List<TestResult> failedListv2v2 = testForTupleWithGivenTests(parameter, component, "v2v2", "", v2, TestResult.getTestNames(failedListv1v2), componentHasStopped);
	int thresholdOfIssues = 10;
        List<TestResult> suspicousList = new ArrayList<TestResult>();
        List<TestResult> issueList = new ArrayList<TestResult>();

        myPrint("failed v1v2 list size " + failedListv1v2.size());
        
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

        myPrint("suspicous list size " + suspicousList.size() + " :");
        for (TestResult t : suspicousList)
            myPrint(t.testName);
    
        myPrint("test v1v1 v2v2 to filter suspicous");
        for (TestResult t : suspicousList) {
            myPrint("suspicious " + t.testName + " v1 " + v1 + " v2 " + v2);
            int runs = 3;
            int i = 0;
            List<String> singleTest = new ArrayList<String>();
            singleTest.add(t.testName);
            for(; i<runs; i++) {
                List<TestResult> failedList1 = testForTupleWithGivenTests(parameter, component, "v1v1", v1, "", singleTest, componentHasStopped);
                List<TestResult> failedList2 = testForTupleWithGivenTests(parameter, component, "v2v2", "", v2, singleTest, componentHasStopped);
                if (failedList1.size() > 0 || failedList2.size() > 0) {
                    myPrint("v1v1 or v2v2 also failed, no issue");
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

    public static List<TestResult> testV1V2PairRestartPointWrapper(String parameter, String component, String v1, String v2, List<String> testSet) {
        List<TestResult> mergedIssueList = new ArrayList<TestResult>();
        List<String> restartTestSet = null;
        List<String> startTestSet = null;
        
        /* set restartTestSet/startTestSet by component if testSet is null */
        if (testSet == null) {
	    if (component.equals("NameNode")) {
		restartTestSet = restartNameNodeTestList;
                startTestSet = startNameNodeTestList;
            } else if (component.equals("DataNode")) { 
		restartTestSet = restartDataNodeTestList;
                startTestSet = startDataNodeTestList;
            } else if (component.equals("JournalNode")) {
		restartTestSet = restartJournalNodeTestList;
                startTestSet = startJournalNodeTestList;
            }
        } else {
            restartTestSet = testSet;
            startTestSet = testSet;
        }
        myPrint("restartTestSet size is " + restartTestSet.size() + ", startTestSet size is " + startTestSet.size());

        try {
            String componentHasStopped = "";
            
            myPrint("Testing restart... size " + restartTestSet.size());
            componentHasStopped = "0";
            List<TestResult> restartIssueList = testV1V2Pair(parameter, component, v1, v2, restartTestSet, componentHasStopped);
            mergedIssueList.addAll(restartIssueList);

            myPrint("Testing start... size " + startTestSet.size());
            componentHasStopped = "1";
            List<TestResult> startIssueList = testV1V2Pair(parameter, component, v1, v2, startTestSet, componentHasStopped);
            mergedIssueList.addAll(startIssueList);

            } catch(Exception e) {
            e.printStackTrace();
        }

        myPrint("");
        myPrint("---------------------------------------short report---------------------------------------------");
        myPrint("mergedIssueList size " + mergedIssueList.size() + " :");
        for (TestResult t : mergedIssueList)
            myPrint(t);
        myPrint("---------------------------------------short report---------------------------------------------");
        myPrint("");
        
        return mergedIssueList;
    }

    public static void main(String[] args) {
        String parameterToTest = "";
        String componentFocused = "";
        String oneTest = "";
        List<String> testSet = null;
        long startTime, endTime, timeElapsed;
        startTime = endTime = timeElapsed = 0; 
    
        /* do this at the begining */
        int onetestArgIndex = 2; 
        if (!(args.length >= onetestArgIndex && args.length <= onetestArgIndex+1)) {
            myPrint("Error: args length is " + args.length);
            myPrint("Controller parameterToTest componentFocused [optional: one_test]");
            System.exit(1);
        }
        
	parameterToTest = args[0];
	myPrint("parameter to test: " + parameterToTest); 
	componentFocused = args[1];
        myPrint("component to reconfig: " + componentFocused); 
	if (!componentFocused.equals("NameNode") && !componentFocused.equals("DataNode") && !componentFocused.equals("JournalNode")) {
	    myPrint("Error: wrong component " + componentFocused);
	    System.exit(1);
	}

	/* set test Set */
        if (args.length == onetestArgIndex+1) { // only use a single test
            oneTest = args[onetestArgIndex];
	    myPrint("onetest is " + oneTest);
            testSet = new ArrayList<String>();
            testSet.add(oneTest);
        } 
        
	startTime = System.nanoTime();
//	testCorrectness(parameterToTest, componentFocused, "", "", testSet);
	List<TestResult> issueList1 = testV1V2PairRestartPointWrapper(parameterToTest, componentFocused, "true", "false", testSet);
        List<TestResult> issueList2 = testV1V2PairRestartPointWrapper(parameterToTest, componentFocused, "false", "true", testSet);
        endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        myPrint("Total execution time in seconds : " + timeElapsed / 1000000000);
        
        /* log */
        Date date = new Date();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
	String dateTime = formatter.format(date);
	TestResult.setFileName(Controller.controllerRootDir + parameterToTest + "_issue_" + componentFocused + "_" +
                dateTime + ".txt");
        TestResult.writeIntoFile(issueList1);
        TestResult.writeIntoFile(issueList2);
        
        try {
            String runLogPath = Controller.controllerRootDir + parameterToTest + "_run_" + componentFocused + "_" + dateTime + ".txt";
            runLogWriter = new BufferedWriter(new FileWriter(new File(runLogPath), true)); 
            runLogWriter.write(runLogBuffer.toString());
            runLogWriter.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void myPrint(Object o) {
        String str = (String) o;
        System.out.println(str);
        runLogBuffer.append(str + System.lineSeparator());
    }
    
    public static void loadStaticTestData() {
        try {
            BufferedReader reader = null;
            String buffer = "";

            reader = new BufferedReader(new FileReader(new File(restartNameNodeTestFileName)));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                restartNameNodeTestList.add(buffer.trim());
            }
            reader.close();
            
	    reader = new BufferedReader(new FileReader(new File(restartDataNodeTestFileName)));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                restartDataNodeTestList.add(buffer.trim());
            }
            reader.close();
            
	    reader = new BufferedReader(new FileReader(new File(restartJournalNodeTestFileName)));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                restartJournalNodeTestList.add(buffer.trim());
            }
            reader.close();
            
            reader = new BufferedReader(new FileReader(new File(startNameNodeTestFileName)));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                startNameNodeTestList.add(buffer.trim());
            }
            reader.close();
            
	    reader = new BufferedReader(new FileReader(new File(startDataNodeTestFileName)));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                startDataNodeTestList.add(buffer.trim());
            }
            reader.close();
            
	    reader = new BufferedReader(new FileReader(new File(startJournalNodeTestFileName)));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                startJournalNodeTestList.add(buffer.trim());
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
  
    public static void setupTestTuple(String parameter, String component, String mode, String v1, String v2, String componentHasStopped) {
	if (!mode.equals("v1v1") && !mode.equals("v2v2") && !mode.equals("v1v2")) {
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
            
            BufferedWriter writer6 = new BufferedWriter(new FileWriter(new File(componentHasStoppedFileName)));
            writer6.write(componentHasStopped);
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

            BufferedWriter writer1 = new BufferedWriter(new FileWriter(new File(componentHasStoppedFileName)));
            writer1.write("");
            writer1.close();
            
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
