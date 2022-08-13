package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Agent;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.ArrayList;
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
        return getSafestNode(gameView.getViewer().getNodeId(), gameView);
    }

    private ArrayList<Integer> getOpponentPoliceList(GameView gameView) {
        ArrayList<Integer> opponentPolice = new ArrayList<>();
        for (Agent agent : gameView.getVisibleAgentsList()) {
            boolean teammate = agent.getTeamValue() == gameView.getViewer().getTeamValue();
            boolean sameType = agent.getTypeValue() == gameView.getViewer().getTypeValue();
            if (teammate || sameType) {
                continue;
            }
            opponentPolice.add(agent.getNodeId());
        }
        return opponentPolice;
    }

    private int getNearestPoliceDistance(int nodeId, GameView gameView) {
        int minDist = Integer.MAX_VALUE;
        for (int agentNodeId : getOpponentPoliceList(gameView)) {
            minDist = Math.min(
                    minDist,
                    config.getMinDistance(nodeId, agentNodeId)
            );
        }
        return minDist;
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
    private int getSafestNode(int nodeId, GameView gameView) {

//        if(getNearestPoliceDistance(nodeId, gameView) > 3) {
//            return nodeId;
//        }

        ArrayList<Integer> neighborNodes = config.getNeighborNodes(nodeId);
        neighborNodes.add(nodeId);

        neighborNodes.sort((d1, d2) -> nextMoveComparator(nodeId, d1,  d2, gameView));

        for (int node : neighborNodes) {
            if (config.getPathCost(nodeId, node) <= gameView.getBalance()) {
                return node;
            }
        }
        return nodeId;

    }

    private int nextMoveComparator(int currNodeId, int nodeId1, int nodeId2, GameView gameView) {
        int dist1 = getNearestPoliceDistance(nodeId1, gameView);
        int dist2 = getNearestPoliceDistance(nodeId2, gameView);
        if (dist1 == dist2 || Math.min(dist1, dist2) > 3) {
            double cost1 = config.getPathCost(currNodeId, nodeId1);
            double cost2 = config.getPathCost(currNodeId, nodeId2);
            if (cost1 == cost2) {
                int ways1 = config.getNeighborNodesCount(nodeId1);
                int ways2 = config.getNeighborNodesCount(nodeId2);
                return Integer.compare(ways2, ways1);
            }
            return Double.compare(cost1, cost2);
        }
        return Integer.compare(dist2, dist1);
    }
}