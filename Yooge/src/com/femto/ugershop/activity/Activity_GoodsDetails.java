package com.femto.ugershop.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.twitter.Twitter;

import com.bumptech.glide.Glide;
import com.easemob.chat.EMContactManager;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.chatuidemo.widget.photoview.PhotoView;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.entity.Commends;
import com.femto.ugershop.view.CircleImageView;
import com.femto.ugershop.view.CustomProgressDialog;
import com.femto.ugershop.view.ScrollViewWithListView;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.sample.ImageGridAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_GoodsDetails extends SwipeBackActivity implements OnPageChangeListener {
	private RelativeLayout rl_back_goodsdetails, rl_buy, rl_shopcar, rl_shopcar_details, rl_prise, rv_onecommends;
	private ImageView im_heart_zan, im_souneitu, im_sounue_1, im_totop, im_focus;
	private int w = 88;
	private Drawable drawable;
	private boolean islogin;
	private double price;
	private int isTop = 0;
	private String desinInfo;
	private int userid;
	private int productId;
	private ViewPager vp_picshow, vp_picde, vp_sjsjs_details;// vp_picde设计介绍
	private ArrayList<String> showList;
	private RelativeLayout rl_play, rl_dianzan, rl_focus_goods, rl_play_sjsjs, rl_goods, rl_bottom_goods, rl_sjtg, rl_other;
	private CircleImageView im_dehead, im_headtwo, im_headthree;
	private TextView tv_designername, tv_price, tv_buypeople, tv_creatday, tv_endtime, tv_discusscount, tv_sharecount, tv_p1,
			tv_p2, tv_p3, tv_p4, tv_goodstitle, tv_onepeople, tv_twopeople, tv_threepeople, tv_desintitle, tv_createdata,
			tv_desininfo, tv_topnub, tv_dename, tv_isopen, tv_focusnub, tv_fansnub, tv_rankde, tv_heartnub, tv_scored,
			tv_rankdeee, tv_styte_de, tv_youbjiage, tv_peoplenub, tv_direction, tv_desinfo, tv_soft, tv_texture, tv_nubtoby,
			tv_price1, tv_price2, tv_tomain, tv_morecommends, tv_commendsnub, tv_phonenub, tv_commendscon, tv_commtime,
			tv_newprise;
	private DisplayImageOptions options;
	private int width_one = 88, width_two = 88;
	private int height_one = 88, height_two = 88;
	private String desinUrl;
	private String title;
	private String userName, shuShiDu;
	private List<TopList> toplist;
	private LinearLayout ll_contain_toplist;
	private String url;
	private int type = 2;// 1，视频，2图片
	private ArrayList<DesinList> desinList;
	private ArrayList<DesinPhotoAndVideo> desinPhotoAndVideo;
	private List<ModeList> modelList;
	private List<OtherList> otherList;
	private List<DesinTeacerList> desinTeacerList;
	private int count = 0;
	private RelativeLayout rl_youbiao, rl_fit_goods;
	private int userId;
	private int arrivePeople;
	private double percentage;
	private int wantBuyCount;
	private int isAction;
	private long allTime, useTime;
	private TextView im_clicktomain;
	private LinearLayout ll_tochat, dots_group;
	private ScrollView sv_all;
	private Button btn_rule;
	private PopupWindow ppwRule;
	private ArrayList<String> pics_sjg, pic_modeshow, pic_other;
	private ArrayList<DesinTeacherVideo> desinTeacherVideo;
	DecimalFormat df = new DecimalFormat("######0.00");
	private ArrayList<String> showbiglist = new ArrayList<String>();
	private ArrayList<String> showbiglist_sjsjs = new ArrayList<String>();
	private ScrollViewWithListView ll_xiangao, lv_modeshow, lv_other;
	private int productTopCount;
	private String videopath = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:

				break;
			case 2:
				showShare();
				break;
			case 3:
				if (createVideoThumbnail1 != null) {
					im_sounue_1.setImageBitmap(createVideoThumbnail1);
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) im_sounue_1.getLayoutParams();
					layoutParams.height = w;
					im_sounue_1.setLayoutParams(layoutParams);

				}
				break;
			case 4:
				if (createVideoThumbnail != null) {
					im_souneitu.setImageBitmap(createVideoThumbnail);
				}

				break;
			default:
				break;
			}

		};
	};
	private boolean isprise;
	private int shenfentype;
	private int screenWidth;
	private int screenHeight;
	private View customView;
	private final static float TARGET_HEAP_UTILIZATION = 0.75f;
	private Commends cs;

	// for same activity

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_back_goodsdetails:
			handler.removeMessages(1);
			handler.removeMessages(2);
			handler.removeMessages(3);
			handler.removeMessages(4);
			if (createVideoThumbnail != null) {
				createVideoThumbnail.recycle();
				createVideoThumbnail = null;
			}
			if (createVideoThumbnail1 != null) {
				createVideoThumbnail1.recycle();
				createVideoThumbnail1 = null;
			}

			finish();
			System.gc();
			break;
		case R.id.rl_buy:
			if (islogin) {
				Intent intent = new Intent(Activity_GoodsDetails.this, Activity_Purchase.class);
				intent.putExtra("productId", productId);
				intent.putExtra("title", title);
				intent.putExtra("price", price);
				startActivity(intent);
			} else {
				Intent intent = new Intent(Activity_GoodsDetails.this, Activity_Login.class);
				startActivity(intent);
			}

			break;
		case R.id.rl_shopcar_details:
			if (islogin) {
				Intent intent_shopcar = new Intent(Activity_GoodsDetails.this, Activity_ShoppingCar.class);
				startActivity(intent_shopcar);
			} else {
				Intent intent = new Intent(Activity_GoodsDetails.this, Activity_Login.class);
				startActivity(intent);
			}
			break;
		case R.id.tv_sharecount:
			if (islogin) {
				if (showList.size() != 0) {
					savePic(showList.get(0));
				} else {
					showShare();
				}

			} else {
				Intent intent = new Intent(Activity_GoodsDetails.this, Activity_Login.class);
				startActivity(intent);
			}

			break;
		case R.id.rl_shopcar:
			if (islogin) {
				Intent intent_addshopcar = new Intent(Activity_GoodsDetails.this, Activity_AddtoShopCar.class);
				intent_addshopcar.putExtra("productId", productId);
				startActivity(intent_addshopcar);
			} else {
				Intent intent = new Intent(Activity_GoodsDetails.this, Activity_Login.class);
				startActivity(intent);
			}

			break;
		case R.id.ll_tochat:
			if (MyApplication.islogin) {
				startActivity(new Intent(this, ChatActivity.class).putExtra("name", "优格官方").putExtra("userId", "youge001"));
			} else {
				Intent intent_lo = new Intent(this, Activity_Login.class);
				startActivity(intent_lo);
			}
			break;
		// case R.id.im_desin:
		// Intent intent_video = new Intent(this, Activity_Video.class);
		// startActivity(intent_video);
		// break;
		case R.id.rl_play:
			if (type == 1 && desinPhotoAndVideo.size() != 0) {
				Intent intent_video = new Intent(this, Activity_Video.class);
				intent_video.putExtra("videourl", desinPhotoAndVideo.get(0).url);
				startActivity(intent_video);
			} else {
				Toast.makeText(Activity_GoodsDetails.this, "视频暂时无法播放", Toast.LENGTH_SHORT).show();

			}
			break;
		case R.id.rl_play_sjsjs:
			if (desinTeacherVideo.size() != 0 && desinTeacherVideo.get(0).type == 1 && desinTeacherVideo.get(0).url.length() != 0) {
				Intent intent_video = new Intent(this, Activity_Video.class);
				intent_video.putExtra("videourl", desinTeacherVideo.get(0).url);
				System.out.println("zuourl==" + desinTeacherVideo.get(0).url);
				startActivity(intent_video);
			} else {
				Toast.makeText(Activity_GoodsDetails.this, "视频暂时无法播放", Toast.LENGTH_SHORT).show();

			}
			break;
		case R.id.rl_dianzan:
			if (islogin) {
				upPrise();
			} else {
				Intent intent = new Intent(Activity_GoodsDetails.this, Activity_Login.class);
				startActivity(intent);
			}

			break;
		case R.id.im_heart_zan:
			if (islogin) {
				if (isTop == 1) {
					cansPrise();
				} else {
					upPrise();
				}
				//
			} else {
				Intent intent = new Intent(Activity_GoodsDetails.this, Activity_Login.class);
				startActivity(intent);
			}
			break;
		case R.id.im_focus:
			if (islogin) {
				System.out.println("zuo==isAction=" + isAction);
				if (isAction == 1) {
					cancelPeople();
				} else {
					upFocus();
				}

			} else {
				Intent intent = new Intent(Activity_GoodsDetails.this, Activity_Login.class);
				startActivity(intent);
			}

			break;
		case R.id.im_headthree1:
			intomain();
			break;
		case R.id.im_deheaddd:
			intomain();
			break;
		case R.id.tv_discusscount:
			Intent intent_com = new Intent(Activity_GoodsDetails.this, Activity_Commends_GoodeDetails.class);
			intent_com.putExtra("productId", productId);
			startActivity(intent_com);
			break;
		case R.id.rl_prise:
			if (islogin) {
				if (isTop == 1) {
					cansPrise();
				} else {

					upPrise();
				}
				//
			} else {
				Intent intent = new Intent(Activity_GoodsDetails.this, Activity_Login.class);
				startActivity(intent);
			}

			break;
		case R.id.im_clicktomain:

			intomain();

			break;
		case R.id.tv_tomain:
			intomain();
			break;
		case R.id.im_totop:
			sv_all.smoothScrollTo(0, 0);
			break;
		case R.id.btn_rule:
			if (ppwRule != null && ppwRule.isShowing()) {
				// ppw_price.setFocusable(false);
				ppwRule.dismiss();
			} else {
				initPpwPrice();
				ppwRule.showAtLocation(v, Gravity.CENTER_HORIZONTAL, 1, 1);
			}

			break;
		case R.id.tv_morecommends:
			Intent intent_com_ = new Intent(Activity_GoodsDetails.this, Activity_Commends_GoodeDetails.class);
			intent_com_.putExtra("productId", productId);
			startActivity(intent_com_);
			break;
		case R.id.rl_fit_goods:
			if (islogin) {
				Intent intent = new Intent(Activity_GoodsDetails.this, Activity_Purchase.class);
				intent.putExtra("productId", productId);
				intent.putExtra("title", title);
				intent.putExtra("price", price);
				intent.putExtra("flag", 1);
				startActivity(intent);
			} else {
				Intent intent = new Intent(Activity_GoodsDetails.this, Activity_Login.class);
				startActivity(intent);
			}
			break;
		default:
			break;
		}

	}

	public void initPpwPrice() {
		customView = View.inflate(this, R.layout.popurule, null);
		ppwRule = new PopupWindow(customView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, false);
		TextView tv_iknow = (TextView) customView.findViewById(R.id.tv_iknow);
		TextView tv_rule_con = (TextView) customView.findViewById(R.id.tv_rule_con);
		tv_rule_con
				.setText(" 购买规则（仅用于C2D商城）\n订单说明：C2D商品以5天为订单期，每一期结束后，优格都会为消费者生产设计师的作品.\n生产说明：是由优格进行统一生产，由于每种商品的生产时间不同，每款的生产周期也有所不同。（具体参考商品详情的生产时间）\n退换货说明：七天无理由退换货是指消费者成功交易后，在签收货物后7天内，如因主观原因不愿意完成本次交易，可以提出“7天无理由退换货”的申请。买家退换货的货物不得影响优格的二次销售，换货仅可换尺码，不可换其他款式。退货金额3个工作日内退还支付宝。\n运费说明：优格承担运费。如需退换货，需由消费者承担寄回运费。\n其他说明：价格是由优格和设计师协商后给出，由优格代工生产，保证每件商品的品质。");
		tv_iknow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ppwRule != null && ppwRule.isShowing()) {
					ppwRule.dismiss();
					ppwRule = null;

				}

			}
		});

	}

	@Override
	public void initView() {
		initParams();
		MyApplication.addActivity(this);
		out = new File(videopath);

		if (!out.exists()) {
			out.mkdirs();
		}
		videopath = out.getPath();
		pics_sjg = new ArrayList<String>();
		pic_modeshow = new ArrayList<String>();
		pic_other = new ArrayList<String>();
		// TODO Auto-generated method stub
		rl_back_goodsdetails = (RelativeLayout) findViewById(R.id.rl_back_goodsdetails);
		rl_buy = (RelativeLayout) findViewById(R.id.rl_buy);
		// im_four = (ImageView) findViewById(R.id.im_four);
		im_dehead = (CircleImageView) findViewById(R.id.im_deheaddd);
		im_headtwo = (CircleImageView) findViewById(R.id.im_headtwo);
		im_headthree = (CircleImageView) findViewById(R.id.im_headthree1);
		tv_designername = (TextView) findViewById(R.id.tv_designername);
		tv_price = (TextView) findViewById(R.id.tv_price);
		tv_buypeople = (TextView) findViewById(R.id.tv_buypeople);
		tv_creatday = (TextView) findViewById(R.id.tv_creatday);
		tv_endtime = (TextView) findViewById(R.id.tv_endtime);
		tv_discusscount = (TextView) findViewById(R.id.tv_discusscount);
		tv_sharecount = (TextView) findViewById(R.id.tv_sharecount);
		tv_p1 = (TextView) findViewById(R.id.tv_p1);
		tv_p2 = (TextView) findViewById(R.id.tv_p2);
		tv_p3 = (TextView) findViewById(R.id.tv_p3);
		tv_p4 = (TextView) findViewById(R.id.tv_p4);
		tv_dename = (TextView) findViewById(R.id.tv_dename);
		tv_desintitle = (TextView) findViewById(R.id.tv_desintitle);
		tv_goodstitle = (TextView) findViewById(R.id.tv_goodstitle);
		tv_onepeople = (TextView) findViewById(R.id.tv_onepeople);
		tv_twopeople = (TextView) findViewById(R.id.tv_twopeople);
		tv_threepeople = (TextView) findViewById(R.id.tv_threepeople);
		tv_createdata = (TextView) findViewById(R.id.tv_createdata);
		tv_desininfo = (TextView) findViewById(R.id.tv_desininfo);
		tv_topnub = (TextView) findViewById(R.id.tv_topnub11);
		im_focus = (ImageView) findViewById(R.id.im_focus);
		tv_isopen = (TextView) findViewById(R.id.tv_isopen);
		tv_focusnub = (TextView) findViewById(R.id.tv_focusnub);
		tv_fansnub = (TextView) findViewById(R.id.tv_fansnub);
		tv_rankde = (TextView) findViewById(R.id.tv_rankde);
		tv_scored = (TextView) findViewById(R.id.tv_scored);
		tv_heartnub = (TextView) findViewById(R.id.tv_heartnub);
		tv_rankdeee = (TextView) findViewById(R.id.tv_rankdeee);
		tv_styte_de = (TextView) findViewById(R.id.tv_styte_de);
		tv_direction = (TextView) findViewById(R.id.tv_direction);
		tv_desinfo = (TextView) findViewById(R.id.tv_desinfo);
		tv_morecommends = (TextView) findViewById(R.id.tv_morecommends);
		tv_soft = (TextView) findViewById(R.id.tv_soft);
		tv_commendsnub = (TextView) findViewById(R.id.tv_commendsnub);
		tv_phonenub = (TextView) findViewById(R.id.tv_phonenub);
		tv_commtime = (TextView) findViewById(R.id.tv_commtime);
		tv_commendscon = (TextView) findViewById(R.id.tv_commendscon);
		tv_peoplenub = (TextView) findViewById(R.id.tv_peoplenub);
		tv_price1 = (TextView) findViewById(R.id.tv_price1);
		tv_price2 = (TextView) findViewById(R.id.tv_price2);
		tv_texture = (TextView) findViewById(R.id.tv_texture);
		tv_nubtoby = (TextView) findViewById(R.id.tv_nubtoby);
		tv_youbjiage = (TextView) findViewById(R.id.tv_youbjiage1);
		tv_tomain = (TextView) findViewById(R.id.tv_tomain);
		tv_newprise = (TextView) findViewById(R.id.tv_newprise);
		sv_all = (ScrollView) findViewById(R.id.sv_all);
		rl_shopcar_details = (RelativeLayout) findViewById(R.id.rl_shopcar_details);
		rv_onecommends = (RelativeLayout) findViewById(R.id.rv_onecommends);
		rl_prise = (RelativeLayout) findViewById(R.id.rl_prise);
		rl_shopcar = (RelativeLayout) findViewById(R.id.rl_shopcar);
		rl_other = (RelativeLayout) findViewById(R.id.rl_other1);
		ll_contain_toplist = (LinearLayout) findViewById(R.id.ll_contain_toplist);
		// ll_contain_xiangao = (LinearLayout)
		// findViewById(R.id.ll_contain_xiangao);
		// ll_contain_mode = (LinearLayout) findViewById(R.id.ll_contain_mode);
		// ll_containother = (LinearLayout) findViewById(R.id.ll_containother);
		ll_tochat = (LinearLayout) findViewById(R.id.ll_tochat);
		vp_picshow = (ViewPager) findViewById(R.id.vp_picshow);
		vp_sjsjs_details = (ViewPager) findViewById(R.id.vp_sjsjs_details);
		vp_picde = (ViewPager) findViewById(R.id.vp_picde);
		rl_play = (RelativeLayout) findViewById(R.id.rl_play);
		rl_fit_goods = (RelativeLayout) findViewById(R.id.rl_fit_goods);
		rl_dianzan = (RelativeLayout) findViewById(R.id.rl_dianzan);
		rl_focus_goods = (RelativeLayout) findViewById(R.id.rl_focus_goods);
		im_heart_zan = (ImageView) findViewById(R.id.im_heart_zan);
		im_souneitu = (ImageView) findViewById(R.id.im_souneitu);
		im_sounue_1 = (ImageView) findViewById(R.id.im_sounue_1);
		im_totop = (ImageView) findViewById(R.id.im_totop);
		rl_play_sjsjs = (RelativeLayout) findViewById(R.id.rl_play_sjsjs);
		rl_goods = (RelativeLayout) findViewById(R.id.rl_goods1);
		im_clicktomain = (TextView) findViewById(R.id.im_clicktomain);
		dots_group = (LinearLayout) findViewById(R.id.ll_dots_group);
		rl_bottom_goods = (RelativeLayout) findViewById(R.id.rl_bottom_goods);
		rl_sjtg = (RelativeLayout) findViewById(R.id.rl_sjtg);
		btn_rule = (Button) findViewById(R.id.btn_rule);
		ll_xiangao = (ScrollViewWithListView) findViewById(R.id.ll_xiangao);
		lv_modeshow = (ScrollViewWithListView) findViewById(R.id.lv_modeshow);
		lv_other = (ScrollViewWithListView) findViewById(R.id.lv_other);
		im_clicktomain.setOnClickListener(this);
		rl_play_sjsjs.setOnClickListener(this);
		rl_play.setOnClickListener(this);
		ll_tochat.setOnClickListener(this);
		rl_fit_goods.setOnClickListener(this);
		im_totop.setOnClickListener(this);
		tv_sharecount.setOnClickListener(this);
		rl_dianzan.setOnClickListener(this);
		rl_focus_goods.setOnClickListener(this);
		im_headthree.setOnClickListener(this);
		im_dehead.setOnClickListener(this);
		tv_discusscount.setOnClickListener(this);
		im_heart_zan.setOnClickListener(this);
		rl_prise.setOnClickListener(this);
		tv_tomain.setOnClickListener(this);
		im_focus.setOnClickListener(this);
		tv_morecommends.setOnClickListener(this);
		if (shenfentype == 1) {
			rl_shopcar_details.setVisibility(View.INVISIBLE);
			rl_shopcar.setVisibility(View.INVISIBLE);
			rl_buy.setVisibility(View.INVISIBLE);
			rl_fit_goods.setVisibility(View.INVISIBLE);
		} else {
			rl_shopcar_details.setVisibility(View.VISIBLE);
			rl_shopcar.setVisibility(View.VISIBLE);
			rl_buy.setVisibility(View.VISIBLE);
			rl_fit_goods.setVisibility(View.VISIBLE);
		}
		screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		int dp2px = dp2px(this, 8);
		drawable = getResources().getDrawable(R.drawable.picture1);
		w = screenWidth - dp2px;

		showList = new ArrayList<String>();
		rl_youbiao = (RelativeLayout) findViewById(R.id.rl_youbiao);
		getData();
		getCommends();
	}

	private void cancelPeople() {
		RequestParams params = new RequestParams();
		params.put("friend.user.id", userid);
		params.put("friend.beuser.id", userId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usercancleFocus, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						// addcon();
						// tv_focus_de.setText("关注");
						isAction = 0;
						im_focus.setImageResource(R.drawable.newfocus);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			System.gc();
		}
		return true;
	}

	public Bitmap getVideoThumbnail1(String videoPath, int width, int height, int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		// System.out.println("w" + bitmap.getWidth());
		// System.out.println("h" + bitmap.getHeight());
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	private void cansPrise() {
		RequestParams params = new RequestParams();
		params.put("userId", userid);
		params.put("friendId", productId);
		params.put("type", 1);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usercancleTop, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						isTop = 0;
						Toast.makeText(Activity_GoodsDetails.this, "已取消赞", Toast.LENGTH_SHORT).show();
						im_heart_zan.setImageResource(R.drawable.newtoprise);

						im_heart_zan.setAnimation(MyApplication.getAni(Activity_GoodsDetails.this, R.anim.prise_ani));
						if (productTopCount > 0) {
							productTopCount = productTopCount - 1;
						}
						tv_newprise.setText("" + productTopCount);
					} else if (result.equals("1")) {
						Toast.makeText(Activity_GoodsDetails.this, "已经赞过了", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(Activity_GoodsDetails.this, "点赞失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	// 商品点赞
	private void upPrise() {
		RequestParams params = new RequestParams();
		params.put("userId", userid);
		params.put("productId", productId);
		params.put("type", 1);
		MobclickAgent.onEvent(Activity_GoodsDetails.this, "prise");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.useraddTop, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String result = response.getString("result");
					System.out.println("zuo===" + response.toString());
					if (result.equals("0")) {
						isTop = 1;
						Toast.makeText(Activity_GoodsDetails.this, "点赞成功", Toast.LENGTH_SHORT).show();
						tv_topnub.setText("" + (toplist.size() + 1));
						im_heart_zan.setImageResource(R.drawable.newprised);
						im_heart_zan.setAnimation(MyApplication.getAni(Activity_GoodsDetails.this, R.anim.prise_ani));
						productTopCount = productTopCount + 1;
						tv_newprise.setText("" + productTopCount);
					} else if (result.equals("1")) {
						Toast.makeText(Activity_GoodsDetails.this, "已经赞过了", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(Activity_GoodsDetails.this, "点赞失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.addActivity(this);
		//
		// VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION);

	}

	private void intomain() {

		Intent intent = new Intent(Activity_GoodsDetails.this, Activity_Designer.class);
		intent.putExtra("userId", userId);
		startActivity(intent);
	}

	private void upFocus() {
		RequestParams params = new RequestParams();
		params.put("friend.user.id", userid);
		params.put("friend.beuser.id", userId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.useraddFocus, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo" + response.toString());
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						// addcon();
						isAction = 1;
						im_focus.setImageResource(R.drawable.newfocusde);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void addcon() {
		new Thread(new Runnable() {
			public void run() {

				try {
					// demo写死了个reason，实际应该让用户手动填入
					String s = getResources().getString(R.string.Add_a_friend);
					EMContactManager.getInstance().addContact("" + userId, s);
					Activity_GoodsDetails.this.runOnUiThread(new Runnable() {
						public void run() {

							String s1 = getResources().getString(R.string.send_successful);
							Toast.makeText(Activity_GoodsDetails.this, "关注成功", 1).show();
						}
					});
				} catch (final Exception e) {
					Activity_GoodsDetails.this.runOnUiThread(new Runnable() {
						public void run() {

						}
					});
				}
			}
		}).start();
	}

	private void showShare() {
		ShareSDK.initSDK(Activity_GoodsDetails.this);
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
		oks.setTitleUrl(AppFinalUrl.usergetProductIdReturnId + "?id=" + productId);
		// text是分享文本，所有平台都需要这个字段

		if (desinInfo != null && !desinInfo.equals("")) {
			oks.setText("" + desinInfo);
		} else {
			oks.setText("优格");
		}
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		if (dir != null) {
			oks.setImagePath(dir.getPath());// 确保SDcard下面存在此张图片
		}
		// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(AppFinalUrl.usergetProductIdReturnId + "?id=" + productId);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("" + desinInfo);
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(AppFinalUrl.usergetProductIdReturnId + "?id=" + productId);
		oks.addHiddenPlatform(Twitter.NAME);
		oks.addHiddenPlatform(Facebook.NAME);
		// 启动分享GUI
		oks.show(Activity_GoodsDetails.this);
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

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		islogin = sp.getBoolean("islogin", false);
		userid = sp.getInt("userId", 0);
		shenfentype = sp.getInt("type", -1);
		productId = getIntent().getIntExtra("productId", 0);
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.newlogyooge) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.newlogyooge) // image连接地址为空时
				.showImageOnFail(R.drawable.newlogyooge) // image加载失败
				.cacheInMemory(false) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT).bitmapConfig(Bitmap.Config.ARGB_8888)
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		title = getIntent().getStringExtra("title");
		toplist = new ArrayList<TopList>();
		desinList = new ArrayList<DesinList>();
		modelList = new ArrayList<ModeList>();
		desinTeacerList = new ArrayList<DesinTeacerList>();
		desinTeacherVideo = new ArrayList<DesinTeacherVideo>();
		desinPhotoAndVideo = new ArrayList<DesinPhotoAndVideo>();
		otherList = new ArrayList<OtherList>();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		ImageLoader.getInstance().clearMemoryCache();
		handler.removeMessages(1);
		handler.removeMessages(2);
		handler.removeMessages(3);
		handler.removeMessages(4);
		// setContentView(R.layout.im_de);
		if (createVideoThumbnail != null) {
			createVideoThumbnail.recycle();
		}
		if (createVideoThumbnail1 != null) {
			createVideoThumbnail1.recycle();
		}
		// vp_picde = null;
		// vp_picshow = null;
		// vp_sjsjs_details = null;
		// ll_contain_mode = null;
		// ll_contain_toplist = null;
		// ll_contain_xiangao = null;
		// ll_containother = null;
		// ll_tochat = null;
		// vpde = null;
		// vpshow = null;
		// vpdee = null;
		// v_toplist = null;
		// v_desinlist = null;
		// v_modelist = null;
		// v_otherlist = null;
		// options = null;
		// rl_back_goodsdetails = null;
		// rl_buy = null;
		// rl_shopcar = null;
		// rl_shopcar_details = null;
		// rl_prise = null;
		// ImageView im_heart_zan;
		// drawable = null;
		//
		// desinInfo = null;
		// ;
		//
		// vp_picshow = null;
		// vp_picde = null;
		// vp_sjsjs_details = null;// vp_picde设计介绍
		// showList = null;
		// ;
		// rl_play = null;
		// rl_dianzan = null;
		// rl_focus_goods = null;
		// rl_play_sjsjs = null;
		// im_dehead = null;
		// im_headtwo = null;
		// im_headthree = null;
		// tv_designername = null;
		// tv_price = null;
		// tv_buypeople = null;
		// tv_creatday = null;
		// tv_endtime = null;
		// tv_discusscount = null;
		// tv_sharecount = null;
		// tv_p1 = null;
		// tv_p2 = null;
		// tv_p3 = null;
		// tv_p4 = null;
		// tv_goodstitle = null;
		// tv_onepeople = null;
		// tv_twopeople = null;
		// tv_threepeople = null;
		// tv_desintitle = null;
		// tv_createdata = null;
		// tv_desininfo = null;
		// tv_topnub = null;
		// tv_dename = null;
		//
		// tv_isopen = null;
		// tv_focusnub = null;
		// tv_fansnub = null;
		// tv_rankde = null;
		// tv_heartnub = null;
		// tv_scored = null;
		// tv_rankdeee = null;
		// tv_styte_de = null;
		// tv_youbjiage = null;
		// tv_peoplenub = null;
		// tv_direction = null;
		// tv_desinfo = null;
		// tv_soft = null;
		// tv_texture = null;
		// tv_nubtoby = null;
		// tv_price1 = null;
		// tv_price2 = null;
		// options = null;
		//
		// desinUrl = null;
		// title = null;
		// userName = null;
		// toplist = null;
		// ll_contain_toplist = null;
		// ll_contain_xiangao = null;
		// ll_contain_mode = null;
		// ll_containother = null;
		// url = null;
		//
		// desinList = null;
		// desinPhotoAndVideo = null;
		// modelList = null;
		// otherList = null;
		// desinTeacerList = null;
		//
		// rl_youbiao = null;
		//
		// im_clicktomain = null;
		// ll_tochat = null;
		// desinTeacherVideo = null;
		// showbiglist = null;
		// showbiglist_sjsjs = null;
		System.gc();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		ImageLoader.getInstance().clearMemoryCache();
	}

	private void setYouBiao() {
		double dd = 0.1, d = 0;
		int dp2px = dp2px(this, 32);

		if (allTime != 0) {
			dd = useTime / (double) allTime;
		}
		// tv_price1.setText("" + price);
		// String st1 = df.format((price * percentage));
		// tv_price2.setText("" + st1 + "元");
		// if (arrivePeople != 0) {
		// if ((wantBuyCount > arrivePeople)) {
		// dd = 1;
		// d = (price * percentage);
		//
		// } else {
		// dd = (double) ((double) wantBuyCount / (double) arrivePeople);
		// d = price - ((double) wantBuyCount / (double) arrivePeople) * (price
		// - (price * percentage));
		// }
		//
		// } else {
		//
		// }
		// tv_peoplenub.setText("" + wantBuyCount + "人");
		//
		// String st = df.format(d);
		tv_youbjiage.setText("" + endTime);
		// System.out.println("zuoddd=" + dd);
		if (dd < 0.25) {
			dd = 0.25;
		}
		RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) rl_youbiao.getLayoutParams();
		p.width = (int) ((screenWidth - dp2px) * dd);
		rl_youbiao.setLayoutParams(p);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		rl_back_goodsdetails.setOnClickListener(this);
		rl_buy.setOnClickListener(this);
		rl_shopcar_details.setOnClickListener(this);
		rl_shopcar.setOnClickListener(this);
		btn_rule.setOnClickListener(this);
	}

	@Override
	public void setContentView() {

		setContentView(R.layout.activity_goodsdetails);

	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	private String endTime;

	public void getData() {
		RequestParams params = new RequestParams();
		params.put("productId", productId);
		params.put("userId", userid);
		System.out.println("zuo=====" + params.toString());
		showProgressDialog("加载中...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetProductById, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				rl_goods.setVisibility(View.VISIBLE);
				rl_bottom_goods.setVisibility(View.VISIBLE);
				sv_all.smoothScrollTo(0, 0);
				System.out.println("zuo====" + response.toString());
				desinUrl = response.optString("desinUrl");
				endTime = response.optString("endTime");
				String info = response.optString("info");
				title = response.optString("title");
				tv_goodstitle.setText(title);
				String cloth = response.optString("cloth");
				shuShiDu = response.optString("shuShiDu");
				userName = response.optString("userName");
				allTime = response.optLong("allTime");
				useTime = response.optLong("useTime");
				String createDate = response.optString("createDate");
				String desinTitle = response.optString("desinTitle");
				String userImg = response.optString("userImg");
				url = response.optString("url");
				String makeDate = response.optString("makeDate");
				String clothAddress = response.optString("clothAddress");
				desinInfo = response.optString("desinInfo");
				isAction = response.optInt("isAction");
				JSONArray optJSONArray = response.optJSONArray("showList");
				if (optJSONArray != null) {
					for (int i = 0; i < optJSONArray.length(); i++) {
						try {
							JSONObject jj = optJSONArray.getJSONObject(i);
							showList.add(jj.optString(("url")));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				initshowVp();
				int onePrice = response.optInt("response");
				int twoPrice = response.optInt("twoPrice");
				int shareCount = response.optInt("shareCount");
				int threePeople = response.optInt("threePeople");
				wantBuyCount = response.optInt("wantBuyCount");
				int fourPeople = response.optInt("fourPeople");
				userId = response.optInt("userId");
				isTop = response.optInt("isTop");
				productTopCount = response.optInt("productTopCount");
				int afterPrice = response.optInt("afterPrice");
				int onePeople = response.optInt("onePeople");
				int status = response.optInt("status");
				int twoPeople = response.optInt("twoPeople");
				price = response.optDouble("price");
				int threePrice = response.optInt("threePrice");
				int discussCount = response.optInt("discussCount");
				int fourPrice = response.optInt("fourPrice");
				arrivePeople = response.optInt("arrivePeople");
				percentage = response.optDouble("percentage");
				tv_soft.setText(shuShiDu);
				tv_texture.setText("" + cloth);
				if (isAction == 1) {
					im_focus.setImageResource(R.drawable.newfocusde);
				} else {
					im_focus.setImageResource(R.drawable.newfocus);
				}
				tv_designername.setText(userName);

				tv_price.setText("¥ " + df.format(price));
				tv_buypeople.setText("" + wantBuyCount + "人");
				tv_nubtoby.setText("" + wantBuyCount);
				tv_creatday.setText("" + makeDate);
				tv_endtime.setText("" + endTime);
				tv_discusscount.setText("" + discussCount);
				// tv_sharecount.setText("" + shareCount);
				tv_p4.setText("" + fourPrice + "元");
				tv_p3.setText("" + threePrice + "元");
				tv_p2.setText("" + twoPrice + "元");
				tv_p1.setText("" + onePrice + "元");
				tv_onepeople.setText("" + onePeople);
				tv_twopeople.setText("" + twoPeople);
				tv_threepeople.setText("" + threePeople);
				tv_desintitle.setText("" + desinTitle);
				tv_createdata.setText("" + createDate);
				if (desinInfo == null || desinInfo.equals("null")) {
					tv_desininfo.setText("简介:");
				} else {
					tv_desininfo.setText("简介:" + desinInfo);
				}

				// Glide.with(Activity_GoodsDetails.this).load(AppFinalUrl.BASEURL
				// + userImg)
				// /*
				// * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
				// */
				// .placeholder(R.drawable.tianc).crossFade().into(im_dehead);
				// Glide.with(Activity_GoodsDetails.this).load(AppFinalUrl.BASEURL
				// + userImg)
				// /*
				// * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
				// */
				// .placeholder(R.drawable.tianc).crossFade().into(im_headtwo);
				// Glide.with(Activity_GoodsDetails.this).load(AppFinalUrl.BASEURL
				// + userImg)
				// /*
				// * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
				// */
				// .placeholder(R.drawable.tianc).crossFade().into(im_headthree);

				ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + userImg, im_dehead, options);
				ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + userImg, im_headtwo, options);
				ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + userImg, im_headthree, options);

				// new Thread() {
				// public void run() {
				// Bitmap imageBitmap = getImageBitmap(AppFinalUrl.BASEURL +
				// desinUrl);
				// if (imageBitmap != null) {
				// width_one = imageBitmap.getWidth();
				// height_one = imageBitmap.getHeight();
				// imageBitmap.recycle();
				// }
				//
				// handler.sendEmptyMessage(1);
				// };
				// }.start();
				// new Thread() {
				// public void run() {
				// Bitmap imageBitmap = getImageBitmap(AppFinalUrl.BASEURL +
				// url);
				// if (imageBitmap != null) {
				// width_two = imageBitmap.getWidth();
				// height_two = imageBitmap.getHeight();
				// imageBitmap.recycle();
				// }
				//
				// handler.sendEmptyMessage(2);
				// };
				// }.start();

				// toplsit
				try {
					JSONArray jsonArray = response.getJSONArray("topList");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String imgUrl = j.optString("imgUrl");
						int userIdd = j.optInt("userId");
						toplist.add(new TopList(imgUrl, userIdd));
					}
					setTopList();
					tv_topnub.setText("" + toplist.size());
					JSONArray jsonArray2 = response.getJSONArray("desinList");
					for (int i = 0; i < jsonArray2.length(); i++) {
						JSONObject j = jsonArray2.getJSONObject(i);
						String url = j.optString("url");
						String width = j.optString("width");
						String height = j.optString("height");
						desinList.add(new DesinList(url, width, height));
						pics_sjg.add(url);
					}
					// 设置设计图稿
					setdesinlist();

					JSONArray jsonArray6 = response.getJSONArray("desinPhotoAndVideo");
					for (int i = 0; i < jsonArray6.length(); i++) {
						JSONObject jj = jsonArray6.getJSONObject(i);
						String url = jj.optString("url");
						String width = jj.optString("width");
						String height = jj.optString("height");
						type = jj.optInt("type");
						desinPhotoAndVideo.add(new DesinPhotoAndVideo(type, url, width, height));
					}
					initdesiVp();

					JSONArray jsonArray3 = response.getJSONArray("modelList");
					for (int i = 0; i < jsonArray3.length(); i++) {
						JSONObject j = jsonArray3.getJSONObject(i);
						String url = j.optString("url");
						String width = j.optString("width");
						String height = j.optString("height");
						modelList.add(new ModeList(url, width, height));
						pic_modeshow.add(url);
					}
					// 模特展示
					setmodelList();
					JSONArray jsonArray4 = response.getJSONArray("desinTeacerList");
					for (int i = 0; i < jsonArray4.length(); i++) {
						JSONObject j = jsonArray4.getJSONObject(i);
						String level = j.optString("level");
						String style = j.optString("style");
						String roomInfo = j.optString("roomInfo");
						String userNamee = j.optString("userName");
						String userInfo = j.optString("userInfo");
						String selfStyle = j.optString("selfStyle");
						String myInfo = j.optString("myInfo");
						int scorce = j.optInt("scorce");
						int fans = j.optInt("fans");
						int myAction = j.optInt("myAction");
						int topcount = j.optInt("topcount");
						int isMake = j.optInt("isMake");
						desinTeacerList.add(new DesinTeacerList(level, style, roomInfo, userNamee, userInfo, selfStyle, myInfo,
								scorce, isMake, fans, myAction, topcount));
					}
					setdesinTeacerList();
					JSONArray jsonArray5 = response.getJSONArray("otherList");
					for (int i = 0; i < jsonArray5.length(); i++) {
						JSONObject j = jsonArray5.getJSONObject(i);
						String url = j.optString("url");
						String width = j.optString("width");
						String height = j.optString("height");
						otherList.add(new OtherList(url, width, height));
						pic_other.add(url);
					}
					// 其他
					setotherList();
					// 设计师介绍
					JSONArray jsonArray7 = response.getJSONArray("desinTeacherVideo");
					for (int i = 0; i < jsonArray7.length(); i++) {
						JSONObject j = jsonArray7.getJSONObject(i);
						String url = j.optString("url");
						String width = j.optString("width");
						String height = j.optString("height");
						int type = j.optInt("type");
						desinTeacherVideo.add(new DesinTeacherVideo(type, url, width, height));
					}
					setsjsjs();
					setYouBiao();
					if (isTop == 1) {
						im_heart_zan.setImageResource(R.drawable.newprised);
					} else {
						im_heart_zan.setImageResource(R.drawable.newtoprise);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});
	}

	private MyVPAdapter vpshow;

	// 展示封面图
	private void initshowVp() {
		vpshow = new MyVPAdapter(showList);
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) vp_picshow.getLayoutParams();
		layoutParams.height = (int) (w * 1.2);
		vp_picshow.setLayoutParams(layoutParams);
		vp_picshow.setAdapter(vpshow);
		vp_picshow.setOnPageChangeListener(this);
		initSmallDot(0);
	}

	private void initSmallDot(int index) {

		dots_group.removeAllViews();
		for (int i = 0; i < showList.size(); i++) {
			ImageView imageView = new ImageView(this);
			imageView.setImageResource(R.drawable.round);
			imageView.setPadding(5, 0, 5, 0);
			dots_group.addView(imageView);
		}
		if (showList.size() != 0) {
			// 设置选中项
			((ImageView) dots_group.getChildAt(index)).setImageResource(R.drawable.round_select);
		}

	}

	private MyVPDEAdapter vpdee;

	// 设计介绍
	private void initdesiVp() {
		if (type == 1) {
			vp_picde.setVisibility(View.GONE);

			// // 设置视频缩略图
			if (desinPhotoAndVideo.size() != 0 && desinPhotoAndVideo.get(0).url != null) {
				new Thread() {
					public void run() {

						// createVideoThumbnail1 =
						// createVideoThumbnail(desinPhotoAndVideo.get(0).url,
						// 300, 400);
						// handler.sendEmptyMessage(3);
						rl_play.setVisibility(View.VISIBLE);
					};
				}.start();

				// createVideoThumbnail.recycle();
			}

		} else {
			// vp_picde.setVisibility(View.GONE);
			// vpdee = new MyVPDEAdapter(desinPhotoAndVideo);
			// for (int i = 0; i < desinPhotoAndVideo.size(); i++) {
			// showbiglist.add(desinPhotoAndVideo.get(i).url);
			// }
			// if (Integer.parseInt("" + desinPhotoAndVideo.get(0).height) != 0
			// && Integer.parseInt(desinPhotoAndVideo.get(0).width) != 0) {
			// RelativeLayout.LayoutParams layoutParams =
			// (RelativeLayout.LayoutParams) vp_picde.getLayoutParams();
			// layoutParams.width = w;
			// layoutParams.height = w * Integer.parseInt("" +
			// desinPhotoAndVideo.get(0).height)
			// / Integer.parseInt(desinPhotoAndVideo.get(0).width);
			// vp_picde.setLayoutParams(layoutParams);
			// }
			// vp_picde.setAdapter(vpdee);
			rl_play.setVisibility(View.GONE);
		}

	}

	class DesinPhotoAndVideo {
		int type;
		String url, width, height;

		public DesinPhotoAndVideo(int type, String url, String width, String height) {
			super();
			this.type = type;
			this.url = url;
			this.width = width;
			this.height = height;
		}

	}

	class MyVPAdapter extends PagerAdapter {
		ArrayList<String> data;

		public MyVPAdapter(ArrayList<String> data) {
			super();
			this.data = data;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			View v = View.inflate(Activity_GoodsDetails.this, R.layout.item_imbig, null);
			ImageView photoView = (ImageView) v.findViewById(R.id.im_bigshow);
			// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
			// data.get(position), photoView, options);

			Glide.with(Activity_GoodsDetails.this).load(AppFinalUrl.photoBaseUri + data.get(position) + "-middle")
			/*
			 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			 */
			.crossFade().placeholder(R.drawable.tianc).into(photoView);

			System.out.println("zuo++show==+" + position + "    " + data.get(position));
			photoView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Activity_GoodsDetails.this, Activity_LookPic.class);
					intent.putExtra("position", position);
					intent.putStringArrayListExtra("pics", data);
					startActivity(intent);
				}
			});
			container.addView(v);
			return v;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	class MyVPDEAdapter extends PagerAdapter {
		ArrayList<DesinPhotoAndVideo> data;

		public MyVPDEAdapter(ArrayList<DesinPhotoAndVideo> data) {
			super();
			this.data = data;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {

			View v = View.inflate(Activity_GoodsDetails.this, R.layout.im_de, null);
			ImageView photoView = (ImageView) v.findViewById(R.id.im_de);
			container.addView(v);

			Glide.with(Activity_GoodsDetails.this).load(AppFinalUrl.photoBaseUri + data.get(position).url + "-middle")
			/*
			 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			 */
			.crossFade().into(photoView);
			// Uri uri = Uri.parse(AppFinalUrl.BASEURL +
			// data.get(position).url);
			// photoView.setImageURI(uri);
			// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
			// data.get(position).url, photoView, options);
			System.out.println("zuo++showListde==+" + position + "    " + data.get(position));
			photoView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Activity_GoodsDetails.this, Activity_LookPic.class);
					intent.putExtra("position", position);
					intent.putStringArrayListExtra("pics", showbiglist);
					startActivity(intent);
				}
			});
			return v;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	private void setdesinTeacerList() {
		// tv_dename,
		// tv_isfocus, tv_isopen, tv_focusnub, tv_fansnub, tv_rankde,
		// tv_heartnub, tv_scored
		tv_dename.setText("" + desinTeacerList.get(0).userName);
		//
		if (desinTeacerList.get(0).isMake == 1) {
			tv_isopen.setText("已开通定制");
		} else {
			tv_isopen.setText("未开通定制");
		}
		tv_newprise.setText("" + productTopCount);
		tv_focusnub.setText("" + desinTeacerList.get(0).myAction);
		tv_fansnub.setText("" + desinTeacerList.get(0).fans);
		tv_rankde.setText("" + desinTeacerList.get(0).level);
		tv_heartnub.setText("" + desinTeacerList.get(0).topcount);
		tv_scored.setText("" + desinTeacerList.get(0).scorce);
		if (desinTeacerList.get(0).userInfo.equals("null") || desinTeacerList.get(0).userInfo == null) {
			tv_desinfo.setText("简介:");
		} else {
			tv_desinfo.setText("简介:" + desinTeacerList.get(0).userInfo);
		}

		if (desinTeacerList.get(0).selfStyle == null || desinTeacerList.get(0).selfStyle.equals("null")) {
			tv_direction.setText("");

		} else {

			tv_direction.setText("" + desinTeacerList.get(0).selfStyle);
		}
		if (desinTeacerList.get(0).style == null || desinTeacerList.get(0).style.equals("null")) {
			tv_styte_de.setText("");

		} else {

			tv_styte_de.setText("" + desinTeacerList.get(0).style);
		}

		if (desinTeacerList.get(0).level.contains("黑一")) {
			tv_rankde.setBackgroundResource(R.drawable.bl1);
		} else if (desinTeacerList.get(0).level.contains("黑二")) {
			tv_rankde.setBackgroundResource(R.drawable.bl2);
		} else if (desinTeacerList.get(0).level.contains("黑三")) {
			tv_rankde.setBackgroundResource(R.drawable.bl3);
		} else if (desinTeacerList.get(0).level.contains("灰一")) {
			tv_rankde.setBackgroundResource(R.drawable.g1);
		} else if (desinTeacerList.get(0).level.contains("灰二")) {
			tv_rankde.setBackgroundResource(R.drawable.g2);
		} else if (desinTeacerList.get(0).level.contains("灰三")) {
			tv_rankde.setBackgroundResource(R.drawable.g3);
		} else if (desinTeacerList.get(0).level.contains("白一")) {
			tv_rankde.setBackgroundResource(R.drawable.w1);
		} else if (desinTeacerList.get(0).level.contains("白二")) {
			tv_rankde.setBackgroundResource(R.drawable.w2);
		} else if (desinTeacerList.get(0).level.contains("白三")) {
			tv_rankde.setBackgroundResource(R.drawable.w3);
		}
		tv_rankdeee.setText("" + desinTeacerList.get(0).level);
	}

	private View v_otherlist;

	private void setotherList() {
		MyOtherAdapter oadapter = new MyOtherAdapter();
		lv_other.setAdapter(oadapter);
		// ll_containother.removeAllViews();
		if (otherList.size() == 0) {
			rl_other.setVisibility(View.GONE);
		}
		// for (int i = 0; i < otherList.size(); i++) {
		// v_otherlist = View.inflate(Activity_GoodsDetails.this,
		// R.layout.im_de, null);
		// ImageView im = (ImageView) v_otherlist.findViewById(R.id.im_de);
		// if (otherList.get(i).height.length() != 0 &&
		// otherList.get(i).width.length() != 0
		// && Integer.parseInt(otherList.get(i).width) != 0) {
		// LinearLayout.LayoutParams params = (LayoutParams)
		// im.getLayoutParams();
		// params.width = w;
		// params.height = w * Integer.parseInt("" + otherList.get(i).height) /
		// Integer.parseInt(otherList.get(i).width);
		// im.setLayoutParams(params);
		// }
		//
		// Glide.with(Activity_GoodsDetails.this).load(AppFinalUrl.BASEURL +
		// otherList.get(i).url)
		// /*
		// * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
		// */
		// .placeholder(R.drawable.tianc).crossFade().into(im);
		// // ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
		// // otherList.get(i).url, im, options);
		// ll_containother.addView(v_otherlist);
		// }
	}

	class MyOtherAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return otherList == null ? 0 : otherList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return otherList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			MYOtherholder h;
			if (v == null) {
				h = new MYOtherholder();
				v = View.inflate(Activity_GoodsDetails.this, R.layout.item_image_newother, null);
				h.im = (ImageView) v.findViewById(R.id.im_other);
				v.setTag(h);
			} else {
				h = (MYOtherholder) v.getTag();
			}
			if (otherList.get(position).height.length() != 0 && otherList.get(position).width.length() != 0
					&& Integer.parseInt(otherList.get(position).width) != 0) {
				LayoutParams params = (LayoutParams) h.im.getLayoutParams();
				params.width = w;
				params.height = w * Integer.parseInt("" + otherList.get(position).height)
						/ Integer.parseInt(otherList.get(position).width);
				h.im.setLayoutParams(params);
			}
			Glide.with(Activity_GoodsDetails.this).load(AppFinalUrl.photoBaseUri + otherList.get(position).url + "-middle")
			/*
			 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			 */
			.placeholder(R.drawable.tianc).crossFade().into(h.im);
			// Uri uri = Uri.parse(AppFinalUrl.BASEURL +
			// otherList.get(position).url);
			// h.im.setImageURI(uri);
			h.im.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg1) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Activity_GoodsDetails.this, Activity_LookPic.class);
					intent.putExtra("position", position);
					intent.putStringArrayListExtra("pics", pic_other);
					startActivity(intent);
				}
			});
			return v;
		}
	}

	class MYOtherholder {
		ImageView im;
	}

	class OtherList {
		String url, width, height;

		public OtherList(String url, String width, String height) {
			super();
			this.url = url;
			this.width = width;
			this.height = height;
		}

	}

	private MySJSJSAdapter vpde;
	private Bitmap createVideoThumbnail, createVideoThumbnail1;

	// 设计师介绍
	private void setsjsjs() {
		// TODO Auto-generated method stub
		if (desinTeacherVideo.size() != 0) {
			if (desinTeacherVideo.get(0).type == 1) {
				vp_sjsjs_details.setVisibility(View.GONE);
				// // 设置视频缩略图
				if (desinTeacherVideo.get(0).url != null) {
					new Thread() {
						public void run() {
							createVideoThumbnail = createVideoThumbnail(desinTeacherVideo.get(0).url, w, (int) (w * 3 / 4.0));
							handler.sendEmptyMessage(4);
						};
					}.start();

					// createVideoThumbnail.recycle();
				}
			} else {
				vpde = new MySJSJSAdapter(desinTeacherVideo);
				for (int i = 0; i < desinTeacherVideo.size(); i++) {
					showbiglist_sjsjs.add(desinTeacherVideo.get(i).url);
				}
				if (Integer.parseInt("" + desinTeacherVideo.get(0).height) != 0
						&& Integer.parseInt(desinTeacherVideo.get(0).width) != 0) {
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) vp_sjsjs_details.getLayoutParams();
					layoutParams.width = w;
					layoutParams.height = w * Integer.parseInt("" + desinTeacherVideo.get(0).height)
							/ Integer.parseInt(desinTeacherVideo.get(0).width);
					vp_sjsjs_details.setLayoutParams(layoutParams);
				}

				vp_sjsjs_details.setAdapter(vpde);
				rl_play_sjsjs.setVisibility(View.GONE);
			}
		}
		dismissProgressDialog();
	}

	private Bitmap createVideoThumbnail(String url, int width, int height) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		int kind = MediaStore.Video.Thumbnails.MINI_KIND;
		try {
			if (Build.VERSION.SDK_INT >= 14) {
				retriever.setDataSource(url, new HashMap<String, String>());
			} else {
				retriever.setDataSource(url);
			}
			bitmap = retriever.getFrameAtTime();
		} catch (IllegalArgumentException ex) {
			// Assume this is a corrupt video file
		} catch (RuntimeException ex) {
			// Assume this is a corrupt video file.
		} finally {
			try {
				retriever.release();
			} catch (RuntimeException ex) {
				// Ignore failures while cleaning up.
			}
		}
		if (kind == Images.Thumbnails.MICRO_KIND && bitmap != null) {
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}
		return bitmap;
	}

	class MySJSJSAdapter extends PagerAdapter {
		ArrayList<DesinTeacherVideo> data;

		public MySJSJSAdapter(ArrayList<DesinTeacherVideo> data) {
			super();
			this.data = data;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {

			View v = View.inflate(Activity_GoodsDetails.this, R.layout.item_imbig, null);
			ImageView photoView = (ImageView) v.findViewById(R.id.im_bigshow);
			container.addView(v);
			Glide.with(Activity_GoodsDetails.this).load(AppFinalUrl.photoBaseUri + data.get(position).url + "-middle")
			/*
			 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			 */
			.crossFade().into(photoView);

			// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
			// data.get(position).url, photoView, options);
			System.out.println("zuo++showListde==+" + position + "    " + data.get(position));
			photoView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Activity_GoodsDetails.this, Activity_LookPic.class);
					intent.putExtra("position", position);
					intent.putStringArrayListExtra("pics", showbiglist_sjsjs);
					startActivity(intent);
				}
			});
			return v;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	class DesinTeacherVideo {
		int type;
		String url, width, height;

		public DesinTeacherVideo(int type, String url, String width, String height) {
			super();
			this.type = type;
			this.url = url;
			this.width = width;
			this.height = height;
		}
	}

	private View v_modelist;

	// 模特展示
	private void setmodelList() {

		MYMOdelist adapter = new MYMOdelist();
		lv_modeshow.setAdapter(adapter);
		// ll_contain_mode.removeAllViews();
		// for (int i = 0; i < modelList.size(); i++) {
		// v_modelist = View.inflate(Activity_GoodsDetails.this,
		// R.layout.image_goodsdetails, null);
		// ImageView im = (ImageView)
		// v_modelist.findViewById(R.id.im_goodsdetails);
		//
		// if (modelList.get(i).height.length() != 0 &&
		// modelList.get(i).width.length() != 0
		// && Integer.parseInt(modelList.get(i).width) != 0 &&
		// Integer.parseInt("" + modelList.get(i).height) != 0) {
		// RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
		// im.getLayoutParams();
		// params.width = w;
		// params.height = w * Integer.parseInt("" + modelList.get(i).height) /
		// Integer.parseInt(modelList.get(i).width);
		// im.setLayoutParams(params);
		// }
		//
		// Glide.with(Activity_GoodsDetails.this).load(AppFinalUrl.BASEURL +
		// modelList.get(i).url)
		// /*
		// * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
		// */
		// .placeholder(R.drawable.tianc).crossFade().into(im);
		//
		// // ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
		// // modelList.get(i).url, im, options);
		// ll_contain_mode.addView(v_modelist);
		// }
	}

	class MYMOdelist extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return modelList == null ? 0 : modelList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return modelList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			MMH h;
			if (v == null) {
				h = new MMH();
				v = View.inflate(Activity_GoodsDetails.this, R.layout.image_goodsdetails, null);
				h.im = (ImageView) v.findViewById(R.id.im_goodsdetails);
				v.setTag(h);
			} else {
				h = (MMH) v.getTag();
			}
			if (modelList.get(position).height.length() != 0 && modelList.get(position).width.length() != 0
					&& Integer.parseInt(modelList.get(position).width) != 0
					&& Integer.parseInt("" + modelList.get(position).height) != 0) {
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) h.im.getLayoutParams();
				params.width = w;
				params.height = w * Integer.parseInt("" + modelList.get(position).height)
						/ Integer.parseInt(modelList.get(position).width);
				h.im.setLayoutParams(params);
			}

			Glide.with(Activity_GoodsDetails.this).load(AppFinalUrl.photoBaseUri + modelList.get(position).url + "-middle")
			/*
			 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			 */
			.placeholder(R.drawable.tianc).crossFade().into(h.im);
			// Uri uri = Uri.parse(AppFinalUrl.photoBaseUri +
			// modelList.get(position).url + "-middle");
			// h.im.setImageURI(uri);
			h.im.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg1) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Activity_GoodsDetails.this, Activity_LookPic.class);
					intent.putExtra("position", position);
					intent.putStringArrayListExtra("pics", pic_modeshow);
					startActivity(intent);
				}
			});
			return v;
		}
	}

	class MMH {
		ImageView im;
	}

	class ModeList {
		String url, width, height;

		public ModeList(String url, String width, String height) {
			super();
			this.url = url;
			this.width = width;
			this.height = height;
		}

	}

	private View v_desinlist;

	// desinList设计图稿
	private void setdesinlist() {
		MYLVAdapter lvadapter = new MYLVAdapter();
		ll_xiangao.setAdapter(lvadapter);

		// im_sounue_1.setImageResource(R.drawable.videoyulan);
		// ll_contain_xiangao.removeAllViews();
		// if (desinList.size() == 0) {
		// rl_sjtg.setVisibility(View.GONE);
		// }
		// for (int i = 0; i < desinList.size(); i++) {
		// v_desinlist = View.inflate(Activity_GoodsDetails.this,
		// R.layout.image_goodsdetails, null);
		// ImageView im = (ImageView)
		// v_desinlist.findViewById(R.id.im_goodsdetails);
		// // tv_jieshaowenzi.setVisibility(View.VISIBLE);
		// TextView tv_jieshaowenzi = (TextView)
		// v_desinlist.findViewById(R.id.tv_jieshaowenzi);
		// if (desinList.get(i).height.length() != 0 &&
		// desinList.get(i).width.length() != 0
		// && Integer.parseInt(desinList.get(i).width) != 0 &&
		// Integer.parseInt("" + desinList.get(i).height) != 0) {
		// RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
		// im.getLayoutParams();
		// params.width = w;
		// params.height = w * Integer.parseInt("" + desinList.get(i).height) /
		// Integer.parseInt(desinList.get(i).width);
		// im.setLayoutParams(params);
		// }
		// if (i == 0) {
		// tv_jieshaowenzi.setVisibility(View.VISIBLE);
		// if (desinInfo == null || desinInfo.equals("null") ||
		// desinInfo.equals("")) {
		// tv_jieshaowenzi.setVisibility(View.GONE);
		// ;
		// } else {
		// tv_jieshaowenzi.setText("" + desinInfo);
		// }
		// } else {
		// tv_jieshaowenzi.setVisibility(View.GONE);
		// }
		// Glide.with(Activity_GoodsDetails.this).load(AppFinalUrl.BASEURL +
		// desinList.get(i).url)
		// /*
		// * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
		// */
		// .crossFade().into(im);
		//
		// // ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
		// // desinList.get(i).url, im, options);
		// ll_contain_xiangao.addView(v_desinlist);
		// }
	}

	class MYLVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return desinList == null ? 0 : desinList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return desinList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int arg0, View v, ViewGroup arg2) {
			// TODO Auto-generated method stub
			MMH h;
			if (v == null) {
				h = new MMH();
				v = View.inflate(Activity_GoodsDetails.this, R.layout.image_goodsdetails, null);
				h.im = (ImageView) v.findViewById(R.id.im_goodsdetails);
				// h.tv_jieshaowenzi = (TextView)
				// v.findViewById(R.id.tv_jieshaowenzi);
				v.setTag(h);
			} else {
				h = (MMH) v.getTag();
			}
			if (desinList.get(arg0).height.length() != 0 && desinList.get(arg0).width.length() != 0
					&& Integer.parseInt(desinList.get(arg0).width) != 0 && Integer.parseInt("" + desinList.get(arg0).height) != 0) {
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) h.im.getLayoutParams();
				params.width = w;
				params.height = w * Integer.parseInt("" + desinList.get(arg0).height)
						/ Integer.parseInt(desinList.get(arg0).width);
				h.im.setLayoutParams(params);
			}
			// Uri uri = Uri.parse(AppFinalUrl.photoBaseUri +
			// desinList.get(arg0).url + "-middle");
			// h.im.setImageURI(uri);
			Glide.with(Activity_GoodsDetails.this).load(AppFinalUrl.photoBaseUri + desinList.get(arg0).url + "-middle")
			/*
			 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			 */
			.crossFade().into(h.im);
			// if (arg0 == 0) {
			// h.tv_jieshaowenzi.setVisibility(View.VISIBLE);
			// if (desinInfo == null || desinInfo.equals("null") ||
			// desinInfo.equals("")) {
			// h.tv_jieshaowenzi.setVisibility(View.GONE);
			//
			// } else {
			// h.tv_jieshaowenzi.setText("" + desinInfo);
			// }
			// } else {
			// h.tv_jieshaowenzi.setVisibility(View.GONE);
			// }
			h.im.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg1) {
					// TODO Auto-generated method stub

					Intent intent = new Intent(Activity_GoodsDetails.this, Activity_LookPic.class);
					intent.putExtra("position", arg0);
					intent.putStringArrayListExtra("pics", pics_sjg);
					startActivity(intent);
				}
			});
			return v;
		}
	}

	class MyHolder {
		ImageView im;
		TextView tv_jieshaowenzi;
	}

	class DesinList {
		String url, width, height;

		public DesinList(String url, String width, String height) {
			super();
			this.url = url;
			this.width = width;
			this.height = height;
		}
	}

	private View v_toplist;

	// 设置toplist
	private void setTopList() {
		ll_contain_toplist.removeAllViews();
		for (int i = 0; i < toplist.size(); i++) {
			v_toplist = View.inflate(Activity_GoodsDetails.this, R.layout.item_im, null);
			CircleImageView cv = (CircleImageView) v_toplist.findViewById(R.id.im_zanph);

			Glide.with(Activity_GoodsDetails.this).load(AppFinalUrl.photoBaseUri + toplist.get(i).imgUrl + "-middle")
			/*
			 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			 */
			.placeholder(R.drawable.tianc).crossFade().into(cv);

			// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
			// toplist.get(i).imgUrl, cv, options);
			ll_contain_toplist.addView(v_toplist);
		}
	}

	private Bitmap getImageBitmap(String url) {
		URL imgUrl = null;
		Bitmap bitmap = null;
		try {
			imgUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	class TopList {
		String imgUrl;
		int userId;

		public TopList(String imgUrl, int userId) {
			super();
			this.imgUrl = imgUrl;
			this.userId = userId;
		}

	}

	class DesinTeacerList {
		String level, style, roomInfo, userName, userInfo, selfStyle, myInfo;
		int scorce, isMake, fans, myAction, topcount;

		public DesinTeacerList(String level, String style, String roomInfo, String userName, String userInfo, String selfStyle,
				String myInfo, int scorce, int isMake, int fans, int myAction, int topcount) {
			super();
			this.level = level;
			this.style = style;
			this.roomInfo = roomInfo;
			this.userName = userName;
			this.userInfo = userInfo;
			this.selfStyle = selfStyle;
			this.myInfo = myInfo;
			this.scorce = scorce;
			this.isMake = isMake;
			this.fans = fans;
			this.myAction = myAction;
			this.topcount = topcount;
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

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		initSmallDot(arg0);
	}

	// 获取评论
	private void getCommends() {
		RequestParams params = new RequestParams();
		params.put("product.id", productId);
		params.put("pageModel.pageIndex", 1);
		params.put("pageModel.pageSize", 1);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetDiscusssByProductId, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo===" + response.toString());

				try {
					JSONArray jsonArray = response.getJSONArray("dicussList");
					String count = response.optString("count");
					tv_commendsnub.setText("评价(" + count + ")");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String content = j.optString("content");
						String userName = j.optString("userName");
						String createDate = j.optString("createDate");
						String url = j.optString("url");
						int dicussId = j.optInt("dicussId");
						int userId = j.optInt("userId");
						cs = new Commends(content, userName, createDate, url, dicussId, userId);
					}
					if (cs != null) {
						setCommends(cs);
						tv_morecommends.setVisibility(View.VISIBLE);
						rv_onecommends.setVisibility(View.VISIBLE);
					} else {

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	// 设置评论
	private void setCommends(Commends cs) {
		// TODO Auto-generated method stub
		tv_commtime.setText("" + cs.getCreateDate());
		tv_commendscon.setText("" + cs.getContent());
		tv_phonenub.setText("" + cs.getUserName());
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("商品详情");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("商品详情");
	}
}
