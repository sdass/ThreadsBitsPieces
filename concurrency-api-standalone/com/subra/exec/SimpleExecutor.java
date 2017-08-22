package com.subra.exec;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleExecutor {
	

	public static void delay(){
		// delay 
	  for(int m=0; m<2; m++){
 		for (int j = 0; j < 999999999; j++) { 
		   for (int k = 0; k < 99999999; k++) { 
		   } 
		} 
	  }
	} //delay()
  //-------------------------------------------
	
	public static void main(String [] args){

		ExecutorService executorService = Executors.newFixedThreadPool(2); //2-threads thread-pool
		//run 4 Runnable obj 
		for (int i=1; i <3; i++){
			CountDownLatch cdl1 = new CountDownLatch(5); //4 child threads ABCD and this main thread.
			
			executorService.submit(new Work("A-task", cdl1));
			executorService.submit(new Work("B-task", cdl1));
			executorService.submit(new Work("C-task", cdl1));
			executorService.submit(new Work("D-task", cdl1));
			
			
			cdl1.countDown();
			
			try {
				cdl1.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
					
			System.out.println("main exiting " + i + "th round");
	}
		
		executorService.shutdown();
		System.out.println("Finihed.");
		
	}

}


 class Work implements Runnable {
	 
	 String name;
	 CountDownLatch cdl;
	 
	  public Work(String name, CountDownLatch cdl) {
		super();
		this.name = name;
		this.cdl = cdl;
		//not creating thread myself
	}

	@Override
	 public void run(){
		
		SimpleExecutor.delay();
		System.out.println("--" + name);
		cdl.countDown();
		
		 
	 }
 }