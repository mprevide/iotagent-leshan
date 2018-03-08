package org.cpqd.iotagent;

import static spark.Spark.get;

public class LwM2MIoTAgent {


    public static void main(String[] args) {
        org.cpqd.iotagent.LwM2mAgent agent = new org.cpqd.iotagent.LwM2mAgent();

        get("/update", (req, res) -> agent.update(req.body()));
        get("/actuate", (req, res) -> agent.actuate(req.body()));


        agent.run();

    }

}
