import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;

public class Controller {

    public static String workingDir = "/root/hadoop-3.1.2-src/hadoop-hdfs-project/hadoop-hdfs";
    public static String controllerRootDir = "/root/parameter_test_controller/";
    
    /* shared files*/
    public static String testSuccessFileName = controllerRootDir + "shared/test_success";
    public static String parameterFileName = controllerRootDir + "shared/parameter";
    public static String reconfigModeFileName = controllerRootDir + "shared/reconfig_mode";
    public static String v1FileName = controllerRootDir + "shared/v1";
    public static String v2FileName = controllerRootDir + "shared/v2";
    
    /* static test and parameter per component */
    public static String testListFileName = controllerRootDir + "test_for_component/hdfs/namenode/test_of_solely_restart_namenode_success.txt";
    public static String parameterListFileName = controllerRootDir + "parameter_for_component/namenode_getBoolean.txt";
    
    public static List<String> parameterList = new ArrayList<String>(); // never used
    public static List<String> testList = new ArrayList<String>();
  
    public static void setupTestTuple(String parameter, String reconfigMode, String v1, String v2) {
        try {
            BufferedWriter writer1 = new BufferedWriter(new FileWriter(new File(parameterFileName)));
            writer1.write(parameter);
            writer1.close();
            
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(new File(reconfigModeFileName)));
            writer2.write(reconfigMode);
            writer2.close();
            
            BufferedWriter writer3 = new BufferedWriter(new FileWriter(new File(v1FileName)));
            writer3.write(v1);
            writer3.close();
            
            BufferedWriter writer4 = new BufferedWriter(new FileWriter(new File(v2FileName)));
            writer4.write(v2);
            writer4.close();
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
            
            BufferedWriter writer1 = new BufferedWriter(new FileWriter(new File(parameterFileName)));
            writer1.write("");
            writer1.close();
            
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(new File(reconfigModeFileName)));
            writer2.write("");
            writer2.close();
            
            BufferedWriter writer3 = new BufferedWriter(new FileWriter(new File(v1FileName)));
            writer3.write("");
            writer3.close();
            
            BufferedWriter writer4 = new BufferedWriter(new FileWriter(new File(v2FileName)));
            writer4.write("");
            writer4.close();
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
        int ret = 0;
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
            ret = process.exitValue();
            process.destroy();
            endTime = System.nanoTime();

            timeElapsed = endTime - startTime;
            System.out.println("Execution time in seconds : " + timeElapsed / 1000000000);

        } catch (Exception e) {
            //System.out.println(e);
            e.printStackTrace();
            System.exit(1);
        }
        return ret;
    }

    public static void loadStaticData() {
        try {
            BufferedReader reader0 = new BufferedReader(new FileReader(new File(testListFileName)));
            String buffer0 = "";
            while ((buffer0 = reader0.readLine()) != null) {
                testList.add(buffer0.trim());
            }
            reader0.close();
            
            BufferedReader reader1 = new BufferedReader(new FileReader(new File(parameterListFileName)));
            String buffer1 = "";
            while ((buffer1 = reader1.readLine()) != null) {
                parameterList.add(buffer1.trim());
            }
            //System.out.println("number of parameters = " + parameterList.size());
            reader0.close();
        } catch (Exception e) {
            //System.out.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static List<String> testForTupleWithGivenTests(String parameter, String reconfigMode, String v1, String v2, List<String> thisTestList) {
        List<String> failedList = new ArrayList<String>();
        if (thisTestList == null) {
            System.exit(1);
        }
        
        System.out.println("reconfigMode=" + reconfigMode + " v1=" + v1 + " v2=" + v2); 

        int index = 1;
        for (String test : testList) {
            cleanUpSharedFiles();
            setupTestTuple(parameter, reconfigMode, v1, v2);
            System.out.println("Running " + index + " of " + testList.size() + " test " 
                                + test);
            int ret = runJunitTest(test);
            Integer res = waitForTestResult();
            if (res < 0) 
                failedList.add(test);
            System.out.println("Result: " +  res);
            System.out.println();
            index++;
        }
        return failedList;
    }
    
    public static void main(String[] args) {
        long startTime, endTime, timeElapsed;
        startTime = endTime = timeElapsed = 0; 
        
        if (args.length > 1) {
            System.exit(1);
        }
        String parameterToTest = args[0];
        System.out.println("parameter to test: " + parameterToTest); 
        loadStaticData();
        startTime = System.nanoTime();
        List<String> failedListv1v2 = testForTupleWithGivenTests(parameterToTest, "v1v2", "true", "false", testList); // all
        List<String> failedListv1v1 = testForTupleWithGivenTests(parameterToTest, "v1v1", "true", "", failedListv1v2); 
        List<String> failedListv2v2 = testForTupleWithGivenTests(parameterToTest, "v2v2", "", "false", failedListv1v2);

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
                System.out.println("UNPARTIAL " + v1v2Test);
            }
        }

        endTime = System.nanoTime();
        
        timeElapsed = endTime - startTime;
        System.out.println("Total execution time " + testList.size() + " in seconds : " + timeElapsed / 1000000000);
    }
}
