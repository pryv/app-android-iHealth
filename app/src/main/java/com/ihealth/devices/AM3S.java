package com.ihealth.devices;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ihealth.R;
import com.ihealth.communication.control.Am3sControl;
import com.ihealth.communication.control.AmProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.utils.Connector;
import com.pryv.api.StreamsCallback;
import com.pryv.api.StreamsSupervisor;
import com.pryv.api.model.Stream;

import org.joda.time.field.PreciseDateTimeField;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class AM3S extends Activity {
    private Am3sControl am3sControl;
    private String mac;
    private int clientId;
    private TextView tv_return;
    private Stream randomStream;
    private Stream userStream;
    private Stream stepsStream;
    private Stream activitiesStream;
    private Stream sleepStream;
    private Stream batteryStream;
    private Stream stageStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_am3_s);

        Connector.initiateConnection();

        randomStream = Connector.saveStream("AM3S_random", "AM3S_random");
        userStream = Connector.saveStream("AM3S_userAge", "AM3S_userAge");
        stepsStream = Connector.saveStream("AM3S_realSteps", "AM3S_realSteps");
        activitiesStream = Connector.saveStream("AM3S_syncActivities", "AM3S_syncActivities");
        sleepStream = Connector.saveStream("AM3S_syncSleeps", "AM3S_syncSleeps");
        batteryStream = Connector.saveStream("AM3S_battery", "AM3S_battery");
        stageStream = Connector.saveStream("AM3S_syncStage", "AM3S_syncStage");

        clientId = iHealthDevicesManager.getInstance().registerClientCallback(iHealthDevicesCallback);

        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(clientId,
                iHealthDevicesManager.TYPE_AM3S);

        Intent intent = getIntent();
        this.mac = intent.getStringExtra("mac");

        am3sControl = iHealthDevicesManager.getInstance().getAm3sControl(this.mac);

        tv_return = (TextView) findViewById(R.id.tv_return);
    }

    @Override
    protected void onDestroy() {
        reset();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        reset();
        super.onBackPressed();
    }

    private void reset() {
        if (am3sControl != null)
            am3sControl.disconnect();
        iHealthDevicesManager.getInstance().unRegisterClientCallback(clientId);
    }


    private iHealthDevicesCallback iHealthDevicesCallback = new iHealthDevicesCallback() {

        @Override
        public void onScanDevice(String mac, String deviceType) {
        }

        @Override
        public void onDeviceConnectionStateChange(String mac, String deviceType, int status) {
            if (status == 2) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                reset();
                finish();
            }
        }

        @Override
        public void onDeviceNotify(String mac, String deviceType, String action, String message) {

            switch (action) {
                case AmProfile.ACTION_QUERY_STATE_AM:
                    try {
                        JSONObject info = new JSONObject(message);
                        String battery = info.getString(AmProfile.QUERY_BATTERY_AM);
                        tv_return.setText("Battery: "+battery);
                        Connector.saveEvent(batteryStream.getId(), "ratio/percent", battery);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case AmProfile.ACTION_USERID_AM:
                    try {
                        JSONObject info = new JSONObject(message);
                        String id = info.getString(AmProfile.USERID_AM);
                        tv_return.setText("User id: "+id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case AmProfile.ACTION_GET_ALARMNUM_AM:
                    try {
                        JSONObject info = new JSONObject(message);
                        String alarm_num = info.getString(AmProfile.GET_ALARMNUM_AM);
                        tv_return.setText("Alarm num: "+alarm_num);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case AmProfile.ACTION_SYNC_STAGE_DATA_AM:
                    try {
                        //TODO: time, activity with duration
                        JSONObject info = new JSONObject(message);
                        tv_return.setText("Sync stage data...");
                        Connector.saveEvent(stageStream.getId(), "note/txt", info.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case AmProfile.ACTION_SYNC_SLEEP_DATA_AM:
                    try {
                        //TODO: time, activity with duration
                        JSONObject info = new JSONObject(message);
                        JSONArray sleep_info = info.getJSONArray(AmProfile.SYNC_SLEEP_DATA_AM);
                        JSONArray activities = sleep_info.getJSONObject(0).getJSONArray(AmProfile.SYNC_SLEEP_EACH_DATA_AM);

                        tv_return.setText("Sync " + activities.length() + " sleeps...");

                        for(int i=0; i<activities.length(); i++) {
                            JSONObject activity = activities.getJSONObject(i);
                            String time = activity.getString(AmProfile.SYNC_SLEEP_DATA_TIME_AM);
                            String level = activity.getString(AmProfile.SYNC_SLEEP_DATA_LEVEL_AM);
                            Connector.saveEvent(sleepStream.getId(), "note/txt", time);
                            Connector.saveEvent(sleepStream.getId(), "count/generic", level);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case AmProfile.ACTION_SYNC_ACTIVITY_DATA_AM:
                    try {
                        //TODO: time, activity with duration
                        JSONObject info = new JSONObject(message);
                        JSONArray activity_info = info.getJSONArray(AmProfile.SYNC_ACTIVITY_DATA_AM);
                        JSONArray activities = activity_info.getJSONObject(0).getJSONArray(AmProfile.SYNC_ACTIVITY_EACH_DATA_AM);

                        tv_return.setText("Sync "+activities.length()+" activities...");

                        for(int i=0; i<activities.length(); i++) {
                            JSONObject activity = activities.getJSONObject(i);
                            String time = activity.getString(AmProfile.SYNC_ACTIVITY_DATA_TIME_AM);
                            String stepLength = activity.getString(AmProfile.SYNC_ACTIVITY_DATA_STEP_LENGTH_AM);
                            String steps = activity.getString(AmProfile.SYNC_ACTIVITY_DATA_STEP_AM);
                            String calories = activity.getString(AmProfile.SYNC_ACTIVITY_DATA_CALORIE_AM);

                            Connector.saveEvent(activitiesStream.getId(), "note/txt", time);
                            Connector.saveEvent(activitiesStream.getId(), "time/min", stepLength);
                            Connector.saveEvent(activitiesStream.getId(), "count/steps", steps);
                            Connector.saveEvent(activitiesStream.getId(), "energy/cal", calories);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case AmProfile.ACTION_SYNC_REAL_DATA_AM:
                    try {
                        JSONObject info = new JSONObject(message);
                        String real_info = info.getString(AmProfile.SYNC_REAL_STEP_AM);
                        tv_return.setText("Real steps: "+real_info);
                        Connector.saveEvent(stepsStream.getId(), "count/generic", real_info);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case AmProfile.ACTION_GET_USERINFO_AM:
                    try {
                        JSONObject info = new JSONObject(message);
                        String user_info = info.getString(AmProfile.GET_USER_AGE_AM);
                        tv_return.setText("User age: "+user_info);
                        Connector.saveEvent(userStream.getId(), "count/generic", user_info);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case AmProfile.ACTION_GET_ALARMINFO_AM:
                    try {
                        JSONObject info = new JSONObject(message);
                        String alarm_id = info.getString(AmProfile.GET_ALARM_ID_AM);
                        tv_return.setText("Alarm id: " + alarm_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case AmProfile.ACTION_SET_USERID_SUCCESS_AM:
                    String obj = "Set ID success";
                    tv_return.setText(obj);
                    break;
                case AmProfile.ACTION_GET_RANDOM_AM:
                    try {
                        JSONObject info = new JSONObject(message);
                        String random = info.getString(AmProfile.GET_RANDOM_AM);

                        tv_return.setText("Generated random: "+random);
                        Connector.saveEvent(randomStream.getId(), "count/generic", random);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void getBattery(View v) {
        am3sControl.queryAMState();
    }

    public void getUserId(View v) {
        am3sControl.getUserId();
    }

    public void getAlarmNum(View v) {
        am3sControl.getAlarmClockNum();
    }

    public void syncStage(View v) {
        am3sControl.syncStageReprotData();
    }

    public void syncSleep(View v) {
        am3sControl.syncSleepData();
    }

    public void syncActivity(View v) {
        am3sControl.syncActivityData();
    }

    public void syncReal(View v) {
        am3sControl.syncRealData();
    }

    public void getUserInfo(View v) {
        am3sControl.getUserInfo();
    }

    public void getAlarmInfo(View v) {
        am3sControl.checkAlarmClock(1);
    }

    public void setUserId(View v) {
        am3sControl.setUserId(1);
    }

    public void sendRandom(View v) {
        am3sControl.sendRandom();
    }

}
