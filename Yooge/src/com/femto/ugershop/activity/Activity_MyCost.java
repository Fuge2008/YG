package com.femto.ugershop.activity;

import com.femto.ugershop.R;
import com.femto.ugershop.application.MyApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class Activity_MyCost extends BaseActivity {
	private RelativeLayout rl_back_mycost, rl_custdetaile, rl_otherfee;
	private int makeMoney;
	private TextView tv_customfee, tv_integraldetail;
	private DisplayImageOptions options;
	private int myId;
	private View customView;
	private PopupWindow ppwRule;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_mycost:
			finish();
			break;
		case R.id.rl_otherfee:
			Intent intent_otherfee = new Intent(Activity_MyCost.this, Activity_OtherFee.class);
			startActivity(intent_otherfee);
			break;
		case R.id.rl_custdetaile:
			Intent intent = new Intent(Activity_MyCost.this, Activity_CustProDetai.class);
			startActivity(intent);
			break;
		case R.id.tv_integraldetail:
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
		rl_back_mycost = (RelativeLayout) findViewById(R.id.rl_back_mycost);
		rl_custdetaile = (RelativeLayout) findViewById(R.id.rl_custdetaile);
		rl_otherfee = (RelativeLayout) findViewById(R.id.rl_otherfee);
		tv_customfee = (TextView) findViewById(R.id.tv_customfee);
		tv_integraldetail = (TextView) findViewById(R.id.tv_integraldetail);

		tv_customfee.setText("" + makeMoney);
	}

	@Override
	public void initUtils() {

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_mycost.setOnClickListener(this);
		rl_custdetaile.setOnClickListener(this);
		rl_otherfee.setOnClickListener(this);
		tv_integraldetail.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_mycost);
		initParams();
		MyApplication.addActivity(this);
	}

	private void initParams() {
		makeMoney = getIntent().getIntExtra("makeMoney", 0);

	}

	public void initPpwPrice() {
		customView = View.inflate(this, R.layout.popurule, null);
		ppwRule = new PopupWindow(customView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, false);
		TextView tv_iknow = (TextView) customView.findViewById(R.id.tv_iknow);
		TextView tv_rule_con = (TextView) customView.findViewById(R.id.tv_rule_con);
		tv_rule_con
				.setText("定制保证金\n优格将在每笔定制订单中冻结交易金额的10%做为定制保证金，定制保证金将用于定制订单的纠纷处理，当定制保证金总额达到5000元时，停止收取定制保证金，当设计师不再进行定制交易时，定制保证金将全额退回设计师账户。");
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

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("我的费用");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("我的费用");
	}
}
