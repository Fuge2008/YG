package com.femto.ugershop.fragment;

import com.femto.ugershop.R;
import com.femto.ugershop.activity.Activity_MyCollectCustomGoods;
import com.femto.ugershop.activity.Activity_Search_Custom;
import com.femto.ugershop.application.MyApplication;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Fragment_MGC extends BaseFragment implements OnClickListener {
	private View view;
	private RadioButton rb_girl1, rb_boy1;
	private TextView tv_girl1, tv_boy1;
	private FragmentTransaction transaction;
	private Fragment_NewCustomize fg;
	private Fragment_NewCutomize_Men fb;
	private RelativeLayout rl_sreach_mgc, rl_collect_mgc, rl_change_mgc;
	private ImageView im_change_mgc;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_mgc, container, false);
		initView(view);
		MobclickAgent.onPageStart("梦工厂");
		initCon();
		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rb_girl1:
			tv_boy1.setVisibility(View.GONE);
			tv_girl1.setVisibility(View.GONE);
			fragmentShowOrHide(fg, fb, false);
			rb_boy1.setBackgroundResource(R.drawable.image_bg);
			rb_girl1.setBackgroundResource(R.drawable.image_bgsele);
			rb_boy1.setTextColor(Color.parseColor("#595959"));
			rb_girl1.setTextColor(Color.parseColor("#FFFFFF"));
			break;
		case R.id.rb_boy1:
			tv_boy1.setVisibility(View.GONE);
			tv_girl1.setVisibility(View.GONE);
			fragmentShowOrHide(fb, fg, false);
			rb_boy1.setBackgroundResource(R.drawable.image_bgsele);
			rb_girl1.setBackgroundResource(R.drawable.image_bg);
			rb_girl1.setTextColor(Color.parseColor("#595959"));
			rb_boy1.setTextColor(Color.parseColor("#FFFFFF"));
			break;
		case R.id.rl_sreach_mgc:
			Intent intent = new Intent(getActivity(), Activity_Search_Custom.class);
			startActivity(intent);

			break;
		case R.id.rl_collect_mgc:
			Intent intent_co = new Intent(getActivity(), Activity_MyCollectCustomGoods.class);
			startActivity(intent_co);

			break;
		case R.id.rl_change_mgc:
			if (MyApplication.issingle) {
				MyApplication.issingle = false;
				im_change_mgc.setImageResource(R.drawable.doublecom);
				Intent intent_ = new Intent();
				intent_.setAction("com.femto.single");
				getActivity().sendBroadcast(intent_);

			} else {
				MyApplication.issingle = true;
				im_change_mgc.setImageResource(R.drawable.single);
				Intent intent_ = new Intent();
				intent_.setAction("com.femto.single");
				getActivity().sendBroadcast(intent_);

			}
			break;
		default:
			break;
		}
	}

	private void initView(View v) {
		// TODO Auto-generated method stub
		rb_girl1 = (RadioButton) v.findViewById(R.id.rb_girl1);
		rb_boy1 = (RadioButton) v.findViewById(R.id.rb_boy1);
		tv_girl1 = (TextView) v.findViewById(R.id.tv_girl1);
		tv_boy1 = (TextView) v.findViewById(R.id.tv_boy1);
		rl_sreach_mgc = (RelativeLayout) v.findViewById(R.id.rl_sreach_mgc);
		rl_collect_mgc = (RelativeLayout) v.findViewById(R.id.rl_collect_mgc);
		rl_change_mgc = (RelativeLayout) v.findViewById(R.id.rl_change_mgc);
		im_change_mgc = (ImageView) v.findViewById(R.id.im_change_mgc);
		if (MyApplication.issingle) {
			im_change_mgc.setImageResource(R.drawable.single);

		} else {
			im_change_mgc.setImageResource(R.drawable.doublecom);

		}
	}

	private void initCon() {
		// TODO Auto-generated method stub
		rb_girl1.setOnClickListener(this);
		rb_boy1.setOnClickListener(this);
		rl_sreach_mgc.setOnClickListener(this);
		rl_collect_mgc.setOnClickListener(this);
		rl_change_mgc.setOnClickListener(this);
		initFragment();
	}

	private void initFragment() {
		transaction = getFragmentManager().beginTransaction();
		// transaction = getFragmentManager().beginTransaction();
		fg = new Fragment_NewCustomize();
		fb = new Fragment_NewCutomize_Men();

		// ftt = new Fragment_Headline();

		transaction.add(R.id.fl_mgc, fg);
		transaction.add(R.id.fl_mgc, fb);
		fragmentShowOrHide(fg, fb, true);
	}

	private void fragmentShowOrHide(Fragment showFragment, Fragment hideFragment1, boolean isInit) {
		if (!isInit) {
			transaction = getFragmentManager().beginTransaction();
		}
		transaction.show(showFragment);
		transaction.hide(hideFragment1);
		transaction.commit();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden) {
			MobclickAgent.onPageStart("梦工厂");
		} else {
			MobclickAgent.onPageEnd("梦工厂");
		}
	}
}
