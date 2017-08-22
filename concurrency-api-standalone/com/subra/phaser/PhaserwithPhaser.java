package com.subra.phaser;

import java.util.concurrent.Phaser;

public class PhaserwithPhaser {
	public static void main(String[] args)  {
		System.out.println("main-5");
		Phaser phaser = new Phaser(1);
		
		int curPhase;
		
		curPhase = phaser.getPhase();

		new AllThreadPiece("A", Phase.First, phaser);
		new AllThreadPiece("B", Phase.First, phaser);
		new AllThreadPiece("C", Phase.First, phaser);
		curPhase = phaser.getPhase();//this call make wait for all phases
		phaser.arriveAndAwaitAdvance();
		System.out.println("1<<<<<<<curPhase=" + curPhase);
		
		new AllThreadPiece("A", Phase.Second, phaser);
		new AllThreadPiece("B", Phase.Second, phaser);
		new AllThreadPiece("C", Phase.Second, phaser);
		curPhase = phaser.getPhase();//this call make wait for all phases
	
		System.out.println("2<<<<<<<curPhase=" + phaser.getPhase());
		phaser.arriveAndAwaitAdvance();
		
		new AllThreadPiece("A", Phase.Third, phaser);
		new AllThreadPiece("B", Phase.Third, phaser);
		new AllThreadPiece("C", Phase.Third, phaser);
		phaser.getPhase();
		System.out.println("3<<<<<<<curPhase=" + curPhase);
		phaser.arriveAndAwaitAdvance();
		
	}//main()
	public static void delay(){
		for (int i = 0; i < 1; i++) {
			for (int j = 0; j < 999999999; j++) {
			}
		}
	   }//delay()

}//class ends

//-----------------------------------------
class AllThreadPiece implements Runnable {

	String threadName;
	Phase whatPhase;
	Phaser phaser;
	
	public AllThreadPiece(String threadName, Phase whatPhase, Phaser phaser) {
		super();
		this.threadName = threadName;
		this.whatPhase = whatPhase;
		this.phaser = phaser;
		new Thread(this, threadName).start();
	
	}

	@Override
	public void run() {

		pickSpecificwork(whatPhase, threadName);
	}

	
	private void pickSpecificwork(Phase phase,  String thread_name){
		phaser.arriveAndAwaitAdvance();
		switch (phase.getVal()){
		case 1:
			phase1works(thread_name);
			break;
		case 2: 
			phase2works(thread_name);
			break;
		case 3:
			phase3works(thread_name);
			break;			
		}
		
	}

	private void phase1works( String threadname){
		if(threadname.equals("A")) {
			System.out.println("Phase1-" + threadname + " starting..");
			PhaserwithPhaser.delay();
			System.out.println("Phase1-" + threadname + " ends.");
		}else if(threadname.equals("B")){
			System.out.println("Phase1-" + threadname + " starting..");
			PhaserwithPhaser.delay();
			System.out.println("Phase1-" + threadname + " ends.");
		}else if(threadname.equals("C")){
			System.out.println("Phase1-" + threadname + " starting..");
			PhaserwithPhaser.delay();
			System.out.println("Phase1-" + threadname + " ends.");
		}
		
	}
	private void phase2works( String threadname){
		if(threadname.equals("A")) {
			System.out.println("Phase2-" + threadname + " starting..");
			PhaserwithPhaser.delay();
			System.out.println("Phase2-" + threadname + " ends.");
		}else if(threadname.equals("B")){
			System.out.println("Phase2-" + threadname + " starting..");
			PhaserwithPhaser.delay();
			System.out.println("Phase2-" + threadname + " ends.");
		}else if(threadname.equals("C")){
			System.out.println("Phase2-" + threadname + " starting..");
			PhaserwithPhaser.delay();
			System.out.println("Phase2-" + threadname + " ends.");
		}
		
	}
	private void phase3works( String threadname){
		if(threadname.equals("A")) {
			System.out.println("Phase3-" + threadname + " starting..");
			PhaserwithPhaser.delay();
			System.out.println("Phase3-" + threadname + " ends.");
		}else if(threadname.equals("B")){
			System.out.println("Phase3-" + threadname + " starting..");
			PhaserwithPhaser.delay();
			System.out.println("Phase3-" + threadname + " ends.");
		}else if(threadname.equals("C")){
			System.out.println("Phase3-" + threadname + " starting..");
			PhaserwithPhaser.delay();
			System.out.println("Phase3-" + threadname + " ends.");
		}
		
	}
	
	
}

//------------------------------
enum Phase{
	First(1), Second(2), Third(3), Fourth(4);
	int val;
	
	Phase(int x){
		this.val=x;
	}
	int getVal(){
		return val;
	}
}
//-----------------------------
