package com.ihealth.devices;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ihealth.R;
import com.ihealth.communication.control.Am3sControl;
import com.ihealth.communication.control.AmProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;
import com.ihealth.utils.Connector;
import com.pryv.api.model.Stream;

import org.json.JSONException;
import org.json.JSONObject;

public class AM3S extends Activity implements OnClickListener{

	private static final String TAG = "AM3SActivity";
	
	private Am3sControl am3sControl;
	private String mac;
	private int clientId;
	private TextView tv_return;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_am3_s);

		Connector.initiateConnection();

		clientId = iHealthDevicesManager.getInstance().registerClientCallback(iHealthDevicesCallback);
		
		iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(clientId,
                iHealthDevicesManager.TYPE_AM3S);
		
		Intent intent = getIntent();
		this.mac = intent.getStringExtra("mac");
		
		am3sControl = iHealthDevicesManager.getInstance().getAm3sControl(this.mac);
		//button
		findViewById(R.id.btn_GetBattery).setOnClickListener(this);
		findViewById(R.id.btn_GetUserId).setOnClickListener(this);
		findViewById(R.id.btn_GetAlarmNum).setOnClickListener(this);
		findViewById(R.id.btn_SyncStage).setOnClickListener(this);
		findViewById(R.id.btn_SyncSleep).setOnClickListener(this);
		findViewById(R.id.btn_SyncActivity).setOnClickListener(this);
		findViewById(R.id.btn_SyncReal).setOnClickListener(this);
		findViewById(R.id.btn_GetUserInfo).setOnClickListener(this);
		findViewById(R.id.btn_GetAlarmInfo).setOnClickListener(this);
		findViewById(R.id.btn_SetUserId).setOnClickListener(this);
		findViewById(R.id.btn_SendRandom).setOnClickListener(this);
		findViewById(R.id.btn_Disconnect).setOnClickListener(this);
		tv_return = (TextView)findViewById(R.id.tv_return);
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		iHealthDevicesManager.getInstance().unRegisterClientCallback(clientId);
	}


	private iHealthDevicesCallback iHealthDevicesCallback = new iHealthDevicesCallback() {

		@Override
		public void onScanDevice(String mac, String deviceType) {}

		@Override
		public void onDeviceConnectionStateChange(String mac, String deviceType, int status) {}

		@Override
		public void onDeviceNotify(String mac, String deviceType, String action, String message) {

			Stream s = Connector.saveStream(action,"AM3S "+action);
			Connector.saveEvent(s.getId(), "note/txt", message);

			switch (action) {
			case AmProfile.ACTION_QUERY_STATE_AM:
				try {
					JSONObject info = new JSONObject(message);
					String battery =info.getString(AmProfile.QUERY_BATTERY_AM);
					Message msg = new Message();
					msg.what = HANDLER_MESSAGE;
					msg.obj = "battery: " + battery;
					myHandler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;

			case AmProfile.ACTION_USERID_AM:
				try {
					JSONObject info = new JSONObject(message);
					String id =info.getString(AmProfile.USERID_AM);
					Message msg = new Message();
					msg.what = HANDLER_MESSAGE;
					msg.obj = "User ID: " + id;
					myHandler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case AmProfile.ACTION_GET_ALARMNUM_AM:
				try {
					JSONObject info = new JSONObject(message);
					String alarm_num =info.getString(AmProfile.GET_ALARMNUM_AM);
					Message msg = new Message();
					msg.what = HANDLER_MESSAGE;
					msg.obj = "Alarm Num: " + alarm_num;
					myHandler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case AmProfile.ACTION_SYNC_STAGE_DATA_AM:
				try {
					JSONObject info = new JSONObject(message);
					String stage_info =info.getString(AmProfile.SYNC_STAGE_DATA_AM);
					Message msg = new Message();
					msg.what = HANDLER_MESSAGE;
					msg.obj = "Stage Data: " + stage_info;
					myHandler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case AmProfile.ACTION_SYNC_SLEEP_DATA_AM:
				try {
					JSONObject info = new JSONObject(message);
					String stage_info =info.getString(AmProfile.SYNC_SLEEP_DATA_AM);
					Message msg = new Message();
					msg.what = HANDLER_MESSAGE;
					msg.obj = "Sleep Data: " + stage_info;
					myHandler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case AmProfile.ACTION_SYNC_ACTIVITY_DATA_AM:
				try {
					JSONObject info = new JSONObject(message);
					String activity_info =info.getString(AmProfile.SYNC_ACTIVITY_DATA_AM);
					Message msg = new Message();
					msg.what = HANDLER_MESSAGE;
					msg.obj = "Activity Data: " + activity_info;
					myHandler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case AmProfile.ACTION_SYNC_REAL_DATA_AM:
				try {
					JSONObject info = new JSONObject(message);
					String real_info =info.getString(AmProfile.SYNC_REAL_STEP_AM);
					Message msg = new Message();
					msg.what = HANDLER_MESSAGE;
					msg.obj = "Real Step: " + real_info;
					myHandler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case AmProfile.ACTION_GET_USERINFO_AM:
				try {
					JSONObject info = new JSONObject(message);
					String user_info =info.getString(AmProfile.GET_USER_AGE_AM);
					Message msg = new Message();
					msg.what = HANDLER_MESSAGE;
					msg.obj = "User Age: " + user_info;
					myHandler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case AmProfile.ACTION_GET_ALARMINFO_AM:
				try {
					JSONObject info = new JSONObject(message);
					String alarm_id =info.getString(AmProfile.GET_ALARM_ID_AM);
					Message msg = new Message();
					msg.what = HANDLER_MESSAGE;
					msg.obj = "Alarm ID: " + alarm_id;
					myHandler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case AmProfile.ACTION_SET_USERID_SUCCESS_AM:
				Message msg = new Message();
				msg.what = HANDLER_MESSAGE;
				msg.obj = "Set ID success";
				myHandler.sendMessage(msg);
				break;
			case AmProfile.ACTION_GET_RANDOM_AM:
				try {
					JSONObject info = new JSONObject(message);
					String random =info.getString(AmProfile.GET_RANDOM_AM);
					Message msg1 = new Message();
					msg1.what = HANDLER_MESSAGE;
					msg1.obj = "Random: " + random;
					myHandler.sendMessage(msg1);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		switch (id) {
		case R.id.btn_GetBattery:
			if (am3sControl != null) {
				am3sControl.queryAMState();
			}else
				Toast.makeText(this, "am3sControl == null", Toast.LENGTH_LONG).show();
			break;
		case R.id.btn_GetUserId:
			if (am3sControl != null) {
				am3sControl.getUserId();
			}else
				Toast.makeText(this, "am3sControl == null", Toast.LENGTH_LONG).show();
			break;
		case R.id.btn_GetAlarmNum:
			if (am3sControl != null) {
				am3sControl.getAlarmClockNum();
			}else
				Toast.makeText(this, "am3sControl == null", Toast.LENGTH_LONG).show();
			break;
		case R.id.btn_SyncStage:
			if (am3sControl != null) {
				am3sControl.syncStageReprotData();
			}else
				Toast.makeText(this, "am3sControl == null", Toast.LENGTH_LONG).show();
			break;
		case R.id.btn_SyncSleep:
			if (am3sControl != null) {
				am3sControl.syncSleepData();
			}else
				Toast.makeText(this, "am3sControl == null", Toast.LENGTH_LONG).show();
			break;
		case R.id.btn_SyncActivity:
			if (am3sControl != null) {
				am3sControl.syncActivityData();
			}else
				Toast.makeText(this, "am3sControl == null", Toast.LENGTH_LONG).show();
			break;
		case R.id.btn_SyncReal:
			if (am3sControl != null) {
				am3sControl.syncRealData();
			}else
				Toast.makeText(this, "am3sControl == null", Toast.LENGTH_LONG).show();
			break;
		case R.id.btn_GetUserInfo:
			if (am3sControl != null) {
				am3sControl.getUserInfo();
			}else
				Toast.makeText(this, "am3sControl == null", Toast.LENGTH_LONG).show();
			break;
		case R.id.btn_GetAlarmInfo:
			if (am3sControl != null) {
				am3sControl.checkAlarmClock(1);
			}else
				Toast.makeText(this, "am3sControl == null", Toast.LENGTH_LONG).show();
			break;
		case R.id.btn_SetUserId:
			if (am3sControl != null) {
				am3sControl.setUserId(1);
			}else
				Toast.makeText(this, "am3sControl == null", Toast.LENGTH_LONG).show();
			break;
		case R.id.btn_SendRandom:
			if (am3sControl != null) {
				am3sControl.sendRandom();
			}else
				Toast.makeText(this, "am3sControl == null", Toast.LENGTH_LONG).show();
			break;
		case R.id.btn_Disconnect:
			if (am3sControl != null) {
				am3sControl.disconnect();
			}else
				Toast.makeText(this, "am3sControl == null", Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
	}

	private static final int HANDLER_MESSAGE = 101;
	Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
             switch (msg.what) {
                  case HANDLER_MESSAGE:
                       tv_return.setText((String)msg.obj);
                       break;
             }
             super.handleMessage(msg);
        }
   };
}
