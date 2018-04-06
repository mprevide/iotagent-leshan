package org.cpqd.iotagent;
import org.cpqd.iotagent.kafka.KafkaHandler;

public class LwM2MIoTAgent {

    public static void main(String[] args) {

        String imageManagerUrl = "http://localhost:5000";
        String deviceManagerUrl = "http://localhost:5001";


        KafkaHandler kafkaHandler = new KafkaHandler();
        LwM2mAgent agent = new LwM2mAgent(kafkaHandler, deviceManagerUrl, imageManagerUrl);

        kafkaHandler.RegisterCallback("update", agent::update);
        kafkaHandler.RegisterCallback("actuate", agent::actuate);
        kafkaHandler.RegisterCallback("create", agent::create);


        // Run Handlers
        (new Thread(kafkaHandler)).start();
        (new Thread(agent)).start();

    }

}
