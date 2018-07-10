package com.femto.hx.adapter;

import java.util.List;

import com.femto.ugershop.R;
import com.femto.ugershop.appfinal.AppFinalUrl;
import com.femto.ugershop.easemob.applib.domain.ContactsList;
import com.femto.ugershop.view.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyConAdaptr extends BaseAdapter {
	private List<ContactsList> contans;
	private Context context;
	private DisplayImageOptions options;

	public MyConAdaptr(List<ContactsList> contans, Context context) {
		super();
		this.contans = contans;
		this.context = context;

		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.picture1) // 在ImageView加载过程中显示图片
				.showImageForEmptyUri(R.drawable.ic_launcher) // image连接地址为空时
				.showImageOnFail(R.drawable.ic_launcher) // image加载失败
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				// .displayer(new RoundedBitmapDisplayer(0)) //
				// 设置用户加载图片task(这里是圆角图片显示)
				.build();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return contans == null ? 0 : contans.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return contans.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		Myholer h;
		if (v == null) {
			h = new Myholer();
			v = View.inflate(context, R.layout.item_mycon, null);
			h.im_myhead = (CircleImageView) v.findViewById(R.id.im_myhead);
			h.tv_myinfo = (TextView) v.findViewById(R.id.tv_myinfo);
			h.tv_myname = (TextView) v.findViewById(R.id.tv_myname);
			v.setTag(h);
		} else {
			h = (Myholer) v.getTag();
		}
		h.tv_myinfo.setText(contans.get(position).getName());
		h.tv_myname.setText(contans.get(position).getUserName());
		ImageLoader.getInstance().displayImage(
				AppFinalUrl.photoBaseUri + contans.get(position).getImgUrl(),
				h.im_myhead, options);
		return v;
	}

	class Myholer {
		TextView tv_myname, tv_myinfo;
		CircleImageView im_myhead;
	}
}
