package com.fieldaware.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class CustomerDAO {
	private static final Logger log = Logger.getLogger(CustomerDAO.class);
	private JdbcTemplate jdbcTemplate = null;
	
	public void setDataSource(DataSource dataSource) {
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public Map<String, Long> getCustomer() throws DataAccessException {
		List<Map<String, Object>> results = jdbcTemplate.queryForList("select name,id from customer");
		Map<String, Long> custMap = new HashMap<String,Long>();
		for(Map<String, Object> m: results){
			custMap.put(m.get("name").toString().replaceAll("[^a-zA-Z0-9]+",""), (Long)m.get("id"));
		}
		return custMap;
	}
}