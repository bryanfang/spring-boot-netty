package com.example.demo.netty.mapping;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Test;
import com.example.demo.service.ITestService;

@Service
public class GetMapping implements IGetMapping{

	@Autowired
	private ITestService iTestService;
	
	@Override
	public List<Test> dealGetRequest() {
		List<Test> list = iTestService.getTestList();
		return list;
	}
	
}
