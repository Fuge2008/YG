package com.femto.ugershop.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.twitter.Twitter;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

public class Activity_Head extends SwipeBackActivity implements OnTouchListener {
	private RelativeLayout rl_back_head, rl_top_head;
	private WebView wb_view;
	private LinearLayout rl_prise_head, rl_share_head;
	private String url;
	private File dir;
	private FileOutputStream fos;
	private TextView tv_topcount_web, tv_share_count;
	private File out;
	private String title, info;
	private ImageView im_istop;

	private int productId;
	private String videopath = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				showShare();
				break;

			default:
				break;
			}
		};
	};
	private String isTop, picurl;
	private int topCount = 0, shareCount = 0;
	private int documentId;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_head:
			finish();
			break;
		case R.id.rl_share_head:
			if (picurl != null && !picurl.equals("")) {
				savePic(picurl);
			} else {
				showShare();
			}

			break;
		case R.id.rl_prise_head_web:
			priseOrCancel();
			break;
		default:
			break;
		}
	}

	// 初始化控件
	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back_head = (RelativeLayout) findViewById(R.id.rl_back_head);
		rl_top_head = (RelativeLayout) findViewById(R.id.rl_top_head);
		rl_share_head = (LinearLayout) findViewById(R.id.rl_share_head);
		rl_prise_head = (LinearLayout) findViewById(R.id.rl_prise_head_web);
		tv_topcount_web = (TextView) findViewById(R.id.tv_topcount_web);
		tv_share_count = (TextView) findViewById(R.id.tv_share_count);
		im_istop = (ImageView) findViewById(R.id.im_istop);
		wb_view = (WebView) findViewById(R.id.wb_view);
		// sc_head.setOnTouchListener(this);

	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	// 初始化监听数据等
	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_head.setOnClickListener(this);
		rl_share_head.setOnClickListener(this);
		rl_prise_head.setOnClickListener(this);
		if (url != null) {
			initWebView(url + "&userid=" + MyApplication.userId);
		}
		if (isTop.equals("1")) {
			im_istop.setImageResource(R.drawable.newpostheartde);
		} else {
			im_istop.setImageResource(R.drawable.newpostheart);
		}
		tv_topcount_web.setText("" + topCount);
		tv_share_count.setText("" + shareCount);
	}

	// 设置webview
	@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
	private void initWebView(String url) {
		// TODO Auto-generated method stub
		// showProgressDialog("加载中");
		wb_view.loadUrl(url);
		// 启用javascript
		wb_view.getSettings().setJavaScriptEnabled(true);
		// 随便找了个带图片的网站
		wb_view.loadUrl(url);
		// 添加js交互接口类，并起别名 imagelistner
		wb_view.addJavascriptInterface(new JavascriptInterface(Activity_Head.this), "imagelistner");
		wb_view.setWebViewClient(new MyWebViewClient());
	}

	// 监听
	@SuppressLint("SetJavaScriptEnabled")
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			return super.shouldOverrideUrlLoading(view, url);
		}

		@SuppressLint("SetJavaScriptEnabled")
		@Override
		public void onPageFinished(WebView view, String url) {

			view.getSettings().setJavaScriptEnabled(true);

			super.onPageFinished(view, url);
			// html加载完成之后，添加监听图片的点击js函数
			System.out.println("zuo加载完成设置监听");
			addImageClickListner();

		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			view.getSettings().setJavaScriptEnabled(true);

			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

			super.onReceivedError(view, errorCode, description, failingUrl);

		}
	}

	// 注入js函数监听
	private void addImageClickListner() {
		// 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，在还是执行的时候调用本地接口传递url过去
		wb_view.loadUrl("javascript:(function(){" + "var objs = document.getElementsByTagName(\"img\"); "
				+ "for(var i=0;i<objs.length;i++)  " + "{" + "    objs[i].onclick=function()  " + "    {  "
				+ "        window.imagelistner.openImage(this.id);  " + "    }  " + "}" + "})()");
	}

	// js通信接口
	public class JavascriptInterface {
		// @JavascriptInterface
		@SuppressWarnings("unused")
		private Context context;

		public JavascriptInterface(Context context) {
			this.context = context;
		}

		@android.webkit.JavascriptInterface
		public void openImage(String productId) {
			System.out.println("zuoproductId==" + productId);
			if (productId != null && !productId.equals("") && !productId.equals("0")) {
				Intent intent = new Intent(Activity_Head.this, Activity_CustomGoodsDetails.class);
				intent.putExtra("makeProductId", Integer.parseInt(productId));
				startActivity(intent);
			}

		}
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_head);
		initParams();
		MyApplication.addActivity(this);
		touchSlop = (int) (ViewConfiguration.get(Activity_Head.this).getScaledTouchSlop() * 0.8);
	}

	// 初始化参数
	private void initParams() {
		// TODO Auto-generated method stub
		url = getIntent().getStringExtra("url");
		isTop = getIntent().getStringExtra("isTop");
		title = getIntent().getStringExtra("title");
		info = getIntent().getStringExtra("info");
		picurl = getIntent().getStringExtra("picurl");
		topCount = getIntent().getIntExtra("topCount", 0);
		shareCount = getIntent().getIntExtra("shareCount", 0);
		documentId = getIntent().getIntExtra("documentId", 0);
		out = new File(videopath);
		if (!out.exists()) {
			out.mkdirs();
		}
		System.out.println("zuohs.get(position).==" + picurl);
		videopath = out.getPath();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("头条详情");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("头条详情");
	}

	// 保存分享的图片
	private void savePic(String urlPath) {
		dir = new File(videopath, "showpic.jpg");
		try {
			fos = new FileOutputStream(dir);
			showProgressDialog("分享中...");
			System.out.println("zuo--urlPath==" + urlPath);
			MyApplication.ahc.setTimeout(10000);
			MyApplication.ahc.post(urlPath + "-small", new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] data) {
					// TODO Auto-generated method stub
					System.out.println("zuo====arg0=" + arg0);
					dismissProgressDialog();
					try {
						fos.write(data);
						fos.flush();
						fos.close();
						handler.sendEmptyMessage(1);
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

	// 显示分享界面
	private void showShare() {
		ShareSDK.initSDK(Activity_Head.this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		if (title != null && !title.equals("")) {
			oks.setTitle("来自优格：" + title);
		} else {
			oks.setTitle("来自优格：");
		}

		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("" + url);
		// text是分享文本，所有平台都需要这个字段

		if (info != null && !info.equals("")) {
			oks.setText("" + info);
		} else {
			oks.setText("优格");
		}
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		if (dir != null) {
			oks.setImagePath(dir.getPath());// 确保SDcard下面存在此张图片
		}
		// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("" + url);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("" + info);
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("" + url);
		oks.addHiddenPlatform(Twitter.NAME);
		oks.addHiddenPlatform(Facebook.NAME);
		oks.setCallback(new PlatformActionListener() {

			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
				// TODO Auto-generated method stub
				shareUp();
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});
		// 启动分享GUI
		oks.show(Activity_Head.this);
	}

	// 点赞或取消
	private void priseOrCancel() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("documentId", documentId);
		params.put("userId", MyApplication.userId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usercancleOrAddTopToDocument, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo--------" + response.toString());
				String result = response.optString("result");
				if (result != null && result.equals("0")) {
					if (isTop.equals("1")) {
						if (topCount > 0) {
							topCount--;
							isTop = "0";
							Toast.makeText(Activity_Head.this, "已取消赞", Toast.LENGTH_SHORT).show();
						}

					} else {
						topCount++;
						isTop = "1";
						Toast.makeText(Activity_Head.this, "点赞成功", Toast.LENGTH_SHORT).show();
					}
					tv_topcount_web.setText("" + topCount);
					if (isTop.equals("1")) {
						im_istop.setImageResource(R.drawable.newpostheartde);
						im_istop.setAnimation(MyApplication.getAni(Activity_Head.this, R.anim.prise_ani));
					} else {
						im_istop.setImageResource(R.drawable.newpostheart);
						im_istop.setAnimation(MyApplication.getAni(Activity_Head.this, R.anim.prise_ani));
					}
				}
			}
		});
	}

	// 增加分享数
	private void shareUp() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("documentId", documentId);
		params.put("userId", MyApplication.userId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.useraddShareDocumentCount, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo--------" + response.toString());
				String result = response.optString("result");
				if (result != null && result.equals("0")) {
					shareCount++;
					tv_share_count.setText("" + shareCount);
				}
			}
		});
	}

	float lastY = 0f;
	float currentY = 0f;
	int touchSlop = 10;
	int lastDirection = 0;
	int currentDirection = 0;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastY = event.getY();
			currentY = event.getY();
			currentDirection = 0;
			lastDirection = 0;
			break;
		case MotionEvent.ACTION_MOVE:

			// new AlphaAnimation(0, 1);
			// new TranslateAnimation(fromXType, fromXValue, toXType, toXValue,
			// fromYType, fromYValue, toYType, toYValue)

			System.out.println("zuo滑动");
			float tmpCurrentY = event.getY();
			if (Math.abs(tmpCurrentY - lastY) > touchSlop) {
				currentY = tmpCurrentY;
				currentDirection = (int) (currentY - lastY);
				if (lastDirection != currentDirection) {
					if (currentDirection < 0) {
						// 隐藏
						rl_top_head.setVisibility(View.GONE);
					} else {
						// 显示
						rl_top_head.setVisibility(View.VISIBLE);
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			currentDirection = 0;
			lastDirection = 0;
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (wb_view != null) {
			wb_view.destroy();
		}

	}
}
