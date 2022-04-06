package be.uantwerpen.namingserver.utils.hash;

import java.net.Inet4Address;
import java.util.*;

/**
 * Class for finding the corresponding node given a filename
 * --Alexander Michielsen
 */
public class NodeFinder {

    private Hash hashGenerator;
    private Map<Integer, Inet4Address> nodeMap;

    public NodeFinder(Hash hashGenerator, Map<Integer, Inet4Address> nodeMap) {
        this.hashGenerator = hashGenerator;
        this.nodeMap = nodeMap;
    }

    public int findNodeFromFile(String filename) {
        int hashCode = hashGenerator.generateHash(filename);
        Set<Integer> nodes = nodeMap.keySet();
        TreeSet<Integer> mainList = new TreeSet<>(nodes);
        Integer smaller = mainList.lower(hashCode);
        return Objects.requireNonNullElse(smaller, mainList.last()); //Wrap around
    }
}
