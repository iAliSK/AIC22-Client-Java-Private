package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

public class ThiefAI extends AI {

    public ThiefAI(Phone phone) {
        this.phone = phone;
    }

    /**
     * Used to select the starting node.
     */
    @Override
    public int getStartingNode(GameView gameView) {
        this.config = Config.getInstance(gameView);

        int agentId = gameView.getViewer().getId();

        return getFarthestRandomNodeFromPoliceStation(agentId);

//        int nodeId = gameView.getViewer().getNodeId();
//        System.out.printf("""
//                        node_id: %d
//                        neighbor_nodes: %s
//                        pathCost[1][46]: %f
//                        minDist[1][43]: %d
//                        """,
//                nodeId,
//                config.getNeighborNodes(nodeId),
//                config.getPathCost(1, 46),
//                config.getMinDistance(1, 43)
//        );

//        return 2;
    }

    /**
     * Implement this function to move your thief agent based on current game view.
     */
    @Override
    public int move(GameView gameView) {
        this.config = Config.getInstance(gameView);
        return gameView.getViewer().getNodeId();
    }

    private int getFarthestRandomNodeFromPoliceStation(int seed) {
        int[] distances = config.getMinDistances(1);

        int maxDist = Arrays.stream(distances).max().orElse(2);

        int minDist = (int) (0.8 * maxDist);

        Integer[] farthestNodes = IntStream.range(0, distances.length)
                .filter(i -> distances[i] >= minDist)
                .boxed().toArray(Integer[]::new);


        // sort by max neighbor nodes count
        Arrays.sort(farthestNodes, Comparator.
                comparingInt(d -> config.getNeighborNodesCount((Integer) d)).reversed());


        int randIndex = config.getRandInt(farthestNodes.length / 2, seed);
        return farthestNodes[randIndex];
    }
}
