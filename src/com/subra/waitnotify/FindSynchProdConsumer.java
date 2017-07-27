package com.subra.waitnotify;

public class FindSynchProdConsumer {
	public static void main(String[] args) throws InterruptedException {

		Common2 cHolder = new Common2();// create common2.

		MyRunnableProducer2 r1 = new MyRunnableProducer2(1, cHolder);
		MyRunnableProducer2 r2 = new MyRunnableProducer2(2, cHolder);
		MyRunnableProducer2 r3 = new MyRunnableProducer2(3, cHolder);

		//below 3 are producer thread produces "1111", "2222" etc. 
		Thread t1 = new Thread(r1); 
		Thread t2 = new Thread(r2);
		Thread t3 = new Thread(r3);
		
		//below 1 is consumer thread
		ConsumerRunnable cr1 = new ConsumerRunnable(cHolder);
		 Thread tc1 = new Thread(cr1);
		 tc1.start();

		t1.start();	t2.start();	t3.start();
		
		t1.join();	t2.join();	t3.join();
		tc1.join();
		System.out.println("finally=" + cHolder.holder + " count: " + cHolder.holder.length());
	}

}// FindSynchProdConsumer ends
//--------------------------------------------------
class MyRunnableProducer2 implements Runnable {
	int val;
	Common2 commonHolder;

	// All MyRunnable obj share commonHolder (ref) when passed. but val own copy 
	public MyRunnableProducer2(int v, Common2 c) {
		val = v;
		commonHolder = c;
	}

	@Override
	public void run() {

		System.out.println("producer locking on-> " + commonHolder);
		synchronized (commonHolder) {
			while(commonHolder.holder.length() != 0){ // I am producer
				try {
					// giveup monitor for consumer
					commonHolder.wait();
				} catch (InterruptedException e) {

					e.printStackTrace();
				} 
			}
			for (int i = 0; i < 4; i++) {
				commonHolder.holder = commonHolder.holder + val;
				// delay
				for (int j = 0; j < 999999999; j++) {
					for (int k = 0; k < 99999999; k++) {
					}
				}
			}
			commonHolder.notifyAll(); //change position of it
			System.out.println("<<<produced. . . " + commonHolder.holder);
		}
		
	}
}//MyRunnable2
//---------------------------------------
/* to create consumer thread will consume "1111",...  and set the holder to "" another Runnable type. Then notify other
 producer thread will use precondition strlen to check 0 else release monitor
 Later  modify to Linked List.
 */

class ConsumerRunnable implements Runnable {

	Common2 pipeline;

	public ConsumerRunnable(Common2 p) {
		pipeline = p;
	}

	@Override
	public void run() {
		System.out.println("Consumer entered...");

		for(int i =0; i < 3; i++){
			System.out.println("consumer for:" + i);
			System.out.println("consumer locking on-> " + pipeline);
			synchronized (pipeline) { //pipeline is same ref obj as commonHolder. see main() construct
	
				while (pipeline.holder.length() != 4) {
					// giveup the monitor for 1(3) producers to populate
					try {
						pipeline.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
	
				String prodString = pipeline.holder;
				pipeline.holder = "";
				// notify here for producer to create new 2222. then print
				pipeline.notifyAll();
				System.out.println(">>>consumed: " + prodString);

			}//synch
		}//for 3 times to consume
	}
} // ConsumerRunnable

//------------ common hold and shared data structure. monitor
class Common2 {
	String holder = "";
}


/* output 
Consumer entered...
consumer for:0
<<<produced. . . 1111
>>>consumed: 1111
consumer for:1
<<<produced. . . 3333
>>>consumed: 3333
consumer for:2
<<<produced. . . 2222
>>>consumed: 2222
finally= count: 0

*/

