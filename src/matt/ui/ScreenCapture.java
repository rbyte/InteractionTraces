package matt.ui;

import java.awt.AWTException;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.imageio.ImageIO;

import matt.parameters.Params;
import matt.util.Util;
import static org.junit.Assert.*;

public class ScreenCapture {
	
	private File outDir;
	private long frame = 1;
	private boolean stopped = true;
	private PAppletPlus pp;
	private final static String extensionVideo = ".tif";
	private final static String extensionScreenshot = ".png";
	
	// manually convert video in given folder
	public static void main(String[] args) {
		final String path = "c:\\Matt\\workspace\\MattsProjectCalgary2011_12\\video\\App10\\";
		ScreenCapture sc = new ScreenCapture();
		sc.outDir = new File(path);
		assertTrue(sc.outDir.isDirectory());
		new Thread(sc.new CreateVideoFromScreenshotsWorkunit()).start();
	}
	
	private ScreenCapture() {}
	
	ScreenCapture(PAppletPlus pp) {
		this.pp = pp;
	}
	
	public boolean isStopped() {
		return stopped;
	}
	
	public void videoScreenCapture() {
		if (!isStopped()) {
			// String.format("%03d", frame)
			screenshot(new File(outDir.getAbsolutePath()+"/"+frame+extensionVideo));
			frame++;
		}
	}
	
	public void switchOnOff() {
		if (isStopped()) {
			start();
		} else {
			stop();
		}
	}
	
	public void start() {
		if (!isStopped()) {
			System.err.println("Previous capture has not been stopped before.");
			stop();
		}
		
		int i = 1;
		do {
			outDir = new File(Params.pathToVideoOut+(i++));
		} while (outDir.exists());
		
		if (!outDir.mkdir()) {
			System.err.println("Could not create video output directory.");
		} else {
			stopped = false;
		}
		
		System.out.println("Started video capture.");
	}
	
	public void stop() {
		if (isStopped()) {
			System.err.println("Already stopped.");
		} else {
			stopped = true;
			frame = 1;
			new Thread(new CreateVideoFromScreenshotsWorkunit()).start();
		}
	}
	
	private class CreateVideoFromScreenshotsWorkunit implements Runnable {
		@SuppressWarnings("unused")
		public void run() {
			// -qmin 50 an qmax 51 gives the lowest quality
			// -qmin 0 -qmax 1 gives the highest quality 
			
			// lessless codec: huffyuv!
			
			File outFile = new File(outDir.getPath()+"\\outfile.mp4");
			String command = Params.pathToFFmpeg_executable+" -i \""+outDir.getPath()
					+"\\%d"+extensionVideo+"\" -vcodec libx264 -qmax "+(int) ((1-Params.videoQuality)*51)+" \""+outFile.getPath()+"\"";
			
			System.out.println("Stopped video capture. Converting video using:");
			System.out.println(command);
			
			if (false) {
				Util.runProgram(command);
				if (outFile.exists()) {
					System.out.println("Done. Converting video.");
				} else {
					System.err.println("Converting video failed. No output file created.");
				}
			}
			
			// ffmpeg -i screencapture%d.jpg -vcodec mpeg4 outfile.avi
			// ffmpeg -i "c:\Matt\workspace\MattsProjectCalgary2011_12\Processing\v1\%d.png" -vcodec mpeg4 "c:\Matt\workspace\MattsProjectCalgary2011_12\Processing\v1\outfile.avi"
		}
	}
	
	public void screenshot() {
		String currentDate = new SimpleDateFormat("yy-MM-dd HH.mm.ss").format(new Date());
		File outputfile = new File(Params.pathToScreenshots+currentDate+extensionScreenshot);
		screenshot(outputfile);
		System.out.println("screenshot taken!");
	}
	
	public void screenshot(File outputfile) {
		System.out.println(outputfile.getParentFile().getFreeSpace()/1_000_000);
		while (outputfile.getParentFile().getFreeSpace() < 10_000_000) {
			boolean enableContinuedCapture = true;
			System.err.println("Out of disk space!");
			
			if (enableContinuedCapture)
				System.err.println("Relocate files! Done? Continue (y,n)?");
			
			Scanner user_input = new Scanner(System.in);
			if (user_input.next().equals("y") && enableContinuedCapture) {
				continue;
			} else {
				stop();
				return;
			}
		}
		
		if (Params.visualS.useFullscreenCapture()) {
			pp.save(outputfile.getPath());
		} else {
			Rectangle captureScreenRect = Params.visualS.captureScreenRect();
			new PImagePlus(pp.g.get(
					captureScreenRect.x,
					captureScreenRect.y,
					captureScreenRect.width,
					captureScreenRect.height), pp).save(outputfile.getPath());				
		}
	}
	
	@Deprecated
	public void screenshotOLD(File outputfile) {
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] gs = ge.getScreenDevices();
			DisplayMode mode = gs[1].getDisplayMode();
			Rectangle bounds = new Rectangle(0, 0, mode.getWidth(),mode.getHeight());
			BufferedImage desktop = new BufferedImage(mode.getWidth(), mode.getHeight(), BufferedImage.TYPE_INT_RGB);
			
			try {
				desktop = new Robot(gs[1]).createScreenCapture(bounds);
			} catch (AWTException e) {
				System.err.println("Screen capture failed.");
			}
			ImageIO.write(desktop, extensionVideo, outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
