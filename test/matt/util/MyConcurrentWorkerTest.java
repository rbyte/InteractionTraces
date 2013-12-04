package matt.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;

import matt.util.MyConcurrentWorker;
import matt.util.ProgressBar;
import matt.util.MyConcurrentWorker.WorkerTemplate;

import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Arrays;

public class MyConcurrentWorkerTest {
	
	private static class WorkunitPrivateStaticStresstest extends WorkerTemplate<Integer> {
		public void run() {
			int i = (int) (Integer.MAX_VALUE*0.98d);
			while (i++>0) {}
		}
	}
	
	public static class WorkunitPublicStatic extends WorkerTemplate<Integer> {
		@Override public void run() {
			val += 10;
		}
	}
	
	public class WorkunitPublicInner extends WorkerTemplate<Integer> {
		@Override public void run() {
			val++;
		}
	}
	
	private class WorkunitPrivateInner extends WorkerTemplate<Integer> {
		@Override public void run() {
			val++;
		}
	}
	
	private static class WorkunitPrivateStatic extends WorkerTemplate<Integer> {
		@Override
		public void run() {
			while (val++ < 10) {}
		}
	}
	
	public static class WorkunitPublicStaticRunnable implements Runnable {
		@Override
		public void run() {
			int i = 1;
			while (i++ < 10) {}
		}
	}
    
	@Test
	public void test1() throws IOException {
		int numberOfWorkunits = 40;
		HashSet<WorkunitPrivateStaticStresstest> ding = new HashSet<WorkunitPrivateStaticStresstest>(numberOfWorkunits);
		for (int i=0; i<numberOfWorkunits; i++)
			ding.add(new WorkunitPrivateStaticStresstest());
		try {
			ProgressBar fast = new ProgressBar("multi threaded");
			new MyConcurrentWorker(ding).run();
			fast.done();
			
			ProgressBar slow = new ProgressBar("single threaded");
			new MyConcurrentWorker(ding, 1, null).run();
			slow.done();
			
			int numberOfThreads = Runtime.getRuntime().availableProcessors();
			
			System.out.println("Running "+numberOfWorkunits+" workunits concurrently.\n"
				+"Time needed compared to singlethreaded mode:\n"+(double) fast.getTimePassed()/(double) slow.getTimePassed()+"%"
				+"\nIdeal (for "+numberOfThreads+ " threads):\n"+1d/(double) numberOfThreads+"%");
			if (numberOfThreads > 1) {
				assertTrue(fast.getTimePassed() < slow.getTimePassed());
			}
		} catch (InterruptedException e) {
			fail();
		}
		
		// Runtime.getRuntime().availableProcessors()
	}
	
	@Test
	public void test2() throws IOException, InterruptedException {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.add(1); arr.add(2); arr.add(3); arr.add(4);
		
		ArrayList<WorkunitPublicStatic> workerArr = new ArrayList<WorkunitPublicStatic>();
		for (Integer i : arr) {
			WorkunitPublicStatic worker = new WorkunitPublicStatic();
			worker.set(i);
			workerArr.add(worker);
		}
		new MyConcurrentWorker<Integer>(workerArr).run();
		for (WorkunitPublicStatic worker : workerArr)
			System.out.println(worker.get());
	}
	
	@Test
	public void test3() throws InstantiationException, IllegalAccessException, InterruptedException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.add(1); arr.add(2); arr.add(3); arr.add(4);
		
		ArrayList<Integer> results = new MyConcurrentWorker<Integer>(arr, WorkunitPublicStatic.class).run().getResults();
		
		for (Integer i : results) {
			System.out.println(i);
		}
	}
	
	@Test(expected = ClassCastException.class)
	public void test4() throws InstantiationException, IllegalAccessException, InterruptedException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.add(1); arr.add(2); arr.add(3); arr.add(4);
		new MyConcurrentWorker(arr, WorkunitPublicStaticRunnable.class).run();
	}
	
	@Test(expected = IllegalAccessException.class)
	public void test5() throws InstantiationException, IllegalAccessException, InterruptedException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.add(1); arr.add(2); arr.add(3); arr.add(4);
		new MyConcurrentWorker<Integer>(arr, WorkunitPrivateStatic.class).run();
	}
	
	@Test(expected = NoSuchMethodException.class)
	public void test6() throws InstantiationException, IllegalAccessException, InterruptedException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.add(1); arr.add(2); arr.add(3); arr.add(4);
		new MyConcurrentWorker<Integer>(arr, WorkunitPublicInner.class).run();
	}
	
	@Test
	public void test7() throws InstantiationException, IllegalAccessException, InterruptedException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.add(1); arr.add(2); arr.add(3); arr.add(4);
		Integer[] expectedResults = {11, 12, 13, 14};
		Integer[] expectedResults2 = {21, 22, 23, 24};
		
		MyConcurrentWorker<Integer> myCw = new MyConcurrentWorker<Integer>(arr, WorkunitPublicStatic.class);
		myCw.run();
		assertArrayEquals(myCw.getResults().toArray(), expectedResults);
		myCw.run();
		assertArrayEquals(myCw.getResults().toArray(), expectedResults2);
	}
	
	@Test
	public void test8() throws InstantiationException, IllegalAccessException, InterruptedException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.add(1); arr.add(2); arr.add(3); arr.add(4);
		Integer[] expectedResults = {2, 3, 4, 5};
		
		MyConcurrentWorker<Integer> myCw = new MyConcurrentWorker<Integer>(arr, WorkunitPublicInner.class, this);
		myCw.run();
		assertArrayEquals(myCw.getResults().toArray(), expectedResults);
	}
	
	@Test(expected = IllegalAccessException.class)
	public void test9() throws InstantiationException, IllegalAccessException, InterruptedException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		arr.add(1); arr.add(2); arr.add(3); arr.add(4);
		Integer[] expectedResults = {2, 3, 4, 5};
		
		MyConcurrentWorker<Integer> myCw = new MyConcurrentWorker<Integer>(arr, WorkunitPrivateInner.class, this);
		myCw.run();
		assertArrayEquals(myCw.getResults().toArray(), expectedResults);
	}
	
	//
	
}
