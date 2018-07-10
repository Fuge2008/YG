package com.femto.ugershop.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.Header;
import org.json.JSONObject;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.twitter.Twitter;

import com.easemob.EMCallBack;
import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_Account;
import com.femto.ugershop.activity.Activity_Account_Statistics;
import com.femto.ugershop.activity.Activity_BrandApply;
import com.femto.ugershop.activity.Activity_ChangePD;
import com.femto.ugershop.activity.Activity_ConsultCen;
import com.femto.ugershop.activity.Activity_Contacts;
import com.femto.ugershop.activity.Activity_CustomApply;
import com.femto.ugershop.activity.Activity_Designer;
import com.femto.ugershop.activity.Activity_FansNub;
import com.femto.ugershop.activity.Activity_FocusMe;
import com.femto.ugershop.activity.Activity_List;
import com.femto.ugershop.activity.Activity_Login;
import com.femto.ugershop.activity.Activity_Message;
import com.femto.ugershop.activity.Activity_MyCoupon;
import com.femto.ugershop.activity.Activity_MyCustom;
import com.femto.ugershop.activity.Activity_OrderMG;
import com.femto.ugershop.activity.Activity_PersonData;
import com.femto.ugershop.activity.Activity_Rule;
import com.femto.ugershop.activity.Activity_SendSample;
import com.femto.ugershop.activity.Activity_ShoppingCar;
import com.femto.ugershop.activity.Activity_StorePutaway;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.easemob.applib.utils.DownLoadManager;
import com.femto.ugershop.view.CircleImageView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
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

public class Fragment_Me extends BaseFragment implements OnClickListener {
	private View view;
	private RelativeLayout rl_account, rl_order, rl_storeput, rl_accountdetails, rl_brandapply, rl_customapply, rl_mycontacts,
			rl_consult, rl_shopcarde, rl_messagemede, rl_share, rl_toper, rl_updata, rl_activity, rl_sendsampls, rl_changepd,
			rl_myupcustom;
	private TextView tv_entrymain;
	private ImageView im_topersondata;
	private int userId;
	private boolean islogin;
	private String level, userName;
	private int productCount, fifteenSale, score, scoreRanking, openMake, fansCount, topCount, fifteenGet;
	private TextView tv_usernameme, tv_isopenme, tv_focusnumme, tv_fans_me, tv_rank_me, tv_score_me, tv_topnub_me;
	private TextView tv_fiveget, fivesalse, tv_paiming, tv_upnub_me, tv_exitde, tv_unredcount, tv_unredcount_message,
			tv_updata_me, tv_tomycoupon;
	private String userImg;
	private DisplayImageOptions options;
	private CircleImageView im_head_me;
	private MyBC mbc;
	private boolean isget = false;
	private int allCount, aMeCount, discussCount, topCount1;
	private String verNub, verUrl, versionName;
	private int myActionCount;
	private LinearLayout ll_contain_trust, ll_dzpj, ll_myfans, ll_focus_me;
	private int truststar = 1;
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
	private int type;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_me, container, false);
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

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		System.out.println("zuo我==" + hidden);
		if (!hidden) {
			getUnread();
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

	private void getversion() {
		MyApplication.ahc.post(AppFinalUrl.usergetVersion, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				verNub = response.optString("nub");
				verUrl = response.optString("url");
				if (versionName.equals("" + verNub)) {
					tv_updata_me.setText("已是最新版本");
				} else {
					tv_updata_me.setText("有新版本");
					// tv_updata_me.setTextSize(30);
					// tv_updata_me.setTextColor(getResources().getColor(R.color.red));
				}
			}
		});

	}

	private void getUnread() {
		RequestParams params = new RequestParams();
		params.put("userId", userId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMyMessageCount, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				// allCount, aMeCount, discussCount, topCount1
				allCount = 0;
				aMeCount = 0;
				discussCount = 0;
				topCount1 = 0;
				allCount = response.optInt("allCount");
				aMeCount = response.optInt("aMeCount");
				discussCount = response.optInt("discussCount");
				topCount1 = response.optInt("topCount");
				if (allCount != 0) {
					tv_unredcount_message.setVisibility(View.VISIBLE);
					if (allCount > 99) {
						tv_unredcount_message.setText("99+");

					} else {

						tv_unredcount_message.setText("" + allCount);
					}
				} else {
					tv_unredcount_message.setVisibility(View.GONE);
				}
			}
		});
	}

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
					tv_unredcount.setVisibility(View.VISIBLE);
					if (intExtra > 99) {
						tv_unredcount.setText("99+");
					} else {
						tv_unredcount.setText("" + intExtra);
					}

				} else {
					tv_unredcount.setVisibility(View.GONE);
				}

			}
			if ("com.refresh.message".equals(action)) {
				getUnread();
			}
		}
	}

	private void getData() {
		RequestParams params = new RequestParams();
		params.put("userId", userId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetDesinUserInfo, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				System.out.println("zuo====" + response.toString());
				isget = true;
				productCount = response.optInt("productCount");
				fifteenSale = response.optInt("fifteenSale");
				fifteenGet = response.optInt("fifteenGet");
				score = response.optInt("score");
				truststar = response.optInt("nub");
				scoreRanking = response.optInt("scoreRanking");
				openMake = response.optInt("openMake");
				fansCount = response.optInt("fansCount");
				topCount = response.optInt("topCount");
				myActionCount = response.optInt("myActionCount");
				level = response.optString("level");
				userName = response.optString("userName");
				userImg = response.optString("userImg");
				if (openMake == 1) {
					tv_isopenme.setText("已开通定制");
					ll_dzpj.setVisibility(View.VISIBLE);
				} else {
					tv_isopenme.setText("未开通定制");
					ll_dzpj.setVisibility(View.GONE);
				}
				// tv_usernameme, tv_isopenme, tv_focusnumme,
				// tv_fans_me,
				// tv_rank_me, tv_score_me, tv_topnub_me;
				if (userName == null || userName.equals("null")) {
					tv_usernameme.setText("未填写");
				} else {
					tv_usernameme.setText("" + userName);
				}

				tv_focusnumme.setText("" + myActionCount);
				tv_fans_me.setText("" + fansCount);
				// tv_rank_me.setText("" + level);

				if (level.contains("合一")) {
					tv_rank_me.setBackgroundResource(R.drawable.heyi);
				} else if (level.contains("黑一")) {
					tv_rank_me.setBackgroundResource(R.drawable.bl1);
				} else if (level.contains("黑二")) {
					tv_rank_me.setBackgroundResource(R.drawable.bl2);
				} else if (level.contains("黑三")) {
					tv_rank_me.setBackgroundResource(R.drawable.bl3);
				} else if (level.contains("灰一")) {
					tv_rank_me.setBackgroundResource(R.drawable.g1);
				} else if (level.contains("灰二")) {
					tv_rank_me.setBackgroundResource(R.drawable.g2);
				} else if (level.contains("灰三")) {
					tv_rank_me.setBackgroundResource(R.drawable.g3);
				} else if (level.contains("白一")) {
					tv_rank_me.setBackgroundResource(R.drawable.w1);
				} else if (level.contains("白二")) {
					tv_rank_me.setBackgroundResource(R.drawable.w2);
				} else if (level.contains("白三")) {
					tv_rank_me.setBackgroundResource(R.drawable.w3);
				}

				tv_score_me.setText("" + score);
				tv_topnub_me.setText("" + topCount);
				// tv_fiveget,fivesalse,tv_paiming,tv_upnub_me
				tv_fiveget.setText("¥" + fifteenGet);
				fivesalse.setText("" + fifteenSale);
				tv_paiming.setText("第" + scoreRanking + "名");
				tv_upnub_me.setText("" + productCount);
				SharedPreferences sp = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
				Editor edit = sp.edit();
				edit.putString("headImage", userImg);
				edit.commit();
				MyApplication.getInfo();
				ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + userImg, im_head_me, options);
				setStart();
			}
		});
	}

	private void initParams() {
		SharedPreferences sp = getActivity().getSharedPreferences("Login", getActivity().MODE_PRIVATE);
		islogin = sp.getBoolean("islogin", false);
		userId = sp.getInt("userId", 0);
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

	private void initView(View v) {
		rl_account = (RelativeLayout) v.findViewById(R.id.rl_account);
		rl_account.setOnClickListener(this);
		rl_order = (RelativeLayout) v.findViewById(R.id.rl_order);
		rl_order.setOnClickListener(this);
		rl_storeput = (RelativeLayout) v.findViewById(R.id.rl_storeput);
		rl_storeput.setOnClickListener(this);
		rl_accountdetails = (RelativeLayout) v.findViewById(R.id.rl_accountdetails);
		rl_accountdetails.setOnClickListener(this);
		rl_brandapply = (RelativeLayout) v.findViewById(R.id.rl_brandapply);
		rl_brandapply.setOnClickListener(this);
		rl_customapply = (RelativeLayout) v.findViewById(R.id.rl_customapply);
		rl_customapply.setOnClickListener(this);
		rl_mycontacts = (RelativeLayout) v.findViewById(R.id.rl_mycontacts_d);
		rl_mycontacts.setOnClickListener(this);
		rl_consult = (RelativeLayout) v.findViewById(R.id.rl_consult);
		rl_myupcustom = (RelativeLayout) v.findViewById(R.id.rl_myupcustom);

		rl_consult.setOnClickListener(this);
		rl_myupcustom.setOnClickListener(this);
		tv_entrymain = (TextView) v.findViewById(R.id.tv_entrymain);
		tv_entrymain.setOnClickListener(this);
		tv_tomycoupon = (TextView) v.findViewById(R.id.tv_tomycoupon);
		tv_tomycoupon.setOnClickListener(this);
		im_topersondata = (ImageView) v.findViewById(R.id.im_topersondata1);
		im_topersondata.setOnClickListener(this);
		rl_toper = (RelativeLayout) v.findViewById(R.id.rl_toper);
		rl_toper.setOnClickListener(this);
		rl_updata = (RelativeLayout) v.findViewById(R.id.rl_updata);
		rl_updata.setOnClickListener(this);
		ll_dzpj = (LinearLayout) v.findViewById(R.id.ll_dzpj);
		ll_myfans = (LinearLayout) v.findViewById(R.id.ll_myfans);
		ll_focus_me = (LinearLayout) v.findViewById(R.id.ll_focus_me);
		ll_myfans.setOnClickListener(this);
		ll_focus_me.setOnClickListener(this);
		// tv_usernameme, tv_isopenme, tv_focusnumme, tv_fans_me,
		// tv_rank_me, tv_score_me, tv_topnub_me;
		// tv_fiveget,fivesalse,tv_paiming,tv_upnub_me
		tv_fiveget = (TextView) v.findViewById(R.id.tv_fiveget);
		fivesalse = (TextView) v.findViewById(R.id.fivesalse);
		tv_paiming = (TextView) v.findViewById(R.id.tv_paiming);
		tv_upnub_me = (TextView) v.findViewById(R.id.tv_upnub_me);

		tv_usernameme = (TextView) v.findViewById(R.id.tv_usernameme);
		tv_isopenme = (TextView) v.findViewById(R.id.tv_isopenme);
		tv_focusnumme = (TextView) v.findViewById(R.id.tv_focusnumme);
		tv_fans_me = (TextView) v.findViewById(R.id.tv_fans_me);
		tv_rank_me = (TextView) v.findViewById(R.id.tv_rank_me1);
		tv_score_me = (TextView) v.findViewById(R.id.tv_score_me);
		tv_topnub_me = (TextView) v.findViewById(R.id.tv_topnub_me);
		tv_unredcount = (TextView) v.findViewById(R.id.tv_unredcount);
		tv_unredcount_message = (TextView) v.findViewById(R.id.tv_unredcount_message1);
		tv_updata_me = (TextView) v.findViewById(R.id.tv_updata_me);
		im_head_me = (CircleImageView) v.findViewById(R.id.im_head_me);
		// rl_shopcarde, rl_messagemede
		rl_activity = (RelativeLayout) v.findViewById(R.id.rl_activity);
		rl_activity.setOnClickListener(this);
		rl_shopcarde = (RelativeLayout) v.findViewById(R.id.rl_shopcarde);
		rl_shopcarde.setOnClickListener(this);
		rl_messagemede = (RelativeLayout) v.findViewById(R.id.rl_messagemede);
		rl_messagemede.setOnClickListener(this);
		tv_exitde = (TextView) v.findViewById(R.id.tv_exitde);
		tv_exitde.setOnClickListener(this);
		rl_share = (RelativeLayout) v.findViewById(R.id.rl_share);
		rl_share.setOnClickListener(this);
		rl_sendsampls = (RelativeLayout) v.findViewById(R.id.rl_sendsampls);
		rl_sendsampls.setOnClickListener(this);
		rl_changepd = (RelativeLayout) v.findViewById(R.id.rl_changepd);
		rl_changepd.setOnClickListener(this);
		ll_contain_trust = (LinearLayout) v.findViewById(R.id.ll_contain_trust);

		tv_updata_me.setText("V" + versionName);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_account:
			Intent intent = new Intent(getActivity(), Activity_Account.class);
			startActivity(intent);
			break;
		case R.id.rl_order:
			Intent intent_order = new Intent(getActivity(), Activity_OrderMG.class);
			startActivity(intent_order);
			break;
		case R.id.rl_storeput:
			Intent intent_storeput = new Intent(getActivity(), Activity_StorePutaway.class);
			startActivity(intent_storeput);
			break;
		case R.id.rl_accountdetails:
			Intent intent_accountdetails = new Intent(getActivity(), Activity_Account_Statistics.class);
			startActivity(intent_accountdetails);
			break;
		case R.id.rl_brandapply:
			Intent intent_brandapply = new Intent(getActivity(), Activity_BrandApply.class);
			startActivity(intent_brandapply);
			break;
		case R.id.rl_customapply:
			Intent intent_customapply = new Intent(getActivity(), Activity_CustomApply.class);
			startActivity(intent_customapply);
			break;
		case R.id.rl_mycontacts_d:
			Intent intent_mycontacts = new Intent(getActivity(), Activity_Contacts.class);
			startActivity(intent_mycontacts);
			break;
		case R.id.rl_consult:
			Intent intent_consult = new Intent(getActivity(), Activity_ConsultCen.class);
			startActivity(intent_consult);
			break;
		case R.id.tv_entrymain:
			Intent intent_entrymain = new Intent(getActivity(), Activity_Designer.class);
			intent_entrymain.putExtra("userId", userId);

			startActivity(intent_entrymain);
			break;
		case R.id.im_topersondata1:
			Intent intent_topersondata = new Intent(getActivity(), Activity_PersonData.class);
			startActivityForResult(intent_topersondata, 1);
			break;
		case R.id.rl_toper:
			Intent intent_topersondata1 = new Intent(getActivity(), Activity_PersonData.class);
			startActivityForResult(intent_topersondata1, 1);
			break;
		case R.id.rl_messagemede:

			Intent intent_message = new Intent(getActivity(), Activity_Message.class);
			// allCount, aMeCount, discussCount, topCount1
			intent_message.putExtra("allCount", allCount);
			intent_message.putExtra("aMeCount", aMeCount);
			intent_message.putExtra("discussCount", discussCount);
			intent_message.putExtra("topCount", topCount1);
			startActivity(intent_message);
			break;
		case R.id.rl_shopcarde:
			Intent intent_shopcar = new Intent(getActivity(), Activity_ShoppingCar.class);

			startActivity(intent_shopcar);
			break;
		case R.id.tv_exitde:
			showExit();
			break;
		case R.id.rl_share:
			if (userImg != null) {
				savePic(userImg);
			} else {
				showShare();
			}

			break;
		case R.id.rl_updata:
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
		case R.id.rl_activity:
			Intent intent_activity = new Intent(getActivity(), Activity_List.class);
			intent_activity.putExtra("type", 1);
			startActivity(intent_activity);
			break;
		case R.id.rl_sendsampls:
			Intent intent_send = new Intent(getActivity(), Activity_SendSample.class);
			startActivity(intent_send);
			break;
		case R.id.rl_changepd:
			Intent intent_cpd = new Intent(getActivity(), Activity_ChangePD.class);
			startActivity(intent_cpd);
			break;
		case R.id.ll_myfans:
			Intent intent_myfans = new Intent(getActivity(), Activity_FansNub.class);
			intent_myfans.putExtra("userId", userId);
			startActivity(intent_myfans);
			break;
		case R.id.ll_focus_me:
			Intent intent_myfocus = new Intent(getActivity(), Activity_FocusMe.class);
			intent_myfocus.putExtra("userId", userId);
			startActivity(intent_myfocus);
			break;
		case R.id.tv_tomycoupon:
			Intent Intent = new Intent(getActivity(), Activity_MyCoupon.class);
			Intent.putExtra("type", 1);
			startActivityForResult(Intent, 208);
			break;
		case R.id.rl_myupcustom:
			if (MyApplication.islogin) {
				Intent intent_ = new Intent(getActivity(), Activity_MyCustom.class);
				startActivity(intent_);
			} else {
				Intent intent_ = new Intent(getActivity(), Activity_Login.class);
				startActivity(intent_);
			}
			break;
		default:
			break;
		}

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
				logout();
				dialog.dismiss();
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).show();
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
					installApk(file);
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

	private String getVersionName() throws Exception {
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageManager packageManager = getActivity().getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
		return packInfo.versionName;
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

	private void setStart() {
		ll_contain_trust.removeAllViews();
		if (truststar == 0) {
			truststar = 1;
		}
		for (int i = 0; i < truststar; i++) {
			View v = View.inflate(getActivity(), R.layout.item_star, null);
			ImageView im = (ImageView) v.findViewById(R.id.im_star);
			im.setPadding(16, 0, 16, 0);
			im.setImageResource(R.drawable.newstar_orange);
			ll_contain_trust.addView(v);
		}
	}

	// public void onResume() {
	// super.onResume();
	// MobclickAgent.onPageStart("我"); // 统计页面，"MainScreen"为页面名称，可自定义
	// }
	//
	// public void onPause() {
	// super.onPause();
	// MobclickAgent.onPageEnd("我");
	// }
}
