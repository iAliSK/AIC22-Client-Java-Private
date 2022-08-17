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
        /*
        System.out.println(
                "  TARGET : " + target +
                "  AgentID : " + currAgentId +
                "  NodeID : " + currNodeId +
                "  LEADER_ID : " + leader.getId() +
                "  LEADER_NodeID : " + leader.getNodeId())
         */
        ArrayList<Agent> polices = getTeammatePolice(false);
        polices.sort((Comparator.comparingInt
                        ((Agent o) -> config.getMinDistance(o.getNodeId(), target)).reversed()
                .thenComparingInt(Agent::getId)));

        ArrayList<Integer> neighborNodes = config.getNearestNodes(target);

        /*
        StringBuilder sb = new StringBuilder();
        for (Agent a:polices) {
            sb.append(a.getId()).append(" ");
        }

         */


        //System.out.println("TargetNode : " + target + " leaderID : " + leader.getId() + " neighborNodes" + neighborNodes);
        //System.out.println("polices :::: " + polices.size() + " :::: " + sb);



        int index = polices.indexOf(view.getViewer());
        int dest = neighborNodes.get(index % neighborNodes.size());

        //System.out.println("index " + index);
        //System.out.println("dest " + dest);

        path = getCheaperPath(currNodeId, dest);
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
        return thievesLocation.stream().min((o1, o2) -> {
            int temp1 = Integer.compare(config.getMinDistance(o1, nodeId), config.getMinDistance(o2, nodeId));
            if (temp1 != 0) return temp1;
            int temp2 = Integer.compare(config.getNeighborNodesCount(o1), config.getNeighborNodesCount(o2));
            if (temp2 != 0) return temp2;
            return Integer.compare(o1, o2);
        })
                .orElse(-1);
    }

    private Agent getPoliceWithMinId() {
        return getTeammatePolice(false).stream()
                .min(Comparator.comparingInt(Agent::getId)).orElse(null);
    }


    private void setThievesLocation() {
        thievesLocation = getOpponentThieves().stream()
                .filter(agent -> !agent.getIsDead())
                .map(Agent::getNodeId)
                .toList();
    }
}
