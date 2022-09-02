package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.Logger;
import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto;
import ir.sharif.aic.hideandseek.protobuf.AIProto.Agent;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PoliceAI extends AI {

    public List<Agent> thievesLocation = new ArrayList<>();
    public int target = -1;
    boolean isTargetJoker = false;

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
//        long start = System.currentTimeMillis();

        try {
            if (isVisibleTurn()) {
                setThievesLocation();
                for (Agent agent : thievesLocation) {
                    if (config.getNeighborNodes(agent.getNodeId(),1).contains(currNodeId)) {
                        if (isMoneyEnoughToMove(currNodeId, agent.getNodeId())) {
//                            thievesLocation.remove(node);
//                            target = getNearestThief();
//                            updatePath(target);
                            return agent.getNodeId();
                        }
                    }
                }
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

//            logger.log("turn:%d\t" +
//                            "curr:%d\t" +
//                            "target:%d\t" +
//                            "path:%s\n",
//                    view.getTurn().getTurnNumber(),
//                    currNodeId,
//                    target,
//                    path
//            );
//
//            long end = System.currentTimeMillis();
//
//            logger.log("time: %d\n", (end - start));
//
//            if ((end - start) > 900) {
//                logger.log("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
//            }
//
//            logger.log("\n------------------------------\n");

            return nextMove;
        } catch (Exception e) {
        }
        return currNodeId;
    }

    void initPath() {
        path = getCheaperPath(currNodeId,
                getFarthestRandomNodeFromPoliceStation(0.7), 1
        );
    }

    void updatePath(int target) {

        this.target = target;

//        int degree = Math.min(3, config.getMinDistance(currNodeId, target));
//        degree = Math.min(degree, getVisibleTurnsDiff());

        int degree = getVisibleTurnsDiff() / 2;

//        logger.log("degree:%d\n", degree);

        List<Integer> neighborNodes = config.getNeighborNodes(target, degree, false, 1);

        neighborNodes.sort(Comparator
                .comparingInt((Integer node) -> config.getMinDistance(node, target, 1))
                .thenComparingInt(node -> -config.getNeighborNodesCount(node, 1))
                .thenComparingInt(nodeId -> nodeId)
        );

//        logger.log("targetNeighbors:%s\n", neighborNodes);

        ArrayList<Agent> polices = new ArrayList<>();

        for (Agent agent: getTeammatePolice(true)) {
            if (isVisibleToPolice(agent.getNodeId(),target,isTargetJoker))
                polices.add(agent);
        }

        if (!polices.contains(view.getViewer())) polices.add(view.getViewer());

        polices.sort(Comparator.comparingInt((Agent agent) ->
                config.getMinDistance(agent.getNodeId(), target, 1))
                .thenComparingInt(Agent::getId)
        );

//        logger.log("polices:%s\n", mapToAgentId(polices, false));


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

                int policeDist = config.getMinDistance(police.getNodeId(), node, 1);
                int thiefDist = config.getMinDistance(target, node, 1);

                if (policeDist <= thiefDist + 1) {

                    boolean badNode = false;

                    for (ArrayList<Integer> path : config.getAllShortestPaths(node, target,1)) {
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

//                    logger.log("badNodes:%s\n", badNodes);
                    badNodes.clear();

//                    logger.log("agent:%d\tdest:%d\n", police.getId(), node);

                    selected.add(police.getId());
                    allDest.add(node);

                    if (police.getId() == currAgentId) {
                        myDest = node;
                    }
                    break;

                }

//                if (myDest != -1) {
//                    break;
//                }
            }
        }

        if (willBeSurrounded(target, allDest)) {
            int killerId = selected.get(0);
            if (killerId == currAgentId) {
                myDest = target;
//                logger.log("killer:%d\n", killerId);
            } else {
                // todo next target
            }
        }

        // ------------------------------------------------------------------


        if (myDest == -1) {
            // strategy2

            List<Integer> notSelected = polices.stream()
                    .map(Agent::getId)
                    .filter(agentId -> !selected.contains(agentId))
                    .collect(Collectors.toList());


            for (int node : neighborNodes) {
                for (int agentId : notSelected) {
                    if (!allDest.contains(node)) {
                        notSelected.remove((Integer) agentId);
                        if (agentId == currAgentId) {
                            myDest = node;
                        }
//                        logger.log("agent:%d\tstrategy2\n", agentId);
                        break;
                    }
                }
                if (myDest != -1) {
                    break;
                }
            }

        }


        if (myDest == -1) {
            // old strategy
//            logger.log("agent:%d\told strategy!!\n", currAgentId);
            int index = polices.indexOf(view.getViewer());
            myDest = neighborNodes.get(index % neighborNodes.size());
        }
//        logger.log("myDest:%d\tallDest: %s\n", myDest, allDest);

        path = getCheaperPath(currNodeId, myDest, 1);
    }

    private boolean willBeSurrounded(int target, ArrayList<Integer> polices) {
        for (int node : config.getNeighborNodes(target, 1)) {
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

        ArrayList<Integer> neighborNodes = config.getNeighborNodes(target, 1);

        for (Agent node : polices) {
            if (neighborNodes.contains(node.getNodeId())) {
                neighborNodes.remove((Integer) node.getNodeId());
            }
        }

        return neighborNodes;

    }

    private boolean isVisibleToPolice(int policeNode, int thiefNode, boolean isJoker) {
        int vision;

        if (isJoker) vision = view.getConfig().getGraph().getVisibleRadiusYPoliceJoker();
        else vision = view.getConfig().getGraph().getVisibleRadiusXPoliceThief();

        return config.getMinDistance(policeNode, thiefNode, 0) <= vision;
    }

    boolean isPoliceWithDist1() {
        ArrayList<Agent> polices = getTeammatePolice(true);

        ArrayList<Integer> neighborNodes = config.getNeighborNodes(target, 1);

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

        ArrayList<Integer> neighborNodes = config.getNeighborNodes(target, 1);

        for (Agent node : polices) {
            neighborNodes.removeAll(config.getNeighborNodes(node.getNodeId(), 1));
        }

        neighborNodes.sort(Comparator
                .comparingInt((Integer node) -> -getDisToAllPolices(node))
                .thenComparingInt(node -> -config.getNeighborNodesCount(node, 1))
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
        Agent thief = thievesLocation.stream().min(


                Comparator
                        .comparingInt(agent -> getDisToAllPolices(((Agent) agent).getNodeId()))
                        .thenComparingInt(agent -> config.getNeighborNodesCount(((Agent) agent).getNodeId(), 1))
                        .thenComparingInt(agent -> ((Agent) agent).getNodeId())

        ).orElse(null);

        isTargetJoker = thief != null && thief.getType().equals(AIProto.AgentType.JOKER);

        if(thief != null) {
            return thief.getNodeId();
        }
        return getFarthestRandomNode(currNodeId, 0.7);
    }

    private int getDisToAllPolices(int thiefLoc) {
        ArrayList<Agent> polices = getTeammatePolice(true);
        int sum = 0;
        for (Agent police : polices) {
            sum += config.getMinDistance(police.getNodeId(), thiefLoc, 1);
        }
        return sum;
    }

    private void setThievesLocation() {
        thievesLocation = getOpponentThieves().stream()
                .filter(agent -> !agent.getIsDead())
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
            minDist = Math.min(minDist, config.getMinDistance(node, nodeId, 1));
        }
        return minDist;
    }
}
