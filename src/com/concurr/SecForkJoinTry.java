/* RecursiveAction works */

package com.concurr;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

class SecMyRecurAction extends RecursiveAction {

	int begin;
	int end;
	List<Integer> list;
	
	@Override
	public String toString() {
		return "SecMyRecurAction begin=" + begin + " :end=" + end + "  [size=" + (end - begin) + "]";
	}

	// public SecMyRecurAction() { }

	public SecMyRecurAction(List<Integer> ls, int b, int e) {
		
		this.list = ls;
		this.begin = b;
		this.end = e;
	}

	@Override
	protected void compute() {

		if ((this.end - this.begin) > 8) {
			List<SecMyRecurAction> ls; 
			ls = divHalf(this);
			for (SecMyRecurAction subtask : ls) {
				// System.out.println("forking sec");
				subtask.fork();

			}

		} else {
			//System.out.println("crunching sec . . ." + (end - begin));
			//list.forEach(x -> System.out.printf("%d^", x));
			List<Integer> subList = this.list.subList(begin, end); //end exclusive. sublist no new operation
			int sum = subList.stream().reduce(0, (x,y) -> x+y);
			System.out.println("sum=" + sum ); //+ " size=" + subList.size()  );
			//System.out.println("reduce-sum=" + list.stream().reduce(0, (x, y) -> x + y));

		}

	}// compute

	// input 1-task output 2-task
	private  List<SecMyRecurAction> divHalf(SecMyRecurAction l) {
		//System.out.println("input-div=" + l);
		List<SecMyRecurAction> ls = new ArrayList<>(2); //list of 2-task
		int midindex = l.begin + (l.end - l.begin)/2;
		
		
		// 1st list=(0, midindex-1) 2nd list=(midindex, size-1)
	
		ls.add(new SecMyRecurAction(l.list, l.begin, midindex));
		ls.add(new SecMyRecurAction(l.list, midindex, l.end));
		//System.out.println("output-div=" + ls);
		return ls;
	}

}

public class SecForkJoinTry {
	public static void main(String[] args) throws InterruptedException {
		System.out.println("3kkk");
		List<Integer> ls = new ArrayList<>(256);
		IntStream.range(1, 257).forEach(ls::add); // 257 exclusive
		//IntStream.range(1, 257).forEach(x->ls.add(1)); // 257 exclusive

	
		//ls.stream().forEach(x -> System.out.printf("%d,", x));

		int sum = ls.stream().reduce(0, (x, y) -> x + y);
		System.out.println("\n1sum=" + sum);
	/*
		System.out.println("2sum="
				+ ls.stream().mapToInt(i -> i.intValue()).sum());
		System.out.println("3sum="
				+ ls.stream().mapToInt(Integer::intValue).sum());
		System.out.println("3sum=" + ls.stream().reduce(0, (x, y) -> x + y));
	*/
		//ForkJoinPool fjp = new ForkJoinPool(7);
		ForkJoinPool fjp = ForkJoinPool.commonPool();
		System.out.println(fjp.getCommonPoolParallelism()  + " " + fjp.getParallelism() + "<>" + fjp.getPoolSize());
		//fjp.invoke(new SecMyRecurAction(ls, 0, ls.size()));
		fjp.submit(new SecMyRecurAction(ls, 0, ls.size()));
		fjp.awaitTermination(1, TimeUnit.SECONDS);
		fjp.shutdownNow();

	}

}
