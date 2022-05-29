package be.uantwerpen.node.lifeCycle.running.services;

import be.uantwerpen.node.utils.NodeParameters;
import be.uantwerpen.node.agents.FailureAgent;
import be.uantwerpen.node.agents.SyncAgent;
import be.uantwerpen.node.utils.fileSystem.EntryType;
import be.uantwerpen.node.utils.fileSystem.FileParameters;
import be.uantwerpen.node.utils.fileSystem.FileSystem;
import be.uantwerpen.node.lifeCycle.Shutdown;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.Map;
import java.util.Objects;

/**
 * This is the "main" running state.
 * 1. We will respond to REST requests.
 * 2. We will adopt new nodes that come on our network.
 * 3. We will report failing nodes to the NameServer.
 * ! Send periodic pings to the neighbors to test their state
 */
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api")
public class RunningRestController {
    /**
     * POST Hello -> payload: send from
     * {
     *     "id": _id of sending node,
     *     "ip": _ip of sending node
     * }
     * return 200 if success, 204 if nothing changes, 503 if not in running, 500 if failed for other reason
     */
    @PostMapping(path ="/hello")
    public static void hello(@RequestBody String payload) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * POST bye -> payload: send from + new neighbor
     * {
     *     "id": _id of sending node,
     *     "ip": _ip of sending node
     * }
     * return 200 if success, 204 if nothing changes, 503 if not in running, 500 if failed for other reason
     */
    @PostMapping(path ="/bye")
    public static void bye(@RequestBody String payload) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
    }


    // GET status -> returns: state + neighbors
    @GetMapping(path ="/status")
    public static String getStatus() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "Running");
        jsonObject.put("online", true);
        jsonObject.put("thisID", NodeParameters.id);
        jsonObject.put("thisIP", NodeParameters.ip);
        jsonObject.put("previousNeighbor", NodeParameters.previousID);
        jsonObject.put("nextNeighbor", NodeParameters.nextID);
        return jsonObject.toString();
    }

    @GetMapping(path="/neighbours")
    public static String getNeighbours(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("previousNeighbor", NodeParameters.previousID);
        jsonObject.put("nextNeighbor", NodeParameters.nextID);
        return jsonObject.toString();
    }

    // Update next node id for
    @PutMapping(path ="/updateNext")
    public static  String updateNextNodeId(@RequestParam Integer hostId) {
        if(Objects.nonNull(hostId))
        {
            NodeParameters.getInstance().setNextID(hostId);
            return "successfully added next id as: "+hostId;
        }
        else return "could not add a null hostId";
    }

    // Update previous node id for
    @PutMapping(path ="/updatePrevious")
    public static  String updatePreviousNodeId(@RequestParam Integer hostId) {
        if(Objects.nonNull(hostId))
        {
            NodeParameters.getInstance().setPreviousID(hostId);
            return "successfully added previous id as:  "+hostId;
        }
        else return "could not add a null hostId";
    }

    /**
     * POST status -> changes settings on the fly (all the payload parameters are optional)
     * {
     *     "newId": _new id for this node,
     *     "newIp": _new ip for this node,
     *     "newPreviousNeighbor: _new previousNeighbor for this node,
     *     "newNextNeighbor: _new nextNeighbor for this node,
     * }
     * return 200 if success, 204 if nothing changes, 503 if not in running, 500 if failed for other reason
     */
    @PostMapping(path ="/status")
    public static void postStatus(@RequestBody String payload) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * POST agent -> changes settings on the fly (all the payload parameters are optional)
     * {
     *     "agent": SYNC | FAILURE
     * }
     * returns 200 if success, 503 if not in running, 500 if failed for other reason
     */
    @PostMapping(path ="/failureagent")
    public static void postAgent(@RequestBody String agentStr) {
        if(NodeParameters.DEBUG) System.out.println("[REST] FailureAgent should start running...");
        FailureAgent agent;
        try {
            agent = new ObjectMapper().readValue(agentStr, FailureAgent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Thread failAgent = new Thread(agent);
        failAgent.start();
    }

    @PostMapping(path ="/syncagent")
    public static void postSyncAgent(@RequestBody String agentStr) {
        //if(NodeParameters.DEBUG) System.out.println("[REST] SyncAgent should start running...");
        SyncAgent agent;
        try {
            agent = new ObjectMapper().readValue(agentStr, SyncAgent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Thread syncThread = new Thread(agent);
        //if(NodeParameters.DEBUG) System.out.println("Received list: " + list);
        syncThread.start();
    }


    @DeleteMapping(path ="/deleteFile")
    public static boolean deleteFile(@RequestBody String filename) {
        return FileDeleter.getInstance().deleteFromReplicaFolder(filename);
    }

    @PutMapping(path ="/addLogEntry")
    public static void addLogEntry(@RequestBody String filename, FileParameters parameters){
         FileSystem.fs.put(filename, parameters);
    }

    @PutMapping(path="/changeOwner")
    public static int changeOwner(@RequestBody String filename){
        if(NodeParameters.DEBUG) System.out.println("[REST] Change owner requested for file: " + filename);
        if(FileSystem.addReplica(filename, NodeParameters.id) != -1) {
            if (NodeParameters.DEBUG) System.out.println("[REST] File not present with name: " + filename);
            FileSystem.fs.get(filename).setReplicatedOnNode(NodeParameters.id);
        }
        if(NodeParameters.DEBUG) System.out.println("[REST] FileSys after owner change: " + FileSystem.fs.get(filename).getReplicatedOnNode());
        return 1;
    }

    @PostMapping(path ="/localDeletion")
    public static void localDeletion(@RequestBody String filename){
        if (FileSystem.getFileParameters(filename).getEntryType() == EntryType.DOWNLOADED) {
            FileSystem.removeFile(filename);
        } else {
            FileSystem.removeFile(filename);
            NodeParameters.upForDeletion.add(filename);
            new File(NodeParameters.replicaFolder + "/"+filename).delete();
        }
    }

    @DeleteMapping(path="/shutdown")
    public static void shutdown(){
        NodeParameters.lifeCycleController.ChangeState(new Shutdown(NodeParameters.lifeCycleController));
    }

    @GetMapping(path="/localfiles")
    public static String getLocalFiles(){
        JSONObject jsonObject = new JSONObject();
        for(Map.Entry<String, FileParameters> entry : FileSystem.getLocalFiles().entrySet())
        jsonObject.put(entry.getKey(), entry.getKey());
        return jsonObject.toString();    }

    @GetMapping(path="/replicafiles")
    public static String getReplicaFiles(){
        JSONObject jsonObject = new JSONObject();
        for(Map.Entry<String, FileParameters> entry : FileSystem.getReplicatedFiles(true).entrySet())
            jsonObject.put(entry.getKey(), entry.getKey());
        return jsonObject.toString();    }
}
