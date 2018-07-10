package com.femto.ugershop.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.femto.ugershop.view.CustomProgressDialog;
import com.umeng.analytics.MobclickAgent;

public class BaseFragment extends Fragment {
	private CustomProgressDialog pd = null;

	public void showProgressDialog(String title) {
		// TODO Auto-generated method stub
		if (pd == null) {
			pd = CustomProgressDialog.createDialog(getActivity());
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

	public int getScroWith() {
		return getActivity().getWindowManager().getDefaultDisplay().getWidth();
	}

	public int dp2px2(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
}
