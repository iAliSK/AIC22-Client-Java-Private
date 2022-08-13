package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto;
import ir.sharif.aic.hideandseek.protobuf.AIProto.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class PoliceAI extends AI {

    public int dest;
    public ArrayList<Integer> thiefNodeIDs;
    public int target;

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

        int agentId = gameView.getViewer().getId();

        if (gameView.getTurn().getTurnNumber() <= 2) {
            dest = getFarthestRandomNodeFromPoliceStation(agentId);
        }

        if (gameView.getConfig().getTurnSettings().getVisibleTurnsList()
                .contains(gameView.getTurn().getTurnNumber())) {
            setThiefNodeIDs(gameView);
            setTarget(gameView);
            setDest(gameView);
        }

        if (gameView.getTurn().getTurnNumber() <
                gameView.getConfig().getTurnSettings().getVisibleTurns(0)) {

        } else {

        }

        // decide next move in case of reaching destination

        return 1;
    }

    private void setTarget(GameView gameView) {
        Agent policeMinID = getPoliceMinID(gameView);

        int minDist = 9999999;
        for (int dest : thiefNodeIDs) {
            int temp = config.getMinDistance(policeMinID.getNodeId(),dest);
            if (temp < minDist) {
                minDist = temp;
                target = dest;
            }
        }

    }

    private Agent getPoliceMinID(GameView gameView) {
        ArrayList<Agent> police = getCurrentTeamPolices(gameView);

        int minID = 999999;
        Agent policeMinID = null;
        for (Agent agent : police) {
            if (agent.getId() < minID) {
                minID = agent.getId();
                policeMinID = agent;
            }
        }
        return policeMinID;
    }

    private void setDest(GameView gameView) {
        ArrayList<Agent> polices = getCurrentTeamPolices(gameView);
        polices.sort(Comparator.comparingInt(Agent::getId));
        ArrayList<Integer> neighborNodes = config.getNeighborNodes(target);
        int index = polices.indexOf(gameView.getViewer());
        dest = neighborNodes.get(index % neighborNodes.size());
    }

    private ArrayList<Agent> getCurrentTeamPolices(GameView gameView) {
        ArrayList<Agent> police = (ArrayList<Agent>) gameView.getVisibleAgentsList();
        police.removeIf(agent -> (agent.getType().equals(AgentType.THIEF)));
        police.removeIf(agent -> (agent.getTeamValue() != gameView.getViewer().getTeamValue()));
        police.add(gameView.getViewer());
        return police;
    }

    private void setThiefNodeIDs(GameView gameView) {
        thiefNodeIDs.clear();

        for (Agent agent : gameView.getVisibleAgentsList()) {
            boolean teammate = agent.getTeamValue() == gameView.getViewer().getTeamValue();
            boolean sameType = agent.getTypeValue() == gameView.getViewer().getTypeValue();

            if (teammate || sameType || agent.getIsDead()) {
                continue;
            }

            thiefNodeIDs.add(agent.getNodeId());
        }
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
