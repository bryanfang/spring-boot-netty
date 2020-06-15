package com.example.demo.entity;

import java.io.Serializable;

public class Test implements Serializable {
	
	private static final long serialVersionUID = -7986285695673133035L;

	private Integer id;

	private String test;
	
	private Integer count;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTest() {
		return test;
	}
	public void setTest(String test) {
		this.test = test;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}

}
