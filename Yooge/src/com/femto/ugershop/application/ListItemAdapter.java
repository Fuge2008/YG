package com.femto.ugershop.application;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class ListItemAdapter<T> extends BaseAdapter {
	protected Context mContext;
	protected List<T> mList;

	public ListItemAdapter(Context context, List<T> list) {
		mContext = context;
		mList = list;
	}

	public ListItemAdapter(Context context) {
		mContext = context;
	}

	/**
	 * 得到适配器所有数据
	 * 
	 * @return
	 */
	public List<T> getList() {
		return mList;
	}

	/**
	 * 为适配器刷新数据。并且重新唤醒适配器
	 * 
	 * @param list
	 */
	public void setList(List<T> list) {
		this.mList = list;
		notifyDataSetChanged();
	}

	/**
	 * 为适配器添加一个泛型条目T
	 * 
	 * @param t
	 */
	public void addItem(T t) {
		if (mList == null) {
			mList = new ArrayList<T>();
		}
		mList.add(t);
		notifyDataSetChanged();
	}

	/**
	 * 为适配器添加一个List<T>的全部数据。并且重新唤醒适配器
	 * 
	 * @param list
	 */
	public void addList(List<T> list) {
		if (mList == null) {
			mList = new ArrayList<T>();
		}
		mList.addAll(list);// 此方法是将传进来的List<T>全部数据条目添加到mList<T>中
		notifyDataSetChanged();
	}

	/**
	 * 判断适配器数据是否为空
	 * 
	 * @return
	 */
	public Boolean isNull() {
		return mList == null || mList.size() == 0;
	}

	@Override
	public int getCount() {
		return null == mList ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public abstract View getView(int position, View convertView,
			ViewGroup parent);

}
