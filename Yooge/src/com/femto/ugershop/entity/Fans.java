package com.femto.ugershop.entity;

public class Fans {
	String imgUrl, userName, info, name;
	int userId;

	public Fans(String imgUrl, String userName, String info, String name, int userId) {
		super();
		this.imgUrl = imgUrl;
		this.userName = userName;
		this.info = info;
		this.name = name;
		this.userId = userId;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
