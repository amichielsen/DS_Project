package be.uantwerpen.node.lifeCycle.running;

import be.uantwerpen.node.NodeParameters;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

/**
 * This is the "main" running state.
 * 1. We will respond to REST requests.
 * 2. We will adopt new nodes that come on our network.
 * 3. We will report failing nodes to the NameServer.
 * ! Send periodic pings to the neighbors to test their state
 */
@RestController
@RequestMapping("/api")
public class RunningRestController {
    /**
     * POST Hello -> payload: send from
     * {
     *     "id": _id of sending node,
     *     "ip": _ip of sending node
     * }
     * @return 200 if success, 204 if nothing changes, 503 if not in running, 500 if failed for other reason
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
     * @return 200 if success, 204 if nothing changes, 503 if not in running, 500 if failed for other reason
     */
    @PostMapping(path ="/bye")
    public static void bye(@RequestBody String payload) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @GetMapping(path ="/ping")
    public static int ping() {
        return 200;
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
     * @return 200 if success, 204 if nothing changes, 503 if not in running, 500 if failed for other reason
     */
    @PostMapping(path ="/status")
    public static void postStatus(@RequestBody String payload) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
