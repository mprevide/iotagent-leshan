package org.cpqd.iotagent;

import java.io.File;

import org.apache.log4j.Logger;
import org.cpqd.iotagent.LwM2MAgent;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.scandium.dtls.pskstore.InMemoryPskStore;
import org.cpqd.iotagent.ImageDownloader;
import br.com.dojot.config.Config;

public class LwM2MIoTAgent {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(LwM2MIoTAgent.class);
        logger.info("Starting LwM2M IoTAgent...");

        InMemoryPskStore securityStore = new InMemoryPskStore();

        File coapConfigFile = new File(new String("fileServerCoAP.properties"));

        NetworkConfig netConfig = NetworkConfig.createStandardWithFile(coapConfigFile);
        int coapPort = netConfig.getInt(NetworkConfig.Keys.COAP_PORT);
        int secureCoapPort = netConfig.getInt(NetworkConfig.Keys.COAP_SECURE_PORT);

        String fileServerAddress;

        if(System.getenv("FILE_SERVER_ADDRESS") == null ){
            logger.fatal("Missing file server address configuration." + 
                "Please check if the 'FILE_SERVER_ADDRESS' if setted");
            System.exit(1);
        }
        fileServerAddress = System.getenv("FILE_SERVER_ADDRESS");

        Config dojotConfig = Config.getInstance();
        String dataDir = "data";
        ImageDownloader imageDownloader = new ImageDownloader(
            "http://" + dojotConfig.getImageManagerAddress(), dataDir,
            fileServerAddress, coapPort, secureCoapPort);
        
        // we need to share the securityStore with the agent
        LwM2MAgent agent = new LwM2MAgent(imageDownloader);

        boolean bootstraped = agent.bootstrap();
        if (!bootstraped) {
        	logger.error("Failed on bootstrap");
        	System.exit(1);
        }

        SimpleFileServer fileServer = new SimpleFileServer(coapConfigFile, securityStore);

        fileServer.start();
        fileServer.addNewResource(dataDir, new File(dataDir));
        (new Thread(agent)).start();

        while (true) {
            logger.info("Running LwM2M IoTAgent");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException exception) {
                logger.error("Exception: " + exception.toString());
            }
        }
    }
}

