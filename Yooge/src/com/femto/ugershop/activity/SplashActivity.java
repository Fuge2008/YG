package com.femto.ugershop.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.femto.ugershop.R;
import com.femto.ugershop.application.MyApplication;
import com.nostra13.universalimageloader.utils.L;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * @author 作者Deep:
 * @version 创建时间：2015年10月19日 下午3:09:09 类说明
 */
public class SplashActivity extends Activity {
	private ImageView im_splash;
	private LinearLayout ll_loginInfo, ll_all_splash;
	private View view;
	private boolean boolean1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final LayoutInflater inflator = getLayoutInflater();
		view = inflator.inflate(R.layout.splashactivity, null, false);
		if (cheVer()) {

		} else {
			toQuide();
			finish();
			return;
		}

		setContentView(view);
		MyApplication.addActivity(this);
		im_splash = (ImageView) view.findViewById(R.id.im_splash);
		ll_all_splash = (LinearLayout) view.findViewById(R.id.ll_all_splash);
		Animation loadAnimation1 = AnimationUtils.loadAnimation(this, R.anim.alpha_first);
		// AlphaAnimation a1 = new AlphaAnimation(1f, 0.1f);
		// AlphaAnimation a2 = new AlphaAnimation(0.1f, 1f);
		im_splash.startAnimation(loadAnimation1);
		loadAnimation1.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				// view = inflator.inflate(R.layout.activity_login, null,
				// false);
				// ll_loginInfo = (LinearLayout)
				// view.findViewById(R.id.ll_loginInfo);
				im_splash.setVisibility(View.GONE);
				ll_all_splash.setVisibility(View.VISIBLE);
				Animation loadAnimation2 = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.alpha_first2);
				ll_all_splash.startAnimation(loadAnimation2);
				loadAnimation2.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationRepeat(Animation arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animation arg0) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(SplashActivity.this, MainActivity.class);
						startActivity(intent);
						finish();
					}
				});
				// setContentView(view);
			}
		});

		// Timer timer = new Timer();
		// TimerTask timertask = new TimerTask() {
		//
		// @Override
		// public void run() {
		//
		// }
		// };
		// timer.schedule(timertask, 3500);

	}

	private boolean cheVer() {
		SharedPreferences sp = getSharedPreferences("isfirstuser", Context.MODE_PRIVATE);
		boolean isfirstuser = sp.getBoolean("isfirstuser", false);
		return isfirstuser;
	}

	private void toQuide() {
		SharedPreferences sp = getSharedPreferences("isfirstuser", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("isfirstuser", true);
		editor.commit();
		Intent intent = new Intent(SplashActivity.this, Activity_Quid.class);
		startActivity(intent);
	}
}
