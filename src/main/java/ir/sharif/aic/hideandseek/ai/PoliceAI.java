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
        int RemainingTurnsToFirstVisibleTurn = (getVisibleTurns().get(0) - 2) / 2;
        initPath(RemainingTurnsToFirstVisibleTurn);
        return 1;
    }

    /**
     * Implement this function to move your police agent based on current game view.
     */
    @Override
    public int move(GameView view) {
        updateGame(view);
        if (target > 0 && view.getViewer().getId() == (isPoliceWithDis1())) {
            // همشون میرن روی نود دزد
            // بعد از رسیدن تکلیفشون مشخص نیست
            path.add(target);
        }
        if (isVisibleTurn()) {
            setThievesLocation();
            target = getNearestThief();
            newStrategy();
        }
        return getNextInPath();
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

    void newStrategy() {

        // money ignore
        // ممکن است مقصد 2 پلیس یکسان شود
        // ممکن است مسیر داده شده خالی باشد


        // each police 1 destination

        Grid.filter.clear();

        ArrayList<Integer> targets = new ArrayList<>();
        targets.add(target);

        ArrayList<Integer> neighborNodes = config.getNeighborNodes
                (targets, 1, new HashSet<>(targets), true);

        neighborNodes.sort((o1, o2) -> {
            int temp1 = Integer.compare(config.getMinDistance(o1, target), config.getMinDistance(o2, target));
            if (temp1 != 0) return temp1;
            int temp2 = Integer.compare(config.getNeighborNodesCount(o2), config.getNeighborNodesCount(o1));
            if (temp2 != 0) return temp2;
            return Integer.compare(o1, o2);
        });

        ArrayList<Agent> polices = getTeammatePolice(true);
        polices.sort((Comparator.comparingInt
                        ((Agent o) -> config.getMinDistance(o.getNodeId(), target))
                .thenComparingInt(Agent::getId)));

        logger.log(
                "turn:%d\t" +
                        "target:%d\t" +
                        "curr:%d\t" +
                        "neighbors:%s\t" +
                        "path:%s\t" +
                        "neighbor22222:%s\t" +
                        "polices:%s\n",
                view.getTurn().getTurnNumber(),
                target,
                currNodeId,
                neighborNodes.toString(),
                path.toString(),
                config.getNeighborNodes(target),
                polices.toString()
        );

        int counter = 0;
        int max = 0;
        for (Agent police : polices) {
            int index = counter % neighborNodes.size();
            counter++;
            int dest = neighborNodes.get(index);
            //=======
            ArrayList<Integer> tgs = new ArrayList<>();
            tgs.add(target);
            Grid.filter = config.getNeighborNodes(tgs, 1, new HashSet<>(tgs), false);
            Grid.filter.remove((Integer) dest);

            // اینجا ممکنه اکسپشن نال بده اگه مسیری نباشه
            ArrayList<ArrayList<Integer>> paths = config.getAllShortestPaths(police.getNodeId(),dest);
            if (paths.size() > 0
                    && paths.get(0).size() > 0
                    && paths.get(0).size() > max)
                max = paths.get(0).size();

            Grid.filter.clear();
        }

        //===============
        // initialize path

        int index = polices.indexOf(view.getViewer()) % neighborNodes.size();
        int dest = neighborNodes.get(index);

        Grid.filter = config.getNeighborNodes(target);
        Grid.filter.add(target);
        Grid.filter.remove((Integer) dest);

        ArrayList<Integer> temp = getCheaperPath(currNodeId,dest);
        if (temp != null && temp.size() > 0)
            path = temp;

        if (path.size() < max) {
            for (int i = 0; i < max-path.size(); i++) {
                path.add(0,currNodeId);
            }
        }

        Grid.filter.clear();

        logger.log(
                "turn:%d\t" +
                "GridFilter:%s\t" +
                "target:%d\t" +
                "max:%d\t" +
                        "curr:%d\t" +
                        "path:%s\t" +
                        "index:%d\t" +
                        "polices:%s\n",
                view.getTurn().getTurnNumber(),
                Grid.filter.toString(),
                target,
                max,
                currNodeId,
                path.toString(),
                index,
                polices.toString()

        );
    }

    void initPath(int number) {
        ArrayList<Integer> start = new ArrayList<>();
        start.add(1);
        ArrayList<Integer> nodes = config.getNeighborNodes(start, number, new HashSet<>(), true);

        nodes.sort((o1, o2) -> {
            int temp1 = Integer.compare(config.getNeighborNodesCount(o2), config.getNeighborNodesCount(o1));
            if (temp1 != 0) return temp1;
            int temp2 = Double.compare(config.getPathCost(getCheaperPath(1, o1)),
                    config.getPathCost(getCheaperPath(1, o2)));
            if (temp2 != 0) return temp2;
            return Integer.compare(o1, o2);
        });

        ArrayList<Agent> polices = getTeammatePolice(true);
        polices.sort(Comparator.comparingInt(Agent::getId));

        int index = polices.indexOf(view.getViewer());
        int dest = nodes.get(index % nodes.size());

        path = getCheaperPath(1, dest);
    }

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

    private void setThievesLocation() {
        thievesLocation = getOpponentThieves().stream()
                .filter(agent -> !agent.getIsDead())
                .map(Agent::getNodeId)
                .toList();
    }

    private int getDisToAllPolices(int thiefLoc) {
        ArrayList<Agent> polices = getTeammatePolice(false);
        int sum = 0;
        for (Agent police : polices) {
            sum += config.getMinDistance(police.getNodeId(), thiefLoc);
        }
        return sum;
    }

    private int isPoliceWithDis1() {
        ArrayList<Agent> polices = getTeammatePolice(false);

        ArrayList<Integer> neighborNodes = config.getNeighborNodes(target);

        for (Agent node : polices) {
            if (neighborNodes.contains(node.getNodeId()))
                return node.getId();
        }

        return -1;
    }

    //-=======================================================
    //-=======================================================
    //-=======================================================
    //-=======================================================
    //-=======================================================
    //-=======================================================
    //-=======================================================ب
    //-=======================================================
    //-=======================================================
    //-=======================================================
    //-=======================================================
    //-=======================================================
    //-=======================================================
//    public int move(GameView view) {
//        updateGame(view);
//
//        if (isVisibleTurn()) {
//            setThievesLocation();
//            target = getNearestThief();
//            updatePath(target);
//        }
//        if (target > 0 && !isVisibleTurn() && isPoliceWithDis1())
//            updatePath(bestPossibilityForThief());
//
//        return getNextInPath();
//    }


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

        ArrayList<Integer> neighborNodes = new ArrayList<>(config.getNeighborNodes
                (targets, 3, new HashSet<>(targets), false));

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
        logger.log(
                "turn:%d\t" +
                        "curr:%d\t" +
                        "path:%s\t" +
                        "polices:%s\t" +
                        "index:%d\n",
                view.getTurn().getTurnNumber(),
                currNodeId,
                path.toString(),
                polices.toString(),
                index
        );


        int dest = neighborNodes.get(index % neighborNodes.size());


        //if (config.getMinDistance(currNodeId,target) <= 2) dest = currNodeId;

        path = getCheaperPath(currNodeId, dest);

        //        if (config.getMinDistance(currNodeId,target) <= 2) dest = currNodeId;
//        path = getCheaperPath(currNodeId, dest);
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
    }

    private int getNextBeginning() {
        int target = getNearestThief();
        ArrayList<Integer> nodes = config.getNeighborNodes(currNodeId);
        nodes.sort(Comparator.comparingDouble((Integer o) -> config.getPathCost(o, currNodeId))
                .thenComparingInt(o -> config.getMinDistance(o, target)));
        if (config.getPathCost(nodes.get(0), currNodeId) <= view.getBalance()) return nodes.get(0);
        return currNodeId;
    }

    private Agent getPoliceWithMinId() {
        return getTeammatePolice(false).stream()
                .min(Comparator.comparingInt(Agent::getId)).orElse(null);
    }

}
