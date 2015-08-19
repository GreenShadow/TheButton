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
		String result = "1秒";
		if (time != -1) {
			difference = now - time;
			if (difference < 1000l) { // 一秒之内
				result = "1秒";
			} else if (difference >= 1000l && difference < 60000l) { // 一分钟之内
				result = difference / 1000 + "秒";
			} else if (difference >= 60000l && difference < 3600000l) { // 一小时之内
				result = difference / 1000 / 60 + "分钟";
			} else if (difference >= 3600000l && difference < 86400000l) { // 一天之内
				result = difference / 1000 / 60 / 60 + "小时";
			} else {
				result = difference / 1000 / 60 / 60 / 24 + "天";
			}
		}
		result += "前";
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
