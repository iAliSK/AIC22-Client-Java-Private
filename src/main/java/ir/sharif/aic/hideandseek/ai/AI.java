package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.Logger;
import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Agent;
import ir.sharif.aic.hideandseek.protobuf.AIProto.AgentType;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Team;

import java.util.*;
import java.util.stream.IntStream;

enum Safety {
    LOW, MEDIUM, HIGH
}

public abstract class AI {
    protected Config config;
    protected GameView view;
    protected ArrayList<Integer> path;
    protected Phone phone;
    protected int currNodeId;
    protected int currAgentId;
    protected Logger logger;


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

    protected int getFarthestUniqueNode(int fromNodeId, double percent) {
        int[] distances = config.getMinDistances(fromNodeId);

        int maxDist = Arrays.stream(distances).max().orElse(2);

        int minDist = (int) Math.ceil(percent * maxDist);

        Integer[] farthestNodes = IntStream.range(1, distances.length + 1)
                .filter(i -> distances[i - 1] >= minDist)
                .boxed().toArray(Integer[]::new);


        ArrayList<Integer> nodes = findNodesWithMaxDiff(
                new ArrayList<>(Arrays.asList(farthestNodes)), 3
        );

        return nodes.get(Math.min(getThiefStrategy(), nodes.size() - 1));
    }

    protected int getFarthestRandomNode(int fromNodeId, double percent) {
        int[] distances = config.getMinDistances(fromNodeId);

        int maxDist = Arrays.stream(distances).max().orElse(2);

        int minDist = (int) Math.ceil(percent * maxDist);

        Integer[] farthestNodes = IntStream.range(1, distances.length + 1)
                .filter(i -> distances[i - 1] >= minDist)
                .boxed().toArray(Integer[]::new);


//        ArrayList<Integer> nodes = findNodesWithMaxDiff(
//                new ArrayList<>(Arrays.asList(farthestNodes)),
//                3
//        );

//        // sort by max neighbor nodes count
        Arrays.sort(farthestNodes, Comparator.
                comparingInt(d -> config.getNeighborNodesCount((Integer) d)).reversed());

        int randIndex = getRandInt(farthestNodes.length / 2);
        return farthestNodes[randIndex];

//        return nodes.get(Math.min(getThiefStrategy(), nodes.size() - 1));
    }

    private int getThiefStrategy() {
        return getTeammateThieves(true).stream()
                .map(Agent::getId).sorted().toList().indexOf(currAgentId);
    }

    private ArrayList<Agent> getAgents(int team, int type, boolean includeMe) {
        ArrayList<Agent> agents = new ArrayList<>();
        for (Agent agent : view.getVisibleAgentsList()) {
            if (agent.getTeamValue() == team && agent.getTypeValue() == type) {
                if (!agents.contains(agent)) {
                    agents.add(agent);
                }
            }
        }
        if (includeMe && !agents.contains(view.getViewer())) {
            agents.add(view.getViewer());
        } else if (!includeMe) {
            agents.remove(view.getViewer());
        }

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

    ArrayList<Integer> findNodesWithMaxDiff(ArrayList<Integer> nodes, int count) {

        if (count >= nodes.size()) return nodes;

        int n = nodes.size();
        int[] allNodes = new int[n];
        int c = 0;
        for (Integer nodeID : nodes) {
            allNodes[c] = nodeID;
            c++;
        }

        int[][] diff = new int[n][n];
        int max = -1;
        int m = 0, k = 0;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                diff[i][j] = config.getMinDistance(allNodes[i], allNodes[j]);
                diff[j][i] = diff[i][j];
                if (diff[i][j] > max) {
                    max = diff[i][j];
                    m = i;
                    k = j;
                }
            }
        }

        ArrayList<Integer> ans = new ArrayList<>();
        ans.add(allNodes[m]);
        ans.add(allNodes[k]);
        if (count == 2)
            return ans;

        //=======================
        nodes.sort((o1, o2) -> Integer.compare(
                config.getMinDistance(o2, ans.get(0)) * config.getMinDistance(o2, ans.get(1)),
                config.getMinDistance(o1, ans.get(0)) * config.getMinDistance(o1, ans.get(1))));

        int counter = 0;
        for (Integer node : nodes) {
            if (counter == count - 2) break;
            ans.add(node);
            counter++;
        }

        return ans;
    }

    public List<Integer> mapToAgentId(ArrayList<Agent> agents, boolean sorted) {
        if (sorted) {
            return agents.stream().map(Agent::getId).sorted().toList();
        } else {
            return agents.stream().map(Agent::getId).toList();
        }
    }

    public List<Integer> mapToNodeId(ArrayList<Agent> agents, boolean sorted) {
        if (sorted) {
            return agents.stream().map(Agent::getNodeId).sorted().toList();
        } else {
            return agents.stream().map(Agent::getNodeId).toList();
        }
    }

    public boolean isVisibleTurn() {
        return getTurnsPassedAfterLastVisibility() <= 1;
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

    public boolean isMoneyEnoughToMove(ArrayList<Integer> path) {
        double cost = 0;
        for (int i = 1; i < path.size(); i++) {
            cost += config.getPathCost(path.get(i), path.get(i - 1));
        }
        cost -= (path.size() - 1) * view.getConfig().getIncomeSettings().getThievesIncomeEachTurn();
        return cost <= view.getBalance();
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

    protected int getTurnsPassedAfterLastVisibility() {
        int currTurn = view.getTurn().getTurnNumber();
        List<Integer> visibleTurns = view.getConfig().getTurnSettings().getVisibleTurnsList();
        int i = 0;
        while (i < visibleTurns.size() && visibleTurns.get(i) <= currTurn) {
            i++;
        }
        if (i == 0) return Integer.MAX_VALUE;
        return currTurn - visibleTurns.get(i - 1);
    }

    protected int getVisibleTurnsDiff() {
        int currTurn = view.getTurn().getTurnNumber();
        List<Integer> visibleTurns = view.getConfig().getTurnSettings().getVisibleTurnsList();
        int i = 0;
        while (visibleTurns.get(i) <= currTurn) {
            i++;
        }
        if (i == 0) return visibleTurns.get(i);
        return visibleTurns.get(i) - visibleTurns.get(i - 1);
    }

    protected List<Integer> getVisibleTurns() {
        return view.getConfig().getTurnSettings().getVisibleTurnsList();
    }

    protected int getMaxTurns() {
        return view.getConfig().getTurnSettings().getMaxTurns();
    }

    protected int getNextInPath(ArrayList<Integer> path) {
        int index = path.indexOf(currNodeId);
        if (index >= 0 && index < path.size() - 1) {
            return path.get(index + 1);
        }
        return currNodeId;
    }

    protected int getLastInPath(ArrayList<Integer> path) {
        if (path.size() > 0) {
            return path.get(path.size() - 1);
        }
        return currNodeId;
    }

    protected int comparePathCost(ArrayList<Integer> p1, ArrayList<Integer> p2) {
        return Double.compare(
                config.getPathCost(p1),
                config.getPathCost(p2)
        );
    }

    protected int compareDistance(int node1, int node2, int target) {
        return Integer.compare(
                config.getMinDistance(node1, target),
                config.getMinDistance(node2, target)
        );
    }

    protected int compareNeighbors(int node1, int node2) {
        return Integer.compare(
                config.getNeighborNodesCount(node1),
                config.getNeighborNodesCount(node2)
        );
    }

}