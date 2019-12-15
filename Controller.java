import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;

public class Controller {

    public static String workingDir = "/root/hadoop-3.1.2-src/hadoop-hdfs-project/hadoop-hdfs";
    public static String sharedFileName = "/root/storage/parameter_test_controller/shared/test_success";
    public static List<String> testList = new ArrayList<String>();
   
    public static void cleanUpTestResult() {
        try {
            File testResultFile = new File(sharedFileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(testResultFile));
            writer.write("");
            writer.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static Integer waitForTestResult() {
        Integer res = 0;
        try {
            File testResultFile = new File(sharedFileName);
            BufferedReader reader = new BufferedReader(new FileReader(testResultFile));
            String buffer = "";
            buffer = reader.readLine();
            //System.out.println("buffer = " + buffer);
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
            Process process = Runtime.getRuntime().exec(cmd, null, new File(workingDir));
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
//            while ((buffer = reader.readLine()) != null)
//                System.out.println(buffer);
            process.waitFor();
            //System.out.println ("exit: " + p.exitValue());
            ret = process.exitValue();
            process.destroy();
            reader.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return ret;
    }

    public static void load() {
        try {
            String fileName = "/root/storage/parameter_test_controller/test_for_component/hdfs/namenode/solely_namenode_restart.txt";
            File file = new File(fileName);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String buffer = "";
            while ((buffer = reader.readLine()) != null) {
                //System.out.println(buffer);
                Controller.testList.add(buffer.trim());
            }
            reader.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        Controller.load();
        System.out.println("size of testList = " + testList.size());
        int i = 1;
        for (String test : testList) {
            cleanUpTestResult();
            System.out.println("Running the " + i + " of " + testList.size() + " test: " 
                                + test);
            int ret = runJunitTest(test);
            Integer res = waitForTestResult();
            System.out.println("Result of the " + i + " of " + testList.size() + " test: " 
                                + res);
            System.out.println();
            i++;
        }
    }
}
