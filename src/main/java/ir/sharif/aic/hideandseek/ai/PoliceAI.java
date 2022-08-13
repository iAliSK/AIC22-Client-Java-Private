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

    public int dest = -1;
    public ArrayList<Integer> path = new ArrayList<>();
    public ArrayList<Integer> thiefNodeIDs = new ArrayList<>();
    public int target = -1;

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
        int curNodeID = gameView.getViewer().getNodeId();

        if (gameView.getTurn().getTurnNumber() <= 2) {
            dest = getFarthestRandomNodeFromPoliceStation(agentId);
            this.path = getCheaperPath(curNodeID,dest);
        }

        if (gameView.getConfig().getTurnSettings().getVisibleTurnsList()
                .contains(gameView.getTurn().getTurnNumber())) {
            setThiefNodeIDs(gameView);
            setTarget(gameView);
            setDest(gameView);
            this.path = getCheaperPath(curNodeID,dest);
        }

        // decide next move in case of reaching destination

        return getNextProperNode(curNodeID, gameView);
    }

    private int getNextProperNode(int nodeId, GameView gameView) {
        double budget = gameView.getBalance();

        if (config.isNeighbor(target,nodeId) &&
                budget <= config.getPathCost(nodeId, target)) return target;

        int index = this.path.indexOf(nodeId);
        int next;

        if (index == path.size()-1)
            return nodeId;
        else
            next = this.path.get(index + 1);

        if (budget <= config.getPathCost(nodeId, next))
            return next;
        else {
            //System.out.println("nooooooooooo mmmmmmmmmmmmmmoneyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
            return nodeId;
        }

    }

    private ArrayList<Integer> getCheaperPath(int start , int end) {
        this.path.clear();

        int min = 9999999;
        ArrayList<Integer> temp = null;
        for (ArrayList<Integer> path : config.getAllShortestPaths(start,end)) {
            int cost = 0;
            for (int i = 1; i < path.size(); i++) {
                cost += config.getPathCost(path.get(i-1),path.get(i));
            }

            if (cost < min) {
                min = cost;
                temp = path;
            }
        }

        //System.out.println(temp);

        return temp;
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
