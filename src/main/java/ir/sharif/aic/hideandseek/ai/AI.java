package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Agent;
import ir.sharif.aic.hideandseek.protobuf.AIProto.AgentType;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Team;

import java.util.*;
import java.util.stream.IntStream;

public abstract class AI {
    protected Config config;
    protected GameView view;
    protected ArrayList<Integer> path;
    protected Phone phone;
    protected int currNodeId;
    protected int currAgentId;
    protected int lastNodeId;


    public abstract int getStartingNode(GameView view);

    public abstract int move(GameView view);

    protected void updateGame(GameView view) {
        this.config = Config.getInstance(view);
        this.view = view;
        this.currNodeId = view.getViewer().getNodeId();
        this.currAgentId = view.getViewer().getId();
    }

    protected int getFarthestRandomNodeFromPoliceStation(double percent) {
        return getFarthestRandomNode(1, percent);
    }

    protected int getFarthestRandomNode(int fromNodeId, double percent) {
        int[] distances = config.getMinDistances(fromNodeId);

        int maxDist = Arrays.stream(distances).max().orElse(2);

        int minDist = (int) (percent * maxDist);

        Integer[] farthestNodes = IntStream.range(1, distances.length + 1)
                .filter(i -> distances[i - 1] >= minDist)
                .boxed().toArray(Integer[]::new);


        // sort by max neighbor nodes count
        Arrays.sort(farthestNodes, Comparator.
                comparingInt(d -> config.getNeighborNodesCount((Integer) d)).reversed());


        int randIndex = getRandInt(farthestNodes.length / 2);
        return farthestNodes[randIndex];
    }

    private ArrayList<Agent> getAgents(int team, int type, boolean includeMe) {
        ArrayList<Agent> agents = new ArrayList<>();
        for (Agent agent : view.getVisibleAgentsList()) {
            if (agent.getTeamValue() == team && agent.getTypeValue() == type) {
                agents.add(agent);
            }
        }
        if (includeMe) agents.add(view.getViewer());
        return agents;
    }

    public ArrayList<Agent> getTeammatePolice(boolean includeMe) {
        return getAgents(view.getViewer().getTeamValue(),
                AgentType.POLICE_VALUE, includeMe
        );
    }

    public ArrayList<Agent> getTeammateThieves(boolean includeMe) {
        return getAgents(view.getViewer().getTeamValue(),
                AgentType.THIEF_VALUE, includeMe
        );
    }


    public ArrayList<Agent> getOpponentPolice() {
        int team = (view.getViewer().getTeamValue() == Team.FIRST_VALUE) ?
                Team.SECOND_VALUE : Team.FIRST_VALUE;
        return getAgents(team,
                AgentType.POLICE_VALUE, false
        );
    }

    public ArrayList<Agent> getOpponentThieves() {
        int team = (view.getViewer().getTeamValue() == Team.FIRST_VALUE) ?
                Team.SECOND_VALUE : Team.FIRST_VALUE;
        return getAgents(team,
                AgentType.THIEF_VALUE, false
        );
    }


    public boolean isVisibleTurn() {
        return view.getConfig().getTurnSettings().getVisibleTurnsList()
                .contains(view.getTurn().getTurnNumber());
    }

    public ArrayList<Integer> getCheaperPath(int fromNodeId, int toNodeId) {

        int min = Integer.MAX_VALUE;
        ArrayList<Integer> temp = null;
        for (ArrayList<Integer> path : config.getAllShortestPaths(fromNodeId, toNodeId)) {
            int cost = 0;
            for (int i = 1; i < path.size(); i++) {
                cost += config.getPathCost(path.get(i - 1), path.get(i));
            }

            if (cost < min) {
                min = cost;
                temp = path;
            }
        }

        return temp;
    }

    public int getRandInt(int bound) {
        Random random = new Random();
        random.setSeed(currAgentId + random.nextInt());
        return random.nextInt(bound);
    }

    public boolean isMoneyEnoughToMove(int fromNodeId, int toNodeId) {
        return config.getPathCost(fromNodeId, toNodeId) <= view.getBalance();
    }

    protected int getRemainingVisibleTurns() {
        int currTurn = view.getTurn().getTurnNumber();
        List<Integer> visibleTurns = view.getConfig().getTurnSettings().getVisibleTurnsList();
        int i = 0;
        while (visibleTurns.get(i) <= currTurn) {
            i++;
        }
        return visibleTurns.get(i) - currTurn;
    }

}
