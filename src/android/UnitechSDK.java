package com.cordova.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.unitech.api.deviceinfo.DeviceInfoCtrl;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class echoes a string called from JavaScript.
 */
public class UnitechSDK extends CordovaPlugin {
    private CallbackContext scan2keyCallbackContext = null;
    public static final String API_DATA = "unitech.scanservice.data";
    private final String DATA_EXTRA = "text";
    static final String RESULT = "Result";
    public static final String BUNDLE_ERROR_CODE = "errorCode";
    public static final String BUNDLE_ERROR_MSG = "errorMsg";
    public static final int RESULT_CODE_ERROR = 1;
    public static final int RESULT_CODE_SUCCESS = 0;
    private BroadcastReceiver receiver;
    ExecutorService executorService;
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        executorService = Executors.newSingleThreadExecutor();
        Log.d("UnitechSDK", "Initializing UnitechSDK");
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d("UnitechSDK", "execute "+action);
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        else if (action.equals("packageName")){
            callbackContext.success(cordova.getActivity().getApplicationContext().getPackageName());
            return true;
        }
        else if (action.equals("serialNumber")){
            executorService.execute(new Runnable() {
                public void run() {
                    DeviceInfoCtrl infoCtrl = new DeviceInfoCtrl(webView.getContext());
                    Bundle bundle = infoCtrl.getDeviceSerialNumber();
                    if (bundle == null) {
                        callbackContext.error("NULL SDK return");
                    }
                    else if (bundle.getInt(BUNDLE_ERROR_CODE,RESULT_CODE_ERROR)==RESULT_CODE_SUCCESS) {
                        callbackContext.success(bundle.getString("getDeviceSerialNumber"));
                    }else{
                        callbackContext.error(bundle.getString(BUNDLE_ERROR_MSG));
                    }
                }
            });
            return true;
        }
        else if (action.equals("start")){
            if (this.receiver==null)
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent mIntent) {
                    String action = mIntent.getAction();
                    Bundle bundle = mIntent.getExtras();
                    if  (bundle == null ) return;
                    switch (action) {
                        case API_DATA:
                            if(mIntent.hasExtra(DATA_EXTRA)){
                                String resultText = mIntent.getStringExtra(DATA_EXTRA);
                                JSONObject obj = new JSONObject();
                                try {
                                    obj.put("action", action);
                                    obj.put("bundle", bundle);
                                } catch (JSONException e) {
                                    LOG.e("SDK", e.getMessage(), e);
                                }
                                sendUpdate(obj,true);
                            }
                            break;
                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(API_DATA);
            //filter.addAction(API_DATABYTE);
            //filter.addAction(API_DATALENGTH);
            webView.getContext().registerReceiver(this.receiver, filter);
            this.scan2keyCallbackContext = callbackContext;
            return true;
        }
        else if (action.equals("stop")){
            webView.getContext().unregisterReceiver(this.receiver);
            scan2keyCallbackContext = null;
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
    private void sendUpdate(JSONObject info, boolean keepCallback) {
        if (this.scan2keyCallbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, info);
            result.setKeepCallback(keepCallback);
            this.scan2keyCallbackContext.sendPluginResult(result);
        }
    }

}


