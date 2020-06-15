package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Test;

public interface ITestService {
	List<Test> getTestList();
	void insertTest();
}
