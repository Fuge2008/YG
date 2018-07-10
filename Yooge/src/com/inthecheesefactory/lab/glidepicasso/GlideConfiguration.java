package com.inthecheesefactory.lab.glidepicasso;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

/**
 * @author 作者Deep:
 * @version 创建时间：2015年10月15日 下午6:37:44 类说明
 */
public class GlideConfiguration implements GlideModule {

	@Override
	public void applyOptions(Context arg0, GlideBuilder arg1) {
		// TODO Auto-generated method stub
		arg1.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
	}

	@Override
	public void registerComponents(Context arg0, Glide arg1) {
		// TODO Auto-generated method stub

	}
}
