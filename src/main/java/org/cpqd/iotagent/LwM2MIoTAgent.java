package org.cpqd.iotagent;
import java.util.*;

public class LwM2MIoTAgent {


    public static void main(String[] args) {
        String imageManagerUrl = "http://localhost:5000";
        String deviceManagerUrl = "http://localhost:5001";

        LwM2mAgent agent = new LwM2mAgent(deviceManagerUrl, imageManagerUrl);
        agent.run();

        KafkaConsumerLoop consumerLoop = new KafkaConsumerLoop(0, "dojot",
                new LinkedList<String>(Arrays.asList("dojot.device-manager.device", "dojot.tenancy", "device-data")));

        consumerLoop.RegisterCallback("update", agent::update);
        consumerLoop.RegisterCallback("actuate", agent::actuate);
        consumerLoop.RegisterCallback("create", agent::create);
        consumerLoop.run();


    }

}
