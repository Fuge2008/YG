package com.femto.ugershop.fragment;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.bumptech.glide.Glide;
import com.femto.ugershop.R;
import com.femto.ugershop.activity.ActivityAddTags;
import com.femto.ugershop.activity.Activity_ActivityMain;
import com.femto.ugershop.activity.Activity_CustomGoodsDetails;
import com.femto.ugershop.activity.Activity_Login;
import com.femto.ugershop.activity.Activity_MyCollectCustomGoods;
import com.femto.ugershop.activity.Activity_MyCustom;
import com.femto.ugershop.activity.Activity_Search_Custom;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.entity.Flags;
import com.femto.ugershop.entity.PhotoList;
import com.femto.ugershop.fragment.Fragment_NewGoods.BannerList;
import com.femto.ugershop.fragment.Fragment_NewGoods.PPListAdapter;
import com.femto.ugershop.fragment.Fragment_SecondCustonize.MakeList;
import com.femto.ugershop.fragment.Fragment_SecondCustonize.MyHolder;
import com.femto.ugershop.interfac.MyInterface;
import com.femto.ugershop.interfac.MyInterface.OnWindowsListener;
import com.femto.ugershop.selepic.ImgFileListActivity;
import com.femto.ugershop.tabpage.TabPageIndicator;
import com.femto.ugershop.tabpage.TabPageIndicator.OnPageCurrentPosition;
import com.femto.ugershop.view.ChildViewPager;
import com.femto.ugershop.view.MyGridView;
import com.femto.ugershop.view.NewMyScrollView;
import com.femto.ugershop.view.NewMyScrollView.OnScrollListener;
import com.femto.ugershop.view.NewMyScrollView.ScrollBottomListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Fragment_NewCustomize extends BaseFragment implements OnPageChangeListener, OnClickListener,
		OnRefreshListener2<ScrollView>, OnItemClickListener {
	private View view;
	private RelativeLayout rl_change, rl_cole_custon, rl_up_mgc;
	private List<MYSort> ss;
	private List<MYSort> stype;
	private List<MYSort> sprice;
	private MyVpFragmentAdapter vpadapter;
	private List<Fragment> fs;
	private ViewPager vp_second_pic;
	private LinearLayout dots_group_second;
	private MyVPAdapter vpadapter_pic;
	private ImageView im_change;
	private EditText ed_search_custom;
	private MyGridView gv_mgc;
	private PullToRefreshScrollView sv_mgc;
	private MyAdapter adapter;
	private List<MakeList> ms;
	private int size = 0;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private List<String> postm;
	private String sceneId = "", stypes = "", price = "";
	private TextView tv_scene, tv_type, tv_price_mgc;
	private RelativeLayout rl_topcustomize, rl_sreach_nc, rl_show1;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (pics.size() != 0) {
					int j = i % pics.size();
					vp_second_pic.setCurrentItem(j);
					i++;
				}
				break;
			case 2:
				sv_mgc.onRefreshComplete();
				break;

			default:
				break;
			}

		};
	};
	private RadioButton rb_boy, rb_girl;
	private TextView tv_boy, tv_girl;
	private List<BannerList> pics;
	private int sWidth;

	class BannerList {
		String url, info;
		List<PhotoList> photoList;

		public BannerList(String url, String info, List<PhotoList> photoList) {
			super();
			this.url = url;
			this.info = info;
			this.photoList = photoList;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_newcustomize, container, false);
		initParams();
		regisMBC();

		initView(view);
		initCon();
		return view;
	}

	// 初始化参数
	private void initParams() {
		int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		sWidth = (int) ((screenWidth - dp2px(getActivity(), 30)) / 2.0);
	}

	@SuppressWarnings("static-access")
	private void initView(View v) {
		// TODO Auto-generated method stub
		rb_boy = (RadioButton) v.findViewById(R.id.rb_boy);
		rb_girl = (RadioButton) v.findViewById(R.id.rb_girl);
		tv_boy = (TextView) v.findViewById(R.id.tv_boy);
		tv_girl = (TextView) v.findViewById(R.id.tv_girl);
		rl_cole_custon = (RelativeLayout) v.findViewById(R.id.rl_cole_custon);
		rl_change = (RelativeLayout) v.findViewById(R.id.rl_change);
		rl_up_mgc = (RelativeLayout) v.findViewById(R.id.rl_up_mgc);
		ed_search_custom = (EditText) v.findViewById(R.id.ed_search_custom);
		rl_sreach_nc = (RelativeLayout) v.findViewById(R.id.rl_sreach_nc);
		im_change = (ImageView) v.findViewById(R.id.im_change);
		rl_topcustomize = (RelativeLayout) v.findViewById(R.id.rl_topcustomize);
		rl_show1 = (RelativeLayout) v.findViewById(R.id.rl_show1);
		dots_group_second = (LinearLayout) v.findViewById(R.id.dots_group_second);
		vp_second_pic = (ViewPager) v.findViewById(R.id.vp_second_pic);
		sv_mgc = (PullToRefreshScrollView) v.findViewById(R.id.sv_mgc);
		gv_mgc = (MyGridView) v.findViewById(R.id.gv_mgc);
		// tv_scene,tv_type,tv_price_mgc
		tv_scene = (TextView) v.findViewById(R.id.tv_scene);
		tv_type = (TextView) v.findViewById(R.id.tv_type);
		tv_price_mgc = (TextView) v.findViewById(R.id.tv_price_mgc);
	}

	private int type = 1;
	private int w;
	private int currposition = 0;
	private int screenWidth;

	private void initCon() {
		// TODO Auto-generated method stub
		ms = new ArrayList<MakeList>();
		ss = new ArrayList<MYSort>();
		stype = new ArrayList<MYSort>();
		sprice = new ArrayList<MYSort>();
		fs = new ArrayList<Fragment>();
		pics = new ArrayList<BannerList>();
		postm = new ArrayList<String>();
		vp_second_pic.setOnPageChangeListener(this);
		rl_change.setOnClickListener(this);
		sv_mgc.setOnRefreshListener(this);
		sv_mgc.setMode(Mode.BOTH);
		rl_up_mgc.setOnClickListener(this);
		gv_mgc.setOnItemClickListener(this);
		rl_sreach_nc.setOnClickListener(this);
		// rb_boy, rb_girl;
		ed_search_custom.setOnClickListener(this);
		rb_boy.setOnClickListener(this);
		rb_girl.setOnClickListener(this);
		rl_cole_custon.setOnClickListener(this);
		tv_scene.setOnClickListener(this);
		tv_type.setOnClickListener(this);
		tv_price_mgc.setOnClickListener(this);
		// tv_scene,tv_type,tv_price_mgc
		screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		int dp2px = dp2px(getActivity(), 0);
		int dp2pxH = dp2px(getActivity(), 180);
		w = (screenWidth - dp2px) / 2;

		android.view.ViewGroup.LayoutParams params = rl_show1.getLayoutParams();
		params.height = ((int) (screenWidth * 5 / (double) 14));
		rl_show1.setLayoutParams(params);
		adapter = new MyAdapter();
		gv_mgc.setAdapter(adapter);
		sv_mgc.requestFocus();
		showProgressDialog("加载中...");
		getSort(type);

	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden) {
			sv_mgc.requestFocus();
		}
	}

	// 注册广播
	private void regisMBC() {
		mbc = new MBC();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.femto.hasfocus");
		filter.addAction("com.select.pic");
		filter.addAction("com.femto.single");
		getActivity().registerReceiver(mbc, filter);
	}

	private MBC mbc;
	private int i;
	private String filepath;
	private int csortId;

	// 广播接收
	class MBC extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals("com.femto.hasfocus")) {

			} else if (action.equals("com.select.pic") && MyApplication.pictype == 1) {
				ArrayList<String> f = intent.getStringArrayListExtra("files");
				for (int i = 0; i < f.size(); i++) {
					filepath = f.get(i);
					Intent intent_ = new Intent(getActivity(), ActivityAddTags.class);
					intent_.putExtra("image_path", filepath);
					startActivity(intent_);
				}

			} else if (action.equals("com.femto.single")) {
				if (MyApplication.issingle) {
					gv_mgc.setNumColumns(1);
					adapter.notifyDataSetChanged();
				} else {
					gv_mgc.setNumColumns(2);
					adapter.notifyDataSetChanged();
				}
			}
		}
	}

	// 获取分类
	private void getSort(int type) {
		RequestParams params = new RequestParams();
		params.put("sex", type);
		MyApplication.ahc.post(AppFinalUrl.usergetMakeSort, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				JSONArray optJSONArray = response.optJSONArray("list");
				JSONArray optJSONArray2 = response.optJSONArray("sortList");
				JSONArray optJSONArray3 = response.optJSONArray("priceList");
				fs.clear();
				ss.clear();
				for (int i = 0; i < optJSONArray.length(); i++) {
					JSONObject j = optJSONArray.optJSONObject(i);
					String sortName = j.optString("sortName");
					int sortId = j.optInt("sortId");
					ss.add(new MYSort(sortName, sortId));
				}
				for (int i = 0; i < optJSONArray2.length(); i++) {
					JSONObject j = optJSONArray2.optJSONObject(i);
					String sortName = j.optString("str");
					int sortId = j.optInt("id");
					stype.add(new MYSort(sortName, sortId));
				}
				for (int i = 0; i < optJSONArray3.length(); i++) {
					JSONObject j = optJSONArray3.optJSONObject(i);
					String sortName = j.optString("str");
					int sortId = j.optInt("str");
					sprice.add(new MYSort(sortName, sortId));
				}

				// banner图
				JSONArray jsonArray = response.optJSONArray("bannerList");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject j = jsonArray.optJSONObject(i);
					String url = j.optString("url");
					String info = j.optString("info");
					JSONArray optJSONArray1 = j.optJSONArray("PhotoList");
					List<PhotoList> photoList = new ArrayList<PhotoList>();
					for (int k = 0; k < optJSONArray1.length(); k++) {
						JSONObject jj = optJSONArray1.optJSONObject(k);
						String photoUrl = jj.optString("photoUrl");
						String high = jj.optString("high");
						String width = jj.optString("width");
						photoList.add(new PhotoList(photoUrl, high, width));
					}
					pics.add(new BannerList(url, info, photoList));
				}

				vpadapter_pic = new MyVPAdapter();
				vp_second_pic.setAdapter(vpadapter_pic);
				initSmallDot(0);
				LunBo();
				if (ss.size() != 0) {
					sceneId = "";
					getData(pageIndex, pageSize, sceneId, stypes, price);
				}

			}
		});
	}

	class MyVpFragmentAdapter extends FragmentPagerAdapter {
		private List<Fragment> fragments;
		private List<MYSort> sss;
		FragmentManager fm;

		public MyVpFragmentAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		public MyVpFragmentAdapter(FragmentManager fm, List<Fragment> fragments, List<MYSort> sss) {
			super(fm);
			this.fragments = fragments;
			this.sss = sss;
			this.fm = fm;
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return sss.get(position).sortName;
		}

	}

	class MYSort {
		String sortName;
		int sortId;

		public MYSort(String sortName, int sortId) {
			super();
			this.sortName = sortName;
			this.sortId = sortId;
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	// 切换小圆点
	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		initSmallDot(arg0);
		currposition = arg0;
	}

	private Timer timer = new Timer();

	// 广告图轮播
	public void LunBo() {

		i = 0;
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(1);
			}
		}, 3000, 3000);
	}

	// 设置小圆点
	private void initSmallDot(int index) {
		if (getActivity() != null) {
			dots_group_second.removeAllViews();
			for (int i = 0; i < pics.size(); i++) {
				ImageView imageView = new ImageView(getActivity());
				imageView.setImageResource(R.drawable.round);
				imageView.setPadding(5, 0, 5, 0);
				dots_group_second.addView(imageView);
			}
			if (pics.size() != 0) {
				// 设置选中项
				((ImageView) dots_group_second.getChildAt(index)).setImageResource(R.drawable.round_select);
			}

		}

	}

	class MyVPAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pics.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView photoView = new ImageView(getActivity());
			photoView.setScaleType(ScaleType.CENTER_CROP);
			container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			Glide.with(getActivity()).load(AppFinalUrl.photoBaseUri + pics.get(position).url).centerCrop()

			/*
			 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			 */
			.crossFade().into(photoView);
			LayoutParams layoutParams = (LayoutParams) photoView.getLayoutParams();
			layoutParams.height = (int) (screenWidth * 5 / (double) 14);
			layoutParams.width = screenWidth;
			photoView.setLayoutParams(layoutParams);
			photoView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(), Activity_ActivityMain.class);
					intent.putExtra("info", pics.get(currposition).info);
					intent.putExtra("photolist", (Serializable) pics.get(currposition).photoList);
					startActivity(intent);
				}
			});
			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_change:
			if (MyApplication.issingle) {
				MyApplication.issingle = false;
				im_change.setImageResource(R.drawable.doublecom);
				// Intent intent = new Intent();
				// intent.setAction("com.femto.single");
				// getActivity().sendBroadcast(intent);
				gv_mgc.setNumColumns(2);
				adapter.notifyDataSetChanged();
			} else {
				MyApplication.issingle = true;
				im_change.setImageResource(R.drawable.single);
				// Intent intent = new Intent();
				// intent.setAction("com.femto.single");
				// getActivity().sendBroadcast(intent);
				gv_mgc.setNumColumns(1);
				adapter.notifyDataSetChanged();
			}
			break;
		case R.id.rl_up_mgc:
			sv_mgc.getRefreshableView().smoothScrollTo(0, 0);
			break;
		case R.id.rl_sreach_nc:
			// if (MyApplication.islogin) {
			// Intent intent_ = new Intent(getActivity(),
			// Activity_MyCustom.class);
			// startActivity(intent_);
			// } else {
			// Intent intent = new Intent(getActivity(), Activity_Login.class);
			// startActivity(intent);
			// }
			Intent intent = new Intent(getActivity(), Activity_Search_Custom.class);
			startActivity(intent);
			break;
		case R.id.ed_search_custom:

			break;
		case R.id.rl_cole_custon:
			Intent intent_co = new Intent(getActivity(), Activity_MyCollectCustomGoods.class);
			startActivity(intent_co);
			break;
		case R.id.rb_boy:
			tv_boy.setVisibility(View.VISIBLE);
			tv_girl.setVisibility(View.INVISIBLE);
			type = 2;
			getSort(type);
			break;
		case R.id.rb_girl:
			tv_boy.setVisibility(View.INVISIBLE);
			tv_girl.setVisibility(View.VISIBLE);
			type = 1;
			getSort(type);
			break;
		// tv_scene,tv_type,tv_price_mgc
		case R.id.tv_scene:
			if (ppwsort != null && ppwsort.isShowing()) {
				// ppw_price.setFocusable(false);
				ppwsort.dismiss();
			} else {
				initPPwSort(1);
				ppwsort.showAsDropDown(v, 0, 1);
			}
			break;
		case R.id.tv_type:
			if (ppwsort != null && ppwsort.isShowing()) {
				// ppw_price.setFocusable(false);
				ppwsort.dismiss();
			} else {
				initPPwSort(2);
				ppwsort.showAsDropDown(v, 0, 1);
			}
			break;
		case R.id.tv_price_mgc:
			if (ppwsort != null && ppwsort.isShowing()) {
				// ppw_price.setFocusable(false);
				ppwsort.dismiss();
			} else {
				initPPwSort(3);
				ppwsort.showAsDropDown(v, 0, 1);
			}
			break;
		default:
			break;
		}
	}

	private String[] items = new String[] { "图库", "拍照" };

	private void showSettingFaceDialog() {

		new AlertDialog.Builder(getActivity()).setTitle("图片来源").setCancelable(true)
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:// Local Image
							MyApplication.pictype = 1;
							MyApplication.flags.clear();
							MyApplication.flags.add(new Flags("", null));
							Intent intent = new Intent();
							intent.putExtra("nub", 1);
							intent.setClass(getActivity(), ImgFileListActivity.class);
							startActivity(intent);
							break;
						case 1:// Take Picture
							photo();
							break;
						}
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	// 拍照
	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		IMAGE_FILE = getPhotoFileName();
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE)));
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	private static final int TAKE_PICTURE = 0x000001;
	private String IMAGE_FILE;
	private View viewppwsort;
	private PopupWindow ppwsort;

	/**
	 * 用当前时间给取得的图片命名
	 * 
	 */
	public String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

		return dateFormat.format(date) + ".jpg";
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case TAKE_PICTURE:

			if (resultCode == getActivity().RESULT_OK) {
				File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE);
				IMAGE_FILE = getPath(getActivity(), Uri.fromFile(tempFile));
				filepath = IMAGE_FILE;
				Intent intent_ = new Intent(getActivity(), ActivityAddTags.class);
				intent_.putExtra("image_path", filepath);
				startActivity(intent_);
			}

			break;
		}
	}

	// 以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		// TODO Auto-generated method stub
		ms.clear();
		pageIndex = 1;
		getData(pageIndex, pageSize, sceneId, stypes, price);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		// TODO Auto-generated method stub
		if (isend) {
			handler.sendEmptyMessage(2);
			Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
		} else {
			getData(pageIndex, pageSize, sceneId, stypes, price);
		}
	}

	// 获取数据
	private void getData(int pi, int ps, String sceneId, String stype, String price) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("userProduct.makeSort.id", sceneId);
		params.put("userProduct.label", price);
		params.put("userProduct.makeCategory.id", stype);
		params.put("pageModel.pageIndex", pi);
		params.put("pageModel.pageSize", ps);
		params.put("userProduct.info", type);
		System.out.println("zuoparams==" + params.toString());
		MyApplication.ahc.post(AppFinalUrl.usergetMakeShow, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				System.out.println("zuo====获取到的===" + response.toString());
				JSONArray optJSONArray = response.optJSONArray("list");
				sv_mgc.onRefreshComplete();
				if (optJSONArray != null) {
					for (int i = 0; i < optJSONArray.length(); i++) {
						JSONObject j = optJSONArray.optJSONObject(i);
						String name = j.optString("name");
						String userName = j.optString("userName");
						String smallUrl = j.optString("smallUrl");
						String productname = j.optString("productname");
						String productpirce = j.optString("productpirce");
						String url = j.optString("url");
						int makeProductId = j.optInt("makeProductId");
						int userId = j.optInt("userId");
						int width = j.optInt("width");
						int high = j.optInt("high");
						ms.add(new MakeList(name, userName, smallUrl, url, makeProductId, userId, width, high, productname,
								productpirce));

						size++;
					}
					if (size == 10) {
						isend = false;
						pageIndex++;
					} else {
						isend = true;
					}

					adapter.notifyDataSetChanged();
					size = 0;

				}
			}
		});
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ms == null ? 0 : ms.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return ms.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			MyHolder h;
			if (v == null) {
				h = new MyHolder();
				v = View.inflate(getActivity(), R.layout.item_nsc, null);
				h.im_cgd = (ImageView) v.findViewById(R.id.im_cgd);
				h.tv_cgdname = (TextView) v.findViewById(R.id.tv_cgdname);
				h.tv_cgdprice = (TextView) v.findViewById(R.id.tv_cgdprice);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			// 判断是否是单列显示
			if (MyApplication.issingle) {
				LayoutParams params = h.im_cgd.getLayoutParams();
				params.height = (int) (sWidth * 1.5) * 2;
				h.im_cgd.setLayoutParams(params);
			} else {
				LayoutParams params = h.im_cgd.getLayoutParams();
				params.height = (int) (sWidth * 1.5);
				// params.width = sWidth;
				h.im_cgd.setLayoutParams(params);
			}
			Glide.with(getActivity()).load(ms.get(position).url + "-middle")

			/*
			 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			 */
			.crossFade().into(h.im_cgd);
			h.tv_cgdname.setText("" + ms.get(position).productname);
			h.tv_cgdprice.setText("¥ " + ms.get(position).productpirce);
			return v;
		}
	}

	class MyHolder {
		ImageView im_cgd;
		TextView tv_cgdname, tv_cgdprice;
	}

	class MakeList {
		String name, userName, smallUrl, url, productname, productpirce;
		int makeProductId, userId, width, high;

		public MakeList(String name, String userName, String smallUrl, String url, int makeProductId, int userId, int width,
				int high, String productname, String productpirce) {
			super();
			this.name = name;
			this.userName = userName;
			this.smallUrl = smallUrl;
			this.url = url;
			this.makeProductId = makeProductId;
			this.userId = userId;
			this.width = width;
			this.high = high;
			this.productname = productname;
			this.productpirce = productpirce;
		}
	}

	private void initPPwSort(final int flag) {
		viewppwsort = View.inflate(getActivity(), R.layout.pp_mgc, null);
		ppwsort = new PopupWindow(viewppwsort, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, false);
		ppwsort.setFocusable(true);
		ppwsort.setBackgroundDrawable(new BitmapDrawable());
		ppwsort.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		GridView gv_ppw = (GridView) viewppwsort.findViewById(R.id.gv_ppmgc);
		LayoutParams params = gv_ppw.getLayoutParams();
		params.width = screenWidth;
		// params.height = (int) (screenWidth / 1.5);
		gv_ppw.setLayoutParams(params);
		gv_ppw.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				ppwsort.dismiss();
				pageIndex = 1;
				ms.clear();
				showProgressDialog("加载中...");
				if (flag == 1) {
					sceneId = "" + ss.get(position).sortId;
					stypes = "";
					price = "";
					getData(pageIndex, pageSize, sceneId, stypes, price);
				} else if (flag == 2) {
					sceneId = "";
					stypes = "" + stype.get(position).sortId;
					price = "";
					getData(pageIndex, pageSize, sceneId, stypes, price);
				} else {
					sceneId = "";
					stypes = "";
					price = "" + sprice.get(position).sortName;
					getData(pageIndex, pageSize, sceneId, stypes, price);
				}

			}
		});
		if (flag == 1) {
			pplistadapter = new PPListAdapter(ss);
		} else if (flag == 2) {
			pplistadapter = new PPListAdapter(stype);
		} else {
			pplistadapter = new PPListAdapter(sprice);
		}

		gv_ppw.setAdapter(pplistadapter);

	}

	private PPListAdapter pplistadapter;

	class PPListAdapter extends BaseAdapter {
		List<MYSort> ss;

		public PPListAdapter(List<MYSort> ss) {
			super();
			this.ss = ss;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ss == null ? 0 : ss.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return ss.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			v = View.inflate(getActivity(), R.layout.item_lv_popu, null);
			TextView tv_sort_name = (TextView) v.findViewById(R.id.tv_sort_name);
			tv_sort_name.setText("" + ss.get(position).sortName);
			return v;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		if (MyApplication.islogin) {
			Intent intent = new Intent(getActivity(), Activity_CustomGoodsDetails.class);
			intent.putExtra("makeProductId", ms.get(position).makeProductId);
			startActivity(intent);
		} else {
			Intent intent = new Intent(getActivity(), Activity_Login.class);
			startActivity(intent);
		}
	}
}
