package org.cpqd.iotagent;


/*
This helper class encapsulate all requests to devices.
Any device registration is expected to be active and working
Checking if registrations are active is not this class responsibility
In case of failure any request should fail silently
 */

import com.google.gson.Gson;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.request.WriteRequest;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.eclipse.leshan.server.LwM2mServer;
import org.eclipse.leshan.server.registration.Registration;

public class LwM2mHandler {

    private Gson gson;
    private LwM2mServer server;
    public LwM2mHandler(LwM2mServer server, Gson gson){
        this.server = server;
        this.gson = gson;
    }

    // TODO(jsiloto) Use generics
    public String ReadResource(Registration registration, int objectId, int objectInstanceId, int resourceId){
        String value= "";
        try {
            ReadResponse response = server.send(registration, new ReadRequest(objectId, objectInstanceId, resourceId));
            value = gson.toJsonTree(response.getContent()).getAsJsonObject().get("value").toString().replaceAll("^\"|\"$", "");
        } catch(Exception e){
            // Todo(jsiloto): Log errors here
            e.printStackTrace();
        }
        return value;
    }

    public void WriteResource(Registration registration, int objectId, int objectInstanceId, int resourceId, String value){
        try {
            WriteResponse response = server.send(registration, new WriteRequest(objectId, objectInstanceId, resourceId, value));
        } catch(Exception e){
            // Todo(jsiloto): Log errors here
            e.printStackTrace();
        }
    }


}
