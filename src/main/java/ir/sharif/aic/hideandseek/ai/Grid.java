package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Path;

import java.util.ArrayList;
import java.util.List;

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
}
