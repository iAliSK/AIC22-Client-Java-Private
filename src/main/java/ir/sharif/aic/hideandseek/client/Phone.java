package ir.sharif.aic.hideandseek.client;

/**
 * Class for sending message to server.
 */
public class Phone {

    private final ClientHandler client;

    public Phone(ClientHandler client) {
        this.client = client;
    }

    /**
     * Call this function wherever you want to send a message.
     *
     * @param message The message we want to send.
     */
    public void sendMessage(String message) {
        client.sendMessage(message);
    }

}
