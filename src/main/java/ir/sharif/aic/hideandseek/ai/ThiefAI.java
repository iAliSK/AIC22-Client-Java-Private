package ir.sharif.aic.hideandseek.ai;

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
        lastNodeId = getFarthestRandomNodeFromPoliceStation(0.8);
        return lastNodeId;
    }

    /**
     * Implement this function to move your thief agent based on current game view.
     */
    @Override
    public int move(GameView view) {
        updateGame(view);
        lastNodeId = getNextMove();
        return lastNodeId;
    }

    private int getNextMove() {
        List<ArrayList<Integer>> allPaths = new ArrayList<>();

        for (int i = 1; i <= config.getNodesCount(); i++) {
            allPaths.addAll(config.getAllShortestPaths(currNodeId, i));
        }

        allPaths = allPaths.stream().
                sorted((p1, p2) -> {
                    int dist1 = getNearestTeammateThiefDistance(getNextInPath(p1));
                    int dist2 = getNearestTeammateThiefDistance(getNextInPath(p1));
                    if (dist1 == dist2) {
                        if (getNearestPoliceDistance(currNodeId) <= getRemainingVisibleTurns()) {
                            if (p1.size() == p2.size()) {
                                double cost1 = config.getPathCost(p1);
                                double cost2 = config.getPathCost(p2);
                                return Double.compare(cost1, cost2);
                            }
                            return Integer.compare(p2.size(), p1.size());
                        } else {
                            double cost1 = config.getPathCost(p1);
                            double cost2 = config.getPathCost(p2);
                            if (cost1 == cost2) {
                                return Integer.compare(p2.size(), p1.size());
                            }
                            return Double.compare(cost1, cost2);
                        }
                    }
                    return Integer.compare(dist2, dist1);
                }).toList();

        for (ArrayList<Integer> path : allPaths) {
            if (isSafePath(path)) {
                this.path = path;
                int next = getNextInPath();

                if (next == currNodeId && getNearestPoliceDistance(currNodeId) == 1) {
                    continue;
                }

                if (next == lastNodeId) {
                    continue;
                }

                if (getTeammateThieves(false).stream()
                        .map(Agent::getNodeId)
                        .toList()
                        .contains(next)
                ) {
                    continue;
                }

                if (isMoneyEnoughToMove(currNodeId, next)) {
                    return next;
                }

            }
        }

        return currNodeId;
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

    private int nextMoveComparator(int fromNodeId, int toNodeId1, int toNodeId2) {
        int dist1 = getNearestPoliceDistance(toNodeId1);
        int dist2 = getNearestPoliceDistance(toNodeId2);
        if (dist1 == dist2 || Math.min(dist1, dist2) > 2) {
            int ways1 = config.getNeighborNodesCount(toNodeId1);
            int ways2 = config.getNeighborNodesCount(toNodeId2);
            if (ways1 == ways2) {
                double cost1 = config.getPathCost(fromNodeId, toNodeId1);
                double cost2 = config.getPathCost(fromNodeId, toNodeId2);
                return Double.compare(cost1, cost2);
            }
            return Integer.compare(ways2, ways1);
        }
        return Integer.compare(dist2, dist1);
    }

//    private int getSafestNode(int nodeId) {
//        ArrayList<Integer> neighborNodes = config.getNeighborNodes(nodeId);
//        neighborNodes.add(nodeId);
//
//        neighborNodes.sort((d1, d2) -> nextMoveComparator(nodeId, d1, d2));
//
//        for (int node : neighborNodes) {
//            if (config.getPathCost(nodeId, node) <= view.getBalance()) {
//                return node;
//            }
//        }
//        return nodeId;
//
//    }
//
//    private int getPathSafety(ArrayList<Integer> path) {
//        int safety = Integer.MAX_VALUE;
//        for (int i = 1; i < path.size(); i++) {
//            safety = Math.min(
//                    safety,
//                    getNearestPoliceDistance(path.get(i))
//            );
//            safety = Math.min(
//                    safety,
//                    getNearestTeammateThiefDistance(path.get(i))
//            );
//        }
//        return safety;
//    }

}