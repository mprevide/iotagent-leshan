package org.cpqd.iotagent;

import br.com.dojot.kafka.Manager;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import static spark.Spark.get;

public class LwM2MIoTAgent {

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(LwM2MIoTAgent.class);

        String imageManagerUrl = "http://localhost:5000";
        String deviceManagerUrl = "http://localhost:5001";

        Manager manager = new Manager();

        LwM2mAgent agent = new LwM2mAgent(deviceManagerUrl, imageManagerUrl);
        agent.run();

        get("/update", (req, res) -> agent.update(new JSONObject(req.body())));
        get("/actuate", (req, res) -> agent.actuate(new JSONObject(req.body())));

        manager.addCallback("create", agent::create);
        manager.addCallback("update", agent::update);
        manager.addCallback("remove", agent::remove);
        manager.addCallback("actuate", agent::actuate);

        while (true) {
            logger.info("Running LwM2M IoT Agent");

            try {
                Thread.sleep(10000);
            } catch (InterruptedException exception) {
                logger.error("Exception: " + exception.toString());
            }
        }
    }
}
