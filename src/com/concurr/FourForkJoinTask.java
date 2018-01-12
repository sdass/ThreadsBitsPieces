package com.concurr;

/* RecursiveTask works. Returns Integer */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

class FourRecursiveTask extends RecursiveTask<Integer>{
	int begin;
	int end; // = 256;	
	List<Integer> list;
	
	public FourRecursiveTask(List<Integer> list, int begin, int end) {
		this.list = list;
		this.begin = begin;
		this.end = end;

	}

	@Override public String toString() { return "FourRecursiveTask [begin=" + begin + ", end=" + end + "]"; 	}
	
	@Override protected Integer compute() {
		if(this.end - this.begin > 8){
			//System.out.println("task=" +  this);
			List<FourRecursiveTask> ls;
			ls = divUp(this);
			for(FourRecursiveTask t: ls){
				t.fork();
			}

			Integer result= 0;
			for(FourRecursiveTask t : ls){
				result = result + t.join(); 
											
			}
			
			return result;
			
		}else{
			//System.out.println("crunching");
			int subsum = this.list.subList(begin, end).stream().reduce(0, (x, y)-> x+y );
			return subsum;
		}
		
	}
	
	private List<FourRecursiveTask> divUp(FourRecursiveTask t){
		int midindex = t.begin + (t.end - t.begin)/2;
		List<FourRecursiveTask> l = new ArrayList<FourRecursiveTask>(2);
		l.add(new FourRecursiveTask(t.list, t.begin, midindex )); l.add(new FourRecursiveTask(t.list, midindex, t.end));
		return l;
	}

	
}

public class FourForkJoinTask {

	public static void main(String[] arg) throws InterruptedException, ExecutionException{
		System.out.println("task");
		List<Integer> ls = new ArrayList<Integer>(256);
		//IntStream.range(1, 257).forEach(x->ls.add(1));
		IntStream.range(1, 257).forEach(x->ls.add(x));
		
		System.out.println("Before=" + ls.stream().reduce((x,y)->x+y).orElse(-1));
		//ForkJoinPool fjp = ForkJoinPool.commonPool();
		ForkJoinPool fjp = new ForkJoinPool(7);
		//Integer xyz = fjp.invoke(new FourRecursiveTask(ls, 0, ls.size()));
		Integer xyz = fjp.submit(new FourRecursiveTask(ls, 0, ls.size() )).get();
		//fjp.execute returns void
		System.out.println("Crunched:" + xyz);
		
		fjp.awaitTermination(200, TimeUnit.MICROSECONDS);
		fjp.shutdownNow();
	}
}
