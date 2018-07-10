package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_ShoppingCar.MyHolder;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.entity.MyCoupon;
import com.femto.ugershop.view.ScrollViewWithListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Order extends BaseActivity {
	private RelativeLayout rl_back_order, rl_creatq, rl_selecoupon;
	private TextView tv_sure;
	private int myId;
	private DisplayImageOptions options;
	private String oderId;
	private int count;
	private double price, amount;
	private String phone = "", orderCode, address = "", productUrl, chiMa, getProductName = "", createDate, productName;
	private EditText ed_name_me, ed_phone_me, ed_address_me, ed_message;
	private TextView tv_goods_name, tv_goodstitle, tv_size_oder, tv_price, tv_count_oderx, tv_odername, tv_tv_count_oder,
			tv_allmoney, tv_newaddress, tv_color_order, tv_fitfee, tv_created, tv_coup;
	private ImageView im_goodspic;
	private int type = 0;
	private List<ShopCar> shopcarList;
	private LinearLayout ll_odersoonbuy, ll_size;
	private ScrollViewWithListView lv_shopcarlist;
	private String ids;
	private MyAdapter lvadapter;
	private String image;
	private String upurl;
	private int allcount = 0;
	private String colour;
	private int newtype = 0;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_back_order:
			finish();
			break;
		case R.id.tv_sure1:
			if (ed_name_me.length() == 0) {
				Toast.makeText(this, "请填写收货人姓名", Toast.LENGTH_SHORT).show();
				return;
			}
			if (ed_phone_me.length() != 11) {
				Toast.makeText(this, "请填写正确的手机号码", Toast.LENGTH_SHORT).show();
				return;
			}
			if (ed_address_me.length() == 0) {
				Toast.makeText(this, "请填写收货人地址", Toast.LENGTH_SHORT).show();
				return;
			}
			showdialog(upurl);
			break;
		case R.id.tv_newaddress:
			Intent intent = new Intent(this, Activity_Address.class);
			startActivityForResult(intent, 101);
			break;
		case R.id.rl_creatq:
			Intent intent_cq = new Intent(this, Activity_CreatQcode.class);
			intent_cq.putExtra("orderCode", orderCode);
			startActivityForResult(intent_cq, 202);
			break;
		case R.id.rl_selecoupon:
			Intent Intent = new Intent(this, Activity_MyCoupon.class);
			Intent.putExtra("orderCode", orderCode);
			startActivityForResult(Intent, 208);
			break;
		default:
			break;
		}
	}

	private MyCoupon cs;
	private String pushCode = "";
	private String couponId = "";

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			switch (resultCode) {
			case 101:
				String address = data.getStringExtra("address");
				ed_address_me.setText("" + address);
				break;
			case 202:
				tv_created.setVisibility(View.VISIBLE);
				break;
			case 208:
				cs = (MyCoupon) data.getSerializableExtra("cs");
				if (cs != null) {
					tv_coup.setText("" + cs.getInfo());
					couponId = "" + cs.getId();
				}

				pushCode = data.getStringExtra("pushCode");
				if (couponId == null) {
					couponId = "";
				}
				if (pushCode == null) {
					pushCode = "";
				}
				if (pushCode != null && !pushCode.equals("")) {
					showToast("已为您打折" , 1);
				}
				if (cs != null) {
					showToast("已为您减免" + cs.getMoney() + "元", 1);
				}
				if (type == 0) {
					upurl = AppFinalUrl.userupdateOrderByUserId;
					getData(AppFinalUrl.usergetOrderByOrderId);
					ll_odersoonbuy.setVisibility(View.VISIBLE);
					lv_shopcarlist.setVisibility(View.GONE);
					rl_creatq.setVisibility(View.VISIBLE);
				} else if (type == 3) {
					upurl = AppFinalUrl.userupdateMakeOrderByUserId;
					getData(AppFinalUrl.usergetMakeOrderByOrderId);
					ll_odersoonbuy.setVisibility(View.VISIBLE);
					lv_shopcarlist.setVisibility(View.GONE);
					rl_creatq.setVisibility(View.GONE);
				} else {
					getListShopCar();
					upurl = AppFinalUrl.userupdateOrderByUserId;
					lv_shopcarlist.setVisibility(View.VISIBLE);
					ll_odersoonbuy.setVisibility(View.GONE);
					lvadapter = new MyAdapter();
					lv_shopcarlist.setAdapter(lvadapter);
					rl_creatq.setVisibility(View.GONE);
				}

				break;
			default:
				break;
			}

		}
	}

	private void showdialog(final String upurl) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示").setMessage("您确定支付" + amount + "元").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				upMessage(upurl);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).show();

	}

	@Override
	public void initView() {
		MyApplication.addpaycct(this);
		shopcarList = new ArrayList<ShopCar>();
		rl_back_order = (RelativeLayout) findViewById(R.id.rl_back_order);
		rl_selecoupon = (RelativeLayout) findViewById(R.id.rl_selecoupon);
		tv_sure = (TextView) findViewById(R.id.tv_sure1);
		ed_name_me = (EditText) findViewById(R.id.ed_name_me);
		ed_phone_me = (EditText) findViewById(R.id.ed_phone_me);
		ed_address_me = (EditText) findViewById(R.id.ed_address_me);
		ed_message = (EditText) findViewById(R.id.ed_message);
		ll_odersoonbuy = (LinearLayout) findViewById(R.id.ll_odersoonbuy);
		ll_size = (LinearLayout) findViewById(R.id.ll_size);
		lv_shopcarlist = (ScrollViewWithListView) findViewById(R.id.lv_shopcarlist);
		// tv_goods_name, tv_goodstitle, tv_size_oder, tv_price,
		// tv_count_oderx;
		tv_goods_name = (TextView) findViewById(R.id.tv_goods_name);
		tv_coup = (TextView) findViewById(R.id.tv_coup);
		tv_goodstitle = (TextView) findViewById(R.id.tv_goodstitle);
		tv_color_order = (TextView) findViewById(R.id.tv_color_order);
		tv_size_oder = (TextView) findViewById(R.id.tv_size_oder);
		tv_price = (TextView) findViewById(R.id.tv_price);
		tv_fitfee = (TextView) findViewById(R.id.tv_fitfee);
		tv_count_oderx = (TextView) findViewById(R.id.tv_count_oderx);
		tv_created = (TextView) findViewById(R.id.tv_created);
		tv_odername = (TextView) findViewById(R.id.tv_odername1);
		tv_tv_count_oder = (TextView) findViewById(R.id.tv_tv_count_oder);
		tv_allmoney = (TextView) findViewById(R.id.tv_allmoney);
		tv_newaddress = (TextView) findViewById(R.id.tv_newaddress);
		im_goodspic = (ImageView) findViewById(R.id.im_goodspic);
		rl_creatq = (RelativeLayout) findViewById(R.id.rl_creatq);
		if (type == 0) {
			upurl = AppFinalUrl.userupdateOrderByUserId;
			getData(AppFinalUrl.usergetOrderByOrderId);
			ll_odersoonbuy.setVisibility(View.VISIBLE);
			lv_shopcarlist.setVisibility(View.GONE);
			rl_creatq.setVisibility(View.VISIBLE);
		} else if (type == 3) {
			upurl = AppFinalUrl.userupdateMakeOrderByUserId;
			getData(AppFinalUrl.usergetMakeOrderByOrderId);
			ll_odersoonbuy.setVisibility(View.VISIBLE);
			lv_shopcarlist.setVisibility(View.GONE);
			rl_creatq.setVisibility(View.GONE);
		} else {
			getListShopCar();
			upurl = AppFinalUrl.userupdateOrderByUserId;
			lv_shopcarlist.setVisibility(View.VISIBLE);
			ll_odersoonbuy.setVisibility(View.GONE);
			lvadapter = new MyAdapter();
			lv_shopcarlist.setAdapter(lvadapter);
			rl_creatq.setVisibility(View.GONE);
		}

	}

	private void getListShopCar() {
		RequestParams params = new RequestParams();
		params.put("shopCarIdList", ids);
		params.put("pushCode", pushCode);
		params.put("couponId", couponId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userUploadshopCarIdList, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo====" + response.toString());
				phone = response.optString("phone");
				orderCode = response.optString("orderCode");
				tv_odername.setText("" + orderCode);

				address = response.optString("address");
				amount = response.optDouble("allMoney");
				getProductName = response.optString("getProductName");
				if (!getProductName.equals("null")) {
					ed_name_me.setText("" + getProductName);
				} else {
					ed_name_me.setHint("填写收货人");
				}
				if (!phone.equals("null")) {
					ed_phone_me.setText("" + phone);
				} else {
					ed_phone_me.setHint("填写手机号码");
				}

				if (!address.equals("null")) {
					ed_address_me.setText("" + address);
					System.out.println("zuojinlaile!=null");
				} else {
					ed_address_me.setHint("请输入收货地址");
					System.out.println("zuojinlaile=null");
				}
				tv_allmoney.setText("" + amount);
				JSONArray optJSONArray = response.optJSONArray("List");
				for (int i = 0; i < optJSONArray.length(); i++) {
					try {
						JSONObject j = optJSONArray.getJSONObject(i);
						String chiMa = j.optString("chiMa");
						String productUrl = j.optString("productUrl");
						String getProductName = j.optString("productName");
						String colour = response.optString("colour");
						int price = j.optInt("price");
						int dressMoney = j.optInt("dressMoney");
						int count = j.optInt("count");
						allcount = allcount + count;
						int productId = j.optInt("productId");
						int shopCarId = j.optInt("shopCarId");
						shopcarList.add(new ShopCar(chiMa, productUrl, getProductName, price, count, productId, shopCarId,
								colour, dressMoney));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				tv_tv_count_oder.setText("" + allcount);
				lvadapter.notifyDataSetChanged();
			}
		});
	}

	class ShopCar {
		String chiMa, productUrl, productName, colour;
		int price, count, productId, shopCarId, dressMoney;

		public ShopCar(String chiMa, String productUrl, String productName, int price, int count, int productId, int shopCarId,
				String colour, int dressMoney) {
			super();
			this.chiMa = chiMa;
			this.productUrl = productUrl;
			this.productName = productName;
			this.price = price;
			this.count = count;
			this.productId = productId;
			this.shopCarId = shopCarId;
			this.colour = colour;
			this.dressMoney = dressMoney;
		}

	}

	private void getData(String url) {
		RequestParams params = new RequestParams();
		params.put("orderId", oderId);
		params.put("pushCode", pushCode);
		params.put("couponId", couponId);
		params.put("token", MyApplication.token);
		System.out.println("zuo-newtype=" + newtype);
		if (newtype == 1) {
			url = AppFinalUrl.usergetMakeUserProductByOrderId;
		}
		MyApplication.ahc.post(url, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				// phone, orderCode, address, productUrl, chiMa,
				// getProductName, createDate, productName
				System.out.println("zuo=response===" + response);
				phone = response.optString("phone");
				orderCode = response.optString("orderCode");
				address = response.optString("address");

				int dressMoney = response.optInt("dressMoney");
				productUrl = response.optString("productUrl");
				chiMa = response.optString("chiMa");
				colour = response.optString("colour");
				getProductName = response.optString("getProductName");
				createDate = response.optString("createDate");
				productName = response.optString("productName");
				// amount, count, price
				amount = response.optDouble("amount");
				count = response.optInt("count");
				price = response.optDouble("price");

				// ed_name_me, ed_phone_me, ed_address_me,
				System.out.println("zuo===" + getProductName);
				if (!getProductName.equals("null")) {
					ed_name_me.setText("" + getProductName);
				} else {
					ed_name_me.setHint("填写收货人");
				}
				if (!phone.equals("null")) {
					ed_phone_me.setText("" + phone);
				} else {
					ed_phone_me.setHint("填写手机号码");
				}

				if (!address.equals("null")) {
					ed_address_me.setText("" + address);
					System.out.println("zuojinlaile!=null");
				} else {
					ed_address_me.setHint("请输入收货地址");
					System.out.println("zuojinlaile=null");
				}

				// tv_goods_name, tv_goodstitle, tv_size_oder, tv_price,
				// tv_count_oderx;
				tv_goods_name.setText("" + productName);
				tv_goodstitle.setText("" + productName);
				tv_color_order.setText("" + colour);
				System.out.println("zuo=productName===" + productName);
				if (chiMa.equals("-1")) {
					tv_fitfee.setVisibility(View.VISIBLE);
					tv_fitfee.setText("量体裁衣费:" + dressMoney);
					ll_size.setVisibility(View.GONE);
				} else {
					tv_fitfee.setVisibility(View.GONE);
					ll_size.setVisibility(View.VISIBLE);
				}
				tv_size_oder.setText("" + chiMa);
				tv_price.setText("" + price);
				tv_count_oderx.setText("X" + count);
				tv_odername.setText("" + orderCode);
				tv_tv_count_oder.setText("" + count);
				tv_allmoney.setText("" + amount);
				// tv_tv_count_oder,tv_allmoney
				if (type == 3) {
					ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + image, im_goodspic, options);

				} else {

					ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + productUrl, im_goodspic, options);
				}
			}
		});

	}

	private void upMessage(String upurl) {
		RequestParams params = new RequestParams();
		if (ed_phone_me.getText().toString().length() != 11) {
			Toast.makeText(Activity_Order.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
			return;
		}
		if (ed_address_me.getText().toString().length() == 0) {
			Toast.makeText(Activity_Order.this, "请输入地址", Toast.LENGTH_SHORT).show();
			return;
		}
		if (ed_name_me.getText().toString().length() == 0) {
			Toast.makeText(Activity_Order.this, "请输入收货人姓名", Toast.LENGTH_SHORT).show();
			return;
		}
		if (type == 3) {
			params.put("orderId", oderId);
		} else {
			params.put("orderId", orderCode);
		}

		params.put("address", ed_address_me.getText().toString());
		params.put("content", ed_message.getText().toString());
		params.put("phone", ed_phone_me.getText().toString());
		params.put("getProductName", ed_name_me.getText().toString());
		showProgressDialog("提交中...");
		System.out.println("zuo==params=" + upurl + "   " + params.toString());
		params.put("token", MyApplication.token);
		if (newtype == 1) {
			upurl = AppFinalUrl.userupdateMakeProductOrder;
		}
		MyApplication.ahc.post(upurl, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				System.out.println("zuo==dsfstijiao=" + response.toString());
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						Intent intent = new Intent(Activity_Order.this, Activity_SelectPay.class);
						intent.putExtra("orderCode", orderCode);
						intent.putExtra("price", "" + amount);
						intent.putExtra("productname", productName);
						startActivity(intent);
					} else {
						Toast.makeText(Activity_Order.this, "提交失败", Toast.LENGTH_SHORT).show();
					}
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
		rl_back_order.setOnClickListener(this);
		tv_sure.setOnClickListener(this);
		tv_newaddress.setOnClickListener(this);
		rl_creatq.setOnClickListener(this);
		rl_selecoupon.setOnClickListener(this);

	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_order);
		initParams();
		MyApplication.addActivity(this);
	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
		myId = sp.getInt("userId", 0);
		oderId = getIntent().getStringExtra("oderId");
		ids = getIntent().getStringExtra("ids");
		type = getIntent().getIntExtra("type", 0);
		newtype = getIntent().getIntExtra("newtype", 0);
		image = getIntent().getStringExtra("image");
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return shopcarList == null ? 0 : shopcarList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return shopcarList.get(position);
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
				v = View.inflate(Activity_Order.this, R.layout.item_shopcar, null);
				h.im_gpic = (ImageView) v.findViewById(R.id.im_gpic);
				h.im_issele = (ImageView) v.findViewById(R.id.im_issele);
				h.tv_gname = (TextView) v.findViewById(R.id.tv_gname);
				h.tv_size = (TextView) v.findViewById(R.id.tv_size);
				h.tv_color = (TextView) v.findViewById(R.id.tv_color);
				h.tv_gprice = (TextView) v.findViewById(R.id.tv_gprice);
				h.tv_gnub = (TextView) v.findViewById(R.id.tv_gnub);
				h.tv_item_fitfee = (TextView) v.findViewById(R.id.tv_item_fitfee);
				h.ll_item_size = (LinearLayout) v.findViewById(R.id.ll_item_size);
				h.v_linew = v.findViewById(R.id.v_linew);
				v.setTag(h);
			} else {
				h = (MyHolder) v.getTag();
			}
			h.im_issele.setVisibility(View.GONE);
			h.tv_gname.setText("" + shopcarList.get(position).productName);
			if (shopcarList.get(position).chiMa.equals("-1")) {
				h.ll_item_size.setVisibility(View.GONE);
				h.tv_item_fitfee.setText("量体裁衣费:" + shopcarList.get(position).dressMoney);
				h.tv_item_fitfee.setVisibility(View.VISIBLE);
			} else {
				h.ll_item_size.setVisibility(View.VISIBLE);
				h.tv_item_fitfee.setVisibility(View.GONE);
			}
			h.tv_size.setText("" + shopcarList.get(position).chiMa);
			h.tv_gprice.setText("￥" + shopcarList.get(position).price);
			h.tv_gnub.setText("X" + shopcarList.get(position).count);

			ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + shopcarList.get(position).productUrl, h.im_gpic,
					options);
			if (position == (shopcarList.size() - 1)) {
				h.v_linew.setVisibility(View.GONE);
			} else {
				h.v_linew.setVisibility(View.VISIBLE);
			}
			h.tv_color.setText("" + shopcarList.get(position).colour);
			return v;
		}
	}

	class MyHolder {
		ImageView im_issele, im_gpic;
		View v_linew;
		TextView tv_gname, tv_size, tv_color, tv_gprice, tv_gnub, tv_item_fitfee;
		LinearLayout ll_item_size;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("订单");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("订单");
	}
}
