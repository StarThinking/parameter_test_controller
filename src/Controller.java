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
    protected static int RECHECK_TIMES = 1;

    /* shared files */
    private static String testResultDirName = systemRootDir + "shared/test_results";
    private static String vvModeFileName = systemRootDir + "shared/reconf_vvmode";
    private static String hListFileName = systemRootDir + "shared/reconf_h_list";
    
    private static void updateTestResult(List<TestResult> testResultList) {
        String SEPERATOR = "@@@";
        try {
            File testResultDir = new File(testResultDirName);
            List<String> updatedTests = new ArrayList<String>();
            for (File f : testResultDir.listFiles()) {
                if (f.isFile()) {
                    //System.out.println("updating test result for " + f.getName());
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
                        System.out.println("ERROR: the content for " + f.getName() + " is wrong, length = " + contents.length);
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
                    //System.exit(-1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void runMvnCmd(TestResult tr) throws Exception {
        int exitCode = -1;
        //String systemLogSavingDir = "/root/reconf_test_gen/target";
        String systemLogSavingDir = "none";
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("/root/reconf_test_gen/run_mvn_test.sh", tr.proj, tr.u_test, systemLogSavingDir);
        Process process = builder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
    	    ;
    	    //System.out.println(line);
        }
        reader.close();
        if(!process.waitFor(1200, TimeUnit.SECONDS)) { // timeout - kill the process.
    	    System.out.println("WARN: wait process timeout!");
		process.destroy(); // consider using destroyForcibly instead
        }
        exitCode = process.exitValue();
       
        List<TestResult> testResultList = new ArrayList<TestResult>();
        testResultList.add(tr);
        updateTestResult(testResultList);

        // override result with cmd exit code
        if ((exitCode == 0 && tr.result.equals("-1")) || (exitCode != 0 && tr.result.equals("1"))) {
            System.out.println("WARN: conflict exitCode = " + exitCode + " but tr.result = " + tr.result);
        }
       
        // update test result
        if (exitCode == 0)
    	    tr.result = "1";
        else
    	    tr.result = "-1";
    }

    // update directly at TestResult tr
    public static void testCore(String vvMode, TestResult tr) {
        try {
            // clean before and after each test
            cleanUpSharedFiles();
            setupTestTuple(vvMode, tr);
            runMvnCmd(tr);
            System.out.println("tr.result is " + tr.result);
            cleanUpSharedFiles();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
 
    private static void setupTestTuple(String vvMode, TestResult tr) throws Exception {
	if (!vvMode.equals("v1v1") && !vvMode.equals("v2v2") && !vvMode.equals("v1v2") && 
                !vvMode.equals("none")) {
	    System.out.println("ERROR, wrong mode " + vvMode);
	    System.exit(1);
	} else {
            BufferedWriter writer = null;
            writer = new BufferedWriter(new FileWriter(new File(hListFileName)));
            writer.write(tr.h_list);
            writer.close();
            
            writer = new BufferedWriter(new FileWriter(new File(vvModeFileName)));
            writer.write(vvMode);
            writer.close();
        }
    }

    private static void cleanUpSharedFiles() throws Exception {
       File testResultDir = new File(testResultDirName);
       for (File testResult : testResultDir.listFiles()) {
           if (testResult.isFile()) {
               testResult.delete();
           }
       }
       TestResult empty = new TestResult("", "", "");
       setupTestTuple("none", empty);
    }
}
