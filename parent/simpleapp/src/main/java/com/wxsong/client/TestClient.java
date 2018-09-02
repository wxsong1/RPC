package com.wxsong.client;

import com.wxsong.registry.ServiceDiscovery;
import com.wxsong.service.SayHello;

public class TestClient {

	public static void main(String[] args) {
		RpcProxy proxy = new RpcProxy(new ServiceDiscovery());
		SayHello sayHello = proxy.getProxy(SayHello.class);
		System.out.println(sayHello.sayHello());
		System.out.println(sayHello.findStudentById("123"));
	}
}
