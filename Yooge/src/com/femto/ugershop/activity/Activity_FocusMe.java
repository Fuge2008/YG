package com.femto.ugershop.activity;

import com.femto.ugershop.R;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.fragment.Fragment_MyTongxunlu;
import com.umeng.analytics.MobclickAgent;

import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;

public class Activity_FocusMe extends SwipeBackActivity {
	private RelativeLayout rl_back_focus;
	private int userId;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_focus:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back_focus = (RelativeLayout) findViewById(R.id.rl_back_focus);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_focus.setOnClickListener(this);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.fl_fm, new Fragment_MyTongxunlu(userId, 2));
		transaction.commit();
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_focusme);
		initParams();
		MyApplication.addActivity(this);
	}

	private void initParams() {
		// TODO Auto-generated method stub
		userId = getIntent().getIntExtra("userId", 0);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("关注我的");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("关注我的");
	}
}
