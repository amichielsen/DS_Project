package be.uantwerpen.node;

import be.uantwerpen.node.utils.Hash;

public class FileParameters {
    public FileParameters() {
    }

    public FileParameters(Integer FileId, String FileName, Boolean LocalOrReplica, Integer ReplicaLocation, Integer LocalLocation) {
        this.fileId = FileId;
        this.fileName = FileName;
        this.localOrReplica = LocalOrReplica;
        this.replicaLocation = ReplicaLocation;
        this.localLocation = LocalLocation;
    }

    private Integer fileId;
    private String fileName;
    private Boolean localOrReplica;
    private Integer replicaLocation;
    private Integer localLocation;


    public Integer FileId() {
        return fileId;
    }

    public void setFileId(Integer FileId) {
        this.fileId = FileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String FileName) {
        this.fileName = FileName;
    }
    public Boolean LocalOrReplica() {
        return localOrReplica;
    }

    public void setLocalOrReplicaId(Boolean LocalOrReplica) {
        this.localOrReplica = LocalOrReplica;
    }

    public Integer ReplicaLocation() {
        return replicaLocation;
    }

    public void setReplicaLocation(Integer ReplicaLocation) {
        this.replicaLocation = ReplicaLocation;
    }
    public Integer LocalLocation() {
        return localLocation;
    }

    public void setLocalLocation(Integer LocalLocation) {
        this.localLocation = LocalLocation;
    }

    @Override
    public String toString() {
        return "File [fileId=" + fileId + ", fileName=" + fileName + ", Local or Replica=" + localOrReplica +", ReplicaLocation=" + replicaLocation + "]";
    }

}
