import java.util.*;

public class NodeFinder {

    private Hash hashGenerator;
    private HashMap<Integer, String> nodeMap;

    public NodeFinder(Hash hashGenerator, HashMap<Integer, String> nodeMap) {
        this.hashGenerator = hashGenerator;
        this.nodeMap = nodeMap;
    }

    public int findNodeFromFile(String filename) {
        int hashCode = hashGenerator.generateHash(filename);
        Set<Integer> nodes = nodeMap.keySet();
        TreeSet<Integer> mainList = new TreeSet<>(nodes);
        Integer smaller = mainList.lower(hashCode);
        return Objects.requireNonNullElse(smaller, -1);
    }
}
