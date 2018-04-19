package org.cpqd.iotagent;
import org.apache.log4j.Logger;
import org.eclipse.californium.examples.SimpleFileServer;

import java.io.File;
import java.net.SocketException;
import java.nio.file.FileSystems;
import java.nio.file.Path;


public class LwM2MIoTAgent {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(LwM2MIoTAgent.class);

        logger.info("Starting LwM2M IoTAgent...");

        String imageManagerUrl = "http://image-manager:5000";
        String deviceManagerUrl = "http://device-manager:5000";

        LwM2mAgent agent = new LwM2mAgent(deviceManagerUrl, imageManagerUrl);

        String[] fileArgs = {};
        SimpleFileServer.main(fileArgs);


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

