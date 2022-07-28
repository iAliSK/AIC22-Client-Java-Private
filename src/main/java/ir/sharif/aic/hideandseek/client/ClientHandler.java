package ir.sharif.aic.hideandseek.client;

import io.grpc.Channel;
import ir.sharif.aic.hideandseek.ai.AI;
import ir.sharif.aic.hideandseek.ai.PoliceAI;
import ir.sharif.aic.hideandseek.ai.ThiefAI;
import ir.sharif.aic.hideandseek.command.CommandImpl;

import ir.sharif.aic.hideandseek.protobuf.AIProto.AgentType;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameStatus;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;
import ir.sharif.aic.hideandseek.protobuf.AIProto.TurnType;
import ir.sharif.aic.hideandseek.protobuf.GameHandlerGrpc;
import ir.sharif.aic.hideandseek.protobuf.GameHandlerGrpc.GameHandlerBlockingStub;
import ir.sharif.aic.hideandseek.protobuf.GameHandlerGrpc.GameHandlerStub;
import java.util.Iterator;

public class ClientHandler {

    private final Channel channel;
    private final GameHandlerStub nonBlockingStub;
    private final GameHandlerBlockingStub blockingStub;
    private final CommandImpl commandImpl;

    private AI ai;
    private int turn = 1;
    private boolean hasMoved = false;

    public ClientHandler(Channel channel, String token) {
        this.channel = channel;
        nonBlockingStub = GameHandlerGrpc.newStub(channel);
        blockingStub = GameHandlerGrpc.newBlockingStub(channel);
        commandImpl = new CommandImpl(token);
    }

    public void handleClient() {
        var watchCommand = commandImpl.createWatchCommand();
        Iterator<GameView> gameViews;
        try {
            gameViews = blockingStub.watch(watchCommand);
            boolean isFirstTurn = true;
            while (gameViews.hasNext()) {
                GameView gameView = gameViews.next();
                if (turn != gameView.getTurn().getTurnNumber()) {
                    turn = gameView.getTurn().getTurnNumber();
                    hasMoved = false;
                }
                if(gameView.getStatus().equals(GameStatus.FINISHED))
                    return;
                if(gameView.getViewer().getIsDead())
                    return;
                if (isFirstTurn) {
                    initialize(gameView);
                    isFirstTurn = false;
                } else if (gameView.getStatus() == GameStatus.ONGOING && canMove(gameView)) {
                    move(gameView);
                } else if (gameView.getStatus() == GameStatus.FINISHED) {
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    private boolean canMove(GameView gameView) {
        if (gameView.getTurn().getTurnType().equals(TurnType.POLICE_TURN)
                && gameView.getViewer().getType().equals(AgentType.THIEF))
            return false;

        if (gameView.getTurn().getTurnType().equals(TurnType.THIEF_TURN)
                && gameView.getViewer().getType().equals(AgentType.POLICE))
            return false;

        if (gameView.getTurn().getTurnNumber() == turn) {
            return !hasMoved;
        }

        return true;
    }

    public void sendMessage(String message) {
        var chatCommand = commandImpl.createChatCommand(message);
        try {
            blockingStub.sendMessage(chatCommand);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void initialize(GameView gameView) {
        setAIMethod(gameView.getViewer().getType().equals(AgentType.POLICE));
        var startingNodeId = ai.getStartingNode(gameView);
        var declareReadinessCommand = commandImpl.declareReadinessCommand(startingNodeId);
        try {
            blockingStub.declareReadiness(declareReadinessCommand);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void setAIMethod(boolean isPolice) {
        if (ai != null) {
            return;
        }
        var phone = new Phone(this);
        ai = isPolice ? new PoliceAI(phone) : new ThiefAI(phone);
    }

    private void move(GameView gameView) {
        var destinationNodeId = ai.move(gameView);
        var moveCommand = commandImpl.createMoveCommand(destinationNodeId);
        try {
            blockingStub.move(moveCommand);
            hasMoved = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
