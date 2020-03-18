import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;
import java.text.SimpleDateFormat;

public class ReconfTester extends Controller {

    private static List<String> allStartTestList = new ArrayList<String>();
    private static Map<Integer, List<String>> restartTestMap = new HashMap<Integer, List<String>>();
    private static List<String> multiInstanceTestList = new ArrayList<String>();

    public static final String RP_MODE_INSTANCE = "-1"; // set v2 to a single instance of the specified component throughout a test
    public static final String RP_MODE_COMPONENT = "-2"; // set v2 to all instances for the specified component
    public static final String RP_MODE_RECONF = "1"; // set v2 to a single life cycle of a single instance of the specified component

    public static void reconfTryerLogic(String parameter, String component, String v1, String v2, String reconfPoint) {    
        Controller.RECHECK_RUNTIMES=2;

        // associate testset for different the reconfPoint
        // do RP_MODE_INSTANCE reconf for allStartTest
        // do RP_MODE_COMPONENT reconf for multiInstanceTest
        // do RP_MODE_RECONF reconf for restartTest
        List<String> testSet = null;
        if (reconfPoint.equals(RP_MODE_COMPONENT)) {
            testSet = multiInstanceTestList;
        } else if (reconfPoint.equals(RP_MODE_INSTANCE)) {
            testSet = allStartTestList;
        }
        List<TestResult> issueList = testLogic(parameter, component, v1, v2, reconfPoint, testSet);
        myPrint("---------------------------------------short report---------------------------------------------");
        myPrint("issueList size " + issueList.size() + " :");
        for (TestResult t : issueList)
            myPrint(t.toString());
        myPrint("---------------------------------------short report---------------------------------------------");
    }

    public static void loadReconfTesterStaticData(String component) {
	try {
            BufferedReader reader = null;
            String buffer = "";
            String multiInstanceTestFileName = systemRootDir + "reconfTester_static_data/hdfs/" +
                component + "/multi_instance.txt";
            String allStartTestFileName = systemRootDir + "reconfTester_static_data/hdfs/" +
                component + "/start.txt";
           
            // instance
	    reader = new BufferedReader(new FileReader(new File(multiInstanceTestFileName)));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                multiInstanceTestList.add(buffer.trim());
            }
            System.out.println("multiInstanceTestList for " + component + " has been loaded, size is " + multiInstanceTestList.size());
            reader.close();

            // start
	    reader = new BufferedReader(new FileReader(new File(allStartTestFileName)));
            buffer = "";
            while ((buffer = reader.readLine()) != null) {
                allStartTestList.add(buffer.trim());
            }
            System.out.println("allStartTestList for " + component + " has been loaded, size is " + allStartTestList.size());
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
	
        if (!reconfPoint.equals(RP_MODE_INSTANCE) && !reconfPoint.equals(RP_MODE_COMPONENT) && !reconfPoint.equals(RP_MODE_RECONF)) {
	    myPrint("Error: wrong reconfPoint " + reconfPoint);
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
        
        reconfTryerLogic(parameter, component, v1, v2, reconfPoint);
        
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
