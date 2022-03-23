import java.util.HashMap;
import java.util.Set;

public class NodeFinder {

    private Hash hashGenerator;
    private HashMap nodeMap;

    public NodeFinder(Hash hashGenerator, HashMap nodeMap) {
        this.hashGenerator = hashGenerator;
        this.nodeMap = nodeMap;
    }

    public String findNodeFromFile(String filename){
        int hashCode = hashGenerator.generateHash(filename);
        Set keys = nodeMap.keySet();
        
    }
}
