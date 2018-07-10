package com.femto.ugershop.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.chatuidemo.activity.ImageGridActivity;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.easemob.applib.utils.GetPathFromUri4kitkat;
import com.femto.ugershop.selepic.ImgFileListActivity;
import com.femto.ugershop.view.MyGridView;
import com.iqiyi.sdk.android.vcop.api.ReturnCode;
import com.iqiyi.sdk.android.vcop.api.UploadResultListener;
import com.iqiyi.sdk.android.vcop.api.VCOPClient;
import com.iqiyi.sdk.android.vcop.api.VCOPException;
import com.iqiyi.sdk.android.vcop.authorize.Authorize2AccessToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.VideoColumns;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Activity_GoodUp extends SwipeBackActivity {
	private RelativeLayout rl_back_gu;
	private MBC mbc;
	private ArrayList<String> pic_log;
	private int flag = 1;// 1为封面选择，2，为设计师介绍，3，设计图稿，4，模特展示，5，相关图片；
	private ViewPager im_vp_log, im_vp_sjtg, im_vp_mode, im_vp_cz, im_vp_sjsjs;
	private String mTestKey = "b5be2e81706541f2880202c04037ac61";
	private String mTestKeySecret = "99409cea12c2367c81f0eb0d4fc7dce8";
	private ImageView im_log, im_selevideoorpic, im_tugao, im_mode, im_cz, im_selevideoorpic_play;
	private String VvvideoPath;
	public static VCOPClient vcopClient;
	private static final String ACCESS_TOKEN_PATH = "/sdcard/Android/data/com.iqiyi.sdk.android.demo/";
	private int type = 1;
	private int clothId = -1;
	private TextView tv_gup;
	private EditText ed_post_up, ed_email_up;
	private Authorize2AccessToken currtoken;
	private List<String> pic_sjtg;
	private List<String> pic_mode;
	private List<String> pic_cz;
	private List<String> pic_sjsjs;
	private List<String> up_log;
	private List<String> up_mode;
	private List<String> up_cz;
	private List<String> up_sjtg;
	private List<String> up_sjsjs;
	private List<ProductJson> productJson;
	private List<CothTypy> coths;
	private RelativeLayout rl_sele_video;
	private int sex = 0, ctype = 0;
	private ListView lv_ppwtow;
	private int isvideo = 2;// 1为视频，2为图片
	private View customViewone, customViewtwo;
	private MyPopuLVAdapter plvadapter;
	private PopupWindow ppw_one;
	private SeekBar sb;
	private List<Rank> rank;
	private double perCen, upNub, downNub;
	private TextView tv_seletype, tv_seletype_name, tv_salseprice, tv_downp, tv_upp;
	private EditText ed_name_up, ed_price, ed_beishu, ed_sjjs, ed_wlqd, ed_caizhi, ed_sjbt;
	String videopath = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				dismissProgressDialog();
				Toast.makeText(Activity_GoodUp.this, "发表成功", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case 2:
				dismissProgressDialog();
				Toast.makeText(Activity_GoodUp.this, "发表失败", Toast.LENGTH_SHORT).show();

				break;

			default:
				break;
			}
		};
	};
	private int myId;
	private String fileid = "";
	private DecimalFormat df;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_gu:

			finish();
			break;
		case R.id.tv_gup:
			if (ed_name_up.getText().toString().length() == 0) {
				Toast.makeText(Activity_GoodUp.this, "请输入商品名", Toast.LENGTH_SHORT).show();
				dismissProgressDialog();
				return;
			}
			if (ed_price.getText().toString().length() == 0) {
				Toast.makeText(Activity_GoodUp.this, "请输入价格", Toast.LENGTH_SHORT).show();
				dismissProgressDialog();
				return;
			}
			// if (ed_sjbt.getText().toString().length() == 0) {
			// Toast.makeText(Activity_GoodUp.this, "请输入设计标题",
			// Toast.LENGTH_SHORT).show();
			// dismissProgressDialog();
			// return;
			// }
			if (ed_sjjs.getText().toString().length() == 0) {
				Toast.makeText(Activity_GoodUp.this, "请输入设计介绍", Toast.LENGTH_SHORT).show();
				dismissProgressDialog();
				return;
			}
			if (ed_wlqd.getText().toString().length() == 0) {
				Toast.makeText(Activity_GoodUp.this, "请输入物料清单", Toast.LENGTH_SHORT).show();
				dismissProgressDialog();
				return;
			}
			if (pic_sjtg.size() == 0) {
				Toast.makeText(Activity_GoodUp.this, "请上传设计图稿", Toast.LENGTH_SHORT).show();
				dismissProgressDialog();
				return;
			}
			showProgressDialog("上传中...");
			if (isvideo == 2) {
				upGoods();
			} else {

				new Thread() {
					public void run() {
						upload();
					};
				}.start();
			}

			break;
		case R.id.im_vp_log:
			flag = 1;
			Intent intent = new Intent();
			intent.putExtra("nub", 100);
			intent.setClass(this, ImgFileListActivity.class);
			startActivity(intent);
			break;
		// log图片只能选择三种
		case R.id.im_log:
			flag = 1;
			Intent intent_ = new Intent();
			intent_.putExtra("nub", 100);
			intent_.setClass(this, ImgFileListActivity.class);
			startActivity(intent_);
			break;
		case R.id.im_tugao:
			flag = 3;
			Intent intent_tugao = new Intent();
			intent_tugao.putExtra("nub", 100);
			intent_tugao.setClass(this, ImgFileListActivity.class);
			startActivity(intent_tugao);
			break;
		case R.id.im_vp_sjtg:
			flag = 3;
			Intent intent_sjtg = new Intent();
			intent_sjtg.putExtra("nub", 100);
			intent_sjtg.setClass(this, ImgFileListActivity.class);
			startActivity(intent_sjtg);
			break;
		case R.id.im_mode:
			flag = 4;
			Intent intent_mode = new Intent();
			intent_mode.putExtra("nub", 100);
			intent_mode.setClass(this, ImgFileListActivity.class);
			startActivity(intent_mode);
			break;
		case R.id.im_vp_mode:
			flag = 4;
			Intent intent_vp_mode = new Intent();
			intent_vp_mode.putExtra("nub", 100);
			intent_vp_mode.setClass(this, ImgFileListActivity.class);
			startActivity(intent_vp_mode);
			break;
		case R.id.im_vp_cz:
			flag = 5;
			Intent intent_vp_cz = new Intent();
			intent_vp_cz.putExtra("nub", 100);
			intent_vp_cz.setClass(this, ImgFileListActivity.class);
			startActivity(intent_vp_cz);
			break;
		case R.id.im_cz:
			flag = 5;
			Intent intent_cz = new Intent();
			intent_cz.putExtra("nub", 100);
			intent_cz.setClass(this, ImgFileListActivity.class);
			startActivity(intent_cz);
			break;
		case R.id.im_selevideoorpic:
			showPhotoDialog();
			// if (type == 1) {
			// seleVideo();
			// type = 2;
			// } else if (type == 2) {
			// shouquan();
			// type = 3;
			// } else if (type == 3) {

			//
			// type = 1;
			// }

			break;
		case R.id.rl_sele_video:
			showPhotoDialog();
			break;
		case R.id.tv_seletype:
			if (ppw_one != null && ppw_one.isShowing()) {
				ppw_one.dismiss();

			} else {
				initPpwone();

				ppw_one.showAsDropDown(v, 0, 0);

			}
			break;
		default:
			break;
		}
	}

	private void upGoods() {
		// ed_name_up, ed_price, ed_beishu, ed_sjjs, ed_wlqd
		if (ed_name_up.getText().toString().length() == 0) {
			Toast.makeText(Activity_GoodUp.this, "请输入商品名", Toast.LENGTH_SHORT).show();
			dismissProgressDialog();
			return;
		}
		if (ed_price.getText().toString().length() == 0) {
			Toast.makeText(Activity_GoodUp.this, "请输入价格", Toast.LENGTH_SHORT).show();
			dismissProgressDialog();
			return;
		}
		if (ed_sjbt.getText().toString().length() == 0) {
			Toast.makeText(Activity_GoodUp.this, "请输入设计标题", Toast.LENGTH_SHORT).show();
			dismissProgressDialog();
			return;
		}
		if (ed_sjjs.getText().toString().length() == 0) {
			Toast.makeText(Activity_GoodUp.this, "请输入设计介绍", Toast.LENGTH_SHORT).show();
			dismissProgressDialog();
			return;
		}
		if (ed_wlqd.getText().toString().length() == 0) {
			Toast.makeText(Activity_GoodUp.this, "请输入物料清单", Toast.LENGTH_SHORT).show();
			dismissProgressDialog();
			return;
		}
		if (pic_sjtg.size() == 0) {
			Toast.makeText(Activity_GoodUp.this, "请上传设计图稿", Toast.LENGTH_SHORT).show();
			dismissProgressDialog();
			return;
		}
		if (clothId == -1) {
			Toast.makeText(Activity_GoodUp.this, "请选择类别", Toast.LENGTH_SHORT).show();
			dismissProgressDialog();
			return;
		}
		if (ed_beishu.getText().toString().length() == 0) {
			Toast.makeText(Activity_GoodUp.this, "请倍数", Toast.LENGTH_SHORT).show();
			dismissProgressDialog();
			return;
		}
		// if (ed_post_up.getText().toString().length() == 0) {
		// Toast.makeText(Activity_GoodUp.this, "请输入快递单号",
		// Toast.LENGTH_SHORT).show();
		// dismissProgressDialog();
		// return;
		// }
		// if (ed_email_up.getText().toString().length() == 0) {
		// Toast.makeText(Activity_GoodUp.this, "请输入邮箱",
		// Toast.LENGTH_SHORT).show();
		// dismissProgressDialog();
		// return;
		// }

		new Thread() {
			public void run() {

				for (int i = 0; i < pic_log.size(); i++) {
					float yaso = 0;
					if ((new File(pic_log.get(i))).length() > 4000000) {
						yaso = 0.2f;
					} else if ((new File(pic_log.get(i))).length() > 2000000 && (new File(pic_log.get(i))).length() < 4000000) {
						yaso = 0.3f;
					} else if ((new File(pic_log.get(i))).length() > 1000000 && (new File(pic_log.get(i))).length() < 2000000) {
						yaso = 0.5f;
					} else if (new File(pic_log.get(i)).length() < 1000000 && new File(pic_log.get(i)).length() > 600000) {
						yaso = 0.7f;
					} else if (new File(pic_log.get(i)).length() < 600000) {
						yaso = 0.9f;
					}
					Bitmap resize_img = resize_img(BitmapFactory.decodeFile(pic_log.get(i)), yaso);
					File saveMyBitmap = saveMyBitmap("imagelog" + i + ".jpg", resize_img);
					up_log.add(saveMyBitmap.getPath().toString());

					System.out.println("zuo===file==" + saveMyBitmap.getPath().toString() + "     前 size="
							+ (new File(pic_log.get(i))).length() + "   后size=" + saveMyBitmap.length());

				}
				for (int i = 0; i < pic_sjsjs.size(); i++) {
					float yaso = 0;
					if ((new File(pic_sjsjs.get(i))).length() > 4000000) {
						yaso = 0.1f;
					} else if ((new File(pic_sjsjs.get(i))).length() > 2000000 && (new File(pic_sjsjs.get(i))).length() < 4000000) {
						yaso = 0.2f;
					} else if ((new File(pic_sjsjs.get(i))).length() > 1000000 && (new File(pic_sjsjs.get(i))).length() < 2000000) {
						yaso = 0.2f;
					} else if (new File(pic_sjsjs.get(i)).length() < 1000000 && new File(pic_sjsjs.get(i)).length() > 600000) {
						yaso = 0.6f;
					} else if (new File(pic_sjsjs.get(i)).length() < 600000) {
						yaso = 0.8f;
					}
					Bitmap resize_img = resize_img(BitmapFactory.decodeFile(pic_sjsjs.get(i)), yaso);
					File saveMyBitmap = saveMyBitmap("imagesjsjs" + i + ".jpg", resize_img);
					up_sjsjs.add(saveMyBitmap.getPath().toString());

					System.out.println("zuo===file==" + saveMyBitmap.getPath().toString() + "     前 size="
							+ (new File(pic_sjsjs.get(i))).length() + "   后size=" + saveMyBitmap.length());

				}
				for (int i = 0; i < pic_cz.size(); i++) {
					float yaso = 0;
					if ((new File(pic_cz.get(i))).length() > 4000000) {
						yaso = 0.1f;
					} else if ((new File(pic_cz.get(i))).length() > 2000000 && (new File(pic_cz.get(i))).length() < 4000000) {
						yaso = 0.2f;
					} else if ((new File(pic_cz.get(i))).length() > 1000000 && (new File(pic_cz.get(i))).length() < 2000000) {
						yaso = 0.2f;
					} else if (new File(pic_cz.get(i)).length() < 1000000 && new File(pic_cz.get(i)).length() > 600000) {
						yaso = 0.6f;
					} else if (new File(pic_cz.get(i)).length() < 600000) {
						yaso = 0.8f;
					}
					Bitmap resize_img = resize_img(BitmapFactory.decodeFile(pic_cz.get(i)), yaso);
					File saveMyBitmap = saveMyBitmap("imagecz" + i + ".jpg", resize_img);
					up_cz.add(saveMyBitmap.getPath().toString());
					System.out.println("zuo===file==" + saveMyBitmap.getPath().toString() + "     前 size="
							+ (new File(pic_cz.get(i))).length() + "   后size=" + saveMyBitmap.length());

				}
				for (int i = 0; i < pic_mode.size(); i++) {
					float yaso = 0;
					if ((new File(pic_mode.get(i))).length() > 4000000) {
						yaso = 0.1f;
					} else if ((new File(pic_mode.get(i))).length() > 2000000 && (new File(pic_mode.get(i))).length() < 4000000) {
						yaso = 0.2f;
					} else if ((new File(pic_mode.get(i))).length() > 1000000 && (new File(pic_mode.get(i))).length() < 2000000) {
						yaso = 0.2f;
					} else if (new File(pic_mode.get(i)).length() < 1000000 && new File(pic_mode.get(i)).length() > 600000) {
						yaso = 0.6f;
					} else if (new File(pic_mode.get(i)).length() < 600000) {
						yaso = 0.8f;
					}
					Bitmap resize_img = resize_img(BitmapFactory.decodeFile(pic_mode.get(i)), yaso);
					File saveMyBitmap = saveMyBitmap("imagemode" + i + ".jpg", resize_img);
					up_mode.add(saveMyBitmap.getPath().toString());
					System.out.println("zuo===file==" + saveMyBitmap.getPath().toString() + "     前 size="
							+ (new File(pic_mode.get(i))).length() + "   后size=" + saveMyBitmap.length());
				}
				for (int i = 0; i < pic_sjtg.size(); i++) {
					float yaso = 0;
					if ((new File(pic_sjtg.get(i))).length() > 4000000) {
						yaso = 0.1f;
					} else if ((new File(pic_sjtg.get(i))).length() > 2000000 && (new File(pic_sjtg.get(i))).length() < 4000000) {
						yaso = 0.2f;
					} else if ((new File(pic_sjtg.get(i))).length() > 1000000 && (new File(pic_sjtg.get(i))).length() < 2000000) {
						yaso = 0.2f;
					} else if (new File(pic_sjtg.get(i)).length() < 1000000 && new File(pic_sjtg.get(i)).length() > 600000) {
						yaso = 0.6f;
					} else if (new File(pic_sjtg.get(i)).length() < 600000) {
						yaso = 0.8f;
					}
					Bitmap resize_img = resize_img(BitmapFactory.decodeFile(pic_sjtg.get(i)), yaso);
					File saveMyBitmap = saveMyBitmap("imagesjtg" + i + ".jpg", resize_img);
					up_sjtg.add(saveMyBitmap.getPath().toString());
					System.out.println("zuo===file==" + saveMyBitmap.getPath().toString() + "     前 size="
							+ (new File(pic_sjtg.get(i))).length() + "   后size=" + saveMyBitmap.length());
				}
				new Thread() {
					public void run() {
						post(AppFinalUrl.useruploadProduct, ddd(up_log, up_sjsjs, up_sjtg, up_mode, up_cz));
					};
				}.start();

			};
		}.start();

	}

	@Override
	public void initView() {
		df = new DecimalFormat("######0.0");
		MyApplication.addActivity(this);
		initIQIYI();
		pic_log = new ArrayList<String>();
		pic_sjtg = new ArrayList<String>();
		pic_mode = new ArrayList<String>();
		pic_cz = new ArrayList<String>();
		up_log = new ArrayList<String>();
		up_mode = new ArrayList<String>();
		up_cz = new ArrayList<String>();
		up_sjtg = new ArrayList<String>();
		pic_sjsjs = new ArrayList<String>();
		up_sjsjs = new ArrayList<String>();
		productJson = new ArrayList<ProductJson>();
		coths = new ArrayList<CothTypy>();
		rank = new ArrayList<Rank>();
		rl_sele_video = (RelativeLayout) findViewById(R.id.rl_sele_video);
		rl_back_gu = (RelativeLayout) findViewById(R.id.rl_back_gu);
		im_vp_log = (ViewPager) findViewById(R.id.im_vp_log);
		im_vp_sjtg = (ViewPager) findViewById(R.id.im_vp_sjtg);
		im_vp_mode = (ViewPager) findViewById(R.id.im_vp_mode);
		im_vp_cz = (ViewPager) findViewById(R.id.im_vp_cz);
		im_vp_sjsjs = (ViewPager) findViewById(R.id.im_vp_sjsjs);
		im_log = (ImageView) findViewById(R.id.im_log);
		im_tugao = (ImageView) findViewById(R.id.im_tugao);
		im_selevideoorpic_play = (ImageView) findViewById(R.id.im_selevideoorpic_play);
		im_mode = (ImageView) findViewById(R.id.im_mode);
		tv_gup = (TextView) findViewById(R.id.tv_gup);
		im_cz = (ImageView) findViewById(R.id.im_cz);
		im_selevideoorpic = (ImageView) findViewById(R.id.im_selevideoorpic);
		ed_email_up = (EditText) findViewById(R.id.ed_email_up);
		ed_post_up = (EditText) findViewById(R.id.ed_email_up);
		// ed_name_up, ed_price, ed_beishu, ed_sjjs, ed_wlqd;
		// tv_downp, tv_upp
		tv_downp = (TextView) findViewById(R.id.tv_downp);
		tv_upp = (TextView) findViewById(R.id.tv_upp);
		ed_name_up = (EditText) findViewById(R.id.ed_name_up);
		ed_price = (EditText) findViewById(R.id.ed_price);
		ed_beishu = (EditText) findViewById(R.id.ed_beishu);
		ed_sjjs = (EditText) findViewById(R.id.ed_sjjs);
		ed_wlqd = (EditText) findViewById(R.id.ed_wlqd);
		ed_caizhi = (EditText) findViewById(R.id.ed_caizhi);
		ed_sjbt = (EditText) findViewById(R.id.ed_sjbt);
		tv_seletype = (TextView) findViewById(R.id.tv_seletype);
		tv_salseprice = (TextView) findViewById(R.id.tv_salseprice);
		tv_seletype_name = (TextView) findViewById(R.id.tv_seletype_name);
		ed_beishu.setFocusable(false);
		ed_beishu.setFocusableInTouchMode(false);
		sb = (SeekBar) findViewById(R.id.sb1);
		plvadapter = new MyPopuLVAdapter();
		getData();
		getSeekBarData();
	}

	private void getSeekBarData() {
		RequestParams params = new RequestParams();
		params.put("userId", myId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMoneyLevel, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					// upNub, downNub
					upNub = response.optDouble("upNub");
					downNub = response.optDouble("downNub");
					JSONArray jsonArray = response.getJSONArray("list");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						double perCen = j.optDouble("perCen");
						double down = j.optDouble("down");
						double up = j.optDouble("up");
						int sorce = j.optInt("sorce");
						String name = j.optString("name");
						rank.add(new Rank(perCen, down, up, sorce, name));
					}
					setSeekBar();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	private void setSeekBar() {
		// tv_downp, tv_upp
		tv_downp.setText("" + downNub);
		tv_upp.setText("" + upNub);
		sb.setMax(100);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				System.out.println("zuo===" + progress);
				if (ed_price.getText().toString().length() != 0) {
					ed_beishu.setText("" + df.format((progress / (double) 100 + downNub)));
					tv_salseprice.setText(""
							+ df.format(Double.parseDouble("" + ed_price.getText().toString())
									* (progress / (double) 100 + downNub)) + "元");
				}

			}
		});
	}

	class Rank {
		double perCen, down, up;
		int sorce;
		String name;

		public Rank(double perCen, double down, double up, int sorce, String name) {
			super();
			this.perCen = perCen;
			this.down = down;
			this.up = up;
			this.sorce = sorce;
			this.name = name;
		}
	}

	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}

	private void initIQIYI() {
		ApplicationInfo info;
		try {
			info = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
			mTestKey = info.metaData.getString("APPKEY");
			mTestKeySecret = info.metaData.getString("APPSECRET");

		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Authorize2AccessToken token = this.getLocalAccessToken();
		if (token != null) {
			// Log.d(TAG, token.toString());
		}
		vcopClient = new VCOPClient(mTestKey, mTestKeySecret, token);
		shouquan();
	}

	public void shouquan() {
		new Thread() {
			public void run() {
				ReturnCode code = vcopClient.authorize("2308108271");
				if (code.isSuccess()) {
					setLocalAccessToken(vcopClient.getToken());
					Authorize2AccessToken token = vcopClient.getToken();
					currtoken = token;
					setLocalAccessToken(token);
					System.out.println("zuotoken成功授权=" + token);
				}

			};
		}.start();

	}

	class ProductJson {
		int sexId;
		String sexName;
		List<SmallProductJy> smallProductJy;

		public ProductJson(int sexId, String sexName, List<SmallProductJy> smallProductJy) {
			super();
			this.sexId = sexId;
			this.sexName = sexName;
			this.smallProductJy = smallProductJy;
		}

	}

	class SmallProductJy {
		int sortId;
		String sortName;
		List<CothTypy> cothTypy;

		public SmallProductJy(int sortId, String sortName, List<CothTypy> cothTypy) {
			super();
			this.sortId = sortId;
			this.sortName = sortName;
			this.cothTypy = cothTypy;
		}
	}

	class CothTypy {
		int cotherId;
		String cotherName;

		public CothTypy(int cotherId, String cotherName) {
			super();
			this.cotherId = cotherId;
			this.cotherName = cotherName;
		}

	}

	public void getData() {
		RequestParams params = new RequestParams();
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetShowSort, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				try {
					JSONArray jsonArray = response.getJSONArray("productJson");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject j = jsonArray.getJSONObject(i);
						int sexId = j.optInt("sexId");
						String sexName = j.optString("sexName");
						List<SmallProductJy> smallProductJy = new ArrayList<SmallProductJy>();
						JSONArray optJSONArray = j.optJSONArray("smallProductJy");
						for (int k = 0; k < optJSONArray.length(); k++) {
							JSONObject jj = optJSONArray.getJSONObject(k);
							int sortId = jj.optInt("sortId");
							String sortName = jj.optString("sortName");
							List<CothTypy> cothTypy = new ArrayList<CothTypy>();
							JSONArray optJSONArray2 = jj.optJSONArray("cothTypy");
							for (int l = 0; l < optJSONArray2.length(); l++) {
								JSONObject jjj = optJSONArray2.getJSONObject(l);
								int cotherId = jjj.optInt("cotherId");
								String cotherName = jjj.optString("cotherName");
								cothTypy.add(new CothTypy(cotherId, cotherName));
							}

							smallProductJy.add(new SmallProductJy(sortId, sortName, cothTypy));
						}
						productJson.add(new ProductJson(sexId, sexName, smallProductJy));
					}
					coths = productJson.get(1).smallProductJy.get(0).cothTypy;
					plvadapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void initPpwone() {
		customViewone = View.inflate(this, R.layout.popu_newgoodsone, null);
		lv_ppwtow = (ListView) customViewone.findViewById(R.id.lv_poputwo);

		lv_ppwtow.setAdapter(plvadapter);
		ppw_one = new PopupWindow(customViewone, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		ppw_one.setFocusable(true);
		ppw_one.setBackgroundDrawable(new BitmapDrawable());
		lv_ppwtow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				clothId = coths.get(position).cotherId;
				tv_seletype_name.setText("" + coths.get(position).cotherName);
				// Intent intent = new Intent();
				// intent.putExtra("clothId", coths.get(position).cotherId);
				// intent.setAction("com.search.cloth");
				// getActivity().sendBroadcast(intent);
				ppw_one.dismiss();
			}
		});
		RelativeLayout rl_woman = (RelativeLayout) customViewone.findViewById(R.id.rl_woman);
		RelativeLayout rl_man = (RelativeLayout) customViewone.findViewById(R.id.rl_man);
		RelativeLayout rl_up = (RelativeLayout) customViewone.findViewById(R.id.rl_up);
		RelativeLayout rl_down = (RelativeLayout) customViewone.findViewById(R.id.rl_down);
		final ImageView im_women = (ImageView) customViewone.findViewById(R.id.im_women);
		final ImageView im_man = (ImageView) customViewone.findViewById(R.id.im_man);
		final ImageView im_up = (ImageView) customViewone.findViewById(R.id.im_up);
		final ImageView im_down = (ImageView) customViewone.findViewById(R.id.im_down);
		rl_woman.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sex = 1;
				im_women.setVisibility(View.VISIBLE);
				im_man.setVisibility(View.INVISIBLE);
				coths = productJson.get(sex).smallProductJy.get(ctype).cothTypy;
				plvadapter.notifyDataSetChanged();
			}
		});
		rl_man.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sex = 0;
				im_man.setVisibility(View.VISIBLE);
				im_women.setVisibility(View.INVISIBLE);
				coths = productJson.get(sex).smallProductJy.get(ctype).cothTypy;
				plvadapter.notifyDataSetChanged();
			}
		});
		rl_up.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ctype = 0;
				im_up.setVisibility(View.VISIBLE);
				im_down.setVisibility(View.INVISIBLE);
				coths = productJson.get(sex).smallProductJy.get(ctype).cothTypy;
				plvadapter.notifyDataSetChanged();
			}
		});
		rl_down.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ctype = 1;
				im_down.setVisibility(View.VISIBLE);
				im_up.setVisibility(View.INVISIBLE);
				coths = productJson.get(sex).smallProductJy.get(ctype).cothTypy;
				plvadapter.notifyDataSetChanged();
			}
		});

	};

	class MyPopuLVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return coths == null ? 0 : coths.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return coths.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			v = View.inflate(Activity_GoodUp.this, R.layout.item_lv_popu, null);
			TextView tv_sort_name = (TextView) v.findViewById(R.id.tv_sort_name);
			tv_sort_name.setText("" + coths.get(position).cotherName);
			return v;
		}
	}

	public void upload() {
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("file_name", ed_name_up.getText().toString() + getPhotoFileName());
		metadata.put("description", ed_sjjs.getText().toString() + getPhotoFileName());
		System.out.println("zuo===VvvideoPath=" + VvvideoPath);
		fileid = vcopClient.upload(VvvideoPath, metadata, new UploadResultListener() {

			@Override
			public void onProgress(String arg0, int progress) {
				// TODO Auto-generated method stub
				System.out.println("zuo进度=" + progress);
			}

			@Override
			public void onFinish(String arg0, Bundle arg1) {
				// TODO Auto-generated method stub
				if (fileid != "") {
					upGoods();
				} else {

				}
			}

			@Override
			public void onError(VCOPException arg0) {
				// TODO Auto-generated method stub
				System.out.println("zuoonError=" + arg0.toString());
			}
		});

		System.out.println("zuo完成fileid=" + fileid);

	}

	private void setLocalAccessToken(Authorize2AccessToken accessToken) {
		try {
			File dir = new File(ACCESS_TOKEN_PATH);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File f = new File(ACCESS_TOKEN_PATH + "accessToken.o");
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			FileOutputStream ou = new FileOutputStream(f);
			ObjectOutputStream oou = new ObjectOutputStream(ou);
			oou.writeObject(accessToken);
			oou.flush();
			oou.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Authorize2AccessToken getLocalAccessToken() {
		Authorize2AccessToken token = null;
		try {
			File f = new File(ACCESS_TOKEN_PATH + "accessToken.o");
			FileInputStream in = new FileInputStream(f);
			ObjectInputStream oin = new ObjectInputStream(in);
			token = (Authorize2AccessToken) oin.readObject();
			oin.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
		return token;
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		rl_back_gu.setOnClickListener(this);
		im_vp_log.setOnClickListener(this);
		im_log.setOnClickListener(this);
		im_vp_sjtg.setOnClickListener(this);
		im_mode.setOnClickListener(this);
		im_vp_mode.setOnClickListener(this);
		im_tugao.setOnClickListener(this);
		im_cz.setOnClickListener(this);
		im_vp_cz.setOnClickListener(this);
		tv_gup.setOnClickListener(this);
		rl_sele_video.setOnClickListener(this);
		im_selevideoorpic.setOnClickListener(this);
		tv_seletype.setOnClickListener(this);
	}

	public void seleVideo() {
		Intent intent = new Intent(this, ImageGridActivity.class);
		startActivityForResult(intent, 23);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 23 && data != null) {
			// Uri uri = data.getData();
			// if (android.os.Build.VERSION.SDK_INT >=
			// android.os.Build.VERSION_CODES.KITKAT) {
			// VvvideoPath = GetPathFromUri4kitkat.getPath(this, uri);
			// } else {
			// VvvideoPath = getPath(uri);
			// }
			VvvideoPath = data.getStringExtra("path");
			if (VvvideoPath != null) {
				im_selevideoorpic_play.setVisibility(View.VISIBLE);
			} else {
				im_selevideoorpic_play.setVisibility(View.GONE);
			}
			System.out.println("zuo==oojfsijfoiadfjoVvvideoPath==" + VvvideoPath);
		}
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Video.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public String getRealFilePath(final Context context, final Uri uri) {
		if (null == uri)
			return null;
		final String scheme = uri.getScheme();
		String data = null;
		if (scheme == null)
			data = uri.getPath();
		else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
			data = uri.getPath();
		} else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			Cursor cursor = context.getContentResolver().query(uri, new String[] { VideoColumns.DATA }, null, null, null);
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					int index = cursor.getColumnIndex(VideoColumns.DATA);
					if (index > -1) {
						data = cursor.getString(index);
					}
				}
				cursor.close();
			}
		}
		return data;
	}

	class MBC extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			List<String> f = new ArrayList<String>();
			if (action.equals("com.select.pic")) {
				f.clear();
				if (flag == 1) {
					pic_log = intent.getStringArrayListExtra("files");
					f = pic_log;
				} else if (flag == 2) {
					pic_sjsjs = intent.getStringArrayListExtra("files");
					f = pic_sjsjs;
				} else if (flag == 3) {
					pic_sjtg = intent.getStringArrayListExtra("files");
					f = pic_sjtg;
				} else if (flag == 4) {
					pic_mode = intent.getStringArrayListExtra("files");
					f = pic_mode;
				} else if (flag == 5) {
					pic_cz = intent.getStringArrayListExtra("files");
					f = pic_cz;
				}

				ArrayList<ImageView> vpData = new ArrayList<ImageView>();
				vpData.clear();
				for (int i = 0; i < f.size(); i++) {
					ImageView im = new ImageView(Activity_GoodUp.this);
					BitmapFactory.Options opts = new BitmapFactory.Options();
					opts.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(f.get(i), opts);
					opts.inSampleSize = computeInitialSampleSize(opts, -1, 299 * 299);
					// 这里一定要将其设置回false，因为之前我们将其设置成了true
					opts.inJustDecodeBounds = false;
					try {
						Bitmap bmp = BitmapFactory.decodeFile(f.get(i), opts);
						im.setImageBitmap(bmp);
					} catch (OutOfMemoryError err) {
					}
					// im.setImageBitmap(BitmapFactory.decodeFile(pic_log.get(i)));
					im.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							if (flag == 2) {
								showPhotoDialog();
							} else {
								Intent intent_ = new Intent();
								intent_.putExtra("nub", 100);
								intent_.setClass(Activity_GoodUp.this, ImgFileListActivity.class);
								startActivity(intent_);
							}
						}
					});
					vpData.add(im);
				}
				MyVPAdapter ada = new MyVPAdapter(vpData);
				if (flag == 1) {
					if (vpData.size() == 0) {

						im_log.setVisibility(View.VISIBLE);
					} else {
						im_log.setVisibility(View.GONE);
					}
				} else if (flag == 2) {
					if (vpData.size() == 0) {
						im_selevideoorpic.setVisibility(View.VISIBLE);
						im_selevideoorpic_play.setVisibility(View.GONE);
					} else {
						im_selevideoorpic.setVisibility(View.GONE);
					}

				} else if (flag == 3) {
					if (vpData.size() == 0) {
						im_tugao.setVisibility(View.VISIBLE);
					} else {
						im_tugao.setVisibility(View.GONE);
					}
				} else if (flag == 4) {
					if (vpData.size() == 0) {
						im_mode.setVisibility(View.VISIBLE);
					} else {
						im_mode.setVisibility(View.GONE);
					}
				} else if (flag == 5) {
					if (vpData.size() == 0) {
						im_cz.setVisibility(View.VISIBLE);
					} else {
						im_cz.setVisibility(View.GONE);
					}
				}

				if (flag == 1) {
					im_vp_log.setAdapter(ada);
				} else if (flag == 2) {
					im_vp_sjsjs.setAdapter(ada);
					System.out.println("zuo设计介绍");
				} else if (flag == 3) {
					im_vp_sjtg.setAdapter(ada);
				} else if (flag == 4) {
					im_vp_mode.setAdapter(ada);
				} else if (flag == 5) {
					im_vp_cz.setAdapter(ada);
				}

			}

		}
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,

	int minSideLength, int maxNumOfPixels) {

		double w = options.outWidth;

		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 :

		(int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));

		int upperBound = (minSideLength == -1) ? 128 :

		(int) Math.min(Math.floor(w / minSideLength),

		Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {

			return lowerBound;

		}

		if ((maxNumOfPixels == -1) &&

		(minSideLength == -1)) {

			return 1;

		} else if (minSideLength == -1) {

			return lowerBound;

		} else {

			return upperBound;

		}

	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_goodsup);
		initParams();
		registMBC();
	}

	private void registMBC() {
		mbc = new MBC();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.select.pic");
		registerReceiver(mbc, filter);
	}

	class MyVPAdapter extends PagerAdapter {
		List<ImageView> vpData;

		public MyVPAdapter(List<ImageView> vpData) {
			super();
			this.vpData = vpData;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return vpData.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(vpData.get(position));
			return vpData.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(vpData.get(position));
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mbc);
	}

	public String post(String url, List<NameValuePair> pairs) {
		System.out.println("zuo开始上传了");
		String content = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext locaContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));
		try {
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			entity.addPart("product.productName", new StringBody(ed_name_up.getText().toString(), Charset.forName("UTF-8")));
			if (isvideo == 1) {
				entity.addPart("product.aqiyiFileId", new StringBody(fileid, Charset.forName("UTF-8")));
				for (int i = 0; i < pairs.size(); i++) {
					if (pairs.get(i).getName().equalsIgnoreCase("pic") || pairs.get(i).getName().equalsIgnoreCase("show")
							|| pairs.get(i).getName().equalsIgnoreCase("mo") || pairs.get(i).getName().equalsIgnoreCase("other")) {
						if (pairs.get(i).getValue() != null) {
							entity.addPart(pairs.get(i).getName(), new FileBody(new File(pairs.get(i).getValue())));
						}
					} else {

					}
				}
			} else {
				for (int i = 0; i < pairs.size(); i++) {
					if (pairs.get(i).getName().equalsIgnoreCase("pic") || pairs.get(i).getName().equalsIgnoreCase("show")
							|| pairs.get(i).getName().equalsIgnoreCase("desin") || pairs.get(i).getName().equalsIgnoreCase("mo")
							|| pairs.get(i).getName().equalsIgnoreCase("other")) {
						if (pairs.get(i).getValue() != null) {
							entity.addPart(pairs.get(i).getName(), new FileBody(new File(pairs.get(i).getValue())));
						}
					} else {

					}
				}
			}
			entity.addPart("product.cotherType.id", new StringBody("" + clothId, Charset.forName("UTF-8")));
			entity.addPart("product.user.id", new StringBody("" + myId, Charset.forName("UTF-8")));
			entity.addPart("product.price", new StringBody(ed_price.getText().toString(), Charset.forName("UTF-8")));
			entity.addPart("product.beishu", new StringBody("" + ed_beishu.getText().toString(), Charset.forName("UTF-8")));
			entity.addPart("product.desinTitle", new StringBody(ed_sjbt.getText().toString(), Charset.forName("UTF-8")));
			entity.addPart("product.desinInfo", new StringBody(ed_sjjs.getText().toString(), Charset.forName("UTF-8")));
			entity.addPart("product.address", new StringBody(ed_caizhi.getText().toString(), Charset.forName("UTF-8")));
			entity.addPart("product.cloth", new StringBody(ed_wlqd.getText().toString(), Charset.forName("UTF-8")));
			entity.addPart("product.info", new StringBody("这个很好看的", Charset.forName("UTF-8")));
			entity.addPart("type", new StringBody("" + isvideo, Charset.forName("UTF-8")));
			entity.addPart("product.postCode", new StringBody("" + ed_post_up.getText().toString(), Charset.forName("UTF-8")));
			entity.addPart("product.email", new StringBody("" + ed_email_up.getText().toString(), Charset.forName("UTF-8")));
			entity.addPart("token", new StringBody("" + MyApplication.token, Charset.forName("UTF-8")));
			System.out.println("zuotype===----------------------------------------clothId----==" + clothId + "   " + isvideo);
			// httpPost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
			Header contentEncoding = entity.getContentEncoding();
			httpPost.setEntity(entity);
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--", LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data";
			String CHARSET = "UTF-8";
			// StringBuilder sb1 = new StringBuilder();
			// sb1.append("Content-Type: application/octet-stream; charset="
			// + CHARSET + LINEND);
			// Header header = new
			// httpPost.setHeader(header);
			HttpEntity re = null;
			HttpResponse response = httpClient.execute(httpPost, locaContext);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				re = response.getEntity();
			}
			if (re != null) {
				content = EntityUtils.toString(re);
				System.out.println("zuo===content=" + content.toString());
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			System.out.println("zuo==ClientProtocolException==" + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("zuo==IOException==" + e.toString());
			e.printStackTrace();
		}
		System.out.println("zuo====" + content);
		if (content == null && content.length() == 0) {
			handler.sendEmptyMessage(2);
		}
		try {
			JSONObject ojj = new JSONObject(content);
			String result = ojj.getString("result");
			dismissProgressDialog();
			if (result.equals("0")) {
				handler.sendEmptyMessage(1);
			} else {
				handler.sendEmptyMessage(2);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;

	}

	public List<NameValuePair> ddd(List<String> logPath, List<String> sjtpPath, List<String> sjtgPath, List<String> modePath,
			List<String> wuliaoPath) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (int i = 0; i < logPath.size(); i++) {
			params.add(new BasicNameValuePair("show", logPath.get(i).toString()));
		}

		for (int i = 0; i < sjtpPath.size(); i++) {
			params.add(new BasicNameValuePair("desin", sjtpPath.get(i).toString()));
		}
		for (int i = 0; i < sjtgPath.size(); i++) {
			params.add(new BasicNameValuePair("pic", sjtgPath.get(i).toString()));
		}
		for (int i = 0; i < modePath.size(); i++) {
			params.add(new BasicNameValuePair("mo", modePath.get(i).toString()));
		}
		for (int i = 0; i < wuliaoPath.size(); i++) {
			params.add(new BasicNameValuePair("other", wuliaoPath.get(i).toString()));
		}
		return params;
	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		myId = sp.getInt("userId", 0);

	}

	// 压缩bitmap
	private Bitmap resize_img(Bitmap bitmap, float pc) {
		;
		Matrix matrix = new Matrix();
		// Log.i("mylog2", "缩放比例--" + pc);
		matrix.postScale(pc, pc); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

		bitmap.recycle();
		bitmap = null;
		System.gc();

		int width = resizeBmp.getWidth();
		int height = resizeBmp.getHeight();
		System.out.println("zuo====压缩后width=" + width + "   height=" + height);
		// Log.i("mylog2", "按比例缩小后宽度--" + width);
		// Log.i("mylog2", "按比例缩小后高度--" + height);

		return resizeBmp;
	}

	// 将压缩的bitmap保存到sdcard卡临时文件夹img_interim，用于上传

	public File saveMyBitmap(String filename, Bitmap bit) {

		File dir = new File(videopath);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File f = new File(videopath + filename);
		try {
			f.createNewFile();
			FileOutputStream fOut = null;
			fOut = new FileOutputStream(f);
			bit.compress(Bitmap.CompressFormat.JPEG, 99, fOut);
			fOut.flush();
			fOut.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			f = null;
			e1.printStackTrace();
		}

		return f;
	}

	private void showPhotoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(Activity_GoodUp.this);
		View view = View.inflate(Activity_GoodUp.this, R.layout.dialog_accompany, null);

		builder.setView(view);
		final AlertDialog dialog = builder.create();
		RelativeLayout rl_local = (RelativeLayout) view.findViewById(R.id.rl_localaccompany);
		TextView tv_sure = (TextView) view.findViewById(R.id.tv_sure);
		tv_sure.setText("上传图片");
		TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
		tv_cancel.setText("上传视频");
		RelativeLayout rl_sure = (RelativeLayout) view.findViewById(R.id.rl_seachaccompany);
		// 上传图片
		rl_local.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				flag = 2;
				isvideo = 2;
				Intent intent = new Intent();
				intent.putExtra("nub", 100);
				intent.setClass(Activity_GoodUp.this, ImgFileListActivity.class);
				startActivity(intent);
				dialog.dismiss();

			}
		});
		// 上传视频
		rl_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				isvideo = 1;
				seleVideo();
				dialog.dismiss();

			}
		});

		dialog.show();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("上传商品");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("上传商品");
	}
}
