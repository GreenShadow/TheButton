package com.greenshadow.thebutton.bean;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * User Bean，重载BmobChatUser对象
 */
public class User extends BmobChatUser {

	private static final long serialVersionUID = 1L;

	private BmobRelation blogs;

	/**
	 * //显示数据拼音的首字母
	 */
	private String sortLetters;

	/**
	 * //性别-true-男
	 */
	private Boolean sex;

	/**
	 * 地理坐标
	 */
	private BmobGeoPoint location;//

	private Integer hight;

	public Integer getHight() {
		return hight;
	}

	public void setHight(Integer hight) {
		this.hight = hight;
	}

	public BmobRelation getBlogs() {
		return blogs;
	}

	public void setBlogs(BmobRelation blogs) {
		this.blogs = blogs;
	}

	public BmobGeoPoint getLocation() {
		return location;
	}

	public void setLocation(BmobGeoPoint location) {
		this.location = location;
	}

	public Boolean getSex() {
		return sex;
	}

	public void setSex(Boolean sex) {
		this.sex = sex;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

}
