package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.Logger;
import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Agent;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PoliceAI extends AI {

    public List<Integer> thievesLocation = new ArrayList<>();
    public int target = -1;

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
        logger.enableLogging(true);
        initPath();
        return 1;
    }

    /**
     * Implement this function to move your police agent based on current game view.
     */
    @Override
    public int move(GameView view) {
        long start = System.currentTimeMillis();

        updateGame(view);
        try {
            if (isVisibleTurn()) {
                setThievesLocation();
                target = getNearestThief();
                updatePath(target);
            }

            int nextMove = getNextInPath();

            // todo change
//            if (target > 0 && !isVisibleTurn() && isPoliceWithDist1()) {
//                updatePath(bestPossibilityForThief());
//            }

            // todo change
            if (target > 0 && !isVisibleTurn() && currNodeId == getLastInPath(path)) {
                logger.log("%d\tcatch\n", view.getTurn().getTurnNumber());
                return currNodeId;
            }

            logger.log("turn:%d\t" +
                            "curr:%d\t" +
                            "target:%d\t" +
                            "path:%s\n",
                    view.getTurn().getTurnNumber(),
                    currNodeId,
                    target,
                    path
            );

            long end = System.currentTimeMillis();

            logger.log("time: %d\n", (end - start));

            if ((end - start) > 900) {
                logger.log("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
            }

            logger.log("\n------------------------------\n");

            return nextMove;
        } catch (Exception e) {
            throw e;
        }
//        return currNodeId;
    }

    void initPath() {
        path = getCheaperPath(currNodeId,
                getFarthestRandomNodeFromPoliceStation(0.7)
        );
    }

    void updatePath(int target) {

        this.target = target;

//        int degree = Math.min(3, config.getMinDistance(currNodeId, target));
//        degree = Math.min(degree, getVisibleTurnsDiff());

        int degree = getVisibleTurnsDiff() / 2;

        logger.log("degree:%d\n", degree);

        List<Integer> neighborNodes = config.getNeighborNodes(target, degree, false);

        neighborNodes.sort(Comparator
                .comparingInt((Integer node) -> config.getMinDistance(node, target))
                .thenComparingInt(node -> -config.getNeighborNodesCount(node))
                .thenComparingInt(nodeId -> nodeId)
        );

        logger.log("targetNeighbors:%s\n", neighborNodes);

        ArrayList<Agent> polices = getTeammatePolice(true);

        polices.sort(Comparator.comparingInt((Agent agent) ->
                config.getMinDistance(agent.getNodeId(), target))
                .thenComparingInt(Agent::getId)
        );

        logger.log("polices:%s\n", mapToAgentId(polices, false));


        int myDest = -1;

        // ------------------------------------------------------------------

        ArrayList<Integer> selected = new ArrayList<>();
        ArrayList<Integer> allDest = new ArrayList<>();
        ArrayList<Integer> badNodes = new ArrayList<>();


        for (int node : neighborNodes) {
            for (Agent police : polices) {

                if (selected.contains(police.getId())) {
                    continue;
                }

                int policeDist = config.getMinDistance(police.getNodeId(), node);
                int thiefDist = config.getMinDistance(target, node);

                if (policeDist <= thiefDist + 1) {

                    boolean badNode = false;


                    for (ArrayList<Integer> path : config.getAllShortestPaths(node, target)) {
                        for (int node2 : allDest) {
                            if (path.contains(node2)) {
                                badNode = true;
                                break;
                            }
                        }
                        if (badNode) {
                            break;
                        }
                    }
                    if (badNode) {
                        badNodes.add(node);
                        break;
                    }

                    logger.log("badNodes:%s\n", badNodes);
                    badNodes.clear();


                    logger.log("agent:%d\tall:%s\n", police.getId(), config.getAllShortestPaths(node, target));

                    selected.add(police.getId());
                    allDest.add(node);

                    if (police.getId() == currAgentId) {
                        myDest = node;
                    }
                    break;
                }

//                if(myDest != -1) {
//                    break;
//                }
            }
        }

        if (willBeSurrounded(target, allDest)) {
            int killerId = selected.get(0);
            if (killerId == currAgentId) {
                myDest = target;
                logger.log("killer:%d\n", killerId);
            } else {
                // todo next target
            }
        }

        // ------------------------------------------------------------------


        if (myDest == -1) {
            // old strategy
            logger.log("agent:%d\told strategy!!\n", currAgentId);
            int index = polices.indexOf(view.getViewer());
            myDest = neighborNodes.get(index % neighborNodes.size());
        }

        logger.log("myDest:%d\tallDest: %s\n", myDest, allDest);

        path = getCheaperPath(currNodeId, myDest);
    }

    private boolean willBeSurrounded(int target, ArrayList<Integer> polices) {
        for (int node : config.getNeighborNodes(target)) {
            if (!polices.contains(node)) {
                return false;
            }
        }
        return true;
    }

    boolean isSurrounded() {
        return emptyNodes().size() == 0;
    }

    ArrayList<Integer> emptyNodes() {
        ArrayList<Agent> polices = getTeammatePolice(true);

        ArrayList<Integer> neighborNodes = config.getNeighborNodes(target);

        for (Agent node : polices) {
            if (neighborNodes.contains(node.getNodeId())) {
                neighborNodes.remove((Integer) node.getNodeId());
            }
        }

        return neighborNodes;

    }


    boolean isPoliceWithDist1() {
        ArrayList<Agent> polices = getTeammatePolice(true);

        ArrayList<Integer> neighborNodes = config.getNeighborNodes(target);

        for (Agent node : polices) {
            if (neighborNodes.contains(node.getNodeId())) {
                return true;
            }
        }

        return false;
    }

    private int getUniqueIndex() {
        return mapToAgentId(getTeammatePolice(true), true).indexOf(currAgentId);
    }

    int bestPossibilityForThief() {
        ArrayList<Agent> polices = getTeammatePolice(true);

        ArrayList<Integer> neighborNodes = config.getNeighborNodes(target);

        for (Agent node : polices) {
            neighborNodes.removeAll(config.getNeighborNodes(node.getNodeId()));
        }

        neighborNodes.sort(Comparator
                .comparingInt((Integer node) -> -getDisToAllPolices(node))
                .thenComparingInt(node -> -config.getNeighborNodesCount(node))
                .thenComparingInt(nodeId -> nodeId)
        );

        if (neighborNodes.size() == 0) return target;
        return neighborNodes.get(0);
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

    private int getNearestThief() {
        return thievesLocation.stream().min(Comparator
                .comparingInt(this::getDisToAllPolices)
                .thenComparingInt(config::getNeighborNodesCount)
                .thenComparingInt(nodeId -> nodeId)
        ).orElse(getFarthestRandomNode(currNodeId, 0.7));
    }

    private int getDisToAllPolices(int thiefLoc) {
        ArrayList<Agent> polices = getTeammatePolice(true);
        int sum = 0;
        for (Agent police : polices) {
            sum += config.getMinDistance(police.getNodeId(), thiefLoc);
        }
        return sum;
    }

    private void setThievesLocation() {
        thievesLocation = getOpponentThieves().stream()
                .filter(agent -> !agent.getIsDead())
                .map(Agent::getNodeId)
                .toList();
    }

    private void getTargets() {
        ArrayList<Agent> targets = getOpponentThieves();


    }

    private int getPathSafetyForThief(ArrayList<Integer> path) {
        int safety = 0;
        for (int i = 0; i < path.size(); i++) {
            if (getNearestTeammatePoliceDistance(path.get(i)) <= i + 1) {
                break;
            }
            safety += 1;
        }
        return safety;
    }

    private int getNearestTeammatePoliceDistance(int nodeId) {
        int minDist = Integer.MAX_VALUE;
        for (Integer node : mapToNodeId(getTeammatePolice(true), false)) {
            minDist = Math.min(minDist, config.getMinDistance(node, nodeId));
        }
        return minDist;
    }
}
