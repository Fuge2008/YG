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

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author 作者Deep:
 * @version 创建时间：2015年10月9日 下午5:26:01 类说明
 */
public class Activity_Rule extends BaseActivity {
	private RelativeLayout rl_back_rule;
	private TextView tv_conrule, tv_ruletitle;
	private String title;
	private String info;
	private int flag;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_rule:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("规则详情");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("规则详情");
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back_rule = (RelativeLayout) findViewById(R.id.rl_back_rule);
		tv_conrule = (TextView) findViewById(R.id.tv_conrule);
		tv_ruletitle = (TextView) findViewById(R.id.tv_ruletitle);
		if (flag == 1) {
			tv_ruletitle.setText("" + title);
			tv_conrule.setText("" + info);
		} else {

		}
		getData();

	}

	private void getData() {
		RequestParams params = new RequestParams();
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetAppXieYi, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String message = response.getString("message");
					tv_conrule.setText("" + message);
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
		rl_back_rule.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_rule);
		initParams();
		MyApplication.addActivity(this);
	}

	private void initParams() {
		// TODO Auto-generated method stub
		title = getIntent().getStringExtra("title");
		info = getIntent().getStringExtra("info");
		flag = getIntent().getIntExtra("flag", 0);
	}
}
