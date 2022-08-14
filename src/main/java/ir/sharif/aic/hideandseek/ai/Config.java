package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Path;

import java.util.ArrayList;

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
        return next[nodeId - 1];
    }

    public boolean isNeighbor(int nodeId1, int nodeId2) {
        return getNeighborNodes(nodeId1).contains(nodeId2);
    }


    public int getNeighborNodesCount(int nodeId) {
        return getNeighborNodes(nodeId).size();
    }


}
