package com.rdfs.namenode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Arrays;

import com.rdfs.NodeLocation;
import com.rdfs.BlockReplicasLocation;
import com.rdfs.Constants;

public class DataNodeLocationStore {
    private static DataNodeLocationStore store = null;
    private ArrayList<NodeLocation> dataNodes;
    private HashMap<String, ArrayList<NodeLocation[]>> fileNameBlockLocationMap; 

    private DataNodeLocationStore() {
        dataNodes = new ArrayList<NodeLocation>();
        fileNameBlockLocationMap = new HashMap<>();
    }

    public static DataNodeLocationStore getDataNodeLocationStore() {
        if (store == null) {
            store = new DataNodeLocationStore();
        }
        return store;
    }

    public void addDataNode(NodeLocation nodeLocation) {
        for (NodeLocation currentNodeLocation: dataNodes) {
            if (nodeLocation.equals(currentNodeLocation)) {
                return ;
            }
        }
        dataNodes.add(nodeLocation);
    }

    public NodeLocation[] getDataNodeLocations(String fileName) {
        ArrayList<NodeLocation[]> blockLocations = fileNameBlockLocationMap.get(fileName);
        int numberOfBlocks = blockLocations.size();
        NodeLocation[] nodeLocations = new NodeLocation[numberOfBlocks];
        for (int i = 0; i < numberOfBlocks; ++i) {
            NodeLocation[] replicaNodeLocations = blockLocations.get(i);
            var firstLocation = replicaNodeLocations[0];
            nodeLocations[i] = firstLocation;
        }
        return nodeLocations;
    }

    public NodeLocation[] addBlockNodeLocations(String fileName) {
        var newDataNodeLocations = selectNewBlockLocations(fileName);
        var blockLocations = fileNameBlockLocationMap.get(fileName);
        if (blockLocations == null) {
            blockLocations = new ArrayList<NodeLocation[]>();
            fileNameBlockLocationMap.put(fileName, blockLocations);
        }
        blockLocations.add(newDataNodeLocations);
        return newDataNodeLocations;
    }

    private NodeLocation[] selectNewBlockLocations(String fileName) {
        //TODO get this from option
        int replicationFactor = Constants.DEFAULT_REPLICATION_FACTOR;
        var randomLocations = new NodeLocation[replicationFactor];
        int numberOfDataNodes = dataNodes.size();
        Random random = new Random();
        for (int i = 0; i < replicationFactor; ++i) {
            int randomIndex = random.nextInt(numberOfDataNodes);
            var randomNodeLocation = dataNodes.get(randomIndex);
            randomLocations[i] = (randomNodeLocation);
        }
        return randomLocations;
    }

    public void deleteFileMetaData(String fileName) {
        fileNameBlockLocationMap.remove(fileName);
    }

    public BlockReplicasLocation[] getBlockLocations(String fileName) {
        ArrayList<NodeLocation[]> blockLocationsArrayList = fileNameBlockLocationMap.get(fileName);
        int numberOfBlocks = blockLocationsArrayList.size();
        BlockReplicasLocation[] blockReplicasLocations = new BlockReplicasLocation[numberOfBlocks];
        for (int i = 0; i < numberOfBlocks; ++i) {
            NodeLocation[] dataNodeLocations = blockLocationsArrayList.get(i);
            BlockReplicasLocation blockReplicasLocation = new BlockReplicasLocation();
            blockReplicasLocation.dataNodeLocations = dataNodeLocations;
            blockReplicasLocations[i] = blockReplicasLocation;
        }
        return blockReplicasLocations;
    }
}
