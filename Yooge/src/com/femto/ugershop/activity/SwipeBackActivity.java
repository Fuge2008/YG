package com.femto.ugershop.activity;

import com.femto.ugershop.R;
import com.femto.ugershop.view.CustomProgressDialog;
import com.femto.ugershop.view.SwipeBackLayout;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.Toast;

public abstract class SwipeBackActivity extends FragmentActivity implements OnClickListener {
	private CustomProgressDialog pd = null;
	protected SwipeBackLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
		layout.attachToActivity(this);
		// // 沉浸模式
		// if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
		// // 状态栏
		// getWindow().addFlags(
		// WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// // 虚拟按键
		// //
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		// }

		// MyApplication.stack.add(this); // Activity添加到栈
		setContentView();
		initView();
		Control();
	}

	/**
	 * 初始化view
	 */
	public abstract void initView();

	/**
	 * 初始化工具
	 */
	public abstract void initUtils();

	/**
	 * 设置view
	 */
	public abstract void Control();

	/**
	 * 加载布局
	 */
	public abstract void setContentView();

	/**
	 * 显示加载框
	 */
	public void showProgressDialog(String title) {
		// TODO Auto-generated method stub
		if (pd == null) {
			pd = CustomProgressDialog.createDialog(this);
			pd.setMessage(title);
		}
		pd.setCanceledOnTouchOutside(false);
		pd.show();
	}

	/**
	 * 取消加载框
	 */
	public void dismissProgressDialog() {
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
	}

	/**
	 * Activity跳转
	 * 
	 * @param cls
	 * @param bundle
	 */
	public void openActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		if (bundle != null) {
			intent.putExtra("bundle", bundle);
		}
		startActivity(intent);
	}

	/**
	 * 获取String资源
	 * 
	 * @param id
	 */
	public String getStringById(int id) {

		return getResources().getString(id);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	/**
	 * Toast显示
	 * 
	 * @param text
	 * @param timeTag
	 *            0:short
	 */
	public void showToast(String text, int timeTag) {
		if (timeTag == 0) {
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, text, Toast.LENGTH_LONG).show();
		}
	}

	public int getWith() {
		return getWindowManager().getDefaultDisplay().getWidth();
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
	}

	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}

	public void setPageStart(String name) {
		if (name != null) {
			MobclickAgent.onPageStart(name);
		}

	}

	public void setPageEnd(String name) {
		if (name != null) {
			MobclickAgent.onPageEnd(name);
		}
	}
}
