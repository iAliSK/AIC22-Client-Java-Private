package ir.sharif.aic.hideandseek.config;

public class Config {
    private GRpcConfig grpc;
    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setGRpc(GRpcConfig gRpc) {
        this.grpc = gRpc;
    }

    public GRpcConfig getGRpc() {
        return grpc;
    }

    @Override
    public String toString() {
        return "ir.sharif.aic.hideandseek.config.Config{" +
                "grpc=" + grpc +
                ", token='" + token + '\'' +
                '}';
    }
}
