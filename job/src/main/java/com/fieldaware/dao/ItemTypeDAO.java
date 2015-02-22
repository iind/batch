package com.fieldaware.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class ItemTypeDAO {
	private static final Logger log = Logger.getLogger(ItemTypeDAO.class);
	private JdbcTemplate jdbcTemplate = null;
	
	public void setDataSource(DataSource dataSource) {
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public Map<String, Long> getItemType() throws DataAccessException {
		List<Map<String, Object>> results = jdbcTemplate.queryForList("select name,id from test.item_type");
		Map<String, Long> itemMap = new HashMap<String,Long>();
		for(Map<String, Object> m: results){
			itemMap.put(m.get("name").toString().replaceAll("[^a-zA-Z]+",""), (Long)m.get("id"));
		}
		return itemMap;
	}
}
