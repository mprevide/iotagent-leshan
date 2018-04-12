package org.cpqd.iotagent;
import org.apache.log4j.Logger;

public class LwM2MIoTAgent {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(LwM2MIoTAgent.class);

        logger.info("Starting LwM2M IoTAgent...");

        String imageManagerUrl = "http://image-manager:5000";
        String deviceManagerUrl = "http://device-manager:5000";

        LwM2mAgent agent = new LwM2mAgent(deviceManagerUrl, imageManagerUrl);

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

