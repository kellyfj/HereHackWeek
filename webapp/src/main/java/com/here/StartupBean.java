package com.here;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.InitializingBean;

public class StartupBean implements InitializingBean {
	private ExecutorService executor;
	
	@Override
	public void afterPropertiesSet() {	
		/**
		 * Launch Hosebird service in a thread because
		 * 1) It runs continuously
		 * 2) If we didn't do it in a thread it would block the rest of the web server startup
		 */
		this.executor = Executors.newFixedThreadPool(1);
		HosebirdThread t= new HosebirdThread();
		executor.execute(t);
	}
	
	@PreDestroy
	public void atShutdown() {
		this.executor.shutdownNow();
	}
}

class HosebirdThread implements Runnable {

	@Override
	public void run() {
		try {

			String consumerKey = "R2srtfc5kEIxuQfiyG9pEA";
			String consumerSecret = "0id5kQT8ZSkQuNRBDhIlKOrE3sKvPDGAfmgIVVZPI";
			String accessToken = "312942836-izeK4pfEQGOt48ySWyKYRQJBdgz716cFFcDqh1eg";
			String accessTokenSecret = "d99NY9IjkygnrPtAqafn77aDyqp0PAhztSaXhqyJE40fp";

			System.out.println("Starting Hosebird Service . . . . .");
			HosebirdService.oauth(consumerKey, consumerSecret, accessToken,
					accessTokenSecret);
		} catch (InterruptedException e) {
			System.err.println(e);
		}
		
	}
	
}
