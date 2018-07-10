package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tpl.OnLoginListener;
import cn.sharesdk.tpl.SignupPage;
import cn.sharesdk.tpl.ThirdPartyLogin;
import cn.sharesdk.tpl.UserInfo;
import cn.sharesdk.wechat.friends.Wechat;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.femto.hx.utils.CommonUtils;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.Constant;
import com.femto.ugershop.application.DemoHXSDKHelper;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.easemob.applib.db.UserDao;
import com.femto.ugershop.easemob.applib.domain.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Login extends BaseActivity implements Callback, PlatformActionListener {
	private RelativeLayout rl_back_login;
	private TextView tv_sure_login, tv_toregist;
	private static final String TAG = "LoginActivity";
	public static final int REQUEST_CODE_SETNICK = 1;
	private EditText usernameEditText;
	private EditText passwordEditText;

	private boolean progressShow;
	private boolean autoLogin = false;
	private int userId;
	private String currentUsername;
	private String currentPassword;
	private int isother;
	private TextView tv_fogetpd, tv_other_nub;
	private ImageView im_wechat, im_weibo, im_qq;

	@Override
	public void initView() {
		MyApplication.addActivity(this);
		rl_back_login = (RelativeLayout) findViewById(R.id.rl_back_login);
		tv_sure_login = (TextView) findViewById(R.id.tv_sure_login);
		tv_toregist = (TextView) findViewById(R.id.tv_toregist);
		usernameEditText = (EditText) findViewById(R.id.ed_phone_lo);
		passwordEditText = (EditText) findViewById(R.id.ed_pd_lo);
		tv_fogetpd = (TextView) findViewById(R.id.tv_fogetpd);
		tv_other_nub = (TextView) findViewById(R.id.tv_other_nub);
		im_wechat = (ImageView) findViewById(R.id.im_wechat);
		im_weibo = (ImageView) findViewById(R.id.im_weibo);
		im_qq = (ImageView) findViewById(R.id.im_qq);

	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		handler = new Handler(this);
		// 如果用户名密码都有，直接进入主页面
		isother = getIntent().getIntExtra("isother", 0);
		System.out.println("zuo===DemoHXSDKHelper.getInstance().isLogined()=" + DemoHXSDKHelper.getInstance().isLogined());
		if (DemoHXSDKHelper.getInstance().isLogined()) {
			autoLogin = true;
			// startActivity(new Intent(Activity_Login.this,
			// MainActivity.class));
			return;
		}
		// 如果用户名改变，清空密码
		usernameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				passwordEditText.setText(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		if (MyApplication.getInstance().getUserName() != null) {
			usernameEditText.setText(MyApplication.getInstance().getUserName());
		}
	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_login.setOnClickListener(this);
		tv_sure_login.setOnClickListener(this);
		tv_toregist.setOnClickListener(this);
		tv_fogetpd.setOnClickListener(this);
		// tv_other_nub.setOnClickListener(this);
		im_wechat.setOnClickListener(this);
		im_weibo.setOnClickListener(this);
		im_qq.setOnClickListener(this);
		initSDK(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_login);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_back_login:
			if (isother == 1) {

				Intent intent = new Intent(Activity_Login.this, MainActivity.class);
				startActivity(intent);
				finish();
			} else {
				finish();
			}

			break;
		case R.id.tv_toregist:
			Intent intent_toregist = new Intent(Activity_Login.this, Activity_Regist.class);
			startActivity(intent_toregist);
			break;
		case R.id.tv_sure_login:
			// Intent intent = new Intent(Activity_Login.this,
			// MainActivity.class);
			// startActivity(intent);
			// finish();

			if (usernameEditText.getText().toString().trim().length() == 0
					|| passwordEditText.getText().toString().trim().length() == 0) {
				showToast("请输入正确的手机号码和密码", 0);
				return;
			}

			login();

			break;
		case R.id.tv_fogetpd:
			Intent intent = new Intent(this, Activity_FindPassWord.class);
			startActivity(intent);
			break;
		case R.id.tv_other_nub:
			// showDemo();
			break;
		// im_wechat, im_weibo, im_qq
		case R.id.im_qq:
			Platform qzone = ShareSDK.getPlatform(QZone.NAME);
			authorize(qzone);
			break;
		case R.id.im_weibo:
			Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
			authorize(sina);
			break;
		case R.id.im_wechat:
			Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
			authorize(wechat);
			break;
		default:
			break;
		}
	}

	/**
	 * 登录
	 * 
	 * @param view
	 */
	public void loginEM(int userId, String huanxinPass) {
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			return;
		}
		currentUsername = "" + userId;
		currentPassword = huanxinPass;

		if (TextUtils.isEmpty(currentUsername)) {
			Toast.makeText(this, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(currentPassword)) {
			Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}

		progressShow = true;
		final ProgressDialog pd = new ProgressDialog(Activity_Login.this);
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				progressShow = false;
			}
		});
		pd.setMessage(getString(R.string.Is_landing));
		// pd.show();

		final long start = System.currentTimeMillis();
		// 调用sdk登陆方法登陆聊天服务器
		EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

			@Override
			public void onSuccess() {
				if (!progressShow) {
					return;
				}
				// 登陆成功，保存用户名密码
				MyApplication.getInstance().setUserName(usernameEditText.getText().toString());
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
				if (!Activity_Login.this.isFinishing() && pd.isShowing()) {
					pd.dismiss();
				}
				dismissProgressDialog();
				Intent intent = new Intent(Activity_Login.this, MainActivity.class);
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

	public void login() {
		showProgressDialog("登录中...");
		RequestParams params = new RequestParams();
		// private EditText usernameEditText;
		// private EditText passwordEditText;
		params.put("userName", usernameEditText.getText().toString().trim());
		params.put("password", passwordEditText.getText().toString().trim());
		MyApplication.ahc.post(AppFinalUrl.useruserlogin, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);

				System.out.println("zuologin" + response.toString());
				try {
					String result = response.optString("result");
					if (result.equals("0")) {
						int type = response.optInt("type");
						userId = response.optInt("userId");
						String huanxinPass = response.optString("huanxinPass");
						String token = response.optString("token");
						SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
						Editor edit = sp.edit();
						edit.putBoolean("islogin", true);
						edit.putInt("userId", userId);
						edit.putString("token", token);
						edit.putInt("type", type);
						edit.commit();
						if (userId != 0) {
							loginEM(userId, huanxinPass);
						}
						MyApplication.getInfo();
					} else {
						dismissProgressDialog();
						Toast.makeText(Activity_Login.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
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
		UserDao dao = new UserDao(Activity_Login.this);
		List<User> users = new ArrayList<User>(userlist.values());
		dao.saveContactList(users);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (autoLogin) {
			return;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isother == 1) {

				Intent intent = new Intent(Activity_Login.this, MainActivity.class);
				startActivity(intent);
				finish();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private static String APPKEY = "27fe7909f8e8";
	// 填写从短信SDK应用后台注册得到的APPSECRET
	private static String APPSECRET = "3c5264e7e05b8860a9b98b34506cfa6e";

	private void showDemo() {
		showProgressDialog("跳转中...");
		ThirdPartyLogin tpl = new ThirdPartyLogin();
		tpl.setSMSSDKAppkey(APPKEY, APPSECRET);
		tpl.setOnLoginListener(new OnLoginListener() {
			public boolean onSignin(String platform, HashMap<String, Object> res) {
				// 在这个方法填写尝试的代码，返回true表示还不能登录，需要注册
				// 此处全部给回需要注册
				String id, name, description, profile_image_url, allmessage;
				System.out.println("zuo-allmessage--" + res.toString() + "    platform=" + platform);
				// id = res.get("id").toString();// ID
				// name = res.get("name").toString();// 用户名
				// description = res.get("description").toString();// 描述
				// profile_image_url =
				// res.get("profile_image_url").toString();// 头像链接
				// allmessage = id + name + description + profile_image_url;
				// System.out.println("zuo-allmessage--" + allmessage);
				if (MyApplication.other_token != null && !MyApplication.other_token.equals("")) {
					loginOther();
				} else {
					showToast("登录失败", 0);
				}
				return false;
			}

			public boolean onSignUp(UserInfo info) {
				// 填写处理注册信息的代码，返回true表示数据合法，注册页面可以关闭
				return true;
			}
		});
		tpl.show(this);
		dismissProgressDialog();
	}

	private void loginOther() {
		System.out.println("zuo111111111111111111");
		RequestParams params = new RequestParams();
		params.put("loginType", MyApplication.other_loginType);
		params.put("type", 2);
		params.put("token", MyApplication.other_token);
		params.put("username", MyApplication.other_username);
		params.put("url", MyApplication.other_url);
		showProgressDialog("登录中...");
		MyApplication.ahc.post(AppFinalUrl.userotherLogin, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuologin" + response.toString());

				String result = response.optString("result");
				if (result.equals("0")) {
					int type = response.optInt("type");
					userId = response.optInt("userId");
					String huanxinPass = response.optString("huanxinPass");
					SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
					Editor edit = sp.edit();
					edit.putBoolean("islogin", true);
					edit.putInt("userId", userId);
					edit.putString("token", "");
					edit.putInt("type", type);
					edit.commit();
					if (userId != 0) {
						loginEM(userId, huanxinPass);
					}
					MyApplication.getInfo();
				} else {
					dismissProgressDialog();
					Toast.makeText(Activity_Login.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	// 执行授权,获取用户信息
	// 文档：http://wiki.mob.com/Android_%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E8%B5%84%E6%96%99
	private void authorize(Platform plat) {

		if (plat == null) {
			popupOthers();
			return;
		}

		if (plat.isValid()) {
			plat.removeAccount();
		}

		plat.setPlatformActionListener(this);
		plat.SSOSetting(false);
		plat.showUser(null);
	}

	private Handler handler;
	private static final int MSG_SMSSDK_CALLBACK = 1;
	private static final int MSG_AUTH_CANCEL = 2;
	private static final int MSG_AUTH_ERROR = 3;
	private static final int MSG_AUTH_COMPLETE = 4;
	private OnLoginListener signupListener;

	/** 设置授权回调，用于判断是否进入注册 */
	public void setOnLoginListener(OnLoginListener l) {
		this.signupListener = l;
	}

	// 其他登录对话框
	private void popupOthers() {
		Dialog dlg = new Dialog(Activity_Login.this, R.style.WhiteDialog);
		View dlgView = View.inflate(this, R.layout.tpl_other_plat_dialog, null);
		View tvFacebook = dlgView.findViewById(R.id.tvFacebook);
		tvFacebook.setTag(dlg);
		tvFacebook.setOnClickListener(this);
		View tvTwitter = dlgView.findViewById(R.id.tvTwitter);
		tvTwitter.setTag(dlg);
		tvTwitter.setOnClickListener(this);

		dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlg.setContentView(dlgView);
		dlg.show();
	}

	public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
		if (action == Platform.ACTION_USER_INFOR) {
			Message msg = new Message();
			msg.what = MSG_AUTH_COMPLETE;
			msg.obj = new Object[] { platform.getName(), res };
			handler.sendMessage(msg);
			MyApplication.other_token = platform.getDb().getUserId();
			MyApplication.other_username = platform.getDb().getUserName();
			MyApplication.other_url = platform.getDb().getUserIcon();
			// MyApplication.other_token
			MyApplication.other_loginType = platform.getName();

			System.out.println("zuo----otherlogin==" + MyApplication.other_token + "   MyApplication.other_username="
					+ MyApplication.other_username + "   platform.getDb().getUserIcon()=" + platform.getDb().getUserIcon()
					+ " MyApplication.other_loginType=" + MyApplication.other_loginType);

		}
	}

	public void onError(Platform platform, int action, Throwable t) {
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(MSG_AUTH_ERROR);
		}
		t.printStackTrace();
		// System.out.println("zuo错误=" + t.toString());
	}

	public void onCancel(Platform platform, int action) {
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(MSG_AUTH_CANCEL);
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_AUTH_CANCEL: {
			// 取消授权
			Toast.makeText(Activity_Login.this, R.string.auth_cancel, Toast.LENGTH_SHORT).show();
		}
			break;
		case MSG_AUTH_ERROR: {
			// 授权失败
			Toast.makeText(Activity_Login.this, R.string.auth_error, Toast.LENGTH_SHORT).show();
		}
			break;
		case MSG_AUTH_COMPLETE: {
			// 授权成功
			Toast.makeText(Activity_Login.this, R.string.auth_complete, Toast.LENGTH_SHORT).show();
			// Object[] objs = (Object[]) msg.obj;
			// String platform = (String) objs[0];
			// HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
			// if (signupListener != null && signupListener.onSignin(platform,
			// res)) {
			// SignupPage signupPage = new SignupPage();
			// signupPage.setOnLoginListener(signupListener);
			// signupPage.setPlatform(platform);
			// signupPage.show(Activity_Login.this, null);
			// }
			// finish();
			if (MyApplication.other_token != null && !MyApplication.other_token.equals("")) {
				loginOther();
			} else {
				showToast("登录失败", 0);
			}
		}
			break;

		}
		return false;
	}

	public void show(Context context) {

	}

	private void initSDK(Context context) {
		// 初始化sharesdk,具体集成步骤请看文档：
		// http://wiki.mob.com/Android_%E5%BF%AB%E9%80%9F%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97
		ShareSDK.initSDK(context);

	}
}
