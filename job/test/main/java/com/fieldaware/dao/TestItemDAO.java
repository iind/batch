package com.fieldaware.dao;

import java.util.Map;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestItemDAO {
	ApplicationContext applicationContext = new ClassPathXmlApplicationContext("scheduler-context.xml");
	ItemDAO itemDAO = (ItemDAO) applicationContext.getBean("itemDAO");
	Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
	
	@Test
	public void testGetItemType(){
		String json = prettyGson.toJson(itemDAO.getItems());
		System.out.println(json);
	}
	
	@Test
	public void testGetSpecificItemType(){
		Map<String, Long> itemMap = itemDAO.getItems();
		String json = prettyGson.toJson(itemMap.get(6));
		System.out.println(json);
	}
	
}
