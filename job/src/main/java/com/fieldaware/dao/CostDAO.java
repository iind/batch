package com.fieldaware.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class CostDAO {
	private static final Logger log = Logger.getLogger(CostDAO.class);
	private JdbcTemplate jdbcTemplate = null;
	
	public void setDataSource(DataSource dataSource) {
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public Map<Integer,Map<Integer, Double>>  getCost() throws DataAccessException {
		List<Map<String, Object>> results = jdbcTemplate.queryForList("select contractor_id, item_id, unit_cost from cost");
		Map<Integer,Map<Integer, Double>> outerMap = new HashMap<Integer,Map<Integer, Double>>();
		for(Map<String, Object> m: results){
			if(outerMap.get((Integer)m.get("contractor_id")) != null ){
				outerMap.get((Integer)m.get("contractor_id")).put((Integer)m.get("item_id"), (Double)m.get("unit_cost"));
			}else{
				Map<Integer, Double> priceMap = new HashMap<Integer,Double>();
				priceMap.put((Integer)m.get("item_id"), (Double)m.get("unit_cost"));
				outerMap.put((Integer)m.get("contractor_id"), priceMap);
			}
		}
		return outerMap;
	}
}
