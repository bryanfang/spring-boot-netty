package com.example.demo.dao;

import java.util.List;

import com.example.demo.entity.Test;

public interface TestDao {
	List<Test> getAllList();
	void addTest(Test test);
}
