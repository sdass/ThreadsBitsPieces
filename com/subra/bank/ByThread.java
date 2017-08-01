package com.subra.bank;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class BlueLedgerRecord {
	double amount;
	List<TransRecord> transHistory = new ArrayList<TransRecord>();
	
	@Override
	public String toString() {
		return "BlueLedgerRecord [amount=" + amount + ", transHistory="
				+ transHistory + "]";
	}
	
}

class TransRecord {
	Double depoOrDraw;
	char transtype; //['+' | '-']
	Date date; // date of transaction
	public TransRecord(Double depoOrDraw, char transtype, Date date) {
		super();
		this.depoOrDraw = depoOrDraw;
		this.transtype = transtype;
		this.date = date;
	}
	@Override
	public String toString() {
		return "TransRecord [depoOrDraw=" + depoOrDraw + ", transtype="
				+ transtype + ", date=" + date + "]";
	}
	
}
// above account creation related


class DepositJob implements Runnable{
	BlueLedgerRecord aBankAccount;
	double amount;
	
	public DepositJob(BlueLedgerRecord aBankAccount, double amount) {
		super();
		this.aBankAccount = aBankAccount;
		this.amount = amount;
	}
	
	@Override
	public void run() {
		synchronized (aBankAccount) {
		aBankAccount.amount = aBankAccount.amount + amount;
		aBankAccount.transHistory.add(new TransRecord(this.amount, '+', new Date()));
		System.out.println("2. bankrecord=" + aBankAccount);
		aBankAccount.notify();
		
		}
	}
	
}
//--------------------------
class WithDrawJob implements Runnable{
	BlueLedgerRecord aBankAccount;
	double amount;
	
	public WithDrawJob(BlueLedgerRecord aBankAccount, double amount) {
		this.aBankAccount = aBankAccount;
		this.amount = amount;
	}
	
	@Override
	public void run() {
		synchronized (aBankAccount) {
			if(aBankAccount.amount < amount){
				System.out.println("Alert!!!!!!! balance -ve NOT ALLOWED");
				try {
					aBankAccount.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		aBankAccount.amount = aBankAccount.amount - amount;
		aBankAccount.transHistory.add(new TransRecord(this.amount, '-', new Date()));
		System.out.println("9. bankrecord=" + aBankAccount);
		
		}
	}
	
}


public class ByThread {

	public static void main(String [] args) throws InterruptedException{
		System.out.println("test3");
		
		//create bank account. main-thread does
		BlueLedgerRecord aBankAccount = new BlueLedgerRecord();//pass this acct to all jobs
		synchronized(aBankAccount){
			double amount = 10.0;
			aBankAccount.amount = aBankAccount.amount + amount;
			aBankAccount.transHistory.add(new TransRecord(amount, '+', new Date()));
		}
		//----------------initialized above----------
		
		System.out.println("1. bankrecord=" + aBankAccount);
		
		DepositJob deposit_1 = new DepositJob(aBankAccount, 100.0);
		
		WithDrawJob draw_1 = new WithDrawJob(aBankAccount, 20);
			
		
		Thread t1 = new Thread(deposit_1);
		Thread m1 = new Thread(draw_1);
		Thread t2 = new Thread(new DepositJob(aBankAccount, 300.0));
		Thread m2 = new Thread(new WithDrawJob(aBankAccount, 300.0));
		
		Thread t3 = new Thread(new DepositJob(aBankAccount, 40.0));
		t1.start(); m1.start(); m2.start();
		t2.start();t3.start();
		//t1.join()
		//System.out.println("2. bankrecord=" + aBankAccount); //ConcurrentModificationException
		/*
		for (TransRecord tr: aBankAccount.transHistory){
			System.out.println(tr);
		}
		*/
		t1.join(); 
		t2.join();
		t3.join();
		m1.join(); m2.join();
		for (TransRecord tr: aBankAccount.transHistory){
			System.out.println(tr);
		}
		System.out.println("2end. bankrecord=" + aBankAccount); //ConcurrentModificationException
		
		
	}
	
}
