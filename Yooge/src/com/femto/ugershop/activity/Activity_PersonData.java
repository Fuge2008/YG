package com.femto.ugershop.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_SendSample.PPListAdapter;
import com.femto.ugershop.activity.Activity_SendSample.PostM;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.view.CircleImageView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.yzr.areawheel.AreaWheel;
import com.yzr.areawheel.ScreenInfo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class Activity_PersonData extends BaseActivity {
	private RelativeLayout rl_back_pd;
	private ImageView im_amend;
	private CircleImageView im_head_phone;
	/* 拍照的照片存储位置 */
	private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	/* 用来标识请求gallery的activity */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	private static final int CAMERA_WITH_DATA = 3023;
	private File mCurrentPhotoFile;// 照相机拍照得到的图片
	private View view;
	private static File mFile;
	private TextView tv_save, tv_username, tv_sigin, tv_phone, tv_email, tv_address, tv_birthday, ed_selebelong, tv_belong;

	private int year;
	private int month;
	private int day;
	private LinearLayout ll_man, ll_woman, ll_area;
	private ImageView im_seleman, im_sele_woman;
	private RelativeLayout rl_saveinfo;
	private String imgUrl, phone, email, userName, birthDay, sex, userInfo, myInfo, address;
	private int userId;
	private DisplayImageOptions options;
	private String level;
	private String label;
	private boolean canEdit = false;
	private PopupWindow ppw;
	private PPListAdapter ppladapter;
	private List<String> postm;
	private int flag;
	private boolean isup = false;
	private RelativeLayout rl_muername, rl_gxqm, rl_phonenub, rl_mail, rl_address;
	private PopupWindow popupWindow;
	private AreaWheel areaWheel;
	private TextView myArea;
	private TextView tv_confirm;
	private View rootView;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (myArea != null) {
					myArea.setText(areaWheel.getArea());
				}
				handler.sendEmptyMessageDelayed(1, 1000);
				break;

			default:
				break;
			}

		}

	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("个人资料");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_pd:
			if (flag == 1) {
				if (!isup) {
					Toast.makeText(Activity_PersonData.this, "请先完善个人信息，否则将不会显示在设计设列表！", Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(this, MainActivity.class);
					startActivity(intent);
					finish();
				}

			} else {
				finish();
			}

			break;
		case R.id.ll_woman1:
			if (canEdit) {
				sex = "女";
				im_seleman.setImageResource(R.drawable.select_no);
				im_sele_woman.setImageResource(R.drawable.select);
			} else {
				Toast.makeText(Activity_PersonData.this, "请先点击修改", Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.ll_man1:
			if (canEdit) {
				sex = "男";
				im_seleman.setImageResource(R.drawable.select);
				im_sele_woman.setImageResource(R.drawable.select_no);
			} else {
				Toast.makeText(Activity_PersonData.this, "请先点击修改", Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.im_amend:
			Toast.makeText(Activity_PersonData.this, "现在可以编辑了", Toast.LENGTH_SHORT).show();

			break;
		case R.id.im_head_phone:
			if (canEdit) {
				showSettingFaceDialog();
				// showPhotoDialog();
			} else {
				Toast.makeText(Activity_PersonData.this, "请先点击修改", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.tv_birthday:
			if (canEdit) {

				showDataDialog();
			} else {
				Toast.makeText(Activity_PersonData.this, "请先点击修改", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.rl_saveinfo:
			if (canEdit) {
				upInfo();

			} else {
				canEdit = true;
				tv_save.setText("保存");

				Toast.makeText(Activity_PersonData.this, "现在可以编辑了", Toast.LENGTH_SHORT).show();
			}
			// if (canEdit) {
			//
			// upInfo();
			// } else {
			// Toast.makeText(Activity_PersonData.this, "请先修改",
			// Toast.LENGTH_SHORT).show();
			// }
			break;
		case R.id.ed_selebelong:
			if (ppw != null && ppw.isShowing()) {
				ppw.dismiss();

			} else {
				initPpwone();
				ppw.showAsDropDown(v, 0, 0);

			}
			break;
		// // rl_muername, rl_gxqm, rl_phonenub, rl_mail, rl_address
		case R.id.rl_muername:
			Intent intent = new Intent(this, Activity_CommonModify.class);
			intent.putExtra("flag", 201);
			startActivityForResult(intent, 201);
			break;
		case R.id.rl_gxqm:
			Intent intent_gxqm = new Intent(this, Activity_CommonModify.class);
			intent_gxqm.putExtra("flag", 202);
			startActivityForResult(intent_gxqm, 202);
			break;
		case R.id.rl_phonenub:
			Intent intent_phone = new Intent(this, Activity_CommonModify.class);
			intent_phone.putExtra("flag", 203);
			startActivityForResult(intent_phone, 203);
			break;
		case R.id.rl_mail:
			Intent intent_mail = new Intent(this, Activity_CommonModify.class);
			intent_mail.putExtra("flag", 204);
			startActivityForResult(intent_mail, 204);
			break;
		case R.id.rl_address:
			Intent intent_address = new Intent(this, Activity_CommonModify.class);
			intent_address.putExtra("flag", 205);
			startActivityForResult(intent_address, 205);
			break;
		case R.id.ll_area:
			showPopupWindow();
			break;
		case R.id.tv_confirm:
			if (areaWheel.getArea() == null || areaWheel.getArea().equals("")) {
				tv_belong.setText("全部");
			} else {
				tv_belong.setText(areaWheel.getArea());
			}

			popupWindow.dismiss();
			break;
		default:
			break;
		}
	}

	private void showPopupWindow() {
		// TODO Auto-generated method stub

		View contentView = view.inflate(Activity_PersonData.this, R.layout.popu_area, null);
		popupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);

		final View timepickerview = View.inflate(Activity_PersonData.this, R.layout.areawheel, null);
		ScreenInfo screenInfo = new ScreenInfo(Activity_PersonData.this);
		areaWheel = new AreaWheel(Activity_PersonData.this, timepickerview);
		areaWheel.screenheight = screenInfo.getHeight();

		areaWheel.initAreaPicker();
		((ViewGroup) contentView).addView(timepickerview);
		myArea = (TextView) contentView.findViewById(R.id.myArea);
		tv_confirm = (TextView) contentView.findViewById(R.id.tv_confirm);
		tv_confirm.setOnClickListener(this);
		myArea.setOnClickListener(this);
		handler.sendEmptyMessage(1);

		popupWindow.setTouchable(true);

		popupWindow.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		// 我觉得这里是API的一个bug
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popu_no));

		// 设置好参数之后再show
		popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

	}

	@Override
	public void initView() {
		MyApplication.addActivity(this);
		postm = new ArrayList<String>();
		postm.add("国内");
		postm.add("日韩");
		postm.add("欧美");
		postm.add("中东");
		postm.add("东南亚");
		ll_man = (LinearLayout) findViewById(R.id.ll_man1);
		ll_woman = (LinearLayout) findViewById(R.id.ll_woman1);
		im_seleman = (ImageView) findViewById(R.id.im_seleman);
		im_sele_woman = (ImageView) findViewById(R.id.im_sele_woman);
		rl_back_pd = (RelativeLayout) findViewById(R.id.rl_back_pd);
		rl_muername = (RelativeLayout) findViewById(R.id.rl_muername);
		rl_gxqm = (RelativeLayout) findViewById(R.id.rl_gxqm);
		rl_phonenub = (RelativeLayout) findViewById(R.id.rl_phonenub);
		rl_mail = (RelativeLayout) findViewById(R.id.rl_mail);
		rl_address = (RelativeLayout) findViewById(R.id.rl_address);
		im_head_phone = (CircleImageView) findViewById(R.id.im_head_phone);
		im_amend = (ImageView) findViewById(R.id.im_amend);
		tv_username = (TextView) findViewById(R.id.tv_username);
		tv_sigin = (TextView) findViewById(R.id.tv_sigin);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_birthday = (TextView) findViewById(R.id.tv_birthday);
		tv_save = (TextView) findViewById(R.id.tv_save);
		tv_email = (TextView) findViewById(R.id.tv_email);
		tv_address = (TextView) findViewById(R.id.tv_address);
		ed_selebelong = (TextView) findViewById(R.id.ed_selebelong);
		tv_belong = (TextView) findViewById(R.id.tv_belong);
		ll_area = (LinearLayout) findViewById(R.id.ll_area);
		ll_area.setOnClickListener(this);

		rl_saveinfo = (RelativeLayout) findViewById(R.id.rl_saveinfo);
		Calendar mycalendar = Calendar.getInstance(Locale.CHINA);
		Date mydate = new Date(); // 获取当前日期Date对象
		mycalendar.setTime(mydate);// //为Calendar对象设置时间为当前日期
		year = mycalendar.get(Calendar.YEAR); // 获取Calendar对象中的年
		month = mycalendar.get(Calendar.MONTH);// 获取Calendar对象中的月
		day = mycalendar.get(Calendar.DAY_OF_MONTH);// 获取这个月的第几天

		getData();
	}

	private void getData() {
		RequestParams params = new RequestParams();
		params.put("userId", userId);
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.usergetUserInfoById, params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				System.out.println("zuo===");
				imgUrl = response.optString("imgUrl");
				phone = response.optString("phone");
				level = response.optString("level");
				email = response.optString("email");
				userName = response.optString("userName");
				label = response.optString("label");
				birthDay = response.optString("birthDay");
				userInfo = response.optString("userInfo");
				myInfo = response.optString("myInfo");
				address = response.optString("address");
				sex = response.optString("sex");
				myInfo = response.optString("myInfo");

				String belong = response.optString("belong");
				if (userName == null || userName.equals("null")) {
					tv_username.setHint("未填写");
				} else {
					tv_username.setText("" + userName);
				}
				tv_belong.setText("" + belong);
				if (myInfo == null || myInfo.equals("null")) {
					tv_sigin.setHint("未填写");
				} else {
					tv_sigin.setText("" + myInfo);
				}
				if (phone == null || phone.equals("null")) {
					tv_phone.setHint("未填写");

				} else {
					tv_phone.setText("" + phone);
				}
				if (email == null || email.equals("null")) {

					tv_email.setHint("未填写");
				} else {

					tv_email.setText("" + email);
				}
				if (address == null || address.equals("null")) {

					tv_address.setHint("未填写");
				} else {

					tv_address.setText("" + address);
				}
				if (birthDay == null || birthDay.equals("null")) {

					tv_birthday.setHint("未填写");
				} else {

					tv_birthday.setText("" + birthDay);
				}
				if (sex.equals("男")) {
					im_seleman.setImageResource(R.drawable.select);
					im_sele_woman.setImageResource(R.drawable.select_no);
				} else {
					im_seleman.setImageResource(R.drawable.select_no);
					im_sele_woman.setImageResource(R.drawable.select);
				}

				ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + imgUrl, im_head_phone, options);
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
		rl_back_pd.setOnClickListener(this);
		im_head_phone.setOnClickListener(this);
		im_amend.setOnClickListener(this);
		tv_birthday.setOnClickListener(this);
		ll_man.setOnClickListener(this);
		ll_woman.setOnClickListener(this);
		rl_saveinfo.setOnClickListener(this);
		ed_selebelong.setOnClickListener(this);
		// rl_muername, rl_gxqm, rl_phonenub, rl_mail, rl_address
		rl_muername.setOnClickListener(this);
		rl_gxqm.setOnClickListener(this);
		rl_phonenub.setOnClickListener(this);
		rl_mail.setOnClickListener(this);
		rl_address.setOnClickListener(this);
		canEdit = true;
	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		rootView = View.inflate(Activity_PersonData.this, R.layout.activity_persondata, null);
		setContentView(rootView);
		initParams();
	}

	private void initParams() {
		SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
		flag = getIntent().getIntExtra("flag", 0);
		userId = sp.getInt("userId", -1);
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.person) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.person) // image连接地址为空时
				.showImageOnFail(R.drawable.person) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
	}

	private void upInfo() {
		RequestParams params = new RequestParams();
		params.put("user.myInfo", tv_sigin.getText().toString().trim());
		if (isEmail(tv_email.getText().toString().trim())) {
			params.put("user.email", tv_email.getText().toString().trim());
		} else {
			Toast.makeText(Activity_PersonData.this, "请输入正确的邮箱", Toast.LENGTH_SHORT).show();
			return;
		}
		if (tv_phone.getText().toString().trim().length() == 11) {
			params.put("user.phone", tv_phone.getText().toString().trim());
		} else {
			Toast.makeText(Activity_PersonData.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
			return;
		}
		if (tv_belong.getText().toString().length() == 0) {
			Toast.makeText(Activity_PersonData.this, "请选择地区", Toast.LENGTH_SHORT).show();
			return;
		}
		params.put("user.userName", tv_username.getText().toString());
		params.put("user.userInfo", tv_sigin.getText().toString().trim());

		params.put("user.address", tv_address.getText().toString());
		params.put("user.id", userId);
		params.put("user.sex", sex);
		params.put("user.birthDay", birthDay);
		params.put("user.belong", tv_belong.getText().toString());
		try {
			if (mCurrentPhotoFile != null) {
				params.put("doc", new File(PHOTO_DIR, IMAGE_FILE_NAME));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showProgressDialog("保存中...");
		params.put("token", MyApplication.token);
		MyApplication.ahc.post(AppFinalUrl.userfinishUserInfo, params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				// TODO Auto-generated method stub
				super.onSuccess(statusCode, headers, response);
				dismissProgressDialog();
				try {
					String result = response.getString("result");
					if (result.equals("0")) {
						isup = true;
						if (flag == 1) {
							if (!isup) {
								Toast.makeText(Activity_PersonData.this, "请先完善个人信息，否则将不会显示在设计设列表！", Toast.LENGTH_SHORT).show();
							} else {
								Intent intent_ = new Intent(Activity_PersonData.this, MainActivity.class);
								startActivity(intent_);
								Toast.makeText(Activity_PersonData.this, "修改成功", Toast.LENGTH_SHORT).show();
								finish();
							}

						} else {
							Toast.makeText(Activity_PersonData.this, "修改成功", Toast.LENGTH_SHORT).show();
							Intent intent = new Intent();
							setResult(1, intent);
							finish();
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	private void showPhotoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(Activity_PersonData.this);
		view = View.inflate(Activity_PersonData.this, R.layout.dialog_accompany, null);

		builder.setView(view);
		final AlertDialog dialog = builder.create();
		RelativeLayout rl_local = (RelativeLayout) view.findViewById(R.id.rl_localaccompany);

		RelativeLayout rl_sure = (RelativeLayout) view.findViewById(R.id.rl_seachaccompany);
		// 本地上传
		rl_local.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doPickPhotoFromGallery();
				dialog.dismiss();

			}
		});
		// 拍摄上传
		rl_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doTakePhoto();
				dialog.dismiss();

			}
		});

		dialog.show();

	}

	/**
	 * 用当前时间给取得的图片命名
	 * 
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date) + ".jpg";
	}

	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// // TODO Auto-generated method stub
	// super.onActivityResult(requestCode, resultCode, data);
	// if (resultCode != RESULT_OK)
	// return;
	// switch (requestCode) {
	// case PHOTO_PICKED_WITH_DATA: {// 调用Gallery返回的
	// Bitmap photo = data.getParcelableExtra("data");
	// Bitmap bmp = BitmapFactory.decodeFile(mCurrentPhotoFile.getPath());
	//
	// im_head_phone.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoFile.getAbsolutePath().toString()));
	// // if (picUri != null) {
	// // im_head_phone.setImageURI(picUri);
	// // }
	//
	// break;
	// }
	// case CAMERA_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
	//
	// try {
	// Runtime.getRuntime().exec("chmod 777" +
	// mCurrentPhotoFile.getAbsolutePath());
	//
	// } catch (IOException e) {
	//
	// e.printStackTrace();
	// }
	//
	// doCropPhoto(mCurrentPhotoFile);
	// break;
	// }
	//
	// }
	// }

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("个人资料");
		if (picUri != null) {
			Bitmap bitmap;
			try {
				bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
				im_head_phone.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// im_head_phone.setImageURI(picUri);
		}
	}

	protected void doCropPhoto(File f) {
		try {
			// 启动gallery去剪辑这个照片
			final Intent intent = getCropImageIntent(Uri.fromFile(f));

			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (Exception e) {
			Toast.makeText(this, "", Toast.LENGTH_LONG).show();
		}
	}

	private Uri sdf = null;

	/**
	 * Constructs an intent for image cropping. 调用图片剪辑程序
	 */
	public Intent getCropImageIntent(Uri photoUri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 299);
		intent.putExtra("outputY", 299);
		intent.putExtra("output", Uri.fromFile(mCurrentPhotoFile));
		intent.putExtra("return-data", true);
		return intent;
	}

	// @Override
	// public boolean onTouch(View arg0, MotionEvent arg1) {
	// closekey();
	// return false;
	// }
	/**
	 * 拍照获取图片
	 * 
	 */
	protected void doTakePhoto() {
		try {
			// Launch camera to take photo for selected contact
			PHOTO_DIR.mkdirs();// 创建照片的存储目录
			mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "", Toast.LENGTH_LONG).show();
		}
	}

	// 封装请求Gallery的intent
	public Intent getPhotoPickIntent() {

		mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
		try {
			Runtime.getRuntime().exec("chmod 777" + mCurrentPhotoFile.getAbsolutePath());

		} catch (IOException e) {

			e.printStackTrace();
		}
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 299);
		intent.putExtra("outputY", 299);
		intent.putExtra("output", Uri.fromFile(mCurrentPhotoFile));
		intent.putExtra("return-data", true);
		return intent;
	}

	// 请求Gallery程序
	protected void doPickPhotoFromGallery() {
		try {
			// Launch picker to choose photo for selected contact
			final Intent intent = getPhotoPickIntent();
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, " ", Toast.LENGTH_LONG).show();
		}
	}

	public void closekey() {
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	private void showDataDialog() {
		DatePickerDialog dpd = new DatePickerDialog(Activity_PersonData.this, Datelistener, year, month, day);
		dpd.show();
	}

	private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {

		/**
		 * params：view：该事件关联的组件 params：myyear：当前选择的年 params：monthOfYear：当前选择的月
		 * params：dayOfMonth：当前选择的日
		 */
		@Override
		public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {

			// 修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
			year = myyear;
			month = monthOfYear;
			day = dayOfMonth;
			// 更新日期
			updateDate();

		}

		// 当DatePickerDialog关闭时，更新日期显示
		private void updateDate() {
			// 在TextView上显示日期
			birthDay = year + "-" + (month + 1) + "-" + day;
			tv_birthday.setText(year + "-" + (month + 1) + "-" + day);
		}
	};

	Uri picUri;

	/**
	 * 初始化缓存目录
	 */
	private void initCacheFolder() {
		picUri = Uri.parse("file://" + getCacheDir() + "/t001.jpg");//
	}

	public static boolean isEmail(String email) {
		if (null == email || "".equals(email))
			return false;
		// Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
		Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
		Matcher m = p.matcher(email);
		return m.matches();
	}

	private String[] items = new String[] { "图库", "拍照" };
	private View customViewone;
	private ListView lv_ppwtow;
	/* 头像名称 */
	private static final String IMAGE_FILE_NAME = "face.jpg";
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int SELECT_PIC_KITKAT = 3;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	private void showSettingFaceDialog() {

		new AlertDialog.Builder(this).setTitle("图片来源").setCancelable(true).setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:// Local Image
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					intent.setType("image/*");
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
						startActivityForResult(intent, SELECT_PIC_KITKAT);
					} else {
						startActivityForResult(intent, IMAGE_REQUEST_CODE);
					}
					break;
				case 1:// Take Picture
					Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// 判断存储卡是否可以用，可用进行存储
					if (hasSdcard()) {
						intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
					}
					startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
					break;
				}
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case SELECT_PIC_KITKAT:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (hasSdcard()) {
					File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					// ToastUtils.showShort(context, "未找到存储卡，无法存储照片！");
				}

				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					setImageToView(data, im_head_phone);
				}
				break;
			case 201:
				String str = data.getStringExtra("str");
				tv_username.setText("" + str);
				break;
			case 202:
				String str1 = data.getStringExtra("str");
				tv_sigin.setText("" + str1);
				break;
			case 203:
				String str2 = data.getStringExtra("str");
				tv_phone.setText("" + str2);
				break;

			case 204:
				String str3 = data.getStringExtra("str");
				tv_email.setText("" + str3);
				break;
			case 205:
				String str4 = data.getStringExtra("str");
				tv_address.setText("" + str4);
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private boolean hasSdcard() {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		if (uri == null) {
			// Log.i("tag", "The uri is not exist.");
			return;
		}

		Intent intent = new Intent("com.android.camera.action.CROP");
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			String url = getPath(this, uri);
			intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
		} else {
			intent.setDataAndType(uri, "image/*");
		}
		mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setImageToView(Intent data, ImageView imageView) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			// Bitmap roundBitmap = ImageUtil.toRoundBitmap(photo);
			imageView.setImageBitmap(photo);
			saveBitmap(photo);
		}
	}

	public void saveBitmap(Bitmap mBitmap) {
		if (!PHOTO_DIR.exists()) {
			PHOTO_DIR.mkdirs();
		}
		File f = new File(PHOTO_DIR, IMAGE_FILE_NAME);
		try {
			f.createNewFile();
			FileOutputStream fOut = null;
			fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
			fOut.close();
			System.out.println("zuo==file=" + f.getPath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initPpwone() {
		customViewone = View.inflate(this, R.layout.popu_newgoodsone, null);
		LinearLayout ll_ppall = (LinearLayout) customViewone.findViewById(R.id.ll_ppall);
		ll_ppall.setVisibility(View.GONE);
		lv_ppwtow = (ListView) customViewone.findViewById(R.id.lv_poputwo);
		ppladapter = new PPListAdapter();
		lv_ppwtow.setAdapter(ppladapter);
		ppw = new PopupWindow(customViewone, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		ppw.setFocusable(true);
		ppw.setBackgroundDrawable(new BitmapDrawable());
		lv_ppwtow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				tv_belong.setText("" + postm.get(position));
				// currentNub = postm.get(position).no;
				ppw.dismiss();
			}
		});
	}

	class PPListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return postm == null ? 0 : postm.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return postm.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub
			v = View.inflate(Activity_PersonData.this, R.layout.item_lv_popu, null);
			TextView tv_sort_name = (TextView) v.findViewById(R.id.tv_sort_name);
			tv_sort_name.setText("" + postm.get(position));
			return v;
		}

	}

	// 以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (flag == 1) {
				if (!isup) {
					Toast.makeText(Activity_PersonData.this, "请先完善个人信息，否则将不会显示在设计设列表！", Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(this, MainActivity.class);
					startActivity(intent);
					finish();
				}

			} else {
				finish();
			}

		}
		return true;
	}
}
