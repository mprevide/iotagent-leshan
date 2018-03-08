

import com.auth0.jwt.*;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.google.gson.*;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.codec.binary.Base64;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.algorithms.Algorithm.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.cpqd.iotagent.LwM2mAgent;

import org.eclipse.leshan.core.node.LwM2mPath;
import org.eclipse.leshan.core.node.LwM2mSingleResource;
import org.eclipse.leshan.core.request.ContentFormat;
import org.eclipse.leshan.core.request.WriteRequest;
import org.eclipse.leshan.core.response.WriteResponse;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationUpdate;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.model.ResourceModel.Type;


import org.eclipse.leshan.server.demo.servlet.json.LwM2mNodeSerializer;
import org.eclipse.leshan.server.demo.servlet.json.LwM2mNodeDeserializer;


import static spark.Spark.*;


public class HelloWorld {

    public static String myGet (){
        System.out.println("Get");
        return "Hello World";
    }
    private static void getDeviceFromDeviceManager(String id){
    }



    public static void main(String[] args) {
        LwM2mAgent agent = new LwM2mAgent();

        get("/update", (req, res) -> agent.update(req.body()));
        get("/actuate", (req, res) -> agent.actuate(req.body()));


        agent.run();

    }




}
