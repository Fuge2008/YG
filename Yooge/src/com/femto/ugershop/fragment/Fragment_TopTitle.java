package com.femto.ugershop.fragment;

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

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.twitter.Twitter;

import com.bumptech.glide.Glide;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_CustomMain;
import com.femto.ugershop.activity.Activity_Designer;
import com.femto.ugershop.activity.Activity_Login;
import com.femto.ugershop.activity.Activity_LookPic;
import com.femto.ugershop.activity.Activity_PostDetails;
import com.femto.ugershop.activity.Activity_UpLoadPost;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.fragment.Fragment_YoogeRecommend.Pics;
import com.femto.ugershop.view.CircleImageView;
import com.femto.ugershop.view.MyGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author 作者Deep:
 * @version 创建时间：2015年10月19日 上午9:09:55 类说明
 */
public class Fragment_TopTitle extends BaseFragment implements OnClickListener, OnRefreshListener2<ListView> {
	private View view;
	private PullToRefreshListView lv_fricir;
	private MyLVFCAdapter lvfcadatper;
	private LinearLayout ll_recommend, ll_boutique;
	private MyCommendLVAdapter commendlvadapter;
	private int userId;
	private List<ListItem> listItem;
	private DisplayImageOptions options;
	private ProgressDialog pdd;
	private int flag = 4;
	GVpicAdapter adapter;
	private String cmsg;
	private int w;
	private int type = 4;
	private int ffff = 4;
	// flag = 2;
	// type = 2;
	// ffff = 1;
	private String url = AppFinalUrl.usergetPush;;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	int size = 0;
	// int friendId, int type, final int position
	private int cfid, ctype, cp;
	private MBC mbc;
	private String videopath = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				lv_fricir.onRefreshComplete();
				break;
			case 2:
				showShare(cmsg, cfid, ctype, cp);
				break;

			default:
				break;
			}

		};
	};
	private File out;
	private Boolean islogin;
	private ImageView im_totop_fricir;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_friendcircle, container, false);
		listItem = new ArrayList<ListItem>();
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		regisMBC();
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

	private void regisMBC() {
		// TODO Auto-generated method stub
		mbc = new MBC();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.refresh.post");
		getActivity().registerReceiver(mbc, filter);
	}

	class MBC extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if ("com.refresh.post".equals(action)) {
				listItem.clear();
				pageIndex = 1;
				getFCdata(url, pageIndex, pageSize);
			}

		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(mbc);
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	private void initParams() {
		SharedPreferences sp = getActivity().getSharedPreferences("Login", getActivity().MODE_PRIVATE);
		userId = sp.getInt("userId", -1);

		islogin = sp.getBoolean("islogin", false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.im_totop_fricir:
			if (listItem.size() != 0) {
				lv_fricir.getRefreshableView().setSelection(1);
			}

			// lv_fricir.getRefreshableView().setSelectionFromTop(0, 0);
			// lv_fricir.getRefreshableView().smoothScrollToPosition(0);

			break;
		default:
			break;
		}

	}

	private void initView(View v) {
		// TODO Auto-generated method stub
		lv_fricir = (PullToRefreshListView) v.findViewById(R.id.lv_fricir);
		im_totop_fricir = (ImageView) v.findViewById(R.id.im_totop_fricir);
		im_totop_fricir.setOnClickListener(this);
		lvfcadatper = new MyLVFCAdapter();
		lv_fricir.setAdapter(lvfcadatper);
		lv_fricir.setOnRefreshListener(this);
		lv_fricir.setMode(Mode.BOTH);

	}

	private void getFCdata(String url, int pageIndex1, int pageSize) {
		RequestParams params = new RequestParams();
		params.put("type", 7);
		params.put("userId", userId);
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);

		System.out.println("zuo====params==" + params.toString());
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(url, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				lv_fricir.onRefreshComplete();
				System.out.println("zuo====params==" + response.toString());
				try {
					JSONArray jsonArray = response.getJSONArray("List");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String msg = j.optString("msg");
						String userName = j.optString("userName");
						String createDate = j.optString("createDate");
						String imgUrl = j.optString("imgUrl");
						int type = j.optInt("type");
						int userType = j.optInt("userType");
						int friendId = j.optInt("friendId");
						int topCount = j.optInt("topCount");
						int shareCount = j.optInt("shareCount");
						int isCollection = j.optInt("isCollection");
						int isTop = j.optInt("isTop");
						int discussCount = j.optInt("discussCount");
						int isAction = j.optInt("isAction");
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
								isCollection, userid, isTop, discussCount, pics, userType, isAction));
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
		params.put("user.id", userId);
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
						Toast.makeText(getActivity(), "分享成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
					}
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
				v = View.inflate(getActivity(), R.layout.item_lv_post, null);
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
				h.im_focus_post = (ImageView) v.findViewById(R.id.im_focus_post);
				h.ll_allitem = (LinearLayout) v.findViewById(R.id.ll_allitem);
				h.rl_tochat = (RelativeLayout) v.findViewById(R.id.rl_tochat);
				h.ll_prise = (LinearLayout) v.findViewById(R.id.ll_prise);
				h.rl_share_post = (RelativeLayout) v.findViewById(R.id.rl_share_post);
				h.rl_commends = (RelativeLayout) v.findViewById(R.id.rl_commends);
				h.rl_newfocus = (RelativeLayout) v.findViewById(R.id.rl_newfocus);
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

			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + listItem.get(position).imgUrl, h.im_head_fc,
					options);
			h.im_head_fc.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (listItem.get(position).userType == 1) {
						Intent intent = new Intent(getActivity(), Activity_Designer.class);
						intent.putExtra("userId", listItem.get(position).userId);
						startActivity(intent);
					} else if (listItem.get(position).userType == 2) {
						Intent intent = new Intent(getActivity(), Activity_CustomMain.class);
						intent.putExtra("userId", listItem.get(position).userId);
						startActivity(intent);
					}
					// Intent intent = new Intent(getActivity(),
					// Activity_CustomMain.class);
					// intent.putExtra("userId", listItem.get(position).userId);
					// startActivity(intent);
				}
			});
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

				}
			});
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

					// try {
					// if (listItem.get(position).pics.size() == 0) {
					// showShare(listItem.get(position).msg,
					// listItem.get(position).friendId, type, position);
					// } else {
					// savePic(listItem.get(position).pics.get(0).imgUrl);
					// }
					//
					// } catch (IOException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }

				}
			});
			if (userId == listItem.get(position).userId) {
				h.tv_iscollect.setText("删除");
			} else {
				if (listItem.get(position).isCollection != 1) {
					h.tv_iscollect.setText("收藏");
				} else {
					h.tv_iscollect.setText("已收藏");
				}
			}

			if (listItem.get(position).isAction == 1) {
				h.im_focus_post.setImageResource(R.drawable.newfocusde);
			} else {
				h.im_focus_post.setImageResource(R.drawable.newfocus);
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
			h.rl_collect.setVisibility(View.GONE);
			h.rl_collect.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (userId == listItem.get(position).userId) {
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
			h.rl_newfocus.setVisibility(View.VISIBLE);
			h.rl_newfocus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (!islogin) {
						Intent intent = new Intent(getActivity(), Activity_Login.class);
						startActivity(intent);
						return;
					}

					if (listItem.get(position).isAction == 1) {
						cancelPeople(listItem.get(position).userId, h.im_focus_post, position);
					} else {
						focusPeople(listItem.get(position).userId, h.im_focus_post, position);
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
					Intent intent = new Intent(getActivity(), Activity_PostDetails.class);
					intent.putExtra("msg", listItem.get(position).msg);
					intent.putExtra("flag", flag);
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
					System.out.println("zuo==flag=" + flag);
					intent.putExtra("type", type);
					intent.putExtra("userType", listItem.get(position).userType);
					intent.putExtra("ffff", ffff);
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
					Intent intent = new Intent(getActivity(), Activity_PostDetails.class);
					intent.putExtra("msg", listItem.get(position).msg);
					intent.putExtra("flag", flag);
					intent.putExtra("imgUrl", listItem.get(position).imgUrl);
					intent.putExtra("userName", listItem.get(position).userName);
					intent.putExtra("createDate", listItem.get(position).createDate);
					intent.putExtra("iscommends", 1);
					intent.putExtra("userType", listItem.get(position).userType);
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
					System.out.println("zuo==flag=" + flag);
					intent.putExtra("type", type);
					intent.putExtra("ffff", ffff);
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
					startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("name", listItem.get(position).userName)
							.putExtra("userId", "" + listItem.get(position).userId));
				}
			});
			return v;
		}
	}

	class MyHolder {
		CircleImageView im_head_fc;
		TextView tv_title_fc, tv_msg_fc, tv_time_fc, tv_share_fc, tv_discussfc, tv_heart_fc, tv_iscollect;
		ImageView im_pic_fc, im_prise, im_focus_post;
		RelativeLayout rl_collect, rl_tochat, rl_share_post, rl_commends, rl_newfocus;
		MyGridView gv_pic;
		LinearLayout ll_allitem, ll_prise;
	}

	public void prise(int friendId, int type, final int position) {
		RequestParams params = new RequestParams();
		params.put("friendId", friendId);
		params.put("userId", userId);
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
						Toast.makeText(getActivity(), "点赞成功", Toast.LENGTH_SHORT).show();
						listItem.get(position).isTop = 1;
						listItem.get(position).topCount = listItem.get(position).topCount + 1;
						lvfcadatper.notifyDataSetChanged();

					}
					if (result.equals("2")) {
						Toast.makeText(getActivity(), "已经赞过了", Toast.LENGTH_SHORT).show();
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
		params.put("userId", userId);
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
						Toast.makeText(getActivity(), "已取消赞", Toast.LENGTH_SHORT).show();
						listItem.get(position).isTop = 0;
						listItem.get(position).topCount = listItem.get(position).topCount - 1;
						lvfcadatper.notifyDataSetChanged();
						// tv_prise_postd.setCompoundDrawables(drawable,
						// null, null, null);
						// tv_prise_postd.setText("" + (--topCount));
						// isTop = 0;
					} else {
						Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void showShare(String msg, final int friendId, final int type, final int position) {
		ShareSDK.initSDK(getActivity());
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
		oks.show(getActivity());
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
			v = View.inflate(getActivity(), R.layout.item_image_post, null);
			ImageView im_commends = (ImageView) v.findViewById(R.id.im_post_pic);

			Glide.with(getActivity()).load(AppFinalUrl.photoBaseUri + pp.get(position).url + "-small")
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
					Intent intent = new Intent(getActivity(), Activity_LookPic.class);
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

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden && listItem.size() == 0) {
			pageIndex = 1;
			showProgressDialog("加载中...");
			getFCdata(url, pageIndex, pageSize);
		}
	}

	private void collect(int productid, int type, final int p) {
		RequestParams params = new RequestParams();
		params.put("collection.user.id", userId);
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
						Toast.makeText(getActivity(), "收藏成功", Toast.LENGTH_SHORT).show();
						listItem.get(p).isCollection = 1;
						lvfcadatper.notifyDataSetChanged();
					}
					if (result.equals("1")) {
						Toast.makeText(getActivity(), "您已收藏", Toast.LENGTH_SHORT).show();

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
		params.put("collection.user.id", userId);
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
						Toast.makeText(getActivity(), "已取消收藏", Toast.LENGTH_SHORT).show();
						listItem.get(p).isCollection = 0;
						lvfcadatper.notifyDataSetChanged();
					}
					if (result.equals("1")) {
						Toast.makeText(getActivity(), "您已收藏", Toast.LENGTH_SHORT).show();

					}
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

	class MyCommendLVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 10;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			v = View.inflate(getActivity(), R.layout.item_lv_product, null);
			return v;
		}
	}

	public void showorhint(LinearLayout ll_show, LinearLayout ll1, LinearLayout ll2) {
		ll_show.setVisibility(View.VISIBLE);
		ll1.setVisibility(View.GONE);
		ll2.setVisibility(View.GONE);
	}

	class ListItem {
		String msg, imgUrl, userName, createDate;
		int type, friendId, topCount, shareCount, isCollection, userId, isTop, discussCount, userType, isAction;
		List<Pics> pics;

		public ListItem(String msg, String imgUrl, String userName, String createDate, int type, int friendId, int topCount,
				int shareCount, int isCollection, int userId, int isTop, int discussCount, List<Pics> pics, int userType,
				int action) {
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
			this.userType = userType;
			this.isAction = action;
		}

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		listItem.clear();
		pageIndex = 1;
		getFCdata(url, pageIndex, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (isend) {
			Toast.makeText(getActivity(), "没有更多", Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(1);
		} else {
			getFCdata(url, pageIndex, pageSize);
		}
	}

	private void showDelePostDia(final int friendId, final int position) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
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
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userdeleteInvitation, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
						listItem.remove(position);
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	// 关注
	private void focusPeople(int beuserid, final ImageView view, final int position) {
		RequestParams params = new RequestParams();
		params.put("friend.user.id", userId);
		params.put("friend.beuser.id", beuserid);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.useraddFocus, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						listItem.get(position).isAction = 1;
						view.setImageResource(R.drawable.newfocusde);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	// 取消关注
	private void cancelPeople(int beuserid, final ImageView view, final int position) {
		RequestParams params = new RequestParams();
		params.put("friend.user.id", userId);
		params.put("friend.beuser.id", beuserid);
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
						listItem.get(position).isAction = 0;
						view.setImageResource(R.drawable.newfocus);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
