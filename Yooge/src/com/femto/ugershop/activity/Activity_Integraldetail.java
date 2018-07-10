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
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Integraldetail extends SwipeBackActivity implements OnRefreshListener2<ListView> {
	private RelativeLayout rl_back_integraldetail;
	private MyLVAdapter lvadapter;
	private PullToRefreshListView lv_integraldetail;
	private int userId;
	private DisplayImageOptions options;
	private List<Score> score;
	int size = 0;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private boolean isfirstbanner = true;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				lv_integraldetail.onRefreshComplete();
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
		case R.id.rl_back_integraldetail:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		score = new ArrayList<Score>();
		MyApplication.addActivity(this);
		rl_back_integraldetail = (RelativeLayout) findViewById(R.id.rl_back_integraldetail);
		lvadapter = new MyLVAdapter();
		lv_integraldetail = (PullToRefreshListView) findViewById(R.id.lv_integraldetail);
		lv_integraldetail.setOnRefreshListener(this);
		lv_integraldetail.setMode(Mode.BOTH);
		lv_integraldetail.setAdapter(lvadapter);
		showProgressDialog("加载中...");
		getData(1, pageSize);
	}

	private void getData(int pageIndex1, int pageSize) {
		RequestParams params = new RequestParams();
		params.put("userId", userId);
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetScoreDeails, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				System.out.println("zuo_response=" + response.toString());
				try {
					JSONArray jsonArray = response.getJSONArray("List");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String createDate = j.optString("createDate");
						String info = j.optString("info");
						String url = j.optString("url");
						int scorce = j.optInt("scorce");
						score.add(new Score(createDate, info, url, scorce));
						size++;
					}
					if (size == 10) {
						pageIndex++;
						isend = false;
					} else {
						isend = true;
					}
					size = 0;
					lv_integraldetail.onRefreshComplete();
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
		rl_back_integraldetail.setOnClickListener(this);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_integraldetail);
		initParams();
	}

	class MyLVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return score == null ? 0 : score.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return score.get(position);
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
				v = View.inflate(Activity_Integraldetail.this, R.layout.item_integraldetail, null);
				h.tv_info_score = (TextView) v.findViewById(R.id.tv_info_score);
				h.tv_score_score = (TextView) v.findViewById(R.id.tv_score_score1);
				h.tv_time_score = (TextView) v.findViewById(R.id.tv_time_score);
				h.im_lv_head = (CircleImageView) v.findViewById(R.id.im_lv_head);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_info_score.setText("" + score.get(position).info);
			h.tv_score_score.setText("" + score.get(position).scorce);
			h.tv_time_score.setText("" + score.get(position).createDate);
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + score.get(position).url, h.im_lv_head, options);
			return v;
		}
	}

	class MyHolder {
		TextView tv_score_score, tv_time_score, tv_info_score;
		CircleImageView im_lv_head;
	}

	class Score {
		String createDate, info, url;
		int scorce;

		public Score(String createDate, String info, String url, int scorce) {
			super();
			this.createDate = createDate;
			this.info = info;
			this.url = url;
			this.scorce = scorce;
		}

	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		userId = sp.getInt("userId", -1);
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
		score.clear();
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
		setPageEnd("积分明细");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("积分明细");
	}
}
