package com.wxsong.service;

import com.wxsong.entity.Student;

public interface SayHello {

	public String sayHello();
	
	public Student findStudentById(String id);
}
