package com.fieldaware.dao;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestCustomerDAO {
	ApplicationContext applicationContext = new ClassPathXmlApplicationContext("scheduler-context.xml");
	CustomerDAO custDAO = (CustomerDAO) applicationContext.getBean("customerDAO");
	Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
	
	@Test
	public void testGetCustomer(){
		String json = prettyGson.toJson(custDAO.getCustomer());
		System.out.println(json);
	}
}
