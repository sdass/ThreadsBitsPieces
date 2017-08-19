package com.subra.cdlatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class AffectCountDownLatch {

	public static void main(String[] args) {

		CountDownLatch countDownLatch = new CountDownLatch(3);
		System.out.println(Thread.currentThread().getName() + "-thread is to exits");
		Semaphore semaphore = new Semaphore(1);
		new TheRunnable("LastOne", countDownLatch, semaphore); // this will go last

		new TheRunnable("A", countDownLatch, semaphore);
		new TheRunnable("B", countDownLatch, semaphore);
		new TheRunnable("C", countDownLatch, semaphore);
		//new TheRunnable("D", countDownLatch, semaphore);
		

		
	}
	
	public static void delay() {
		// delay
		for (int j = 0; j < 999999999; j++) {
			for (int k = 0; k < 99999999; k++) {
			}
		}
	}

}
//--------------------------------

class TheRunnable implements Runnable {

	String name;
	CountDownLatch cntdnLatch;
	Semaphore semaphore;
	
	
	public TheRunnable(String name, CountDownLatch cntdnLatch, Semaphore semaphore) {
		this.name = name;
		this.cntdnLatch = cntdnLatch;
		this.semaphore = semaphore;
		new Thread(this, name).start();
	}


	@Override
	public void run() {

		System.out.println(Thread.currentThread().getName() + "-->> status" + Thread.currentThread().getState());
		try {
			semaphore.acquire();
			
			
			if(Thread.currentThread().getName().equals("LastOne")){
					if (cntdnLatch.getCount() > 0) semaphore.release();
					cntdnLatch.await();
	
			}
			
			System.out.println(Thread.currentThread().getName() + " status" + Thread.currentThread().getState());
			
			//no put here syncronization here

			for(int i=0; i<4; i++){
				System.out.println( Thread.currentThread().getName() + "i= " + i + " latchCount=" + cntdnLatch.getCount());
				AffectCountDownLatch.delay();
			}
		
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cntdnLatch.countDown();
		System.out.println("<--------After countdown latchCount=" + cntdnLatch.getCount());
		semaphore.release();
		//cntdnLatch.notifyAll();
		
		System.out.println(Thread.currentThread().getName() + " status" + Thread.currentThread().getState());
		
	}
	
}