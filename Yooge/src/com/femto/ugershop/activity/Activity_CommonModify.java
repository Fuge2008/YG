package com.femto.ugershop.activity;

import com.femto.ugershop.R;
import com.femto.ugershop.application.MyApplication;

import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Activity_CommonModify extends SwipeBackActivity {
	private RelativeLayout rl_back_common;
	private TextView tv_commontitle, tv_sure_common;
	private EditText ed_modify;
	private int flag;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_common:
			finish();
			break;
		case R.id.tv_sure_common:
			if (ed_modify.getText().toString().length() == 0) {
				showToast("请输入修改内容", 0);
				return;
			}
			Intent intent = new Intent();
			intent.putExtra("str", ed_modify.getText().toString());
			setResult(106, intent);
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back_common = (RelativeLayout) findViewById(R.id.rl_back_common);
		tv_commontitle = (TextView) findViewById(R.id.tv_commontitle);
		tv_sure_common = (TextView) findViewById(R.id.tv_sure_common);
		ed_modify = (EditText) findViewById(R.id.ed_modify);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		tv_sure_common.setOnClickListener(this);
		rl_back_common.setOnClickListener(this);
		switch (flag) {
		case 201:
			ed_modify.setEms(8);
			tv_commontitle.setText("修改用户名");
			break;
		case 202:
			ed_modify.setMaxEms(140);
			tv_commontitle.setText("修改个性签名");
			break;
		case 203:
			ed_modify.setEms(11);
			ed_modify.setInputType(InputType.TYPE_CLASS_PHONE);
			tv_commontitle.setText("修改手机号码");
			break;
		case 204:
			tv_commontitle.setText("修改邮箱");
			break;
		case 205:
			tv_commontitle.setText("修改收货地址");
			break;

		default:
			break;
		}
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_commonmodify);
		initParams();
		MyApplication.addActivity(this);
	}

	private void initParams() {
		// TODO Auto-generated method stub
		flag = getIntent().getIntExtra("flag", 0);
	}

}
