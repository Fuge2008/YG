package com.femto.ugershop.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.bumptech.glide.Glide;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_CustomMain;
import com.femto.ugershop.activity.Activity_Designer;
import com.femto.ugershop.activity.Activity_LookPic;
import com.femto.ugershop.activity.Activity_PostDetails;
import com.femto.ugershop.activity.Activity_UpLoadPost;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.view.CircleImageView;
import com.femto.ugershop.view.MyGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class Fragment_Community extends BaseFragment implements OnClickListener {
	private View view;

	private RadioButton rb_boutique, rb_recommend, rb_fricir, rb_headline;
	private RelativeLayout rl_addpost;
	private int userId;
	private DisplayImageOptions options;
	private ProgressDialog pdd;
	private int flag = 2;
	private String cmsg;
	private int w;
	private int type = 2;
	private int ffff = 1;
	private String url;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private boolean isinitFragment = false;
	int size = 0;
	// int friendId, int type, final int position
	private int cfid, ctype, cp;
	private Fragment_FriendCircle ffc;
	private Fragment_YoogeRecommend fyr;
	private Fragment_Boutique fbq;

	private Fragment_Headline ftt;

	private LinearLayout ll_topTitle;
	private LinearLayout ll_found;
	private LinearLayout ll_attention;
	private ImageView iv_addDoucument;
	private TextView tv_1;
	private TextView tv_2;
	private TextView tv_3;

	private String videopath = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:

				break;
			case 2:
				break;

			default:
				break;
			}

		};
	};
	private File out;
	private FragmentTransaction transaction;
	private RelativeLayout rl_addDoucument;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_community, container, false);
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();

		out = new File(videopath);
		if (!out.exists()) {
			out.mkdirs();
		}
		videopath = out.getPath();
		int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
		int dp2px = dp2px(getActivity(), 36);
		w = (screenWidth - dp2px) / 3;
		pdd = new ProgressDialog(getActivity());
		pdd.setMessage("加载中");
		initParams();
		initView(view);

		return view;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		System.out.println("zuo社区==" + hidden);
		if (!hidden && !isinitFragment) {
			initFragment();
			isinitFragment = true;
			MobclickAgent.onPageStart("社区");
		} else {
			MobclickAgent.onPageEnd("社区");
		}
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	private void initParams() {
		SharedPreferences sp = getActivity().getSharedPreferences("Login", getActivity().MODE_PRIVATE);
		userId = sp.getInt("userId", -1);

	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.rb_boutique:
			// showProgressDialog("加载中...");
			// listItem.clear();
			// url = AppFinalUrl.usergetFriendIsGood;
			// pageIndex = 1;
			// getFCdata(url, pageIndex, pageSize);
			// flag = 3;
			// type = 1;
			// ffff = 3;
			// showorhint(ll_boutique, ll_fricir, ll_recommend);
			fragmentShowOrHide(fbq, fyr, ffc, ftt, false);
			break;
		case R.id.rb_recommend:
			// showProgressDialog("加载中...");
			// listItem.clear();
			// url = AppFinalUrl.usergetPush;
			// pageIndex = 1;
			// getFCdata(url, pageIndex, pageSize);
			// type = 4;
			// flag = 4;
			// ffff = 2;
			// showorhint(ll_recommend, ll_boutique, ll_fricir);
			fragmentShowOrHide(fyr, ffc, fbq, ftt, false);
			break;
		case R.id.rb_fricir:
			// showProgressDialog("加载中...");
			// listItem.clear();
			// url = AppFinalUrl.usergetFriendCircles;
			// pageIndex = 1;
			// getFCdata(url, pageIndex, pageSize);
			// flag = 2;
			// type = 2;
			// ffff = 1;
			// showorhint(ll_fricir, ll_boutique, ll_recommend);
			fragmentShowOrHide(ffc, fyr, fbq, ftt, false);
			break;
		case R.id.rb_headline:
			fragmentShowOrHide(ftt, ffc, fyr, fbq, false);
			break;
		case R.id.rl_addpost1:
			intent = new Intent(getActivity(), Activity_UpLoadPost.class);
			startActivityForResult(intent, 11);
			break;

		case R.id.ll_topTitle:
			tv_1.setVisibility(View.VISIBLE);
			tv_2.setVisibility(View.INVISIBLE);
			tv_3.setVisibility(View.INVISIBLE);
			fragmentShowOrHide(ftt, ffc, fyr, fbq, false);
			break;
		case R.id.ll_found:
			tv_1.setVisibility(View.INVISIBLE);
			tv_2.setVisibility(View.VISIBLE);
			tv_3.setVisibility(View.INVISIBLE);
			fragmentShowOrHide(fyr, ffc, fbq, ftt, false);
			break;
		case R.id.ll_attention:
			tv_1.setVisibility(View.INVISIBLE);
			tv_2.setVisibility(View.INVISIBLE);
			tv_3.setVisibility(View.VISIBLE);
			fragmentShowOrHide(ffc, fyr, fbq, ftt, false);
			break;
		case R.id.rl_addDoucument:
			intent = new Intent(getActivity(), Activity_UpLoadPost.class);
			startActivityForResult(intent, 11);
			break;
		default:
			break;
		}

	}

	private void initView(View v) {
		// TODO Auto-generated method stub
		rl_addpost = (RelativeLayout) v.findViewById(R.id.rl_addpost1);
		rl_addpost.setOnClickListener(this);
		rb_boutique = (RadioButton) v.findViewById(R.id.rb_boutique);
		rb_recommend = (RadioButton) v.findViewById(R.id.rb_recommend);
		rb_fricir = (RadioButton) v.findViewById(R.id.rb_fricir);
		rb_headline = (RadioButton) v.findViewById(R.id.rb_headline);
		rl_addDoucument = (RelativeLayout) v.findViewById(R.id.rl_addDoucument);
		rb_boutique.setOnClickListener(this);
		rb_recommend.setOnClickListener(this);
		rb_fricir.setOnClickListener(this);
		rb_headline.setOnClickListener(this);
		rl_addDoucument.setOnClickListener(this);

		ll_topTitle = (LinearLayout) v.findViewById(R.id.ll_topTitle);
		ll_found = (LinearLayout) v.findViewById(R.id.ll_found);
		ll_attention = (LinearLayout) v.findViewById(R.id.ll_attention);
		iv_addDoucument = (ImageView) v.findViewById(R.id.iv_addDoucument);
		tv_1 = (TextView) v.findViewById(R.id.tv_1);
		tv_2 = (TextView) v.findViewById(R.id.tv_2);
		tv_3 = (TextView) v.findViewById(R.id.tv_3);
		ll_topTitle.setOnClickListener(this);
		ll_found.setOnClickListener(this);
		ll_attention.setOnClickListener(this);
		tv_1.setOnClickListener(this);
		tv_2.setOnClickListener(this);
		tv_3.setOnClickListener(this);

	}

	private void initFragment() {
		transaction = getFragmentManager().beginTransaction();
		// transaction = getFragmentManager().beginTransaction();
		ffc = new Fragment_FriendCircle();
		fbq = new Fragment_Boutique();
		fyr = new Fragment_YoogeRecommend();
		ftt = new Fragment_Headline();
		// ftt = new Fragment_Headline();

		transaction.add(R.id.fl_contain_friendcircel, ftt);
		transaction.add(R.id.fl_contain_friendcircel, ffc);
		transaction.add(R.id.fl_contain_friendcircel, fbq);
		transaction.add(R.id.fl_contain_friendcircel, fyr);
		fragmentShowOrHide(ftt, fyr, ffc, fbq, true);
	}

	private void fragmentShowOrHide(Fragment showFragment, Fragment hideFragment1, Fragment hideFragment2,
			Fragment hideFragment3, boolean isInit) {
		if (!isInit) {
			transaction = getFragmentManager().beginTransaction();
		}
		transaction.show(showFragment);
		transaction.hide(hideFragment1);
		transaction.hide(hideFragment2);
		transaction.hide(hideFragment3);
		transaction.commit();
	}

	// public void onResume() {
	// super.onResume();
	// MobclickAgent.onPageStart("社区"); // 统计页面，"MainScreen"为页面名称，可自定义
	// }
	//
	// public void onPause() {
	// super.onPause();
	// MobclickAgent.onPageEnd("社区");
	// }
}
