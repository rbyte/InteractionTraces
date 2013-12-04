package matt.util;

import org.junit.Before;
import org.junit.Test;

public class ProgressBarTest {
	
	@Before
	public void first() {
		System.out.println("\n");
	}

	@Test
	public void test1() {
		int totalTicks = 1000;
		ProgressBar progressBar = new ProgressBar(totalTicks);
		for (int i=0; i<totalTicks; i++) {
			progressBar.progress();
		}
		progressBar.done();
	}
	
	@Test
	public void test2() {
		ProgressBar progressBar = new ProgressBar(true);
		for (int i=0; i<50; i++) {
			progressBar.progress();
		}
		progressBar.done();
	}
	
	@Test
	public void test3() {
		ProgressBar progressBar = new ProgressBar(false);
		for (int i=0; i<50; i++) {
			progressBar.progress();
		}
		progressBar.done();
	}
	
	@Test
	public void test4() {
		ProgressBar progressBar = new ProgressBar(true);
		for (int i=0; i<200; i++) {
			progressBar.progress();
		}
		progressBar.done();
	}
	
	@Test
	public void test5() {
		int totalTicks = 1000;
		ProgressBar progressBar = new ProgressBar(totalTicks);
		for (int i=0; i<totalTicks+10; i++) {
			progressBar.progress();
		}
		progressBar.done();
	}
	
	@Test
	public void test6() {
		int totalTicks = 1000;
		ProgressBar progressBar = new ProgressBar(totalTicks);
		for (int i=0; i<totalTicks-100; i++) {
			progressBar.progress();
		}
		progressBar.done();
		progressBar.done();
	}
	
	@Test
	public void test7() {
		int totalTicks = 10000;
		ProgressBar progressBar = new ProgressBar("Main", totalTicks);
		for (int i=0; i<totalTicks; i++) {
			progressBar.progress();
		}
		progressBar.done();
	}
	
	@Test
	public void test8() {
		ProgressBar progressBar = new ProgressBar("Main", false);
		progressBar.done();
	}
	
	@Test
	public void test9() {
		int totalTicks = 8;
		ProgressBar progressBar = new ProgressBar("Main", totalTicks);
		for (int i=0; i<totalTicks; i++) {
			progressBar.progress();
		}
		progressBar.done();
	}
}
