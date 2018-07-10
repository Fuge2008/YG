package com.femto.ugershop.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.entity.PhotoList;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
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

/**
 * @author 作者Deep:
 * @version 创建时间：2015年10月10日 上午9:56:20 类说明
 */
public class Activity_List extends BaseActivity implements OnRefreshListener2<ListView>, OnItemClickListener {
	private RelativeLayout rl_back_list;
	private PullToRefreshListView plv_activitylist;
	private MyAdapter adapter;
	private List<ActivityList> alist;
	private int type;
	int size = 0;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				plv_activitylist.onRefreshComplete();
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
		case R.id.rl_back_list:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		alist = new ArrayList<ActivityList>();
		rl_back_list = (RelativeLayout) findViewById(R.id.rl_back_list);
		plv_activitylist = (PullToRefreshListView) findViewById(R.id.plv_activitylist);
		plv_activitylist.setMode(Mode.BOTH);
		adapter = new MyAdapter();
		plv_activitylist.setAdapter(adapter);
		getdata(1, 10);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_list.setOnClickListener(this);
		plv_activitylist.setOnRefreshListener(this);
		plv_activitylist.setOnItemClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_list);
		initParams();
		MyApplication.addActivity(this);
	}

	private void initParams() {
		type = getIntent().getIntExtra("type", 0);

	}

	private void getdata(int pageIndex1, int pageSize) {
		RequestParams params = new RequestParams();
		params.put("type", type);
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetPartListByUserType, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo" + response.toString());
				try {
					JSONArray jsonArray = response.getJSONArray("List");

					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.optJSONObject(i);
						String title = j.optString("title");
						String createDate = j.optString("createDate");
						String info = j.optString("info");

						JSONArray optJSONArray = j.optJSONArray("PhotoList");
						List<PhotoList> photoList = new ArrayList<PhotoList>();
						for (int k = 0; k < optJSONArray.length(); k++) {
							JSONObject jj = optJSONArray.getJSONObject(k);
							String photoUrl = jj.optString("photoUrl");
							String high = jj.optString("high");
							String width = jj.optString("width");
							photoList.add(new PhotoList(photoUrl, high, width));
						}
						alist.add(new ActivityList(title, createDate, info, photoList));
						size++;
					}
					if (size == 10) {
						pageIndex++;
						isend = false;
					} else {
						isend = true;
					}
					size = 0;
					plv_activitylist.onRefreshComplete();
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
			return alist == null ? 0 : alist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return alist.get(position);
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
				v = View.inflate(Activity_List.this, R.layout.item_alist, null);
				h.tv_atime = (TextView) v.findViewById(R.id.tv_atime);
				h.tv_atitle = (TextView) v.findViewById(R.id.tv_atitle);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_atime.setText("" + alist.get(position).createDate);
			h.tv_atitle.setText("" + alist.get(position).title);
			return v;
		}
	}

	class MyHolder {
		TextView tv_atitle, tv_atime;
	}

	class ActivityList {
		String title, createDate, info;
		List<PhotoList> photoList;

		public ActivityList(String title, String createDate, String info, List<PhotoList> photoList) {
			super();
			this.title = title;
			this.createDate = createDate;
			this.info = info;
			this.photoList = photoList;
		}

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		pageIndex = 1;
		alist.clear();
		getdata(pageIndex, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (isend) {
			handler.sendEmptyMessage(1);
			Toast.makeText(this, "没有更多了", Toast.LENGTH_SHORT).show();
		} else {
			getdata(pageIndex, pageSize);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(Activity_List.this, Activity_ActivityMain.class);
		intent.putExtra("info", alist.get(position - 1).info);
		intent.putExtra("photolist", (Serializable) alist.get(position - 1).photoList);
		startActivity(intent);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("活动列表");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("活动列表");
	}
}
