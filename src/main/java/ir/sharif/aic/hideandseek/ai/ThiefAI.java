package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.Logger;
import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Agent;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.ArrayList;
import java.util.List;

public class ThiefAI extends AI {

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
        logger = new Logger(String.format("logs/thief-%d.log", currAgentId));
        logger.enableLogging(true);
        return getFarthestRandomNodeFromPoliceStation(0.7);
    }

    /**
     * Implement this function to move your thief agent based on current game view.
     */
    @Override
    public int move(GameView view) {
        updateGame(view);
        return getNextMove();
    }

    private int getNextMove() {

//        if (path.size() > 0) {
//            if ((getAllowedNeighborNodesCount(currNodeId) >= 3
//                    && getAllowedNeighborNodesCount(path.get(path.size() - 1)) >= 3)
//                    && isVerySafePath(path)) {
//                int next = getNextInPath(path);
//                if(next != currNodeId)
//                    return next;
//            }
//        }

        List<ArrayList<Integer>> allPaths = new ArrayList<>();

        for (int i = 1; i <= config.getNodesCount(); i++) {
            allPaths.addAll(config.getAllShortestPaths(currNodeId, i));
        }


        allPaths.sort((p1, p2) -> {
            int next1 = getNextInPath(p1);
            int next2 = getNextInPath(p2);

            int compPathSafety = comparePathSafety(p1, p2);
            int compPathCost = comparePathCost(p1, p2);
            int compPathSize = comparePathSize(p1, p2);
            int compThievesCount = comparePathThievesCount(p1, p2);
//            int compWaysCount = compareNeighborNodes(next1, next2);
            int compThievesDist = compareThievesDistance(p1.get(p1.size() - 1), p2.get(p2.size() - 1));
//            int compNodeSafety = compareNodesSafety(next1, next2);
//            int compPoliceDist = comparePoliceDist(next1, next2);

            if (compPathSafety != 0) return compPathSafety;
//            if (compThievesDist != 0) return compThievesDist;
//            if (compPathSize != 0) return compPathSize;
//            if (compThievesCount != 0) return compThievesCount;

            return compPathCost;

//            if (compThievesDist != 0) return compThievesDist;
//            if (compWaysCount != 0) return compWaysCount;
//
//            if (getNearestPoliceDistance(currNodeId) <= getRemainingVisibleTurns()) {
//                if (compPathSize != 0) return compPathSize;
//                return compPathCost;
//            } else {
//                if (compPathCost != 0) return compPathCost;
//                return compPathSize;
//            }
        });

        ArrayList<ArrayList<Integer>> ignoredPaths = new ArrayList<>();
        ArrayList<ArrayList<Integer>> safePaths = new ArrayList<>();
        ArrayList<ArrayList<Integer>> verySafePaths = new ArrayList<>();

        int nextMove;

        for (ArrayList<Integer> path : allPaths) {
            int next = getNextInPath(path);

            if (next == currNodeId && getNearestPoliceDistance(currNodeId) == 1) {
                continue;
            }

            if (!isMoneyEnoughToMove(currNodeId, next)) {
                continue;
            }

            if (!isMoneyEnoughToMove(path)) {
                ignoredPaths.add(path);
                continue;
            }

            int dest = path.get(path.size() - 1);
            if (config.getNeighborNodesCount(dest) <= 2) {
                ignoredPaths.add(path);
                continue;
            }

            if (isVerySafePath(path)) {
                verySafePaths.add(path);

                if (isTeammateThiefHere(currNodeId)
                        && !isPoliceNeighbor(currNodeId)
                        && config.getNeighborNodesCount(currNodeId) >= 3) {

                    if (getRandInt(10) % 2 == 0) {
                        ignoredPaths.add(path);
                        continue;
                    }
                }

                if (isTeammateThiefHere(next)
                        && !isPoliceNeighbor(currNodeId)
                        && config.getNeighborNodesCount(currNodeId) >= 3) {

                    ignoredPaths.add(path);
                    continue;
                }
            }

            if (isSafePath(path)) {
                safePaths.add(path);
            }
        }

        path.clear();

        String type = null;

        if (safePaths.size() > 0) {
            path = safePaths.get(0);
            type = "safe";
        }

        if (verySafePaths.size() > 0 && verySafePaths.get(0).size() >= 2) {
            path = verySafePaths.get(0);
            type = "very-safe";
        }

        if (path.size() <= 1 && ignoredPaths.size() > 0) {
            ignoredPaths.sort(this::comparePathSafety);
            type = "ignored";
            path = ignoredPaths.get(0);
        }

        nextMove = getNextInPath(path);

        logger.log(
                "turn:%d\t" +
                        "curr:%d\t" +
                        "next:%d\t" +
                        "path:%s\t" +
                        "type:%s\t" +
                        "vs:%d s:%d ig:%d\t" +
                        "vs:%s\n",
                view.getTurn().getTurnNumber(),
                currNodeId,
                nextMove,
                path.toString(),
                type,
                verySafePaths.size(),
                safePaths.size(),
                ignoredPaths.size(),
                verySafePaths
        );

        return nextMove;
    }

    private int getNextInPath(ArrayList<Integer> path) {
        ArrayList<Integer> temp = this.path;
        this.path = path;
        int next = getNextInPath();
        this.path = temp;
        return next;
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

    private boolean isSafePath(ArrayList<Integer> path) {
        for (int i = 1; i < path.size(); i++) {
            if (getNearestPoliceDistance(path.get(i)) <= 1) {
                return false;
            }
        }
        return true;
    }

    private boolean isVerySafePath(ArrayList<Integer> path) {
        for (int i = 1; i < path.size(); i++) {
            if (getNearestPoliceDistance(path.get(i)) <= i) {
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

    private int getNearestTeammateThiefDistance(int nodeId) {
        int minDist = Integer.MAX_VALUE;
        for (Agent agent : getTeammateThieves(false)) {
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

    private boolean isTeammateThiefHere(int nodeId) {
        return getTeammateThieves(false).stream()
                .map(Agent::getNodeId).toList()
                .contains(nodeId);
    }

    private boolean isPoliceNeighbor(int nodeId) {
        return getNearestPoliceDistance(nodeId) <= 1;
    }

    private int comparePathSize(ArrayList<Integer> p1, ArrayList<Integer> p2) {
        return Integer.compare(p2.size(), p1.size());
    }

    private int comparePathCost(ArrayList<Integer> p1, ArrayList<Integer> p2) {
        return Double.compare(
                config.getPathCost(p1),
                config.getPathCost(p2)
        );
    }

    private int compareNeighborNodes(int node1, int node2) {
        return Integer.compare(
                getAllowedNeighborNodesCount(node2),
                getAllowedNeighborNodesCount(node1)
        );
    }

    private int compareNodesSafety(int node1, int node2) {
        return Integer.compare(
                getNearestPoliceDistance(node2),
                getNearestPoliceDistance(node1)
        );
    }

    private int getPathThievesCount(ArrayList<Integer> path) {
        int thieves = 0;
        List<Integer> thievesLoc = getTeammateThieves(false).stream().map(Agent::getNodeId).toList();
        for (int i = 1; i < path.size(); i++) {
            if (thievesLoc.contains(path.get(i))) {
                thieves++;
            }
        }
        return thieves;
    }

    private int comparePathThievesCount(ArrayList<Integer> p1, ArrayList<Integer> p2) {
        return Integer.compare(
                getPathThievesCount(p1),
                getPathThievesCount(p2)
        );
    }

    private int compareThievesDistance(int node1, int node2) {
        return Integer.compare(
                getNearestTeammateThiefDistance(node2),
                getNearestTeammateThiefDistance(node1)
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

    private int getTotalPoliceDist(int nodeId) {
        int policeDist = 0;
        List<Integer> policeLoc = getOpponentPolice().stream().map(Agent::getNodeId).toList();
        for (int node : policeLoc) {
            policeDist += config.getAllShortestPaths(nodeId, node).get(0).size();
        }
        return policeDist;
    }

    private int comparePoliceDist(int node1, int node2) {
        return Double.compare(
                getTotalPoliceDist(node2),
                getTotalPoliceDist(node1)
        );
    }

}