package com.femto.ugershop.activity;

import java.util.ArrayList;
import java.util.List;

import com.femto.ugershop.R;
import com.femto.ugershop.application.MyApplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author 作者Deep:
 * @version 创建时间：2015年10月21日 下午4:32:59 类说明
 */
public class Activity_Quid extends MainBaseActivity {
	private ViewPager vp_quid;
	private VpAdapter vpadapter;
	private List<Integer> data;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		vp_quid = (ViewPager) findViewById(R.id.vp_quid);
		data = new ArrayList<Integer>();
		data.add(R.drawable.newsplash);
		vpadapter = new VpAdapter();
		vp_quid.setAdapter(vpadapter);
	}

	@Override
	public void initUtils() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Control() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_quid);
		MyApplication.addActivity(this);
	}

	class VpAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View v = View.inflate(Activity_Quid.this, R.layout.item_quid, null);
			ImageView im = (ImageView) v.findViewById(R.id.im_quid);
			TextView tv_go = (TextView) v.findViewById(R.id.tv_go);
			im.setImageResource(data.get(position));
			if (position == data.size() - 1) {
				tv_go.setVisibility(View.VISIBLE);
			} else {
				tv_go.setVisibility(View.GONE);
			}
			tv_go.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					SharedPreferences sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
					int type = sp.getInt("type", 0);
					if (type == 1) {
						Intent intent = new Intent(Activity_Quid.this, Activity_PersonData.class);
						intent.putExtra("flag", 1);
						startActivity(intent);
					} else {
						Intent intent = new Intent(Activity_Quid.this, SplashActivity.class);
						startActivity(intent);
					}

					finish();
				}
			});
			container.addView(v);
			return v;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(Activity_Quid.this, MainActivity.class);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}
}
