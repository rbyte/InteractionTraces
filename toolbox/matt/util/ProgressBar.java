package matt.util;

import matt.meta.AuthorInformation;

import org.contract4j5.contract.*;
import static org.junit.Assert.*;

/**
 * Shows program execution progress in the console.
 */
@AuthorInformation
@Contract
public class ProgressBar {
	
	private static final int BAR_LENGTH = 80;
	private static final char TICK_DEFAULT = '>';
	
	@Invar("$this.totalTicks >= 0")
	private int totalTicks = 0;
	@Invar("$this.currentTick >= 0")
	private int currentTick = 0;
	@Invar("$this.ticksPrinted >= 0")
	private int ticksPrinted = 0;
	private boolean done = false;
	@Invar("$this.startTime <= System.currentTimeMillis()")
	private long startTime = 0;
	@Invar("$this.startTime <= $this.endTime")
	private long endTime = Long.MAX_VALUE;
	private boolean showProgress = true;
	private String appName = "";
	
	public ProgressBar(int totalTicks) {
		this("", totalTicks, true);
	}
	
	public ProgressBar(String appName) {
		this(appName, 0, false);
	}
	
	public ProgressBar(String appName, int totalTicks) {
		this(appName, totalTicks, true);
	}
	
	public ProgressBar(String appName, boolean showProgress) {
		this(appName, 0, showProgress);
	}
	
	public ProgressBar(boolean showProgress) {
		this("", 0, showProgress);
	}
	
	@Pre("totalTicks >= 0 && appName != null && showProgress != null")
	public ProgressBar(String appName, int totalTicks, boolean showProgress) {
		this.showProgress = showProgress;
		this.totalTicks = totalTicks;
		this.appName = appName;
		startTime = System.currentTimeMillis();
		initMsg();
	}
	
	public void reset() {
		startTime = System.currentTimeMillis();
		totalTicks = 0;
		currentTick = 0;
		ticksPrinted = 0;
		done = false;
		endTime = Long.MAX_VALUE;
		initMsg();
	}
	
	private void initMsg() {
		System.out.println(appName.length() > 0 ? appName : "Running ...");
		if (this.showProgress && this.totalTicks > 0) {
			System.out.println(" "+StringHandling.concat("_", BAR_LENGTH));
			System.out.print("|");
		}
	}
	
	public int getReportedProgressTicks() {
		return currentTick;
	}
	
	public long getTimePassed() {
		return done ? endTime-startTime : System.currentTimeMillis()-startTime;
	}
	
	public synchronized void progress() {
		progress(TICK_DEFAULT);
	}
	
	public synchronized void progress(char tick) {
		if (showProgress) {
			currentTick++;
			if (totalTicks == 0) {
				if (currentTick % BAR_LENGTH == BAR_LENGTH-1) {
					System.out.println(tick);
				} else {
					System.out.print(tick);
				}
			} else {
				if (currentTick > totalTicks) {
					// overshoot!
					System.err.print(TICK_DEFAULT);
				} else {
					assertTrue(ticksPrinted <= BAR_LENGTH);
					assertTrue(!done);
					while ((float) ticksPrinted/(float) BAR_LENGTH < (float) currentTick/(float) totalTicks) {
						System.out.print(tick);
						ticksPrinted++;
					}
				}
			}
		}
	}
	
	public synchronized void done() {
		if (!done) {
			done = true;
			endTime = System.currentTimeMillis();
			if (totalTicks != 0 && currentTick < totalTicks) {
				System.err.print("| Early done.");
			} else {
				System.out.print("| Done.");
			}
			System.out.print(appName.length() > 0 ? " "+appName : "");
			System.out.println(" Took " + ((System.currentTimeMillis() - startTime)/1000f)+" secs.");
		} else {
			System.err.print("ProgressBar: done() called twice.");
		}
	}
	
}
