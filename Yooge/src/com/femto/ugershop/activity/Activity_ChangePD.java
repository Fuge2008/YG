package com.femto.ugershop.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author 作者Deep:
 * @version 创建时间：2015年10月19日 下午6:08:07 类说明
 */
public class Activity_ChangePD extends SwipeBackActivity {
	private EditText ed_password_once, ed_pd_new, ed_morepd_new;
	private RelativeLayout rl_back_changepd;
	private TextView tv_sure_changepd;
	private int myId;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_changepd:
			finish();
			break;
		case R.id.tv_sure_changepd:
			if (ed_password_once.getText().toString().length() == 0) {
				Toast.makeText(this, "请输入旧密码", Toast.LENGTH_SHORT).show();
				return;
			}
			if (ed_pd_new.getText().toString().length() == 0) {
				Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!ed_pd_new.getText().toString().equals(ed_morepd_new.getText().toString())) {
				Toast.makeText(this, "两次新密码不一致", Toast.LENGTH_SHORT).show();
				return;
			}
			changePd();
			break;
		default:
			break;
		}
	}

	private void changePd() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("userId", myId);
		params.put("oldPassWord", ed_password_once.getText().toString());
		params.put("newPassWord", ed_morepd_new.getText().toString());
		showProgressDialog("提交中...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userupdatePassWordByUserId, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo+++" + response.toString());
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_ChangePD.this, "修改成功", Toast.LENGTH_SHORT).show();
						finish();
					} else if (result.equals("3")) {
						Toast.makeText(Activity_ChangePD.this, "旧密码不正确", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(Activity_ChangePD.this, "修改失败", Toast.LENGTH_SHORT).show();
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
		ed_password_once = (EditText) findViewById(R.id.ed_password_once);
		ed_pd_new = (EditText) findViewById(R.id.ed_pd_new);
		ed_morepd_new = (EditText) findViewById(R.id.ed_morepd_new);
		rl_back_changepd = (RelativeLayout) findViewById(R.id.rl_back_changepd);
		tv_sure_changepd = (TextView) findViewById(R.id.tv_sure_changepd);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_changepd.setOnClickListener(this);
		tv_sure_changepd.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_changepd);
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
		setPageEnd("修改密码");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("修改密码");
	}
}
