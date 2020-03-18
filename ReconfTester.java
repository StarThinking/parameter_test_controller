import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;
import java.text.SimpleDateFormat;

public class ReconfTester extends Controller {

    private static boolean ENABLE_COMMON = true;
    private static List<String> allStartTestList = new ArrayList<String>();
    private static Map<Integer, List<String>> restartTestMap = new TreeMap<Integer, List<String>>();
    private static List<String> restartTestList = new ArrayList<String>();
    private static List<String> multiInstanceTestList = new ArrayList<String>();

    public static final int RP_MODE_INSTANCE = -1; // set v2 to a single instance of the specified component throughout a test
    public static final int RP_MODE_COMPONENT = -2; // set v2 to all instances for the specified component
    //public static final String RP_MODE_RECONF = "N"; // set v2 to a single life cycle of a single instance of the specified component

    private static void enableCommonTest(List<String> testSet) {
        List<String> commonTestList = new ArrayList<String>();
        String commonTestFileName = systemRootDir + "reconfTester_static_data/hdfs/common.txt";
        BufferedReader reader = null;
        String buffer = "";
        try {
            reader = new BufferedReader(new FileReader(new File(commonTestFileName)));
            while ((buffer = reader.readLine()) != null) {
                commonTestList.add(buffer.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        myPrint("commonTestList has been loaded, size is " + commonTestList.size());
        myPrint("testSet size before finding common is " + testSet.size());
        Iterator<String> itr = testSet.iterator(); 
        while (itr.hasNext()) { 
            String test = itr.next(); 
            if (!commonTestList.contains(test)) { 
                itr.remove(); 
            } 
        }
        myPrint("testSet size after finding common is " + testSet.size());
    }

    public static void reconfTryerLogic(String parameter, String component, String v1, String v2, String reconfPoint) {    
        Controller.RECHECK_RUNTIMES=2;

        // associate testset for different the reconfPoint
        // do RP_MODE_INSTANCE reconf for allStartTest; RP_MODE_COMPONENT for multiInstanceTest; RP_MODE_RECONF for restartTest
        List<String> testSet = null;
        int reconfPointInt = Integer.valueOf(reconfPoint);
        if (reconfPointInt == RP_MODE_COMPONENT) {
            testSet = multiInstanceTestList;
        } else if (reconfPointInt == RP_MODE_INSTANCE) { 
            testSet = allStartTestList;
        } else {
            testSet = restartTestList;
        }
        if (ENABLE_COMMON == true)
            enableCommonTest(testSet);
        List<TestResult> issueList = testLogic(parameter, component, v1, v2, reconfPoint, testSet);
        if (issueList.size() > 0) {
            myPrint("---------------------------------------short report---------------------------------------------");
            myPrint("issueList size " + issueList.size() + " :");
            for (TestResult t : issueList)
                myPrint(t.toString());
            myPrint("---------------------------------------short report---------------------------------------------");
        }
    }

    public static void loadReconfTesterStaticData(String component, String reconfPoint) {
	try {
            int reconfPointInt = Integer.valueOf(reconfPoint);
            BufferedReader reader = null;
            String buffer = "";
            String multiInstanceTestFileName = systemRootDir + "reconfTester_static_data/hdfs/" +
                component + "/multi_instance.txt";
            String allStartTestFileName = systemRootDir + "reconfTester_static_data/hdfs/" +
                component + "/start.txt";
            String restartTestFileName = systemRootDir + "reconfTester_static_data/hdfs/" +
                component + "/restart.txt";

            if (reconfPointInt == RP_MODE_COMPONENT) {
	         reader = new BufferedReader(new FileReader(new File(multiInstanceTestFileName)));
                 buffer = "";
                 while ((buffer = reader.readLine()) != null) {
                     multiInstanceTestList.add(buffer.trim());
                 }
                 myPrint("multiInstanceTestList for " + component + " has been loaded, size is " + multiInstanceTestList.size());
                 reader.close();
            } else if (reconfPointInt == RP_MODE_INSTANCE) {
	        reader = new BufferedReader(new FileReader(new File(allStartTestFileName)));
                buffer = "";
                while ((buffer = reader.readLine()) != null) {
                    allStartTestList.add(buffer.trim());
                }
                myPrint("allStartTestList for " + component + " has been loaded, size is " + allStartTestList.size());
            } else if (reconfPointInt >= 1) { // RP_MODE_RECONF
                reader = new BufferedReader(new FileReader(new File(restartTestFileName)));
                buffer = "";
                String[] result = null;
                String testName = "";
                String startPointNumStr = "";
                Integer startPointNum = 0;
                String SPLIT = " ";
                while ((buffer = reader.readLine()) != null) {
                    result = buffer.split(SPLIT);
                    testName = result[0];
                    startPointNumStr = result[1];
                    try {
                        startPointNum = Integer.valueOf(startPointNumStr);
                    } catch (Exception e) {
                        System.out.println(e); 
                        System.exit(-1);
                    }
                    List<String> perPointNumList = restartTestMap.get(startPointNum);
                    if (perPointNumList == null) {
                        perPointNumList = new ArrayList<String>();
                        restartTestMap.put(startPointNum, perPointNumList);
                    }
                    perPointNumList.add(testName);
                }
                reader.close();
                // display entries which are naturally sorted as treemap is used
                for (Map.Entry<Integer, List<String>> entry : restartTestMap.entrySet())   {
                    //System.out.println("Key = " + entry.getKey() +  ", # of Value = " + entry.getValue().size()); 
                    if (entry.getKey() >= reconfPointInt) {
                        restartTestList.addAll(entry.getValue());
                    }
                }
                myPrint("restartTestMap for " + component + " has been loaded, size is " + restartTestList.size());
                if (restartTestList.isEmpty()) {
                    myPrint("restartTestMap is empty, just quit");
                }
            } else {
                System.out.println("Error: reconfPoint " + reconfPoint + " is invalid!");
                System.exit(-1);
            }
//            System.exit(0);
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
        
        loadReconfTesterStaticData(component, reconfPoint);
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
