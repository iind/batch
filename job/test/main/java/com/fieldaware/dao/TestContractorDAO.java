package com.fieldaware.dao;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestContractorDAO {
	ApplicationContext applicationContext = new ClassPathXmlApplicationContext("scheduler-context.xml");
	ContractorDAO contDAO = (ContractorDAO) applicationContext.getBean("contractorDAO");
	Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
	
	@Test
	public void testGetContractors(){
		String json = prettyGson.toJson(contDAO.getContractors());
		System.out.println(json);
	}
}
