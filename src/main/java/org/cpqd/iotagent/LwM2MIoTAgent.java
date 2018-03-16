package org.cpqd.iotagent;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import java.io.UnsupportedEncodingException;
import java.sql.Array;
import java.util.*;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.cpqd.iotagent.LwM2mAgent;
import org.cpqd.iotagent.KafkaConsumerLoop;

import org.apache.kafka.clients.consumer.*;

import static spark.Spark.get;

public class LwM2MIoTAgent {


    public static void main(String[] args) {
        String imageManagerUrl = "http://localhost:5000";
        String deviceManagerUrl = "http://localhost:5001";

        LwM2mAgent agent = new LwM2mAgent(deviceManagerUrl, imageManagerUrl);
        agent.run();


        get("/update", (req, res) -> agent.update(req.body()));
        get("/actuate", (req, res) -> agent.actuate(req.body()));


        KafkaConsumerLoop consumerLoop = new KafkaConsumerLoop(0, "dojot",
                new LinkedList<String>(Arrays.asList("dojot.device-manager.device", "dojot.tenancy", "device-data")));

        consumerLoop.RegisterCallback("update", agent::update);
        consumerLoop.RegisterCallback("actuate", agent::actuate);
        consumerLoop.run();


    }

}
