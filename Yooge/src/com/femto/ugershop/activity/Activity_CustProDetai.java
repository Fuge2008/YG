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

public class Activity_CustProDetai extends SwipeBackActivity implements OnRefreshListener2<ListView> {
	private RelativeLayout rl_back_sustprodetai;
	private PullToRefreshListView lv_custprodetail;
	private MylVAdapter lvadapter;
	private int myId;
	private DisplayImageOptions options;
	private List<CustomFee> customFee;
	int size = 0;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private boolean isfirstbanner = true;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				lv_custprodetail.onRefreshComplete();
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
		case R.id.rl_back_sustprodetai:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		customFee = new ArrayList<CustomFee>();
		MyApplication.addActivity(this);
		rl_back_sustprodetai = (RelativeLayout) findViewById(R.id.rl_back_sustprodetai);
		lv_custprodetail = (PullToRefreshListView) findViewById(R.id.lv_custprodetail);
		lv_custprodetail.setOnRefreshListener(this);
		lv_custprodetail.setMode(Mode.BOTH);
		lvadapter = new MylVAdapter();
		lv_custprodetail.setAdapter(lvadapter);
		getData(1, pageSize);
	}

	private void getData(int pageIndex1, int pageSize) {
		RequestParams params = new RequestParams();
		params.put("userId", myId);
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMakeMoneyDeails, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo==" + response.toString());
				try {
					JSONArray jsonArray = response.getJSONArray("List");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String userName = j.optString("userName");
						String createDate = j.optString("createDate");
						String userImg = j.optString("userImg");
						int price = j.optInt("price");
						int count = j.optInt("count");
						int status = j.optInt("status");
						int money = j.optInt("money");
						int type = j.optInt("type");
						customFee.add(new CustomFee(userName, createDate, userImg, price, count, status, money, type));
						size++;
					}
					if (size == 10) {
						pageIndex++;
						isend = false;
					} else {
						isend = true;
					}
					size = 0;
					lv_custprodetail.onRefreshComplete();
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
		rl_back_sustprodetai.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_custprodetai);
		initParams();
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

	class MylVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return customFee == null ? 0 : customFee.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return customFee.get(position);
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
				v = View.inflate(Activity_CustProDetai.this, R.layout.item_dingzhibaozhengjin, null);
				h.im_head_dzbzj = (CircleImageView) v.findViewById(R.id.im_head_dzbzj);
				h.tv_all_dzbzj = (TextView) v.findViewById(R.id.tv_all_dzbzj);
				h.tv_count_dzbzj = (TextView) v.findViewById(R.id.tv_count_dzbzj);
				h.tv_fee_dzbzj = (TextView) v.findViewById(R.id.tv_fee_dzbzj);
				h.tv_money_dzbzj = (TextView) v.findViewById(R.id.tv_money_dzbzj);
				h.tv_name_dzbzj = (TextView) v.findViewById(R.id.tv_name_dzbzj);
				h.tv_time_dzbzj = (TextView) v.findViewById(R.id.tv_time_dzbzj);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_all_dzbzj.setText("" + customFee.get(position).money + "元");
			h.tv_money_dzbzj.setText("" + customFee.get(position).money + "元");
			h.tv_count_dzbzj.setText("" + customFee.get(position).count);
			h.tv_fee_dzbzj.setText("" + customFee.get(position).price + "元");
			h.tv_time_dzbzj.setText("" + customFee.get(position).createDate);
			h.tv_name_dzbzj.setText("" + customFee.get(position).userName);
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + customFee.get(position).userImg, h.im_head_dzbzj,
					options);
			return v;
		}
	}

	class MyHolder {
		CircleImageView im_head_dzbzj;
		TextView tv_name_dzbzj, tv_time_dzbzj, tv_fee_dzbzj, tv_count_dzbzj, tv_all_dzbzj, tv_money_dzbzj;
	}

	class CustomFee {
		String userName, createDate, userImg;
		int price, count, status, money, type;

		public CustomFee(String userName, String createDate, String userImg, int price, int count, int status, int money, int type) {
			super();
			this.userName = userName;
			this.createDate = createDate;
			this.userImg = userImg;
			this.price = price;
			this.count = count;
			this.status = status;
			this.money = money;
			this.type = type;
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		customFee.clear();
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
		setPageEnd("定制保证金明细");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("定制保证金明细");
	}
}
