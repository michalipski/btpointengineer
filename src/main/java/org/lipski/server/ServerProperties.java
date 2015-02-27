package org.lipski.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerProperties {

    static Properties serverProperties = new Properties();
    static InputStream inputStream;

    public static String getServerId() throws IOException {
        inputStream = new FileInputStream("src/serverConfig.properties");

        serverProperties.load(inputStream);

        return serverProperties.getProperty("serverId");
    }

}
