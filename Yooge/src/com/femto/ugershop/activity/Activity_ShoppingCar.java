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
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_ShoppingCar extends BaseActivity implements OnItemLongClickListener {
	private RelativeLayout rl_back_shopcar;
	private ListView lv_shopcar;
	private MyAdapter adapter;
	private int myId;
	private DisplayImageOptions options;
	private List<ShopCar> shopCar;
	private CheckBox cb_all;
	private TextView tv_allmoney, tv_jiesuan;
	private int allmoney;
	private String ids = "";

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("购物车");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_shopcar:
			finish();
			break;
		case R.id.cb_all:
			if (cb_all.isChecked()) {
				for (int i = 0; i < shopCar.size(); i++) {
					shopCar.get(i).setIssele(true);
				}
			} else {
				for (int i = 0; i < shopCar.size(); i++) {
					shopCar.get(i).setIssele(false);
				}
			}
			for (int i = 0; i < shopCar.size(); i++) {
				if (shopCar.get(i).isIssele()) {
					allmoney = allmoney + (shopCar.get(i).price * shopCar.get(i).nub);
				}
			}
			tv_allmoney.setText("￥" + allmoney);
			allmoney = 0;
			adapter.notifyDataSetChanged();
			break;
		case R.id.tv_jiesuan:
			jiesuan();
			break;
		default:
			break;
		}
	}

	private void jiesuan() {
		for (int i = 0; i < shopCar.size(); i++) {
			if (shopCar.get(i).isIssele()) {

				ids = ids + shopCar.get(i).shopCarId + ",";
			}
		}

		if (ids.length() == 0) {
			Toast.makeText(Activity_ShoppingCar.this, "未选择商品", Toast.LENGTH_SHORT).show();
			return;
		} else {
			ids = ids.substring(0, ids.length() - 1);
		}
		Intent intent = new Intent(Activity_ShoppingCar.this, Activity_Order.class);
		intent.putExtra("type", 1);
		intent.putExtra("ids", ids);
		System.out.println("zuo===ids=" + ids);
		startActivity(intent);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		shopCar = new ArrayList<ShopCar>();
		rl_back_shopcar = (RelativeLayout) findViewById(R.id.rl_back_shopcar);
		lv_shopcar = (ListView) findViewById(R.id.lv_shopcar);
		cb_all = (CheckBox) findViewById(R.id.cb_all);
		tv_jiesuan = (TextView) findViewById(R.id.tv_jiesuan);
		tv_allmoney = (TextView) findViewById(R.id.tv_allmoney);
		adapter = new MyAdapter();
		lv_shopcar.setAdapter(adapter);
		lv_shopcar.setOnItemLongClickListener(this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("购物车");
		shopCar.clear();
		getData();
	}

	private void getData() {
		RequestParams params = new RequestParams();
		params.put("user.id", myId);
		params.put("pageModel.pageIndex", 1);
		params.put("pageModel.pageSize", 100);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMyShopCar, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					JSONArray jsonArray = response.getJSONArray("carList");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String productImg = j.optString("productImg");
						String createDate = j.optString("createDate");
						String colour = j.optString("colour");
						String chiMa = j.optString("chiMa");
						String productName = j.optString("productName");
						int nub = j.optInt("nub");
						int dressMoney = j.optInt("dressMoney");
						int allPrice = j.optInt("allPrice");
						int price = j.optInt("price");
						int shopCarId = j.optInt("shopCarId");
						int productId = j.optInt("productId");
						shopCar.add(new ShopCar(productImg, createDate, colour, chiMa, productName, nub, allPrice, price,
								shopCarId, productId, false, dressMoney));
					}
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	class ShopCar {
		String productImg, createDate, colour, chiMa, productName;
		int nub, allPrice, price, shopCarId, productId, dressMoney;
		boolean issele;

		public ShopCar(String productImg, String createDate, String colour, String chiMa, String productName, int nub,
				int allPrice, int price, int shopCarId, int productId, boolean issele, int dressMoney) {
			super();
			this.productImg = productImg;
			this.createDate = createDate;
			this.colour = colour;
			this.chiMa = chiMa;
			this.productName = productName;
			this.nub = nub;
			this.allPrice = allPrice;
			this.price = price;
			this.shopCarId = shopCarId;
			this.productId = productId;
			this.issele = issele;
			this.dressMoney = dressMoney;
		}

		public int getDressMoney() {
			return dressMoney;
		}

		public void setDressMoney(int dressMoney) {
			this.dressMoney = dressMoney;
		}

		public String getProductImg() {
			return productImg;
		}

		public void setProductImg(String productImg) {
			this.productImg = productImg;
		}

		public String getCreateDate() {
			return createDate;
		}

		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}

		public String getColour() {
			return colour;
		}

		public void setColour(String colour) {
			this.colour = colour;
		}

		public String getChiMa() {
			return chiMa;
		}

		public void setChiMa(String chiMa) {
			this.chiMa = chiMa;
		}

		public String getProductName() {
			return productName;
		}

		public void setProductName(String productName) {
			this.productName = productName;
		}

		public int getNub() {
			return nub;
		}

		public void setNub(int nub) {
			this.nub = nub;
		}

		public int getAllPrice() {
			return allPrice;
		}

		public void setAllPrice(int allPrice) {
			this.allPrice = allPrice;
		}

		public int getPrice() {
			return price;
		}

		public void setPrice(int price) {
			this.price = price;
		}

		public int getShopCarId() {
			return shopCarId;
		}

		public void setShopCarId(int shopCarId) {
			this.shopCarId = shopCarId;
		}

		public int getProductId() {
			return productId;
		}

		public void setProductId(int productId) {
			this.productId = productId;
		}

		public boolean isIssele() {
			return issele;
		}

		public void setIssele(boolean issele) {
			this.issele = issele;
		}

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return shopCar == null ? 0 : shopCar.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return shopCar.get(position);
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
				v = View.inflate(Activity_ShoppingCar.this, R.layout.item_shopcar, null);
				h.im_gpic = (ImageView) v.findViewById(R.id.im_gpic);
				h.im_issele = (ImageView) v.findViewById(R.id.im_issele);
				h.tv_gname = (TextView) v.findViewById(R.id.tv_gname);
				h.tv_size = (TextView) v.findViewById(R.id.tv_size);
				h.tv_color = (TextView) v.findViewById(R.id.tv_color);
				h.tv_gprice = (TextView) v.findViewById(R.id.tv_gprice);
				h.tv_gnub = (TextView) v.findViewById(R.id.tv_gnub);
				h.tv_item_fitfee = (TextView) v.findViewById(R.id.tv_item_fitfee);
				h.ll_item_size = (LinearLayout) v.findViewById(R.id.ll_item_size);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}

			h.tv_gname.setText("" + shopCar.get(position).productName);
			h.tv_size.setText("" + shopCar.get(position).chiMa);
			h.tv_color.setText("" + shopCar.get(position).colour);
			h.tv_gprice.setText("￥" + shopCar.get(position).price);
			h.tv_gnub.setText("X" + shopCar.get(position).nub);
			if (shopCar.get(position).chiMa.equals("-1")) {
				h.ll_item_size.setVisibility(View.GONE);
				h.tv_item_fitfee.setText("量体裁衣费:" + shopCar.get(position).dressMoney);
				h.tv_item_fitfee.setVisibility(View.VISIBLE);
			} else {
				h.ll_item_size.setVisibility(View.VISIBLE);
				h.tv_item_fitfee.setVisibility(View.GONE);
			}
			if (shopCar.get(position).issele) {
				h.im_issele.setImageResource(R.drawable.seleall);
			} else {
				h.im_issele.setImageResource(R.drawable.seleall_no);
			}
			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + shopCar.get(position).productImg, h.im_gpic,
					options);
			h.im_issele.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (shopCar.get(position).issele) {
						shopCar.get(position).setIssele(false);
					} else {
						shopCar.get(position).setIssele(true);
					}
					for (int i = 0; i < shopCar.size(); i++) {
						if (shopCar.get(i).isIssele()) {
							allmoney = allmoney + (shopCar.get(i).price * shopCar.get(i).nub);
						}
					}
					tv_allmoney.setText("￥" + allmoney);
					allmoney = 0;
					adapter.notifyDataSetChanged();
				}
			});
			return v;
		}
	}

	class MyHolder {
		ImageView im_issele, im_gpic;
		TextView tv_gname, tv_size, tv_color, tv_gprice, tv_gnub, tv_item_fitfee;
		LinearLayout ll_item_size;
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		rl_back_shopcar.setOnClickListener(this);
		cb_all.setOnClickListener(this);
		tv_jiesuan.setOnClickListener(this);

	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_shoppingcar);
		initParams();
		MyApplication.addActivity(this);
	}

	private void initParams() {
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();

		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		myId = sp.getInt("userId", 0);

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		showDeleDialog(shopCar.get(position).shopCarId, position);
		return false;
	}

	private void showDeleDialog(final int shopCarId, final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(Activity_ShoppingCar.this);
		builder.setTitle("提示").setMessage("确定删除?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				deleoder(shopCarId, position);
			}

		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}

	private void deleoder(int shopCarId, final int position) {
		RequestParams params = new RequestParams();
		params.put("shopCarId", shopCarId);
		showProgressDialog("正在删除");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userdeleteShopCar, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Toast.makeText(Activity_ShoppingCar.this, "删除成功", Toast.LENGTH_SHORT).show();
						shopCar.remove(position);
						adapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}
}
