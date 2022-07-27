package ir.sharif.aic.hideandseek.config;

public class ConfigLoader {

    private static Config config;

    private static Config loadConfig() {
        Config config = new Config();
        GRpcConfig gRpcConfig = new GRpcConfig();
        gRpcConfig.setPort("7000");
        gRpcConfig.setServer("127.0.0.1");
        config.setGRpc(gRpcConfig);
        return config;
    }

    public static Config getConfig() {
        if (config == null) {
            config = loadConfig();
        }

        return config;
    }

}
