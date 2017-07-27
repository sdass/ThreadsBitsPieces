package com.subra.waitnotify;

//------------ common hold and shared data structure
class Common {
	String holder = "";
}

public class FindSynch {
	public static void main(String[] args) throws InterruptedException {

		Common cHolder = new Common();// create common.

		MyRunnable r1 = new MyRunnable(1, cHolder);
		MyRunnable r2 = new MyRunnable(2, cHolder);
		MyRunnable r3 = new MyRunnable(3, cHolder);

		Thread t1 = new Thread(r1); 
		Thread t2 = new Thread(r2);
		Thread t3 = new Thread(r3);

		t1.start();	t2.start();	t3.start();
		t1.join();
		System.out.println("finally=" + cHolder.holder);
		t2.join();
		System.out.println("finally=" + cHolder.holder);
		t3.join();
		System.out.println("finally=" + cHolder.holder + " count=" + cHolder.holder.length());

	}

}// FindSynch ends

class MyRunnable implements Runnable {

	int val;
	Common commonHolder;

	// All MyRunnable obj share commonHolder (ref) when passed. but val own copy 
	public MyRunnable(int v, Common c) {
		val = v;
		commonHolder = c;
	}

	@Override
	public void run() {

		System.out.println("locking on-> " + commonHolder);
		synchronized (commonHolder) {

			for (int i = 0; i < 4; i++) {
				commonHolder.holder = commonHolder.holder + val;
				// delay
				for (int j = 0; j < 999999999; j++) {
					for (int k = 0; k < 99999999; k++) {

					}
				}
			}
			System.out.println("entered. . . " + commonHolder.holder);

		}
	}
}//MyRunnable

/* output 
locking on-> com.subra.waitnotify.Common@197de6c9
locking on-> com.subra.waitnotify.Common@197de6c9
locking on-> com.subra.waitnotify.Common@197de6c9
entered. . . 2222
entered. . . 22223333
entered. . . 222233331111
finally=222233331111
finally=222233331111
finally=222233331111 count=12 

 */

