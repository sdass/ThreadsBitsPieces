package com.subra.semaphore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducConsumer {
	
	static AtomicInteger atomicInteger = new AtomicInteger();
	
	static List<String> sharedList = new ArrayList<String>();//shared arraylist
	static CountDownLatch countdownLatch;

	public static void main(String[] args) {
		
		System.out.println("size=" + sharedList.size());
		Semaphore semaphore = new Semaphore(1); //1-means only 1 thread at a time on critical-sec
		new Consumable("C", semaphore);
		//1. putting Milk
		new Producble("M", semaphore, "Milk");
		countdownLatch = new CountDownLatch(2);//2 threads
		
		for(String s: sharedList){
			System.out.println(s);
		}

		//2. putting bread
		
		new Producble("B", semaphore, "Bread");
		System.out.println("count-down-latch count=" + countdownLatch.getCount() + " atomin=" + ProducConsumer.atomicInteger.get());
		
		try{
			countdownLatch.await(); //current main thread calling below
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("\nafter coutdownlatch is zeroed size=" + sharedList.size() + " atomin=" + ProducConsumer.atomicInteger.get());
			if (sharedList.size()>0){
				
				for(String s: sharedList){
					System.out.print(s + " ");
				}
			}
		
	}//main()

	
	public static void delay(){
		for (int i = 0; i < 1; i++) {
			for (int j = 0; j < 999999999; j++) {
			}
		}
	   }//delay()
}//class ends


class Producble implements Runnable{

	String name;
	Semaphore semaphore;
	String thing;
	
	public Producble(String name, Semaphore semaphore, String thing) {
		this.name = name;
		this.semaphore = semaphore;
		this.thing = thing;
		new Thread(this, name).start();
	}

	@Override
	public void run() {
		System.out.println( Thread.currentThread().getName()+ " Thread waiting");
		for(int j=0; j<3; j++){
			ProducConsumer.delay();
			try {
				semaphore.acquire();
				for (int i = 0; i < 1; i++) {
					ProducConsumer.delay();
				}
				System.out.println(this.name );
				ProducConsumer.sharedList.add(thing);
				ProducConsumer.atomicInteger.incrementAndGet();
				
				semaphore.release();
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		ProducConsumer.countdownLatch.countDown();
	}

}
//----------------------------
class Consumable implements Runnable{

	String name;
	Semaphore semaphore;
	String thing;
	
	public Consumable(String name, Semaphore semaphore) {
		this.name = name;
		this.semaphore = semaphore;
		new Thread(this, name).start();
	}

	@Override
	public void run() {
		System.out.println( Thread.currentThread().getName()+ " Thread waiting");
			ProducConsumer.delay();
			try {
				int m;
			  while( (m = ProducConsumer.atomicInteger.getAndDecrement())>= 0){
				System.out.println("m=" + m);
				semaphore.acquire();
				for (int i = 0; i < 1; i++) {
					ProducConsumer.delay();
				}
				System.out.println("\n<<consumed=" +ProducConsumer.sharedList.remove(0));
				semaphore.release();
				
			  }
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

	}
	
}
