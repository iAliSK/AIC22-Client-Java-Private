package ir.sharif.aic.hideandseek;

import io.grpc.ManagedChannelBuilder;
import ir.sharif.aic.hideandseek.client.ClientHandler;
import ir.sharif.aic.hideandseek.config.ConfigLoader;

public class Main {

    public static void main(String[] args) {
        var config = ConfigLoader.getConfig();
        var handler = new ClientHandler(
                ManagedChannelBuilder.forAddress(
                        config.getGRpc().getServer(),
                        Integer.parseInt(config.getGRpc().getPort())).usePlaintext().build());
        handler.handleClient();
    }

}
