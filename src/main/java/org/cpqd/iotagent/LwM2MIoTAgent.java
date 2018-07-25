package org.cpqd.iotagent;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.californium.scandium.dtls.pskstore.InMemoryPskStore;


public class LwM2MIoTAgent {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(LwM2MIoTAgent.class);

        logger.info("Starting LwM2M IoTAgent...");
        
        InMemoryPskStore securityStore = new InMemoryPskStore();

        String imageManagerUrl = "http://image-manager:5000";
        String deviceManagerUrl = "http://device-manager:5000";
        
        // we need to share the securityStore with the agent
        LwM2mAgent agent = new LwM2mAgent(deviceManagerUrl, imageManagerUrl);

        File coapConfigFile = new File(new String("fileServerCoAP.properties"));

        SimpleFileServer fileServer = new SimpleFileServer(coapConfigFile, securityStore);
        
        fileServer.start();
        // we need to share the path with the ImageDownloader
        fileServer.addNewResource(new String("data"), new File(new String("data")));
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

