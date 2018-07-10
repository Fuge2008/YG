package com.femto.ugershop.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;

import com.easemob.EMCallBack;
import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_Account;
import com.femto.ugershop.activity.Activity_ChangePD;
import com.femto.ugershop.activity.Activity_ConsultCen;
import com.femto.ugershop.activity.Activity_Contacts;
import com.femto.ugershop.activity.Activity_CustomMain;
import com.femto.ugershop.activity.Activity_Designer;
import com.femto.ugershop.activity.Activity_FansNub;
import com.femto.ugershop.activity.Activity_FocusMe;
import com.femto.ugershop.activity.Activity_GoodsDetails;
import com.femto.ugershop.activity.Activity_List;
import com.femto.ugershop.activity.Activity_Login;
import com.femto.ugershop.activity.Activity_Message;
import com.femto.ugershop.activity.Activity_MyCoupon;
import com.femto.ugershop.activity.Activity_MyCouponCode;
import com.femto.ugershop.activity.Activity_MyCustom;
import com.femto.ugershop.activity.Activity_NewDZ;
import com.femto.ugershop.activity.Activity_OrderMG_Custom;
import com.femto.ugershop.activity.Activity_PersonData;
import com.femto.ugershop.activity.Activity_PostDetails;
import com.femto.ugershop.activity.Activity_Rule;
import com.femto.ugershop.activity.Activity_ShoppingCar;
import com.femto.ugershop.activity.Ativity_GetMoney;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.easemob.applib.utils.DownLoadManager;
import com.femto.ugershop.fragment.Fragment_Me.MyBC;
import com.femto.ugershop.view.CircleImageView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_Me_Custom extends BaseFragment implements OnClickListener {
	private ImageView im_topersondata_c;
	private DisplayImageOptions options;
	private int myId, userId, money, score, scoreRanking, myActionCount, openMake, fansCount, topCount;
	private boolean isget = false;
	private String level, userName, userImg;
	private CircleImageView im_head_me_c;
	private TextView tv_usernameme_c, tv_isopenme_c, tv_focusnumme_c, tv_fans_me_c, tv_rank_me_c, tv_score_me_c, tv_topnub_me_c,
			tv_yue_c, rank_c, tv_exit, tv_unredcount_c, tv_updata_me_c, tv_entrymain_c, tv_tomycoupon;
	private RelativeLayout rl_account_c, rl_odergm_c, rl_conta_c, rl_mode_c, rl_shopcar, rl_share, rl_more_c, rl_toperc,
			rl_updata_c, rl_changepd_c;

	private String verNub, verUrl, versionName;
	private ImageView im_rank;
	private LinearLayout ll_myfans_c, ll_myfocus_c, ll_myyhm, ll_getcash;
	private String videopath = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2:
				showShare();
				break;

			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_me_custom, container, false);

		try {
			versionName = getVersionName();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out = new File(videopath);
		if (!out.exists()) {
			out.mkdirs();
		}
		videopath = out.getPath();
		registMBC();
		initParams();
		initView(view);

		return view;
	}

	private void getversion() {
		MyApplication.ahc.post(AppFinalUrl.usergetVersion, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				verNub = response.optString("nub");
				verUrl = response.optString("url");
				if (versionName.equals("" + verNub)) {
					tv_updata_me_c.setText("已是最新版本");
				} else {
					tv_updata_me_c.setText("有新版本");

				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.im_topersondata_c1:
			Intent intent = new Intent(getActivity(), Activity_PersonData.class);
			startActivityForResult(intent, 1);
			break;
		case R.id.rl_toperc:
			Intent intent1 = new Intent(getActivity(), Activity_PersonData.class);
			startActivityForResult(intent1, 1);
			break;
		case R.id.rl_account_c:
			Intent intent_account_c = new Intent(getActivity(), Activity_Account.class);
			startActivity(intent_account_c);
			break;
		case R.id.rl_odergm_c:
			Intent intent_odergm_c = new Intent(getActivity(), Activity_OrderMG_Custom.class);
			startActivity(intent_odergm_c);
			break;
		case R.id.rl_conta_cs:
			Intent intent_conta_c = new Intent(getActivity(), Activity_Contacts.class);
			startActivity(intent_conta_c);
			break;
		case R.id.rl_mode_c:
			Intent intent_mode_c = new Intent(getActivity(), Activity_NewDZ.class);
			intent_mode_c.putExtra("imageurl", userImg);
			startActivity(intent_mode_c);
			break;

		case R.id.rl_shopcar:
			Intent intent_shopcar = new Intent(getActivity(), Activity_ShoppingCar.class);
			startActivity(intent_shopcar);
			break;
		case R.id.tv_exit:
			showExit();
			break;
		case R.id.rl_share:
			if (userImg != null) {
				savePic(userImg);
			} else {

				showShare();
			}
			break;

		case R.id.rl_more_c:
			Toast.makeText(getActivity(), "开发中！敬请期待！", Toast.LENGTH_SHORT).show();
			break;
		case R.id.rl_updata_c:
			// if (verNub.equals(versionName)) {
			// Toast.makeText(getActivity(), "已是最新版本",
			// Toast.LENGTH_SHORT).show();
			// } else {
			// showUpdataDialog();
			// }
			UmengUpdateAgent.setUpdateOnlyWifi(false);
			UmengUpdateAgent.update(getActivity());
			UmengUpdateAgent.setDeltaUpdate(false);
			break;
		case R.id.tv_entrymain_c:
			Intent intent_tomain = new Intent(getActivity(), Activity_CustomMain.class);
			intent_tomain.putExtra("userId", myId);
			startActivity(intent_tomain);
			break;

		case R.id.rl_changepd_c:
			Intent intent_cpd = new Intent(getActivity(), Activity_ChangePD.class);
			startActivity(intent_cpd);
			break;
		case R.id.ll_myfans_c:
			Intent intent_myfans = new Intent(getActivity(), Activity_FansNub.class);
			intent_myfans.putExtra("userId", userId);
			startActivity(intent_myfans);
			break;
		case R.id.ll_myfocus_c:
			Intent intent_myfocus = new Intent(getActivity(), Activity_FocusMe.class);
			intent_myfocus.putExtra("userId", userId);
			startActivity(intent_myfocus);
			break;
		case R.id.tv_tomycoupon_c:
			Intent Intent = new Intent(getActivity(), Activity_MyCoupon.class);
			Intent.putExtra("type", 1);
			startActivityForResult(Intent, 208);
			break;

		case R.id.ll_myyhm:
			Intent intent_code = new Intent(getActivity(), Activity_MyCouponCode.class);
			startActivity(intent_code);
			break;
		case R.id.ll_getcash:
			// getCashDia();
			Intent intent_getcash = new Intent(getActivity(), Ativity_GetMoney.class);
			startActivityForResult(intent_getcash, 1);
			break;
		default:
			break;
		}

	}

	private void getCashDia() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("是否提现?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				// logOutOtherLogin();
				getCash();
			}

		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).show();
	}

	private void getCash() {
		// TODO Auto-generated method stub

	}

	private void showShare() {
		ShareSDK.initSDK(getActivity());
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("分享优格设计师:" + userName);
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.femto.ugershop");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("优格是一款由设计师和消费者对接的服装定制平台");
		if (dir != null) {
			oks.setImagePath(dir.getPath());// 确保SDcard下面存在此张图片
		}
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.femto.ugershop");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("优格出品");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.femto.ugershop");
		oks.addHiddenPlatform(Twitter.NAME);
		oks.addHiddenPlatform(Facebook.NAME);
		// 启动分享GUI
		oks.show(getActivity());

	}

	private void showExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("确定退出?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				// logOutOtherLogin();
				logout();
			}

		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).show();
	}

	private void logOutOtherLogin() {
		// TODO Auto-generated method stub
		ShareSDK.initSDK(getActivity());
		Platform qzone = ShareSDK.getPlatform(getActivity(), QQ.NAME);
		Platform sinaWeibo = ShareSDK.getPlatform(getActivity(), SinaWeibo.NAME);
		Platform wechat = ShareSDK.getPlatform(getActivity(), Wechat.NAME);
		if (qzone != null) {
			qzone.removeAccount();
		} else {
			System.out.println("zuoqq没有授权===");
		}
		if (sinaWeibo != null) {
			sinaWeibo.removeAccount();
		} else {
			System.out.println("zuoxina没有授权===");
		}
		if (wechat != null) {
			wechat.removeAccount();
		} else {
			System.out.println("zuoweixin没有授权===");
		}
		// if (pf != null) {
		// pf.setPlatformActionListener(new PlatformActionListener() {
		//
		// @Override
		// public void onError(Platform arg0, int arg1, Throwable arg2) {
		// // TODO Auto-generated method stub
		// System.out.println("zuo退出登录===" + arg2.toString());
		// }
		//
		// @Override
		// public void onComplete(Platform arg0, int arg1, HashMap<String,
		// Object> arg2) {
		// // TODO Auto-generated method stub
		// logout();
		// }
		//
		// @Override
		// public void onCancel(Platform arg0, int arg1) {
		// // TODO Auto-generated method stub
		//
		// }
		// });

		logout();
		// } else {
		// System.out.println("zuo没有退出第三方===");
		// logout();
		// }

		// isValid和removeAccount不开启线程，会直接返回。
	}

	public void logout() {
		System.out.println("zuo===退出");
		final ProgressDialog pd = new ProgressDialog(getActivity());
		String st = getResources().getString(R.string.Are_logged_out);
		pd.setMessage(st);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		MyApplication.getInstance().logout(new EMCallBack() {

			@Override
			public void onSuccess() {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						// 重新显示登陆页面
						Intent intent_login = new Intent(getActivity(), Activity_Login.class);
						startActivity(intent_login);
						SharedPreferences sp = getActivity().getSharedPreferences("Login", getActivity().MODE_PRIVATE);
						Editor edit = sp.edit();
						edit.putBoolean("islogin", false);
						edit.commit();
						MyApplication.getInfo();
						getActivity().finish();
					}
				});
			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(int code, String message) {

			}
		});
	}

	private void initView(View v) {
		im_topersondata_c = (ImageView) v.findViewById(R.id.im_topersondata_c1);
		im_topersondata_c.setOnClickListener(this);
		im_head_me_c = (CircleImageView) v.findViewById(R.id.im_head_me_c);
		tv_usernameme_c = (TextView) v.findViewById(R.id.tv_usernameme_c);
		tv_isopenme_c = (TextView) v.findViewById(R.id.tv_isopenme_c);
		tv_focusnumme_c = (TextView) v.findViewById(R.id.tv_focusnumme_c);
		tv_fans_me_c = (TextView) v.findViewById(R.id.tv_fans_me_c);
		tv_rank_me_c = (TextView) v.findViewById(R.id.tv_rank_me_c);
		tv_tomycoupon = (TextView) v.findViewById(R.id.tv_tomycoupon_c);
		tv_score_me_c = (TextView) v.findViewById(R.id.tv_score_me_c);
		tv_topnub_me_c = (TextView) v.findViewById(R.id.tv_topnub_me_c);
		tv_yue_c = (TextView) v.findViewById(R.id.tv_yue_c);
		rank_c = (TextView) v.findViewById(R.id.rank_c);
		im_rank = (ImageView) v.findViewById(R.id.im_rank);
		tv_exit = (TextView) v.findViewById(R.id.tv_exit);
		tv_unredcount_c = (TextView) v.findViewById(R.id.tv_unredcount_c);

		tv_updata_me_c = (TextView) v.findViewById(R.id.tv_updata_me_c);
		tv_entrymain_c = (TextView) v.findViewById(R.id.tv_entrymain_c);
		rl_toperc = (RelativeLayout) v.findViewById(R.id.rl_toperc);
		ll_myfans_c = (LinearLayout) v.findViewById(R.id.ll_myfans_c);
		ll_myfocus_c = (LinearLayout) v.findViewById(R.id.ll_myfocus_c);
		ll_myyhm = (LinearLayout) v.findViewById(R.id.ll_myyhm);
		ll_getcash = (LinearLayout) v.findViewById(R.id.ll_getcash);

		ll_myfans_c.setOnClickListener(this);

		ll_getcash.setOnClickListener(this);
		tv_tomycoupon.setOnClickListener(this);
		ll_myfocus_c.setOnClickListener(this);
		rl_toperc.setOnClickListener(this);
		tv_exit.setOnClickListener(this);
		tv_entrymain_c.setOnClickListener(this);
		rl_account_c = (RelativeLayout) v.findViewById(R.id.rl_account_c);
		rl_account_c.setOnClickListener(this);
		rl_odergm_c = (RelativeLayout) v.findViewById(R.id.rl_odergm_c);
		rl_odergm_c.setOnClickListener(this);
		rl_conta_c = (RelativeLayout) v.findViewById(R.id.rl_conta_cs);
		rl_conta_c.setOnClickListener(this);
		rl_mode_c = (RelativeLayout) v.findViewById(R.id.rl_mode_c);
		rl_mode_c.setOnClickListener(this);

		rl_shopcar = (RelativeLayout) v.findViewById(R.id.rl_shopcar);
		rl_shopcar.setOnClickListener(this);

		rl_share = (RelativeLayout) v.findViewById(R.id.rl_share);
		rl_share.setOnClickListener(this);

		rl_more_c = (RelativeLayout) v.findViewById(R.id.rl_more_c);
		rl_more_c.setOnClickListener(this);
		ll_myyhm.setOnClickListener(this);
		rl_updata_c = (RelativeLayout) v.findViewById(R.id.rl_updata_c);
		rl_updata_c.setOnClickListener(this);

		rl_changepd_c = (RelativeLayout) v.findViewById(R.id.rl_changepd_c);
		rl_changepd_c.setOnClickListener(this);
		tv_updata_me_c.setText("V" + versionName);
	}

	private String getVersionName() throws Exception {
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageManager packageManager = getActivity().getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
		return packInfo.versionName;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden) {

			// getversion();
			MobclickAgent.onPageStart("我");
		} else {
			MobclickAgent.onPageEnd("我");
		}

		if (!hidden && !isget) {
			showProgressDialog("加载中");
			getData();
		}
	}

	private MyBC mbc;
	private int type;

	private void registMBC() {
		mbc = new MyBC();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.hx.refresh.unread");
		filter.addAction("com.refresh.message");
		getActivity().registerReceiver(mbc, filter);
	}

	class MyBC extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if ("com.hx.refresh.unread".equals(action)) {
				int intExtra = intent.getIntExtra("count", 0);
				if (intExtra != 0) {
					tv_unredcount_c.setVisibility(View.VISIBLE);
					if (intExtra > 99) {
						tv_unredcount_c.setText("99+");
					} else {
						tv_unredcount_c.setText("" + intExtra);
					}

				} else {
					tv_unredcount_c.setVisibility(View.GONE);
				}

			}
			if ("com.refresh.message".equals(action)) {
				// getUnread();
			}
		}
	}

	private void getData() {

		RequestParams params = new RequestParams();
		params.put("userId", myId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetUserInfo, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo====response=" + response.toString());
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						isget = true;
					}
					level = response.optString("level");
					userName = response.optString("userName");
					userImg = response.optString("userImg");
					userId = response.optInt("userId");
					money = response.optInt("money");
					score = response.optInt("score");
					scoreRanking = response.optInt("scoreRanking");
					myActionCount = response.optInt("myActionCount");
					openMake = response.optInt("openMake");
					fansCount = response.optInt("fansCount");
					topCount = response.optInt("topCount");

					// tv_usernameme_c, tv_isopenme_c, tv_focusnumme_c,
					// tv_fans_me_c, tv_rank_me_c, tv_score_me_c,
					// tv_topnub_me_c,
					// tv_yue_c, rank_c
					if (userName == null || userName.equals("null")) {
						tv_usernameme_c.setText("未填写");
					} else {
						tv_usernameme_c.setText("" + userName);
					}

					tv_focusnumme_c.setText("" + myActionCount);
					tv_fans_me_c.setText("" + fansCount);
					// tv_rank_me_c.setText("" + level);

					if (level.contains("合一")) {
						im_rank.setBackgroundResource(R.drawable.heyi);
					} else if (level.contains("黑一")) {
						im_rank.setBackgroundResource(R.drawable.bl1);
					} else if (level.contains("黑二")) {
						im_rank.setBackgroundResource(R.drawable.bl2);
					} else if (level.contains("黑三")) {
						im_rank.setBackgroundResource(R.drawable.bl3);
					} else if (level.contains("灰一")) {
						im_rank.setBackgroundResource(R.drawable.g1);
					} else if (level.contains("灰二")) {
						im_rank.setBackgroundResource(R.drawable.g2);
					} else if (level.contains("灰三")) {
						im_rank.setBackgroundResource(R.drawable.g3);
					} else if (level.contains("白一")) {
						im_rank.setBackgroundResource(R.drawable.w1);
					} else if (level.contains("白二")) {
						im_rank.setBackgroundResource(R.drawable.w2);
					} else if (level.contains("白三")) {
						im_rank.setBackgroundResource(R.drawable.w3);
					}
					tv_score_me_c.setText("" + score);
					tv_topnub_me_c.setText("" + topCount);
					tv_yue_c.setText("" + money);
					rank_c.setText("第" + scoreRanking + "名");
					SharedPreferences sp = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
					Editor edit = sp.edit();
					edit.putString("headImage", userImg);
					edit.commit();
					MyApplication.getInfo();
					ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + userImg, im_head_me_c, options);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void initParams() {
		SharedPreferences sp = getActivity().getSharedPreferences("Login", getActivity().MODE_PRIVATE);
		myId = sp.getInt("userId", 0);
		type = sp.getInt("type", 0);
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.person) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.person) // image连接地址为空时
				.showImageOnFail(R.drawable.person) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null && resultCode == 1) {
			getData();
		}
	}

	// 安装apk
	protected void installApk(File file) {
		Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		// 执行的数据类型
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		startActivity(intent);
		System.exit(0);
	}

	int DOWN_ERROR;
	ProgressDialog pd;

	/*
	 * 从服务器中下载APK
	 */
	protected void downLoadApk() {
		final ProgressDialog pd; // 进度条对话框
		pd = new ProgressDialog(getActivity());
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在下载更新");
		pd.setCancelable(false);
		pd.show();
		new Thread() {
			@Override
			public void run() {
				try {
					File file = DownLoadManager.getFileFromServer(AppFinalUrl.BASEURL + verUrl, pd);
					sleep(1000);
					if (file != null) {
						installApk(file);
					}
					pd.dismiss(); // 结束掉进度条对话框
				} catch (Exception e) {
					// Message msg = new Message();
					// msg.what = DOWN_ERROR;
					// handler.sendMessage(msg);
					// e.printStackTrace();
				}
			}
		}.start();
	}

	protected void showUpdataDialog() {
		AlertDialog.Builder builer = new AlertDialog.Builder(getActivity());
		builer.setTitle("版本升级");
		builer.setMessage("有最新版本，是否升级?");
		// 当点确定按钮时从服务器上下载 新的apk 然后安装 װ
		builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				downLoadApk();
			}
		});
		builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// do sth
			}
		});
		AlertDialog dialog = builer.create();
		dialog.show();
	}

	private File dir;
	FileOutputStream fos;
	private File out;

	private void savePic(String urlPath) {
		dir = new File(videopath, "showpic.jpg");
		try {
			fos = new FileOutputStream(dir);
			showProgressDialog("分享中...");
			MyApplication.ahc.post(AppFinalUrl.BASEURL + urlPath, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] data) {
					// TODO Auto-generated method stub
					System.out.println("zuo====arg0=" + arg0);
					dismissProgressDialog();
					try {
						fos.write(data);
						fos.flush();
						fos.close();
						handler.sendEmptyMessage(2);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					// TODO Auto-generated method stub
					dismissProgressDialog();
				}
			});
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dismissProgressDialog();
		}
	}

}
