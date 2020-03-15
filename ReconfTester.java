import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;
import java.text.SimpleDateFormat;

public class ReconfTester extends Controller {

    private static List<String> startTestList = new ArrayList<String>();
    private static Map<Integer, List<String>> restartTestMap = new HashMap<Integer, List<String>>();
    private static List<String> instanceTestList = new ArrayList<String>();
    
    public static void reconfTryerLogic(String parameter, String component, String v1, String v2, String reconfPoint) {    
        /*List<String> restartTestSet = null;
        List<String> startTestSet = null;

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
        Controller.RECHECK_RUNTIMES=5;
        List<TestResult> issueList = testLogic(parameter, component, v1, v2, reconfPoint, startTestSet);
        myPrint("---------------------------------------short report---------------------------------------------");
        myPrint("issueList size " + issueList.size() + " :");
        for (TestResult t : issueList)
            myPrint(t.toString());
        myPrint("---------------------------------------short report---------------------------------------------");
    */
    }

    public static void loadReconfTesterStaticData(String component) {
	try {
            BufferedReader reader = null;
            String buffer = "";
    
            String instanceTestFileName = systemRootDir + "reconfTester_static_data/hdfs/" +
                component + "/instance.txt";
            reader = new BufferedReader(new FileReader(new File(instanceTestFileName)));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                instanceTestList.add(buffer.trim());
            }
            System.out.println("instanceTestList has been loaded, size is " + instanceTestList.size());
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

    public static void main(String[] args) {
        String parameter = "";
        String component = "";
        String v1 = "";
        String v2 = "";
        String reconfPoint = "";
        final int parameterNum = 5;
        long startTime, endTime, timeElapsed;
        startTime = endTime = timeElapsed = 0; 

        if (args.length != parameterNum) {
            myPrint("Error: args length is " + args.length);
            myPrint("ReconfTryer parameter component v1 v2 reconfPoint");
            System.exit(1);
        }
       
	parameter = args[0];
	component = args[1];
        v1=args[2];
        v2=args[3];
        reconfPoint=args[4];

	if (!component.equals("NameNode") && !component.equals("DataNode") && !component.equals("JournalNode")) {
	    myPrint("Error: wrong component " + component);
	    System.exit(1);
	}

        loadReconfTesterStaticData(component);
	
        /* set run log */
        Date date = new Date();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
	String dateTime = formatter.format(date);
        String runLogPath = Controller.systemRootDir + parameter + "%" + component + "%" + v1 +
            "%" + v2 + "%" + reconfPoint + "_run_" + dateTime + ".txt";
        try {
            runLogWriter = new BufferedWriter(new FileWriter(new File(runLogPath), true)); 
        } catch(Exception e) {
            e.printStackTrace();
        }
	
	myPrint("parameter: " + parameter); 
        myPrint("component: " + component); 
        myPrint("v1: " + v1);
        myPrint("v2: " + v2);
        myPrint("reconfPoint: " + reconfPoint);

	startTime = System.nanoTime();
        reconfTryerLogic( parameter, component, v1, v2, reconfPoint);
        endTime = System.nanoTime();
        timeElapsed = endTime - startTime;
        myPrint("Total execution time in seconds : " + timeElapsed / 1000000000);
        
        try {
            runLogWriter.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
