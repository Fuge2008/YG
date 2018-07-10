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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Activity_Account extends SwipeBackActivity {
	private RelativeLayout rl_back_account, rl_earnings, rl_mycosttt;
	private LinearLayout rl_mycost;
	private TextView tv_integraldetail, tv_allscore, tv_accountbalance;
	private int userId;
	private int score, money, makeMoney;
	private int type;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_account:
			finish();
			break;
		case R.id.rl_earnings:
			Intent intent_earnings = new Intent(Activity_Account.this, Activity_Earnings.class);
			startActivity(intent_earnings);
			break;
		case R.id.rl_mycost:
			Intent intent_mycost = new Intent(Activity_Account.this, Activity_MyCost.class);
			intent_mycost.putExtra("makeMoney", makeMoney);
			startActivity(intent_mycost);
			break;
		case R.id.tv_integraldetail:
			Intent intent = new Intent(Activity_Account.this, Activity_Integraldetail.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		MyApplication.addActivity(this);
		rl_back_account = (RelativeLayout) findViewById(R.id.rl_back_account);
		rl_earnings = (RelativeLayout) findViewById(R.id.rl_earnings);
		rl_mycosttt = (RelativeLayout) findViewById(R.id.rl_mycosttt);
		tv_integraldetail = (TextView) findViewById(R.id.tv_integraldetail);
		rl_mycost = (LinearLayout) findViewById(R.id.rl_mycost);
		tv_accountbalance = (TextView) findViewById(R.id.tv_accountbalance);
		tv_allscore = (TextView) findViewById(R.id.tv_allscore);
		if (type == 2) {
			rl_earnings.setVisibility(View.GONE);
			rl_mycosttt.setVisibility(View.GONE);
		}
		getData();
	}

	private void getData() {
		RequestParams params = new RequestParams();
		params.put("userId", userId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userdesinMoneyAndScore, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);

				try {
					score = response.getInt("score");
					money = response.getInt("money");
					makeMoney = response.getInt("makeMoney");
					tv_allscore.setText("" + score);
					tv_accountbalance.setText("" + money);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_account.setOnClickListener(this);
		tv_integraldetail.setOnClickListener(this);
		rl_mycost.setOnClickListener(this);
		rl_earnings.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_account);
		initParams();
	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		userId = sp.getInt("userId", -1);
		type = sp.getInt("type", -1);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("账户管理");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("账户管理");
	}
}
