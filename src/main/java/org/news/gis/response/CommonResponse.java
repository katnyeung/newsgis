package org.news.gis.response;

public class CommonResponse {
	Object body;

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public static CommonResponse success(Object body) {
		CommonResponse cr = new CommonResponse();
		cr.setBody(body);
		return cr;
	}
}
