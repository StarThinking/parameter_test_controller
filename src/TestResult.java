import java.util.*;

public class TestResult {
    // test info
    public String proj = "";
    public String u_test = "";
    public String h_list = "";
   
    // test result info
    public String result = "";
    public String failureMessage = "";
    public String stackTrace = "";
    
    public static final int NumOfFieldsFromFile = 4;

    public TestResult(String p, String t, String l) {
        this.proj = p;
        this.u_test = t;
        this.h_list = l;
        // 1: succeed -1:failed, assign failed as default
        this.result = "-1"; 
    }

    public TestResult(TestResult origin) {
        this.proj = origin.proj;
        this.u_test = origin.u_test;
        this.h_list = origin.h_list;
        this.result = origin.result;
    }

    @Override
    public String toString() {
        return "proj: " + proj + "\n" +
            "u_test: " + u_test + "\n" +
            "h_list: " + h_list;
    }
    
    public String completeInfo() {
        return this.toString() + "\n" +
            "result: " + result + "\n" +
            "failureMessage: " + failureMessage + "\n" +
            "stackTrace: " + stackTrace + "\n";
    }

    public static TestResult getTestResultByName(List<TestResult> list, String name) {
        for (TestResult t : list) {
            if (t.u_test.equals(name))
                return t;
        }
        // error
        System.out.println("Error: name " + name + " cannot be found in the list");
        return null;
    }

    public static List<String> getTestNames(List<TestResult> list) {
        List<String> names = new ArrayList<String>();
        for (TestResult t : list)
            names.add(t.u_test);
        return names;
    }

    public static boolean isValid(TestResult tr) {
        if (!tr.proj.equals("hdfs") && !tr.proj.equals("yarn") && !tr.proj.equals("mapreduce") && !tr.proj.equals("hadoop-tools") && !tr.proj.equals("hbase")) {
    	System.out.println("ERROR: wrong proj " + tr.proj);
    	return false;
        }

        if (tr.u_test == null || tr.u_test.equals("")) {
    	System.out.println("ERROR: wrong unit test " + tr.u_test);
            return false;
        }
        return true;
    }
}

