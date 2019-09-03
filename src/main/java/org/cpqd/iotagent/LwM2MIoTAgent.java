package org.cpqd.iotagent;

import java.io.File;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.network.config.NetworkConfig;

public class LwM2MIoTAgent {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(LwM2MIoTAgent.class);
        logger.info("Starting lwm2m IoTAgent...");

        FileServerPskStore securityStore = new FileServerPskStore();
        org.cpqd.iotagent.Config config = org.cpqd.iotagent.Config.getInstance();
        

        File coapConfigFile = new File(new String("fileServerCoAP.properties"));

        NetworkConfig netConfig = NetworkConfig.createStandardWithFile(coapConfigFile);
        int coapPort = netConfig.getInt(NetworkConfig.Keys.COAP_PORT);
        int coapsPort = netConfig.getInt(NetworkConfig.Keys.COAP_SECURE_PORT);

        String fileServerAddress = config.getFileServerAddress();
        int httpPort = config.getFileServerHttpPort();
        int httpsPort = config.getFileServerHttpsPort();
        String fileServerDataPath = Paths.get(config.getFileServerDataPath()).toAbsolutePath().toString();

        com.cpqd.app.config.Config dojotConfig = com.cpqd.app.config.Config.getInstance();
        
        ImageDownloader imageDownloader = new ImageDownloader(
                "http://" + dojotConfig.getImageManagerAddress(), fileServerDataPath,
                fileServerAddress, coapPort, coapsPort, httpPort, httpsPort);


        Long consumerPollTime = dojotConfig.getKafkaDefaultConsumerPollTime();

        LwM2MAgent agent = new LwM2MAgent(consumerPollTime, imageDownloader, securityStore);

        boolean bootstraped = agent.bootstrap();
        if (!bootstraped) {
            logger.error("Failed on bootstrap");
            System.exit(1);
        }

        SimpleFileServerHttp httpFileServer = new SimpleFileServerHttp(httpPort, fileServerDataPath);
        httpFileServer.start();

        SimpleFileServerCoap coapFileServer = new SimpleFileServerCoap(coapConfigFile, securityStore);
        coapFileServer.start();
        coapFileServer.addNewResource("data", new File(fileServerDataPath));

        (new Thread(agent)).start();

        while (true) {
            logger.info("Running lwm2m IoTAgent");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException exception) {
                logger.error("Exception: " + exception.toString());
            }
        }
    }
}

