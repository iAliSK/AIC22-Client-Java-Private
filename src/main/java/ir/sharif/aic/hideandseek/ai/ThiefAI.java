package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

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

        return 2;
    }

    /**
     * Implement this function to move your thief agent based on current game view.
     */
    @Override
    public int move(GameView gameView) {
        this.config = Config.getInstance(gameView);
        return 2;
    }

}