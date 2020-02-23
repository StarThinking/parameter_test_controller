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

    /* shared files */
    public static String testResultDirName = controllerRootDir + "shared/test_results";
    public static String parameterFileName = controllerRootDir + "shared/parameter";
    public static String reconfigModeFileName = controllerRootDir + "shared/reconfig_mode";
    public static String reconfigComponentFileName = controllerRootDir + "shared/reconfig_component";
    public static String v1FileName = controllerRootDir + "shared/v1";
    public static String v2FileName = controllerRootDir + "shared/v2";
    public static String componentHasStoppedFileName = controllerRootDir + "shared/componentHasStopped";
    public static String currentBeforeClassTestNameFileName = controllerRootDir + "shared/currentBeforeClassTestName";
   
    /* static parameter value information */
    public static Map<String, List<String>> intNameNodeParameterValues = new HashMap<String, List<String>>();
    public static String intNameNodeParameterValuesFileName = controllerRootDir + "parameter_for_component/namenode_getInt_makeup_value.txt";
    public static Map<String, List<String>> intDataNodeParameterValues = new HashMap<String, List<String>>();
    public static String intDataNodeParameterValuesFileName = controllerRootDir + "parameter_for_component/datanode_getInt_makeup_value.txt";
    public static Map<String, List<String>> intJournalNodeParameterValues = new HashMap<String, List<String>>();
    public static String intJournalNodeParameterValuesFileName = controllerRootDir + "parameter_for_component/journalnode_getInt_makeup_value.txt";
    
    public static Map<String, List<String>> longNameNodeParameterValues = new HashMap<String, List<String>>();
    public static String longNameNodeParameterValuesFileName = controllerRootDir + "parameter_for_component/namenode_getLong_makeup_value.txt";
    public static Map<String, List<String>> longDataNodeParameterValues = new HashMap<String, List<String>>();
    public static String longDataNodeParameterValuesFileName = controllerRootDir + "parameter_for_component/datanode_getLong_makeup_value.txt";
    public static Map<String, List<String>> longJournalNodeParameterValues = new HashMap<String, List<String>>();
    public static String longJournalNodeParameterValuesFileName = controllerRootDir + "parameter_for_component/journalnode_getLong_makeup_value.txt";

    /* static test information for component */
    public static String beforeClassFileName = controllerRootDir + "test_for_component/hdfs/before_class.txt";
    public static List<String> beforeClassList = new ArrayList<String>();
    
    public static String getRestartNameNodeTestFileName(String paraType) {
	return controllerRootDir + "test_for_component/hdfs/" + paraType + "/restart_namenode.txt";
    }

    public static String getRestartDataNodeTestFileName(String paraType) {
	return controllerRootDir + "test_for_component/hdfs/" + paraType + "/restart_datanode.txt";
    }

    public static String getRestartJournalNodeTestFileName(String paraType) { 
	return controllerRootDir + "test_for_component/hdfs/" + paraType + "/restart_journalnode.txt";
    }
    
    public static String getStartNameNodeTestFileName(String paraType) {
	return controllerRootDir + "test_for_component/hdfs/" + paraType + "/start_namenode.txt";
    }

    public static String getStartDataNodeTestFileName(String paraType) {
	return controllerRootDir + "test_for_component/hdfs/" + paraType + "/start_datanode.txt";
    }

    public static String getStartJournalNodeTestFileName(String paraType) { 
	return controllerRootDir + "test_for_component/hdfs/" + paraType + "/start_journalnode.txt";
    }
    
    public static String getStartAllComponentTestFileName(String paraType) { 
	return controllerRootDir + "test_for_component/hdfs/" + paraType + "/start_all_component.txt";
    }
   
    public static List<String> restartNameNodeTestList = new ArrayList<String>();
    public static List<String> restartDataNodeTestList = new ArrayList<String>();
    public static List<String> restartJournalNodeTestList = new ArrayList<String>();
    public static List<String> startNameNodeTestList = new ArrayList<String>();
    public static List<String> startDataNodeTestList = new ArrayList<String>();
    public static List<String> startJournalNodeTestList = new ArrayList<String>();
    	
    /* used for Vanilla Correctness Test */
    public static String vanillaFailedTestFileName = controllerRootDir + "test_for_component/hdfs/vanilla_failed_test.txt";
    public static List<String> startAllComponentTestList = new ArrayList<String>();
    public static List<String> vanillaFailedTestList = new ArrayList<String>();
    
    //static {
    //    loadStaticTestData();
    //}

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
            // 1: succeed -1:failed
            this.result = "-1"; // some tests may not complete, so let's set it as success by default
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
		writer.newLine();
                for (TestResult t : list) {
                    writer.write(t.toString());
                    writer.newLine();
                }
		writer.newLine();
                writer.write("---------------------------------------full report---------------------------------------------");
		writer.newLine();
                for (TestResult t : list) {
                    writer.write(t.completeInfo());
                    writer.newLine();
                }
		writer.newLine();
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
                    //System.exit(-1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void runCombinedMvnCmd(List<String> testNameList, int numPerPart) throws Exception{
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
            //myPrint("indexOfParts = " + indexOfParts + " start = " + start + " end = " + end);
            listOfParts.add(testNameList.subList(start, end));
            indexOfParts ++;
        } while(end < testNameList.size());
        
        for (List<String> partOfTestNameList: listOfParts) { 
            // merge tests into a single command 
            String combinedMethods = "";
            int numOfTests = 0;
            //myPrint("part size = " + partOfTestNameList.size());
            for (String test : partOfTestNameList) {
                numOfTests ++;
                if (numOfTests == partOfTestNameList.size())
                    combinedMethods += test;
                else
                    combinedMethods += test + ",";
            }

            if (numPerPart == 1) { // before-class test
                myPrint("writing before-class test name into file " + combinedMethods);
                // overwrite
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(currentBeforeClassTestNameFileName)));
                writer.write(combinedMethods);
                writer.close();
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

    public static void runMvnCmd(List<TestResult> testResultList) {
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
            List<String> thisTestSet, String componentHasStopped) {
        List<TestResult> failedTests = new ArrayList<TestResult>();
        if (thisTestSet == null) {
            System.exit(1);
        }

        if (thisTestSet.size() == 0)
            return failedTests; // empty
        
        myPrint("Test reconfigMode=" + reconfigMode + " v1=" + v1 + " v2=" + v2 + " componentHasStopped=" + componentHasStopped); 
        List<TestResult> testResultList = new ArrayList<TestResult>();
	
	//myPrint("Vanilla failed tests contained:");
 	//thisTestSet.stream().filter(test -> vanillaFailedTestList.contains(test)).forEach(System.out::println); 

	myPrint("thisTestSet before removing vanilla failure: " + thisTestSet.size());
	// remove vanilla failed tests
	thisTestSet.removeIf(test -> vanillaFailedTestList.contains(test));
	myPrint("thisTestSet after filter out vanilla failure: " + thisTestSet.size());
        myPrint("# of vanilla failed tests is " + vanillaFailedTestList.size());
 
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

    public static List<TestResult> testVanillaCorrectness(String parameter, String component, String v1, String v2, List<String> testSet) {
	testSet = startAllComponentTestList;
        // dummy setting
	// parameter, component, componentHasStopped, v1, v2 are not effective for vanilla correctness test
	String componentHasStopped = "1";
	List<TestResult> failedList = testCore(parameter, component, "none", "", "", testSet, componentHasStopped); // all
        myPrint("failed v1v2 list size " + failedList.size());
	return failedList;
    }
      
    public static List<TestResult> testLogic(String parameter, String component, String v1, String v2, List<String> testSet, String componentHasStopped) {    
        List<TestResult> failedListv1v2 = testCore(parameter, component, "v1v2", v1, v2, testSet, componentHasStopped); // all
        myPrint("failed v1v2 list size " + failedListv1v2.size());
        for (TestResult t : failedListv1v2)
            myPrint(t.testName);
      
        List<TestResult> issueList = new ArrayList<TestResult>();
        int runs = 20;
	int thresholdOfIssues = 10;
        myPrint("do " + runs + " v1v1 v2v2 tests to filter false alarm");
        for (TestResult t : failedListv1v2) {
            myPrint("failed v1v2 test: " + t.testName + " v1 " + v1 + " v2 " + v2);
            int i = 0;
            List<String> singleTest = new ArrayList<String>();
            singleTest.add(t.testName);
            for(; i<runs; i++) {
                List<TestResult> failedList1 = testCore(parameter, component, "v1v1", v1, "", singleTest, componentHasStopped);
                List<TestResult> failedList2 = testCore(parameter, component, "v2v2", "", v2, singleTest, componentHasStopped);
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
            List<TestResult> restartIssueList = testLogic(parameter, component, v1, v2, restartTestSet, componentHasStopped);
            mergedIssueList.addAll(restartIssueList);

            myPrint("Testing start... size " + startTestSet.size());
            componentHasStopped = "1";
            List<TestResult> startIssueList = testLogic(parameter, component, v1, v2, startTestSet, componentHasStopped);
            mergedIssueList.addAll(startIssueList);

            } catch(Exception e) {
            e.printStackTrace();
        }

        myPrint("");
        myPrint("---------------------------------------short report---------------------------------------------");
        myPrint("mergedIssueList size " + mergedIssueList.size() + " :");
        for (TestResult t : mergedIssueList)
            myPrint(t.toString());
        myPrint("---------------------------------------short report---------------------------------------------");
        myPrint("");
        
        return mergedIssueList;
    }

    public static void main(String[] args) {
        String parameterType = "";
        String parameterToTest = "";
        String componentFocused = "";
        String oneTest = "";
        List<String> testSet = null;
        long startTime, endTime, timeElapsed;
        startTime = endTime = timeElapsed = 0; 

        int onetestArgIndex = 3; 
        //if (!(args.length >= onetestArgIndex && args.length <= onetestArgIndex+1)) {
        if (!(args.length >= onetestArgIndex)) {
            myPrint("Error: args length is " + args.length);
            myPrint("Controller parameterType parameterToTest componentFocused [optional: one_test]");
            System.exit(1);
        }
       
        parameterType = args[0];
	parameterToTest = args[1];
	componentFocused = args[2];
        if (!parameterType.equals("Boolean") && !parameterType.equals("Int") && !parameterType.equals("Long")) {
            myPrint("Error: wrong component " + componentFocused);
            System.exit(1);
        } else {
  	    loadStaticTestData(parameterType);
	}
	if (!componentFocused.equals("NameNode") && !componentFocused.equals("DataNode") && !componentFocused.equals("JournalNode") && !componentFocused.equals("None")) {
	    myPrint("Error: wrong component " + componentFocused);
	    System.exit(1);
	}
        
	/* set run log */
        Date date = new Date();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
	String dateTime = formatter.format(date);
        String runLogPath = Controller.controllerRootDir + parameterToTest + "_run_" + componentFocused + "_" + dateTime + ".txt";
        try {
            runLogWriter = new BufferedWriter(new FileWriter(new File(runLogPath), true)); 
        } catch(Exception e) {
            e.printStackTrace();
        }
	
	myPrint("parameter to test: " + parameterToTest); 
        myPrint("component to reconfig: " + componentFocused); 

	/* set test Set */
        if (args.length >= onetestArgIndex+1) { // only use a single test
            testSet = new ArrayList<String>();
            int index = onetestArgIndex;
            for (; index < args.length; index++) {
                testSet.add(args[index]);
            }
        } 
        
	startTime = System.nanoTime();
	
	if (componentFocused.equals("None")) {
	    TestResult.setFileName(Controller.controllerRootDir + parameterToTest + "_vanilla_issue_" + componentFocused + "_" +
                    dateTime + ".txt");
	} else {
	    TestResult.setFileName(Controller.controllerRootDir + parameterToTest + "_issue_" + componentFocused + "_" +
                    dateTime + ".txt");
	}
	
	if (componentFocused.equals("None")) {
	    List<TestResult> issueList0 = testVanillaCorrectness(parameterToTest, componentFocused, "", "", testSet);
	    TestResult.writeIntoFile(issueList0);
	} else {
            // Set value pairs
            List<List<String>> valuePairs = new ArrayList<List<String>>();

            if (parameterType.equals("Boolean")) {
                valuePairs.add(new ArrayList<String>(Arrays.asList("true", "false")));
                valuePairs.add(new ArrayList<String>(Arrays.asList("false", "true")));
            } else if (parameterType.equals("Int") || parameterType.equals("Long")) {
                myPrint("parameterType is " + parameterType);
		List<String> values = null;
                if (parameterType.equals("Int")) {
		    if (componentFocused.equals("NameNode")) {
                        values = intNameNodeParameterValues.get(parameterToTest);
		    } else if (componentFocused.equals("DataNode")) {
                        values = intDataNodeParameterValues.get(parameterToTest);
		    } else if (componentFocused.equals("JournalNode")) {
                        values = intJournalNodeParameterValues.get(parameterToTest);
                    }
                } else if (parameterType.equals("Long")) {
		    if (componentFocused.equals("NameNode")) {
                        values = longNameNodeParameterValues.get(parameterToTest);
		    } else if (componentFocused.equals("DataNode")) {
                        values = longDataNodeParameterValues.get(parameterToTest);
		    } else if (componentFocused.equals("JournalNode")) {
                        values = longJournalNodeParameterValues.get(parameterToTest);
                    }
                }

                if (values == null) {
                    myPrint("Error: cannot find " + parameterType + " " + componentFocused + "  parameter " + parameterToTest);
                    System.exit(1);
                }

                if (values.size() != 3) {
                     myPrint("Error: num of values is not 2 " + values);
                     System.exit(1);
                }

                myPrint("parameter " + parameterToTest + " values " + values);
	  	valuePairs.add(new ArrayList<String>(Arrays.asList(values.get(1), values.get(0))));
	  	valuePairs.add(new ArrayList<String>(Arrays.asList(values.get(1), values.get(2))));
            }
            
            List<TestResult> issueList = null;
            for (List<String> valuePair : valuePairs) {
                myPrint("value pair: v1 " + valuePair.get(0) + " v2 " + valuePair.get(1));
                issueList = testV1V2PairRestartPointWrapper(parameterToTest, componentFocused, valuePair.get(0), valuePair.get(1), testSet);  
                TestResult.writeIntoFile(issueList);
            }

	}
        
	endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        myPrint("Total execution time in seconds : " + timeElapsed / 1000000000);
        
        try {
            runLogWriter.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
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

            reader = new BufferedReader(new FileReader(new File(getRestartNameNodeTestFileName(paraType))));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                restartNameNodeTestList.add(buffer.trim());
            }
            reader.close();
            
	    reader = new BufferedReader(new FileReader(new File(getRestartDataNodeTestFileName(paraType))));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                restartDataNodeTestList.add(buffer.trim());
            }
            reader.close();
            
	    reader = new BufferedReader(new FileReader(new File(getRestartJournalNodeTestFileName(paraType))));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                restartJournalNodeTestList.add(buffer.trim());
            }
            reader.close();
            
            reader = new BufferedReader(new FileReader(new File(getStartNameNodeTestFileName(paraType))));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                startNameNodeTestList.add(buffer.trim());
            }
            reader.close();
            
	    reader = new BufferedReader(new FileReader(new File(getStartDataNodeTestFileName(paraType))));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                startDataNodeTestList.add(buffer.trim());
            }
            reader.close();
            
	    reader = new BufferedReader(new FileReader(new File(getStartJournalNodeTestFileName(paraType))));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                startJournalNodeTestList.add(buffer.trim());
            }
            reader.close();
	    
            reader = new BufferedReader(new FileReader(new File(beforeClassFileName)));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                beforeClassList.add(buffer.trim());
            }
            reader.close();
            
	    reader = new BufferedReader(new FileReader(new File(getStartAllComponentTestFileName(paraType))));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                startAllComponentTestList.add(buffer.trim());
            }
            reader.close();
	    
	    reader = new BufferedReader(new FileReader(new File(vanillaFailedTestFileName)));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                vanillaFailedTestList.add(buffer.trim());
            }
            reader.close();
	  
            Map<String, Map<String, List<String>>> intParameterValues = new HashMap<String, Map<String, List<String>>>();
            intParameterValues.put(intNameNodeParameterValuesFileName, intNameNodeParameterValues);
            intParameterValues.put(intDataNodeParameterValuesFileName, intDataNodeParameterValues);
            intParameterValues.put(intJournalNodeParameterValuesFileName, intJournalNodeParameterValues);

            for (String componentFileName : intParameterValues.keySet()) {
                System.out.println("loading componentFileName " + componentFileName);
                Map<String, List<String>> parameterValues = intParameterValues.get(componentFileName);
                reader = new BufferedReader(new FileReader(new File(componentFileName)));
                buffer = "";
                while ((buffer = reader.readLine()) != null) {
                    String[] contents = buffer.trim().split(" ");
                    if (contents.length != 4) {
                        System.out.println("error: " + contents[0]);
                        System.exit(1);
                    } 
                    String parameter = contents[0];
                    List<String> values = new ArrayList<String>();
                    for (int i=1; i<contents.length; i++)
                        values.add(contents[i]);
                    //System.out.println("parameter = " + parameter + " values = " + values);
                    parameterValues.put(parameter, values);
                }
                reader.close();
            }
            
            Map<String, Map<String, List<String>>> longParameterValues = new HashMap<String, Map<String, List<String>>>();
            longParameterValues.put(longNameNodeParameterValuesFileName, longNameNodeParameterValues);
            longParameterValues.put(longDataNodeParameterValuesFileName, longDataNodeParameterValues);
            longParameterValues.put(longJournalNodeParameterValuesFileName, longJournalNodeParameterValues);

            for (String componentFileName : longParameterValues.keySet()) {
                System.out.println("loading componentFileName " + componentFileName);
                Map<String, List<String>> parameterValues = longParameterValues.get(componentFileName);
                reader = new BufferedReader(new FileReader(new File(componentFileName)));
                buffer = "";
                while ((buffer = reader.readLine()) != null) {
                    String[] contents = buffer.trim().split(" ");
                    if (contents.length != 4) {
                        System.out.println("error: " + contents[0]);
                        System.exit(1);
                    } 
                    String parameter = contents[0];
                    List<String> values = new ArrayList<String>();
                    for (int i=1; i<contents.length; i++)
                        values.add(contents[i]);
                    //System.out.println("parameter = " + parameter + " values = " + values);
                    parameterValues.put(parameter, values);
                }
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
  
    public static void setupTestTuple(String parameter, String component, String mode, String v1, String v2, String componentHasStopped) {
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
            
            BufferedWriter writer7 = new BufferedWriter(new FileWriter(new File(currentBeforeClassTestNameFileName)));
            writer7.write("");
            writer7.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static <E> List<List<E>> generatePerm(List<E> original) {
        if (original.isEmpty()) {
            List<List<E>> result = new ArrayList<>(); 
            result.add(new ArrayList<>()); 
            return result; 
        }
        E firstElement = original.remove(0);
        List<List<E>> returnValue = new ArrayList<>();
        List<List<E>> permutations = generatePerm(original);
        for (List<E> smallerPermutated : permutations) {
            for (int index=0; index <= smallerPermutated.size(); index++) {
                List<E> temp = new ArrayList<>(smallerPermutated);
                temp.add(index, firstElement);
                returnValue.add(temp);
            }
        }
        return returnValue;
    }
}
