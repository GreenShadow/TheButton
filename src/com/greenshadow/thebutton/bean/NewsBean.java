package com.greenshadow.thebutton.bean;

public class NewsBean {
	private String title;
	private String content;
	private long time = -1l;
	private String url;

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public String getTime() {
		long now = System.currentTimeMillis();
		long difference;
		String result = "1��";
		if (time != -1) {
			difference = now - time;
			if (difference < 1000l) { // һ��֮��
				result = "1��";
			} else if (difference >= 1000l && difference < 60000l) { // һ����֮��
				result = difference / 1000 + "��";
			} else if (difference >= 60000l && difference < 3600000l) { // һСʱ֮��
				result = difference / 1000 / 60 + "����";
			} else if (difference >= 3600000l && difference < 86400000l) { // һ��֮��
				result = difference / 1000 / 60 / 60 + "Сʱ";
			} else {
				result = difference / 1000 / 60 / 60 / 24 + "��";
			}
		}
		result += "ǰ";
		return result;
	}

	public String getUrl() {
		return url;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
