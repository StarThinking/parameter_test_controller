import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;
import java.text.SimpleDateFormat;

public class Controller {

    private static String workingDir = "/root/hadoop-3.1.2-src/hadoop-hdfs-project";
    //private static String workingDir = "/root/hadoop-3.1.2-src";
    protected static String systemRootDir = "/root/parameter_test_controller/";
    protected static BufferedWriter runLogWriter = null;
    protected static int RECHECK_RUNTIMES = 5;
    protected static int ISSUE_NUM_THRESHOLD = 50;

    /* shared files */
    private static String testResultDirName = systemRootDir + "shared/test_results";
    private static String vvModeFileName = systemRootDir + "shared/reconf_vvmode";
    private static String parameterFileName = systemRootDir + "shared/reconf_parameter";
    private static String componentFileName = systemRootDir + "shared/reconf_component";
    private static String v1FileName = systemRootDir + "shared/reconf_v1";
    private static String v2FileName = systemRootDir + "shared/reconf_v2";
    private static String reconfPointFileName = systemRootDir + "shared/reconf_point";
   
    /* static test information */
    private static String beforeClassFileName = systemRootDir + "controller_static_data/hdfs/before_class.txt";
    private static List<String> beforeClassList = new ArrayList<String>();
    
    /* failed vanilla unit tests */
    private static List<String> vanillaFailedTestList = new ArrayList<String>();
    //private static String vanillaFailedTestFileName = systemRootDir + "test_for_component/hdfs/vanilla_failed_test.txt";
   
    static {
        loadControllerStaticData();
    }

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

    private static List<TestGroup> groupBeforeClasssTests(List<String> tests) {
        List<TestGroup> groupList = new ArrayList<TestGroup>();

        for (String perTest : tests) {
            boolean findOkGroup = false;
            TestGroup okGroup = null;
            String[] result = perTest.split("#");
            String perTestClass = result[0];
            for (TestGroup group : groupList) {
                if (group.findSameClassInGroup(perTestClass) == false) {
                    findOkGroup = true;
                    okGroup = group;
                    break;
                }
            }
            if (findOkGroup) {
                okGroup.addIntoTestList(perTest);
                okGroup.addIntoClassNameList(perTestClass);
            } else {
                TestGroup newGroup = new TestGroup();
                newGroup.addIntoTestList(perTest);
                newGroup.addIntoClassNameList(perTestClass);
                groupList.add(newGroup);
            }
        }

        myPrint("size of groupList =  " + groupList.size());
        //for (TestGroup g : groupList) {
        //    myPrint(g.toString());
        //}
        return groupList;
    } 
    
    private static void runMvnCmd(List<TestResult> testResultList) {
        final int COMBINE_NUM = 200;
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

	    if (nonBeforeClassTests.size() > 0) {   
                runCombinedMvnCmd(nonBeforeClassTests, COMBINE_NUM);
            }

            if (beforeClassTests.size() > 0) {
                List<TestGroup> groupList = groupBeforeClasssTests(beforeClassTests);
                for (TestGroup g : groupList)
		    runCombinedMvnCmd(g.getTests(), COMBINE_NUM);
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

    public static List<TestResult> testCore(String parameter, String component, String vvMode, String v1, String v2,
            String reconfPoint, List<String> thisTestSet) {
        List<TestResult> failedTests = new ArrayList<TestResult>();
        if (thisTestSet == null) {
            System.exit(1);
        }

        if (thisTestSet.size() == 0)
            return failedTests; // empty
        
        myPrint("\nTest vvMode=" + vvMode); 
        List<TestResult> testResultList = new ArrayList<TestResult>();

	//myPrint("thisTestSet before removing vanilla failure: " + thisTestSet.size());
	// remove vanilla failed tests
        //myPrint("# of vanilla failed tests is " + vanillaFailedTestList.size());
	thisTestSet.removeIf(test -> vanillaFailedTestList.contains(test));
	myPrint("thisTestSet after filter out vanilla failure: " + thisTestSet.size());
 
	// construct TestResult list
        for (String test : thisTestSet) {
            testResultList.add(new TestResult(test, parameter, component, v1, v2, reconfPoint));
        }
        cleanUpSharedFiles();
        setupTestTuple(vvMode, parameter, component, v1, v2, reconfPoint);
        runMvnCmd(testResultList);
        
        for (TestResult t : testResultList) {
            if (Integer.valueOf(t.result) < 0)
                failedTests.add(t);
        }
        return failedTests;
    }
      
    public static List<TestResult> testLogic(String parameter, String component, String v1, String v2, String reconfPoint, List<String> testSet) {    
        List<TestResult> failedListv1v2 = testCore(parameter, component, "v1v2", v1, v2, reconfPoint, testSet); // all
        myPrint("failed v1v2 list size " + failedListv1v2.size());
        for (TestResult t : failedListv1v2)
            myPrint(t.testName);
      
        List<TestResult> issueList = new ArrayList<TestResult>();
        int runs = Controller.RECHECK_RUNTIMES;
        myPrint("do " + runs + " v1v1 v2v2 tests to filter false alarm");
        for (TestResult t : failedListv1v2) {
            myPrint("failed v1v2 test: " + t.testName + " v1 " + v1 + " v2 " + v2);
            int i = 0;
            List<String> singleTest = new ArrayList<String>();
            singleTest.add(t.testName);
            for(; i<runs; i++) {
                List<TestResult> failedList1 = testCore(parameter, component, "v1v1", v1, "", reconfPoint, singleTest);
                List<TestResult> failedList2 = testCore(parameter, component, "v2v2", "", v2, reconfPoint, singleTest);
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
            if (issueList.size() >= ISSUE_NUM_THRESHOLD)
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
    
    public static void loadControllerStaticData() {
        try {
            BufferedReader reader = null;
            String buffer = "";

            reader = new BufferedReader(new FileReader(new File(beforeClassFileName)));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                beforeClassList.add(buffer.trim());
            }
            System.out.println("beforeClassList has been loaded, size is " + beforeClassList.size());
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
  
    private static void setupTestTuple(String vvMode, String parameter, String component, String v1, String v2, String reconfPoint) {
	if (!vvMode.equals("v1v1") && !vvMode.equals("v2v2") && !vvMode.equals("v1v2") && !vvMode.equals("none")) {
	    myPrint("Error, wrong mode " + vvMode);
	    System.exit(1);
	}
        try {
            BufferedWriter writer1 = new BufferedWriter(new FileWriter(new File(parameterFileName)));
            writer1.write(parameter);
            writer1.close();
            
	    BufferedWriter writer2 = new BufferedWriter(new FileWriter(new File(componentFileName)));
            writer2.write(component);
            writer2.close();
            
            BufferedWriter writer3 = new BufferedWriter(new FileWriter(new File(vvModeFileName)));
            writer3.write(vvMode);
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

    private static void cleanUpSharedFiles() {
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
    
    public static class TestGroup {
        private List<String> classNameList = new ArrayList<String>();
        private List<String> testList = new ArrayList<String>();

        public void addIntoTestList(String t) {
            testList.add(t);
        }
        
        public void addIntoClassNameList(String c) {
            classNameList.add(c);
        }

        public List<String> getTests() {
            return testList;
        }

        public boolean findSameClassInGroup(String perTestClass) {
            for (String c : classNameList) {
                if (perTestClass.equals(c)) {
                    return true;
                }
            }
            return false;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("classNameList:\n");
            for (String c : classNameList) {
                sb.append(c + "\n");
            }
            sb.append("testList:\n");
            for (String t : testList) {
                sb.append(t + "\n");
            }
            return sb.toString();
        }
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
}
