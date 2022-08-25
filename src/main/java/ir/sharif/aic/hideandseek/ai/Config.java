package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Config {

    private static Config instance;
    private final Grid grid;
    private final int[][] dist;
    private ArrayList<Integer>[] next;
    private double[][] cost;
    private final int nodesCount;

    private Config(GameView view) {
        grid = new Grid(view);
        nodesCount = view.getConfig().getGraph().getNodesCount();
        dist = grid.floyd();
        initCost(view);
        initNext();
    }

    public static Config getInstance(GameView view) {
        if (instance == null) {
            instance = new Config(view);
        }
        return instance;
    }

    private void initCost(GameView view) {
        int n = nodesCount;
        cost = new double[n][n];
        for (Path path : view.getConfig().getGraph().getPathsList()) {
            int i = path.getFirstNodeId() - 1;
            int j = path.getSecondNodeId() - 1;
            cost[i][j] = cost[j][i] = path.getPrice();
        }
    }

    @SuppressWarnings("unchecked")
    private void initNext() {
        int n = nodesCount;
        next = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            next[i] = grid.getNeighborNodes(i);
        }
    }

    public int getNodesCount() {
        return nodesCount;
    }

    public double getPathCost(int fromNodeId, int toNodeId) {
        return cost[fromNodeId - 1][toNodeId - 1];
    }

    public int getMinDistance(int fromNodeId, int toNodeId) {
        return dist[fromNodeId - 1][toNodeId - 1];
    }

    public int[] getMinDistances(int fromNodeId) {
        return dist[fromNodeId - 1];
    }

    public ArrayList<ArrayList<Integer>> getAllShortestPaths(int fromNodeId, int toNodeId) {
        return grid.getAllShortestPaths(fromNodeId - 1, toNodeId - 1);
    }

    public ArrayList<Integer> getNeighborNodes(int nodeId) {
        return new ArrayList<>(next[nodeId - 1]);
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

    public ArrayList<Integer> getNearestNodes(int nodeId) {
        ArrayList<Integer> neighborNodes = getNeighborNodes(nodeId);
        ArrayList<Integer> temp = new ArrayList<>();
        HashSet<Integer> nodes = new HashSet<>(neighborNodes);
        nodes.add(nodeId);

        for (Integer node : neighborNodes) {
            nodes.addAll(getNeighborNodes(node));
            temp.addAll(getNeighborNodes(node));
        }
        for (Integer node : temp) {
            nodes.addAll(getNeighborNodes(node));
        }

        ArrayList<Integer> selectedNodes = new ArrayList<>(nodes);
        selectedNodes.sort((o1, o2) -> {
            int temp1 = Integer.compare(getMinDistance(o1, nodeId), getMinDistance(o2, nodeId));
            if (temp1 != 0) return temp1;
            int temp2 = Integer.compare(getNeighborNodesCount(o2), getNeighborNodesCount(o1));
            if (temp2 != 0) return temp2;
            return Integer.compare(o1, o2);
        });

        return selectedNodes;
    }

    public boolean isNeighbor(int nodeId1, int nodeId2) {
        return getNeighborNodes(nodeId1).contains(nodeId2);
    }


    public int getNeighborNodesCount(int nodeId) {
        return getNeighborNodes(nodeId).size();
    }

    public double getPathCost(ArrayList<Integer> path) {
        double pathCost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            pathCost += getPathCost(path.get(i), path.get(i + 1));
        }
        return pathCost;
    }

}
