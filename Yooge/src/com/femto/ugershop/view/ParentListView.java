package com.femto.ugershop.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * @author 作者Deep:
 * @version 创建时间：2015年10月13日 下午12:35:24 类说明
 */

public class ParentListView extends ListView {

	public ParentListView(Context context) {

		super(context);

		// TODO Auto-generated constructor stub

	}

	public ParentListView(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);

		// TODO Auto-generated constructor stub

	}

	public ParentListView(Context context, AttributeSet attrs) {

		super(context, attrs);

		// TODO Auto-generated constructor stub

	}

	// 将 onInterceptTouchEvent的返回值设置为false，取消其对触摸事件的处理，将事件分发给子view

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		// TODO Auto-generated method stub

		return false;

	}

}
