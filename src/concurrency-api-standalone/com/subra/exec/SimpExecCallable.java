package com.subra.exec;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SimpExecCallable {

	public static void main(String[] args) {
		
		ExecutorService eservice = Executors.newFixedThreadPool(2);
		CountDownLatch cdl = new CountDownLatch(7);

		eservice.submit(new WorkCall("Ax1", 'x', 4, cdl));
		eservice.submit(new WorkCall("B+1", '+' , 4, cdl));
		
		eservice.submit(new WorkCall("Ax2", 'x', 5, cdl));
		Future<MyFuture> futResult = eservice.submit(new WorkCall("B+2", '+', 5, cdl));
		try {
			MyFuture myfuture = futResult.get();
			ResultForFuture resultForFuture =  myfuture.get();
			System.out.println("<< one returned resultForFuture =" + resultForFuture);
		} catch (InterruptedException | ExecutionException e1) {
			e1.printStackTrace();
		}
		//ResultForFuture
		
		eservice.submit(new WorkCall("Ax3", 'x', 6, cdl));
		eservice.submit(new WorkCall("Bx3", '+', 6, cdl));

		cdl.countDown();
		try {
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("main thread exits last...");
		
		eservice.shutdown();

	}
	
	public static void delay(){
		// delay 
	  //for(int m=0; m<2; m++){
 		for (int j = 0; j < 999999999; j++) { 
		   for (int k = 0; k < 99999999; k++) { 
		   } 
		} 
	  //}
	} //delay()

}
//--------------------------------
	class WorkCall implements Callable<MyFuture> {

		String name;
		int data;
		char oper;
		CountDownLatch cdl;
		
		
		
		public WorkCall(String name, char oper, int uplimit, CountDownLatch cdl) {
			super();
			this.name = name;
			this.oper = oper;
			this.data = uplimit;
			this.cdl = cdl;

		}

		@Override
		public MyFuture call() throws Exception { //same code code for old run() method
			SimpleExecutor.delay();
			int total=0;
			int totalx=1;
			int finaltotal =0;
			int data1 = data;
			while (data1 > 0){
			if(oper == '+') {
				total = total + data1;
			}else if(oper == 'x'){
				totalx = totalx * data1;
			}
			data1--;
			SimpleExecutor.delay();
			}
			total = (oper == 'x')? totalx : total;
			
			
			MyFuture futureret = prepareAndGetFuture(total);
			System.out.println(name + " " + oper + " data=" +  data  + " result=" + total);
			
			cdl.countDown();
			
			return futureret;
		}
		
		private MyFuture prepareAndGetFuture(int total){
			ResultForFuture rslt = new ResultForFuture("desc-oper=" + oper, null, 0, total);
			MyFuture myFuture = new MyFuture(rslt);
			return myFuture;
		}
		
	}

//---------------------------------------

//class WorkCall implements Callable<V>

class MyFuture implements Future<ResultForFuture>{

	ResultForFuture resultAll;
	
	public MyFuture() {
		
	}
	
	
	public MyFuture(ResultForFuture resultAll) {
		super();
		this.resultAll = resultAll;
	}


	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		//????
		return false;
	}

	@Override
	public ResultForFuture get() throws InterruptedException, ExecutionException {
		
		return resultAll;
	}

	@Override
	public ResultForFuture get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		
		return resultAll;
	}

	@Override
	public boolean isCancelled() {
		//???
		return false;
	}

	@Override
	public boolean isDone() {
		//???
		return false;
	}
	
}

class ResultForFuture  {
	String desc;
	Exception e;
	int status;
	int retval;
	public ResultForFuture(String desc, Exception e, int status, int retval) {
		super();
		this.desc = desc;
		this.e = e;
		this.status = status;
		this.retval = retval;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Exception getE() {
		return e;
	}
	public void setE(Exception e) {
		this.e = e;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getRetval() {
		return retval;
	}
	public void setRetval(int retval) {
		this.retval = retval;
	}
	@Override
	public String toString() {
		return "ResultForFuture [desc=" + desc + ", e=" + e + ", status=" + status + ", retval=" + retval + "]";
	}
	
	
	
	
}