package com.wxsong.common;

public class Response {

	private String requestId;
    private Throwable error;
    private Object result;
	public String getRequestId() {
		return requestId;
	}
	public Throwable getError() {
		return error;
	}
	public Object getResult() {
		return result;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public void setError(Throwable error) {
		this.error = error;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	
}
