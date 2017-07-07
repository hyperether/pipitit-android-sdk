package com.hyperether.pipitit.util;

/**
 * Keep all contacts
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/27/2017
 */
public class Constants {

    public final static String PIPITIT_WEB_SERVER = "ws://52.53.86.56:5082";

    public final static String PIPITIT_API_SERVER = "http://52.53.86.56:3000";
    //API URLs
//    final static String REQUEST_DEVICE_CREATE = PIPITIT_API_SERVER + "device";
//    final static String REQUEST_DEVICE_UPDATE = PIPITIT_API_SERVER + "device/";
//    final static String REQUEST_JOB = PIPITIT_API_SERVER + "job/";
//    final static String REQUEST_JOB_CREATE = REQUEST_JOB + "create";


    //web server message type
    public final static String MESSAGE_TYPE_REGISTER = "reg";
    public final static String MESSAGE_TYPE_BYE = "bye";
    public final static String SERVER_MESSAGE_TYPE_REGISTER = "registered";
    public final static String SERVER_MESSAGE_TYPE_PONG = "pong";
    public final static String MESSAGE_TYPE_PING = "ping";

    public final static String INTENT_CAMPAIGN = "pipitit.application.intent.campaign";
    public final static String KEY_TITLE = "pipitit.application.keyTitle";
    public final static String KEY_MESSAGE = "pipitit.application.keyMessage";
    public final static String KEY_NOTIFICATION_ID = "pipitit.application.keyNotId";

    public final static String BROADCAST_ON_PUSH_CLICKED = ".pipitit.application.ON_PUSH_CLICKED";

}
