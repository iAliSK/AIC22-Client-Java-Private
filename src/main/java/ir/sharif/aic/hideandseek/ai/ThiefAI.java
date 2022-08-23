package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.Logger;
import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Agent;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.*;
import java.util.stream.IntStream;

public class ThiefAI extends AI {

    int visibleNodeId = 1;
    HashMap<Integer, Integer> lastPoliceLoc = new HashMap<>();

    public ThiefAI(Phone phone) {
        this.phone = phone;
    }

    /**
     * Used to select the starting node.
     */
    @Override
    public int getStartingNode(GameView view) {
        updateGame(view);
        path = new ArrayList<>();
//        logger = new Logger(String.format("logs/thief-%d.log", currAgentId));
//        logger.enableLogging(true);
        updateLastPoliceLog();
        return getFarthestRandomNodeFromPoliceStation(0.7);
    }

    /**
     * Implement this function to move your thief agent based on current game view.
     */
    @Override
    public int move(GameView view) {
        updateGame(view);
        int nextMove = getNextMove();
        updateLastPoliceLog();
        return nextMove;
    }

    private void updateLastPoliceLog() {
        for (Agent agent : getOpponentPolice()) {
            lastPoliceLoc.put(agent.getId(), agent.getNodeId());
        }
    }

    private int getStrategy() {
        return getTeammateThieves(true).stream()
                .map(Agent::getId).sorted().toList().indexOf(currAgentId);
    }

//    protected int getFarthestUniqueNode(int fromNodeId) {
//        int[] distances = config.getMinDistances(fromNodeId);
//
//        int maxDist = Arrays.stream(distances).max().orElse(2);
//
//        int minDist = (int) Math.ceil(0.7 * maxDist);
//
//        Integer[] farthestNodes = IntStream.range(1, distances.length + 1)
//                .filter(i -> distances[i - 1] >= minDist)
//                .boxed().toArray(Integer[]::new);
//
//
//        // sort by max neighbor nodes count
//        Arrays.sort(farthestNodes, Comparator.
//                comparingInt(d -> config.getNeighborNodesCount((Integer) d)).reversed());
//
//
//        int index = Math.min(getStrategy(), farthestNodes.length - 1);
//        return farthestNodes[index];
//    }

    private int getNextMove() {

        Grid.filter = new ArrayList<>(getOpponentPolice().stream().map(Agent::getNodeId).toList());

        if (incomingPoliceCount(currNodeId) <= 1
                && getNearestPoliceDistance(currNodeId) >= 4
                && getAllowedNeighborNodesCount(currNodeId) >= 4) {
            return currNodeId;
        }

        if (getNearestPoliceDistance(currNodeId) >= 4
                && getTurnsPassedAfterLastVisibility() == Integer.MAX_VALUE) {
            return currNodeId;
        }

        if (getTurnsPassedAfterLastVisibility() <= 1) {
            visibleNodeId = currNodeId;
        }

        List<ArrayList<Integer>> paths = new ArrayList<>();

        int nextMove;
        for (int i = 1; i <= config.getNodesCount(); i++) {
            for (ArrayList<Integer> path : config.getAllShortestPaths(currNodeId, i)) {
                int next = getNextInPath(path);
                if (getNearestPoliceDistance(next) == 0 || !isMoneyEnoughToMove(currNodeId, next)) {
                    continue;
                }
//                if (getNearestPoliceDistance(currNodeId) > 1
//                        && getNearestPoliceDistance(next) == 1
//                        && getTurnsPassedAfterLastVisibility() <= 1) {
//                    continue;
//                }
                paths.add(path);
            }
        }

        paths.sort((p1, p2) -> {
            int next1 = getNextInPath(p1);
            int next2 = getNextInPath(p2);
            int last1 = p1.get(p1.size() - 1);
            int last2 = p2.get(p2.size() - 1);

            int compPathSafety = comparePathSafety(p1, p2);
            int compPathCost = comparePathCost(p1, p2);
            int compDistFromMe = compDistFromMe(last1, last2);
            int compLastVisibleNodeDist = compareLastVisibleNodeId(last1, last2);
            int compAllowedNodes = compAllowedNodes(last1, last2);

            if (getNearestPoliceDistance(currNodeId) >= 4
                    && incomingPoliceCount(currNodeId) <= 1) {
                if (compPathCost != 0) {
                    return compPathCost;
                }
            }

            // todo for dest with 2 neighbors
            if (compPathSafety != 0) return compPathSafety;

            if (getNearestPoliceDistance(currNodeId) <= 2) {
                return compAllowedNodes;
            }

            switch (getStrategy()) {
                case 0 -> {
                    if (compAllowedNodes != 0) return compAllowedNodes;
                }
                case 1 -> {
                    if (compLastVisibleNodeDist != 0) return compLastVisibleNodeDist;
                }
                case 2 -> {
                    if (compDistFromMe != 0) return compDistFromMe;
                }
            }
            return compPathCost;
        });


        if (isTeammateThiefHere(currNodeId) && getNearestPoliceDistance(currNodeId) >= 3) {
            path = paths.get(Math.min(getStrategy(), paths.size() - 1));
        } else {
            path = paths.get(0);
        }

        nextMove = getNextInPath(path);

//        logger.log(
//                "turn:%d\t" +
//                        "paths:%d\t" +
//                        "coming:%d\t" +
//                        "curr:%3d\t" +
//                        "next:%3d\t" +
//                        "safe:%3d\t" +
//                        "pass:%2d\t" +
//                        "path:%s\n",
//                view.getTurn().getTurnNumber(),
//                paths.size(),
//                incomingPoliceCount(currNodeId),
//                currNodeId,
//                nextMove,
//                getPathSafetyLength(path),
//                getTurnsPassedAfterLastVisibility(),
//                path
//        );

        return nextMove;
    }

    private boolean isTeammateThiefHere(int nodeId) {
        return getTeammateThieves(false).stream()
                .map(Agent::getNodeId).toList()
                .contains(nodeId);
    }

    private int getNextInPath(ArrayList<Integer> path) {
        int index = path.indexOf(currNodeId);
        if (index >= 0 && index < path.size() - 1) {
            return path.get(index + 1);
        }
        return currNodeId;
    }


    private boolean isSafePath(ArrayList<Integer> path, boolean very) {
        for (int i = 1; i < path.size(); i++) {
            if (getNearestPoliceDistance(path.get(i)) <= (very ? i : 1)) {
                return false;
            }
        }
        return true;
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

    private int getAllowedNeighborNodesCount(int nodeId) {
        double nextBalance = view.getBalance()
                - config.getPathCost(currNodeId, nodeId)
                + view.getConfig().getIncomeSettings().getThievesIncomeEachTurn()
                * getNearestPoliceDistance(nodeId);

        int allowedNeighbors = 0;
        for (int node : config.getNeighborNodes(nodeId)) {
            if (config.getPathCost(nodeId, node) <= nextBalance) {
                allowedNeighbors++;
            }
        }
        return allowedNeighbors;
    }

    private int compAllowedNodes(int node1, int node2) {
        return Integer.compare(
                getAllowedNeighborNodesCount(node2),
                getAllowedNeighborNodesCount(node1)
        );
    }

    private int comparePathCost(ArrayList<Integer> p1, ArrayList<Integer> p2) {
        return Double.compare(
                config.getPathCost(p1),
                config.getPathCost(p2)
        );
    }

    private int getPathSafetyLength(ArrayList<Integer> path) {
        int safetyLength = 0;
        for (int i = 0; i < path.size(); i++) {
            if (getNearestPoliceDistance(path.get(i)) <= i) {
                break;
            }
            safetyLength += 1;
        }
        return safetyLength;
    }

    private int comparePathSafety(ArrayList<Integer> p1, ArrayList<Integer> p2) {
        return Integer.compare(
                getPathSafetyLength(p2),
                getPathSafetyLength(p1)
        );
    }

    private int compareLastVisibleNodeId(int node1, int node2) {
        return Integer.compare(
                config.getMinDistance(node2, visibleNodeId),
                config.getMinDistance(node1, visibleNodeId)
        );
    }

    int compDistFromMe(int node1, int node2) {
        return Integer.compare(
                config.getMinDistance(currNodeId, node2),
                config.getMinDistance(currNodeId, node1)
        );
    }

    private ArrayList<ArrayList<Integer>> getIncomingPolicePaths() {
        ArrayList<ArrayList<Integer>> paths = new ArrayList<>();

        for (Agent agent : getOpponentPolice()) {

            int lastNodeId = lastPoliceLoc.get(agent.getId());
            for (ArrayList<Integer> path :
                    config.getAllShortestPaths(currNodeId, lastNodeId)) {
                if (path.contains(agent.getNodeId())) {
                    paths.add(path);
                }
            }

        }
        return paths;
    }

    private int incomingPoliceCount(int nodeId) {
        int incoming = 0;
        for (Map.Entry<Integer, Integer> entry : lastPoliceLoc.entrySet()) {
            int id = entry.getKey();
            int lastNodeId = entry.getValue();
            for (Agent agent : getOpponentPolice()) {
                if (agent.getId() == id) {
                    if (config.getMinDistance(agent.getNodeId(), nodeId) <
                            config.getMinDistance(lastNodeId, nodeId)) {
                        incoming++;
                    }
                }
            }
        }
        return incoming;
    }

}

