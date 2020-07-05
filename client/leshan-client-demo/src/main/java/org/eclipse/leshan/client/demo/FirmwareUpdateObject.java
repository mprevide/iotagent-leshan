package org.eclipse.leshan.client.demo;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.model.ResourceModel.Type;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirmwareUpdateObject extends BaseInstanceEnabler {
    
    static final int STATE_UPDATING = 3;
    static final int STATE_IDLE = 0;
    static final int STATE_DOWNLOADING = 1;
    static final int STATE_DOWNLOADED = 2;
    static final int RESOURCE_STATE = 3;
    
    static final int RESOURCE_UPDATE_RESULT = 5;
    static final int UR_INITIAL_VALUE = 0;
    static final int UR_SUCCESS = 1;
    static final int UR_FAIL_DOWNLOAD = 4;
    static final int UR_INTEGRITY_CHECK_FAILED = 5;

    static final long PROTOCOL_COAP = 0;
    static final long PROTOCOL_COAPS = 1;
    static final long PROTOCOL_HTTP = 2;
    static final long PROTOCOL_HTTPS = 3;

    static final int DELIVERY_METHOD_BOTH = 2;

    private Map<Integer, Long> protocols;
    private int deliveryMethod;

    private static final Logger LOG = LoggerFactory.getLogger(FirmwareUpdateObject.class);

    private static final List<Integer> supportedResources = Arrays.asList(1, 2, 3, 5, 8, 9);
    public FirmwareUpdateObject(String[] protocols) {
        URI = "";
        this.deliveryMethod = DELIVERY_METHOD_BOTH;

        this.protocols = new HashMap<Integer, Long>();
        int protocolCount = 0;
        for (int i =0; i < protocols.length; ++i) {
            if (protocols[i].equalsIgnoreCase("coap")) {
                this.protocols.put(protocolCount, PROTOCOL_COAP);
                ++protocolCount;
                LOG.info("This device suppots the firmware update protocol: COAP");
            } else if (protocols[i].equalsIgnoreCase("coaps")) {
                this.protocols.put(protocolCount, PROTOCOL_COAPS);
                ++protocolCount;
                LOG.info("This device suppots the  firmware update protocol: COAPS");
            } else if (protocols[i].equalsIgnoreCase("http")) {
                this.protocols.put(protocolCount, PROTOCOL_HTTP);
                ++protocolCount;
                LOG.info("This device suppots the  firmware update protocol: HTTP");
            } else if (protocols[i].equalsIgnoreCase("https")) {
                this.protocols.put(protocolCount, PROTOCOL_HTTPS);
                ++protocolCount;
                LOG.info("This device suppots the  firmware update protocol: HTTPS");
            }
        }

        if (protocolCount == 0) {
            this.protocols.put(protocolCount, PROTOCOL_COAP);
        }
    }

    @Override
    public ReadResponse read(int resourceid) {
        LOG.info("Read on Device Resource /5/0/" + resourceid);
        switch (resourceid) {
        case 1:
            LOG.info("Device Resource /5/0/" + resourceid + " value: " + getURI());
            return ReadResponse.success(resourceid, getURI());
        case 3:
            LOG.info("Device Resource /5/0/" + resourceid + " value: " + getState());
            return ReadResponse.success(resourceid, getState());
        case 5:
            LOG.info("Device Resource /5/0/" + resourceid + " value: " + getUpdateResult());
            return ReadResponse.success(resourceid, getUpdateResult());
        case 8:
            LOG.info("Device Resource /5/0/" + resourceid + " value: " + getProtocols().toString());
            return ReadResponse.success(resourceid, getProtocols(), Type.INTEGER);
        case 9:
            LOG.info("Device Resource /5/0/" + resourceid + " value: " + getDeliveryMethod());
            return ReadResponse.success(resourceid, getDeliveryMethod());
        default:
            return super.read(resourceid);
        }
    }

    @Override
    public ExecuteResponse execute(int resourceid, String params) {
        LOG.info("Execute on Firmware Update resource " + resourceid);
        if (params != null && params.length() != 0) {
            System.out.println("\t params " + params);
        }
        switch (resourceid) {
            case 2:
                setState(STATE_UPDATING);
                fireResourcesChange(RESOURCE_STATE);
                LOG.info("Updating firmware...");
                Runnable r1 = new Runnable() {

                    public void run (){
                        try {
                            Thread.sleep(5000);
                            setState(STATE_IDLE);
                            fireResourcesChange(RESOURCE_STATE);
                            setUpdateResult(UR_SUCCESS);
                            fireResourcesChange(RESOURCE_UPDATE_RESULT);
                            LOG.info("...firmware update successfully");
                        } catch (Exception error) {
                            System.out.println(error);
                        }
    
                    }
                };
                Thread t1 = new Thread(r1);
                t1.start();
        }
        return ExecuteResponse.success();
    }

    @Override
    public WriteResponse write(int resourceid, LwM2mResource value) {
        LOG.info("Write on Device Resource /5/0/" + resourceid + " value " + value);
        switch (resourceid) {
        case 1:
        try {
            resetUpdateResult();
            setURI((String) value.getValue());
            fireResourcesChange(resourceid);
            setState(STATE_DOWNLOADING);
            fireResourcesChange(RESOURCE_STATE);
            LOG.info("Downloading firmware image... [dummy]");
            Runnable r1 = new Runnable() {

                public void run (){
                    try {
                        Thread.sleep(5000);
                        if(checkURI(getURI())){
                            setState(STATE_DOWNLOADED);
                            fireResourcesChange(RESOURCE_STATE);
                            LOG.info("...firmware image download successfully");
                        } else {
                            setState(STATE_IDLE);
                            fireResourcesChange(RESOURCE_STATE);
                            LOG.info("...firmware image download failed");
                            if (getURI()==null || getURI().isEmpty()) {
                                setUpdateResult(UR_INTEGRITY_CHECK_FAILED);
                                fireResourcesChange(RESOURCE_UPDATE_RESULT);                                
                            } else{
                                setUpdateResult(UR_FAIL_DOWNLOAD);
                                fireResourcesChange(RESOURCE_UPDATE_RESULT);
                            }
                        }
                    } catch (Exception error) {
                        System.out.println(error);
                    }

                }
            };

            Thread t1 = new Thread(r1);
            t1.start();

            return WriteResponse.success();

            } catch (Exception e) {
                LOG.error("Unexpected exception: " + e.getMessage());
                return WriteResponse.internalServerError("Unexpected error");
            } 
            
           
        default:
            return super.write(resourceid, value);
        }
    }

    private void resetUpdateResult(){
        setUpdateResult(UR_INITIAL_VALUE);
        fireResourcesChange(RESOURCE_UPDATE_RESULT);
    }

    private Boolean checkURI(String URI) {
    	return true;
    }


    private int updateResult = 0;

    private int getUpdateResult(){
        return updateResult;
    }

    private void setUpdateResult(int r){
        updateResult = r;
    }

    private String URI;
    
    private String getURI(){
        return URI;
    }

    private void setURI(String u) {
        URI = u;
    }

    private int state = 0;

    private int getState(){
        return state;
    }

    private Map<Integer, Long> getProtocols() {
        return this.protocols;
    }

    private int getDeliveryMethod() {
        return this.deliveryMethod;
    }

    private void setState(int s){
        state = s;
    }

    @Override
    public List<Integer> getAvailableResourceIds(ObjectModel model) {
        return supportedResources;
    }
}
