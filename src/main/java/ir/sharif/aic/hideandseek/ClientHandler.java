package ir.sharif.aic.hideandseek;

import io.grpc.Channel;
import ir.sharif.aic.hideandseek.api.grpc.AIProto.GameView;
import ir.sharif.aic.hideandseek.api.grpc.AIProto.WatchCommand;
import ir.sharif.aic.hideandseek.api.grpc.GameHandlerGrpc;
import ir.sharif.aic.hideandseek.api.grpc.GameHandlerGrpc.GameHandlerBlockingStub;
import ir.sharif.aic.hideandseek.api.grpc.GameHandlerGrpc.GameHandlerStub;
import ir.sharif.aic.hideandseek.config.ConfigLoader;
import java.util.Iterator;

public class ClientHandler {

    private final Channel channel;
    private final GameHandlerStub nonBlockingStub;
    private final GameHandlerBlockingStub blockingStub;

    public ClientHandler(Channel channel) {
        this.channel = channel;
        nonBlockingStub = GameHandlerGrpc.newStub(channel);
        blockingStub = GameHandlerGrpc.newBlockingStub(channel);
    }

    public void handleClient() {
        WatchCommand watchCommand = WatchCommand.newBuilder().setToken(ConfigLoader.getConfig().getToken()).build();
        Iterator<GameView> gameViews;
        try {
            gameViews = blockingStub.watch(watchCommand);
            for (int i = 1; gameViews.hasNext(); i++) {
                GameView gameView = gameViews.next();
                System.out.println("Game View number " + i + " : " + gameView.getStatus());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendMessage(GameView gameView) {
        // generate chat command
    }

    private void move(GameView gameView) {
        // generate move command
    }

    private void sendJoinGameCommand(GameView gameView) {
        // generate declare readiness command
    }

}
