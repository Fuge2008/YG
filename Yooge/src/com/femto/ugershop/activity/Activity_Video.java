package com.femto.ugershop.activity;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Relation;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.femto.ugershop.R;
import com.femto.ugershop.application.MyApplication;
import com.umeng.analytics.MobclickAgent;

public class Activity_Video extends BaseActivity implements OnTouchListener {
	private VideoView vv;
	private Button btn_se, btn_open;
	private String filePath;
	private MediaController mController;
	private int old_duration;
	private ProgressBar pb;
	private LinearLayout ll_topvideo;
	private ImageView iv_backvideo, im_play;
	private RelativeLayout rl_back_video;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				ll_topvideo.setVisibility(View.GONE);
				break;
			case 2:
				che();
				break;

			default:
				break;
			}
		};
	};

	// Runnable runnable = new Runnable()
	// {
	//
	// public void run()
	// {
	// int duration = vv.getCurrentPosition();
	// String totime = totime(duration / 1000);
	// if (old_duration == duration && vv.isPlaying())
	// {
	// pb.setVisibility(View.VISIBLE);
	// } else
	// {
	// pb.setVisibility(View.GONE);
	// }
	// old_duration = duration;
	// System.out.println("zuoduration==" + duration);
	// handler.postDelayed(runnable, 500);
	// }
	// };

	public String totime(int t) {
		String ti = "";
		if (t < 10) {
			ti = "00:0" + t;
		}
		if (t > 9 && t < 60) {
			ti = "00:" + t;
		}
		if (t < 3600 && t > 60) {
			if (t % 60 > 10) {
				if (t / 60 < 10) {
					ti = "0" + t / 60 + ":" + t % 60;
				} else {
					ti = t / 60 + ":" + t % 60;
				}

			} else {
				if (t / 60 > 10) {
					ti = "" + t / 60 + ":" + "0" + t % 60;
				} else {
					ti = "0" + t / 60 + ":" + "0" + t % 60;
				}

			}

		}

		if (t == 60) {
			ti = "01:" + "00";
		}

		return ti;
	}

	public void che() {
		int duration = vv.getCurrentPosition();
		String totime = totime(duration / 1000);
		if (old_duration == duration && vv.isPlaying()) {
			pb.setVisibility(View.VISIBLE);
		} else {
			pb.setVisibility(View.GONE);
		}
		old_duration = duration;

		handler.sendEmptyMessageDelayed(2, 500);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_se1:
			Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
			getImage.addCategory(Intent.CATEGORY_OPENABLE);
			getImage.setType("video/mp4");
			startActivityForResult(getImage, 2);
			break;
		case R.id.im_play:
			im_play.setVisibility(View.GONE);
			pb.setVisibility(View.VISIBLE);
			playVideo();
			break;
		case R.id.iv_backvideo:
			vv.pause();
			finish();
			break;
		case R.id.rl_back_video:
			vv.pause();
			finish();
			break;
		default:
			break;
		}

	}

	@Override
	public void initView() {
		initParams();
		MyApplication.addActivity(this);
		vv = (VideoView) findViewById(R.id.vv);
		btn_se = (Button) findViewById(R.id.btn_se1);
		pb = (ProgressBar) findViewById(R.id.pb);
		ll_topvideo = (LinearLayout) findViewById(R.id.ll_topvideo);
		iv_backvideo = (ImageView) findViewById(R.id.iv_backvideo);
		im_play = (ImageView) findViewById(R.id.im_play);
		rl_back_video = (RelativeLayout) findViewById(R.id.rl_back_video);
		mController = new MediaController(this);
		vv.setMediaController(mController);

	}

	private void initParams() {
		filePath = getIntent().getStringExtra("videourl");

	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		btn_se.setOnClickListener(this);
		iv_backvideo.setOnClickListener(this);
		im_play.setOnClickListener(this);
		rl_back_video.setOnClickListener(this);
		vv.setOnTouchListener(this);

	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_video);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("播放视频");
		if (filePath.length() != 0) {
			playVideo();
			pb.setVisibility(View.VISIBLE);
		}
	}

	private void playVideo() {
		System.out.println("zuofilePath=" + filePath);
		vv.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer bm) {
				bm.start();
				che();
				im_play.setVisibility(View.INVISIBLE);
			}
		});
		vv.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer arg0) {
				im_play.setVisibility(View.VISIBLE);
			}
		});
		// vv_upvideo.setVideoURI(Uri.parse(filePath));
		// "http://120.24.168.115:8080/idorTime/doc/8b0677fb-58d5-491f-952e-61aa38671341.mp4"
		vv.setVideoPath(filePath);
		// int width = vv.getWidth();
		// int height = vv.getHeight();
		// height = (width * 640) / 480;
		// LayoutParams params = (LayoutParams) vv.getLayoutParams();
		// params.height = height;
		// vv.setLayoutParams(params);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2 && data != null) {
			Uri uri = data.getData();
			filePath = getPath(uri);

		}
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Video.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		switch (arg1.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (ll_topvideo.getVisibility() == View.VISIBLE) {
				handler.removeMessages(1);
				ll_topvideo.setVisibility(View.GONE);

			} else {
				ll_topvideo.setVisibility(View.VISIBLE);
				handler.sendEmptyMessageDelayed(1, 3000);
			}

			break;

		default:
			break;
		}
		return false;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		handler.removeMessages(1);
		handler.removeMessages(2);
		MobclickAgent.onPause(this);
		setPageEnd("播放视频");
	}
}
