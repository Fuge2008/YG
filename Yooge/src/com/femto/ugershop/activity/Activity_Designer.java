package com.femto.ugershop.activity;

import com.femto.ugershop.R;
import com.femto.ugershop.application.MyApplication;
import com.femto.ugershop.fragment.Fragment_Post;
import com.femto.ugershop.fragment.Fragment_Production;
import com.femto.ugershop.fragment.Fragment_Studio;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Activity_Designer extends SwipeBackActivity {
	private RelativeLayout rl_back_designer;
	private Fragment_Studio fstudio;
	private Fragment_Post fpost;
	private Fragment_Production fpdt;
	private FragmentTransaction transaction;
	private RelativeLayout rl_post, rl_product, rl_studio, rl_share, rl_mess;

	private int userId;
	private TextView tv_post, tv_pro, tv_studio, tv_1, tv_2, tv_3;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_back_designer:
			finish();
			break;
		case R.id.rl_mess:

			break;
		case R.id.rl_post:
			fragmentShowOrHide(fpost, fstudio, fpdt, false);
			showText(tv_3, tv_1, tv_2);
			rl_share.setVisibility(View.GONE);
			// im_post.setBackgroundResource(R.drawable.frame_select_righ);
			// im_product.setBackgroundColor(Color.TRANSPARENT);
			// im_studio.setBackgroundColor(Color.TRANSPARENT);
			// tv_post, tv_pro, tv_studio
			// tv_post.setTextColor(getResources().getColor(R.color.tab_main_sele));
			// tv_pro.setTextColor(getResources().getColor(R.color.tab_main_));
			// tv_studio.setTextColor(getResources().getColor(R.color.tab_main_));
			break;
		case R.id.rl_product:
			fragmentShowOrHide(fpdt, fstudio, fpost, false);
			showText(tv_2, tv_3, tv_1);
			rl_share.setVisibility(View.GONE);
			// im_product.setBackgroundResource(R.drawable.frammid);
			// im_post.setBackgroundColor(Color.TRANSPARENT);
			// im_studio.setBackgroundColor(Color.TRANSPARENT);
			// tv_post.setTextColor(getResources().getColor(R.color.tab_main_));
			// tv_pro.setTextColor(getResources().getColor(R.color.tab_main_sele));
			// tv_studio.setTextColor(getResources().getColor(R.color.tab_main_));
			break;
		case R.id.rl_studio:
			fragmentShowOrHide(fstudio, fpost, fpdt, false);
			showText(tv_1, tv_3, tv_2);
			rl_share.setVisibility(View.VISIBLE);
			// im_studio.setBackgroundResource(R.drawable.frame_select_left);
			// im_post.setBackgroundColor(Color.TRANSPARENT);
			// im_product.setBackgroundColor(Color.TRANSPARENT);
			// tv_post.setTextColor(getResources().getColor(R.color.tab_main_));
			// tv_pro.setTextColor(getResources().getColor(R.color.tab_main_));
			// tv_studio.setTextColor(getResources().getColor(R.color.tab_main_sele));
			break;
		case R.id.rl_share:
			Intent intent = new Intent();
			intent.setAction("shareDesigner");
			sendBroadcast(intent);
			break;

		default:
			break;
		}

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		initParams();
		MyApplication.addActivity(this);
		rl_back_designer = (RelativeLayout) findViewById(R.id.rl_back_designer);
		rl_studio = (RelativeLayout) findViewById(R.id.rl_studio);
		rl_product = (RelativeLayout) findViewById(R.id.rl_product);
		rl_post = (RelativeLayout) findViewById(R.id.rl_post);
		// im_post, im_product, im_studio
		// im_post = (ImageView) findViewById(R.id.im_post);
		// im_product = (ImageView) findViewById(R.id.im_product);
		// im_studio = (ImageView) findViewById(R.id.im_studio);
		rl_share = (RelativeLayout) findViewById(R.id.rl_share);
		rl_mess = (RelativeLayout) findViewById(R.id.rl_mess);
		// tv_post, tv_pro, tv_studio
		tv_post = (TextView) findViewById(R.id.tv_post);
		tv_pro = (TextView) findViewById(R.id.tv_pro);
		tv_studio = (TextView) findViewById(R.id.tv_studio);
		tv_1 = (TextView) findViewById(R.id.tv_1);
		tv_2 = (TextView) findViewById(R.id.tv_2);
		tv_3 = (TextView) findViewById(R.id.tv_3);
	}

	private void showText(TextView tshow, TextView tnoshow1, TextView tnoshow2) {
		tshow.setVisibility(View.VISIBLE);
		tnoshow1.setVisibility(View.INVISIBLE);
		tnoshow2.setVisibility(View.INVISIBLE);
	}

	private void initParams() {

	}

	@Override
	public void initUtils() {

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ImageLoader.getInstance().clearMemoryCache();

		fstudio = null;
		fpost = null;
		fpdt = null;
		System.gc();
	}

	@Override
	public void Control() {
		rl_back_designer.setOnClickListener(this);
		rl_studio.setOnClickListener(this);
		rl_product.setOnClickListener(this);
		rl_post.setOnClickListener(this);
		rl_mess.setOnClickListener(this);
		rl_share.setOnClickListener(this);

	}

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_designer);
		userId = getIntent().getIntExtra("userId", -1);
		initFragment();
	}

	private void initFragment() {
		transaction = getSupportFragmentManager().beginTransaction();
		fstudio = new Fragment_Studio(userId);
		fpost = new Fragment_Post(userId);
		fpdt = new Fragment_Production(userId);

		transaction.add(R.id.fl_con_designer, fstudio);
		transaction.add(R.id.fl_con_designer, fpost);
		transaction.add(R.id.fl_con_designer, fpdt);
		fragmentShowOrHide(fstudio, fpost, fpdt, true);
	}

	private void fragmentShowOrHide(Fragment showFragment, Fragment hideFragment1, Fragment hideFragment2, boolean isInit) {
		if (!isInit) {
			transaction = getSupportFragmentManager().beginTransaction();
		}
		transaction.show(showFragment);
		transaction.hide(hideFragment1);
		transaction.hide(hideFragment2);
		transaction.commit();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		setPageEnd("设计师主页");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		setPageStart("设计师主页");
	}
}
