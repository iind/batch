package com.fieldaware.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class PriceDAO {
	private static final Logger log = Logger.getLogger(PriceDAO.class);
	private JdbcTemplate jdbcTemplate = null;
	
	public void setDataSource(DataSource dataSource) {
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public Map<Integer,Map<Integer, Double>>  getPrice() throws DataAccessException {
		List<Map<String, Object>> results = jdbcTemplate.queryForList("select customer_id, item_id, unit_price from test.price");
		Map<Integer,Map<Integer, Double>> outerMap = new HashMap<Integer,Map<Integer, Double>>();
		for(Map<String, Object> m: results){
			if(outerMap.get((Integer)m.get("customer_id")) != null ){
				outerMap.get((Integer)m.get("customer_id")).put((Integer)m.get("item_id"), (Double)m.get("unit_price"));
			}else{
				Map<Integer, Double> priceMap = new HashMap<Integer,Double>();
				priceMap.put((Integer)m.get("item_id"), (Double)m.get("unit_price"));
				outerMap.put((Integer)m.get("customer_id"), priceMap);
			}
		}
		return outerMap;
	}
}
