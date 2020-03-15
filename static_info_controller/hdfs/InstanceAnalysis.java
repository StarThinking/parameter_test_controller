import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

public class InstanceAnalysis {

    public static List<Integer> portList = new ArrayList<Integer>();
    public static int instances = 0;

    public static void addIntoPortList(Integer port) {
        if (!portList.contains(port)) {
            portList.add(port);
        }
        instances = portList.size() > instances ? portList.size() : instances;
        return;
    }

    public static void removeFromPortList(Integer port) {
        portList.remove(port);
        return;
    }

    public static int findPort(String component, String str, String line) {
        int port = -1;
        String patternStr = " " + str;
        String prePatternStr = "[msx-restart] " + component + " ";
        Pattern portPattern = Pattern.compile(Pattern.quote(prePatternStr) + "(.*?)" + Pattern.quote(patternStr));
        Matcher portMatcher = portPattern.matcher(line);
        while (portMatcher.find()) {
            String portStr = "";
            try {
                portStr = portMatcher.group(1);
                port = Integer.valueOf(portStr);
            } catch (NumberFormatException e) {
                System.out.println("Error: cannot be converted to integer, wrong string " + portStr);
            }
            break;
        }
        return port;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Error: wrong arguments: InstanceAnalysis [filePath] [component]");
            System.exit(-1);
        }
        String filePath = args[0];
        String component = args[1];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = null;
            while ((line = reader.readLine()) != null) {
                int startPort = findPort(component, "start", line);
                int stopPort = findPort(component, "stop", line);
                if (startPort >= 0) {
                    //System.out.println("start port String = " + startPort);
                    addIntoPortList(startPort);
                }
                if (stopPort >= 0) {
                    //System.out.println("stop port String = " + stopPort);
                    removeFromPortList(stopPort);
                }
                if (startPort >= 0 && stopPort >= 0) {
                    System.out.println("Error: it is weired that both startPort and stopPort are valid!");
                    System.exit(-1);
                }
            }
            reader.close();
	} catch (IOException e) {
            e.printStackTrace();
	}
        System.out.println(instances);
    }
}
