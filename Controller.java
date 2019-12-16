import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;

public class Controller {

    public static String workingDir = "/root/hadoop-3.1.2-src/hadoop-hdfs-project/hadoop-hdfs";
    public static String controllerRootDir = "/root/storage/parameter_test_controller/";
    
    /* shared files*/
    public static String testSuccessFileName = controllerRootDir + "shared/test_success";
    public static String parameterFileName = controllerRootDir + "shared/parameter";
    public static String reconfigModeFileName = controllerRootDir + "shared/reconfig_mode";
    public static String v1FileName = controllerRootDir + "shared/v1";
    public static String v2FileName = controllerRootDir + "shared/v2";
    
    /* static test and parameter per component */
    public static String testListFileName = controllerRootDir + "test_for_component/hdfs/namenode/test_of_solely_restart_namenode_success.txt";
    public static String parameterListFileName = controllerRootDir + "parameter_for_component/namenode_getBoolean.txt";
    
    public static List<String> testList = new ArrayList<String>();
    public static List<String> parameterList = new ArrayList<String>();
  
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
            System.out.println(e);
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
            System.out.println(e);
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
            System.out.println(e);
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
            //while ((buffer = reader.readLine()) != null)
            //    System.out.println(buffer);
            process.waitFor();
            ret = process.exitValue();
            process.destroy();
            reader.close();
            endTime = System.nanoTime();

            timeElapsed = endTime - startTime;
            System.out.println("Execution time of test " + method + " in seconds : " + timeElapsed / 1000000000);

        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
        return ret;
    }

    public static void load() {
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
            reader0.close();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        long startTime, endTime, timeElapsed;
    
        Controller.load();
        System.out.println("number of tests = " + testList.size());
        System.out.println("number of parameters = " + parameterList.size());
    
//        for (String parameter : parameterList) {
            startTime = endTime = timeElapsed = 0;
            startTime = System.nanoTime();
            int i = 1;
            for (String test : testList) {
                cleanUpSharedFiles();
                setupTestTuple("dfs.block.access.token.enable", "v1v1", "false", "false");
                System.out.println("Running the " + i + " of " + testList.size() + " test " 
                                    + test);
                int ret = runJunitTest(test);
                Integer res = waitForTestResult();
                System.out.println("Result of the " + i + " of " + testList.size() + " test " + test
                                    + " " + res);
                System.out.println();
                i++;
            }
            endTime = System.nanoTime();
            
            timeElapsed = endTime - startTime;
            System.out.println("Execution time of all " + testList.size() + " tests in seconds : " + timeElapsed / 1000000000);
//        }
    }
}
