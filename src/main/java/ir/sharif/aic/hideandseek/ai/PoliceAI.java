package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

public class PoliceAI extends AI {

    public PoliceAI(Phone phone) {
        this.phone = phone;
    }

    /**
     * This function always returns zero (Polices can not set their starting node).
     */
    @Override
    public int getStartingNode(GameView gameView) {
        return 1;
    }

    /**
     * Implement this function to move your police agent based on current game view.
     */
    @Override
    public int move(GameView gameView) {
        this.config = Config.getInstance(gameView);
        return 1;
    }

}
