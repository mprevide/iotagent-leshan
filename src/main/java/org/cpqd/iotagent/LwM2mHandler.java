package org.cpqd.iotagent;


/*
This helper class encapsulate all requests to devices.
Any device registration is expected to be active and working
Checking if registrations are active is not this class responsibility
In case of failure any request should fail silently
 */

import org.eclipse.leshan.core.request.ExecuteRequest;
import org.eclipse.leshan.core.request.ObserveRequest;
import org.eclipse.leshan.core.request.ContentFormat;
import org.apache.log4j.Logger;
import org.eclipse.leshan.core.request.WriteRequest;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.node.LwM2mMultipleResource;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.node.LwM2mSingleResource;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.server.LwM2mServer;
import org.eclipse.leshan.server.registration.Registration;

public class LwM2mHandler {
    private Logger mLogger = Logger.getLogger(LwM2mHandler.class);

    private LwM2mServer server;
    private long readTimout;
    private long writeTimeout;

    public LwM2mHandler(LwM2mServer server) {
        this.readTimout = 5000;
        this.writeTimeout = 5000;
        this.server = server;
    }

    public LwM2mResource ObserveResource(Registration registration, String path) {
        LwM2mResource resource = null;
        try {
            Integer pathArray[] = DeviceAttribute.getIdsfromPath(path);
            ObserveResponse response = server.send(registration,
                new ObserveRequest(ContentFormat.fromCode(ContentFormat.TLV_CODE), pathArray[0], pathArray[1], pathArray[2]), readTimout);
            if (response == null) {
                this.mLogger.error("observe request timed out");
                return null;
            }
            if (!response.isSuccess()) {
                this.mLogger.error("Observe request failed. Error: " +
                    response.toString());
                return null;
            }
            LwM2mNode lwm2mNode = response.getContent();
            if (!(lwm2mNode instanceof LwM2mResource)) {
                this.mLogger.warn("Unsuported content object.");
                return null;
            }
            resource = (LwM2mResource)lwm2mNode;
        } catch (Exception e) {
            // Todo(jsiloto): Log errors here
            e.printStackTrace();
            mLogger.error(e);
        }
        return resource;
    }

    public void CancelAllObservations(Registration registration) {
        server.getObservationService().cancelObservations(registration);
    }

    public void ExecuteResource(Registration registration, String path, String parameters) {
        try {
            if ( (parameters == null) || parameters.isEmpty() ) {
                mLogger.debug("Execute: " + path + " without parameters");
                server.send(registration, new ExecuteRequest(path));
            } else {
                mLogger.debug("Execute: " + path + " with parameters: " + parameters);
                server.send(registration, new ExecuteRequest(path, parameters));
            }
        } catch (Exception e) {
            e.printStackTrace();
            mLogger.error(e);
        }
    }

    public LwM2mSingleResource ReadResource(Registration registration, String path) {
        LwM2mSingleResource resource = null;
        try {
            Integer pathArray[] = DeviceAttribute.getIdsfromPath(path);
            ReadResponse response = server.send(registration,
                new ReadRequest(ContentFormat.fromCode(ContentFormat.TLV_CODE), pathArray[0], pathArray[1], pathArray[2]), readTimout);
            if (response == null) {
                this.mLogger.error("read request timed out");
                return null;
            }
            if (!response.isSuccess()) {
                this.mLogger.error("read request failed. Error: " +
                    response.toString());
                return null;
            }                
            LwM2mNode lwm2mNode = response.getContent();
            if (!(lwm2mNode instanceof LwM2mSingleResource)) {
        		this.mLogger.warn("Unsuported content object.");
        		return null;
        	}
        	resource = (LwM2mSingleResource)lwm2mNode;
        } catch (Exception e) {
            e.printStackTrace();
            this.mLogger.error(e);
        }
        return resource;
    }

    public LwM2mMultipleResource ReadMultipleResource(Registration registration, String path) {
        LwM2mMultipleResource resource = null;
        try {
            Integer pathArray[] = DeviceAttribute.getIdsfromPath(path);
            ReadResponse response = server.send(registration,
                new ReadRequest(ContentFormat.fromCode(ContentFormat.TLV_CODE), pathArray[0], pathArray[1], pathArray[2]), readTimout);
            if (response == null) {
                this.mLogger.error("read request timed out");
                return null;
            }
            if (!response.isSuccess()) {
                this.mLogger.error("read request failed. Error: " +
                    response.toString());
                return null;
            }                
            LwM2mNode lwm2mNode = response.getContent();
            if (!(lwm2mNode instanceof LwM2mMultipleResource)) {
        		this.mLogger.warn("Unsuported content object.");
        		return null;
        	}
        	resource = (LwM2mMultipleResource)lwm2mNode;
        } catch (Exception e) {
            e.printStackTrace();
            this.mLogger.error(e);
        }
        return resource;
    }

    public void WriteResource(Registration registration, String path, Object value) {
        try {
            Integer pathArray[] = DeviceAttribute.getIdsfromPath(path);
            if (value instanceof String) {
                WriteResponse response = server.send(registration, new WriteRequest(pathArray[0], pathArray[1], pathArray[2], (String) value));
            } else if (value instanceof Double) {
                WriteResponse response = server.send(registration, new WriteRequest(pathArray[0], pathArray[1], pathArray[2], (Double) value));
            } else if (value instanceof Boolean) {
                WriteResponse response = server.send(registration, new WriteRequest(pathArray[0], pathArray[1], pathArray[2], (Boolean) value));
            } else if (value instanceof Integer) {
                WriteResponse response = server.send(registration, new WriteRequest(pathArray[0], pathArray[1], pathArray[2], (Integer) value));
            } else {
                mLogger.error("Unexpected type: " + value.toString());
            }

        } catch (Exception e) {
            // Todo(jsiloto): Log errors here
            e.printStackTrace();
            mLogger.error(e);
        }
    }


}
