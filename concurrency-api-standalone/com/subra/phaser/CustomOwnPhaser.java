package com.subra.phaser;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/* Phaser implemented without Phaser class (using only CountDownLatch and CyclicBarrier 
 */


public class CustomOwnPhaser {

	static CountDownLatch maincountDownLatch = new CountDownLatch(1); //main() last to exit
	public static void main(String[] args)  {
		CyclicBarrier cyclicBarrierp1 = new CyclicBarrier(2); // 1+ for main-thread. reused
		
		Thread ph1_thread = new Thread(new Phase1Driver("thread-phase1-driver", cyclicBarrierp1), "phase1-driver");

		//ph1_thread.setDaemon(true);
		ph1_thread.start();
		System.out.println("main-1");
		
		//ph1_thread.join();
		try {
			cyclicBarrierp1.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}

		System.out.println("main-2 parties=" + cyclicBarrierp1.getParties() + "waitcount=" + cyclicBarrierp1.getNumberWaiting());
		
		//reusing cycliBarrier of phase 1 for next phases
		//cyclicBarrierp1.reset(); not needed
		Thread ph2_thread = new Thread(new Phase2Driver("thread-phase2-driver", cyclicBarrierp1), "phase2-driver");
		ph2_thread.start();
		
		System.out.println("main-3");
		
		try {
			cyclicBarrierp1.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		
		System.out.println("main-4");
		new Phase3Driver("thread-phase3-driver", cyclicBarrierp1); //starting in the child-thread
		
				
		try {
			cyclicBarrierp1.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		//main to always exit last
		
		try {
			maincountDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("main-5");
		
		
	}//main()
	public static void delay(){
		for (int i = 0; i < 1; i++) {
			for (int j = 0; j < 999999999; j++) {
			}
		}
	   }//delay()


}
//---------------------
class CheckInventory implements Runnable{ //of phase-1-check-inventory
	String name;
	CountDownLatch latch;
	
	public CheckInventory(String name, CountDownLatch latch) {
		super();
		this.name = name;
		this.latch = latch;
		new Thread(this, name).start();
	}

	@Override
	public void run() {
		System.out.println(name + ":on phase 1 checking Inventrory");
		CustomOwnPhaser.delay();
		System.out.println(name + ":exiting phase 1 checking Inventrory");
		latch.countDown();
	}
}
//---------------------
class ConfirmPricing implements Runnable{ //of phase-1-confirm-price
	String name;
	CountDownLatch latch;
	
	public ConfirmPricing(String name, CountDownLatch latch) {
		super();
		this.name = name;
		this.latch = latch;
		new Thread(this, name).start();
	}

	@Override
	public void run() {
		System.out.println(name + ":on phase 1 Confirming price");
		CustomOwnPhaser.delay();
		System.out.println(name + ":exiting phase 1 Confirming price");	
		latch.countDown();
	}
}
//-------------------------------------
class ValidateCustomer implements Runnable{ //of phase-2-validate-customer
	String name;
	CountDownLatch latch;
	
	public ValidateCustomer(String name, CountDownLatch latch) {
		this.name = name;
		this.latch = latch;
		new Thread(this, name).start();
	}

	@Override
	public void run() {
		System.out.println(name + ":on phase 2 validating customer");
		CustomOwnPhaser.delay();
		System.out.println(name + ":exiting phase 2 validating customer");	
		latch.countDown();
	}
}
//-----------------------
class CalculateShipping implements Runnable{ //of phase-2-calculate-shipping
	String name;
	CountDownLatch latch;
	
	public CalculateShipping(String name, CountDownLatch latch) {
		this.name = name;
		this.latch = latch;
		new Thread(this, name).start();
	}

	@Override
	public void run() {
		System.out.println(name + ":on phase 2 calculate shipping");
		CustomOwnPhaser.delay();
		System.out.println(name + ":exiting phase 2 calculate shipping");	
		latch.countDown();

	}
}
//--------------------------------
class MakeTransaction implements Runnable{ //of phase-3-make-transaction
	String name;
	CountDownLatch latch;
	
	public MakeTransaction(String name, CountDownLatch latch) {
		super();
		this.name = name;
		this.latch = latch;
		new Thread(this, name).start();
	}

	@Override
	public void run() {
		System.out.println(name + ":on phase 3 make transaction");
		CustomOwnPhaser.delay();
		System.out.println(name + ":exiting phase 3 make transaction");	
		latch.countDown();

	}
}
//--------------------------------
class GiveShippingDate implements Runnable{ //of phase-3-give-shipping-date
	String name;
	CountDownLatch latch;
	
	public GiveShippingDate(String name, CountDownLatch latch) {
		super();
		this.name = name;
		this.latch = latch;
		new Thread(this, name).start();
	}

	@Override
	public void run() {
		System.out.println(name + ":on phase 3 give shipping date");
		CustomOwnPhaser.delay();
		System.out.println(name + ":exiting phase 3 give shipping date");	
		latch.countDown();

	}
}
//----------------------------
class SendFulfillmentRequest implements Runnable{ //of phase-3-send-fulfillment-request
	String name;
	CountDownLatch latch;
	
	public SendFulfillmentRequest(String name, CountDownLatch latch) {
		super();
		this.name = name;
		this.latch = latch;
		new Thread(this, name).start();
	}

	@Override
	public void run() {
		System.out.println(name + ":on phase 3 send fulfillment request");
		CustomOwnPhaser.delay();
		System.out.println(name + ":exiting phase 3 send fulfillment request");	
		latch.countDown();

	}
}
//--------------phase 1------------------
//-------------------------------
 class Phase1Driver implements Runnable {
	 String name;
	 CyclicBarrier cyclicBarrier_phase1;
	 
	 public Phase1Driver(String name, CyclicBarrier cyclicBarrier){
		 this.name = name;
		 this.cyclicBarrier_phase1 = cyclicBarrier;
		 //start from main thread
	 }
	
	 //access by only one thread
	 @Override
	public void run() {
		CountDownLatch latch = new CountDownLatch(2);// 2 threads to finish first
		//It creates two threads
		new CheckInventory("thread-phs1-CheckInventory",  latch);
		new ConfirmPricing("thread-phs1-ConfirmPricing", latch);
		try {
			latch.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			cyclicBarrier_phase1.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		System.out.println("Phase1Driver-thread is exiting");
	}
	 
 }
 //------------------------
 class Phase2Driver implements Runnable {
	 String name;
	 CyclicBarrier cyclicBarrier_phase2;
	 
	 public Phase2Driver(String name, CyclicBarrier cyclicBarrier){
		 this.name = name;
		 this.cyclicBarrier_phase2 = cyclicBarrier;
		 //start from main thread
	 }
	
	 //access by only one thread
	 @Override
	public void run() {
		CountDownLatch latch = new CountDownLatch(2);// 2 threads to finish first
		//It creates two threads
		new ValidateCustomer("thread-phs2-ValidateCustomer",  latch);
		new CalculateShipping("thread-phs2-CalculateShipping", latch);
		try {
			latch.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			cyclicBarrier_phase2.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		System.out.println("Phase2Driver-thread is exiting");
	}
	 
 }
 //-----------------------------------
 class Phase3Driver implements Runnable {
	 String name;
	 CyclicBarrier cyclicBarrier_phase3;
	 
	 public Phase3Driver(String name, CyclicBarrier cyclicBarrier){
		 this.name = name;
		 this.cyclicBarrier_phase3 = cyclicBarrier;
		 new Thread(this, name).start();
	 }
	
	 //access by only one thread
	 @Override
	public void run() {
		CountDownLatch latch = new CountDownLatch(3);// 3 child threads to finish at last phase
		//It creates two threads
		new MakeTransaction("thread-phs3-MakeTransaction",  latch);
		new GiveShippingDate("thread-phs3-GiveShippingDate", latch);
		new SendFulfillmentRequest("thread-phs3-SendFulfillmentRequest", latch);
		try {
			latch.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			cyclicBarrier_phase3.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		System.out.println("Phase3Driver-thread is exiting");
		CustomOwnPhaser.maincountDownLatch.countDown();
	}
	 
 }