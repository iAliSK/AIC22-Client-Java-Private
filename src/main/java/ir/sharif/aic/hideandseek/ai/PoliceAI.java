package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.Logger;
import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Agent;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PoliceAI extends AI {

    public List<Integer> thievesLocation = new ArrayList<>();
    public int target = -1;

    public PoliceAI(Phone phone) {
        this.phone = phone;
    }

    /**
     * This function always returns zero (Polices can not set their starting node).
     */
    @Override
    public int getStartingNode(GameView view) {
        updateGame(view);
        logger = new Logger(String.format("logs/police-%d.log", currAgentId));
        logger.enableLogging(true);
        initPath();
        return 1;
    }

    /**
     * Implement this function to move your police agent based on current game view.
     */
    @Override
    public int move(GameView view) {
        updateGame(view);
        try {
            if (isVisibleTurn()) {
                setThievesLocation();
                target = getNearestThief();
                updatePath(target);
            }
            if (target > 0 && !isVisibleTurn() && isPoliceWithDist1()) {
                updatePath(bestPossibilityForThief());
            }
            return getNextInPath();
        } catch (Exception e) {
            throw e;
        }
//        return currNodeId;
    }

    boolean isSurrounded() {
        return emptyNodes().size() == 0;
    }

    ArrayList<Integer> emptyNodes() {
        ArrayList<Agent> polices = getTeammatePolice(false);

        ArrayList<Integer> neighborNodes = config.getNeighborNodes(target);

        for (Agent node : polices) {
            if (neighborNodes.contains(node.getNodeId())) {
                neighborNodes.remove((Integer) node.getNodeId());
            }
        }

        return neighborNodes;

    }

    boolean isPoliceWithDist1() {
        ArrayList<Agent> polices = getTeammatePolice(false);

        ArrayList<Integer> neighborNodes = config.getNeighborNodes(target);

        for (Agent node : polices) {
            if (neighborNodes.contains(node.getNodeId())) {
                return true;
            }
        }

        return false;
    }

    private int getUniqueIndex() {
        return mapToAgentId(getTeammatePolice(true), true).indexOf(currAgentId);
    }

    int bestPossibilityForThief() {
        ArrayList<Agent> polices = getTeammatePolice(false);

        ArrayList<Integer> neighborNodes = config.getNeighborNodes(target);

        for (Agent node : polices) {
            neighborNodes.removeAll(config.getNeighborNodes(node.getNodeId()));
        }

        neighborNodes.sort(Comparator
                .comparingInt((Integer node) -> -getDisToAllPolices(node))
                .thenComparingInt(node -> -config.getNeighborNodesCount(node))
                .thenComparingInt(nodeId -> nodeId)
        );

        if (neighborNodes.size() == 0) return target;
        return neighborNodes.get(0);
    }

    void initPath() {
        path = getCheaperPath(currNodeId,
                getFarthestRandomNodeFromPoliceStation(0.7)
        );
    }

    void updatePath(int tg) {

        target = tg;

        int degree = Math.min(3, config.getMinDistance(currNodeId, target));
        List<Integer> neighborNodes = config.getNeighborNodes(target, degree, false);

        neighborNodes.sort(Comparator
                .comparingInt((Integer node) -> config.getMinDistance(node, target))
                .thenComparingInt(node -> -config.getNeighborNodesCount(node))
                .thenComparingInt(nodeId -> nodeId)
        );

        ArrayList<Agent> polices = getTeammatePolice(true);

        polices.sort(Comparator.comparingInt((Agent agent) ->
                config.getMinDistance(agent.getNodeId(), target))
                .thenComparingInt(Agent::getId)
        );

        int index = polices.indexOf(view.getViewer());

        int dest = neighborNodes.get(index % neighborNodes.size());

        path = getCheaperPath(currNodeId, dest);

        logger.log("turn:%d\t" +
                        "curr:%d\t" +
                        "target:%d\t" +
                        "path:%s\t" +
                        "neigh:%s\n",
                view.getTurn().getTurnNumber(),
                currNodeId,
                target,
                path.toString(),
                neighborNodes.toString()
        );
    }

    private int getNextInPath() {
        int index = path.indexOf(currNodeId);
        int choseNode = currNodeId;
        if (index >= 0 && index < path.size() - 1) {
            int next = path.get(index + 1);
            if (isMoneyEnoughToMove(currNodeId, next)) {
                choseNode = next;
            }
        }
        return choseNode;
    }

    private int getNearestThief() {
        return thievesLocation.stream().min(Comparator
                .comparingInt(this::getDisToAllPolices)
                .thenComparingInt(config::getNeighborNodesCount)
                .thenComparingInt(nodeId -> nodeId)
        ).orElse(getFarthestRandomNode(currNodeId, 0.7));
    }

    private int getDisToAllPolices(int thiefLoc) {
        ArrayList<Agent> polices = getTeammatePolice(false);
        int sum = 0;
        for (Agent police : polices) {
            sum += config.getMinDistance(police.getNodeId(), thiefLoc);
        }
        return sum;
    }

    private void setThievesLocation() {
        thievesLocation = getOpponentThieves().stream()
                .filter(agent -> !agent.getIsDead())
                .map(Agent::getNodeId)
                .toList();
    }
}
