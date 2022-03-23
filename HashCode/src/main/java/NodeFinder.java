import java.util.*;

public class NodeFinder {

    private Hash hashGenerator;
    private HashMap<Integer, String> nodeMap;

    public NodeFinder(Hash hashGenerator, HashMap nodeMap) {
        this.hashGenerator = hashGenerator;
        this.nodeMap = nodeMap;
    }

    public String findNodeFromFile(String filename){
        int hashCode = hashGenerator.generateHash(filename);
        Set keys = nodeMap.keySet();
        List<String> mainList = new ArrayList<String>(keys);
        int value = usingBinarySearch(hashCode, mainList);
        return nodeMap.get(value);
    }

    public static int usingBinarySearch(int value, int[] a) {
        if (value <= a[0]) { return a[0]; }
        if (value >= a[a.length - 1]) { return a[a.length - 1]; }

        int result = Arrays.binarySearch(a, value);
        if (result >= 0) { return a[result]; }

        int insertionPoint = -result - 1;
        return (a[insertionPoint] - value) < (value - a[insertionPoint - 1]) ?
                a[insertionPoint] : a[insertionPoint - 1];

    }
