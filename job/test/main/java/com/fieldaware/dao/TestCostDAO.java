package com.fieldaware.dao;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestCostDAO {
	ApplicationContext applicationContext = new ClassPathXmlApplicationContext("scheduler-context.xml");
	CostDAO costDAO = (CostDAO) applicationContext.getBean("costDAO");
	Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
	
	@Test
	public void testGetCost(){
		String json = prettyGson.toJson(costDAO.getCost());
		System.out.println(json);
	}
}
