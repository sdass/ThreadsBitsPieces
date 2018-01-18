package com.etc;

import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class MonitorAbstractionCyclicBarrier {
	/* 10 workers run parallel and Accumulator thread waits for 10 to finish first. */

	//Semaphore 
	//CountDownLatch
	//CyclicBarrier  -- implemented cyclicalbarrier use twice and a Runnable
	//Exchanger
	//Phaser
	
	static class SharedQ {
		static  volatile int[] input = new int[100];
		static{
			IntStream.range(1, 101).forEach(i-> input[i-1] = i);
		}
		
		public SharedQ() {
			System.out.println("in SharedQ constructor");
			//Arrays.stream(input).forEach(x->System.out.print(x+ ","));
			System.out.println();
		}
				
	}//SharedQ
	
	static class UnitWorker implements Runnable{
		int start; //10 unit each
		int end; //exclusive
		int[] result;
		SharedQ q;
		
		public UnitWorker(int s, int e, int[] res, SharedQ sq) { 	this.start = s; this.end = e; this.result = res; this.q = sq; }

		public void run(){			
			int sum=0;  int begin =  start;
			for(int i=start; i< end; i++){
				sum = sum + q.input[start++];				
			}			
			System.out.println( "UnitWorker range-points=" + begin + " - " + end + " and sum=" + sum);
			result[begin / 10] = sum;			
			System.out.println("party=" + cyclicBarrierB.getParties() + " waitingcount=" + cyclicBarrierB.getNumberWaiting());
			try {
				cyclicBarrierB.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
		}
	}
	
	static class Accumulator implements Runnable{
		
		int[] res;
		public Accumulator(int[] subResultArray) { this.res = subResultArray;	 }
		int sum=0;
		public void run(){

			for(int i=0; i < res.length; ){
				sum = sum + res[i++];
			}
			System.out.println("RESULT=" + sum);
			sum =0; //reinitializing for reusing cyclic barrier 1275 + 37775 = 5050
		}
	}

	static CyclicBarrier cyclicBarrierB;
	
	public static void main(String[] args) throws InterruptedException{
		//concurrent writing allowed because slot is fixed for each write
		int[] subResultArray = new int[10];
		SharedQ sharedInput = new SharedQ();
		cyclicBarrierB = new CyclicBarrier(5, new Accumulator(subResultArray));
		
		//ExecutorService executorService = Executors.newFixedThreadPool(7);
		ExecutorService executorService = Executors.newCachedThreadPool();
		
		
		for(int i=0; i<50; i+=10){
			executorService.submit(new UnitWorker(i, i+10, subResultArray, sharedInput ));
		}
		//Thread.sleep(1000);
		
		//reuse (recycle) barrier		
		cyclicBarrierB.reset();
		for(int i=50; i<100; i+=10){
			executorService.submit(new UnitWorker(i, i+10, subResultArray, sharedInput ));
		}
	
		//no need Separate Accumulator thread. 5th/last arriving thread do the Runnable sum
		
		executorService.awaitTermination(1, TimeUnit.SECONDS); executorService.shutdownNow();

	}
	
}
