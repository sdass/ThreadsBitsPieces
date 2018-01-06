package com.subra.cyclicb;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class BarrierCyclic {

	final static int[][] twoDimen = new int[4][6];
	final static public CyclicBarrier cyclicBarrier;
	
	static {
		cyclicBarrier = new CyclicBarrier(twoDimen.length,  new TotalSum());
	}
	public static void main(String[] args) {
		//setup
		init(); //printDegub();
		//start parallel
		int i = twoDimen.length;
		while( i  > 0){
			new ProcessAdd(i+"-thread", i);
			i--;
		}
			//last after last piece on main()
			//printDegub();
		System.out.println("main exits.");
	}
	

	public static void delay() {
		for (int i = 0; i < 1; i++) {
			for (int j = 0; j < 999999999; j++) {
			}
		}
	}// delay()

	public static void init(){
		System.out.println("twoDimen.length=" + twoDimen.length);
		for(int i =0; i< twoDimen.length; i++){
			for(int j=0; j< twoDimen[i].length; j++){
			//System.out.format("twoDimen[%d].length=%d\n", i, twoDimen[i].length);
				twoDimen[i][j] = j+1 ; 
			}
			
		}
		
	}
	
	public static void printDegub(){
		for(int i =0; i< twoDimen.length; i++){
			for(int j=0; j< twoDimen[i].length; j++){
				System.out.print(twoDimen[i][j] + ", ");
			}
			System.out.println("\n");
		}
	}

}//class ends

 class ProcessAdd implements Runnable {

	 String name;
	 int rowNum;
	@Override
	public void run() {

		//System.out.println("thread-"+ name + " is waiting");
		int sum=0;
		//no semaphore needed parallel processing subarray
		for(int i=0; i< BarrierCyclic.twoDimen[rowNum].length; i++){
			sum = sum + BarrierCyclic.twoDimen[rowNum][i];
		}
			//BarrierCyclic.delay();
			//System.out.println("name="+ name + " sum=" + sum);
			BarrierCyclic.twoDimen[rowNum][0]= sum;
			try {
				System.out.println("!!!!!!!parties="+  BarrierCyclic.cyclicBarrier.getParties() + " waiting=" + BarrierCyclic.cyclicBarrier.getNumberWaiting());
				BarrierCyclic.cyclicBarrier.await();
				
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
		
	}
	public ProcessAdd(String name, int rowNum) {
		this.name = name;
		this.rowNum = rowNum -1;
		new Thread(this,name).start(); //start from main
	
	}
	 
 }
//-----------------------------
  class TotalSum implements Runnable {

	@Override
	public void run() {
		//last thread will do
		System.out.println(Thread.currentThread().getName() + " into final");
		int tot=0;
		for(int i=0; i< BarrierCyclic.twoDimen.length; i++){
			tot = tot + BarrierCyclic.twoDimen[i][0];
			//System.out.println(BarrierCyclic.twoDimen[i][0]);
		}
		System.out.println("total=" + tot);
		BarrierCyclic.twoDimen[BarrierCyclic.twoDimen.length -1][BarrierCyclic.twoDimen[BarrierCyclic.twoDimen.length -1].length -1] = tot;
		BarrierCyclic.printDegub();
	}
	  
  }