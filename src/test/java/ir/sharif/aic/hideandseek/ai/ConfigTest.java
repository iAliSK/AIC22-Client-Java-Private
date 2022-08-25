package ir.sharif.aic.hideandseek.ai;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

class ConfigTest {

    int[][] D;
    int size;

    public ArrayList<Integer> getNeighborNodes(int nodeId) {
        nodeId--;
        ArrayList<Integer> aroundNodes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (D[nodeId][i] == 1) {
                aroundNodes.add(i + 1);
            }
        }
        return aroundNodes;
    }

    public ArrayList<Integer> getNeighborNodes(ArrayList<Integer> nodeId, int degree,
                                               HashSet<Integer> nodes, boolean onlyDegree) {
        if (degree == 0) {
            if (!onlyDegree) return new ArrayList<>(nodes);
            return nodeId;
        }

        ArrayList<Integer> temp = new ArrayList<>();
        for (Integer node : nodeId) {
            for (Integer neighbor : getNeighborNodes(node)) {
                if (!nodes.contains(neighbor))
                    temp.add(neighbor);
            }
        }

        nodes.addAll(temp);
        return getNeighborNodes(temp, degree - 1, nodes, onlyDegree);
    }


    public void getNeighborNodes(int currDegree, int maxDegree, HashMap<Integer, HashSet<Integer>> neighbors) {
        if (currDegree == maxDegree) return;

        neighbors.putIfAbsent(currDegree + 1, new HashSet<>());

        for (int node : neighbors.get(currDegree)) {
            neighbors.get(currDegree + 1).addAll(getNeighborNodes(node));
        }

        getNeighborNodes(currDegree + 1, maxDegree, neighbors);
    }

    @Test
    void getNeighborNodes() {
        D = new int[][]{
                {0, 1, 99, 99, 99},
                {1, 0, 1, 99, 1},
                {99, 1, 0, 1, 99},
                {99, 99, 1, 0, 99},
                {99, 1, 99, 99, 0},
        };
        size = D.length;

        ArrayList<Integer> targets = new ArrayList<>();
        targets.add(2);
        HashSet<Integer> nodes = new HashSet<>(targets);

        ArrayList<Integer> neighborNodes = getNeighborNodes(targets, 2, nodes, true);

        System.out.println(neighborNodes);


        HashMap<Integer, HashSet<Integer>> neighbors = new HashMap<>();
        neighbors.put(0, new HashSet<>(Collections.singletonList(2)));
        getNeighborNodes(0, 2, neighbors);
        System.out.println(neighbors);

    }
}