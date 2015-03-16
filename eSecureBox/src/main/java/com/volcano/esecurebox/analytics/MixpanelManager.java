package com.volcano.esecurebox.analytics;

import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.volcano.esecurebox.VlApplication;
import com.volcano.esecurebox.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Mixpanel manager for capture and send user events to Mixpanel server
 */
public final class MixpanelManager {

    public static final String EVENT_CHANGE_PASSCODE    = "Change Passcode";
    public static final String EVENT_CREATE_ITEM        = "Create Item";
    public static final String EVENT_DISABLE_PASSCODE   = "Disable Passcode";
    public static final String EVENT_EDIT_ITEM          = "Edit Item";
    public static final String EVENT_ENABLE_PASSCODE    = "Enable Passcode";
    public static final String EVENT_GENERATE_PASSWORD  = "Generate Password";
    public static final String EVENT_SHARE_APP          = "Share App";
    public static final String EVENT_CONTACT_SERVER     = "Fail Contact Server";

    private static final String PARAM_CATEGORY          = "Category";
    private static final String PARAM_CONNECTED         = "Connected";
    private static final String PARAM_MILLIS            = "Millisecond";
    private static final String PARAM_NETWORK_INFO      = "Network Info";
    private static final String PARAM_OBJECT_NAME       = "Object Name";

    private static final String TAG = LogUtils.makeLogTag(MixpanelManager.class);
    private static final String JSON_EXCEPTION_MESSAGE = "Error in creating JSONObject";
    private static final String TOKEN = "1a5276829d32fc4b170a18287bce1fd8";

    private MixpanelAPI mMixpanelAPI;
    private boolean mEnabled = true;

    public MixpanelManager() {
        mMixpanelAPI = MixpanelAPI.getInstance(VlApplication.getInstance(), TOKEN);
    }

    /**
     * Push all queued Mixpanel events and People Analytics to Mixpanel servers
     */
    public void flush() {
        mMixpanelAPI.flush();
    }

    /**
     * Track an event without parameter
     * @param eventName The event name in order to track
     */
    public void track(String eventName) {
        if (mEnabled) {
            mMixpanelAPI.track(eventName, null);
        }
    }

    /**
     * Track an event with a set of name/value pairs that describe the properties of event
     * @param eventName The event name in order to track
     * @param properties A JSONObject containing the key value pairs of the properties to include in this event.
     */
    private void track(String eventName, JSONObject properties) {
        if (mEnabled) {
            mMixpanelAPI.track(eventName, properties);
        }
    }

    /**
     * Set Identity for following events
     * @param identity The identity
     */
    public void setIdentity(String identity) {
        mMixpanelAPI.identify(identity);
    }

    /**
     * Track create item event
     * @param category The category
     */
    public void trackCreateItemEvent(String category) {
        final JSONObject params = new JSONObject();

        try {
            params.put(PARAM_CATEGORY, category);

            track(EVENT_CREATE_ITEM, params);
        }
        catch (JSONException e) {
            Log.e(TAG, JSON_EXCEPTION_MESSAGE);
        }
    }

    /**
     * Track fail contact server event
     * @param objectName The Object name query for
     * @param millis The time took for contacting server
     * @param networkInfo The network info
     */
    public void trackFailContactServerEvent(boolean connected, String objectName, float millis, String networkInfo) {
        final JSONObject params = new JSONObject();

        try {
            params.put(PARAM_CONNECTED, connected);
            params.put(PARAM_OBJECT_NAME, objectName);
            params.put(PARAM_MILLIS, millis);
            params.put(PARAM_NETWORK_INFO, networkInfo);

            track(EVENT_CONTACT_SERVER, params);
        }
        catch (JSONException e) {
            Log.e(TAG, JSON_EXCEPTION_MESSAGE);
        }
    }
}
