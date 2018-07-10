package com.femto.ugershop.entity;

public class Commends {
	String content, userName, createDate, url;
	int dicussId, userId;

	public Commends(String content, String userName, String createDate, String url, int dicussId, int userId) {
		super();
		this.content = content;
		this.userName = userName;
		this.createDate = createDate;
		this.url = url;
		this.dicussId = dicussId;
		this.userId = userId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getDicussId() {
		return dicussId;
	}

	public void setDicussId(int dicussId) {
		this.dicussId = dicussId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
