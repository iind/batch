package com.fieldaware.dao;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestItemTypeDAO {
	ApplicationContext applicationContext = new ClassPathXmlApplicationContext("scheduler-context.xml");
	ItemTypeDAO itemTypeDAO = (ItemTypeDAO) applicationContext.getBean("itemTypeDAO");
	Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
	
	@Test
	public void testGetItemType(){
		String json = prettyGson.toJson(itemTypeDAO.getItemType());
		System.out.println(json);
	}
}
