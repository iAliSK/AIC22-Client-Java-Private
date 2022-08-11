package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

public abstract class AI {
    protected Config config;
    protected Phone phone;
    public abstract int getStartingNode(GameView gameView);
    public abstract int move(GameView gameView);
}
