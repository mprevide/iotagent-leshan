package org.cpqd.iotagent;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import static spark.Spark.get;

public class LwM2MIoTAgent {


    public static void main(String[] args) {
        String imageManagerUrl = "http://localhost:8000";
        String deviceManagerUrl = "http://localhost:8000";

        org.cpqd.iotagent.LwM2mAgent agent = new org.cpqd.iotagent.LwM2mAgent(deviceManagerUrl, imageManagerUrl);
        agent.run();


        get("/update", (req, res) -> agent.update(req.body()));
        get("/actuate", (req, res) -> agent.actuate(req.body()));
    }

}
