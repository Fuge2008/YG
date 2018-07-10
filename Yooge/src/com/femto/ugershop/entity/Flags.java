package com.femto.ugershop.entity;

import java.util.List;

import com.example.AndroidCaptureCropTags.model.TagInfoModel;

public class Flags {
	String picPath;
	List<TagInfoModel> tagInfos;

	public Flags(String picPath, List<TagInfoModel> tagInfos) {
		super();
		this.picPath = picPath;
		this.tagInfos = tagInfos;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public List<TagInfoModel> getTagInfos() {
		return tagInfos;
	}

	public void setTagInfos(List<TagInfoModel> tagInfos) {
		this.tagInfos = tagInfos;
	}

}
