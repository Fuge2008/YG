package com.femto.ugershop.entity;

import java.io.Serializable;

public class MyCoupon implements Serializable {
	String name, lastTime, info;
	int id, money, type, fullMoney;

	public MyCoupon(String name, String lastTime, String info, int id, int money, int type, int fullMoney) {
		super();
		this.name = name;
		this.lastTime = lastTime;
		this.info = info;
		this.id = id;
		this.money = money;
		this.type = type;
		this.fullMoney = fullMoney;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getFullMoney() {
		return fullMoney;
	}

	public void setFullMoney(int fullMoney) {
		this.fullMoney = fullMoney;
	}

}
