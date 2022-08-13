package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.ArrayList;

public class PoliceAI extends AI {

    public PoliceAI(Phone phone) {
        this.phone = phone;
    }

    /**
     * This function always returns zero (Polices can not set their starting node).
     */
    @Override
    public int getStartingNode(GameView gameView) {
        return 1;
    }

    /**
     * Implement this function to move your police agent based on current game view.
     */
    @Override
    public int move(GameView gameView) {
        this.config = Config.getInstance(gameView);
//        int agentId = gameView.getViewer().getId();
//        int nodeId = gameView.getViewer().getNodeId();
//
//        int farthest = getFarthestRandomNodeFromPoliceStation(agentId);
//
//        ArrayList<ArrayList<Integer>> paths;
//
//        ArrayList<Integer> opponents = getOpponentPoliceList(gameView);
//
//        if (opponents.isEmpty())
//            paths = config.getAllShortestPaths(nodeId, farthest);
//        else
//            paths = config.getAllShortestPaths(nodeId, opponents.get(0));
//
//
//        for (ArrayList<Integer> path : paths) {
//            if (config.getPathCost(nodeId, path.get(1)) <= gameView.getBalance()) {
//                return path.get(1);
//            }
//        }
//        return nodeId;
        return 1;
    }
}
