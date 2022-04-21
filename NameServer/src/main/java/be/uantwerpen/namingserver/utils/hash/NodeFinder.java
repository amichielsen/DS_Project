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

    /**
     * Constructor
     * @param hashGenerator hashgenerator to be used
     * @param nodeMap the map in which to look for the node
     */
    public NodeFinder(Hash hashGenerator, Map<Integer, Inet4Address> nodeMap) {
        this.hashGenerator = hashGenerator;
        this.nodeMap = nodeMap;
    }

    /**
     * Finds the corresponding node
     * @param filename filename to be queried
     * @return the value of the corresponding node
     */
    public int findNodeFromFile(String filename) {
        int hashCode = hashGenerator.generateHash(filename);
        Set<Integer> nodes = nodeMap.keySet();
        TreeSet<Integer> treeNodes = new TreeSet<>(nodes);
        Integer smaller = treeNodes.lower(hashCode);
        return Objects.requireNonNullElse(smaller, treeNodes.last()); //Wrap around
    }
}
