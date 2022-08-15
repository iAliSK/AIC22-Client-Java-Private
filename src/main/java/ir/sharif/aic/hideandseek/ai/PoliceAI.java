package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Agent;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PoliceAI extends AI {

    public List<Integer> thievesLocation = new ArrayList<>();

    public PoliceAI(Phone phone) {
        this.phone = phone;
    }

    /**
     * This function always returns zero (Polices can not set their starting node).
     */
    @Override
    public int getStartingNode(GameView view) {
        updateGame(view);
        initPath();
        return 1;
    }

    /**
     * Implement this function to move your police agent based on current game view.
     */
    @Override
    public int move(GameView view) {
        updateGame(view);
        if (isVisibleTurn()) {
            updatePath();
        }
        return getNextInPath();
    }

    void initPath() {
        path = getCheaperPath(currNodeId,
                getFarthestRandomNodeFromPoliceStation(0.7)
        );
    }

    void updatePath() {
        setThievesLocation();

        Agent leader = getPoliceWithMinId();

        int target = getNearestThief(leader.getNodeId());
        List<Agent> polices = getTeammatePolice(true).stream()
                .sorted(Comparator.comparingInt(Agent::getId)).toList();

        List<Integer> neighborNodes = config.getNearestNodes(target,polices.size());

        int index = polices.indexOf(view.getViewer());
        int dest = neighborNodes.get(index % neighborNodes.size());

        path = getCheaperPath(currNodeId, dest);
        path.add(target);
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

    private int getNearestThief(int nodeId) {
        return thievesLocation.stream().min(Comparator.comparingInt(thiefNodeId ->
                config.getMinDistance(nodeId, thiefNodeId))).orElse(-1);
    }

    private Agent getPoliceWithMinId() {
        return getTeammatePolice(true).stream()
                .min(Comparator.comparingInt(Agent::getId)).orElse(null);
    }


    private void setThievesLocation() {
        thievesLocation = getOpponentThieves().stream()
                .filter(agent -> !agent.getIsDead())
                .map(Agent::getNodeId)
                .toList();
    }
}
