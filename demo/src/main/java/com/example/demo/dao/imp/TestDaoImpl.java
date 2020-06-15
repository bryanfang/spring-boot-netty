package com.example.demo.dao.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.dao.TestDao;
import com.example.demo.entity.Test;
@Repository
public class TestDaoImpl implements TestDao {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	
	@Override
	public List<Test> getAllList() {
		List<Test> retList = new ArrayList<>();
		List<Map<String, Object>> resultSet = jdbcTemplate.queryForList("select * from test");
		for(Map<String,Object> row : resultSet) {
			Test t = new Test();
			t.setId((int)row.get("id"));
			t.setTest((String)row.get("test"));
			t.setCount((int)row.get("count"));
			retList.add(t);
		}
		return retList;
	}

	@Override
	public void addTest(Test t) {
		jdbcTemplate.update("insert into test (test, count) values(?,?)",
                t.getTest(), t.getCount());
	}

}
