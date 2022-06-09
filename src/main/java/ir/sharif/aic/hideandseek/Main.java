package ir.sharif.aic.hideandseek;

import io.grpc.ManagedChannelBuilder;
import ir.sharif.aic.hideandseek.config.Config;
import ir.sharif.aic.hideandseek.config.ConfigLoader;

public class Main {

    public static void main(String[] args) throws Exception {
        var config = ConfigLoader.getConfig();
        var handler = new ir.sharif.aic.hideandseek.ClientHandler(
                ManagedChannelBuilder.forAddress(
                        config.getGRpc().getServer(),
                        Integer.parseInt(config.getGRpc().getPort())).usePlaintext().build());
        handler.handleClient();
    }

}
