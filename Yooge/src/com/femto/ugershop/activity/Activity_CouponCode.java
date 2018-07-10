package com.femto.ugershop.activity;

import org.apache.http.Header;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Activity_CouponCode extends BaseActivity {
	private RelativeLayout rl_back_couponcode;
	private TextView tv_exchange;
	private EditText ed_couponcode;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_couponcode:
			finish();
			break;
		case R.id.tv_exchange:
			if (ed_couponcode.getText().toString().trim().length() == 0) {
				showToast("请输入优惠码!", 0);
				return;
			}
			checkCode();
			break;
		default:
			break;
		}
	}

	private void checkCode() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("userId", MyApplication.userId);
		params.put("token", MyApplication.token);
		params.put("code", ed_couponcode.getText().toString().trim());
		showProgressDialog("兑换中...");
		MyApplication.ahc.post(AppFinalUrl.userexchangeCoupon, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				System.out.println("zuo===" + response.toString());
				String result = response.optString("result");
				String message = response.optString("message");
				if (result != null) {
					if (result.equals("0")) {
						Intent intent = new Intent();
						intent.putExtra("pushCode", "0");
						setResult(309, intent);
						showToast("兑换成功", 0);
						finish();
					} else if (result.equals("-1")) {
						Intent intent = new Intent();
						intent.putExtra("pushCode", ed_couponcode.getText().toString());
						setResult(309, intent);
						showToast("兑换成功", 0);
						finish();
					} else {
						showToast("兑换失败", 0);
					}

				} else {

				}

			}
		});

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back_couponcode = (RelativeLayout) findViewById(R.id.rl_back_couponcode);
		tv_exchange = (TextView) findViewById(R.id.tv_exchange);
		ed_couponcode = (EditText) findViewById(R.id.ed_couponcode);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_couponcode.setOnClickListener(this);
		tv_exchange.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_couponcode);
		MyApplication.addActivity(this);
	}

}
