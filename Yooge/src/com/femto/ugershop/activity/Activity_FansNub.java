package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.easemob.chatuidemo.activity.ChatActivity;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.entity.Fans;
import com.femto.ugershop.view.CircleImageView;
import com.femto.ugershop.view.SortModel;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_FansNub extends SwipeBackActivity implements OnRefreshListener2<ListView>, OnItemClickListener {
	private RelativeLayout rl_back_fansnub;
	private PullToRefreshListView lv_fansnub;
	private int size = 0;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private int userId;
	private List<Fans> fans;
	private DisplayImageOptions options;
	private MyAdapter adapter;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				lv_fansnub.onRefreshComplete();
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_fansnub:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back_fansnub = (RelativeLayout) findViewById(R.id.rl_back_fansnub);
		lv_fansnub = (PullToRefreshListView) findViewById(R.id.lv_fansnub);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_fansnub.setOnClickListener(this);
		lv_fansnub.setOnRefreshListener(this);
		lv_fansnub.setOnItemClickListener(this);
		lv_fansnub.setMode(Mode.BOTH);
		fans = new ArrayList<Fans>();
		adapter = new MyAdapter();
		lv_fansnub.setAdapter(adapter);
		getData(1, 10);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_fansnub);
		initParams();
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		MyApplication.addActivity(this);
	}

	private void initParams() {
		// TODO Auto-generated method stub
		userId = getIntent().getIntExtra("userId", 0);
	}

	private void getData(int i, int p) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("userId", userId);
		params.put("pageModel.pageIndex", i);
		params.put("pageModel.pageSize", p);
		showProgressDialog("加载中...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMyFans, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo---" + response.toString());
				dismissProgressDialog();
				JSONArray jsonArray = response.optJSONArray("Firends");
				if (jsonArray != null) {
					for (int j = 0; j < jsonArray.length(); j++) {
						JSONObject jj = jsonArray.optJSONObject(j);
						String imgUrl = jj.optString("imgUrl");
						String userName = jj.optString("userName");
						String info = jj.optString("info");
						String name = jj.optString("name");
						int userId = jj.optInt("userId");
						fans.add(new Fans(imgUrl, userName, info, name, userId));
						size++;
					}
					if (size == 10) {
						pageIndex++;
						isend = false;
					} else {
						isend = true;
					}
					size = 0;
					lv_fansnub.onRefreshComplete();
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fans == null ? 0 : fans.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return fans.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;

			if (v == null) {
				viewHolder = new ViewHolder();
				v = View.inflate(Activity_FansNub.this, R.layout.item, null);
				viewHolder.tvTitle = (TextView) v.findViewById(R.id.tv_myname_me);
				viewHolder.cirim = (CircleImageView) v.findViewById(R.id.vim_head);
				viewHolder.tvLetter = (TextView) v.findViewById(R.id.catalog);
				viewHolder.tv_myinfo_me = (TextView) v.findViewById(R.id.tv_myinfo_me);
				v.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) v.getTag();
			}

			viewHolder.tvLetter.setVisibility(View.GONE);

			viewHolder.tvTitle.setText(fans.get(position).getUserName());
			viewHolder.tv_myinfo_me.setText(fans.get(position).getInfo());
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + fans.get(position).getImgUrl(), viewHolder.cirim,
					options);
			return v;
		}
	}

	class ViewHolder {
		TextView tvLetter, tv_myinfo_me;
		TextView tvTitle;
		CircleImageView cirim;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		pageIndex = 1;
		fans.clear();
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		position = position - 1;
		startActivity(new Intent(this, ChatActivity.class).putExtra("name", fans.get(position).getUserName()).putExtra("userId",
				"" + fans.get(position).getUserId()));
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("我的粉丝");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("我的粉丝");
	}
}
