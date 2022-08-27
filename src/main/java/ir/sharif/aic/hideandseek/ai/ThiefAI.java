package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.Logger;
import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Agent;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        logger = new Logger(String.format("logs/thief-%d.log", currAgentId));
        logger.enableLogging(false);
        updateLastPoliceLoc();
        return getFarthestRandomNodeFromPoliceStation(0.55);
    }

    /**
     * Implement this function to move your thief agent based on current game view.
     */
    @Override
    public int move(GameView view) {
        updateGame(view);
        try {
            int nextMove = getNextMove();
            updateLastPoliceLoc();
            return nextMove;
        } catch (Exception ignored) {
        }
        return view.getViewer().getNodeId();
    }


    private void updateLastPoliceLoc() {
        for (Agent agent : getOpponentPolice()) {
            lastPoliceLoc.put(agent.getId(), agent.getNodeId());
        }
    }

    private int getUniqueIndex() {
        return mapToAgentId(getTeammateThieves(true), true).indexOf(currAgentId);
    }

    protected ArrayList<ArrayList<Integer>> getAllAllowedPaths(int fromNodeId) {
        ArrayList<ArrayList<Integer>> paths = new ArrayList<>();
        for (int i = 1; i <= config.getNodesCount(); i++) {
            for (ArrayList<Integer> path : config.getAllShortestPaths(fromNodeId, i)) {
                int next = getNextInPath(path);
                if (getNearestPoliceDistance(next) == 0) {
                    continue;
                }
                if (!isMoneyEnoughToMove(fromNodeId, next)) {
                    continue;
                }
                paths.add(path);
            }
        }
        return paths;
    }

    private void arrangeByFirstStrategy(ArrayList<ArrayList<Integer>> paths, int fromNodeId) {
        paths.sort((p1, p2) -> {
            int next1 = getNextInPath(p1);
            int next2 = getNextInPath(p2);
            int last1 = p1.get(p1.size() - 1);
            int last2 = p2.get(p2.size() - 1);

            int compPathSafety = comparePathSafety(p1, p2);
            int compPathCost = comparePathCost(p1, p2);
            int compDistFromMe = compDistFromMe(last1, last2);
            int compLastVisibleNodeDist = compareLastVisibleNodeId(last1, last2);
            int compAllowedNodes = compAllowedNodes(next1, next2);
            int compThiefDist = compThiefDist(next1, next2);

            if (getNearestPoliceDistance(fromNodeId) >= 3 && getNearestThiefDistance(fromNodeId) <= 2) {
                if (compThiefDist != 0) return compThiefDist;
            }

            if (getNearestPoliceDistance(fromNodeId) >= 4
                    && incomingPoliceCount(fromNodeId) <= 1) {
                if (compPathCost != 0) return compPathCost;
            }

            if (compPathSafety != 0) return compPathSafety;

            if (getNearestPoliceDistance(fromNodeId) <= 2) {
                if (compAllowedNodes != 0) return compAllowedNodes;
            }

            switch (getUniqueIndex()) {
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
    }

    private int countNeighborPolices(int nodeId) {
        int count = 0;
        List<Integer> polices = mapToNodeId(getOpponentPolice(), false);
        List<Integer> neighbors = config.getNeighborNodes(nodeId);
        for (int next : neighbors) {
            if (polices.contains(next)) {
                count++;
            }
        }
        return count;
    }

    private int compPoliceCount(int node1, int node2) {
        return Integer.compare(countNeighborPolices(node1),
                countNeighborPolices(node2));
    }

    private ArrayList<Integer> arrangeBySecondStrategy(ArrayList<ArrayList<Integer>> paths) {
        List<ArrayList<Integer>> filteredPaths = paths.stream()
                .filter(p -> getNearestPoliceDistance(getNextInPath(p)) > 0)
                .sorted((p1, p2) -> {
                    int next1 = getNextInPath(p1);
                    int next2 = getNextInPath(p2);
                    int compPathSafety = comparePathSafety(p1, p2);
                    int compPoliceCount = compPoliceCount(next1, next2);
                    if (compPathSafety != 0) return compPathSafety;
                    return compPoliceCount;
                }).toList();

        logger.log("turn:%d\t" +
                        "filtered:%s\n",
                view.getTurn().getTurnNumber(),
                filteredPaths.toString()
        );
        return filteredPaths.get(0);
    }

    private ArrayList<Integer> selectBestPath(ArrayList<ArrayList<Integer>> paths) {
        ArrayList<Integer> path;
        if (isTeammateThiefHere(currNodeId) && getNearestPoliceDistance(currNodeId) >= 3) {
            path = paths.get(Math.min(getUniqueIndex(), paths.size() - 1));
        } else {
            path = paths.get(0);
        }
        return path;
    }

    private int getNextMove() {

        Grid.filter = new ArrayList<>(mapToNodeId(getOpponentPolice(), false));

        if (getTurnsPassedAfterLastVisibility() <= 1) {
            visibleNodeId = currNodeId;
        }

        ArrayList<ArrayList<Integer>> paths;

        paths = getAllAllowedPaths(currNodeId);
        arrangeByFirstStrategy(paths, currNodeId);
        path = selectBestPath(paths);

        int nextMove = getNextInPath(path);

        boolean sec = false;
        if (getNearestPoliceDistance(nextMove) <= 1) {
            sec = true;
            path = arrangeBySecondStrategy(paths);
        }

        int safety = getPathSafetyLength(path);
        int last = path.get(safety - 1);
        if (config.getNeighborNodesCount(last) <= 2) {
            for (ArrayList<Integer> path : paths) {
                safety = getPathSafetyLength(path);
                last = path.get(safety - 1);
                if (config.getNeighborNodesCount(last) >= 3
                        && getPathSafetyLength(path) >= 3) {
                    this.path = path;
                    break;
                }
            }
        }

        nextMove = getNextInPath(path);

        logger.log(
                "turn:%d\t" +
                        "paths:%d\t" +
                        "coming:%d\t" +
                        "police:%d\t" +
                        "sec:%b\t" +
                        "curr:%3d\t" +
                        "next:%3d\t" +
                        "safe:%3d\t" +
                        "pass:%2d\t" +
                        "path:%s\n",
                view.getTurn().getTurnNumber(),
                paths.size(),
                incomingPoliceCount(currNodeId),
                countNeighborPolices(currNodeId),
                sec,
                currNodeId,
                nextMove,
                getPathSafetyLength(path),
                getTurnsPassedAfterLastVisibility(),
                path
        );

        return nextMove;
    }

    private boolean isTeammateThiefHere(int nodeId) {
        return mapToNodeId(getTeammateThieves(false), false).contains(nodeId);
    }

    private int getNearestThiefDistance(int nodeId) {
        int minDist = Integer.MAX_VALUE;
        for (Agent agent : getTeammateThieves(false)) {
            minDist = Math.min(
                    minDist,
                    config.getMinDistance(nodeId, agent.getNodeId())
            );
        }
        return minDist;
    }

    private int compThiefDist(int node1, int node2) {
        return Integer.compare(
                getNearestThiefDistance(node2),
                getNearestThiefDistance(node1)
        );
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

