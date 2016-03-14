/**
 * @title
 * @Description
 * @author
 * @date 2015年11月18日 下午11:02:31 
 * @version V1.0  
 */

package com.ihealth.devices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.example.devicelibtest.R;
import com.ihealth.communication.control.Hs3Control;
import com.ihealth.communication.control.Hs4Control;
import com.ihealth.communication.control.HsProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class HS4 extends Activity implements OnClickListener {
    private TextView tv_return;
    private String deviceMac;
    private static String TAG = "HS4";
    private int clientId;
    private Hs4Control mHs4Control;
    private int userid = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hs4);
        initView();
        Intent intent = getIntent();
        deviceMac = intent.getStringExtra("mac");
        clientId = iHealthDevicesManager.getInstance().registerClientCallback(mIHealthDevicesCallback);
        /* Limited wants to receive notification specified device */
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(clientId,
                iHealthDevicesManager.TYPE_HS4);
        /* Get hs4 controller */
        mHs4Control = iHealthDevicesManager.getInstance().getHs4Control(deviceMac);
    }

    private void initView() {
        tv_return = (TextView) findViewById(R.id.tv_return);
        findViewById(R.id.btn_getOfflineData).setOnClickListener(this);
        findViewById(R.id.btn_disconnect).setOnClickListener(this);
        findViewById(R.id.btn_startMeasure).setOnClickListener(this);
    }

    iHealthDevicesCallback mIHealthDevicesCallback = new iHealthDevicesCallback() {
        public void onScanDevice(String mac, String deviceType) {
        };

        public void onDeviceConnectionStateChange(String mac, String deviceType, int status) {
            Log.e(TAG, "mac:" + mac + "-deviceType:" + deviceType + "-status:" + status);

            switch (status) {
                case iHealthDevicesManager.DEVICE_STATE_DISCONNECTED:
                    mHs4Control = null;
                    Toast.makeText(HS4.this, "The device disconnect", Toast.LENGTH_LONG).show();
                    noticeString = "The device disconnect";
                    Message message2 = new Message();
                    message2.what = 1;
                    message2.obj = noticeString;
                    mHandler.sendMessage(message2);
                    break;

                default:
                    break;
            }
        };

        public void onDeviceNotify(String mac, String deviceType, String action, String message) {
            Log.d(TAG, "mac:" + mac + "--type:" + deviceType + "--action:" + action + "--message:" + message);
            JSONTokener jsonTokener = new JSONTokener(message);
            switch (action) {
                case HsProfile.ACTION_HISTORICAL_DATA_HS:
                    try {
                        JSONObject object = (JSONObject) jsonTokener.nextValue();
                        JSONArray jsonArray = object.getJSONArray(HsProfile.HISTORDATA__HS);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            String dateString = jsonObject.getString(HsProfile.MEASUREMENT_DATE_HS);
                            float weight = (float) jsonObject.getDouble(HsProfile.WEIGHT_HS);
                            Log.d(TAG, "date:" + dateString + "-weight:" + weight);
                        }
                        Message message2 = new Message();
                        message2.what = 1;
                        message2.obj = message;
                        mHandler.sendMessage(message2);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case HsProfile.ACTION_LIVEDATA_HS:
                    try {
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        float weight = (float) jsonObject.getDouble(HsProfile.LIVEDATA_HS);
                        Log.d(TAG, "weight:" + weight);
                        noticeString = "weight:" + weight;
                        Message message3 = new Message();
                        message3.what = 1;
                        message3.obj = noticeString;
                        mHandler.sendMessage(message3);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case HsProfile.ACTION_ONLINE_RESULT_HS:
                    try {
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        float weight = (float) jsonObject.getDouble(HsProfile.WEIGHT_HS);
                        Log.d(TAG, "weight:" + weight);
                        noticeString = "weight:" + weight;
                        Message message3 = new Message();
                        message3.what = 1;
                        message3.obj = noticeString;
                        mHandler.sendMessage(message3);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case HsProfile.ACTION_NO_HISTORICALDATA:
                    noticeString = "no history data";
                    Message message2 = new Message();
                    message2.what = 1;
                    message2.obj = message;
                    mHandler.sendMessage(message2);
                    break;
                case HsProfile.ACTION_ERROR_HS:
                    try {
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        int err = jsonObject.getInt(HsProfile.ERROR_NUM_HS);
                        Log.d(TAG, "weight:" + err);
                        noticeString = "err:" + err;
                        Message message3 = new Message();
                        message3.what = 1;
                        message3.obj = noticeString;
                        mHandler.sendMessage(message3);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        };
    };
    String noticeString = "";
    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    tv_return.setText((String) msg.obj);
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        iHealthDevicesManager.getInstance().unRegisterClientCallback(clientId);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_getOfflineData:
                if (mHs4Control == null) {
                    Toast.makeText(HS4.this, "mHs4Control == null", Toast.LENGTH_LONG);

                } else {
                    mHs4Control.getOfflineData();
                }
                break;
            case R.id.btn_startMeasure:
                if (mHs4Control == null) {
                    Toast.makeText(HS4.this, "mHs4Control == null", Toast.LENGTH_LONG);

                } else {
                    mHs4Control.measureOnline(1, userid);
                }
                break;
            case R.id.btn_disconnect:
                if (mHs4Control == null) {
                    Toast.makeText(HS4.this, "mHs4Control == null", Toast.LENGTH_LONG);

                } else {
                    mHs4Control.disconnect();
                }
                break;
            default:
                break;

        }
    }

}
