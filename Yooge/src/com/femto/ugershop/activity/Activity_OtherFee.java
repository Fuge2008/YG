package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
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

import android.content.SharedPreferences;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_OtherFee extends BaseActivity implements OnRefreshListener2<ListView> {
	private RelativeLayout rl_back_otherfee;
	private PullToRefreshListView lv_otherfee;
	private MylVAdapter lvadapter;
	private List<OtherFee> otherFee;
	private int myId;
	private DisplayImageOptions options;
	int size = 0;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private boolean isfirstbanner = true;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				lv_otherfee.onRefreshComplete();
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
		case R.id.rl_back_otherfee:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		otherFee = new ArrayList<OtherFee>();
		MyApplication.addActivity(this);
		rl_back_otherfee = (RelativeLayout) findViewById(R.id.rl_back_otherfee);
		lv_otherfee = (PullToRefreshListView) findViewById(R.id.lv_otherfee);
		lv_otherfee.setOnRefreshListener(this);
		lv_otherfee.setMode(Mode.BOTH);
		lvadapter = new MylVAdapter();
		lv_otherfee.setAdapter(lvadapter);
		getData(1, pageSize);
	}

	private void getData(int pageIndex1, int pageSize) {
		RequestParams params = new RequestParams();
		params.put("userId", myId);
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetOtherMoney, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					JSONArray jsonArray = response.getJSONArray("List");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);

						String createDate = j.optString("createDate");
						String url = j.optString("url");
						String name = j.optString("name");
						int price = j.optInt("price");
						int count = j.optInt("count");
						otherFee.add(new OtherFee(price, count, name, createDate, url));
						size++;
					}
					if (size == 10) {
						pageIndex++;
						isend = false;
					} else {
						isend = true;
					}
					size = 0;
					lv_otherfee.onRefreshComplete();
					lvadapter.notifyDataSetChanged();
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
		rl_back_otherfee.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_otherfee);
		initParams();
	}

	class MylVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return otherFee == null ? 0 : otherFee.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return otherFee.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			MyHolder h;
			if (v == null) {
				h = new MyHolder();
				v = View.inflate(Activity_OtherFee.this, R.layout.item_lv_otherfee, null);
				h.im_head_of = (CircleImageView) v.findViewById(R.id.im_head_of);
				h.tv_name_of = (TextView) v.findViewById(R.id.tv_name_of);
				h.tv_time_of = (TextView) v.findViewById(R.id.tv_time_of);
				h.tv_price_of = (TextView) v.findViewById(R.id.tv_price_of);
				h.tv_count_of = (TextView) v.findViewById(R.id.tv_count_of);
				h.tv_allfee_of = (TextView) v.findViewById(R.id.tv_allfee_of);
				h.tv_allmoney_fee = (TextView) v.findViewById(R.id.tv_allmoney_fee);
				h.tv_nub_of = (TextView) v.findViewById(R.id.tv_nub_of);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_nub_of.setText("x" + otherFee.get(position).count);
			h.tv_name_of.setText("" + otherFee.get(position).name);
			h.tv_time_of.setText("" + otherFee.get(position).createDate);
			h.tv_price_of.setText("" + otherFee.get(position).price);
			h.tv_count_of.setText("" + otherFee.get(position).count);
			h.tv_allfee_of.setText("" + (otherFee.get(position).price * otherFee.get(position).count));
			h.tv_allmoney_fee.setText("" + (otherFee.get(position).price * otherFee.get(position).count));
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + otherFee.get(position).url, h.im_head_of, options);
			return v;
		}
	}

	class MyHolder {
		CircleImageView im_head_of;
		TextView tv_name_of, tv_time_of, tv_price_of, tv_count_of, tv_allfee_of, tv_allmoney_fee, tv_nub_of;
	}

	class OtherFee {
		int price, count;
		String name, createDate, url;

		public OtherFee(int price, int count, String name, String createDate, String url) {
			super();
			this.price = price;
			this.count = count;
			this.name = name;
			this.createDate = createDate;
			this.url = url;
		}
	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
		myId = sp.getInt("userId", 0);
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
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		otherFee.clear();
		getData(1, pageSize);
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
		setPageEnd("其他费用");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("其他费用");
	}
}
