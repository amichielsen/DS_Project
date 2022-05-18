package be.uantwerpen.node.fileSystem;

import be.uantwerpen.node.NodeParameters;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static be.uantwerpen.node.fileSystem.EntryType.*;

public class FileSystem {
    private static final FileSystem instance;
    public static Map<String, FileParameters> fs;

    static {
            instance = new FileSystem();
    }

    private FileSystem() {
        fs = new HashMap<>();
    }

    public static FileSystem getInstance() {
        return instance;
    }

    public static int addLocal(String file, int ReplicaID) {
        if (fs.containsKey(file)) return -1;
        try {
            fs.put(file, new FileParameters(LOCAL, ReplicaID));
            return 0;
        } catch (InstantiationException e) {
            return -1;
        }
    }

    public static int addReplica(String file, int LocalID) {
        if (fs.containsKey(file)) return -1;
        try {
            fs.put(file, new FileParameters(REPLICA, LocalID));
            return 0;
        } catch (InstantiationException e) {
            return -1;
        }
    }

    public static int addDownloaded(String file, int LocalID, int ReplicaID) {
        if (fs.containsKey(file)) return -1;
        try {
            fs.put(file, new FileParameters(DOWNLOADED, LocalID, ReplicaID));
            return 0;
        } catch (InstantiationException e) {
            return -1;
        }
    }

    public static int addEmpty(String file) {
        if (fs.containsKey(file)) return -1;
        try {
            fs.put(file, new FileParameters(EMPTY));
            return 0;
        } catch (InstantiationException e) {
            return -1;
        }
    }
    public static int removeFile(String file) {
        if (! fs.containsKey(file)) return -1;

        fs.remove(file);
        return 0;
    }

    public static Map<String, FileParameters> getReplicatedFiles(boolean onlyThisNode) {
        if (onlyThisNode) return fs.entrySet()
                                    .stream()
                                    .filter( e -> !e.getValue().isLocalOnThisNode())
                                    .filter( e -> e.getValue().getReplicatedOnNode() == NodeParameters.id)
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return fs.entrySet()
                .stream()
                .filter( e -> !e.getValue().isLocalOnThisNode())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<String, FileParameters> getLocalFiles() {
        return fs.entrySet()
                .stream()
                .filter( e -> e.getValue().isLocalOnThisNode())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<String, FileParameters> getDownloadedFiles() {
        return fs.entrySet()
                .stream()
                .filter( e -> e.getValue().isDownloaded())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static FileParameters getFileParameters(String file) {
        return fs.get(file);
    }
}
