package com.femto.ugershop.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
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
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author 作者Deep:
 * @version 创建时间：2015年9月28日 上午9:27:23 类说明
 */
public class Activity_CustomMain extends SwipeBackActivity implements OnScrollListener {
	private RelativeLayout rl_back_cmain, rl_focus_head;
	private ListView lv_main;
	private View headView;
	private CircleImageView im_head_me_main;
	private TextView tv_usernameme_main, tv_focusnumme_main, tv_fans_me_main, tv_rank_me_main, tv_score_me_main,
			tv_topnub_me_main, tv_cmfans;
	private int userId;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	int size = 0;
	private List<ListItem> listItem;
	private GVpicAdapter adapter;
	private int w;
	MyLVFCAdapter lvfcadatper;
	private DisplayImageOptions options;
	private int myId;
	private String level;
	private String userName;
	private String userImg;
	private int money;
	private int score;
	private int scoreRanking;
	private int myActionCount;
	private int openMake;
	private int fansCount;
	private int topCount;
	private int type = 2;
	private int cfid, ctype, cp;
	private String videopath = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private File out;
	private String cmsg;
	private int isAction = 0;
	private ImageView im_rank_head;
	private LinearLayout ll_myfan_cmain, ll_myfocus_cmain;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:

				break;
			case 2:
				showShare(cmsg, cfid, ctype, cp);
				break;

			default:
				break;
			}

		};
	};
	// private TextView tv_addfocus;
	private ImageView im_focus_head;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_cmain:
			finish();
			break;
		case R.id.ll_myfan_cmain:
			Intent intent_myfans = new Intent(this, Activity_FansNub.class);
			intent_myfans.putExtra("userId", userId);
			startActivity(intent_myfans);
			break;
		case R.id.ll_myfocus_cmain:
			Intent intent_myfocus = new Intent(this, Activity_FocusMe.class);
			intent_myfocus.putExtra("userId", userId);
			startActivity(intent_myfocus);
			break;
		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TOrl_back_cmainDO Auto-generated method stub
		out = new File(videopath);
		if (!out.exists()) {
			out.mkdirs();
		}
		videopath = out.getPath();
		listItem = new ArrayList<ListItem>();
		rl_back_cmain = (RelativeLayout) findViewById(R.id.rl_back_cmain);
		lv_main = (ListView) findViewById(R.id.lv_main);
		headView = View.inflate(this, R.layout.main_headview, null);
		initHeadView();
		lvfcadatper = new MyLVFCAdapter();
		lv_main.addHeaderView(headView, null, false);
		lv_main.setAdapter(lvfcadatper);
		getData();
		isloading = true;
		getFCdata("", pageIndex, pageSize);

	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_cmain.setOnClickListener(this);
		lv_main.setOnScrollListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_custommain);
		initParams();
		MyApplication.addActivity(this);
	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		myId = sp.getInt("userId", -1);
		userId = getIntent().getIntExtra("userId", 0);
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		int dp2px = dp2px(Activity_CustomMain.this, 36);
		w = (screenWidth - dp2px) / 3;
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	private void getData() {

		RequestParams params = new RequestParams();
		params.put("userId", userId);
		params.put("beuserId", myId);
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
					level = response.optString("level");
					userName = response.optString("userName");
					userImg = response.optString("userImg");
					userId = response.optInt("userId");
					money = response.optInt("money");
					score = response.optInt("score");
					isAction = response.optInt("isAction");
					scoreRanking = response.optInt("scoreRanking");
					myActionCount = response.optInt("myActionCount");
					openMake = response.optInt("openMake");
					fansCount = response.optInt("fansCount");
					topCount = response.optInt("topCount");
					setHeadView();
					// tv_usernameme_c, tv_isopenme_c, tv_focusnumme_c,
					// tv_fans_me_c, tv_rank_me_c, tv_score_me_c,
					// tv_topnub_me_c,
					// tv_yue_c, rank_c
					// if (userName == null || userName.equals("null")) {
					// tv_usernameme_c.setText("未填写");
					// } else {
					// tv_usernameme_c.setText("" + userName);
					// }
					//
					// tv_focusnumme_c.setText("" + myActionCount);
					// tv_fans_me_c.setText("" + fansCount);
					// tv_rank_me_c.setText("" + level);
					// tv_score_me_c.setText("" + score);
					// tv_topnub_me_c.setText("" + topCount);
					// tv_yue_c.setText("" + money);
					// rank_c.setText("第" + scoreRanking + "名");
					// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL
					// + userImg, im_head_me_c, options);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	private void initHeadView() {
		im_head_me_main = (CircleImageView) headView.findViewById(R.id.im_head_me_main);
		tv_usernameme_main = (TextView) headView.findViewById(R.id.tv_usernameme_main);
		tv_fans_me_main = (TextView) headView.findViewById(R.id.tv_focusnumme_main);
		tv_rank_me_main = (TextView) headView.findViewById(R.id.tv_rank_me_main1);
		tv_score_me_main = (TextView) headView.findViewById(R.id.tv_score_me_main);
		tv_topnub_me_main = (TextView) headView.findViewById(R.id.tv_topnub_me_main);
		tv_focusnumme_main = (TextView) headView.findViewById(R.id.tv_focusnumme_main);
		rl_focus_head = (RelativeLayout) headView.findViewById(R.id.rl_focus_head);
		tv_cmfans = (TextView) headView.findViewById(R.id.tv_fans_me_main);
		ll_myfan_cmain = (LinearLayout) headView.findViewById(R.id.ll_myfan_cmain);
		ll_myfan_cmain.setOnClickListener(this);
		ll_myfocus_cmain = (LinearLayout) headView.findViewById(R.id.ll_myfocus_cmain);
		ll_myfocus_cmain.setOnClickListener(this);
		im_rank_head = (ImageView) headView.findViewById(R.id.im_rank_head);
		// tv_addfocus = (TextView) headView.findViewById(R.id.tv_addfocus);
		im_focus_head = (ImageView) headView.findViewById(R.id.im_focus_head);
		if (myId != userId) {
			rl_focus_head.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isAction == 1) {
						cancelPeople();
					} else {
						addFocus();
					}

				}

			});
		} else {
			im_focus_head.setVisibility(View.INVISIBLE);
		}

	}

	private void cancelPeople() {
		RequestParams params = new RequestParams();
		params.put("friend.user.id", myId);
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
						// tv_addfocus.setText("关注");
						im_focus_head.setImageResource(R.drawable.newfocus);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void addFocus() {

		RequestParams params = new RequestParams();
		params.put("friend.user.id", myId);
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
						// tv_addfocus.setText("已关注");
						im_focus_head.setImageResource(R.drawable.newfocusde);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	// private String level;
	// private String userName;
	// private String userImg;
	// private int money;
	// private int score;
	// private int scoreRanking;
	// private int myActionCount;
	// private int openMake;
	// private int fansCount;
	// private int topCount;
	private void setHeadView() {
		tv_usernameme_main.setText("" + userName);
		tv_cmfans.setText("" + fansCount);
		tv_rank_me_main.setText("" + level);
		tv_score_me_main.setText("" + score);
		tv_topnub_me_main.setText("" + topCount);
		tv_focusnumme_main.setText("" + myActionCount);
		ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + userImg, im_head_me_main, options);
		if (isAction == 1) {
			// tv_addfocus.setText("已关注");
			im_focus_head.setImageResource(R.drawable.newfocusde);
		} else {
			// tv_addfocus.setText("关注");
			im_focus_head.setImageResource(R.drawable.newfocus);
		}
		if (level.contains("合一")) {
			im_rank_head.setBackgroundResource(R.drawable.heyi);
		} else if (level.contains("黑一")) {
			im_rank_head.setBackgroundResource(R.drawable.bl1);
		} else if (level.contains("黑二")) {
			im_rank_head.setBackgroundResource(R.drawable.bl2);
		} else if (level.contains("黑三")) {
			im_rank_head.setBackgroundResource(R.drawable.bl3);
		} else if (level.contains("灰一")) {
			im_rank_head.setBackgroundResource(R.drawable.g1);
		} else if (level.contains("灰二")) {
			im_rank_head.setBackgroundResource(R.drawable.g2);
		} else if (level.contains("灰三")) {
			im_rank_head.setBackgroundResource(R.drawable.g3);
		} else if (level.contains("白一")) {
			im_rank_head.setBackgroundResource(R.drawable.w1);
		} else if (level.contains("白二")) {
			im_rank_head.setBackgroundResource(R.drawable.w2);
		} else if (level.contains("白三")) {
			im_rank_head.setBackgroundResource(R.drawable.w3);
		}

	}

	private void getFCdata(String url, int pageIndex1, int pageSize) {
		RequestParams params = new RequestParams();
		params.put("userId", userId);
		params.put("beUserId", myId);
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMyFriendCircles, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					JSONArray jsonArray = response.getJSONArray("List");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String msg = j.optString("msg");
						String userName = j.optString("userName");
						String createDate = j.optString("createDate");
						String imgUrl = j.optString("imgUrl");
						int type = j.optInt("type");
						int friendId = j.optInt("friendId");
						int topCount = j.optInt("topCount");
						int shareCount = j.optInt("shareCount");
						int isCollection = j.optInt("isCollection");
						int isTop = j.optInt("isTop");
						int discussCount = j.optInt("discussCount");
						int userid = j.optInt("userId");
						List<Pics> pics = new ArrayList<Pics>();
						JSONArray jsonArray2 = j.getJSONArray("photoList");
						for (int k = 0; k < jsonArray2.length(); k++) {
							JSONObject jj = jsonArray2.getJSONObject(k);
							String imgUrll = jj.optString("imgUrl");
							String url = jj.optString("url");
							pics.add(new Pics(imgUrll, url));

						}
						size++;
						listItem.add(new ListItem(msg, imgUrl, userName, createDate, type, friendId, topCount, shareCount,
								isCollection, userid, isTop, discussCount, pics));

					}
					System.out.println("zuo==size=" + size);
					if (size == 10) {
						pageIndex++;
						isend = false;
					} else {
						isend = true;
					}
					size = 0;
					lvfcadatper.notifyDataSetChanged();
					isloading = false;
					// lv_fricir.onRefreshComplete();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	class MyLVFCAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listItem == null ? 0 : listItem.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listItem.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			final MyHolder h;
			if (v == null) {
				h = new MyHolder();
				v = View.inflate(Activity_CustomMain.this, R.layout.item_lv_post, null);
				h.im_head_fc = (CircleImageView) v.findViewById(R.id.im_head_fc);
				h.tv_msg_fc = (TextView) v.findViewById(R.id.tv_msg_fc);
				h.tv_time_fc = (TextView) v.findViewById(R.id.tv_time_fc);
				h.tv_title_fc = (TextView) v.findViewById(R.id.tv_title_fc);
				h.tv_discussfc = (TextView) v.findViewById(R.id.tv_discussfc1);
				h.tv_share_fc = (TextView) v.findViewById(R.id.tv_share_fc);
				h.tv_heart_fc = (TextView) v.findViewById(R.id.tv_heart_fc);
				h.tv_iscollect = (TextView) v.findViewById(R.id.tv_iscollect);
				h.gv_pic = (MyGridView) v.findViewById(R.id.gv_pic);
				h.rl_collect = (RelativeLayout) v.findViewById(R.id.rl_collect);
				h.im_prise = (ImageView) v.findViewById(R.id.im_prise);
				h.ll_allitem = (LinearLayout) v.findViewById(R.id.ll_allitem);
				h.rl_tochat = (RelativeLayout) v.findViewById(R.id.rl_tochat);
				h.ll_prise = (LinearLayout) v.findViewById(R.id.ll_prise);
				h.rl_share_post = (RelativeLayout) v.findViewById(R.id.rl_share_post);
				h.rl_commends = (RelativeLayout) v.findViewById(R.id.rl_commends);
				h.tv_delepost = (TextView) v.findViewById(R.id.tv_delepost);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}

			// Glide.with(getActivity()).load(AppFinalUrl.BASEURL +
			// listItem.get(position).imgUrl)
			// /*
			// * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			// */
			// .placeholder(R.drawable.tianc).crossFade().into(h.im_head_fc);
			if (myId == userId) {
				h.tv_delepost.setVisibility(View.VISIBLE);
				h.tv_iscollect.setVisibility(View.GONE);
				h.rl_collect.setVisibility(View.VISIBLE);
			} else {
				h.tv_delepost.setVisibility(View.GONE);
				h.tv_iscollect.setVisibility(View.VISIBLE);
			}

			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + listItem.get(position).imgUrl, h.im_head_fc,
					options);
			// if (listItem.get(position).pics.size() != 0) {
			// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
			// listItem.get(position).pics.get(0).imgUrl,
			// h.im_pic_fc, options);
			// }
			// h.tv_msg_fc.setText(new
			// String(Base64.decode(listItem.get(position).msg.getBytes(),
			// Base64.DEFAULT)));
			h.tv_msg_fc.setText(listItem.get(position).msg);
			// h.tv_msg_fc.setText(StringEscapeUtils.unescapeJava(listItem.get(position).msg));

			// byte b[] = android.util.Base64.decode(listItem.get(position).msg,
			// Base64.DEFAULT);
			// String string = new String(b);
			// String string = new
			// String(android.util.Base64.decode(listItem.get(position).msg,
			// Base64.DEFAULT));
			// h.tv_msg_fc.setText(string);
			// string = null;
			// String strBase64 = new
			// String(Base64.encode(listItem.get(position).msg.getBytes(),
			// Base64.DEFAULT));

			h.tv_time_fc.setText(listItem.get(position).createDate);
			h.tv_title_fc.setText(listItem.get(position).userName);
			h.tv_discussfc.setText("" + listItem.get(position).discussCount);
			h.tv_share_fc.setText("" + listItem.get(position).shareCount);
			h.tv_heart_fc.setText("" + listItem.get(position).topCount);
			h.ll_prise.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (listItem.get(position).isTop == 1) {
						cancleprise(listItem.get(position).friendId, type, position);
					} else {
						prise(listItem.get(position).friendId, type, position);
					}
				}
			});
			h.rl_share_post.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// showShare(String msg, int friendId, int type, int
					// position)
					// cfid, ctype, cp
					cfid = listItem.get(position).friendId;
					ctype = type;
					cp = position;
					cmsg = listItem.get(position).msg;
					if (listItem.get(position).pics.size() != 0) {
						savePic(listItem.get(position).pics.get(0).imgUrl);
					} else {
						showShare(listItem.get(position).msg, listItem.get(position).friendId, type, position);
					}

				}
			});
			if (listItem.get(position).isCollection != 1) {
				h.tv_iscollect.setText("收藏");
			} else {
				h.tv_iscollect.setText("已收藏");
			}
			if (listItem.get(position).isTop == 1) {

				h.im_prise.setImageResource(R.drawable.newpostheartde);
				// Drawable drawable = getResources()
				// .getDrawable(R.drawable.heart);
				// drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				// drawable.getMinimumHeight());
				// h.tv_heart_fc.setCompoundDrawables(drawable, null, null,
				// null);
			} else {
				h.im_prise.setImageResource(R.drawable.newpostheart);
				// Drawable drawable = getResources().getDrawable(
				// R.drawable.attention_big);
				// drawable.setBounds(0, 0, 0, 0);
				// h.tv_heart_fc.setCompoundDrawables(drawable, null, null,
				// null);
			}

			h.rl_collect.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (myId == userId) {
						System.out.println("zuo--点击position==" + position);
						showDelePostDia(listItem.get(position).friendId, position);
					} else {
						if (listItem.get(position).isCollection == 1) {
							cancelcollect(listItem.get(position).friendId, type, position);
						} else {
							collect(listItem.get(position).friendId, type, position);
						}

					}

				}

			});

			// change(new Oncollect() {
			//
			// @Override
			// public void setoncollect(int flag, int p) {
			// commendlvadapter.notifyDataSetChanged();
			// if (flag == 1 && p == position) {
			// h.tv_iscollect.setText("已收藏");
			// }
			// }
			// });
			adapter = new GVpicAdapter(listItem.get(position).pics);
			h.gv_pic.setAdapter(adapter);
			h.ll_allitem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Activity_CustomMain.this, Activity_PostDetails.class);
					intent.putExtra("msg", listItem.get(position).msg);
					intent.putExtra("flag", type);
					intent.putExtra("imgUrl", listItem.get(position).imgUrl);
					intent.putExtra("userName", listItem.get(position).userName);
					intent.putExtra("createDate", listItem.get(position).createDate);

					ArrayList<String> picsss = new ArrayList<String>();
					ArrayList<String> picurl = new ArrayList<String>();
					for (int i = 0; i < listItem.get(position).pics.size(); i++) {
						picurl.add(listItem.get(position).pics.get(i).url + "-small");
					}
					for (int i = 0; i < listItem.get(position).pics.size(); i++) {
						picsss.add(listItem.get(position).pics.get(i).url);
					}
					intent.putStringArrayListExtra("picurl", picurl);
					intent.putStringArrayListExtra("pics", picsss);
					System.out.println("zuo==flag=" + type);
					intent.putExtra("type", type);
					intent.putExtra("ffff", type);
					intent.putExtra("friendId", listItem.get(position).friendId);
					System.out.println("zuo==friendId=跳转前" + listItem.get(position).friendId);

					intent.putExtra("topCount", listItem.get(position).topCount);
					intent.putExtra("shareCount", listItem.get(position).shareCount);
					intent.putExtra("isCollection", listItem.get(position).isCollection);
					intent.putExtra("userId", listItem.get(position).userId);
					intent.putExtra("isTop", listItem.get(position).isTop);
					intent.putExtra("discussCount", listItem.get(position).discussCount);
					startActivity(intent);
				}
			});
			h.rl_commends.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Activity_CustomMain.this, Activity_PostDetails.class);
					intent.putExtra("msg", listItem.get(position).msg);
					intent.putExtra("flag", type);
					intent.putExtra("imgUrl", listItem.get(position).imgUrl);
					intent.putExtra("userName", listItem.get(position).userName);
					intent.putExtra("createDate", listItem.get(position).createDate);
					intent.putExtra("iscommends", 1);
					ArrayList<String> picsss = new ArrayList<String>();
					ArrayList<String> picurl = new ArrayList<String>();
					for (int i = 0; i < listItem.get(position).pics.size(); i++) {
						picurl.add(listItem.get(position).pics.get(i).url + "-small");
					}
					for (int i = 0; i < listItem.get(position).pics.size(); i++) {
						picsss.add(listItem.get(position).pics.get(i).url);
					}
					intent.putStringArrayListExtra("picurl", picurl);
					intent.putStringArrayListExtra("pics", picsss);
					System.out.println("zuo==flag=" + type);
					intent.putExtra("type", type);
					intent.putExtra("ffff", type);
					intent.putExtra("friendId", listItem.get(position).friendId);
					System.out.println("zuo==friendId=跳转前" + listItem.get(position).friendId);

					intent.putExtra("topCount", listItem.get(position).topCount);
					intent.putExtra("shareCount", listItem.get(position).shareCount);
					intent.putExtra("isCollection", listItem.get(position).isCollection);
					intent.putExtra("userId", listItem.get(position).userId);
					intent.putExtra("isTop", listItem.get(position).isTop);
					intent.putExtra("discussCount", listItem.get(position).discussCount);
					startActivity(intent);
				}
			});
			h.rl_tochat.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					startActivity(new Intent(Activity_CustomMain.this, ChatActivity.class).putExtra("name",
							listItem.get(position).userName).putExtra("userId", "" + listItem.get(position).userId));
				}
			});
			return v;
		}
	}

	class MyHolder {
		CircleImageView im_head_fc;
		TextView tv_title_fc, tv_msg_fc, tv_time_fc, tv_share_fc, tv_discussfc, tv_heart_fc, tv_iscollect, tv_delepost;
		ImageView im_pic_fc, im_prise;
		RelativeLayout rl_collect, rl_tochat, rl_share_post, rl_commends;
		MyGridView gv_pic;
		LinearLayout ll_allitem, ll_prise;
	}

	private void showDelePostDia(final int friendId, final int position) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(Activity_CustomMain.this);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				System.out.println("zuo--删除position==" + position);
				delePost(friendId, position);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).setMessage("确定删除该帖?").show();

	}

	private void delePost(int postId, final int position) {
		RequestParams params = new RequestParams();
		params.put("friendId", postId);
		System.out.println("zuo--position==" + position);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userdeleteInvitation, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_CustomMain.this, "删除成功", Toast.LENGTH_SHORT).show();
						System.out.println("zuo--remove =position==" + position);
						listItem.remove(position);
						lvfcadatper.notifyDataSetChanged();
					} else {
						Toast.makeText(Activity_CustomMain.this, "删除失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void prise(int friendId, int type, final int position) {
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
						Toast.makeText(Activity_CustomMain.this, "点赞成功", Toast.LENGTH_SHORT).show();
						listItem.get(position).isTop = 1;
						listItem.get(position).topCount = listItem.get(position).topCount + 1;
						lvfcadatper.notifyDataSetChanged();

					}
					if (result.equals("2")) {
						Toast.makeText(Activity_CustomMain.this, "已经赞过了", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void cancleprise(int friendId, int type, final int position) {
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
						Toast.makeText(Activity_CustomMain.this, "已取消赞", Toast.LENGTH_SHORT).show();
						listItem.get(position).isTop = 0;
						listItem.get(position).topCount = listItem.get(position).topCount - 1;
						lvfcadatper.notifyDataSetChanged();
						// tv_prise_postd.setCompoundDrawables(drawable,
						// null, null, null);
						// tv_prise_postd.setText("" + (--topCount));
						// isTop = 0;
					} else {
						Toast.makeText(Activity_CustomMain.this, "操作失败", Toast.LENGTH_SHORT).show();
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
						Toast.makeText(Activity_CustomMain.this, "收藏成功", Toast.LENGTH_SHORT).show();
						listItem.get(p).isCollection = 1;
						lvfcadatper.notifyDataSetChanged();
					}
					if (result.equals("1")) {
						Toast.makeText(Activity_CustomMain.this, "您已收藏", Toast.LENGTH_SHORT).show();

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void cancelcollect(int productid, int type, final int p) {
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
						Toast.makeText(Activity_CustomMain.this, "已取消收藏", Toast.LENGTH_SHORT).show();
						listItem.get(p).isCollection = 0;
						lvfcadatper.notifyDataSetChanged();
					}
					if (result.equals("1")) {
						Toast.makeText(Activity_CustomMain.this, "您已收藏", Toast.LENGTH_SHORT).show();

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void showShare(String msg, final int friendId, final int type, final int position) {
		ShareSDK.initSDK(this);
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
		oks.show(this);
		oks.setCallback(new PlatformActionListener() {

			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
				shareMsg(friendId, type, position);
				System.out.println("zuo==succ==" + arg1);
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				// TODO Auto-generated method stub

			}

		});
	}

	private File dir;
	FileOutputStream fos;
	private boolean isloading;

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

	private void shareMsg(int friendId, int type, final int position) {
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
						listItem.get(position).shareCount = listItem.get(position).shareCount + 1;
						lvfcadatper.notifyDataSetChanged();
						Toast.makeText(Activity_CustomMain.this, "分享成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(Activity_CustomMain.this, "操作失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	//
	// Oncollect co;
	//
	// public interface Oncollect {
	// public void setoncollect(int flag, int p);
	// }
	//
	// public void change(Oncollect oncollect) {
	// co = oncollect;
	// }
	class GVpicAdapter extends BaseAdapter {
		List<Pics> pp;

		public GVpicAdapter(List<Pics> pp) {
			super();
			this.pp = pp;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pp == null ? 0 : pp.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return pp.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			v = View.inflate(Activity_CustomMain.this, R.layout.item_image_post, null);
			ImageView im_commends = (ImageView) v.findViewById(R.id.im_post_pic);

			Glide.with(Activity_CustomMain.this).load(AppFinalUrl.photoBaseUri + pp.get(position).url + "-small")
			/*
			 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			 */
			.crossFade().into(im_commends);

			// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
			// pp.get(position).imgUrl, im_commends, options);
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) im_commends.getLayoutParams();
			params.width = w;
			params.height = w;
			im_commends.setLayoutParams(params);
			im_commends.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Activity_CustomMain.this, Activity_LookPic.class);
					ArrayList<String> dd = new ArrayList<String>();
					for (int i = 0; i < pp.size(); i++) {
						dd.add(pp.get(i).url);
					}
					intent.putExtra("position", position);
					intent.putStringArrayListExtra("pics", dd);
					startActivity(intent);
				}
			});
			return v;
		}
	}

	//
	// private void collect(int productid, int type, final int p) {
	// RequestParams params = new RequestParams();
	// params.put("collection.user.id", userId);
	// params.put("collection.friendCircle.id", productid);
	// params.put("collection.type", type);
	// MyApplication.ahc.post(AppFinalUrl.useraddCollection, params, new
	// JsonHttpResponseHandler() {
	// @Override
	// public void onSuccess(int statusCode, Header[] headers, JSONObject
	// response) {
	// // TODO Auto-generated method stub
	// super.onSuccess(statusCode, headers, response);
	// try {
	// String result = response.getString("result");
	//
	// if (result.equals("0")) {
	// Toast.makeText(getActivity(), "收藏成功", Toast.LENGTH_SHORT).show();
	// listItem.get(p).isCollection = 1;
	// lvfcadatper.notifyDataSetChanged();
	// }
	// if (result.equals("1")) {
	// Toast.makeText(getActivity(), "您已收藏", Toast.LENGTH_SHORT).show();
	//
	// }
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// });
	// }

	// private void cancelcollect(int productid, int type, final int p) {
	// RequestParams params = new RequestParams();
	// params.put("collection.user.id", userId);
	// params.put("collection.friendCircle.id", productid);
	// params.put("collection.type", type);
	// MyApplication.ahc.post(AppFinalUrl.usercancleCollection, params, new
	// JsonHttpResponseHandler() {
	// @Override
	// public void onSuccess(int statusCode, Header[] headers, JSONObject
	// response) {
	// // TODO Auto-generated method stub
	// super.onSuccess(statusCode, headers, response);
	// try {
	// String result = response.getString("result");
	//
	// if (result.equals("0")) {
	// Toast.makeText(getActivity(), "已取消收藏", Toast.LENGTH_SHORT).show();
	// listItem.get(p).isCollection = 0;
	// lvfcadatper.notifyDataSetChanged();
	// }
	// if (result.equals("1")) {
	// Toast.makeText(getActivity(), "您已收藏", Toast.LENGTH_SHORT).show();
	//
	// }
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// });
	// }
	class ListItem {
		String msg, imgUrl, userName, createDate;
		int type, friendId, topCount, shareCount, isCollection, userId, isTop, discussCount;
		List<Pics> pics;

		public ListItem(String msg, String imgUrl, String userName, String createDate, int type, int friendId, int topCount,
				int shareCount, int isCollection, int userId, int isTop, int discussCount, List<Pics> pics) {
			super();
			this.msg = msg;
			this.imgUrl = imgUrl;
			this.userName = userName;
			this.createDate = createDate;
			this.type = type;
			this.friendId = friendId;
			this.topCount = topCount;
			this.shareCount = shareCount;
			this.isCollection = isCollection;
			this.userId = userId;
			this.isTop = isTop;
			this.discussCount = discussCount;
			this.pics = pics;
		}

	}

	class Pics {
		String imgUrl, url;

		public Pics(String imgUrl, String url) {
			super();
			this.imgUrl = imgUrl;
			this.url = url;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		int lastItem = firstVisibleItem + visibleItemCount;
		if (lastItem == totalItemCount) {
			if (isend) {
				// sv_hot.onRefreshComplete();
				// Toast.makeText(getActivity(), "没有更多了",
				// Toast.LENGTH_SHORT).show();
			} else {
				// pics.clear();
				// vpData.clear();
				if (isloading) {

				} else {
					isloading = true;
					getFCdata("", pageIndex, lastItem);
				}

			}
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("设计师主页");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("设计师主页");
	}
}
