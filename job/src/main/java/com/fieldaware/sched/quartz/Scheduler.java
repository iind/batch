package com.fieldaware.sched.quartz;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Scheduler {
	private static Scheduler c1XScheduler = new Scheduler();

	@SuppressWarnings("unused")
	private ApplicationContext schedulerContext;

	private Scheduler() {
		schedulerContext = new ClassPathXmlApplicationContext("scheduler-context.xml");
	}

	public static Scheduler getInstance() {
		return c1XScheduler;
	}

	public static void main(String args[]) throws Exception {
		Scheduler.getInstance();
	}
}
