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
    private CallbackContext scan2dataCallbackContext = null;
    public static String API_DATA = "unitech.scanservice.data";//"android.intent.ACTION_DECODE_DATA";
    private String DATA_EXTRA = "text";//"barcode_string";
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
        if (action.equals("packageName")){
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
            Log.d("scan2data","Start"+ args);
            if (this.receiver==null)
                if (args.optString(0).length()>0){
                    API_DATA = args.optString(0);
                }
            if (args.optString(1).length()>0){
                DATA_EXTRA = args.optString(1);
            }
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent mIntent) {
                    String action = mIntent.getAction();
                    Bundle bundle = mIntent.getExtras();
                    if  (bundle == null ) return;
                    if (action.equals(API_DATA) && mIntent.hasExtra(DATA_EXTRA)){
                        JSONObject jobj = new JSONObject();
                        for (String item : bundle.keySet()){
                            Object obj = bundle.get(item);
                            try {
                                jobj.put(item, obj);
                            }catch (Exception e) {
                            }
                        }
                        sendUpdate(jobj,true);
                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(API_DATA);
            webView.getContext().registerReceiver(this.receiver, filter);
            this.scan2dataCallbackContext = callbackContext;
            return true;
        }
        else if (action.equals("stop")){
            webView.getContext().unregisterReceiver(this.receiver);
            scan2dataCallbackContext = null;
            return true;
        }
        return false;
    }

    private void sendUpdate(JSONObject info, boolean keepCallback) {
        if (this.scan2dataCallbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, info);
            result.setKeepCallback(keepCallback);
            this.scan2dataCallbackContext.sendPluginResult(result);
        }
    }

}


