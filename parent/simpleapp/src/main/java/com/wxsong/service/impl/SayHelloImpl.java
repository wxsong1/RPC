package com.wxsong.service.impl;

import com.wxsong.entity.Student;
import com.wxsong.rpc.server.RpcService;
import com.wxsong.service.SayHello;
@RpcService(SayHello.class)
public class SayHelloImpl implements SayHello{

	public String sayHello() {
		return "wxsong is no.1";
	}

	public Student findStudentById(String id) {
		Student s = new Student();
		s.setId("123");
		s.setName("wxsong");
		return s;
	}

}
