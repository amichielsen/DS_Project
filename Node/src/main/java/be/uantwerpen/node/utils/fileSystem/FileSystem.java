package be.uantwerpen.node.utils.fileSystem;

import be.uantwerpen.node.utils.NodeParameters;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import static be.uantwerpen.node.utils.fileSystem.EntryType.*;

public class FileSystem {
    private static final FileSystem instance;
    public static Map<String, FileParameters> fs;

    static {
            instance = new FileSystem();
    }

    private FileSystem() {
        fs = new ConcurrentHashMap<>();
    }

    private static ReadWriteLock lock = new ReentrantReadWriteLock();

    private static Lock readLock = lock.readLock();
    private static Lock writeLock = lock.writeLock();

    public static FileSystem getInstance() {
        return instance;
    }

    public static int addLocal(String file, int ReplicaID) {
        writeLock.lock();
        if (fs.containsKey(file)) return -1;
        try {
            fs.put(file, new FileParameters(LOCAL, ReplicaID));
            return 0;
        } catch (InstantiationException e) {
            return -1;
        }finally {
            writeLock.unlock();
        }
    }

    public static int addReplica(String file, int LocalID) {
        writeLock.lock();
        if (fs.containsKey(file)) return -1;
        try {
            if(NodeParameters.DEBUG)
                System.out.println("[FileSystem] File to be added: " + file);
            fs.put(file, new FileParameters(REPLICA, LocalID));
            return 0;
        } catch (InstantiationException e) {
            return -1;
        }finally {
            writeLock.unlock();
        }
    }

    public static int addDownloaded(String file, int LocalID, int ReplicaID) {
        writeLock.lock();
        if (fs.containsKey(file)) return -1;
        try {
            fs.put(file, new FileParameters(DOWNLOADED, LocalID, ReplicaID));
            return 0;
        } catch (InstantiationException e) {
            return -1;
        }finally {
            writeLock.unlock();
        }
    }

    public static int addEmpty(String file) {
        writeLock.lock();
        if (fs.containsKey(file)) return -1;
        try {
            fs.put(file, new FileParameters(EMPTY));
            return 0;
        } catch (InstantiationException e) {
            return -1;
        }finally {
            writeLock.unlock();
        }
    }
    public static int removeFile(String file) {
        writeLock.lock();
        if (! fs.containsKey(file)) return -1;
        try {
            fs.remove(file);
        }finally {
            writeLock.unlock();
        }
        return 0;
    }

    public static Map<String, FileParameters> getReplicatedFiles(boolean onlyThisNode) {
        readLock.lock();
        try {
            if (onlyThisNode) return fs.entrySet()
                    .stream()
                    .filter(e -> !(e.getValue().getLocalOnNode() == NodeParameters.id))
                    .filter(e -> e.getValue().getReplicatedOnNode() == NodeParameters.id)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            return fs.entrySet()
                    .stream()
                    .filter(e -> !(e.getValue().getLocalOnNode() == NodeParameters.id))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }finally {
            readLock.unlock();
        }
    }

    public static Map<String, FileParameters> getLocalFiles() {
        readLock.lock();
        try {
            return fs.entrySet()
                    .stream()
                    .filter(e -> (e.getValue().getLocalOnNode() == NodeParameters.id))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }finally {
            readLock.unlock();
        }
    }

    public static Map<String, FileParameters> getDownloadedFiles() {
        return fs.entrySet()
                .stream()
                .filter( e -> e.getValue().isDownloaded())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static FileParameters getFileParameters(String file) {
        readLock.lock();
        try {
            return fs.get(file);
        }finally {
            readLock.unlock();
        }
    }
}
