package cx.mobilechecksh.mvideo.camera;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;



import java.net.MalformedURLException;
import java.net.URL;

import cx.mobilechecksh.R;
import cx.mobilechecksh.mvideo.camera.config.SettingHelper;
import cx.mobilechecksh.mvideo.picc.net.HttpParams;
import cx.mobilechecksh.mvideo.picc.ui.Dialog;
import cx.mobilechecksh.mvideo.picc.ui.Titlebar;
import cx.mobilechecksh.mvideo.picc.updateapp.UpdateApp;
import cx.mobilechecksh.mvideo.picc.utils.G;
import cx.mobilechecksh.theme.MBaseActivity;

public class CameraSetting extends MBaseActivity {

	private EditText mIP_EditText;
	private EditText sign_edit;
	private EditText mFPS_EditText;
	private EditText mBIT_EditText;
	private Spinner mPwPh_Spinner,mCon_spinner;
	private String mPwPh_Str = "640x480";
	private int mPwPhSelect_int = 0;
	private int mConnectSelect_int = 0;
	private ArrayAdapter<?> spinnerAdapter,conspinnerAdapter;
	private final String TAG = this.getClass().getName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_setting);
		initTitle();
		initView();
	}
	
	
	public void initTitle(){
		
		Titlebar titlebar = (Titlebar)findViewById(R.id.titlebar);
		titlebar.showLeft();
		titlebar.setLeftBackImagesRes(R.drawable.ui_titile_return);
		titlebar.showRight(onClickSave);
		titlebar.setRightText(R.string.setting_save);
	}
	
	
	private void initView() {
		// TODO Auto-generated method stub
		String BSASURL = getString(R.string.base_url);
		String preferencesIP = null ;
		try {
			URL url = new URL(BSASURL);
			 preferencesIP = SettingHelper.getInstance().getIP(url.getHost());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
		
		 mIP_EditText = (EditText)this.findViewById(R.id.ip);
		 if(preferencesIP.equals("")){
			 preferencesIP = HttpParams.BSASURL;
		 }
		 mIP_EditText.setText(preferencesIP);
		 
		 String preferencesSIGN = SettingHelper.getInstance().getSIGN("");
		 
		 sign_edit= (EditText)this.findViewById(R.id.sign);
		 if(preferencesSIGN!=null&&preferencesSIGN.trim().equals("")){
			 preferencesSIGN = new String("");
			 sign_edit.setEnabled(true);
		 }else if(preferencesSIGN!=null&&!preferencesSIGN.trim().equals("")){
			 sign_edit.setEnabled(false);
		}
		 sign_edit.setText(preferencesSIGN);
		 
		 
		 String FPS = SettingHelper.getInstance().getFPS(getString(R.string.setting_defaultFPS));
		 if(FPS.equals("")){
			 FPS = getString(R.string.setting_defaultFPS);
		 }
		 mFPS_EditText = (EditText)this.findViewById(R.id.FPS);
		 mFPS_EditText.setText(FPS);
		 mFPS_EditText.setEnabled(false);
		 
		 String BIT = SettingHelper.getInstance().getBIT(getString(R.string.setting_defaultBIT));
		 if(BIT.equals("")){
			 BIT = getString(R.string.setting_defaultBIT);
		 }
		 mBIT_EditText = (EditText)this.findViewById(R.id.BIT);
		 mBIT_EditText.setText(BIT);
		 
//		 String pw_ph = UserManager.getInstance().getPw_ph();
//		 if(pw_ph.equals("")){
//			 pw_ph = UserManager.getInstance().getPw_ph();
//		 }
		 
		 int pwphSelect= SettingHelper.getInstance().getPwPhSelect(0);
		 
		 mPwPh_Spinner = (Spinner)this.findViewById(R.id.pw_ph);
		 
		//将可选内容与ArrayAdapter连接起来  
		 spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.pref_camera_previewsize_entryvalues , android.R.layout.simple_spinner_item);
		 //设置下拉列表的风格    
		 spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 mPwPh_Spinner.setAdapter(spinnerAdapter);
		 mPwPh_Spinner.setSelection(pwphSelect, true);
		 mPwPh_Spinner.setOnItemSelectedListener(mPwPh_SpinnerOnItemSelectedListener);
		 
		 
		  mConnectSelect_int= SettingHelper.getInstance().getConnecSelect(0);
		 
		 mCon_spinner = (Spinner)this.findViewById(R.id.connect_type);
		 
		 //将可选内容与ArrayAdapter连接起来  
		 conspinnerAdapter = ArrayAdapter.createFromResource(this,R.array.pref_camera_connect_entryvalues , android.R.layout.simple_spinner_item);
		 //设置下拉列表的风格    
		 conspinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 mCon_spinner.setAdapter(conspinnerAdapter);
		 mCon_spinner.setSelection(mConnectSelect_int, true);
		 mCon_spinner.setOnItemSelectedListener(mCon_SpinnerOnItemSelectedListener);
		 
		 
		 Button aboutBtn = (Button) findViewById(R.id.setting_about);
		 aboutBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CameraSetting.this, APPabout.class);
				startActivity(intent);
			}
		});
		 
		 Button fixBtn = (Button) findViewById(R.id.fixnet);
		 fixBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CameraSetting.this, Fixnet.class);
				startActivity(intent);
			}
		});
	}
	
	OnItemSelectedListener mPwPh_SpinnerOnItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			mPwPh_Str = spinnerAdapter.getItem(arg2).toString();
			mPwPhSelect_int = arg2;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	OnItemSelectedListener mCon_SpinnerOnItemSelectedListener = new OnItemSelectedListener() {
		
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			mConnectSelect_int = arg2;
		}
		
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private OnClickListener onClickSave = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String ip_Str =mIP_EditText.getText().toString();
			String ip_sign =sign_edit.getText().toString();
			String FPS_Str = mFPS_EditText.getText().toString();
			String BIT_Str = mBIT_EditText.getText().toString();
			String PwPh_Str = mPwPh_Spinner.getSelectedItem().toString();
			String connectTyep_Str = mCon_spinner.getSelectedItem().toString();
			
			if(ip_Str.trim().equals("") || FPS_Str.trim().equals("") || BIT_Str.trim().equals("")||ip_sign.trim().equals("")){
				Dialog.negativeDialog(CameraSetting.this, CameraSetting.this.getResources().getString(R.string.settingip_nomes));
				return;
			}
			if(!G.isIp(ip_Str)){
				Dialog.negativeDialog(CameraSetting.this, CameraSetting.this.getResources().getString(R.string.settingip_error));
				return;
			}
			boolean saveIP_boolean = HttpParams.saveIP("http://"+ip_Str+":"+getString(R.string.picc_url_port)+"/", CameraSetting.this);
			if(saveIP_boolean){
				HttpParams.init(CameraSetting.this);
				boolean b = UpdateApp.clearPreferences(CameraSetting.this);
				Log.d(TAG, " clearPreferences is success:"+b);
			}
			
			boolean b =  SettingHelper.getInstance().saveSettingSystem(CameraSetting.this,ip_Str, FPS_Str, BIT_Str, PwPh_Str,connectTyep_Str,mPwPhSelect_int,mConnectSelect_int,ip_sign);
			if(b){
				Log.d(TAG, "saveSettingSystem success");
			}
			finish();
		}
	};
}
