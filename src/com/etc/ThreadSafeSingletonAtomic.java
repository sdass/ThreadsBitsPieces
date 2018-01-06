package com.etc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/* threadsafe Singleton design with AmoicInteger tested with 100 threads */

public class ThreadSafeSingletonAtomic {
	private static AtomicInteger isInstanceReady = new AtomicInteger(0); //domain {0,1}
    private static ThreadSafeSingletonAtomic instance;
    int xyz;
    private ThreadSafeSingletonAtomic(){ ++ this.xyz;} //to debug

	public static ThreadSafeSingletonAtomic getInstance() {
		if (isInstanceReady.compareAndSet(0, 1)) {
			instance = new ThreadSafeSingletonAtomic();
		} else if (isInstanceReady.get() == 1) { // small time-window to create the object and assign to instance by other thread
			while (instance == null); // loop
		}
		return instance;
	}

	@Override
	public String toString() {
		return "ThreadSafeSingletonAtomic [xyz=" + xyz + "]";
	}
	
	//test with 100 threades below
	public static void main(String[] args) throws InterruptedException{
		hammer_with_100threads();
	}
	
	public static void hammer_with_100threads() throws InterruptedException{
		ExecutorService executor = Executors.newFixedThreadPool(101);

		IntStream.range(1, 101).forEach( i -> {
			executor.submit( () -> { 
				if(ThreadSafeSingletonAtomic.getInstance().xyz != 1){
					System.out.println("ERROR-Singleton"); 
				} 
				});

		});
		executor.awaitTermination(200, TimeUnit.MILLISECONDS);
		executor.shutdownNow();
		if(ThreadSafeSingletonAtomic.getInstance().xyz == 1){
			System.out.println("SUCCESS-singleton with 100 threads testing");
		}		
	}
	
}//class ends
