package com.femto.ugershop.activity;

import com.femto.ugershop.R;
import com.femto.ugershop.application.MyApplication;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class Activity_StorePutaway extends BaseActivity {
	private RelativeLayout rl_back_sput, rl_allstore, rl_applyup;
	private TextView tv_applyrule;
	private View customView;
	private PopupWindow ppwRule;

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("商城上架");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("商城上架");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_sput:
			finish();
			break;
		case R.id.rl_applyup:
			Intent intent_up = new Intent(Activity_StorePutaway.this, Activity_GoodUp.class);
			startActivity(intent_up);
			break;
		case R.id.rl_allstore:
			Intent intent = new Intent(Activity_StorePutaway.this, Activity_AllStrorePut.class);
			startActivity(intent);
			break;
		case R.id.tv_applyrule:
			if (ppwRule != null && ppwRule.isShowing()) {
				// ppw_price.setFocusable(false);
				ppwRule.dismiss();
			} else {
				initPpwPrice();
				ppwRule.showAtLocation(v, Gravity.CENTER_HORIZONTAL, 1, 1);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back_sput = (RelativeLayout) findViewById(R.id.rl_back_sput);
		rl_allstore = (RelativeLayout) findViewById(R.id.rl_allstore);
		rl_applyup = (RelativeLayout) findViewById(R.id.rl_applyup);
		tv_applyrule = (TextView) findViewById(R.id.tv_applyrule);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_sput.setOnClickListener(this);
		rl_allstore.setOnClickListener(this);
		rl_applyup.setOnClickListener(this);
		tv_applyrule.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_storeputaway);
		MyApplication.addActivity(this);
	}

	public void initPpwPrice() {
		customView = View.inflate(this, R.layout.popurule, null);
		ppwRule = new PopupWindow(customView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, false);
		TextView tv_iknow = (TextView) customView.findViewById(R.id.tv_iknow);
		TextView tv_rule_con = (TextView) customView.findViewById(R.id.tv_rule_con);
		tv_rule_con.setText("上架\n按要求上传商品信息，优格会组织消费者团体（在优格注册用户中随机抽取）对商品进行评估，评估通过后，将安排进入拍照及生产等流程。");
		tv_iknow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ppwRule != null && ppwRule.isShowing()) {
					ppwRule.dismiss();
					ppwRule = null;

				}

			}
		});
	}
}
