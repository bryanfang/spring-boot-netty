package com.example.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.TestDao;
import com.example.demo.entity.Test;
import com.example.demo.service.ITestService;

@Service
public class TestServiceImpl implements ITestService {

	@Autowired
	private TestDao testRepository;
	
	private static Integer count = 0;
	
	@Override
	public List<Test> getTestList() {
		return testRepository.getAllList();
	}

	@Override
	public void insertTest() {
		Test test = new Test();
		test.setTest("test");
		test.setCount(count++);
		testRepository.addTest(test);
	}

}
