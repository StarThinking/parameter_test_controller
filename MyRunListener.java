import org.junit.runner.notification.*;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MyRunListener extends RunListener {

    public static int testNum = 0;
    public static int classNum = 0;
    public static String controllerRootDir = "/root/parameter_test_controller/";
    public static String sharedFileName = controllerRootDir + "shared/test_success";
    public static String res = "";

    public MyRunListener() {
        System.out.println("[msx] Creation of Run Listener...");
    }

    // Called before any tests have been run.
    public void testRunStarted(Description description) throws java.lang.Exception {
        System.out.println("[msx] testRunStarted " + description.testCount());
	classNum ++;
    }
 
    // Called when all tests have finished
    public void testRunFinished(Result result) throws java.lang.Exception {
        System.out.println("[msx] testRunFinished " + result.getRunCount() + " testNum = " + 
				testNum + " classNum = " + classNum);
    }
 
    // Called when an atomic test is about to be started.
    public void testStarted(Description description) throws java.lang.Exception {
        System.out.println("[msx] testStarted " + description.getMethodName());
	testNum ++;
        // clean up
        res = "1";
    }
 
    // Called when an atomic test has finished, whether the test succeeds or
    // fails.
    public void testFinished(Description description) throws java.lang.Exception {
        System.out.println("[msx] testFinished " + description.getMethodName() + " testNum = " + 
                                testNum + " classNum = " + classNum);
        System.out.println("res0 = " + res);
        BufferedWriter writer = new BufferedWriter(new FileWriter(sharedFileName));
        System.out.println("res = " + res);
        writer.write(res);
        writer.close();
    }
 
    // Called when an atomic test fails.
    public void testFailure(Failure failure) throws java.lang.Exception {
        System.out.println("[msx] testFailure " + failure.getException());
        //BufferedWriter writer = new BufferedWriter(new FileWriter(sharedFileName));
        res = "-1";
        //writer.write("-1");
        //writer.close();
    }
 
    // Called when a test will not be run, generally because a test method is
    // annotated with Ignore.
    public void testIgnored(Description description) throws java.lang.Exception {
        System.out.println("[msx] testIgnored " + description.getMethodName());
        //BufferedWriter writer = new BufferedWriter(new FileWriter(sharedFileName));
        //writer.write("");
        //writer.close();
        res = "0";
    }
     
    // Called when an atomic test flags that it assumes a condition that is false
    public void testAssumptionFailure(Failure failure){
        System.out.println("[msx] testAssumptionFailure " + failure.getMessage());
        //BufferedWriter writer = new BufferedWriter(new FileWriter(sharedFileName));
        //writer.write("-2");
        //writer.close();
        res = "-2";
    }
}
