package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

public abstract class AI {
    protected Config config;
    protected Phone phone;

    public abstract int getStartingNode(GameView gameView);

    public abstract int move(GameView gameView);

//    protected int getFarthestRandomNodeFromPoliceStation(int seed) {
//        int[] distances = config.getMinDistances(1);
//
//        int maxDist = Arrays.stream(distances).max().orElse(2);
//
//        int minDist = (int) (0.8 * maxDist);
//
//        Integer[] farthestNodes = IntStream.range(0, distances.length)
//                .filter(i -> distances[i] >= minDist)
//                .boxed().toArray(Integer[]::new);
//
//
//        // sort by max neighbor nodes count
//        Arrays.sort(farthestNodes, Comparator.
//                comparingInt(d -> config.getNeighborNodesCount((Integer) d)).reversed());
//
//
//        int randIndex = config.getRandInt(farthestNodes.length / 2, seed);
//        return farthestNodes[randIndex];
//    }
//
//    protected ArrayList<Integer> getOpponentPoliceList(GameView gameView) {
//        ArrayList<Integer> opponentPolice = new ArrayList<>();
//        for (AIProto.Agent agent : gameView.getVisibleAgentsList()) {
//            boolean teammate = agent.getTeamValue() == gameView.getViewer().getTeamValue();
//            boolean sameType = agent.getTypeValue() == gameView.getViewer().getTypeValue();
//            if (teammate || sameType) {
//                continue;
//            }
//            opponentPolice.add(agent.getNodeId());
//        }
//        return opponentPolice;
//    }
}
