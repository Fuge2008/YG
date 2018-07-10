package com.femto.ugershop.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.photoview.PhotoView;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.twitter.Twitter;

import com.easemob.chatuidemo.activity.ChatActivity;
import com.example.AndroidCaptureCropTags.model.TagInfoModel;
import com.example.AndroidCaptureCropTags.tagview.TagsView;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.entity.Commends;
import com.femto.ugershop.entity.Flags;
import com.femto.ugershop.view.CircleImageView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_CustomGoodsDetails extends BaseActivity implements OnPageChangeListener {
	private RelativeLayout rl_back_cgd, rl_toprise, rl_flag_show, rv_onecommends_cgd, rl_addcole, rl_sharecdg;
	private TextView tv_newtocustom, tv_cgd, tv_commendsnub_cgd, tv_newnub, tv_setmode, tv_price_cgd, tv_username_cdg,
			tv_morecommends_cgd;
	private ViewPager vp_cgd;
	private MyVPAdapter vpadapter;
	private LinearLayout dots_group_cgd, ll_commends_cdg;
	private int sWidth;
	private int makeProductId;
	private List<Flags> flag;
	private CircleImageView im_head_cdg;
	private ImageView im_isprise_cdg, im_newaddco;
	private int isTop;
	private int topCount;
	private String productName;
	private double price;
	private TextView tv_colecustomde, tv_commtime, tv_phonenub, tv_commendscon, tv_rule_cdg, tv_sendcommends_cdg, tv_standard;
	private ScrollView sv_cdg;
	private RelativeLayout rl_bottom_cgd, rl_chart_cgd;
	private View v_bottom;
	private int userId;
	private String userName;
	private PopupWindow ppwRule;
	private View customView;
	private int isCollection = 0;
	private EditText ed_commend_cdg;
	private String videopath = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private ArrayList<String> pics;
	// private TagsView tv_tagsvtest;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:

				break;
			case 2:
				showShare();
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
		setPageEnd("定制商品详情");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("定制商品详情");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_cgd:
			finish();
			break;
		case R.id.rl_toprise1111:
			priseOrCancel();
			break;
		case R.id.tv_setmode:
			Intent intent = new Intent(this, Activity_NewDZ.class);
			startActivity(intent);
			break;
		case R.id.tv_newtocustom:
			Intent intent_ = new Intent(Activity_CustomGoodsDetails.this, Activity_Purchase.class);
			intent_.putExtra("productId", makeProductId);
			intent_.putExtra("title", productName);
			intent_.putExtra("price", price);
			intent_.putExtra("flag", 0);
			intent_.putExtra("type", 1);
			intent_.putExtra("newtype", 2);
			startActivity(intent_);
			break;
		case R.id.tv_colecustomde:

			break;
		case R.id.tv_morecommends_cgd:
			Intent intent_com_ = new Intent(this, Activity_Commends_GoodeDetails.class);
			intent_com_.putExtra("productId", makeProductId);
			intent_com_.putExtra("flag", 1);
			startActivity(intent_com_);
			break;
		case R.id.rl_chart_cgd:
			if (MyApplication.islogin) {
				startActivity(new Intent(this, ChatActivity.class).putExtra("name", "优格官方").putExtra("userId", "youge001"));
			} else {
				Intent intent_lo = new Intent(this, Activity_Login.class);
				startActivity(intent_lo);
			}
			break;
		case R.id.tv_rule_cdg:

			// if (ppwRule != null && ppwRule.isShowing()) {
			// // ppw_price.setFocusable(false);
			// ppwRule.dismiss();
			// } else {
			// initPpwPrice();
			// ppwRule.showAtLocation(v, Gravity.CENTER_HORIZONTAL, 1, 1);
			// }
			ArrayList<String> data = new ArrayList<String>();
			data.add(chiCunUrl);
			if (data.size() != 0) {
				Intent intent_look = new Intent(Activity_CustomGoodsDetails.this, Activity_LookPic.class);
				intent_look.putExtra("position", 0);
				intent_look.putExtra("flag", 2);
				intent_look.putStringArrayListExtra("pics", data);
				startActivity(intent_look);
			}

			break;
		case R.id.im_head_cdg:
			toMain();
			break;
		case R.id.rl_addcole:
			if (isCollection == 1) {
				deleteCollectGoods();
			} else {
				collectGoods();
			}
			break;
		case R.id.rl_sharecdg:
			if (flag.size() != 0) {
				savePic(flag.get(0).getPicPath());
			} else {
				showShare();
			}
			break;

		case R.id.tv_sendcommends_cdg:
			if (ed_commend_cdg.getText().toString().trim().length() == 0) {
				showToast("评论不能为空!", 0);
				return;
			}
			commends();
			break;
		case R.id.tv_standard:
			Intent intent_standard = new Intent(Activity_CustomGoodsDetails.this, Activity_Purchase.class);
			intent_standard.putExtra("productId", makeProductId);
			intent_standard.putExtra("title", productName);
			intent_standard.putExtra("price", price);
			intent_standard.putExtra("flag", 0);
			intent_standard.putExtra("type", 1);
			intent_standard.putExtra("newtype", 1);
			startActivity(intent_standard);
			break;
		default:
			break;
		}
	}

	// 评论
	private void commends() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("token", MyApplication.token);
		params.put("discuss.content", ed_commend_cdg.getText().toString().trim());
		params.put("discuss.userProduct.id", makeProductId);
		params.put("discuss.user.id", MyApplication.userId);
		showProgressDialog("评论中...");
		MyApplication.ahc.post(AppFinalUrl.useraddDiscussUserToMakeProduct, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				String result = response.optString("result");
				dismissProgressDialog();
				if (result.equals("0")) {
					ed_commend_cdg.setText("");
					showToast("评论成功!", 0);
					getCommends();

				} else {
					showToast("评论失败!", 0);
				}
			}
		});
	}

	// if (showList.size() != 0) {
	// savePic(showList.get(0));
	// } else {
	// showShare();
	// }
	public void initPpwPrice() {
		customView = View.inflate(this, R.layout.popurule, null);
		ppwRule = new PopupWindow(customView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, false);
		TextView tv_iknow = (TextView) customView.findViewById(R.id.tv_iknow);
		PhotoView im_picsize = (PhotoView) customView.findViewById(R.id.im_picsize);
		im_picsize.setVisibility(View.VISIBLE);
		ImageLoader.getInstance().displayImage(chiCunUrl, im_picsize, MyApplication.getOptions());
		TextView tv_rule_con = (TextView) customView.findViewById(R.id.tv_rule_con);
		tv_rule_con.setVisibility(View.GONE);
		tv_rule_con
				.setText(" 制作规则：\n上图制作：优格梦工厂根据您提供的图片制作成衣，但优格不制作成衣的商标和涉及侵权的产品。\n生产说明：是由优格进行统一生产，由于每种商品的生产时间不同，每款的生产周期也有所不同。每款商品均由手工制作，完全保障质量。\n退换货说明：七天无理由退换货是指消费者成功交易后，在签收货物后7天内，除质量问题，优格不接受退换货。\n运费说明：优格承担运费。");
		tv_iknow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (ppwRule != null && ppwRule.isShowing()) {
					ppwRule.dismiss();
					ppwRule = null;
				}

			}
		});
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub

		out = new File(videopath);

		if (!out.exists()) {
			out.mkdirs();
		}
		videopath = out.getPath();

		rl_back_cgd = (RelativeLayout) findViewById(R.id.rl_back_cgd);
		rl_bottom_cgd = (RelativeLayout) findViewById(R.id.rl_bottom_cgd);
		rl_toprise = (RelativeLayout) findViewById(R.id.rl_toprise1111);
		rv_onecommends_cgd = (RelativeLayout) findViewById(R.id.rv_onecommends_cgd);
		rl_flag_show = (RelativeLayout) findViewById(R.id.rl_flag_show);
		rl_chart_cgd = (RelativeLayout) findViewById(R.id.rl_chart_cgd);
		rl_addcole = (RelativeLayout) findViewById(R.id.rl_addcole);
		rl_sharecdg = (RelativeLayout) findViewById(R.id.rl_sharecdg);
		tv_cgd = (TextView) findViewById(R.id.tv_cgd);
		tv_morecommends_cgd = (TextView) findViewById(R.id.tv_morecommends_cgd);
		tv_colecustomde = (TextView) findViewById(R.id.tv_colecustomde);
		tv_newtocustom = (TextView) findViewById(R.id.tv_newtocustom);
		tv_commendsnub_cgd = (TextView) findViewById(R.id.tv_commendsnub_cgd);
		tv_setmode = (TextView) findViewById(R.id.tv_setmode);
		tv_price_cgd = (TextView) findViewById(R.id.tv_price_cgd);
		tv_username_cdg = (TextView) findViewById(R.id.tv_username_cdg);
		tv_sendcommends_cdg = (TextView) findViewById(R.id.tv_sendcommends_cdg);
		vp_cgd = (ViewPager) findViewById(R.id.vp_cgd);
		dots_group_cgd = (LinearLayout) findViewById(R.id.dots_group_cgd);
		im_head_cdg = (CircleImageView) findViewById(R.id.im_head_cdg);
		im_isprise_cdg = (ImageView) findViewById(R.id.im_isprise_cdg);
		// / tv_tagsvtest = (TagsView) findViewById(R.id.tv_tagsvtest);
		tv_commtime = (TextView) findViewById(R.id.tv_commtime);
		tv_standard = (TextView) findViewById(R.id.tv_standard);
		tv_commendscon = (TextView) findViewById(R.id.tv_commendscon);
		tv_phonenub = (TextView) findViewById(R.id.tv_phonenub);
		tv_rule_cdg = (TextView) findViewById(R.id.tv_rule_cdg);
		tv_newnub = (TextView) findViewById(R.id.tv_newnub);
		sv_cdg = (ScrollView) findViewById(R.id.sv_cdg);
		v_bottom = findViewById(R.id.v_bottom);
		im_newaddco = (ImageView) findViewById(R.id.im_newaddco);
		ll_commends_cdg = (LinearLayout) findViewById(R.id.ll_commends_cdg);
		ed_commend_cdg = (EditText) findViewById(R.id.ed_commend_cdg);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		pics = new ArrayList<String>();
		if (MyApplication.type == 1) {
			rl_bottom_cgd.setVisibility(View.GONE);
			v_bottom.setVisibility(View.GONE);
		} else {
			rl_bottom_cgd.setVisibility(View.VISIBLE);
			v_bottom.setVisibility(View.VISIBLE);
		}
		rl_back_cgd.setOnClickListener(this);
		rl_chart_cgd.setOnClickListener(this);
		tv_standard.setOnClickListener(this);
		vp_cgd.setOnPageChangeListener(this);
		rl_toprise.setOnClickListener(this);
		rl_sharecdg.setOnClickListener(this);
		tv_morecommends_cgd.setOnClickListener(this);
		tv_setmode.setOnClickListener(this);
		tv_sendcommends_cdg.setOnClickListener(this);
		rl_addcole.setOnClickListener(this);
		im_head_cdg.setOnClickListener(this);
		tv_colecustomde.setOnClickListener(this);
		tv_newtocustom.setOnClickListener(this);
		tv_rule_cdg.setOnClickListener(this);
		flag = new ArrayList<Flags>();
		vpadapter = new MyVPAdapter();
		vp_cgd.setAdapter(vpadapter);

		tv_commendsnub_cgd.setText("评论(" + "0)");
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vp_cgd.getLayoutParams();
		params.height = (int) (sWidth);
		params.width = sWidth;
		vp_cgd.setLayoutParams(params);
		getData();
		getCommends();
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_customgoodsdetails);
		sWidth = getWith();
		initParams();
	}

	private void initParams() {
		// TODO Auto-generated method stub
		makeProductId = getIntent().getIntExtra("makeProductId", 0);
	}

	private String chiCunUrl;

	private void getData() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("makeProductId", makeProductId);
		params.put("userId", MyApplication.userId);
		params.put("token", MyApplication.token);
		showProgressDialog("加载中...");
		MyApplication.ahc.post(AppFinalUrl.usergetMakeProductById, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuosss+" + response.toString());
				dismissProgressDialog();
				sv_cdg.setVisibility(View.VISIBLE);
				int makeProductId = response.optInt("makeProductId");
				userId = response.optInt("userId");
				isTop = response.optInt("isTop");
				isCollection = response.optInt("isCollection");
				topCount = response.optInt("topCount");
				type = response.optInt("type");
				price = response.optDouble("price");
				userName = response.optString("userName");
				productName = response.optString("productName");
				chiCunUrl = response.optString("chiCunUrl");
				String userImg = response.optString("userImg");
				if (isCollection == 1) {
					im_newaddco.setImageResource(R.drawable.newcancelcole);
				} else {
					im_newaddco.setImageResource(R.drawable.newaddcole);
				}
				setMessage(makeProductId, userId, isTop, topCount, price, userName, productName, userImg);
				JSONArray optJSONArray = response.optJSONArray("list");
				if (optJSONArray != null) {
					for (int i = 0; i < optJSONArray.length(); i++) {
						JSONObject j = optJSONArray.optJSONObject(i);
						String smallUrl = j.optString("smallUrl");
						String url = j.optString("url");
						int high = j.optInt("high");
						int width = j.optInt("width");
						pics.add(url);
						List<TagInfoModel> tagInfos = new ArrayList<TagInfoModel>();
						JSONArray optJSONArray2 = j.optJSONArray("labelList");
						if (optJSONArray2 != null) {
							for (int k = 0; k < optJSONArray2.length(); k++) {
								JSONObject jj = optJSONArray2.optJSONObject(k);
								String y = jj.optString("y");
								String direction = jj.optString("direction");
								String x = jj.optString("x");
								String labelName = jj.optString("labelName");
								TagInfoModel tag = new TagInfoModel();
								tag.tag_name = labelName;
								tag.x = Float.parseFloat(x);
								tag.y = Float.parseFloat(y);
								tagInfos.add(tag);
							}
						}
						flag.add(new Flags(url, tagInfos));

					}
					initSmallDot(0);
					vpadapter.notifyDataSetChanged();
				}
			}

		});
	}

	private void setMessage(int makeProductId, int userId, int isTop, int topCount, double price, String userName,
			String productName, String userImg) {
		// TODO Auto-generated method stub
		tv_cgd.setText("" + productName);
		tv_price_cgd.setText("¥ " + price);
		tv_username_cdg.setText("" + userName);
		ImageLoader.getInstance().displayImage(userImg, im_head_cdg, MyApplication.getOptions());
		if (isTop == 1) {
			im_isprise_cdg.setImageResource(R.drawable.newprised);
		} else {
			im_isprise_cdg.setImageResource(R.drawable.newtoprise);
		}
		tv_newnub.setText("" + topCount);
	}

	class MYPhoto {
		String smallUrl, url;
		int high, width;
		List<Lable> labs;

		public MYPhoto(String smallUrl, String url, int high, int width, List<Lable> labs) {
			super();
			this.smallUrl = smallUrl;
			this.url = url;
			this.high = high;
			this.width = width;
			this.labs = labs;
		}

	}

	class Lable {
		String y, direction, x, labelName;

		public Lable(String y, String direction, String x, String labelName) {
			super();
			this.y = y;
			this.direction = direction;
			this.x = x;
			this.labelName = labelName;
		}
	}

	private void initSmallDot(int index) {
		if (Activity_CustomGoodsDetails.this != null) {
			dots_group_cgd.removeAllViews();
			for (int i = 0; i < flag.size(); i++) {
				ImageView imageView = new ImageView(Activity_CustomGoodsDetails.this);
				imageView.setImageResource(R.drawable.round);
				imageView.setPadding(5, 0, 5, 0);
				dots_group_cgd.addView(imageView);
			}
			if (flag.size() != 0) {
				// 设置选中项
				((ImageView) dots_group_cgd.getChildAt(index)).setImageResource(R.drawable.round_select);
			}

		}
		// tv_tagsvtest.setTagInfoModels(flag.get(0).getTagInfos());
		// tv_tagsvtest.setImage(flag.get(0).getPicPath(), this);
	}

	class MyVPAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return flag.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			// TagsView tv_tagsView = new
			// TagsView(Activity_CustomGoodsDetails.this);

			View v = View.inflate(Activity_CustomGoodsDetails.this, R.layout.item_flags, null);
			container.addView(v, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			TagsView tv_tagsView = (TagsView) v.findViewById(R.id.tv_tagsView);
			tv_tagsView.setImage(flag.get(position).getPicPath(), Activity_CustomGoodsDetails.this);
			tv_tagsView.setTagInfoModels(flag.get(position).getTagInfos());
			tv_tagsView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Activity_CustomGoodsDetails.this, Activity_LookPic.class);
					intent.putExtra("position", position);
					intent.putExtra("flag", 2);
					intent.putStringArrayListExtra("pics", pics);
					startActivity(intent);
				}
			});
			return v;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		initSmallDot(arg0);
	}

	// 点赞或取消
	private void priseOrCancel() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("makeProductId", makeProductId);
		params.put("userId", MyApplication.userId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.useraddTopToMakeProduct, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				String result = response.optString("result");
				if (result != null && result.equals("0")) {
					if (isTop == 1) {
						if (topCount > 0) {
							topCount--;
							isTop = 0;
							Toast.makeText(Activity_CustomGoodsDetails.this, "已取消赞", Toast.LENGTH_SHORT).show();
						}

					} else {
						topCount++;
						isTop = 1;
						Toast.makeText(Activity_CustomGoodsDetails.this, "点赞成功", Toast.LENGTH_SHORT).show();
					}
					tv_newnub.setText("" + topCount);
					if (isTop == 1) {
						im_isprise_cdg.setImageResource(R.drawable.newprised);
						im_isprise_cdg.setAnimation(MyApplication.getAni(Activity_CustomGoodsDetails.this, R.anim.prise_ani));
					} else {
						im_isprise_cdg.setImageResource(R.drawable.newtoprise);
						im_isprise_cdg.setAnimation(MyApplication.getAni(Activity_CustomGoodsDetails.this, R.anim.prise_ani));
					}
				}
			}
		});
	}

	// 收藏
	private void collectGoods() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("collection.user.id", MyApplication.userId);
		params.put("collection.userProduct.id", makeProductId);
		showProgressDialog("收藏中...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.useraddUserProductCollection, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo------" + response.toString());
				dismissProgressDialog();
				String result = response.optString("result");
				String message = response.optString("message");
				showToast("" + message, 0);
				if (result.equals("0")) {
					isCollection = 1;
					if (isCollection == 1) {
						im_newaddco.setImageResource(R.drawable.newcancelcole);
					} else {
						im_newaddco.setImageResource(R.drawable.newaddcole);
					}
				}

			}
		});
	}

	// 取消收藏

	private void deleteCollectGoods() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("collection.user.id", MyApplication.userId);
		params.put("collection.userProduct.id", makeProductId);
		showProgressDialog("取消中...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userdeleteCollection, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo------" + response.toString());
				dismissProgressDialog();
				String result = response.optString("result");
				String message = response.optString("message");
				showToast("" + message, 0);
				if (result.equals("0")) {
					isCollection = 0;
					if (isCollection == 1) {
						im_newaddco.setImageResource(R.drawable.newcancelcole);
					} else {
						im_newaddco.setImageResource(R.drawable.newaddcole);
					}
				}
			}
		});
	}

	private Commends cs;
	private int type;

	// 获取评论
	private void getCommends() {
		RequestParams params = new RequestParams();
		params.put("product.id", makeProductId);
		params.put("pageModel.pageIndex", 1);
		params.put("pageModel.pageSize", 1);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetDiscusssByMakeProductId, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo===" + response.toString());

				try {
					JSONArray jsonArray = response.getJSONArray("dicussList");
					String count = response.optString("count");
					tv_commendsnub_cgd.setText("评论(" + count + ")");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						String content = j.optString("content");
						String userName = j.optString("userName");
						String createDate = j.optString("createDate");
						String url = j.optString("url");
						int dicussId = j.optInt("dicussId");
						int userId = j.optInt("userId");
						cs = new Commends(content, userName, createDate, url, dicussId, userId);
					}
					if (cs != null) {
						setCommends(cs);
						tv_morecommends_cgd.setVisibility(View.VISIBLE);
						rv_onecommends_cgd.setVisibility(View.VISIBLE);
					} else {

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	// 设置评论
	private void setCommends(Commends cs) {
		// TODO Auto-generated method stub
		tv_commtime.setText("" + cs.getCreateDate());
		tv_commendscon.setText("" + cs.getContent());
		tv_phonenub.setText("" + cs.getUserName());
	}

	private void toMain() {
		if (userId == 0) {
			return;
		}
		if (type == 1) {
			Intent intent = new Intent(Activity_CustomGoodsDetails.this, Activity_Designer.class);
			intent.putExtra("userId", userId);
			startActivity(intent);
		} else if (type == 2) {
			Intent intent = new Intent(Activity_CustomGoodsDetails.this, Activity_CustomMain.class);
			intent.putExtra("userId", userId);
			startActivity(intent);
		}

	}

	private File dir;
	FileOutputStream fos;
	private File out;

	private void savePic(String urlPath) {
		dir = new File(videopath, "showpic.jpg");
		try {
			fos = new FileOutputStream(dir);
			showProgressDialog("分享中...");
			MyApplication.ahc.post(urlPath, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] data) {
					// TODO Auto-generated method stub
					System.out.println("zuo====arg0=" + arg0);
					dismissProgressDialog();
					try {
						fos.write(data);
						fos.flush();
						fos.close();
						handler.sendEmptyMessage(2);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					// TODO Auto-generated method stub
					dismissProgressDialog();
				}
			});
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dismissProgressDialog();
		}
	}

	private void showShare() {
		ShareSDK.initSDK(Activity_CustomGoodsDetails.this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		if (productName != null && !productName.equals("")) {
			oks.setTitle("来自优格：" + productName);
		} else {
			oks.setTitle("来自优格：");
		}

		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(AppFinalUrl.usergetProductIdReturnId + "?id=" + makeProductId);
		// text是分享文本，所有平台都需要这个字段

		if (productName != null && !productName.equals("")) {
			oks.setText("" + productName);
		} else {
			oks.setText("优格");
		}
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		if (dir != null) {
			oks.setImagePath(dir.getPath());// 确保SDcard下面存在此张图片
		}
		// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(AppFinalUrl.usergetProductIdReturnId + "?id=" + makeProductId);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("" + productName);
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(AppFinalUrl.usergetProductIdReturnId + "?id=" + makeProductId);
		oks.addHiddenPlatform(Twitter.NAME);
		oks.addHiddenPlatform(Facebook.NAME);
		// 启动分享GUI
		oks.show(Activity_CustomGoodsDetails.this);
	}
}
