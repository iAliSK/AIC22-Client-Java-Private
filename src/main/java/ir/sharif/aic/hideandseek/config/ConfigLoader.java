package ir.sharif.aic.hideandseek.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ir.sharif.aic.hideandseek.Main;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Objects;

public class ConfigLoader {

    private static Config config;

    private static Config loadConfig() {
//        try {
            Config config = new Config();
            GRpcConfig gRpcConfig = new GRpcConfig();
            gRpcConfig.setPort("7000");
            gRpcConfig.setServer("127.0.0.1");
            config.setGRpc(gRpcConfig);
            return config;
            // fuck java
//            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
//            return objectMapper.readValue(Paths.get(
//                            Objects.requireNonNull(Main.class.getClassLoader().getResource("application.yml")).toURI())
//                    .toFile(), Config.class);
//        }  catch (IOException | URISyntaxException e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    public static Config getConfig() {
        if (config == null) {
            config = loadConfig();
        }

        return config;
    }

}
