package com.femto.ugershop.activity;

import com.femto.ugershop.R;
import com.femto.ugershop.application.MyApplication;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Activity_Message extends BaseActivity {
	private RelativeLayout rl_back_message;
	private LinearLayout ll_cllect, ll_atme, ll_praise, ll_comment;
	private int allCount, aMeCount, discussCount, topCount1;
	private TextView tv_newat, tv_newcommends, tv_newprise;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_message:
			finish();
			break;
		case R.id.ll_cllect:
			Intent intent = new Intent(Activity_Message.this, Activity_Mycollect.class);
			startActivity(intent);
			break;
		case R.id.ll_comment:
			Intent intent_comment = new Intent(Activity_Message.this, Activity_Comments.class);
			startActivity(intent_comment);
			tv_newcommends.setVisibility(View.GONE);
			break;
		case R.id.ll_praise:
			Intent intent_praise = new Intent(Activity_Message.this, Activity_Prise.class);
			startActivity(intent_praise);
			tv_newprise.setVisibility(View.GONE);
			break;
		case R.id.ll_atme:
			Intent intent_atme = new Intent(Activity_Message.this, Activity_AtMe.class);
			startActivity(intent_atme);
			tv_newat.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}

	@Override
	public void initView() {
		initParams();
		MyApplication.addActivity(this);
		rl_back_message = (RelativeLayout) findViewById(R.id.rl_back_message);
		ll_cllect = (LinearLayout) findViewById(R.id.ll_cllect);
		ll_atme = (LinearLayout) findViewById(R.id.ll_atme);
		ll_praise = (LinearLayout) findViewById(R.id.ll_praise);
		ll_comment = (LinearLayout) findViewById(R.id.ll_comment);
		// tv_newat, tv_newcommends, tv_newprise
		tv_newat = (TextView) findViewById(R.id.tv_newat);
		tv_newcommends = (TextView) findViewById(R.id.tv_newcommends);
		tv_newprise = (TextView) findViewById(R.id.tv_newprise);
		if (aMeCount != 0) {
			tv_newat.setVisibility(View.VISIBLE);
			if (aMeCount > 99) {
				tv_newat.setText("99+");
			} else {
				tv_newat.setText("" + aMeCount);
			}

		} else {
			tv_newat.setVisibility(View.GONE);
		}
		if (topCount1 != 0) {
			tv_newprise.setVisibility(View.VISIBLE);
			if (topCount1 > 99) {
				tv_newprise.setText("99+");
			} else {
				tv_newprise.setText("" + topCount1);
			}

		} else {
			tv_newprise.setVisibility(View.GONE);
		}
		if (discussCount != 0) {
			tv_newcommends.setVisibility(View.VISIBLE);
			if (discussCount > 99) {
				tv_newcommends.setText("99+");
			} else {
				tv_newcommends.setText("" + discussCount);
			}

		} else {
			tv_newcommends.setVisibility(View.GONE);
		}

	}

	private void initParams() {
		// // allCount, aMeCount, discussCount, topCount1
		allCount = getIntent().getIntExtra("allCount", 0);
		aMeCount = getIntent().getIntExtra("aMeCount", 0);
		discussCount = getIntent().getIntExtra("discussCount", 0);
		topCount1 = getIntent().getIntExtra("topCount", 0);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_message.setOnClickListener(this);
		ll_cllect.setOnClickListener(this);
		ll_atme.setOnClickListener(this);
		ll_praise.setOnClickListener(this);
		ll_comment.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_message);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("消息");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("消息");
	}
}
