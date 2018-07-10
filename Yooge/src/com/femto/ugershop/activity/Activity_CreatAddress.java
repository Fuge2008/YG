package com.femto.ugershop.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

/**
 * @author 作者Deep:
 * @version 创建时间：2015年10月19日 下午4:24:27 类说明
 */
public class Activity_CreatAddress extends SwipeBackActivity {
	private RelativeLayout rl_back_creataddress;
	private EditText ed_newaddress;
	private TextView tv_sure_creataddress;
	private int myId;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_creataddress:
			finish();
			break;
		case R.id.tv_sure_creataddress:
			if (ed_newaddress.getText().toString().length() == 0) {
				Toast.makeText(this, "请填写地址", Toast.LENGTH_SHORT).show();
				return;
			}
			saveAddress();
			break;
		default:
			break;
		}
	}

	private void saveAddress() {
		RequestParams params = new RequestParams();
		params.put("userAddress.name", ed_newaddress.getText().toString());
		params.put("userAddress.user.id", myId);
		showProgressDialog("正在提交...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.useraddAddressByUser, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_CreatAddress.this, "添加成功", Toast.LENGTH_SHORT).show();
						finish();
					} else {
						Toast.makeText(Activity_CreatAddress.this, "添加失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back_creataddress = (RelativeLayout) findViewById(R.id.rl_back_creataddress);
		tv_sure_creataddress = (TextView) findViewById(R.id.tv_sure_creataddress);
		ed_newaddress = (EditText) findViewById(R.id.ed_newaddress);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_creataddress.setOnClickListener(this);
		tv_sure_creataddress.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_creataddress);
		initParams();
		MyApplication.addActivity(this);
	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
		myId = sp.getInt("userId", 0);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("创建地址");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("创建地址");
	}
}
