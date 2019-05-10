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

}
