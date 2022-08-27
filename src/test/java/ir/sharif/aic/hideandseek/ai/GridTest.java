package ir.sharif.aic.hideandseek.ai;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

class GridTest {

    Grid grid;

    public ArrayList<ArrayList<Integer>> getAllShortestPaths(int fromNodeId, int toNodeId) {
        return grid.getAllShortestPaths(fromNodeId - 1, toNodeId - 1);
    }

    @Test
    void getAllPaths() {
        int[][] graph = new int[][]{
                {0, 999, 1, 1},
                {999, 0, 1, 1},
                {1, 1, 0, 1},
                {1, 1, 1, 0}
        };
        grid = new Grid(graph);

        ArrayList<Integer> filter = new ArrayList<>(Arrays.asList(2, 3));
        Grid.filter = filter;

        System.out.println(getAllShortestPaths(1, 2));
    }
}