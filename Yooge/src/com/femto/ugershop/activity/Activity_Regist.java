package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.activity.RegisterActivity;
import com.easemob.exceptions.EaseMobException;
import com.femto.hx.utils.CommonUtils;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.easemob.applib.db.UserDao;
import com.femto.ugershop.easemob.applib.domain.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Regist extends BaseActivity {
	private RelativeLayout rl_back_regist;
	private TextView tv_sure_regist, tv_rule, tv_getcode_r;
	private EditText ed_pdmoreregist, ed_pdregist, ed_phone_regist, ed_code_r;
	private CheckBox cb_isdesiger, cb_iscustom;
	private String username;
	private String pwd;
	private String confirm_pwd;
	private String currentUsername;
	private String currentPassword;
	private boolean progressShow;
	private CheckBox cb_agree;
	private int i = 60;
	private boolean isSend = false;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (i == 0) {
					tv_getcode_r.setText("重新发送");
					isSend = false;

				} else {
					tv_getcode_r.setText("(" + (i--) + ")已发送");
					sendCond();
				}

				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("注册");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("注册");
	}

	private void sendCond() {
		handler.sendEmptyMessageDelayed(1, 1000);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_regist:
			finish();
			break;
		case R.id.cb_isdesiger:
			cb_iscustom.setChecked(false);
			break;
		case R.id.cb_iscustom:
			cb_isdesiger.setChecked(false);
			break;
		case R.id.tv_sure_regist:

			regist();

			// registerEM();
			break;
		case R.id.tv_rule:
			Intent intent = new Intent(this, Activity_Rule.class);
			intent.putExtra("title", "优格用户服务条款");
			startActivity(intent);
			break;
		case R.id.tv_getcode_r:
			if (isSend) {

			} else {
				if (ed_phone_regist.getText().toString().length() != 11 && !isEmail(ed_phone_regist.getText().toString())) {
					Toast.makeText(this, "请输入正确的手机号码或邮箱", Toast.LENGTH_SHORT).show();
					return;
				}
				getCode();
			}

			break;
		default:
			break;
		}
	}

	private void getCode() {
		handler.sendEmptyMessage(1);
		isSend = true;
		i = 60;
		RequestParams params = new RequestParams();
		params.put("user.name", ed_phone_regist.getText().toString().trim());
		MyApplication.ahc.post(AppFinalUrl.usersendCode, params, new JsonHttpResponseHandler() {
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

	@Override
	public void initView() {
		MyApplication.addActivity(this);
		// TODO Auto-generated method stub
		rl_back_regist = (RelativeLayout) findViewById(R.id.rl_back_regist);
		tv_sure_regist = (TextView) findViewById(R.id.tv_sure_regist);
		ed_pdmoreregist = (EditText) findViewById(R.id.ed_pdmoreregist);
		ed_pdregist = (EditText) findViewById(R.id.ed_pdregist);
		ed_code_r = (EditText) findViewById(R.id.ed_code_r);
		ed_phone_regist = (EditText) findViewById(R.id.ed_phone_regist);
		cb_isdesiger = (CheckBox) findViewById(R.id.cb_isdesiger);
		cb_iscustom = (CheckBox) findViewById(R.id.cb_iscustom);
		cb_agree = (CheckBox) findViewById(R.id.cb_agree);
		tv_rule = (TextView) findViewById(R.id.tv_rule);
		tv_getcode_r = (TextView) findViewById(R.id.tv_getcode_r);
		cb_iscustom.setOnClickListener(this);
		cb_isdesiger.setOnClickListener(this);
		tv_rule.setOnClickListener(this);

	}

	public void regist() {
		if (!ed_pdmoreregist.getText().toString().equals(ed_pdregist.getText().toString())) {
			dismissProgressDialog();
			Toast.makeText(Activity_Regist.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
			return;
		}
		if (ed_pdmoreregist.getText().toString().length() == 0 || ed_phone_regist.getText().toString().length() == 0) {
			dismissProgressDialog();
			Toast.makeText(Activity_Regist.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
			return;
		}
		if (ed_code_r.getText().toString().length() == 0) {
			dismissProgressDialog();
			Toast.makeText(Activity_Regist.this, "请输入验证码", Toast.LENGTH_SHORT).show();
			return;
		}
		showProgressDialog("注册中...");
		RequestParams params = new RequestParams();
		params.put("user.name", ed_phone_regist.getText().toString().trim());
		params.put("user.passWord", ed_pdmoreregist.getText().toString());
		params.put("code", ed_code_r.getText().toString());

		if (cb_isdesiger.isChecked()) {
			params.put("user.type", 1);
		} else if (cb_iscustom.isChecked()) {
			params.put("user.type", 2);
		} else {
			// Toast.makeText(Activity_Regist.this, "请选择身份",
			// Toast.LENGTH_SHORT).show();
			// dismissProgressDialog();
			// return;
			params.put("user.type", 2);
		}
		if (!cb_agree.isChecked()) {
			Toast.makeText(this, "请选择同意优格相关条款", Toast.LENGTH_SHORT).show();
			return;
		}
		System.out.println("zuo=" + params.toString());
		MyApplication.ahc.post(AppFinalUrl.userregist, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				System.out.println("zuo==" + response.toString());
				try {
					String result = response.optString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_Regist.this, "注册成功", Toast.LENGTH_SHORT).show();
						SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
						Editor edit = sp.edit();
						edit.putString("username", ed_phone_regist.getText().toString());
						edit.commit();
						login();
					} else if (result.equals("1")) {
						Toast.makeText(Activity_Regist.this, "用户名已经存在", Toast.LENGTH_SHORT).show();
						dismissProgressDialog();
					} else if (result.equals("2")) {
						Toast.makeText(Activity_Regist.this, "用户名已经存在", Toast.LENGTH_SHORT).show();
						dismissProgressDialog();
					} else if (result.equals("3")) {
						Toast.makeText(Activity_Regist.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
						dismissProgressDialog();
					} else {
						Toast.makeText(Activity_Regist.this, "验证码错误", Toast.LENGTH_SHORT).show();
						dismissProgressDialog();
					}

				} catch (Exception e) {
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
		rl_back_regist.setOnClickListener(this);
		tv_sure_regist.setOnClickListener(this);
		cb_iscustom.setOnClickListener(this);
		cb_isdesiger.setOnClickListener(this);
		tv_getcode_r.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_regist);

	}

	public void login() {
		showProgressDialog("登录中...");
		RequestParams params = new RequestParams();
		// private EditText usernameEditText;
		// private EditText passwordEditText;
		params.put("userName", ed_phone_regist.getText().toString().trim());
		params.put("password", ed_pdregist.getText().toString().trim());

		MyApplication.ahc.post(AppFinalUrl.useruserlogin, params, new JsonHttpResponseHandler() {
			private int userId;

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				System.out.println("zuologin" + response.toString());
				try {
					String result = response.optString("result");
					if (result.equals("0")) {
						int type = response.optInt("type");
						userId = response.optInt("userId");
						String token = response.optString("token");
						SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
						Editor edit = sp.edit();
						edit.putBoolean("islogin", true);
						edit.putInt("userId", userId);
						edit.putString("token", token);
						edit.putInt("type", type);
						edit.putString("token", "");
						edit.commit();
						if (userId != 0) {
							loginEM(userId);
						}
						MyApplication.getInfo();
					} else {
						Toast.makeText(Activity_Regist.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void initializeContacts() {
		Map<String, User> userlist = new HashMap<String, User>();

		// // 添加user"申请与通知"
		// User newFriends = new User();
		// newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
		// String strChat = getResources().getString(
		// R.string.Application_and_notify);
		// newFriends.setNick(strChat);
		//
		// userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends); // 添加"群聊"
		// User groupUser = new User();
		// String strGroup = getResources().getString(R.string.group_chat);
		// groupUser.setUsername(Constant.GROUP_USERNAME);
		// groupUser.setNick(strGroup);
		// groupUser.setHeader("");
		// userlist.put(Constant.GROUP_USERNAME, groupUser);
		//
		// // 添加"Robot"
		// User robotUser = new User();
		// String strRobot = getResources().getString(R.string.robot_chat);
		// robotUser.setUsername(Constant.CHAT_ROBOT);
		// robotUser.setNick(strRobot);
		// robotUser.setHeader("");
		// userlist.put(Constant.CHAT_ROBOT, robotUser);

		// 存入内存
		MyApplication.getInstance().setContactList(userlist);
		// 存入db
		UserDao dao = new UserDao(Activity_Regist.this);
		List<User> users = new ArrayList<User>(userlist.values());
		dao.saveContactList(users);
	}

	/**
	 * 登录
	 * 
	 * @param view
	 */
	public void loginEM(int userId) {
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			return;
		}
		currentUsername = "" + userId;
		currentPassword = ed_pdmoreregist.getText().toString().trim();

		if (TextUtils.isEmpty(currentUsername)) {
			Toast.makeText(this, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(currentPassword)) {
			Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}

		progressShow = true;
		final ProgressDialog pd = new ProgressDialog(Activity_Regist.this);
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				progressShow = false;
			}
		});
		pd.setMessage(getString(R.string.Is_landing));
		pd.show();

		final long start = System.currentTimeMillis();
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

			@Override
			public void onSuccess() {
				if (!progressShow) {
					return;
				}
				// 登陆成功，保存用户名密码
				MyApplication.getInstance().setUserName(ed_phone_regist.getText().toString());
				MyApplication.getInstance().setPassword(currentPassword);

				try {
					// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
					// ** manually load all local groups and
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					// 处理好友和群组
					initializeContacts();
				} catch (Exception e) {
					e.printStackTrace();
					// 取好友或者群聊失败，不让进入主页面
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							MyApplication.getInstance().logout(null);
							Toast.makeText(getApplicationContext(), R.string.login_failure_failed, 1).show();
						}
					});
					return;
				}
				// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
				boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(MyApplication.currentUserNick.trim());
				if (!updatenick) {
					Log.e("LoginActivity", "update current user nick fail");
				}
				if (!Activity_Regist.this.isFinishing() && pd.isShowing()) {
					pd.dismiss();
				}
				Intent intent = new Intent(Activity_Regist.this, SplashActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(final int code, final String message) {
				if (!progressShow) {
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message, Toast.LENGTH_SHORT)
								.show();
					}
				});
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
}
