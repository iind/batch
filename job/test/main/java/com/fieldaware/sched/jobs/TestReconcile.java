package com.fieldaware.sched.jobs;

import java.util.List;

import org.junit.Assert;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestReconcile {
	
	ApplicationContext applicationContext = new ClassPathXmlApplicationContext("scheduler-context.xml");
	Reconcile rConcile = (Reconcile) applicationContext.getBean("reconcile");
	
	@Test
	public void testGetCompletedJobs(){
		List<String> jobIds = rConcile.getCompletedJobs();
		for(String jId : jobIds){
			System.out.println(jId);
		}
	}
	
	@Test
	public void testUpdateJobDetails(){
		rConcile.updateJobDetails("J99");
	}
	
	@Test
	public void testAll(){
		List<String> jobIds = rConcile.getCompletedJobs();
		Assert.assertTrue(!jobIds.isEmpty());
		
		rConcile.loadData();
		rConcile.updateJobDetails("J99");
	}
}
