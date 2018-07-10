package com.femto.ugershop.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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
import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.EMError;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.chatuidemo.activity.ImageGridActivity;
import com.easemob.util.VoiceRecorder;
import com.femto.hx.adapter.VoicePlayClickListener;
import com.femto.hx.utils.CommonUtils;
import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_UpLoadPost.MBC;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.selepic.ImgFileListActivity;
import com.femto.ugershop.view.MyGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.picasso.Picasso;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class Activity_CreatQcode extends BaseActivity implements OnCompletionListener, OnLongClickListener {
	private RelativeLayout rl_back_qc, rl_selevideo_cq, recordingContainer, rl_video_cq;
	private MyGridView gv_pic_cq;
	private List<String> filePath;
	private int width;
	private MyGVAdapter gvadapter;
	private MBC mbc;
	private String orderCode;
	private TextView tv_addvideo, recordingHint, tv_upcq;
	private ImageView im_video, mic_image_c;
	private String videoPath;
	private Bitmap videoThumbnail;
	private EditText ed_contentcq, ed_brandlabel;
	// 录音相关
	private View buttonPressToSpeak;
	private VoiceRecorder voiceRecorder;
	private Drawable[] micImages;
	private PowerManager.WakeLock wakeLock;
	private MediaPlayer mMediaPlayer;
	private TextView tv_playvoice;
	private String voicePath;
	private List<String> upFilePath;
	private String savepic = Environment.getExternalStorageDirectory().toString() + "/uger/";
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				rl_video_cq.setVisibility(View.VISIBLE);
				im_video.setImageBitmap(videoThumbnail);
				break;
			case 2:
				dismissProgressDialog();
				showToast("创建成功", 0);
				Intent intent = new Intent();
				setResult(202, intent);
				finish();
				break;
			default:
				break;
			}
		};
	};
	private Handler micImageHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// 切换msg切换图片
			mic_image_c.setImageDrawable(micImages[msg.what]);
		}
	};
	private DisplayImageOptions options;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_back_qc:
			finish();
			break;
		case R.id.rl_selevideo_cq:
			if (videoPath != null) {
				Intent intent_video = new Intent(this, Activity_Video.class);
				intent_video.putExtra("videourl", videoPath);
				startActivity(intent_video);
			} else {
				seleVideo();
			}

			break;
		case R.id.tv_playvoice:
			if (!mMediaPlayer.isPlaying() && voicePath != null) {
				System.out.println("zuo---voicePath--" + voicePath);
				playMusic(voicePath);
				tv_playvoice.setText("暂停");
			} else {
				pauseMusic();
				tv_playvoice.setText("播放");
			}

			break;
		case R.id.tv_upcq:
			showProgressDialog("上传中");
			new Thread() {
				public void run() {
					for (int i = 1; i < filePath.size(); i++) {
						int yaso = 0;
						if ((new File(filePath.get(i))).length() > 4000000) {
							yaso = 10;
						} else if ((new File(filePath.get(i))).length() > 2000000) {
							yaso = 5;
						} else if ((new File(filePath.get(i))).length() > 1000000) {
							yaso = 3;
						} else if ((new File(filePath.get(i))).length() > 200000) {
							yaso = 1;
						} else {
							yaso = 1;
						}
						File saveMyBitmap = saveMyBitmap("image" + i + ".jpg", convertToBitmap(filePath.get(i), yaso));
						upFilePath.add(saveMyBitmap.getPath().toString());
					}
					new Thread() {
						public void run() {

							post(AppFinalUrl.usercreateQrCode, ddd(upFilePath));

						};
					}.start();
				};
			}.start();
			break;
		default:
			break;
		}
	}

	@Override
	public void initView() {
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.newlogyooge) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.newlogyooge) // image连接地址为空时
				.showImageOnFail(R.drawable.newlogyooge) // image加载失败
				.cacheInMemory(false) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT).bitmapConfig(Bitmap.Config.ARGB_8888)
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
		// TODO Auto-generated method stub
		rl_back_qc = (RelativeLayout) findViewById(R.id.rl_back_qc);
		rl_selevideo_cq = (RelativeLayout) findViewById(R.id.rl_selevideo_cq);
		recordingContainer = (RelativeLayout) findViewById(R.id.recording_container_c);
		rl_video_cq = (RelativeLayout) findViewById(R.id.rl_video_cq);
		buttonPressToSpeak = findViewById(R.id.btn_press_to_speak_c);
		ed_contentcq = (EditText) findViewById(R.id.ed_contentcq);
		ed_brandlabel = (EditText) findViewById(R.id.ed_brandlabel);
		gv_pic_cq = (MyGridView) findViewById(R.id.gv_pic_cq);
		tv_addvideo = (TextView) findViewById(R.id.tv_addvideo);
		tv_upcq = (TextView) findViewById(R.id.tv_upcq);
		recordingHint = (TextView) findViewById(R.id.recording_hint_c);
		tv_playvoice = (TextView) findViewById(R.id.tv_playvoice);
		im_video = (ImageView) findViewById(R.id.im_video);
		mic_image_c = (ImageView) findViewById(R.id.mic_image_c);
		initVoice();
	}

	private void initVoice() {
		// TODO Auto-generated method stub
		voiceRecorder = new VoiceRecorder(micImageHandler);
		// 动画资源文件,用于录制语音时
		micImages = new Drawable[] { getResources().getDrawable(R.drawable.record_animate_01),
				getResources().getDrawable(R.drawable.record_animate_02),
				getResources().getDrawable(R.drawable.record_animate_03),
				getResources().getDrawable(R.drawable.record_animate_04),
				getResources().getDrawable(R.drawable.record_animate_05),
				getResources().getDrawable(R.drawable.record_animate_06),
				getResources().getDrawable(R.drawable.record_animate_07),
				getResources().getDrawable(R.drawable.record_animate_08),
				getResources().getDrawable(R.drawable.record_animate_09),
				getResources().getDrawable(R.drawable.record_animate_10),
				getResources().getDrawable(R.drawable.record_animate_11),
				getResources().getDrawable(R.drawable.record_animate_12),
				getResources().getDrawable(R.drawable.record_animate_13),
				getResources().getDrawable(R.drawable.record_animate_14), };
		buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
		initMediaplayer();
		/* 监听播放是否完成 */
		mMediaPlayer.setOnCompletionListener(this);
	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub
		rl_back_qc.setOnClickListener(this);
		rl_selevideo_cq.setOnClickListener(this);
		rl_selevideo_cq.setOnLongClickListener(this);
		tv_playvoice.setOnClickListener(this);
		tv_upcq.setOnClickListener(this);
		filePath = new ArrayList<String>();
		upFilePath = new ArrayList<String>();
		filePath.add("aa");
		gvadapter = new MyGVAdapter();
		gv_pic_cq.setAdapter(gvadapter);
	}

	// 获取视频广播
	private void registMBC() {
		mbc = new MBC();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.select.pic");
		registerReceiver(mbc, filter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mbc);
		destoryMusic();
	}

	class MBC extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals("com.select.pic")) {
				ArrayList<String> f = intent.getStringArrayListExtra("files");
				for (int i = 0; i < f.size(); i++) {
					filePath.add(f.get(i));
				}
				gvadapter.notifyDataSetChanged();
			}

		}
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_creatqcode);
		initParams();
		MyApplication.addActivity(this);
	}

	private void initParams() {
		// TODO Auto-generated method stub
		int dp2px = dp2px(this, 36);
		width = (getWith() - dp2px) / 3;
		orderCode = getIntent().getStringExtra("orderCode");
		registMBC();
		wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
				.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
	}

	public int dp2px(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	class MyGVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return filePath == null ? 0 : filePath.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return filePath.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			v = View.inflate(Activity_CreatQcode.this, R.layout.item_gv_pic, null);
			ImageView im = (ImageView) v.findViewById(R.id.im_gvitem);
			if (position == 0) {
				im.setImageResource(R.drawable.addoder);
			} else {
				File imageFile = new File(filePath.get(position));
				Picasso.with(Activity_CreatQcode.this).load(imageFile).placeholder(R.drawable.tianc).error(R.drawable.tianc)
						.resize(180, 180).centerCrop().into(im);
				// ImageLoader.getInstance().displayImage(Uri.parse(filePath.get(position)).toString(),
				// im, options);
			}
			LayoutParams params = (LayoutParams) im.getLayoutParams();
			params.height = width;
			im.setLayoutParams(params);
			im.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (position == 0 && filePath.size() != 10) {
						Intent intent = new Intent();
						intent.putExtra("nub", 10 - filePath.size());
						intent.setClass(Activity_CreatQcode.this, ImgFileListActivity.class);
						startActivity(intent);
					}

				}
			});
			im.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					if (position != 0) {
						showExit(position);
					}
					return true;
				}
			});
			return v;
		}
	}

	// 删除图片
	private void showExit(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(Activity_CreatQcode.this);
		builder.setMessage("确定删除此图片?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				filePath.remove(position);
				gvadapter.notifyDataSetChanged();

			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).show();
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

	// 添加视频
	public void seleVideo() {
		Intent intent = new Intent(this, ImageGridActivity.class);
		startActivityForResult(intent, 205);
	}

	// 获取视频地址
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, data);
		if (data != null && arg0 == 205) {
			videoPath = data.getStringExtra("path");
			if (videoPath != null) {
				new Thread() {

					public void run() {
						videoThumbnail = getVideoThumbnail2(videoPath, 260, 260, Thumbnails.MINI_KIND);
						handler.sendEmptyMessage(1);
					};
				}.start();
			}
		}
	}

	// 获取视频缩略图
	private Bitmap getVideoThumbnail2(String videoPath, int width, int height, int kind) {
		Bitmap bitmap = null;
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	// 录音
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
					Toast.makeText(Activity_CreatQcode.this, st4, Toast.LENGTH_SHORT).show();
					return false;
				}
				try {
					v.setPressed(true);
					wakeLock.acquire();
					if (VoicePlayClickListener.isPlaying)
						VoicePlayClickListener.currentPlayListener.stopPlayVoice();
					recordingContainer.setVisibility(View.VISIBLE);
					recordingHint.setText("手指上滑，取消录制");
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
					voiceRecorder.startRecording(null, "" + MyApplication.userId, getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
					v.setPressed(false);
					if (wakeLock.isHeld())
						wakeLock.release();
					if (voiceRecorder != null)
						voiceRecorder.discardRecording();
					recordingContainer.setVisibility(View.INVISIBLE);
					Toast.makeText(Activity_CreatQcode.this, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
					return false;
				}

				return true;
			case MotionEvent.ACTION_MOVE: {
				if (event.getY() < 0) {
					recordingHint.setText(getString(R.string.release_to_cancel));
					recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
				} else {
					recordingHint.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				v.setPressed(false);
				recordingContainer.setVisibility(View.INVISIBLE);
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
							// voiceRecorder.getVoiceFileName(toChatUsername),
							// Integer.toString(length), false);
							setVoice(voiceRecorder.getVoiceFilePath());
							System.out.println("zuo===语音=" + voiceRecorder.getVoiceFilePath());
						} else if (length == EMError.INVALID_FILE) {
							Toast.makeText(getApplicationContext(), st1, Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), st2, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(Activity_CreatQcode.this, st3, Toast.LENGTH_SHORT).show();
					}
				}
				return true;
			default:
				recordingContainer.setVisibility(View.INVISIBLE);
				if (voiceRecorder != null)
					voiceRecorder.discardRecording();
				return false;
			}
		}

	}

	// 设置录音
	private void setVoice(String voiceFilePath) {
		if (voiceFilePath != null) {
			voicePath = voiceFilePath;
			tv_playvoice.setVisibility(View.VISIBLE);
		} else {
			tv_playvoice.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		// 唤醒锁
		if (wakeLock != null && wakeLock.isHeld())
			wakeLock.release();
		if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
			// 停止语音播放
			VoicePlayClickListener.currentPlayListener.stopPlayVoice();
		}

		try {
			// 停止录音
			if (voiceRecorder.isRecording()) {
				voiceRecorder.discardRecording();
				recordingContainer.setVisibility(View.INVISIBLE);
			}
		} catch (Exception e) {
		}
	}

	// 初始化播放器
	private void initMediaplayer() {
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		mMediaPlayer = new MediaPlayer();
	}

	// 销毁音乐
	private void destoryMusic() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	// 暂停播放
	private void pauseMusic() {
		if (mMediaPlayer.isPlaying()) {// 正在播放
			mMediaPlayer.pause();// 暂停
		} else {// 没有播放
			mMediaPlayer.start();
		}
	}

	// 停止播放
	private void stopMusic() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
		}
	}

	// 播放音乐
	private void playMusic(String mFileName) {
		try {
			/* 重置多媒体 */
			mMediaPlayer.reset();
			/* 读取mp3文件 */
			mMediaPlayer.setDataSource(mFileName);
			/* 准备播放 */
			mMediaPlayer.prepare();
			/* 开始播放 */
			mMediaPlayer.start();
			/* 是否单曲循环 */
			mMediaPlayer.setLooping(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 播放完毕
	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		stopMusic();
		tv_playvoice.setText("播放");
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		showDeledia();
		return true;
	}

	// 删除视频

	private void showDeledia() {
		AlertDialog.Builder builder = new AlertDialog.Builder(Activity_CreatQcode.this);
		builder.setMessage("确定删除此视频?").setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				videoPath = null;
				rl_video_cq.setVisibility(View.INVISIBLE);
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).show();
	}

	public String post(String url, List<NameValuePair> pairs) {
		String content = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext locaContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);
		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));

		try {
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			for (int i = 0; i < pairs.size(); i++) {
				if (pairs.get(i).getName().equalsIgnoreCase("pic")) {
					if (pairs.get(i).getValue() != null) {
						entity.addPart(pairs.get(i).getName(), new FileBody(new File(pairs.get(i).getValue())));
					}
				}
			}
			if (voicePath != null && !voicePath.equals("")) {
				entity.addPart("music", new FileBody(new File(voicePath)));
			}
			if (videoPath != null && !videoPath.equals("")) {
				entity.addPart("doc", new FileBody(new File(videoPath)));
			}

			// entity.addPart("friendCircle.msg",
			// new
			// StringBody(Base64.encodeToString(ed_postmessage.getText().toString().getBytes("UTF-8"),
			// Base64.DEFAULT),
			// Charset.forName("UTF-8")));
			entity.addPart("twoCode.info", new StringBody("" + ed_contentcq.getText().toString(), Charset.forName("UTF-8")));
			entity.addPart("twoCode.title", new StringBody("" + ed_brandlabel.getText().toString(), Charset.forName("UTF-8")));
			entity.addPart("twoCode.orderCode", new StringBody("" + orderCode, Charset.forName("UTF-8")));
			entity.addPart("token", new StringBody("" + MyApplication.token, Charset.forName("UTF-8")));
			httpPost.setEntity(entity);
			HttpEntity re = null;
			HttpResponse response = httpClient.execute(httpPost, locaContext);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				re = response.getEntity();
			}
			if (re != null) {
				content = EntityUtils.toString(re);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("zuo====" + content);

		try {
			JSONObject ojj = new JSONObject(content);
			String result = ojj.getString("result");
			if (result.equals("0")) {
				handler.sendEmptyMessage(2);
			} else {

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;

	}

	public List<NameValuePair> ddd(List<String> fPath) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (int i = 0; i < fPath.size(); i++) {
			params.add(new BasicNameValuePair("pic", fPath.get(i).toString()));
		}

		return params;
	}

	// 压缩
	public Bitmap convertToBitmap(String path, int w) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		opts.inSampleSize = w;// 压缩比例
		// 这里一定要将其设置回false，因为之前我们将其设置成了true
		opts.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, opts);
	}

	// 将压缩的bitmap保存到sdcard卡临时文件夹img_interim，用于上传

	public File saveMyBitmap(String filename, Bitmap bit) {
		System.out.println("zuo保存前with=" + bit.getWidth() + "  hight=" + bit.getHeight());
		File dir = new File(savepic);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File f = new File(savepic + filename);
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
}
