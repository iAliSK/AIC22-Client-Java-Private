package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Path;

import java.util.ArrayList;
import java.util.Random;

public class Config {

    private static Config instance;
    private final Grid grid;
    private final int[][] dist;
    private ArrayList<Integer>[] next;
    private double[][] cost;
    private final int nodesCount;

    private Config(GameView gameView) {
        grid = new Grid(gameView);
        dist = grid.floyd();
        initCost(gameView);
        initNext(gameView);
        nodesCount = gameView.getConfig().getGraph().getNodesCount();
    }

    public static Config getInstance(GameView gameView) {
        if (instance == null) {
            instance = new Config(gameView);
        }
        return instance;
    }

    private void initCost(GameView gameView) {
        int n = gameView.getConfig().getGraph().getNodesCount();
        cost = new double[n][n];
        for (Path path : gameView.getConfig().getGraph().getPathsList()) {
            int i = path.getFirstNodeId() - 1;
            int j = path.getSecondNodeId() - 1;
            cost[i][j] = cost[j][i] = path.getPrice();
        }
    }

    @SuppressWarnings("unchecked")
    private void initNext(GameView gameView) {
        int n = gameView.getConfig().getGraph().getNodesCount();
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

    public ArrayList<Integer> getNeighborNodes(int nodeId) {
        return next[nodeId - 1];
    }

    public boolean isNeighbor(int nodeId1, int nodeId2) {
        return getNeighborNodes(nodeId1).contains(nodeId2);
    }

    public int getRandInt(int bound, int seed) {
        Random random = new Random();
        random.setSeed(seed+random.nextInt());
        return random.nextInt(bound);
    }

    public int getNeighborNodesCount(int nodeId) {
        return getNeighborNodes(nodeId).size();
    }
}
