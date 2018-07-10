package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.chatuidemo.activity.ChatActivity;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.entity.Commends;
import com.femto.ugershop.view.CircleImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Commends_GoodeDetails extends SwipeBackActivity implements OnRefreshListener2<ListView> {
	private RelativeLayout rl_back_comment_goods;
	private PullToRefreshListView lv_commends;
	private List<Commends> commends;
	private boolean islogin;
	private int myId;
	private DisplayImageOptions options;
	private int productId;
	private MyAdapter adapter;
	int size = 0;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				lv_commends.onRefreshComplete();
				break;

			default:
				break;
			}
		};
	};
	private int flag;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_comment_goods:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		commends = new ArrayList<Commends>();
		rl_back_comment_goods = (RelativeLayout) findViewById(R.id.rl_back_comment_goods);
		lv_commends = (PullToRefreshListView) findViewById(R.id.lv_commends);
		lv_commends.setOnRefreshListener(this);
		lv_commends.setMode(Mode.BOTH);
		adapter = new MyAdapter();
		MyApplication.addActivity(this);
		lv_commends.setAdapter(adapter);
		showProgressDialog("加载中...");
		getData(pageIndex, pageSize);
	}

	private void getData(int pageIndex1, int pageSize1) {
		RequestParams params = new RequestParams();
		params.put("product.id", productId);
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize1);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(url, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo===" + response.toString());
				dismissProgressDialog();
				try {
					JSONArray jsonArray = response.getJSONArray("dicussList");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String content = j.optString("content");
						String userName = j.optString("userName");
						String createDate = j.optString("createDate");
						int dicussId = j.optInt("dicussId");
						int userId = j.optInt("userId");
						String url = j.optString("url");
						commends.add(new Commends(content, userName, createDate, url, dicussId, userId));
						size++;
					}
					if (size == 10) {
						pageIndex++;
						isend = false;
					} else {
						isend = true;
					}
					size = 0;
					lv_commends.onRefreshComplete();
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_comment_goods.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_commends_goodsdetails);
		initParams();
	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		islogin = sp.getBoolean("islogin", false);
		myId = sp.getInt("userId", 0);
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.person) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		productId = getIntent().getIntExtra("productId", 0);
		flag = getIntent().getIntExtra("flag", 0);
		if (flag == 1) {
			url = AppFinalUrl.usergetDiscusssByMakeProductId;
		}
	}

	private String url = AppFinalUrl.usergetDiscusssByProductId;

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return commends == null ? 0 : commends.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return commends.get(position);
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
				v = View.inflate(Activity_Commends_GoodeDetails.this, R.layout.item_commends, null);
				h.im_head_commends_goods = (CircleImageView) v.findViewById(R.id.im_head_commends_goods);
				h.tv_content = (TextView) v.findViewById(R.id.tv_content);
				h.tv_time_commends = (TextView) v.findViewById(R.id.tv_time_commends1);
				h.tv_name_commends_goods = (TextView) v.findViewById(R.id.tv_name_commends_goods);
				h.tv_delete_content = (TextView) v.findViewById(R.id.tv_delete_content);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.im_head_commends_goods.setVisibility(View.VISIBLE);
			h.tv_content.setText("" + commends.get(position).getContent());
			h.tv_time_commends.setText("" + commends.get(position).getCreateDate());
			h.tv_name_commends_goods.setText("" + commends.get(position).getUserName());
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + commends.get(position).getUrl(),
					h.im_head_commends_goods, options);
			if (commends.get(position).getUserId() == MyApplication.userId) {
				h.tv_delete_content.setVisibility(View.VISIBLE);
			} else {
				h.tv_delete_content.setVisibility(View.GONE);
			}
			h.tv_delete_content.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					deleCommends(position, commends.get(position).getDicussId());
				}
			});
			h.im_head_commends_goods.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					startActivity(new Intent(Activity_Commends_GoodeDetails.this, ChatActivity.class).putExtra("name",
							commends.get(position).getUserName()).putExtra("userId", "" + commends.get(position).getUserId()));
				}
			});
			return v;
		}
	}

	class MyHolder {
		TextView tv_content, tv_time_commends, tv_name_commends_goods, tv_delete_content;
		CircleImageView im_head_commends_goods;
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
					commends.remove(position);
					adapter.notifyDataSetChanged();
				} else {
					showToast("删除失败", 0);
				}
			}
		});
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		pageIndex = 1;
		commends.clear();
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

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("商品评论");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("商品评论");
	}
}
