package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.chatuidemo.activity.ChatActivity;
import com.femto.hx.adapter.SortAdapter;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.easemob.applib.domain.ContactsList;
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
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_CommunityFindRank extends SwipeBackActivity implements OnItemClickListener, OnRefreshListener2<ListView> {

	private RelativeLayout rl_back;
	private PullToRefreshListView lv_people;
	private TextView tv_title;
	private int myId;
	private List<ContactsList> contans;
	private MyAdater adapter;
	private DisplayImageOptions options;
	private String url = AppFinalUrl.getFamousPersonRank;;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private int size = 0;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				// lv_fricir.onRefreshComplete();
				lv_people.onRefreshComplete();
				break;

			default:
				break;
			}

		};
	};

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_communityfindrank);
		MyApplication.addActivity(this);

		SharedPreferences sp = getSharedPreferences("Login", Activity_CommunityFindRank.MODE_PRIVATE);
		myId = sp.getInt("userId", -1);
		contans = new ArrayList<ContactsList>();

		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back = (RelativeLayout) findViewById(R.id.rl_back);
		lv_people = (PullToRefreshListView) findViewById(R.id.lv_people);
		tv_title = (TextView) findViewById(R.id.tv_title);
		rl_back.setOnClickListener(this);
		tv_title.setOnClickListener(this);
		lv_people.setOnItemClickListener(this);
		lv_people.setOnRefreshListener(this);
		lv_people.setMode(Mode.BOTH);
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		String type = intent.getStringExtra("type");
		if (type.equals("1")) {
			url = AppFinalUrl.getFamousPersonRank;
		} else {
			url = AppFinalUrl.getVigourPersonRank;
		}
		tv_title.setText(title);
		adapter = new MyAdater();
		lv_people.setAdapter(adapter);
		getData(pageIndex, pageSize);
	}

	private void getData(int pageIndex1, int pageSize1) {
		showProgressDialog("正在加载...");
		RequestParams params = new RequestParams();
		params.put("userId", myId);
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize1);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(url, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo---活力帮==" + response.toString());
				lv_people.onRefreshComplete();
				try {
					JSONArray jsonArray = response.getJSONArray("List");
					ContactsList c;
					for (int i = 0; i < jsonArray.length(); i++) {
						c = new ContactsList();
						JSONObject j = jsonArray.getJSONObject(i);
						c.setImgUrl(j.optString("imgUrl"));
						c.setName(j.optString("name"));
						c.setUserId(j.optInt("userId"));
						c.setType(j.optInt("type"));
						c.setUserName(j.optString("userName"));
						c.setInfo(j.optString("info"));
						contans.add(c);
						size++;
					}
					if (size == 10) {
						pageIndex++;
						isend = false;
					} else {
						isend = true;
					}
					size = 0;
					adapter.notifyDataSetChanged();
					dismissProgressDialog();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.rl_back:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub

	}

	class MyAdater extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return contans.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return contans.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			final ContactsList mContent = (ContactsList) getItem(arg0);
			if (view == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(Activity_CommunityFindRank.this).inflate(R.layout.item_recommand, null);
				viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_myname_me);
				viewHolder.cirim = (CircleImageView) view.findViewById(R.id.vim_head);
				viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
				viewHolder.tv_myinfo_me = (TextView) view.findViewById(R.id.tv_myinfo_me);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			viewHolder.tvTitle.setText(mContent.getUserName());
			viewHolder.tv_myinfo_me.setText("" + mContent.getInfo());
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + mContent.getImgUrl(), viewHolder.cirim, options);
			return view;

		}

		final class ViewHolder {
			TextView tvLetter, tv_myinfo_me;
			TextView tvTitle;
			CircleImageView cirim;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		if (contans.get(position - 1).getType() == 2) {
			Intent intent_tomain = new Intent(this, Activity_CustomMain.class);
			intent_tomain.putExtra("userId", contans.get(position - 1).getUserId());
			startActivity(intent_tomain);
		} else {
			Intent intent_entrymain = new Intent(this, Activity_Designer.class);
			intent_entrymain.putExtra("userId", contans.get(position - 1).getUserId());
			startActivity(intent_entrymain);
		}
		// startActivity(new Intent(Activity_CommunityFindRank.this,
		// ChatActivity.class).putExtra("name",
		// contans.get(position).getUserName()).putExtra("userId", "" +
		// contans.get(position - 1).getUserId()));
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		contans.clear();
		pageIndex = 1;
		getData(pageIndex, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (isend) {
			Toast.makeText(Activity_CommunityFindRank.this, "没有更多", Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(1);
		} else {
			getData(pageIndex, pageSize);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("名人榜");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("名人榜");
	}
}
