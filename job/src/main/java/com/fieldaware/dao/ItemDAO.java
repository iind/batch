package com.fieldaware.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class ItemDAO {
	private static final Logger log = Logger.getLogger(ItemDAO.class);
	private JdbcTemplate jdbcTemplate = null;
	
	public void setDataSource(DataSource dataSource) {
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public Map<String, Long> getItems() throws DataAccessException {
		List<Map<String, Object>> results = jdbcTemplate.queryForList("select name, id from item");
		Map<String, Long> outerMap = new HashMap<String, Long>();
		for(Map<String, Object> m: results){
			outerMap.put(m.get("name").toString().replaceAll("[^a-zA-Z0-9]+",""), (Long)m.get("id"));
		}
		return outerMap;
	}
}
