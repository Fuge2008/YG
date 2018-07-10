package com.femto.ugershop.entity;

import java.io.Serializable;

/**
 * @author 作者Deep:
 * @version 创建时间：2015年10月21日 上午9:52:38 类说明
 */
public class PhotoList implements Serializable {

	String photoUrl, high, width;

	public String getHigh() {
		return high;
	}

	public void setHigh(String high) {
		this.high = high;
	}

	public PhotoList(String photoUrl, String high, String width) {
		super();
		this.photoUrl = photoUrl;
		this.high = high;
		this.width = width;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

}
