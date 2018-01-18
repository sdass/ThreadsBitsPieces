package com.etc;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class MonitorAbstractionCountDownLatch {
	/* 10 workers run parallel and Accumulator thread waits for 10 to finish first. */

	//Semaphore 
	//CountDownLatch -- implemented with parallel storming Unit worker and Single Accumulator waiting
	//CyclicBarrier
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
			try {
				cntULatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int sum=0;  int begin =  start;
			for(int i=start; i< end; i++){
				sum = sum + q.input[start++];				
			}			
			System.out.println( "UnitWorker range-points=" + begin + " - " + end + " and sum=" + sum);
			result[begin / 10] = sum;
			
			cntULatch.countDown();
			cntAccumLatch.countDown();
			
			
		}
	}
	
	static CountDownLatch cntULatch = new CountDownLatch(0);//begin first changed from 1
	static CountDownLatch cntAccumLatch = new CountDownLatch(10);
	
	static class Accumulator implements Runnable{
		
		int[] res;
		public Accumulator(int[] subResultArray) { this.res = subResultArray;	 }
		int sum=0;
		public void run(){
			try {
				System.out.println("AccumLatch count=" + cntAccumLatch.getCount());
				cntAccumLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for(int i=0; i < res.length; ){
				sum = sum + res[i++];
			}
			System.out.println("RESULT=" + sum);
		}
	}
	
	public static void main(String[] args) throws InterruptedException{
		//concurrent writing allowed because slot is fixed for each write
		int[] subResultArray = new int[10];
		SharedQ sharedInput = new SharedQ();

		//this accumulator must wait for other 10 to finish
		new Thread(new Accumulator(subResultArray)).start();
		
		/*	old way	
 		for(int i=0; i<100; i+=10){
			//System.out.println(i + " initing.." + (i+ 10));
			new Thread(new UnitWorker(i, i+10, subResultArray, sharedInput )).start();
		}
		*/
	
		ExecutorService executorService = Executors.newFixedThreadPool(7);
		for(int i=0; i<100; i+=10){
			executorService.submit(new UnitWorker(i, i+10, subResultArray, sharedInput ));
		}
	
		executorService.awaitTermination(1, TimeUnit.SECONDS); executorService.shutdownNow();

	}
	
}
