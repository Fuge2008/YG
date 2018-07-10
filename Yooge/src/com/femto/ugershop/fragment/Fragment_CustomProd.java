package com.femto.ugershop.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_LookPic;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.view.CircleImageView;
import com.femto.ugershop.view.MyGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class Fragment_CustomProd extends BaseFragment implements OnRefreshListener2<ListView> {
	private PullToRefreshListView lv_last_product;
	private MyAdapter adapter;
	private View view;
	private int userId;
	private List<ProductList> productList;
	private DisplayImageOptions options;
	private int w;
	GVpicAdapter gvadapter;
	int size = 0;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				lv_last_product.onRefreshComplete();
				break;

			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_lastprod, container, false);
		productList = new ArrayList<ProductList>();
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(false) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		int screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
		int dp2px = dp2px(getActivity(), 18);
		w = (screenWidth - dp2px) / 3;
		initView(view);

		return view;
	}

	public static int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);

		if (!hidden && productList.size() == 0) {
			getData(3, 1, 10);
		}
		if (!hidden) {
			MobclickAgent.onResume(getActivity());
			setPageStart("定制订单");
		} else {
			MobclickAgent.onPause(getActivity());
			setPageEnd("定制订单");
		}
	}

	public Fragment_CustomProd(int userId) {
		super();
		this.userId = userId;
	}

	private void initView(View v) {
		lv_last_product = (PullToRefreshListView) v.findViewById(R.id.lv_last_product);
		lv_last_product.setOnRefreshListener(this);
		lv_last_product.setMode(Mode.BOTH);
		adapter = new MyAdapter();
		lv_last_product.setAdapter(adapter);
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return productList == null ? 0 : productList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return productList.get(position);
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
				v = View.inflate(getActivity(), R.layout.item_dingzhizuop_lv, null);
				h.tv_title_dingzhizp = (TextView) v.findViewById(R.id.tv_title_dingzhizp);
				h.tv_time_dingzhizp = (TextView) v.findViewById(R.id.tv_time_dingzhizp);
				h.im_head_dzzp = (CircleImageView) v.findViewById(R.id.im_head_dzzp);
				h.gv_pic_dzzp = (MyGridView) v.findViewById(R.id.gv_pic_dzzp);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_title_dingzhizp.setText("" + productList.get(position).userName);
			h.tv_time_dingzhizp.setText("" + productList.get(position).createDate);
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + productList.get(position).userImgUrl,
					h.im_head_dzzp, options);
			gvadapter = new GVpicAdapter(productList.get(position).pics);
			h.gv_pic_dzzp.setAdapter(gvadapter);
			return v;
		}
	}

	class MyHolder {
		TextView tv_title_dingzhizp, tv_time_dingzhizp;
		CircleImageView im_head_dzzp;
		MyGridView gv_pic_dzzp;
	}

	private void getData(int type, int pageIndex1, int pageSize) {
		RequestParams params = new RequestParams();

		params.put("user.id", userId);
		showProgressDialog("加载中...");
		params.put("pageModel.pageIndex", pageIndex1);
		params.put("pageModel.pageSize", pageSize);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMakePhoto, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				System.out.println("zuopro=userId=" + userId + "    " + response.toString());
				try {
					JSONArray jsonArray = response.getJSONArray("allList");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String desinImgUrl = j.optString("desinImgUrl");
						String userImgUrl = j.optString("userImgUrl");
						String name = j.optString("name");
						String userName = j.optString("userName");
						String createDate = j.optString("createDate");
						String desinUserName = j.optString("desinUserName");
						int desinUserId = j.optInt("desinUserId");
						int makeId = j.optInt("makeId");
						int userId = j.optInt("userId");
						List<Pics> pics = new ArrayList<Pics>();

						JSONArray jsonArray2 = j.optJSONArray("List");
						for (int k = 0; k < jsonArray2.length(); k++) {
							JSONObject jj = jsonArray2.getJSONObject(k);
							String photoUrl = jj.optString("photoUrl");
							int photoId = jj.optInt("photoId");
							pics.add(new Pics(photoUrl, photoId));

						}
						size++;
						productList.add(new ProductList(desinImgUrl, userImgUrl, name, userName, createDate, desinUserName,
								desinUserId, makeId, userId, pics));
					}
					if (size == 10) {
						isend = false;
						pageIndex++;
					} else {
						isend = true;
					}
					size = 0;
					lv_last_product.onRefreshComplete();
					adapter.notifyDataSetChanged();
					System.out.println("zuoproductList.size==" + productList.size());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	class ProductList {
		String desinImgUrl, userImgUrl, name, userName, createDate, desinUserName;
		int desinUserId, makeId, userId;
		List<Pics> pics;

		public ProductList(String desinImgUrl, String userImgUrl, String name, String userName, String createDate,
				String desinUserName, int desinUserId, int makeId, int userId, List<Pics> pics) {
			super();
			this.desinImgUrl = desinImgUrl;
			this.userImgUrl = userImgUrl;
			this.name = name;
			this.userName = userName;
			this.createDate = createDate;
			this.desinUserName = desinUserName;
			this.desinUserId = desinUserId;
			this.makeId = makeId;
			this.userId = userId;
			this.pics = pics;
		}
	}

	class Pics {
		String photoUrl;
		int photoId;

		public Pics(String photoUrl, int photoId) {
			super();
			this.photoUrl = photoUrl;
			this.photoId = photoId;
		}
	}

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
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + pp.get(position).photoUrl, im_commends, options);
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
						dd.add(pp.get(i).photoUrl);
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
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-genproductListerated method stub
		productList.clear();
		getData(3, 1, pageSize);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (isend) {
			handler.sendEmptyMessage(1);
			Toast.makeText(getActivity(), "没有更多了", Toast.LENGTH_SHORT).show();
		} else {
			getData(3, pageIndex, pageSize);
		}

	}
}
