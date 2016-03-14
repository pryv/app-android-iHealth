/**
 * @title
 * @Description
 * @author
 * @date 2015年11月18日 下午1:37:14 
 * @version V1.0  
 */

package com.ihealth.devices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.example.devicelibtest.R;
import com.ihealth.communication.control.Hs5Control;
import com.ihealth.communication.control.HsProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HS5wifi extends Activity implements OnClickListener {
    private int clientId;
    private Hs5Control mHs5Control;
    private static String TAG = "HS5wifi";
    private TextView tv_return;
    private int position; // the position user in scale
    private int emptyPosition; // the first empty position in scale
    private int[] status; // the status of position.
    private int userid = 123;
    private int deletePosition; // the position of delete user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hs5_wifi);
        initView();
        Intent intent = getIntent();
        String deviceMac = intent.getStringExtra("mac");
        clientId = iHealthDevicesManager.getInstance().registerClientCallback(mIHealthDevicesCallback);
        /* Limited wants to receive notification specified device */
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(clientId,
                iHealthDevicesManager.TYPE_HS5);
        /* Get hs5bt controller */
        mHs5Control = iHealthDevicesManager.getInstance().getHs5Control(deviceMac);
    }

    private void initView() {
        tv_return = (TextView) findViewById(R.id.tv_return);
        findViewById(R.id.btn_creatManagement).setOnClickListener(this);
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_update).setOnClickListener(this);
        findViewById(R.id.btn_getOfflineData).setOnClickListener(this);
        findViewById(R.id.btn_startMeasure).setOnClickListener(this);
        findViewById(R.id.btn_disconnect).setOnClickListener(this);

    }

    iHealthDevicesCallback mIHealthDevicesCallback = new iHealthDevicesCallback() {
        public void onScanDevice(String mac, String deviceType) {
        };

        public void onDeviceConnectionStateChange(String mac, String deviceType, int status) {
            Log.e(TAG, "mac:" + mac + "-deviceType:" + deviceType + "-status:" + status);

            switch (status) {
                case iHealthDevicesManager.DEVICE_STATE_DISCONNECTED:
                    noticeString = "The device disconnect";
                    Message message2 = new Message();
                    message2.what = 1;
                    message2.obj = noticeString;
                    mHandler.sendMessage(message2);
                    mHs5Control = null;
                    break;
                case iHealthDevicesManager.DEVICE_STATE_CONNECTED:
                    noticeString = "The device connected";
                    Message message3 = new Message();
                    message3.what = 1;
                    message3.obj = noticeString;
                    mHandler.sendMessage(message3);
                    mHs5Control = iHealthDevicesManager.getInstance().getHs5Control(mac);
                    break;
                default:
                    break;
            }
        };

        public void onDeviceNotify(String mac, String deviceType, String action, String message) {
            Log.d(TAG, "mac:" + mac + "--type:" + deviceType + "--action:" + action + "--message:" + message);
            JSONTokener jsonTokener = new JSONTokener(message);
            switch (action) {
                case HsProfile.ACTION_MANAGEMENT_HS:
                    try {
                        JSONObject object = (JSONObject) jsonTokener.nextValue();
                        int userInfoInscale = object.getInt(HsProfile.USERINFO_IN_HS);
                        position = object.getInt(HsProfile.USERPOSITION_HS);
                        emptyPosition = object.getInt(HsProfile.EMPTYPOSITION_HS);
                        JSONArray stateArray = object.getJSONArray(HsProfile.STATUS_HS);
                        status = new int[stateArray.length()];
                        for (int i = 0; i < stateArray.length(); i++) {
                            status[i] = stateArray.getInt(i);
                            Log.i(TAG, "status:" + status[i]);
                        }
                        switch (userInfoInscale) {
                            case 3:
                                noticeString = " The user isn't in scale but  the  scale is full need delete";
                                position = 0;
                                break;
                            case 2:
                                noticeString = "The user isn't in scale and the scale has empty position";
                                break;
                            case 1:
                                noticeString = "the user is in scale";
                                break;

                            default:
                                break;
                        }
                        Message message2 = new Message();
                        message2.what = 1;
                        message2.obj = noticeString;
                        mHandler.sendMessage(message2);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case HsProfile.ACTION_ADDUSER_HS:
                    try {
                        JSONObject object = (JSONObject) jsonTokener.nextValue();
                        boolean result = object.getBoolean(HsProfile.ADDUSER_RESULT_HS);
                        if (result) {
                            noticeString = "Add user success!";
                            position = emptyPosition;
                        } else {
                            noticeString = "Add user fail";
                        }
                        Message message2 = new Message();
                        message2.what = 1;
                        message2.obj = noticeString;
                        mHandler.sendMessage(message2);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case HsProfile.ACTION_UPDATEUSER_HS:
                    try {
                        JSONObject object = (JSONObject) jsonTokener.nextValue();
                        boolean result = object.getBoolean(HsProfile.UPDATEUSER_RESULT_HS);
                        if (result) {
                            noticeString = "Update user success!";
                        } else {
                            noticeString = "Update user fail";
                        }
                        Message message2 = new Message();
                        message2.what = 1;
                        message2.obj = noticeString;
                        mHandler.sendMessage(message2);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case HsProfile.ACTION_DELETEUSER_HS:
                    try {
                        JSONObject object = (JSONObject) jsonTokener.nextValue();
                        boolean result = object.getBoolean(HsProfile.DELETEUSER_RESULT_HS);
                        if (result) {
                            noticeString = "Delete user success!";
                            position = deletePosition;
                        } else {
                            noticeString = "Delete user fail";
                        }
                        Message message2 = new Message();
                        message2.what = 1;
                        message2.obj = noticeString;
                        mHandler.sendMessage(message2);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case HsProfile.ACTION_HISTORICAL_DATA_HS:
                    try {
                        JSONObject object = (JSONObject) jsonTokener.nextValue();
                        JSONArray jsonArray = object.getJSONArray(HsProfile.HISTORDATA__HS);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            String dateString = jsonObject.getString(HsProfile.MEASUREMENT_DATE_HS);
                            float weight = (float) jsonObject.getDouble(HsProfile.WEIGHT_HS);
                            float fat = (float) jsonObject.getDouble(HsProfile.FAT_HS);
                            float water = (float) jsonObject.getDouble(HsProfile.WATER_HS);
                            float muscle = (float) jsonObject.getDouble(HsProfile.MUSCLE_HS);
                            float skeleton = (float) jsonObject.getDouble(HsProfile.SKELETON_HS);
                            int fatLevel = jsonObject.getInt(HsProfile.FATELEVEL_HS);
                            int dci = jsonObject.getInt(HsProfile.DCI_HS);
                            Log.d(TAG, "date:" + dateString + "-weight:" + weight + "-fat:" + fat + "-water:" + water
                                    + "-muscle:" + muscle
                                    + "-skeleton:" + skeleton + "fatLel:" + fatLevel + "-dci:" + dci);
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
                case HsProfile.ACTION_NO_HISTORICALDATA:
                    noticeString = "no history data";
                    Message message2 = new Message();
                    message2.what = 1;
                    message2.obj = message;
                    mHandler.sendMessage(message2);
                    break;
                case HsProfile.ACTION_LIVEDATA_HS:
                    try {
                        JSONObject jsonobjec = (JSONObject) jsonTokener.nextValue();
                        float weight = (float) jsonobjec.getDouble(HsProfile.LIVEDATA_HS);
                        noticeString = "weight: " + weight;
                        Message message3 = new Message();
                        message3.what = 1;
                        message3.obj = noticeString;
                        mHandler.sendMessage(message3);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    break;
                case HsProfile.ACTION_STABLEDATA_HS:
                    try {
                        JSONObject jsonobjec = (JSONObject) jsonTokener.nextValue();
                        float weight = (float) jsonobjec.getDouble(HsProfile.STABLEDATA_HS);
                        noticeString = "weight: " + weight;
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
                        float fat = (float) jsonObject.getDouble(HsProfile.FAT_HS);
                        float water = (float) jsonObject.getDouble(HsProfile.WATER_HS);
                        float muscle = (float) jsonObject.getDouble(HsProfile.MUSCLE_HS);
                        float skeleton = (float) jsonObject.getDouble(HsProfile.SKELETON_HS);
                        int fateLevel = jsonObject.getInt(HsProfile.FATELEVEL_HS);
                        int dci = jsonObject.getInt(HsProfile.DCI_HS);
                        Log.d(TAG, "weight:" + weight + "-fat:" + fat + "-water:" + water
                                + "-muscle:" + muscle
                                + "-skeleton:" + skeleton + "fatLel:" + fateLevel + "-dci:" + dci);
                        Message message3 = new Message();
                        message3.what = 1;
                        message3.obj = message;
                        mHandler.sendMessage(message3);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case HsProfile.ACTION_ERROR_HS:
                    try {
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        int err = jsonObject.getInt(HsProfile.ERROR_NUM_HS);
                        switch (err) {
                            case 600: // communication error need disconnect
                                mHs5Control.disconnect();
                                break;
                            case 700: // timeout error need disconnect
                                mHs5Control.disconnect();
                                break;
                            default:
                                break;
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
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

    public void onClick(android.view.View v) {
        switch (v.getId()) {
            case R.id.btn_creatManagement:
                if (mHs5Control == null) {
                    Log.i(TAG, "mHs5Control == null");
                    Toast.makeText(HS5wifi.this, "mHs5Control == null", Toast.LENGTH_LONG).show();

                } else {
                    mHs5Control.creatManagement(userid);
                }

                break;
            case R.id.btn_add:
                if (mHs5Control == null) {
                    Log.i(TAG, "mHs5Control == null");
                    Toast.makeText(HS5wifi.this, "mHs5Control == null", Toast.LENGTH_LONG).show();
                } else {
                    mHs5Control.WriteUserToScale(emptyPosition, userid, 25, 150, 1, 0);
                }

                break;
            case R.id.btn_delete:
                if (mHs5Control == null) {
                    Log.i(TAG, "mHs5Control == null");
                    Toast.makeText(HS5wifi.this, "mHs5Control == null", Toast.LENGTH_LONG).show();

                } else {
                    if (position != -1) {
                        deletePosition = position;
                    } else {
                        deletePosition = 0;

                    }
                    mHs5Control.DeleteUserInScale(deletePosition); // delete the first user
                }
                break;
            case R.id.btn_update:
                if (mHs5Control == null) {
                    Log.i(TAG, "mHs5Control == null");
                    Toast.makeText(HS5wifi.this, "mHs5Control == null", Toast.LENGTH_LONG).show();

                } else {
                    mHs5Control.updateUserInfo(position, userid, 26, 170, 3, 0);
                }
                break;
            case R.id.btn_getOfflineData:
                if (mHs5Control == null) {
                    Log.i(TAG, "mHs5Control == null");
                    Toast.makeText(HS5wifi.this, "mHs5Control == null", Toast.LENGTH_LONG).show();

                } else {
                    mHs5Control.getOfflineData(position, userid);
                }
                break;
            case R.id.btn_startMeasure:
                if (mHs5Control == null) {
                    Log.i(TAG, "mHs5Control == null");
                    Toast.makeText(HS5wifi.this, "mHs5Control == null", Toast.LENGTH_LONG).show();

                } else {
                    mHs5Control.startMeasure(position, userid);
                }
                break;
            case R.id.btn_disconnect:
                if (mHs5Control == null) {
                    Log.i(TAG, "mHs5Control == null");
                    Toast.makeText(HS5wifi.this, "mHs5Control == null", Toast.LENGTH_LONG).show();

                } else {
                    mHs5Control.disconnect();
                }
                break;
            default:
                break;
        }
    };

    protected void onDestroy() {
        super.onDestroy();
        iHealthDevicesManager.getInstance().unRegisterClientCallback(clientId);
    };

}
