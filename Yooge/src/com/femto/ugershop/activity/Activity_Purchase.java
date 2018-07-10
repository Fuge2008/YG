package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.chatuidemo.activity.ImageGridActivity;
import com.easemob.util.VoiceRecorder;
import com.femto.hx.adapter.VoicePlayClickListener;
import com.femto.hx.utils.CommonUtils;
import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.view.MyGridView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.umeng.analytics.MobclickAgent;

public class Activity_Purchase extends BaseActivity implements OnItemClickListener {
	private RelativeLayout rl_back_purchase;
	private TextView tv_surebuy, tv_tomodecar;
	private MyGridView gv_size, gv_color;
	private MyGVSizeAdapter gvsizeadapter;
	private MyGVColorAdapter gvcoloradapter;
	private ImageView im_count, im_reduce;
	private EditText ed_count;
	private int count = 1;
	private int myId;
	private DisplayImageOptions options;
	private List<String> datas_size;
	private int flag = 0, flag1 = 0;
	private int productId;
	private String title;
	private double price;
	private List<String> data_color;
	private boolean iscom = false;
	private CheckBox cb_celefit;
	private int flag2 = 0;
	private boolean isclick = false;
	private VoiceRecorder voiceRecorder;
	private PowerManager.WakeLock wakeLock;
	private int type = 0, newtype = 0;
	private LinearLayout ll_custom, ll_size_pur;
	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			// micImage.setImageDrawable(micImages[msg.what]);
		}
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("立即购买");
		if (wakeLock != null) {
			if (wakeLock.isHeld())
				wakeLock.release();
			if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
				// 停止语音播放
				VoicePlayClickListener.currentPlayListener.stopPlayVoice();
			}

			try {
				// 停止录音
				if (voiceRecorder.isRecording()) {
					voiceRecorder.discardRecording();
					// recordingContainer.setVisibility(View.INVISIBLE);
				}
			} catch (Exception e) {
			}
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("立即购买");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_back_purchase:
			finish();
			break;
		case R.id.tv_surebuy:
			if (count > 0) {
				if (newtype == 2) {
					isclick = false;
					checkMode();
				} else {
					UpOder();
				}

			} else {
				Toast.makeText(Activity_Purchase.this, "请选择数量", Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.im_reduce:
			if (count > 0) {
				ed_count.setText("" + (--count));
			}
			break;
		case R.id.im_count:
			ed_count.setText("" + (++count));
			break;
		case R.id.tv_tomodecar:
			isclick = true;
			Intent intent_mode_c = new Intent(Activity_Purchase.this, Activity_NewDZ.class);
			startActivityForResult(intent_mode_c, 102);
			break;
		case R.id.cb_celefit:
			if (cb_celefit.isChecked()) {
				flag = -1;
				flag2 = 1;
				gvcoloradapter.notifyDataSetChanged();
				gvsizeadapter.notifyDataSetChanged();
			} else {
				flag = 0;
				flag2 = 0;
				gvcoloradapter.notifyDataSetChanged();
				gvsizeadapter.notifyDataSetChanged();
			}
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg2 != null && arg0 == 102) {
			if (cb_celefit.isChecked()) {
				checkMode();
			}

		}
	}

	@Override
	public void initView() {
		MyApplication.addpaycct(this);
		datas_size = new ArrayList<String>();

		data_color = new ArrayList<String>();

		rl_back_purchase = (RelativeLayout) findViewById(R.id.rl_back_purchase);
		tv_surebuy = (TextView) findViewById(R.id.tv_surebuy);
		tv_tomodecar = (TextView) findViewById(R.id.tv_tomodecar);
		gv_size = (MyGridView) findViewById(R.id.gv_size);
		gv_color = (MyGridView) findViewById(R.id.gv_color);
		im_reduce = (ImageView) findViewById(R.id.im_reduce);
		im_count = (ImageView) findViewById(R.id.im_count);
		ed_count = (EditText) findViewById(R.id.ed_count);
		cb_celefit = (CheckBox) findViewById(R.id.cb_celefit);
		ll_size_pur = (LinearLayout) findViewById(R.id.ll_size_pur);
		ll_custom = (LinearLayout) findViewById(R.id.ll_custom);
		ed_count.setText("" + count);
		gvcoloradapter = new MyGVColorAdapter();
		gvsizeadapter = new MyGVSizeAdapter();
		gv_size.setAdapter(gvsizeadapter);
		gv_size.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gv_color.setAdapter(gvcoloradapter);
		gv_color.setSelector(new ColorDrawable(Color.TRANSPARENT));
		if (flag2 == 1) {
			cb_celefit.setChecked(true);
			flag = -1;
		}

	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	private List<String> colors;
	private String colorUrl = AppFinalUrl.usergetProductColor;

	public void getColor() {
		RequestParams params = new RequestParams();
		if (type == 1) {
			params.put("makeProductId", productId);
		} else {
			params.put("product.id", productId);
		}

		params.put("token", MyApplication.token);
		MyApplication.ahc.post(colorUrl, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuocolor" + response.toString());
				JSONArray optJSONArray = response.optJSONArray("List");

				for (int i = 0; i < optJSONArray.length(); i++) {
					JSONObject j = optJSONArray.optJSONObject(i);
					data_color.add(j.optString("color"));
				}
				if (data_color.size() == 0) {
					data_color.add("无");
				}
				gvcoloradapter.notifyDataSetChanged();
				JSONArray ChiMaJSONArray = response.optJSONArray("colorList");

				if (ChiMaJSONArray != null) {
					for (int k = 0; k < ChiMaJSONArray.length(); k++) {
						JSONObject jj = ChiMaJSONArray.optJSONObject(k);
						String chiCun = jj.optString("chiCun");
						datas_size.add(chiCun);
					}

				}
				if (datas_size.size() == 0) {
					datas_size.add("无");
				}
				gvsizeadapter.notifyDataSetChanged();
			}
		});
	}

	private String buyUrl = AppFinalUrl.usersoonBuy;

	public void UpOder() {
		RequestParams params = new RequestParams();
		params.put("userId", myId);
		params.put("productId", productId);
		if (flag2 != 1) {
			if (newtype == 2) {
				params.put("chiMa", "-1");
			} else {
				params.put("chiMa", datas_size.get(flag));
			}

		} else {

		}

		params.put("count", count);
		if (data_color.size() != 0) {
			params.put("colour", data_color.get(flag1));
		}

		showProgressDialog("正在提交...");
		if (type == 1) {
			params.put("pushCode", "34242141");
			// params.put("couponId", "1244");
		}
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(buyUrl, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					String oderId = response.getString("message");
					if (result.equals("0")) {
						Intent intent = new Intent(Activity_Purchase.this, Activity_Order.class);
						intent.putExtra("oderId", oderId);
						intent.putExtra("title", title);
						intent.putExtra("newtype", type);
						intent.putExtra("price", "" + (price * count));
						startActivity(intent);
					} else {
						Toast.makeText(Activity_Purchase.this, "提交失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
		myId = sp.getInt("userId", 0);
		productId = getIntent().getIntExtra("productId", 0);
		flag2 = getIntent().getIntExtra("flag", 0);
		type = getIntent().getIntExtra("type", 0);
		newtype = getIntent().getIntExtra("newtype", 0);
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();

		title = getIntent().getStringExtra("title");
		price = getIntent().getDoubleExtra("price", 0);
		if (type == 1) {
			colorUrl = AppFinalUrl.usergetMakeProductColor;
			buyUrl = AppFinalUrl.usersoonMake;
		}
	}

	@Override
	public void Control() {
		colors = new ArrayList<String>();
		rl_back_purchase.setOnClickListener(this);
		tv_surebuy.setOnClickListener(this);
		im_count.setOnClickListener(this);
		im_reduce.setOnClickListener(this);
		cb_celefit.setOnClickListener(this);
		tv_tomodecar.setOnClickListener(this);
		gv_size.setOnItemClickListener(this);
		gv_color.setOnItemClickListener(this);
		getColor();

		if (type == 1) {
			if (newtype == 1) {
				ll_custom.setVisibility(View.GONE);
			} else if (newtype == 2) {
				ll_size_pur.setVisibility(View.GONE);
			}
		}

	}

	@Override
	public void setContentView() {
		setContentView(R.layout.acitivity_purchase);
		initParams();
		MyApplication.addActivity(this);
	}

	class MyGVSizeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return datas_size == null ? 0 : datas_size.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return datas_size.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			v = View.inflate(Activity_Purchase.this, R.layout.item_purchase, null);
			TextView tv_sort_name = (TextView) v.findViewById(R.id.tv_purchasename);
			tv_sort_name.setText(datas_size.get(position));
			if (flag == position) {
				tv_sort_name.setBackgroundResource(R.drawable.bg_purchase);
				tv_sort_name.setTextColor(getResources().getColor(R.color.white));
			} else {
				tv_sort_name.setBackgroundColor(getResources().getColor(R.color.white));
				tv_sort_name.setTextColor(getResources().getColor(R.color.black));
			}
			return v;
		}
	}

	class MyGVColorAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data_color == null ? 0 : data_color.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data_color.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			v = View.inflate(Activity_Purchase.this, R.layout.item_purchase, null);
			TextView tv_sort_name = (TextView) v.findViewById(R.id.tv_purchasename);
			tv_sort_name.setText(data_color.get(position));
			if (flag1 == position) {
				tv_sort_name.setBackgroundResource(R.drawable.bg_purchase);
				tv_sort_name.setTextColor(getResources().getColor(R.color.white));
			} else {
				tv_sort_name.setBackgroundColor(getResources().getColor(R.color.white));
				tv_sort_name.setTextColor(getResources().getColor(R.color.black));
			}
			return v;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent == gv_size) {
			if (flag2 != 1) {
				flag = position;
				gvsizeadapter.notifyDataSetChanged();
			}

		} else if (parent == gv_color) {
			flag1 = position;
			gvcoloradapter.notifyDataSetChanged();
		}

	}

	private void showPic(View view) {
		if (ppwRule != null && ppwRule.isShowing()) {
			// ppw_price.setFocusable(false);
			ppwRule.dismiss();
		} else {
			initPpwPrice();
			ppwRule.showAtLocation(view, Gravity.CENTER_HORIZONTAL, 1, 1);
		}

	}

	private PopupWindow ppwRule;
	private View customView;

	public void initPpwPrice() {
		customView = View.inflate(this, R.layout.popurule, null);
		ppwRule = new PopupWindow(customView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, false);
		TextView tv_iknow = (TextView) customView.findViewById(R.id.tv_iknow);
		TextView tv_rule_con = (TextView) customView.findViewById(R.id.tv_rule_con);
		tv_rule_con
				.setText(" 购买规则（仅用于C2D商城）\n订单说明：C2D商品以5天为订单期，每一期结束后，优格都会为消费者生产设计师的作品.\n生产说明：是由优格进行统一生产，由于每种商品的生产时间不同，每款的生产周期也有所不同。（具体参考商品详情的生产时间）\n退换货说明：七天无理由退换货是指消费者成功交易后，在签收货物后7天内，如因主观原因不愿意完成本次交易，可以提出“7天无理由退换货”的申请。买家退换货的货物不得影响优格的二次销售，换货仅可换尺码，不可换其他款式。退货金额3个工作日内退还支付宝。\n运费说明：优格承担运费。如需退换货，需由消费者承担寄回运费。\n其他说明：价格是由优格和设计师协商后给出，由优格代工生产，保证每件商品的品质。");
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

	private void checkMode() {
		RequestParams params = new RequestParams();
		params.put("userId", myId);
		showProgressDialog("正在检查模特卡是否完善...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetMyMedolCardIsOk, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						// cb_checkmode.setChecked(true);
						iscom = true;
						showToast("您的模特卡已完善", 0);
						if (!isclick) {
							UpOder();
						}

					} else {
						showDialogMode();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	private void showDialogMode() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("您的模特卡尚未完善!").setMessage("确定去完善您的模特卡?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				cb_celefit.setChecked(true);
				isclick = true;
				Intent intent_mode_c = new Intent(Activity_Purchase.this, Activity_NewDZ.class);
				startActivityForResult(intent_mode_c, 102);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				flag = 0;
				cb_celefit.setChecked(false);
				flag2 = 0;
				gvcoloradapter.notifyDataSetChanged();
				gvsizeadapter.notifyDataSetChanged();
			}
		}).show();
	}

	private Bitmap getVideoThumbnail2(String videoPath, int width, int height, int kind) {
		Bitmap bitmap = null;
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	public void seleVideo() {
		Intent intent = new Intent(Activity_Purchase.this, ImageGridActivity.class);
		startActivityForResult(intent, 23);
	}

	private void INitVoice() {
		voiceRecorder = new VoiceRecorder(micImageHandler);
		wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
				.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
	}

	/**
	 * 按住说话listener
	 * 
	 */
	class PressToSpeakListen implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonUtils.isExitsSdcard()) {
					String st4 = getResources().getString(R.string.Send_voice_need_sdcard_support);
					Toast.makeText(Activity_Purchase.this, st4, Toast.LENGTH_SHORT).show();
					return false;
				}
				try {
					v.setPressed(true);
					wakeLock.acquire();
					if (VoicePlayClickListener.isPlaying)
						VoicePlayClickListener.currentPlayListener.stopPlayVoice();
					// recordingContainer.setVisibility(View.VISIBLE);
					// recordingHint.setText(getString(R.string.move_up_to_cancel));
					// recordingHint.setBackgroundColor(Color.TRANSPARENT);
					voiceRecorder.startRecording(null, "" + myId, getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					if (wakeLock.isHeld())
						wakeLock.release();
					if (voiceRecorder != null)
						voiceRecorder.discardRecording();
					// recordingContainer.setVisibility(View.INVISIBLE);
					Toast.makeText(Activity_Purchase.this, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
					return false;
				}

				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					// recordingHint.setText(getString(R.string.release_to_cancel));
					// recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
				} else {
					// recordingHint.setText(getString(R.string.move_up_to_cancel));
					// recordingHint.setBackgroundColor(Color.TRANSPARENT);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				// recordingContainer.setVisibility(View.INVISIBLE);
				if (wakeLock.isHeld())
					wakeLock.release();
				if (event.getY() < 0) {
					// discard the recorded audio.
					voiceRecorder.discardRecording();

				} else {
					// stop recording and send voice file
					String st1 = getResources().getString(R.string.Recording_without_permission);
					String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
					String st3 = getResources().getString(R.string.send_failure_please);
					try {
						int length = voiceRecorder.stopRecoding();
						if (length > 0) {
							// sendVoice(voiceRecorder.getVoiceFilePath(),
							// voiceRecorder.getVoiceFileName(""+myId),
							// Integer.toString(length), false);
						} else if (length == EMError.INVALID_FILE) {
							Toast.makeText(getApplicationContext(), st1, Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), st2, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(Activity_Purchase.this, st3, Toast.LENGTH_SHORT).show();
					}

				}
				return true;
			default:
				// recordingContainer.setVisibility(View.INVISIBLE);
				if (voiceRecorder != null)
					voiceRecorder.discardRecording();
				return false;
			}
		}
	}

}
