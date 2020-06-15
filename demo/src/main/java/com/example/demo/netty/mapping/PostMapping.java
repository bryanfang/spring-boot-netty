package com.example.demo.netty.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.service.ITestService;

@Service
public class PostMapping implements IPostMapping {

	@Autowired
	private ITestService iTestService;
	
	@Override
	public void dealPost() {
		iTestService.insertTest();
	}

}
