package com.femto.ugershop.view;

public class SortModel {

	private String userName; // ��ʾ�����
	private String sortLetters; // ��ʾ���ƴ��������ĸ
	private String imgUrl, name;
	private int userId;
	private boolean issele;
	private String info;

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public boolean isIssele() {
		return issele;
	}

	public void setIssele(boolean issele) {
		this.issele = issele;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the sortLetters
	 */
	public String getSortLetters() {
		return sortLetters;
	}

	/**
	 * @param sortLetters
	 *            the sortLetters to set
	 */
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	/**
	 * @return the imgUrl
	 */
	public String getImgUrl() {
		return imgUrl;
	}

	/**
	 * @param imgUrl
	 *            the imgUrl to set
	 */
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

}
