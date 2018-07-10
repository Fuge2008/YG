package com.femto.hx.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.view.CircleImageView;
import com.femto.ugershop.view.SortModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MySortAdapter extends BaseAdapter implements SectionIndexer {
	private List<SortModel> list = null;
	private Context mContext;
	private DisplayImageOptions options;

	public MySortAdapter(Context mContext, List<SortModel> list) {
		this.mContext = mContext;
		this.list = list;
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.tianc) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.tianc) // image连接地址为空时
				.showImageOnFail(R.drawable.tianc) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<SortModel> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final SortModel mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.item_mytongxunlu, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_myname_me_c);
			viewHolder.cirim = (CircleImageView) view.findViewById(R.id.vim_head_c);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog_c);
			viewHolder.tv_myinfo_me = (TextView) view.findViewById(R.id.tv_myinfo_me_c);
			viewHolder.cb_sele = (CheckBox) view.findViewById(R.id.cb_sele);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 根据position获取分类的首字母的char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		if (this.list.get(position).isIssele()) {
			viewHolder.cb_sele.setChecked(true);
		} else {
			viewHolder.cb_sele.setChecked(false);
		}

		viewHolder.tvTitle.setText(this.list.get(position).getUserName());
		ImageLoader.getInstance().displayImage(AppFinalUrl.photoBaseUri + this.list.get(position).getImgUrl(), viewHolder.cirim,
				options);
		return view;

	}

	final static class ViewHolder {
		TextView tvLetter, tv_myinfo_me;
		TextView tvTitle;
		CircleImageView cirim;
		CheckBox cb_sele;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}