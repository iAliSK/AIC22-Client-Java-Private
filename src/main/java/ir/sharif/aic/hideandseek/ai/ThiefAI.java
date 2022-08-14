package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Agent;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.ArrayList;

public class ThiefAI extends AI {

    public ThiefAI(Phone phone) {
        this.phone = phone;
    }

    /**
     * Used to select the starting node.
     */
    @Override
    public int getStartingNode(GameView view) {
        updateGame(view);
        return getFarthestRandomNodeFromPoliceStation();
    }

    /**
     * Implement this function to move your thief agent based on current game view.
     */
    @Override
    public int move(GameView view) {
        updateGame(view);
        return getSafestNode(currNodeId);
    }

    private int getNearestPoliceDistance(int nodeId) {
        int minDist = Integer.MAX_VALUE;
        for (Agent agent : getOpponentPolice()) {
            minDist = Math.min(
                    minDist,
                    config.getMinDistance(nodeId, agent.getNodeId())
            );
        }
        return minDist;
    }

    private int getSafestNode(int nodeId) {
        ArrayList<Integer> neighborNodes = config.getNeighborNodes(nodeId);
        neighborNodes.add(nodeId);

        neighborNodes.sort((d1, d2) -> nextMoveComparator(nodeId, d1, d2));

        for (int node : neighborNodes) {
            if (config.getPathCost(nodeId, node) <= view.getBalance()) {
                return node;
            }
        }
        return nodeId;

    }

    private int nextMoveComparator(int fromNodeId, int toNodeId1, int toNodeId2) {
        int dist1 = getNearestPoliceDistance(toNodeId1);
        int dist2 = getNearestPoliceDistance(toNodeId2);
        if (dist1 == dist2 || Math.min(dist1, dist2) > 3) {
            double cost1 = config.getPathCost(fromNodeId, toNodeId1);
            double cost2 = config.getPathCost(fromNodeId, toNodeId2);
            if (cost1 == cost2) {
                int ways1 = config.getNeighborNodesCount(toNodeId1);
                int ways2 = config.getNeighborNodesCount(toNodeId2);
                return Integer.compare(ways2, ways1);
            }
            return Double.compare(cost1, cost2);
        }
        return Integer.compare(dist2, dist1);
    }
}