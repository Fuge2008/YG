package com.femto.ugershop.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_GoodsDetails;
import com.femto.ugershop.activity.Activity_Head;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.entity.HeadTitle;
import com.femto.ugershop.entity.PhotoList;
import com.femto.ugershop.view.ScrollViewWithListView;
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
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class Fragment_Head extends BaseFragment implements OnRefreshListener2<ListView>, OnItemClickListener {
	private View view;
	private String title;
	private PullToRefreshListView lv_head;
	private List<HeadTitle> ht = new ArrayList<HeadTitle>();;
	private MyAdapter adapter = new MyAdapter();;
	private int sortId;
	private int size = 0;
	private int pageSize = 10;
	private int pageIndex = 1;
	private boolean isend = false;
	private List<Head> hs;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				lv_head.onRefreshComplete();
				break;

			default:
				break;
			}

		};
	};
	private DisplayImageOptions options;
	private int scroWith;

	@SuppressLint("ValidFragment")
	public Fragment_Head(String title, int sortId) {
		super();
		this.title = title;
		this.sortId = sortId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_head, container, false);
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		scroWith = getScroWith() - dp2px2(getActivity(), 32);
		initView(view);
		initCont();
		return view;
	}

	private void initView(View v) {
		// TODO Auto-generated method stub
		lv_head = (PullToRefreshListView) v.findViewById(R.id.lv_head);
	}

	private void initCont() {
		hs = new ArrayList<Head>();
		// TODO Auto-generated method stub
		lv_head.setOnItemClickListener(this);
		lv_head.setOnRefreshListener(this);
		lv_head.setMode(Mode.BOTH);
		lv_head.setAdapter(adapter);
		getdata(pageSize, pageIndex);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);

	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);

	}

	private void getdata(int p, int i) {
		RequestParams params = new RequestParams();
		params.put("sortId", sortId);
		params.put("userId", MyApplication.userId);
		params.put("pageModel.pageSize", p);
		params.put("pageModel.pageIndex", i);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetDocumentList, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuoget" + title + "   " + response.toString());
				JSONArray optJSONArray = response.optJSONArray("list");
				if (lv_head != null) {
					lv_head.onRefreshComplete();
				}
				for (int j = 0; j < optJSONArray.length(); j++) {
					JSONObject js = optJSONArray.optJSONObject(j);
					int id = js.optInt("id");
					String imgUrl = js.optString("imgUrl");
					String comeFrom = js.optString("comeFrom");
					String username = js.optString("username");
					String title = js.optString("title");
					String jimpUrl = js.optString("jimpUrl");
					String fileurl = js.optString("fileurl");
					String width = js.optString("width");
					String high = js.optString("high");
					String isTop = js.optString("isTop");
					String createDate = js.optString("createDate");
					String topCount = js.optString("topCount");
					String shareCount = js.optString("shareCount");
					int topCount1 = 0;
					int shareCount1 = 0;

					if (topCount != null && !topCount.equals("")) {
						topCount1 = Integer.parseInt(topCount);
					}
					if (shareCount != null && !shareCount.equals("")) {
						shareCount1 = Integer.parseInt(shareCount);
					}

					String info = js.optString("info");
					List<PhotoList> ps = new ArrayList<PhotoList>();
					ps.add(new PhotoList(imgUrl, high, width));
					hs.add(new Head(id, imgUrl, comeFrom, username, title, jimpUrl, fileurl, width, high, isTop, createDate,
							topCount1, info, ps, shareCount1));

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
		});
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(getActivity());
		setPageEnd("头条");
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(getActivity());
		setPageStart("头条");
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return hs == null ? 0 : hs.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return hs.get(position);
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
				v = View.inflate(getActivity(), R.layout.item_head_line, null);
				h.tv_from = (TextView) v.findViewById(R.id.tv_from);
				h.tv_usernamehead = (TextView) v.findViewById(R.id.tv_usernamehead);
				h.tv_ctime = (TextView) v.findViewById(R.id.tv_ctime);
				h.tv_title_head = (TextView) v.findViewById(R.id.tv_title_head);
				h.tv_info_head = (TextView) v.findViewById(R.id.tv_info_head);
				h.tv_topnub_head = (TextView) v.findViewById(R.id.tv_topnub_head);
				h.im_isseletop = (ImageView) v.findViewById(R.id.im_isseletop);
				h.lv_pic_head = (ScrollViewWithListView) v.findViewById(R.id.lv_pic_head);
				h.ll_prise_head = (LinearLayout) v.findViewById(R.id.ll_prise_head);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.tv_from.setText(hs.get(position).comeFrom);
			h.tv_usernamehead.setText(hs.get(position).username);
			h.tv_ctime.setText(hs.get(position).createDate);
			h.tv_title_head.setText(hs.get(position).title);
			h.tv_info_head.setText(hs.get(position).info);
			h.tv_topnub_head.setText("" + hs.get(position).topCount);

			h.lv_pic_head.setAdapter(new LVAdapter(hs.get(position).ps));
			if (hs.get(position).isTop.equals("1")) {
				h.im_isseletop.setImageResource(R.drawable.newpostheartde);
			} else {
				h.im_isseletop.setImageResource(R.drawable.newpostheart);
			}
			h.ll_prise_head.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					priseOrCancel(hs.get(position).isTop, hs.get(position).id, position);
				}

			});
			return v;
		}
	}

	class MyHolder {
		TextView tv_from, tv_usernamehead, tv_ctime, tv_title_head, tv_info_head, tv_topnub_head;
		ScrollViewWithListView lv_pic_head;
		ImageView im_isseletop;
		LinearLayout ll_prise_head;
	}

	// 点赞或取消
	private void priseOrCancel(final String isTop, int documentId, final int position) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("documentId", documentId);
		params.put("userId", MyApplication.userId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usercancleOrAddTopToDocument, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo--------" + response.toString());
				String result = response.optString("result");
				if (result != null && result.equals("0")) {
					if (isTop.equals("1")) {
						hs.get(position).topCount = hs.get(position).topCount - 1;
						hs.get(position).isTop = "0";
						Toast.makeText(getActivity(), "已取消赞", Toast.LENGTH_SHORT).show();
					} else {
						hs.get(position).topCount = hs.get(position).topCount + 1;
						hs.get(position).isTop = "1";
						Toast.makeText(getActivity(), "点赞成功", Toast.LENGTH_SHORT).show();
					}
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	// 图片适配器
	class LVAdapter extends BaseAdapter {
		private List<PhotoList> pics;

		public LVAdapter(List<PhotoList> pics) {
			super();
			this.pics = pics;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pics == null ? 0 : pics.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return pics.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			MYlvholder h;
			if (v == null) {
				h = new MYlvholder();
				v = View.inflate(getActivity(), R.layout.item_image_newother, null);
				h.im = (ImageView) v.findViewById(R.id.im_other);
				v.setTag(h);
			} else {
				h = (MYlvholder) v.getTag();
			}
			// Glide.with(getActivity()).load(pics.get(position))
			// /*
			// * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			// */
			// .crossFade().into(h.im);

			Glide.with(getActivity()).load(pics.get(position).getPhotoUrl())
			/*
			 * 缺省的占位图片，一般可以设置成一个加载中的进度GIF图
			 */
			.crossFade().into(h.im);

			// ImageLoader.getInstance().displayImage(pics.get(position), h.im,
			// options);
			if (pics.get(position).getHigh() != null && !pics.get(position).getHigh().equals("")
					&& pics.get(position).getHigh().length() != 0 && pics.get(position).getWidth() != null
					&& !pics.get(position).getWidth().equals("") && pics.get(position).getWidth().length() != 0) {
				LayoutParams params = h.im.getLayoutParams();
				params.width = scroWith;
				params.height = (int) ((scroWith * Integer.parseInt(pics.get(position).getHigh())) / (double) Integer
						.parseInt(pics.get(position).getWidth()));
				h.im.setLayoutParams(params);
			}
			return v;
		}
	}

	class MYlvholder {
		ImageView im;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		hs.clear();
		pageIndex = 1;
		getdata(pageSize, pageIndex);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		if (isend) {
			Toast.makeText(getActivity(), "没有更多", Toast.LENGTH_SHORT).show();
			handler.sendEmptyMessage(1);
		} else {
			getdata(pageSize, pageIndex);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		position = position - 1;
		Intent intent = new Intent(getActivity(), Activity_Head.class);
		intent.putExtra("url", hs.get(position).jimpUrl);
		intent.putExtra("isTop", hs.get(position).isTop);
		intent.putExtra("topCount", hs.get(position).topCount);
		intent.putExtra("documentId", hs.get(position).id);
		intent.putExtra("title", hs.get(position).title);
		intent.putExtra("info", hs.get(position).info);
		intent.putExtra("shareCount", hs.get(position).shareCount1);
		intent.putExtra("picurl", hs.get(position).imgUrl);
		startActivity(intent);
	}

	class Head {
		int id, topCount, shareCount1;
		String imgUrl, comeFrom, username, title, jimpUrl, fileurl, width, high, isTop, createDate, info;
		List<PhotoList> ps;

		public Head(int id, String imgUrl, String comeFrom, String username, String title, String jimpUrl, String fileurl,
				String width, String high, String isTop, String createDate, int topCount, String info, List<PhotoList> ps,
				int shareCount1) {
			super();
			this.id = id;
			this.imgUrl = imgUrl;
			this.comeFrom = comeFrom;
			this.username = username;
			this.title = title;
			this.jimpUrl = jimpUrl;
			this.fileurl = fileurl;
			this.width = width;
			this.high = high;
			this.isTop = isTop;
			this.createDate = createDate;
			this.topCount = topCount;
			this.info = info;
			this.ps = ps;
			this.shareCount1 = shareCount1;
		}

	}
}
