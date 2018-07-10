package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_MyCustom extends BaseActivity implements OnRefreshListener2<GridView>, OnItemClickListener {
	private RelativeLayout rl_back_mc;
	private PullToRefreshGridView gv_mc;
	private List<MakeList> ms;
	private int sWidth;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private int size = 0;
	private MyAdapter adapter;

	private String name = "";
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				gv_mc.onRefreshComplete();
				break;

			default:
				break;
			}

		};
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("我的定制上传");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("我的定制上传");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_mc:
			finish();
			break;
		// case R.id.tv_sc:
		//
		// if (ed_sc.getText().toString().trim().length() > 0) {
		// pageIndex = 1;
		// pageSize = 10;
		// name = ed_sc.getText().toString().trim();
		// getData(pageIndex, pageSize, name);
		// } else {
		// showToast("请输入搜索的商品名!", 0);
		// }
		// break;

		default:
			break;
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		rl_back_mc = (RelativeLayout) findViewById(R.id.rl_back_mc);
		gv_mc = (PullToRefreshGridView) findViewById(R.id.gv_mc);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_mc.setOnClickListener(this);
		gv_mc.setOnRefreshListener(this);
		gv_mc.setOnItemClickListener(this);
		gv_mc.setMode(Mode.BOTH);
		ms = new ArrayList<MakeList>();
		adapter = new MyAdapter();
		gv_mc.setAdapter(adapter);
		getData(pageIndex, pageSize);
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_mycustom);
		sWidth = (int) ((getWith() - dp2px(Activity_MyCustom.this, 30)) / 2.0);
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	private void getData(int pi, int ps) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("user.id", MyApplication.userId);
		params.put("pageModel.pageIndex", pi);
		params.put("pageModel.pageSize", ps);
		params.put("token", MyApplication.token);
		showProgressDialog("加载中...");
		MyApplication.ahc.post(AppFinalUrl.usergetMyUploadMakeProduct, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo定制=" + response.toString());
				JSONArray optJSONArray = response.optJSONArray("list");
				gv_mc.onRefreshComplete();
				dismissProgressDialog();
				if (optJSONArray != null) {
					for (int i = 0; i < optJSONArray.length(); i++) {
						JSONObject j = optJSONArray.optJSONObject(i);
						String name = j.optString("name");
						String userName = j.optString("userName");
						String smallUrl = j.optString("smallUrl");
						String url = j.optString("url");
						int makeProductId = j.optInt("makeProductId");
						int userId = j.optInt("userId");
						int width = j.optInt("width");
						int high = j.optInt("high");
						String productname = j.optString("productname");
						String productpirce = j.optString("productpirce");
						ms.add(new MakeList(name, userName, smallUrl, url, makeProductId, userId, width, high, productname,
								productpirce));
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
				}
			}
		});
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ms == null ? 0 : ms.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return ms.get(position);
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
				v = View.inflate(Activity_MyCustom.this, R.layout.item_nsc, null);
				h.im_cgd = (ImageView) v.findViewById(R.id.im_cgd);
				h.tv_cgdname = (TextView) v.findViewById(R.id.tv_cgdname);
				h.tv_cgdprice = (TextView) v.findViewById(R.id.tv_cgdprice);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}

			if (MyApplication.issingle) {
				LayoutParams params = h.im_cgd.getLayoutParams();
				params.height = (int) (sWidth * 1.5) * 2;
				// params.width = sWidth * 2;
				h.im_cgd.setLayoutParams(params);
			} else {
				LayoutParams params = h.im_cgd.getLayoutParams();
				params.height = (int) (sWidth * 1.5);
				// params.width = sWidth;
				h.im_cgd.setLayoutParams(params);
			}
			Glide.with(Activity_MyCustom.this).load(ms.get(position).smallUrl)

			/*
			 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			 */
			.crossFade().into(h.im_cgd);
			h.tv_cgdname.setText("" + ms.get(position).productname);
			h.tv_cgdprice.setText("¥ " + ms.get(position).productpirce);
			return v;
		}
	}

	class MyHolder {
		ImageView im_cgd;
		TextView tv_cgdname, tv_cgdprice;
	}

	class MakeList {
		String name, userName, smallUrl, url, productname, productpirce;
		int makeProductId, userId, width, high;

		public MakeList(String name, String userName, String smallUrl, String url, int makeProductId, int userId, int width,
				int high, String productname, String productpirce) {
			super();
			this.name = name;
			this.userName = userName;
			this.smallUrl = smallUrl;
			this.url = url;
			this.makeProductId = makeProductId;
			this.userId = userId;
			this.width = width;
			this.high = high;
			this.productname = productname;
			this.productpirce = productpirce;
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
		// TODO Auto-generated method stub
		ms.clear();
		pageIndex = 1;
		getData(pageIndex, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
		// TODO Auto-generated method stub
		if (isend) {
			Toast.makeText(Activity_MyCustom.this, "没有更多", Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(1);
		} else {
			getData(pageIndex, pageSize);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Activity_MyCustom.this, Activity_CustomGoodsDetails.class);
		intent.putExtra("makeProductId", ms.get(position).makeProductId);
		startActivity(intent);
	}
}
