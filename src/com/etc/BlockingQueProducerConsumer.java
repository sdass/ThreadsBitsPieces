package com.etc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
/*
 * ArrayBlockingQueue use ReentrantLock (with Condition obj) in turn use AbstractQueuedSynchronizer.compareAndSetState() in turn use
 * sun.misc.Unsafe.compareAndSwapInt(this, stateOffset, expect, update). Idiom of Unsafe is identical to any CAS (AtomicInteger) op. 
 * Because monitor[lock/unlock and signal/await] taken care of by ArrayBlockingQueue implementation, so code become compact. 
 */
public class BlockingQueProducerConsumer {
	
	private static final ArrayBlockingQueue<Integer> blcQue = new ArrayBlockingQueue<Integer>(3);

	public static void main(String[] args) throws InterruptedException {

		System.out.println("begin");

		Runnable producertask = () ->{
			IntStream.range(1,101).forEach(
					x -> {
						try {
							blcQue.put(x);//atomic
							System.out.print("put x=" + x);
						} catch (Exception e) {
							e.printStackTrace();
						}					
					}); };
					
		Runnable consumertask = () ->{
			IntStream.range(1,101).forEach(
					x -> {
						try {
							System.out.println("taken" + blcQue.take() + ".");//atomic only take()
						} catch (Exception e) {
							e.printStackTrace();
						}					
					}); };

		ExecutorService executor = Executors.newFixedThreadPool(2);
		executor.submit(consumertask); executor.submit(producertask);
		executor.awaitTermination(500, TimeUnit.MILLISECONDS);
		executor.shutdown();							
	}
}

