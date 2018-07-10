package com.femto.ugershop.activity;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_ReturnOrBack extends BaseActivity {
	private RelativeLayout rl_back_return, rl_newgoods1_c_r, rl_designer1_c_r;
	private LinearLayout ll_back, ll_return;
	private int flag = 1;
	private TextView tv_sure_up, tv_returng, tv_backg;
	private int orderId;
	private EditText ed_return, ed_back, ed_postreturn, ed_postback;
	private int myId;
	private DisplayImageOptions options;
	private int type;
	private int newtype = 0;

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("退换货");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("退换货");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_return:
			finish();
			break;
		case R.id.tv_sure_up:
			if (newtype == 1) {
				up(AppFinalUrl.userchangeUserMakeProduct);
			} else {
				if (type == 1) {
					up(AppFinalUrl.userchangeMakeProduct);
				} else {
					up(AppFinalUrl.userchangeProduct);
				}
			}

			break;
		case R.id.rl_designer1_c_r:
			ShowTV(tv_backg, tv_returng);
			ll_back.setVisibility(View.GONE);
			ll_return.setVisibility(View.VISIBLE);
			flag = 2;
			break;
		case R.id.rl_newgoods1_c_r:

			ShowTV(tv_returng, tv_backg);
			ll_back.setVisibility(View.VISIBLE);
			ll_return.setVisibility(View.GONE);
			flag = 1;
			break;

		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back_return = (RelativeLayout) findViewById(R.id.rl_back_return);
		MyApplication.addActivity(this);
		rl_designer1_c_r = (RelativeLayout) findViewById(R.id.rl_designer1_c_r);
		rl_newgoods1_c_r = (RelativeLayout) findViewById(R.id.rl_newgoods1_c_r);

		ll_return = (LinearLayout) findViewById(R.id.ll_return);
		ll_back = (LinearLayout) findViewById(R.id.ll_back);
		tv_sure_up = (TextView) findViewById(R.id.tv_sure_up);
		ed_back = (EditText) findViewById(R.id.ed_back);
		ed_return = (EditText) findViewById(R.id.ed_return);
		ed_postback = (EditText) findViewById(R.id.ed_postback);
		ed_postreturn = (EditText) findViewById(R.id.ed_postreturn);

		tv_returng = (TextView) findViewById(R.id.tv_returng);
		tv_backg = (TextView) findViewById(R.id.tv_backg);
	}

	private void ShowTV(TextView tshow, TextView tnoshow) {
		tshow.setVisibility(View.VISIBLE);
		tnoshow.setVisibility(View.INVISIBLE);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_return.setOnClickListener(this);
		rl_newgoods1_c_r.setOnClickListener(this);
		tv_sure_up.setOnClickListener(this);
		rl_designer1_c_r.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_returnorback);
		initParams();
	}

	private void up(String url) {
		RequestParams params = new RequestParams();
		params.put("orderId", orderId);
		params.put("type", flag);
		if (flag == 1) {
			if (ed_back.getText().toString().length() == 0) {
				Toast.makeText(Activity_ReturnOrBack.this, "请输入申请内容", Toast.LENGTH_SHORT).show();
				return;
			} else {
				params.put("content", ed_back.getText().toString());
			}

		} else {
			if (ed_return.getText().toString().length() == 0) {
				Toast.makeText(Activity_ReturnOrBack.this, "请输入申请内容", Toast.LENGTH_SHORT).show();
				return;
			} else {
				params.put("content", ed_return.getText().toString());
			}

		}
		if (flag == 1) {
			if (ed_postback.getText().toString().length() == 0) {
				Toast.makeText(Activity_ReturnOrBack.this, "请输入单号", Toast.LENGTH_SHORT).show();
				return;
			} else {
				params.put("backCode", ed_postback.getText().toString().trim());
			}

		} else {
			if (ed_postreturn.getText().toString().length() == 0) {
				Toast.makeText(Activity_ReturnOrBack.this, "请输入单号", Toast.LENGTH_SHORT).show();
				return;
			} else {
				params.put("backCode", ed_postreturn.getText().toString().trim());
			}

		}
		showProgressDialog("正在提交...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(url, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_ReturnOrBack.this, "提交成功", Toast.LENGTH_SHORT).show();

						finish();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		myId = sp.getInt("userId", 0);
		orderId = getIntent().getIntExtra("orderId", 0);
		newtype = getIntent().getIntExtra("newtype", 0);
		type = getIntent().getIntExtra("type", 0);
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
	}
}
