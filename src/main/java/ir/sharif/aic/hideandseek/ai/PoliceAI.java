package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.Logger;
import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Agent;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class PoliceAI extends AI {

    public List<Integer> thievesLocation = new ArrayList<>();
    public int target = -1;
    private int nextTarget = -1;

    public PoliceAI(Phone phone) {
        this.phone = phone;
    }

    /**
     * This function always returns zero (Polices can not set their starting node).
     */
    @Override
    public int getStartingNode(GameView view) {
        updateGame(view);
        logger = new Logger(String.format("logs/police-%d.log", currAgentId));
        logger.enableLogging(false);
        initPath();
        return 1;
    }

    /**
     * Implement this function to move your police agent based on current game view.
     */
    @Override
    public int move(GameView view) {
        updateGame(view);

        logger.log("-------------\n");

        if (isVisibleTurn()) {
            setThievesLocation();
            target = getNearestThief();
            updatePath(target);
            logger.log("turn:%d\tvisible\n", view.getTurn().getTurnNumber());
        }

        if (getTurnsPassedAfterLastVisibility() == 2) {
            nextTarget = getSafestDestForThief(target);
            logger.log("turn:%d\tnext target updated:%d\n", view.getTurn().getTurnNumber(), nextTarget);
        }

        if (target > 0 && !isVisibleTurn() && isPoliceWithDis1()) {
            if (nextTarget != -1) {
                updatePath(nextTarget);
                logger.log("turn:%d\tnextTarget:%d\n", view.getTurn().getTurnNumber(), nextTarget);
            } else {
                int best = bestPossibilityForThief();
                updatePath(best);
                logger.log("turn:%d\tnoTarget:%d\n", view.getTurn().getTurnNumber(), best);
            }
        }

        logger.log("-------------\n\n");

        return getNextInPath();
    }

    boolean isSurrounded() {
        return emptyNodes().size() == 0;
    }

    ArrayList<Integer> emptyNodes() {
        ArrayList<Agent> polices = getTeammatePolice(false);

        ArrayList<Integer> neighborNodes = config.getNeighborNodes(target);

        for (Agent node : polices) {
            if (neighborNodes.contains(node.getNodeId()))
                neighborNodes.remove((Integer) node.getNodeId());
        }

        return neighborNodes;

    }

    boolean isPoliceWithDis1() {
        ArrayList<Agent> polices = getTeammatePolice(false);

        ArrayList<Integer> neighborNodes = config.getNeighborNodes(target);

        for (Agent node : polices) {
            if (neighborNodes.contains(node.getNodeId()))
                return true;
        }

        return false;
    }

    private int getStrategy() {
        return getTeammatePolice(true).stream()
                .map(Agent::getId).sorted().toList()
                .indexOf(currAgentId);
    }

    int bestPossibilityForThief() {
        ArrayList<Agent> polices = getTeammatePolice(false);

        ArrayList<Integer> neighborNodes = config.getNeighborNodes(target);

        for (Agent node : polices) {
            neighborNodes.removeAll(config.getNeighborNodes(node.getNodeId()));
        }

        neighborNodes.sort((o1, o2) -> {
            int temp1 = Integer.compare(getDisToAllPolices(o2), getDisToAllPolices(o1));
            if (temp1 != 0) return temp1;
            int temp2 = Integer.compare(config.getNeighborNodesCount(o2), config.getNeighborNodesCount(o1));
            if (temp2 != 0) return temp2;
            return Integer.compare(o1, o2);
        });

        if (neighborNodes.size() == 0) return target;
        return neighborNodes.get(0);
    }

    void initPath() {
        path = getCheaperPath(currNodeId,
                getFarthestRandomNodeFromPoliceStation(0.7)
        );
    }

    void updatePath(int tg) {

        target = tg;

//        ArrayList<Integer> distances = new ArrayList<>();
//        for (Agent police : polices) {
//            distances.add(config.getMinDistance(police.getNodeId(),target));
//        }
//
//        distances.sort(Integer::compare);
//
//        int min = distances.get(0);
//        int max = distances.get(distances.size()-1);
//
//        int rem = (getRemainingVisibleTurns() - view.getTurn().getTurnNumber()) / 2;
//        int targetNeighbors = config.getNeighborNodesCount(target);

        //====================================================================================================

//        ArrayList<Integer> targetsTemp = new ArrayList<>();
//        targetsTemp.add(target);
//
//        ArrayList<Integer> neighborNodesTemp = new ArrayList<>(config.getNeighborNodes
//                (targetsTemp,3,new HashSet<>(targetsTemp),false));
//
//        neighborNodesTemp.sort((o1, o2) -> {
//            int temp1 = Integer.compare(config.getMinDistance(o1, target), config.getMinDistance(o2, target));
//            if (temp1 != 0) return temp1;
//            int temp2 = Integer.compare(config.getNeighborNodesCount(o2), config.getNeighborNodesCount(o1));
//            if (temp2 != 0) return temp2;
//            return Integer.compare(o1, o2);
//        });

        //ArrayList<Integer> neighborNodes = config.getNearestNodes(target);

        //====================================================================================================

        ArrayList<Integer> targets = new ArrayList<>();
        targets.add(target);

        ArrayList<Integer> neighborNodes = new ArrayList<>(config.getNeighborNodes(targets, 3, new HashSet<>(targets), false));

        neighborNodes.sort((o1, o2) -> {
            int temp1 = Integer.compare(config.getMinDistance(o1, target), config.getMinDistance(o2, target));
            if (temp1 != 0) return temp1;
            int temp2 = Integer.compare(config.getNeighborNodesCount(o2), config.getNeighborNodesCount(o1));
            if (temp2 != 0) return temp2;
            return Integer.compare(o1, o2);
        });

        //ArrayList<Integer> neighborNodes = config.getNearestNodes(target);

        //======================================================================================================

        ArrayList<Agent> polices = getTeammatePolice(true);

        polices.sort((Comparator.comparingInt
                ((Agent o) -> config.getMinDistance(o.getNodeId(), target))
                .thenComparingInt(Agent::getId)));

        int index = polices.indexOf(view.getViewer());


        int dest = neighborNodes.get(index % neighborNodes.size());


        //if (config.getMinDistance(currNodeId,target) <= 2) dest = currNodeId;

        path = getCheaperPath(currNodeId, dest);


        logger.log(
                "turn:%d\t" +
                        "curr:%d\t" +
                        "target:%d\t" +
                        "path:%s\t" +
                        "thief-safest:%d\t" +
                        "polices:%s\t" +
                        "index:%d\t" +
                        "neigh:%s\n",
                view.getTurn().getTurnNumber(),
                currNodeId,
                target,
                path.toString(),
                getSafestDestForThief(target),
                mapToNodeId(polices, false),
                index,
                neighborNodes.toString()
        );
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

//    private int getNextBeginning() {
//        int target = getNearestThief();
//        ArrayList<Integer> nodes = config.getNeighborNodes(currNodeId);
//        nodes.sort(Comparator.comparingDouble((Integer o) -> config.getPathCost(o, currNodeId))
//                .thenComparingInt(o -> config.getMinDistance(o, target)));
//        if (config.getPathCost(nodes.get(0), currNodeId) <= view.getBalance()) return nodes.get(0);
//        return currNodeId;
//    }

    private int getNearestThief() {
        return thievesLocation.stream().min((o1, o2) -> {

            int temp1 = Integer.compare(getDisToAllPolices(o1), getDisToAllPolices(o2));
            if (temp1 != 0) return temp1;
            int temp2 = Integer.compare(config.getNeighborNodesCount(o1), config.getNeighborNodesCount(o2));
            if (temp2 != 0) return temp2;
            return Integer.compare(o1, o2);
        })
                .orElse(getFarthestRandomNodeFromPoliceStation(0.7));
    }

    private int getDisToAllPolices(int thiefLoc) {
        ArrayList<Agent> polices = getTeammatePolice(false);
        int sum = 0;
        for (Agent police : polices) {
            sum += config.getMinDistance(police.getNodeId(), thiefLoc);
        }
        return sum;
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

    private int getNextInPathForThief(ArrayList<Integer> path) {
        if (path.size() >= 2) {
            return path.get(1);
        }
        return path.get(0);
    }

    private int getSafestDestForThief(int nodeId) {
        ArrayList<ArrayList<Integer>> paths = new ArrayList<>();
        List<Integer> teammatePolices = mapToNodeId(getTeammatePolice(true), false);
        ArrayList<Integer> filter = Grid.filter;
        Grid.filter = new ArrayList<>(teammatePolices);
        for (int i = 1; i <= config.getNodesCount(); i++) {
            for (ArrayList<Integer> path : config.getAllShortestPaths(nodeId, i)) {
                int next = getNextInPathForThief(path);
                if (mapToNodeId(getTeammatePolice(true), false).contains(next)) {
                    continue;
                }
                paths.add(path);
            }
        }
//        Grid.filter.clear();
        Grid.filter = filter;

        paths.sort((p1, p2) -> {
            int compPathSafety = comparePathSafetyForThief(p1, p2);
            int compPathCost = comparePathCost(p1, p2);
            if (compPathSafety != 0) return compPathSafety;
            return compPathCost;
        });

        if (paths.size() == 0) {
            return -1;
        }
        if (paths.get(0).size() == 0) {
            return -1;
        }


//        if(getTurnsPassedAfterLastVisibility() == 2) {
//            logger.log("paths:%d\ttPath:%s\tsafety:%d\n",
//                    paths.size(), paths.get(0), getPathSafetyLengthForThief(paths.get(0)));
//        }

        ArrayList<Integer> safestPath = paths.get(0);
        return safestPath.get(safestPath.size() - 1);
    }

    private int getNearestDistanceToOurPolices(int nodeId) {
        int minDist = Integer.MAX_VALUE;
        for (Agent agent : getTeammatePolice(true)) {
            minDist = Math.min(
                    minDist,
                    config.getMinDistance(nodeId, agent.getNodeId())
            );
        }
        return minDist;
    }

    private int getPathSafetyLengthForThief(ArrayList<Integer> path) {
        int safetyLength = 0;
        for (int i = 0; i < path.size(); i++) {
            if (getNearestDistanceToOurPolices(path.get(i)) <= i) {
                break;
            }
            safetyLength += 1;
        }
        return safetyLength;
    }

    private int comparePathSafetyForThief(ArrayList<Integer> p1, ArrayList<Integer> p2) {
        return Integer.compare(
                getPathSafetyLengthForThief(p2),
                getPathSafetyLengthForThief(p1)
        );
    }
}
