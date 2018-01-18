package com.etc;

import java.util.concurrent.Semaphore;

class BrokenException extends RuntimeException {
	public BrokenException(int n, String msg) {
		super(msg + " " + n);
	}
}

public class MonitorAbstractionSemaphore {

	//Semaphore -- implemented with multiple producer consumer
	//CountDownLatch
	//CyclicBarrier
	//Exchanger
	//Phaser
	
	static class SharedQ {
		volatile int n = -1;
		
		// synchronized int get(){
		int get(){
		try{
			sem0.acquire();
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			if(n == -1) throw new BrokenException(n, "Nothing for Consumer");
			int tmp = n; n= -1;
			//sem0.release();
			sem1.release();
			return tmp;
		}

		//synchronized void put(int n)  {// monitor getting hold
		 void put(int n)  {
			try{
			sem1.acquire();
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			if(this.n != -1){ throw new BrokenException(n, "Notready for Producer"); }
			this.n = n;
			sem0.release(1);
			//sem1.release();
			
		}		
	}//SharedQ
	
	static class ProducerTask implements Runnable{
		SharedQ q;
		//static AtomicBoolean releasFlag = new AtomicBoolean(true);
		
		public ProducerTask(SharedQ que) { 	this.q = que;  }

		public void run(){
			int n = (int) (Math.random()*100);
			q.put(n);
			System.out.println( "Producer " + n);
			
			
		}
	}
	
	static Semaphore sem1 = new Semaphore(1);
	static Semaphore sem0 = new Semaphore(0);
	
	static class ConsumerTask implements Runnable{
		SharedQ q;
		
		public ConsumerTask(SharedQ que) { 	this.q = que;  }
		
		public void run(){
			int n = q.get();
			System.out.println( "Consumer " + n);
			
		}
	}
	
	public static void main(String[] args) throws InterruptedException{
		SharedQ sq = new SharedQ();
		Semaphore sem = new Semaphore(2);

		Thread c1 = new Thread(new ConsumerTask(sq));
		Thread p1 = new Thread(new ProducerTask(sq));
		p1.start(); 
		c1.start();  
		new Thread(new ConsumerTask(sq)).start();
		new Thread(new ProducerTask(sq)).start();
		new Thread(new ConsumerTask(sq)).start();
		new Thread(new ProducerTask(sq)).start();
		
	}
	
}
