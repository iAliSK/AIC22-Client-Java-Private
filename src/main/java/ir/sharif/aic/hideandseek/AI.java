package ir.sharif.aic.hideandseek;

import ir.sharif.aic.hideandseek.api.grpc.AIProto.ChatCommand;
import ir.sharif.aic.hideandseek.api.grpc.AIProto.GameView;
import ir.sharif.aic.hideandseek.api.grpc.AIProto.MoveCommand;

public class AI {

    public static int getThiefStartingNode(GameView gameView) {
        return 0;
    }

    public static MoveCommand thiefMove(GameView gameView) {
        return null;
    }

    public static MoveCommand policeMove(GameView gameView) {
        return null;
    }

    public static ChatCommand thiefChat(GameView gameView) {
        return null;
    }

    public static ChatCommand policeChat(GameView gameView) {
        return null;
    }

}
