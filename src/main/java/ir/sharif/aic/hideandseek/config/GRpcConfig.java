package ir.sharif.aic.hideandseek.config;

public class GRpcConfig {
    private String server;
    private String port;

    public String getServer() {
        return server;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setServer(String server) {
        this.server = server;
    }

    @Override
    public String toString() {
        return "ir.sharif.aic.hideandseek.config.GRpc{" +
                "server='" + server + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}
