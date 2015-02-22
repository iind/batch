package com.fieldaware.dao;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestPriceDAO {
	ApplicationContext applicationContext = new ClassPathXmlApplicationContext("scheduler-context.xml");
	PriceDAO priceDAO = (PriceDAO) applicationContext.getBean("priceDAO");
	Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
	
	@Test
	public void testGetPrice(){
		String json = prettyGson.toJson(priceDAO.getPrice());
		System.out.println(json);
	}
}
