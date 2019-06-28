package org.cpqd.iotagent.lwm2m.objects;

public class FirmwareUpdatePath {

    /**
     * URI from where the device can download the
     * firmware package by an alternative mechanism. As
     * soon the device has received the Package URI it
     * performs the download at the next practical
     * opportunity. The URI format is defined in RFC 3986.
     */
    public static final String PACKAGE_URI = "/5/0/1";


    /**
     * Updates firmware by using the firmware package
     * stored in Package, or, by using the firmware
     * downloaded from the Package URI. This Resource is
     * only executable when the value of the State Resource
     * is Downloaded.
     */
    public static final String UPDATE = "/5/0/2";


    /**
     * Indicates current state with respect to this firmware
     * update. This value is set by the lwm2m Client. 0:
     * Idle (before downloading or after successful
     * updating)
     */
    public static final String STATE = "/5/0/3";

    /**
     * Contains the result of downloading or updating the
     * firmware
     */
    public static final String UPDATE_RESULT = "/5/0/5";

    /**
     * This resource indicates what protocols the LwM2M Client implements to
     * retrieve firmware images.
     */
    public static final String FIRMWARE_UPDATE_PROTOCOL_SUPPORT = "/5/0/8";

    /**
     * The LwM2M Client uses this resource to indicate its support for transferring firmware
     * images to the client either via the Package Resource (=push) or via the Package URI
     * Resource (=pull) mechanism.
     */
    public static final String FIRMWARE_UPDATE_DELIVERY_METHOD = "/5/0/9";

    /**
     * Values that represents the supported protocol to execute the firmware
     * update process
     */
    public static final int PROTOCOL_COAP = 0;
    public static final int PROTOCOL_COAPS = 1;
    public static final int PROTOCOL_HTTP = 2;
    public static final int PROTOCOL_HTTPS = 3;

    /**
     * Values that represents the supported delivery method to transfer the firmware
     * image
     */
    public static final int DELIVERY_METHOD_PULL = 0;
    public static final int DELIVERY_METHOD_PUSH = 1;
    public static final int DELIVERY_METHOD_BOTH = 2;
}
