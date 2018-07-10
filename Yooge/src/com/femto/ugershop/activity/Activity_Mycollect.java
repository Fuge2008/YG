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
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.view.CircleImageView;
import com.femto.ugershop.view.MyGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Mycollect extends BaseActivity implements OnRefreshListener2<ListView> {
	private PullToRefreshListView lv_mycollect;
	private RelativeLayout rl_back_collect;
	private MyAdapter adapter;
	private DisplayImageOptions options;
	private int w;
	private int myId;
	private List<ListItem> listItem;
	private MyGVAdapter gvAdapter;
	int size = 0;
	private boolean islogin;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private int cfid;
	private int ctype;
	private int cp;
	private int type = 2;
	private String cmsg;
	private boolean isfirstbanner = true;
	private String videopath = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private File out;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				lv_mycollect.onRefreshComplete();
				break;
			case 2:
				showShare(cmsg, cfid, ctype, cp);
				break;
			default:
				break;
			}

		}

	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_collect:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		MyApplication.addActivity(this);
		listItem = new ArrayList<ListItem>();
		lv_mycollect = (PullToRefreshListView) findViewById(R.id.lv_mycollect);
		rl_back_collect = (RelativeLayout) findViewById(R.id.rl_back_collect);
		lv_mycollect.setOnRefreshListener(this);
		lv_mycollect.setMode(Mode.BOTH);
		adapter = new MyAdapter();
		lv_mycollect.setAdapter(adapter);
		getData(pageIndex, pageSize);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_collect.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_mycollect);
		initParams();
	}

	private void initParams() {
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.newlogyooge) // image连接地址为空时
				.showImageOnFail(R.drawable.newlogyooge) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		int dp2px = dp2px(this, 16);
		w = (screenWidth - dp2px) / 3;
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		myId = sp.getInt("userId", 0);
		islogin = sp.getBoolean("islogin", false);
		out = new File(videopath);
		if (!out.exists()) {
			out.mkdirs();
		}
		videopath = out.getPath();
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	private void getData(int pageIndex1, int pageSize) {
		System.out.println("zuo开始==");
		RequestParams params = new RequestParams();
		params.put("user.id", MyApplication.userId);
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);
		params.put("token", MyApplication.token);
		System.out.println("zuocollect=完成=");
		System.out.println("zuocollect==" + params.toString());
		showProgressDialog("加载中...");
		MyApplication.ahc.post(AppFinalUrl.usergetMyCollection, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuoresponse==" + response.toString());
				dismissProgressDialog();
				try {
					JSONArray jsonArray = response.getJSONArray("List");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String msg = j.optString("msg");
						String userName = j.optString("userName");
						String createDate = j.optString("createDate");
						String imgUrl = j.optString("imgUrl");
						String info = j.optString("info");
						String discussUserName = j.optString("discussUserName");
						String discussUserImg = j.optString("discussUserImg");
						int type = j.optInt("type");
						int friendId = j.optInt("friendId");
						int topCount = j.optInt("topCount");
						int shareCount = j.optInt("shareCount");
						int isCollection = j.optInt("isCollection");
						int isTop = j.optInt("isTop");
						int discussCount = j.optInt("discussCount");
						int userid = j.optInt("userId");
						int discussUserId = j.optInt("discussUserId");
						int collectionType = j.optInt("collectionType");
						int collectionId = j.optInt("collectionId");
						int userType = j.optInt("userType");
						List<Pics> pics = new ArrayList<Pics>();
						JSONArray jsonArray2 = j.getJSONArray("photoList");
						for (int k = 0; k < jsonArray2.length(); k++) {
							JSONObject jj = jsonArray2.getJSONObject(k);
							String imgUrll = jj.optString("imgUrl");
							String url = jj.optString("url");
							pics.add(new Pics(imgUrll, url));
						}

						listItem.add(new ListItem(msg, imgUrl, userName, createDate, discussUserImg, discussUserName, info, type,
								friendId, topCount, shareCount, isCollection, userid, isTop, discussCount, discussUserId,
								collectionType, collectionId, pics, userType));
						size++;
					}
					if (size == 10) {
						pageIndex++;
						isend = false;
					} else {
						isend = true;
					}
					size = 0;
					lv_mycollect.onRefreshComplete();
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	class MyAdapter extends BaseAdapter {

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
			// TODO Auto-generated method stub
			MyHolder h;
			if (v == null) {
				h = new MyHolder();
				v = View.inflate(Activity_Mycollect.this, R.layout.item_lv_prise, null);
				h.gv = (MyGridView) v.findViewById(R.id.gv_commends);
				h.tv_name_commends = (TextView) v.findViewById(R.id.tv_name_commends);
				h.tv_time = (TextView) v.findViewById(R.id.tv_time);
				h.tv_comcon = (TextView) v.findViewById(R.id.tv_comcon);
				h.tv_info = (TextView) v.findViewById(R.id.tv_info);
				h.tv_shareshu = (TextView) v.findViewById(R.id.tv_shareshu);
				h.tv_plshu = (TextView) v.findViewById(R.id.tv_plshu1);
				h.tv_zanshu = (TextView) v.findViewById(R.id.tv_zanshu);
				h.tv_newtime = (TextView) v.findViewById(R.id.tv_newtime);
				h.im_heart = (ImageView) v.findViewById(R.id.im_heart);
				h.ll_all = (LinearLayout) v.findViewById(R.id.ll_all);
				h.im_head_commends_pic = (CircleImageView) v.findViewById(R.id.im_head_commends_pic);
				h.ll_newprise = (LinearLayout) v.findViewById(R.id.ll_newprise);
				h.rl_share_message = (RelativeLayout) v.findViewById(R.id.rl_share_message);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_name_commends.setText("" + listItem.get(position).userName);
			h.tv_time.setText("删除");
			h.tv_time.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showdeletDialog(listItem.get(position).collectionId, position);
				}

			});
			h.tv_comcon.setVisibility(View.GONE);
			h.tv_shareshu.setText("" + listItem.get(position).shareCount);
			h.tv_plshu.setText("" + listItem.get(position).discussCount);
			h.tv_zanshu.setText("" + listItem.get(position).topCount);
			h.tv_info.setVisibility(View.VISIBLE);
			h.tv_info.setText("" + listItem.get(position).msg);
			h.tv_newtime.setText("" + listItem.get(position).createDate);
			// h.tv_info.setText(new
			// String(Base64.decode(listItem.get(position).msg.getBytes(),
			// Base64.DEFAULT)));
			h.ll_newprise.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (listItem.get(position).isTop == 1) {
						cancleprise(listItem.get(position).friendId, listItem.get(position).collectionType, position);
					} else {
						prise(listItem.get(position).friendId, listItem.get(position).collectionType, position);
					}
				}
			});
			h.rl_share_message.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					cfid = listItem.get(position).friendId;
					ctype = type;
					cp = position;
					cmsg = listItem.get(position).msg;
					if (listItem.get(position).pics.size() != 0) {
						savePic(listItem.get(position).pics.get(0).imgUrl);
					} else {
						showShare(listItem.get(position).msg, listItem.get(position).friendId,
								listItem.get(position).collectionType, position);
					}
				}
			});
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + listItem.get(position).imgUrl,
					h.im_head_commends_pic, options);
			System.out.println("zuohead+++" + listItem.get(position).imgUrl);
			if (listItem.get(position).isTop == 1) {
				h.im_heart.setImageResource(R.drawable.newpostheartde);
			} else {
				h.im_heart.setImageResource(R.drawable.newpostheart);
			}
			gvAdapter = new MyGVAdapter(listItem.get(position).pics);
			h.gv.setAdapter(gvAdapter);

			h.ll_all.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Activity_Mycollect.this, Activity_PostDetails.class);
					intent.putExtra("msg", listItem.get(position).msg);
					// intent.putExtra("flag", flag);
					intent.putExtra("imgUrl", listItem.get(position).imgUrl);
					System.out.println("zuo发送的+++" + listItem.get(position).imgUrl);
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
					intent.putStringArrayListExtra("pics", picsss);
					intent.putStringArrayListExtra("picurl", picurl);
					intent.putExtra("userType", listItem.get(position).userType);
					intent.putExtra("type", listItem.get(position).collectionType);
					intent.putExtra("friendId", listItem.get(position).friendId);
					intent.putExtra("topCount", listItem.get(position).topCount);
					intent.putExtra("shareCount", listItem.get(position).shareCount);
					intent.putExtra("isCollection", listItem.get(position).isCollection);
					intent.putExtra("userId", listItem.get(position).userId);
					intent.putExtra("isTop", listItem.get(position).isTop);
					intent.putExtra("discussCount", listItem.get(position).discussCount);
					startActivity(intent);
				}
			});
			return v;
		}
	}

	class MyHolder {
		MyGridView gv;
		CircleImageView im_head_commends_pic;
		TextView tv_name_commends, tv_time, tv_comcon, tv_info, tv_shareshu, tv_plshu, tv_zanshu, tv_newtime;
		ImageView im_heart;
		LinearLayout ll_all, ll_newprise;
		RelativeLayout rl_share_message;
	}

	private void showdeletDialog(final int id, final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Mycollect.this);

		builder.setTitle("提示").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				DeleCollect(id, position);
			}

		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).setMessage("确定删除此收藏?").show();
	}

	private void DeleCollect(int id, final int position) {
		RequestParams params = new RequestParams();
		params.put("collectionId", id);
		showProgressDialog("正在删除");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userdeleteMyCollection, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_Mycollect.this, "删除成功", Toast.LENGTH_SHORT).show();
						listItem.remove(position);
						adapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	class MyGVAdapter extends BaseAdapter {
		List<Pics> pics;

		public MyGVAdapter(List<Pics> pics) {
			super();
			this.pics = pics;
		}

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
			v = View.inflate(Activity_Mycollect.this, R.layout.item_image_post, null);
			ImageView im_commends = (ImageView) v.findViewById(R.id.im_post_pic);
			Glide.with(Activity_Mycollect.this).load(AppFinalUrl.photoBaseUri + pics.get(position).url + "-small").override(w, w)
					.centerCrop()
					/*
					 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
					 */
					.crossFade().into(im_commends);

			// ImageLoader.getInstance().displayImage(AppFinalUrl.BASEURL +
			// pics.get(position).imgUrl, im_commends, options);

			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) im_commends.getLayoutParams();
			params.width = w;
			params.height = w;
			im_commends.setLayoutParams(params);
			im_commends.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Activity_Mycollect.this, Activity_LookPic.class);
					ArrayList<String> dd = new ArrayList<String>();
					for (int i = 0; i < pics.size(); i++) {
						dd.add(pics.get(i).url);
					}
					intent.putExtra("position", position);
					intent.putStringArrayListExtra("pics", dd);
					startActivity(intent);
				}
			});
			return v;
		}
	}

	class ListItem {
		String msg, imgUrl, userName, createDate, discussUserImg, discussUserName, info;
		int type, friendId, topCount, shareCount, isCollection, userId, isTop, discussCount, discussUserId, collectionType,
				collectionId, userType;
		List<Pics> pics;

		public ListItem(String msg, String imgUrl, String userName, String createDate, String discussUserImg,
				String discussUserName, String info, int type, int friendId, int topCount, int shareCount, int isCollection,
				int userId, int isTop, int discussCount, int discussUserId, int collectionType, int collectionId,
				List<Pics> pics, int userType) {
			super();
			this.msg = msg;
			this.imgUrl = imgUrl;
			this.userName = userName;
			this.createDate = createDate;
			this.discussUserImg = discussUserImg;
			this.discussUserName = discussUserName;
			this.info = info;
			this.type = type;
			this.friendId = friendId;
			this.topCount = topCount;
			this.shareCount = shareCount;
			this.isCollection = isCollection;
			this.userId = userId;
			this.isTop = isTop;
			this.discussCount = discussCount;
			this.discussUserId = discussUserId;
			this.collectionType = collectionType;
			this.collectionId = collectionId;
			this.pics = pics;
			this.userType = userType;
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
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		listItem.clear();
		pageIndex = 1;
		getData(pageIndex, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (isend) {
			handler.sendEmptyMessage(1);
			Toast.makeText(this, "没有更多了", Toast.LENGTH_SHORT).show();
		} else {
			getData(pageIndex, pageSize);
		}
	}

	public void prise(int friendId, int type, final int position) {
		RequestParams params = new RequestParams();
		params.put("friendId", friendId);
		params.put("userId", myId);
		params.put("type", type);
		System.out.println("zuoprise==" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.useraddTopForFriend, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_Mycollect.this, "点赞成功", Toast.LENGTH_SHORT).show();
						listItem.get(position).isTop = 1;
						listItem.get(position).topCount = listItem.get(position).topCount + 1;
						adapter.notifyDataSetChanged();

					}
					if (result.equals("2")) {
						Toast.makeText(Activity_Mycollect.this, "已经赞过了", Toast.LENGTH_SHORT).show();
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
		System.out.println("zuocancleprise==" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usercancleTop, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_Mycollect.this, "已取消赞", Toast.LENGTH_SHORT).show();
						listItem.get(position).isTop = 0;
						listItem.get(position).topCount = listItem.get(position).topCount - 1;
						adapter.notifyDataSetChanged();
						// tv_prise_postd.setCompoundDrawables(drawable,
						// null, null, null);
						// tv_prise_postd.setText("" + (--topCount));
						// isTop = 0;
					} else {
						Toast.makeText(Activity_Mycollect.this, "操作失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private File dir;
	FileOutputStream fos;
	private FragmentTransaction transaction;

	private void savePic(String urlPath) {
		dir = new File(videopath, "showpic.jpg");
		try {
			fos = new FileOutputStream(dir);
			showProgressDialog("分享中...");
			MyApplication.ahc.post(AppFinalUrl.BASEURL + urlPath, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] data) {
					// TODO Auto-generated method stub
					dismissProgressDialog();
					try {
						fos.write(data);
						fos.flush();
						fos.close();
						// String str = new String(data);
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
				String result;
				try {
					result = response.getString("result");
					if (result.equals("0")) {
						listItem.get(position).shareCount = listItem.get(position).shareCount + 1;
						adapter.notifyDataSetChanged();
						Toast.makeText(Activity_Mycollect.this, "分享成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(Activity_Mycollect.this, "操作失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	private void showShare(String msg, final int friendId, final int type, final int position) {
		ShareSDK.initSDK(Activity_Mycollect.this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("来自优格");
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
		oks.show(Activity_Mycollect.this);
		oks.setCallback(new PlatformActionListener() {

			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
				shareMsg(friendId, type, position);
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {

			}
		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("我的收藏");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("我的收藏");
	}
}
