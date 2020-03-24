import java.util.*;

public class TestResult {
        public String parameter = "";
        public String component = "";
        public String v1 = "";
        public String v2 = "";
        public String reconfPoint = "";
        public String unitTest = "";
        
        public String result = "";
        public String failureMessage = "";
        public String stackTrace = "";
        
        public static int NumOfFieldsFromFile = 4;

        public TestResult(TestResult origin) {
            this.parameter = origin.parameter;
            this.component = origin.component;
            this.reconfPoint = origin.reconfPoint;
            this.v1 = origin.v1;
            this.v2 = origin.v2;
            this.unitTest = origin.unitTest;
            // 1: succeed -1:failed
            this.result = origin.result; // some tests may not complete, so treat no result as failed
        }

        public TestResult(String parameter, String component, String v1, String v2, String reconfPoint, String unitTest) {
            this.parameter = parameter;
            this.component = component;
            this.reconfPoint = reconfPoint;
            this.v1 = v1;
            this.v2 = v2;
            this.unitTest = unitTest;
            // 1: succeed -1:failed
            this.result = "-1"; // some tests may not complete, so treat no result as failed
        }

        @Override
        public String toString() {
            return "issue_parameter: " + parameter + "\n" +
                "component: " + component + "\n" +
                "reconfPoint: " + reconfPoint + "\n" +
                "v1: " + v1 + "\n" +
                "v2: " + v2 + "\n" +
                "unitTest: " + unitTest + "\n" +
                "result: " + result;
        }
        
        public String completeInfo() {
            return this.toString() + "\n" +
                "failureMessage: " + failureMessage + "\n" +
                "stackTrace: " + stackTrace + "\n";
        }

        public String shortName() {
            return parameter + "%" + component + "%" + v1 + "%" + v2 + "%" + reconfPoint + "%" + unitTest;
        }
        
        public static TestResult getTestResultByName(List<TestResult> list, String name) {
            for (TestResult t : list) {
                if (t.unitTest.equals(name))
                    return t;
            }
            // error
            Controller.myPrint("Error: name " + name + " cannot be found in the list");
            return null;
        }

        public static List<String> getTestNames(List<TestResult> list) {
            List<String> names = new ArrayList<String>();
            for (TestResult t : list)
                names.add(t.unitTest);
            return names;
        }

        public static boolean isValid(TestResult tr) {
            if (!tr.component.equals("NameNode") && !tr.component.equals("DataNode") && !tr.component.equals("JournalNode")) {
                Controller.myPrint("Error: wrong component " + tr.component);
                return false;
            }
            return true;
        }
    }

