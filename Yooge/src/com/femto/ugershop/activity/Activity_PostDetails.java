package com.femto.ugershop.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.jivesoftware.smackx.muc.Affiliate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.twitter.Twitter;

import com.bumptech.glide.Glide;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.view.CircleImageView;
import com.femto.ugershop.view.MyGridView;
import com.femto.ugershop.view.MyScrollView;
import com.femto.ugershop.view.ScrollViewWithListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_PostDetails extends BaseActivity implements OnTouchListener {
	private RelativeLayout rl_back_postdetails, rl_chart, rl_sharepost, rl_comment, rl_praise, rl_collectdetails;
	private ScrollViewWithListView lv_comment;
	private MyLvAdapter lvadapter;
	private ScrollView sv_postdetails;
	private String msg, imgUrl, userName, createDate;
	private int type, friendId, topCount, shareCount, isCollection, userId, isTop, discussCount;
	private TextView tv_title_postd, tv_name_postd, tv_iscollect_postd, tv_title_postd2, tv_msg_post, tv_prise_postd,
			tv_comment_postd, tv_share_postd, tv_chat_post, tv_time_postd;
	private ImageView im_showpic_postd, im_at;
	private CircleImageView im_head_postd;
	private DisplayImageOptions options;
	private List<ListComments> listComments;
	private int myId;
	private int flag;
	private MyGridView gv_pic_details;
	private int w;
	private ArrayList<String> pics;
	private ArrayList<String> picurl;
	private GVpicAdapter adapter;
	private LinearLayout ll_commends;
	private EditText ed_commendpost;
	private TextView tv_sendcommends, tv_clicktomore;
	private String url;
	private int ffff = 1;
	private ArrayList<Integer> ids;
	private ArrayList<String> names;
	private int iscommends = 0;
	private LinearLayout ll_sv;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	int size = 0;
	private String idsstr = "";
	private String videopath = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private File out;
	private int userType;

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("帖子详情");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_postdetails:
			finish();
			break;
		case R.id.rl_chart:
			if (myId == 0) {
				Intent intent = new Intent(this, Activity_Login.class);
				startActivity(intent);
				return;
			}

			startActivity(new Intent(Activity_PostDetails.this, ChatActivity.class).putExtra("name", userName).putExtra("userId",
					"" + userId));

			// showBack(rl_chart, rl_share, rl_comment, rl_praise);
			break;
		case R.id.rl_sharepost:

			if (pics.size() != 0) {
				savePic(pics.get(0));
			} else {
				showShare();
			}

			// showBack(rl_share, rl_chart, rl_comment, rl_praise);
			break;
		case R.id.rl_comment:
			if (myId == 0) {
				Intent intent = new Intent(this, Activity_Login.class);
				startActivity(intent);
				return;
			}
			ed_commendpost.setFocusable(true);
			ed_commendpost.setFocusableInTouchMode(true);
			ed_commendpost.requestFocus();
			InputMethodManager imm = (InputMethodManager) ed_commendpost.getContext().getSystemService(
					Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			ll_commends.setVisibility(View.VISIBLE);

			// showBack(rl_comment, rl_chart, rl_share, rl_praise);
			break;
		case R.id.rl_praise:
			if (myId == 0) {
				Intent intent = new Intent(this, Activity_Login.class);
				startActivity(intent);
				return;
			}
			if (isTop == 1) {
				cancleprise();
			} else {
				prise();
			}

			// showBack(rl_praise, rl_chart, rl_share, rl_comment);
			break;
		case R.id.rl_collectdetails:
			if (myId == 0) {
				Intent intent = new Intent(this, Activity_Login.class);
				startActivity(intent);
				return;
			}
			if (isCollection == 1) {
				delecollect(friendId, type, 0);
			} else {
				collect(friendId, type, 0);
			}

			break;
		case R.id.tv_sendcommends:
			if (myId == 0) {
				Intent intent = new Intent(this, Activity_Login.class);
				startActivity(intent);
				return;
			}
			commends();
			break;
		case R.id.im_at:
			if (myId == 0) {
				Intent intent = new Intent(this, Activity_Login.class);
				startActivity(intent);
				return;
			}
			Intent intent = new Intent(Activity_PostDetails.this, Activity_AtMeTongXunlu.class);
			startActivityForResult(intent, 111);
			break;
		case R.id.tv_clicktomore:
			if (isend) {
				tv_clicktomore.setVisibility(View.INVISIBLE);
				Toast.makeText(this, "没有更多", Toast.LENGTH_SHORT).show();
			} else {
				tv_clicktomore.setText("加载中...");
				listComments.clear();
				getData(pageIndex, pageSize);
			}

			break;
		case R.id.im_head_postd:
			System.out.println("zuouserType==" + userType);
			if (userType == 1) {
				Intent intent_to = new Intent(this, Activity_Designer.class);
				intent_to.putExtra("userId", userId);
				startActivity(intent_to);
			} else if (userType == 2) {
				Intent intent_to = new Intent(this, Activity_CustomMain.class);
				intent_to.putExtra("userId", userId);
				startActivity(intent_to);
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null && resultCode == 111) {
			names = data.getStringArrayListExtra("names");
			ids = data.getIntegerArrayListExtra("ids");
			System.out.println("zuo==names==" + names);
			String atnames = "";
			idsstr = "";
			for (int i = 0; i < ids.size(); i++) {
				if (i == ids.size() - 1) {
					idsstr = idsstr + ids.get(i);
				} else {
					idsstr = idsstr + ids.get(i) + ",";
				}
			}
			for (int i = 0; i < names.size(); i++) {
				if (i == names.size() - 1) {
					atnames = atnames + "@" + names.get(i);
				} else {
					atnames = atnames + "@" + names.get(i) + ";";
				}

			}
			ed_commendpost.setText(ed_commendpost.getText().toString() + atnames);
			ed_commendpost.setFocusable(true);
			ed_commendpost.setFocusableInTouchMode(true);
			ed_commendpost.requestFocus();
			InputMethodManager imm = (InputMethodManager) ed_commendpost.getContext().getSystemService(
					Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
		}
	}

	private void showShare() {
		System.out.println("zuoshowshare");
		ShareSDK.initSDK(Activity_PostDetails.this);
		OnekeyShare oks = new OnekeyShare();

		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("" + msg);
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.femto.ugershop");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("" + msg);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		if (dir != null) {
			oks.setImagePath(dir.getPath());// 确保SDcard下面存在此张图片
		}
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
		oks.show(Activity_PostDetails.this);
		oks.setCallback(new PlatformActionListener() {

			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
				shareMsg();
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {

			}
		});
	}

	@Override
	public void initView() {
		MyApplication.addActivity(this);
		// TODO Auto-generated method stub
		out = new File(videopath);
		if (!out.exists()) {
			out.mkdirs();
		}
		videopath = out.getPath();
		listComments = new ArrayList<ListComments>();
		ids = new ArrayList<Integer>();
		names = new ArrayList<String>();
		rl_back_postdetails = (RelativeLayout) findViewById(R.id.rl_back_postdetails);
		lv_comment = (ScrollViewWithListView) findViewById(R.id.lv_comment);
		lvadapter = new MyLvAdapter();
		lv_comment.setAdapter(lvadapter);
		ll_sv = (LinearLayout) findViewById(R.id.ll_sv);
		sv_postdetails = (ScrollView) findViewById(R.id.sv_postdetails);
		rl_chart = (RelativeLayout) findViewById(R.id.rl_chart);
		rl_sharepost = (RelativeLayout) findViewById(R.id.rl_sharepost);
		rl_comment = (RelativeLayout) findViewById(R.id.rl_comment);
		rl_praise = (RelativeLayout) findViewById(R.id.rl_praise);
		tv_sendcommends = (TextView) findViewById(R.id.tv_sendcommends);
		ed_commendpost = (EditText) findViewById(R.id.ed_commendpost);
		// private TextView tv_title_postd, tv_name_postd, tv_iscollect_postd,
		// tv_title_postd2, tv_msg_post, tv_prise_postd, tv_comment_postd,
		// tv_share_postd, tv_chat_post;
		// private ImageView im_showpic_postd;
		// private CircleImageView im_head_postd;
		tv_title_postd = (TextView) findViewById(R.id.tv_title_postd);
		tv_name_postd = (TextView) findViewById(R.id.tv_name_postd);
		tv_iscollect_postd = (TextView) findViewById(R.id.tv_iscollect_postd);
		ll_commends = (LinearLayout) findViewById(R.id.ll_commends);
		tv_msg_post = (TextView) findViewById(R.id.tv_msg_post);
		tv_prise_postd = (TextView) findViewById(R.id.tv_prise_postd);
		rl_collectdetails = (RelativeLayout) findViewById(R.id.rl_collectdetails);
		tv_comment_postd = (TextView) findViewById(R.id.tv_comment_postd);
		tv_share_postd = (TextView) findViewById(R.id.tv_share_postd);
		tv_chat_post = (TextView) findViewById(R.id.tv_chat_post);
		im_head_postd = (CircleImageView) findViewById(R.id.im_head_postd);
		tv_time_postd = (TextView) findViewById(R.id.tv_time_postd);
		im_at = (ImageView) findViewById(R.id.im_at);
		tv_clicktomore = (TextView) findViewById(R.id.tv_clicktomore);
		tv_name_postd.setText(userName);

		// tv_msg_post.setText(new String(Base64.decode(msg.getBytes(),
		// Base64.DEFAULT)));

		tv_msg_post.setText(msg);
		tv_prise_postd.setText("" + topCount);
		tv_comment_postd.setText("" + discussCount);
		tv_share_postd.setText("" + shareCount);
		tv_time_postd.setText("" + createDate);
		if (isCollection != 1) {

			tv_iscollect_postd.setText("收藏");
		} else {
			tv_iscollect_postd.setText("已收藏");
		}
		if (isTop == 1) {
			Drawable drawable = getResources().getDrawable(R.drawable.newpostheartde);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			tv_prise_postd.setCompoundDrawables(drawable, null, null, null);
		} else {
			Drawable drawable = getResources().getDrawable(R.drawable.newpostheart);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			tv_prise_postd.setCompoundDrawables(drawable, null, null, null);
		}

		// Glide.with(Activity_PostDetails.this).load(AppFinalUrl.BASEURL +
		// imgUrl)
		// /*
		// * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
		// */
		// .placeholder(R.drawable.tianc).crossFade().into(im_showpic_postd);
		// Glide.with(Activity_PostDetails.this).load(AppFinalUrl.BASEURL +
		// imgUrl)
		// /*
		// * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
		// */
		// .placeholder(R.drawable.tianc).crossFade().into(im_head_postd);

		ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + imgUrl, im_showpic_postd, options);
		ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + imgUrl, im_head_postd, options);
		gv_pic_details = (MyGridView) findViewById(R.id.gv_pic_details);
		adapter = new GVpicAdapter();
		gv_pic_details.setAdapter(adapter);
		if (type == 4) {
			url = AppFinalUrl.usergetPushDiscuss;
		} else if (type == 2) {
			url = AppFinalUrl.usergetDiscussByFriendId;
		} else if (type == 1) {
			url = AppFinalUrl.usergetGoodDiscuss;
		} else if (type == 7) {
			url = AppFinalUrl.usergetPushDiscuss;
		}
		getData(1, 10);

	}

	private int mLastY;

	class GVpicAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pics == null ? 0 : pics.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return pics.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			v = View.inflate(Activity_PostDetails.this, R.layout.item_image_post, null);
			ImageView im_commends = (ImageView) v.findViewById(R.id.im_post_pic);

			Glide.with(Activity_PostDetails.this).load(AppFinalUrl.photoBaseUri + pics.get(position) + "-small").centerCrop()
			/*
			 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			 */
			.crossFade().into(im_commends);
			System.out.println("zuooourl=详情==" + AppFinalUrl.photoBaseUri + pics.get(0) + "-small");
			// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
			// picurl.get(position), im_commends, options);
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) im_commends.getLayoutParams();
			params.width = w;
			params.height = w;
			im_commends.setLayoutParams(params);
			im_commends.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Activity_PostDetails.this, Activity_LookPic.class);
					intent.putExtra("position", position);
					intent.putStringArrayListExtra("pics", pics);
					startActivity(intent);
				}
			});
			return v;
		}
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		rl_back_postdetails.setOnClickListener(this);
		sv_postdetails.smoothScrollTo(0, 0);
		sv_postdetails.setOnTouchListener(this);
		// rl_chart, rl_share, rl_comment,
		rl_praise.setOnClickListener(this);
		rl_chart.setOnClickListener(this);
		rl_sharepost.setOnClickListener(this);
		rl_comment.setOnClickListener(this);
		rl_collectdetails.setOnClickListener(this);
		tv_sendcommends.setOnClickListener(this);
		im_at.setOnClickListener(this);
		tv_clicktomore.setOnClickListener(this);
		im_head_postd.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_postdetails);
		initParams();
	}

	public void getData(int pageIndex1, int pageSize) {
		RequestParams params = new RequestParams();
		params.put("friendCircle.id", friendId);
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);
		System.out.println("zuo==params==" + params);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(url, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo==" + response.toString());
				tv_clicktomore.setText("点击加载更多");
				try {
					JSONArray jsonArray = response.getJSONArray("List");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String content = j.optString("content");
						String userName1 = j.optString("userName");
						String createDate1 = j.optString("createDate");
						String userImg1 = j.optString("userImg");
						int userId1 = j.optInt("userId");
						int dicussId = j.optInt("dicussId");
						int type = j.optInt("type");
						size++;
						listComments.add(new ListComments(content, userName1, createDate1, userImg1, userId1, dicussId, type));
					}
					System.out.println("zuo==size==" + size);
					if (size == 10) {
						isend = false;
						tv_clicktomore.setVisibility(View.VISIBLE);
						pageIndex++;
					} else {
						isend = true;
						tv_clicktomore.setVisibility(View.GONE);
					}
					size = 0;
					lvadapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	class Pics {
		String imgUrl, url;

		public Pics(String imgUrl, String url) {
			super();
			this.imgUrl = imgUrl;
			this.url = url;
		}
	}

	private File dir;
	FileOutputStream fos;

	private void savePic(String urlPath) {

		dir = new File(videopath, "showpic.jpg");
		try {
			fos = new FileOutputStream(dir);
			showProgressDialog("分享中...");
			System.out.println("zuo====1111=" + AppFinalUrl.photoBaseUri + urlPath);
			MyApplication.ahc.post(AppFinalUrl.photoBaseUri + urlPath + "-small", new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] data) {
					// TODO Auto-generated method stub

					dismissProgressDialog();
					try {
						fos.write(data);
						fos.flush();
						fos.close();
						showShare();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					// TODO Auto-generated method stub
					dismissProgressDialog();
					System.out.println("zuo----failure==---" + arg0 + "   arg3==" + arg3.toString());
				}
			});
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dismissProgressDialog();
		}
	}

	private void shareMsg() {
		RequestParams params = new RequestParams();
		params.put("user.id", myId);
		params.put("friendCircle.id", friendId);
		params.put("type", type);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.useraddShare, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo分享成功" + response.toString());
				String result;
				try {
					result = response.getString("result");
					if (result.equals("0")) {
						tv_share_postd.setText("" + (++shareCount));
						Toast.makeText(Activity_PostDetails.this, "分享成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(Activity_PostDetails.this, "操作失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	public void commends() {
		RequestParams params = new RequestParams();
		try {
			params.put("discuss.content",
					Base64.encodeToString(ed_commendpost.getText().toString().getBytes("UTF-8"), Base64.DEFAULT));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		params.put("discuss.friendCircle.id", friendId);
		params.put("discuss.user.id", myId);
		params.put("discuss.type", type);
		params.put("ids", idsstr);
		System.out.println("zuo==type=" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.useraddDiscuss, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						closekey();
						tv_comment_postd.setText("" + (++discussCount));
						ll_commends.setVisibility(View.GONE);
						Toast.makeText(Activity_PostDetails.this, "评论成功", Toast.LENGTH_SHORT).show();
						listComments.clear();
						getData(1, 10);
						ids.clear();
					} else {
						Toast.makeText(Activity_PostDetails.this, "评论失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("帖子详情");
		if (iscommends == 1) {
			System.out.println("zuo====来了");
			ed_commendpost.setFocusable(true);
			ed_commendpost.setFocusableInTouchMode(true);
			ed_commendpost.requestFocus();
			InputMethodManager imm = (InputMethodManager) ed_commendpost.getContext().getSystemService(
					Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			ll_commends.setVisibility(View.VISIBLE);
		}
	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		myId = sp.getInt("userId", 0);

		pics = getIntent().getStringArrayListExtra("pics");
		picurl = getIntent().getStringArrayListExtra("picurl");
		msg = getIntent().getStringExtra("msg");
		flag = getIntent().getIntExtra("flag", -1);
		ffff = getIntent().getIntExtra("ffff", -1);
		userType = getIntent().getIntExtra("userType", 2);
		imgUrl = getIntent().getStringExtra("imgUrl");
		System.out.println("zuo收到的+++" + imgUrl);
		userName = getIntent().getStringExtra("userName");
		createDate = getIntent().getStringExtra("createDate");
		type = getIntent().getIntExtra("type", 0);
		iscommends = getIntent().getIntExtra("iscommends", 0);
		friendId = getIntent().getIntExtra("friendId", 0);
		topCount = getIntent().getIntExtra("topCount", 0);
		shareCount = getIntent().getIntExtra("shareCount", 0);
		isCollection = getIntent().getIntExtra("isCollection", 0);
		userId = getIntent().getIntExtra("userId", 0);
		isTop = getIntent().getIntExtra("isTop", 0);
		discussCount = getIntent().getIntExtra("discussCount", 0);
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();

		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		int dp2px = dp2px(this, 32);
		w = (screenWidth - dp2px) / 3;
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	class MyLvAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listComments == null ? 0 : listComments.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listComments.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			MyHolder h;
			if (v == null) {
				h = new MyHolder();
				v = View.inflate(Activity_PostDetails.this, R.layout.item_commends_postde, null);
				h.im_chart = (ImageView) v.findViewById(R.id.im_chart);
				h.im_head_commends = (CircleImageView) v.findViewById(R.id.im_head_commends);
				h.tv_comment_contain = (TextView) v.findViewById(R.id.tv_comment_contain);
				h.tv_comment_name = (TextView) v.findViewById(R.id.tv_comment_name);
				h.tv_delete_content_post = (TextView) v.findViewById(R.id.tv_delete_content_post);

				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_comment_contain.setText("" + listComments.get(position).content);
			h.tv_comment_name.setText("" + listComments.get(position).userName);

			// Glide.with(Activity_PostDetails.this).load(AppFinalUrl.BASEURL +
			// listComments.get(position).userImg)
			// /*
			// * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			// */
			// .placeholder(R.drawable.tianc).crossFade().into(h.im_head_commends);

			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + listComments.get(position).userImg,
					h.im_head_commends, options);
			h.im_chart.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					startActivity(new Intent(Activity_PostDetails.this, ChatActivity.class).putExtra("name",
							listComments.get(position).userName).putExtra("userId", "" + listComments.get(position).userId));
				}
			});
			h.im_head_commends.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (listComments.get(position).type == 1) {
						Intent intent = new Intent(Activity_PostDetails.this, Activity_Designer.class);
						intent.putExtra("userId", listComments.get(position).userId);
						startActivity(intent);
					} else if (listComments.get(position).type == 2) {
						Intent intent = new Intent(Activity_PostDetails.this, Activity_CustomMain.class);
						intent.putExtra("userId", listComments.get(position).userId);
						startActivity(intent);
					}

				}
			});
			if (listComments.get(position).userId == MyApplication.userId) {
				h.tv_delete_content_post.setVisibility(View.VISIBLE);
				h.im_chart.setVisibility(View.GONE);
			} else {
				h.tv_delete_content_post.setVisibility(View.GONE);
				h.im_chart.setVisibility(View.VISIBLE);
			}
			h.tv_delete_content_post.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					deleCommends(position, listComments.get(position).dicussId);
				}

			});
			return v;
		}
	}

	class MyHolder {
		CircleImageView im_head_commends;
		TextView tv_comment_name, tv_comment_contain, tv_delete_content_post;
		ImageView im_chart;
	}

	private void deleCommends(final int position, int dicussId) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("dicussId", dicussId);
		params.put("token", MyApplication.token);
		showProgressDialog("删除中...");
		MyApplication.ahc.post(AppFinalUrl.userdeleteDisucuss, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				String result = response.optString("result");
				if (result.equals("0")) {
					showToast("删除成功", 0);
					listComments.remove(position);
					lvadapter.notifyDataSetChanged();
				} else {
					showToast("删除失败", 0);
				}
			}
		});
	}

	public void showBack(RelativeLayout showrl, RelativeLayout hintrl1, RelativeLayout hintrl2, RelativeLayout hintr3) {
		showrl.setBackgroundColor(getResources().getColor(R.color.white));
		hintrl1.setBackgroundColor(getResources().getColor(R.color.backgrey));
		hintrl2.setBackgroundColor(getResources().getColor(R.color.backgrey));
		hintr3.setBackgroundColor(getResources().getColor(R.color.backgrey));
	}

	class ListComments {
		String content, userName, createDate, userImg;
		int userId, dicussId, type;

		public ListComments(String content, String userName, String createDate, String userImg, int userId, int dicussId, int type) {
			super();
			this.content = content;
			this.userName = userName;
			this.createDate = createDate;
			this.userImg = userImg;
			this.userId = userId;
			this.dicussId = dicussId;
			this.type = type;
		}
	}

	public void prise() {
		RequestParams params = new RequestParams();
		params.put("friendId", friendId);
		params.put("userId", myId);
		params.put("type", type);
		System.out.println("zuo赞" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.useraddTopForFriend, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo====" + response.toString());
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_PostDetails.this, "点赞成功", Toast.LENGTH_SHORT).show();
						Drawable drawable = getResources().getDrawable(R.drawable.newpostheartde);
						drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
						tv_prise_postd.setCompoundDrawables(drawable, null, null, null);
						tv_prise_postd.setText("" + (++topCount));
						isTop = 1;
					}
					if (result.equals("2")) {
						Toast.makeText(Activity_PostDetails.this, "已经赞过了", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void collect(int productid, int type, final int p) {
		RequestParams params = new RequestParams();
		params.put("collection.user.id", myId);
		params.put("collection.friendCircle.id", productid);
		params.put("collection.type", type);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.useraddCollection, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String result = response.getString("result");

					if (result.equals("0")) {
						Toast.makeText(Activity_PostDetails.this, "收藏成功", Toast.LENGTH_SHORT).show();
						tv_iscollect_postd.setText("已收藏");
						isCollection = 1;
					}
					if (result.equals("1")) {
						Toast.makeText(Activity_PostDetails.this, "您已收藏", Toast.LENGTH_SHORT).show();

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void delecollect(int productid, int type, final int p) {
		RequestParams params = new RequestParams();
		params.put("collection.user.id", myId);
		params.put("collection.friendCircle.id", productid);
		params.put("collection.type", type);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usercancleCollection, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String result = response.getString("result");

					if (result.equals("0")) {
						Toast.makeText(Activity_PostDetails.this, "已取消收藏", Toast.LENGTH_SHORT).show();
						tv_iscollect_postd.setText("收藏");
						isCollection = 0;
					}
					if (result.equals("1")) {
						Toast.makeText(Activity_PostDetails.this, "您已收藏", Toast.LENGTH_SHORT).show();

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void cancleprise() {
		RequestParams params = new RequestParams();
		params.put("userId", myId);
		params.put("friendId", friendId);
		params.put("type", type);

		System.out.println("zuo取消赞" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usercancleTop, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_PostDetails.this, "已取消赞", Toast.LENGTH_SHORT).show();
						Drawable drawable = getResources().getDrawable(R.drawable.newpostheart);
						drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
						tv_prise_postd.setCompoundDrawables(drawable, null, null, null);
						tv_prise_postd.setText("" + (--topCount));
						isTop = 0;
					} else {
						Toast.makeText(Activity_PostDetails.this, "操作失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			closekey();
			ll_commends.setVisibility(View.GONE);

			break;

		default:
			break;
		}

		return false;
	}

	public void closekey() {
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
}
