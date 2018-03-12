package org.cpqd.iotagent;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.cpqd.iotagent.LwM2mAgent;
import org.cpqd.iotagent.KafkaConsumerLoop;

import org.apache.kafka.clients.consumer.*;
import java.util.Properties;

import static spark.Spark.get;

public class LwM2MIoTAgent {


    public static void main(String[] args) {
        String imageManagerUrl = "http://localhost:8000";
        String deviceManagerUrl = "http://localhost:8001";

        LwM2mAgent agent = new LwM2mAgent(deviceManagerUrl, imageManagerUrl);
        agent.run();


        get("/update", (req, res) -> agent.update(req.body()));
        get("/actuate", (req, res) -> agent.actuate(req.body()));


        KafkaConsumerLoop consumerLoop = new KafkaConsumerLoop(0, "dojot", Arrays.asList("test"));
        consumerLoop.run();


    }

}
