package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Path;

import java.util.ArrayList;
import java.util.HashSet;

public class Config {

    private static Config instance;
    private final Grid grid;
    private final int[][] dist1;
    private final int[][] dist0;
    private ArrayList<Integer>[] next0;
    private ArrayList<Integer>[] next1;
    private double[][] cost;
    private final int nodesCount;

    private Config(GameView view) {
        grid = new Grid(view);
        nodesCount = view.getConfig().getGraph().getNodesCount();
        dist1 = grid.floyd(1);
        dist0 = grid.floyd(0);
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
        next0 = new ArrayList[n];
        next1 = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            next0[i] = grid.getNeighborNodes(i,0);
            next1[i] = grid.getNeighborNodes(i,1);
        }
    }

    public int getNodesCount() {
        return nodesCount;
    }

    public double getPathCost(int fromNodeId, int toNodeId) {
        return cost[fromNodeId - 1][toNodeId - 1];
    }

    public int getMinDistance(int fromNodeId, int toNodeId, int type) {
        int[][] dist = type == 0 ? dist0 : dist1;
        return dist[fromNodeId - 1][toNodeId - 1];
    }

    public int[] getMinDistances(int fromNodeId, int type) {
        int[][] dist = type == 0 ? dist0 : dist1;
        return dist[fromNodeId - 1];
    }

    public ArrayList<ArrayList<Integer>> getAllShortestPaths(int fromNodeId, int toNodeId, int type) {
        return grid.getAllShortestPaths(fromNodeId - 1, toNodeId - 1, type);
    }

    public ArrayList<Integer> getNeighborNodes(int nodeId, int type) {
        ArrayList<Integer>[] next = type == 0 ? next0 : next1;
        return new ArrayList<>(next[nodeId - 1]);
    }


    private ArrayList<Integer> getNeighborNodes(ArrayList<Integer> nodeId, int degree,
                                                HashSet<Integer> nodes, boolean onlyDegree, int type) {
        if (degree == 0) {
            if (!onlyDegree) return new ArrayList<>(nodes);
            return nodeId;
        }

        ArrayList<Integer> temp = new ArrayList<>();
        for (Integer node : nodeId) {
            for (Integer neighbor : getNeighborNodes(node, type)) {
                if (!nodes.contains(neighbor))
                    temp.add(neighbor);
            }
        }

        nodes.addAll(temp);
        return getNeighborNodes(temp, degree - 1, nodes, onlyDegree, type);
    }

    public ArrayList<Integer> getNeighborNodes(int target, int degree, boolean onlyDegree, int type) {
        ArrayList<Integer> targets = new ArrayList<>();
        targets.add(target);
        return getNeighborNodes(targets, degree, new HashSet<>(targets), onlyDegree, type);
    }


//    public boolean isNeighbor(int nodeId1, int nodeId2) {
//        return getNeighborNodes(nodeId1).contains(nodeId2);
//    }


    public int getNeighborNodesCount(int nodeId, int type) {
        return getNeighborNodes(nodeId, type).size();
    }

    public double getPathCost(ArrayList<Integer> path) {
        double pathCost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            pathCost += getPathCost(path.get(i), path.get(i + 1));
        }
        return pathCost;
    }

}
