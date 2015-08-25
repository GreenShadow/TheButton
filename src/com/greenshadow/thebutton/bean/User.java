package com.greenshadow.thebutton.bean;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * User Bean������BmobChatUser����
 */
public class User extends BmobChatUser {

	private static final long serialVersionUID = 1L;

	/**
	 * //��ʾ����ƴ��������ĸ
	 */
	private String sortLetters;

	/**
	 * //�Ա�-true-��
	 */
	private Boolean sex;

	/**
	 * ��������
	 */
	private BmobGeoPoint location;

	/**
	 * ƴͼ��Ϸ�Ѷ�
	 */
	private int difficulty = 2;

	public void setDifficulty(int d) {
		this.difficulty = d;
	}

	public int getDifficulty() {
		return difficulty;
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
