package com.lock;

/* with AtomicInteger lock/monitor-free 2consumer-2producer by Executorservice
 * and another set of 2-consumer-2producer by hand-manipulated thread
 * mixmatching Executorservice thread and hand-created thread is safe.
 * java-8 implementation with lamda.
 * challenge: invokeAll() with two list of Callable hanged. Not used
 */
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;


class Shared2 {
	final int[] arrqueue = new int[1];
}

public class ProdConsumerExecutorAtomic {
	final static AtomicInteger atomInt = new AtomicInteger(0); //0 for empty
	public static void main(String[] args) throws InterruptedException{
		System.out.println("origin=" + atomInt.get());
		//switch position between  these two statements ok. Only output differs
		byExecutorServicefrmwrk();	byHandThread();
		//byHandThread(); byExecutorServicefrmwrk();	

		
	}

	public static void byExecutorServicefrmwrk() throws InterruptedException{
		System.out.println("----<><><><>---byExecutorServicefrmwrk----<><><><>---");
		Shared2 shc = new Shared2();
		ExecutorService executor = Executors.newFixedThreadPool(4);

		//create all afresh. no reuse ref
		executor.submit(consumerTask(shc.arrqueue));
		executor.submit(producerTask(shc.arrqueue));
		executor.submit(consumerTask(shc.arrqueue));
		executor.submit(producerTask(shc.arrqueue));
		
		executor.awaitTermination(1, TimeUnit.SECONDS);
		executor.shutdownNow();
		
		
	}	
	public static void byHandThread(){
		System.out.println("----<><><><>---byHandThread----<><><><>---");
		Shared2 shc = new Shared2();
		Runnable prodTask = producerTask(shc.arrqueue);
		Callable<Integer> callTask = consumerTask(shc.arrqueue);
		FutureTask<Integer> consumerTask = new FutureTask<>(callTask);
		
		new Thread(consumerTask).start();
		new Thread( new FutureTask<Integer>(consumerTask(shc.arrqueue))).start(); //critical get a new Runnable no old ref
		new Thread(prodTask).start(); 
		new Thread(producerTask(shc.arrqueue)).start(); //critical get a new Runnable no old ref


		
	}
		
	public static Runnable producerTask(int[] arque){
		System.out.println("-----a new producer thread-----");
		return ()-> {
		Arrays.asList(10,20,30,40,50).forEach(
				
				x-> {
					while (!atomInt.compareAndSet(0, 2)){
						//loop until you see 0 and set to 2 [two -> so that you finish filling up queue
					}
					//set to 2 now. So fill up
					
				arque[0] = x; // 1 Int at a time so free atomInt below
				System.out.print(">>>producer=" + x);
				System.out.print( atomInt.compareAndSet(2, 1)); //set to 1 for consumer. if or no if enough				
				});
		};
				
	}

	public static Callable<Integer> consumerTask(int[] arque){
		System.out.println("-----a new consumer thread-----");
		return ()-> {
			IntStream.range(1, 6).forEach( x -> {
			while (!atomInt.compareAndSet(1, 3)){
				//loop until you see 1 and set to 3 [three -> so that you finish eating up produce
			}
			//now it is 3. So eat produce restfully
			int m = arque[0];
			System.out.println("<<consumer=" + m);
			atomInt.compareAndSet(3, 0); // 0-for producer. no need if|while-loop. This thread alone among n-threads
			});
			return 1;//consumed

		};
							
	}	

}

/* output
 
origin=0
----<><><><>---byExecutorServicefrmwrk----<><><><>---
-----a new consumer thread-----
-----a new producer thread-----
-----a new consumer thread-----
-----a new producer thread-----
>>>producer=10true<<consumer=10
>>>producer=20true<<consumer=20
>>>producer=30true<<consumer=30
>>>producer=40true<<consumer=40
>>>producer=50true<<consumer=50
>>>producer=10true<<consumer=10
>>>producer=20true<<consumer=20
>>>producer=30true<<consumer=30
>>>producer=40true<<consumer=40
>>>producer=50true<<consumer=50
----<><><><>---byHandThread----<><><><>---
-----a new producer thread-----
-----a new consumer thread-----
-----a new consumer thread-----
-----a new producer thread-----
>>>producer=10true<<consumer=10
>>>producer=20true<<consumer=20
>>>producer=10true<<consumer=10
>>>producer=30true<<consumer=30
>>>producer=40true<<consumer=40
>>>producer=50true<<consumer=50
>>>producer=20true<<consumer=20
>>>producer=30true<<consumer=30
>>>producer=40true<<consumer=40
>>>producer=50true<<consumer=50

 
*/