package ir.sharif.aic.hideandseek;

import io.grpc.ManagedChannelBuilder;
import ir.sharif.aic.hideandseek.client.ClientHandler;
import ir.sharif.aic.hideandseek.config.ConfigLoader;

public class Main {

    public static void main(String[] args) throws Exception {
        String token;
        try {
            token = args[0];
        } catch (IndexOutOfBoundsException e) {
            throw new Exception("No token provided. Please provide a token as the first argument to the program.");
        }
        var config = ConfigLoader.getConfig();
        var handler = new ClientHandler(
                ManagedChannelBuilder.forAddress(
                        config.getGRpc().getServer(),
                        Integer.parseInt(config.getGRpc().getPort())).usePlaintext().build(), token);
        handler.handleClient();
    }

}
