package com.femto.ugershop.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author 作者Deep:
 * @version 创建时间：2015年10月19日 上午10:11:05 类说明
 */
public class Activity_FindPassWord extends SwipeBackActivity {
	private RelativeLayout rl_back_findpassword;
	private EditText ed_phone_find, ed_code, ed_password_find, ed_morepd_find;
	private TextView tv_getcode, tv_sure_find;
	private int i = 60;
	private boolean isSend = false;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (i == 0) {
					tv_getcode.setText("重新发送");
					isSend = false;
				} else {
					tv_getcode.setText("(" + (i--) + ")已发送");
					sendCond();
				}

				break;

			default:
				break;
			}
		};
	};

	private void sendCond() {
		handler.sendEmptyMessageDelayed(1, 1000);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_findpassword:
			finish();
			break;
		case R.id.tv_getcode:
			if (isSend) {

			} else {
				if (ed_phone_find.getText().toString().length() != 11 && !isEmail(ed_phone_find.getText().toString())) {
					Toast.makeText(this, "请输入正确的手机号码或邮箱", Toast.LENGTH_SHORT).show();
					return;
				}

				getCode();
			}

			break;
		case R.id.tv_sure_find:
			findPassWord();
			break;
		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back_findpassword = (RelativeLayout) findViewById(R.id.rl_back_findpassword);
		ed_phone_find = (EditText) findViewById(R.id.ed_phone_find);
		ed_code = (EditText) findViewById(R.id.ed_code);
		ed_password_find = (EditText) findViewById(R.id.ed_password_find);
		ed_morepd_find = (EditText) findViewById(R.id.ed_morepd_find);
		tv_getcode = (TextView) findViewById(R.id.tv_getcode);
		tv_sure_find = (TextView) findViewById(R.id.tv_sure_find);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		tv_getcode.setOnClickListener(this);
		tv_sure_find.setOnClickListener(this);
		rl_back_findpassword.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_findpassword);
		MyApplication.addActivity(this);
	}

	private void getCode() {
		handler.sendEmptyMessage(1);
		isSend = true;
		i = 60;
		RequestParams params = new RequestParams();
		params.put("user.name", ed_phone_find.getText().toString().trim());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userpassWordSendCode, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String result = response.getString("result");
					if (result.equals("0")) {

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void findPassWord() {
		RequestParams params = new RequestParams();
		if (ed_phone_find.getText().toString().length() == 0) {
			Toast.makeText(Activity_FindPassWord.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (ed_password_find.getText().toString().length() == 0) {
			Toast.makeText(Activity_FindPassWord.this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (ed_code.getText().toString().length() == 0) {
			Toast.makeText(Activity_FindPassWord.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!ed_morepd_find.getText().toString().equals(ed_password_find.getText().toString())) {
			Toast.makeText(Activity_FindPassWord.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
			return;
		}

		params.put("name", ed_phone_find.getText().toString().trim());
		params.put("code", ed_code.getText().toString().trim());
		params.put("password", ed_morepd_find.getText().toString().trim());
		showProgressDialog("提交中...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userfindPassByCode, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_FindPassWord.this, "修改成功,重新登录", Toast.LENGTH_SHORT).show();
						finish();
					} else {
						Toast.makeText(Activity_FindPassWord.this, "该用户不存在", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public static boolean isEmail(String email) {
		if (null == email || "".equals(email))
			return false;
		// Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
		Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
		Matcher m = p.matcher(email);
		return m.matches();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("找回密码");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("找回密码");
	}
}
