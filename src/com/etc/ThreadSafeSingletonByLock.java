package com.etc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/* Singleton with Lock [Condition] but not clean as Atomic */
public class ThreadSafeSingletonByLock {

	private static ReentrantLock rl = new ReentrantLock();
	private static Condition notAssignedCondition;
	private static ThreadSafeSingletonByLock instance;
	int xyz;

	private ThreadSafeSingletonByLock() {
		++xyz;
	}

	private static ThreadSafeSingletonByLock getFactoryInsance() {
		if (instance == null) { // create
			try{
			if (rl.tryLock()) { // true got the lock
				if (instance == null) {
					notAssignedCondition = rl.newCondition();
					instance = new ThreadSafeSingletonByLock();
					notAssignedCondition.signalAll();
				}
			
			} else { // did not get the lock
				if (instance == null) {
					try {
						notAssignedCondition.await(); // loopwaiting?
						// when signalled singleton already created
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			}finally{
				System.out.println(rl.getWaitQueueLength(notAssignedCondition) + "<>" + rl.getHoldCount());
				rl.unlock();
			}
		}
		return instance;
	}// ThreadSafeSingletonByLock() ends

	@Override
	public String toString() {
		return "ThreadSafeSingletonAtomic [xyz=" + xyz + "]";
	}

	// test with 100 threades below
	public static void main(String[] args) throws InterruptedException {
		hammer_with_100threads();
	}

	public static void hammer_with_100threads() throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(101);

		IntStream.range(1, 101).forEach(i -> {
			executor.submit(() -> {
				if (ThreadSafeSingletonAtomic.getInstance().xyz != 1) {
					System.out.println("ERROR-Singleton");
				}

			});

		});
		executor.awaitTermination(100, TimeUnit.MILLISECONDS);
		executor.shutdownNow();
		if (ThreadSafeSingletonByLock.getFactoryInsance().xyz == 1) {
			System.out
					.println("SUCCESS-singleton-by-Lock with 100 threads testing");
		}
	}

}
