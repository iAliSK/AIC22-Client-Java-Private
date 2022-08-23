package ir.sharif.aic.hideandseek.ai;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class GridTest {

    @Test
    void getAllPaths() {
        int[][] graph = new int[][]{
                {0, 999, 1, 1},
                {999, 0, 1, 1},
                {1, 1, 0, 1},
                {1, 1, 1, 0}
        };
        Grid grid = new Grid(graph);

        ArrayList<Integer> filter = new ArrayList<>();
        filter.add(2);
        Grid.filter = filter;

        System.out.println(grid.getAllShortestPaths(0, 1));
    }
}