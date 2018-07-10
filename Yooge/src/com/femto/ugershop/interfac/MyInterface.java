package com.femto.ugershop.interfac;

public class MyInterface {
	public static OnWindowsListener interfa;

	public interface OnWindowsListener {
		/**
		 * 回调方法， 返回MyScrollView滑动的Y方向距离
		 */
		public void onWinds(boolean focus);
	}

	@SuppressWarnings("static-access")
	public void setOnScrollListener(OnWindowsListener interfa) {
		this.interfa = interfa;
	}
}
