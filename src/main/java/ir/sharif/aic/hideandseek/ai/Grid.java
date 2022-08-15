package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Path;

import java.util.*;

public class Grid {

    private int[][] D;
    private int size;

    public Grid(GameView gameView) {
        initGrid(gameView);
    }

    private void initGrid(GameView gameView) {
        size = gameView.getConfig().getGraph().getNodesCount();

        D = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                D[i][j] = (i == j) ? 0 : 9999999;
            }
        }

        List<Path> paths = gameView.getConfig().getGraph().getPathsList();
        for (Path path : paths) {
            int i = path.getFirstNodeId() - 1;
            int j = path.getSecondNodeId() - 1;
            D[i][j] = D[j][i] = 1;
        }
    }

    public ArrayList<Integer> getNeighborNodes(int nodeId) {
        ArrayList<Integer> aroundNodes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (D[nodeId][i] == 1) {
                aroundNodes.add(i + 1);
            }
        }
        return aroundNodes;
    }

    public int[][] floyd() {
        int[][] dist = D.clone();
        for (int k = 0; k < size; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
                }
            }
        }
        return dist;
    }

    private void bfs(ArrayList<ArrayList<Integer>> parent, int src) {
        int n = size;
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Queue<Integer> q = new LinkedList<>();
        q.offer(src);
        parent.get(src).clear();
        parent.get(src).add(-1);
        dist[src] = 0;
        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v = 0; v < n; v++) {
                if (D[u][v] != 1) continue;
                if (dist[v] > dist[u] + 1) {
                    dist[v] = dist[u] + 1;
                    q.offer(v);
                    parent.get(v).clear();
                    parent.get(v).add(u);
                } else if (dist[v] == dist[u] + 1) {
                    parent.get(v).add(u);
                }
            }
        }
    }

    private void findPaths(ArrayList<ArrayList<Integer>> paths, ArrayList<Integer> path,
                           ArrayList<ArrayList<Integer>> parent, int u) {
        if (u == -1) {
            paths.add(new ArrayList<>(path));
            return;
        }
        for (int par : parent.get(u)) {
            path.add(u + 1);
            findPaths(paths, path, parent, par);
            path.remove(path.size() - 1);
        }
    }

    public ArrayList<ArrayList<Integer>> getAllShortestPaths(int src, int dst) {
        int n = size;
        ArrayList<ArrayList<Integer>> paths = new ArrayList<>();
        ArrayList<Integer> path = new ArrayList<>();
        ArrayList<ArrayList<Integer>> parent = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            parent.add(new ArrayList<>());
        }
        bfs(parent, dst);
        findPaths(paths, path, parent, src);
        return paths;
    }


    public ArrayList<ArrayList<Integer>> getAllPaths(int src, int dst) {
        ArrayList<ArrayList<Integer>> allPaths = new ArrayList<>();
        Queue<List<Integer>> queue = new LinkedList<>();

        List<Integer> path = new ArrayList<>();
        path.add(src);
        queue.offer(path);

        while (!queue.isEmpty()) {
            path = queue.poll();
            int last = path.get(path.size() - 1);

            if (last == dst) {
                allPaths.add(new ArrayList<>(path));
            }

            for (int node : getNeighborNodes(last)) {
                if (!path.contains(node-1)) {
                    List<Integer> newpath = new ArrayList<>(path);
                    newpath.add(node-1);
                    queue.offer(newpath);
                }
            }
        }
        return allPaths;
    }


}
