package com.utils;

public class szIP {
	private String id = "";
	private String ip = "";
	
	public szIP(){}
	
	public szIP(String id, String ip) {
		this.id = id;
		this.ip = ip;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ip;
	}

}