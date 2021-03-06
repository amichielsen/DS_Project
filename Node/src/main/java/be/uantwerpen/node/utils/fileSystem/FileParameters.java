package be.uantwerpen.node.utils.fileSystem;

import be.uantwerpen.node.utils.NodeParameters;

public class FileParameters {
    private boolean isDownloaded;
    private boolean isLocked;
    private int replicatedOnNode;
    private int localOnNode;

    private int lockedOnNode = -1;

    private EntryType entryType;

    public FileParameters(){}
    public FileParameters(EntryType type) throws InstantiationException {
        if (type == EntryType.EMPTY) {
            this.entryType = type;
            instantiateEmpty();
        } else {
            System.out.println("[FP] Wrong constructor has been used!");
            throw new InstantiationException("Cannot be instantiated");

        }
    }

    public FileParameters(EntryType type, int node1) throws InstantiationException {
        if (type == EntryType.LOCAL) {
            this.entryType = type;
            instantiateLocal(node1);
        } else if (type == EntryType.REPLICA) {
            this.entryType = type;
            instantiateReplica(node1);
        } else {
            System.out.println("[FP] Wrong constructor has been used!");
            throw new InstantiationException("Cannot be instantiated");

        }
    }

    public FileParameters(EntryType type, int node1, int node2) throws InstantiationException {
        if (type == EntryType.DOWNLOADED) {
            this.entryType = type;
            instantiateDownloaded(node1, node2);
        } else {
            System.out.println("[FP] Wrong constructor has been used!");
            throw new InstantiationException("Cannot be instantiated");

        }
    }

    public void instantiateLocal(int replicatedOnNode) {
        this.isDownloaded = false;
        this.isLocked = false;
        this.replicatedOnNode = replicatedOnNode;
        this.localOnNode = NodeParameters.id;
    }

    public void instantiateReplica(int localOnNode) {
        this.isDownloaded = false;
        this.isLocked = false;
        this.replicatedOnNode = NodeParameters.id;
        this.localOnNode = localOnNode;
    }

    public void instantiateDownloaded(int localOnNode, int replicatedOnNode) {
        this.isDownloaded = true;
        this.isLocked = false;
        this.replicatedOnNode = replicatedOnNode;
        this.localOnNode = localOnNode;
    }

    public void instantiateEmpty() {
        isLocked = false;
    }

    public void lock(int ID) {
        lockedOnNode = ID;
        isLocked = true;
    }

    public void unLock() {
        isLocked = true;
    }
    public boolean isLocked() {
        return isLocked;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public int getReplicatedOnNode() {
        return replicatedOnNode;
    }

    public void setReplicatedOnNode(int replicatedOnNode) {
        this.replicatedOnNode = replicatedOnNode;
    }

    public int getLocalOnNode() {
        return localOnNode;
    }

    public void setLocalOnNode(int localOnNode) {
        this.localOnNode = localOnNode;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public int getLockedOnNode() {
        return lockedOnNode;
    }

    public void setLockedOnNode(int lockedOnNode) {
        this.lockedOnNode = lockedOnNode;
    }

    public void setEntryType(EntryType entryType) {
        this.entryType = entryType;
    }
}
