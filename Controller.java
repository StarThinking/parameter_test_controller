import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;

public class Controller {

    //public static String workingDir = "/root/hadoop-3.1.2-src/hadoop-hdfs-project/hadoop-hdfs";
    public static String workingDir = "/root/hadoop-3.1.2-src-reconf/hadoop-hdfs-project";
    public static String controllerRootDir = "/root/parameter_test_controller/";
    
    /* shared files*/
    public static String testSuccessFileName = controllerRootDir + "shared/test_success";
    public static String failureMessageFileName = controllerRootDir + "shared/failure_message";
    public static String parameterFileName = controllerRootDir + "shared/parameter";
    public static String reconfigModeFileName = controllerRootDir + "shared/reconfig_mode";
    public static String reconfigComponentFileName = controllerRootDir + "shared/reconfig_component";
    public static String v1FileName = controllerRootDir + "shared/v1";
    public static String v2FileName = controllerRootDir + "shared/v2";
    
    /* static test and parameter per component */
    //public static String allTestListFileName = controllerRootDir + "test_for_component/hdfs/namenode/test_of_solely_restart_namenode_success.txt";
    public static String testForNameNodeFileName = controllerRootDir + "test_for_component/hdfs/restart_namenode.txt";
    public static String testForDataNodeFileName = controllerRootDir + "test_for_component/hdfs/restart_datanode.txt";
    public static String testForJournalNodeFileName = controllerRootDir + "test_for_component/hdfs/restart_journalnode.txt";
    public static List<String> nameNodeTestList = new ArrayList<String>();
    public static List<String> dataNodeTestList = new ArrayList<String>();
    public static List<String> journalNodeTestList = new ArrayList<String>();
    
    public static String parameterListFileName = controllerRootDir + "parameter_for_component/namenode_getBoolean.txt";
    
    public static List<String> parameterList = new ArrayList<String>(); // never used
    
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
            /*
            BufferedReader reader1 = new BufferedReader(new FileReader(new File(parameterListFileName)));
            String buffer1 = "";
            while ((buffer1 = reader1.readLine()) != null) {
                parameterList.add(buffer1.trim());
            }
            reader1.close();*/
            //System.out.println("number of parameters = " + parameterList.size());
        } catch (Exception e) {
            //System.out.println(e);
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
            //System.out.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void cleanUpSharedFiles() {
        try {
            BufferedWriter writer0 = new BufferedWriter(new FileWriter(new File(testSuccessFileName)));
            writer0.write("");
            writer0.close();
            
            BufferedWriter writer1 = new BufferedWriter(new FileWriter(new File(failureMessageFileName)));
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
            //System.out.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static Integer waitForTestResult() {
        Integer res = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(testSuccessFileName)));
            String buffer = "";
            buffer = reader.readLine();
	    int tried = 0;
	    while (buffer == null && tried < 3) {
		System.out.println("testSuccess is empty, let us sleep 2s and try again");
		Thread.sleep(2000);
		tried ++;
	        buffer = reader.readLine();
	    }
            res = Integer.valueOf(buffer);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println(e);
            System.exit(1);
        }
        return res;
    }

    public static int runJunitTest(String method) {
        int res = 0;
        try {
	    String buffer = "";
            String cmd = "mvn test -Dtest=" + method;
            long startTime, endTime, timeElapsed;
            startTime = endTime = timeElapsed = 0;
            
            startTime = System.nanoTime();
            Process process = Runtime.getRuntime().exec(cmd, null, new File(workingDir));
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
//            while ((buffer = reader.readLine()) != null)
//                System.out.println(buffer);
            reader.close();
            process.waitFor();
            int ret = process.exitValue();
	    res = waitForTestResult();
            process.destroy();
            endTime = System.nanoTime();

            timeElapsed = endTime - startTime;
            System.out.print(" Execution time (sec) " + timeElapsed / 1000000000);

        } catch (Exception e) {
            //System.out.println(e);
            e.printStackTrace();
            System.exit(1);
        }
        return res;
    }

    public static List<String> testForTupleWithGivenTests(String parameter, String component, String reconfigMode, String v1, String v2, List<String> thisTestSet) {
        List<String> failedList = new ArrayList<String>();
        if (thisTestSet == null) {
            System.exit(1);
        }

        if (thisTestSet.size() == 0)
            return failedList; // empty
        System.out.println("------------------------------------------------------" +
			   "------------------------------------------------------");
        System.out.println("reconfigMode=" + reconfigMode + " v1=" + v1 + " v2=" + v2); 
	System.out.println("thisTestSet size " + thisTestSet.size());
        int index = 1;
        for (String test : thisTestSet) {
            cleanUpSharedFiles();
            setupTestTuple(parameter, component, reconfigMode, v1, v2);
            System.out.print("Running " + index + " of " + thisTestSet.size() + " test " + test);
            int res = runJunitTest(test);
            //int res = waitForTestResult();
            System.out.print(" Result " +  res + "\n");
            if (res < 0) { 
                failedList.add(test);
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(new File(failureMessageFileName)));
                    String buffer = "";
                    while ((buffer = reader.readLine()) != null) {
                        System.out.println(buffer);
                    }
                    reader.close();
                } catch(Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
            index++;
        }
        System.out.println("------------------------------------------------------" +
			   "------------------------------------------------------");
        return failedList;
    }

    public static void testCorrectness(String parameter, String component, String v1, String v2, List<String> testSet) {
	List<String> failedListv1v2 = testForTupleWithGivenTests(parameter, component, "none", "", "", testSet); // all
    }
      
    public static int testLogic(String parameter, String component, String v1, String v2, List<String> testSet) {    
        List<String> failedListv1v2 = testForTupleWithGivenTests(parameter, component, "v1v2", v1, v2, testSet); // all
        List<String> failedListv1v1 = testForTupleWithGivenTests(parameter, component, "v1v1", v1, "", failedListv1v2); 
        List<String> failedListv2v2 = testForTupleWithGivenTests(parameter, component, "v2v2", "", v2, failedListv1v2);
        int unpartial = 0;
	int numOfSuspicous = 0;
	int thresholdOfSuspicous = 10;

        for (String v1v2Test : failedListv1v2) {
            boolean v1v1Failed, v2v2Failed;
            v1v1Failed = v2v2Failed = false;
            for (String v1v1Test : failedListv1v1) {
                /* found */
                if (v1v2Test.equals(v1v1Test)) {
                    v1v1Failed = true;
                    break;
                }
            }
            for (String v2v2Test : failedListv2v2) {
                /* found */
                if (v1v2Test.equals(v2v2Test)) {
                    v2v2Failed = true;
                    break;
                }
            }
            if (v1v1Failed == false && v2v2Failed == false) {
                System.out.println("UNPARTIAL suspicious " + v1v2Test + " for parameter " + parameter + " v1 " + v1 + " v2 " + v2);
		numOfSuspicous ++;
                // double check
                int runs = 10;
                int i = 0;
                List<String> singleTest = new ArrayList<String>();
                singleTest.add(v1v2Test);
                for(; i<runs; i++) {
                    List<String> failedList1 = testForTupleWithGivenTests(parameter, component, "v1v1", v1, "", singleTest);
                    List<String> failedList2 = testForTupleWithGivenTests(parameter, component, "v2v2", "", v2, singleTest);
                    if (failedList1.size() > 0 || failedList2.size() > 0) {
                        System.out.println("UNPARTIAL false positive " + v1v2Test + " for parameter " + parameter + " v1 " + v1 + " v2 " + v2);
                        break;
                    }
                }
                if (numOfSuspicous > thresholdOfSuspicous) {
		    System.out.println("numOfSuspicous " + numOfSuspicous + " > thresholdOfSuspicous " + thresholdOfSuspicous);
                    System.out.println("UNPARTIAL foresure " + v1v2Test + " for parameter " + parameter + " v1 " + v1 + " v2 " + v2);
        	    unpartial = 1;
		    return unpartial;
                }
		
                if (i == runs) {
                    System.out.println("UNPARTIAL foresure " + v1v2Test + " for parameter " + parameter + " v1 " + v1 + " v2 " + v2);
        	    unpartial = 1;
                }
            }
       	}
	return unpartial;
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
        
	startTime = System.nanoTime();
//	testCorrectness(parameterToTest, componentFocused, "", "", testSet);
	int ret = testLogic(parameterToTest, componentFocused, "true", "false", testSet);
	if (ret == 0)
	    testLogic(parameterToTest, componentFocused, "false", "true", testSet);
        endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        System.out.println("Total execution time in seconds : " + timeElapsed / 1000000000);
        System.out.println();
    }
}
