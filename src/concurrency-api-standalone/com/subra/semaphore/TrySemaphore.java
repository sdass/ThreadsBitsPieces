package com.subra.semaphore;

import java.util.concurrent.Semaphore;

public class TrySemaphore {
	
	public static void main(String[] args){
		
		Semaphore semaphore = new Semaphore(1); // permits 1 thread at a time
		new Incrementable("thread-A", semaphore);
		new Deccrementable("thread-B", semaphore);
		
	}

	public static void delay(){
	// delay
	for (int i = 0; i < 3; i++) {
		for (int j = 0; j < 999999999; j++) {

		}
	}
   }//ends
	
}
 
class SharedVar {
	public static int sharedInt; // default initialization =0
}
//-------------------

class Incrementable implements Runnable {
	String name;
	Semaphore c;

	public Incrementable(String name, Semaphore c) {
		this.name = name;
		this.c = c;
		new Thread(this, name).start();
	}

	@Override
	public void run() {
		try{
			c.acquire();
		System.out.println(Thread.currentThread().getName() + " started running");
		for(int i=0; i < 3; i++){
			SharedVar.sharedInt++;
			TrySemaphore.delay();
			System.out.println("sharedInt=+" + SharedVar.sharedInt);
		}
		System.out.println(Thread.currentThread().getName() + " ended.");
		c.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
}

class Deccrementable implements Runnable {
	String name;
	Semaphore c;
	
	public Deccrementable(String name, Semaphore c) {
		this.name = name;
		this.c = c;
		new Thread(this, name).start();
	}

	@Override
	public void run() {
		try {
			c.acquire();
	
		System.out.println(Thread.currentThread().getName() + " started running");
		for(int i=0; i < 3; i++){
			SharedVar.sharedInt--;
			TrySemaphore.delay();
			System.out.println("sharedInt=-" + SharedVar.sharedInt);
		}
		System.out.println(Thread.currentThread().getName() + " ended.");
		c.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
}
/* output:
thread-A started running
sharedInt=+1
sharedInt=+2
sharedInt=+3
thread-A ended.
thread-B started running
sharedInt=-2
sharedInt=-1
sharedInt=-0
thread-B ended.
*/