package matt.ui.screenElement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import matt.parameters.Params;
import matt.util.StringHandling;
import matt.util.Util;
import static org.junit.Assert.*;

public class InteractionLog<T extends ScreenElement<? extends ScreenElementSet<T>>> extends LinkedList<TimestampedObject<T>> {
	
	private static final long serialVersionUID = 1713859708195045148L;
	
	public static class Combi<T> {
		public TimestampedObject<T> pre;
		public TimestampedObject<T> suc;
		Combi(TimestampedObject<T> pre, TimestampedObject<T> suc) {
			this.pre = pre;
			this.suc = suc;
		}
	}
	
	public ArrayList<Combi<T>> getCombiList() {
		ArrayList<Combi<T>> result = new ArrayList<Combi<T>>();
		TimestampedObject<T> pre = null;
		for (TimestampedObject<T> t : this) {
			if (pre != null)
				result.add(new Combi<T>(pre, t));
			pre = t;
		}
		return result;
	}
	
	public boolean addV(T v) {
		return addV(new TimestampedObject<T>(v, size()));
	}
	
	private boolean addV(T v, long timestamp) {
		return addV(new TimestampedObject<T>(v, size(), timestamp));
	}
	
	@SuppressWarnings("unused")
	private boolean addV(TimestampedObject<T> t) {
		boolean addR = add(t);
		if (Params.limitLogSize && size() > Params.maxLogSize)
			removeABunch(1);
		return addR;
	}
	
	public void removeABunch(int amount) {
		assertTrue(amount >= 1);
		int stillToKill = amount;
		while (stillToKill-- > 0 && !isEmpty())
			remove();
		for (TimestampedObject<T> tt : this)
			tt.counter -= amount-stillToKill;
	}
	
	public boolean containsV(T v) {
		for (TimestampedObject<T> o : this) {
			if (o.v == v)
				return true;
		}
		return false;
	}
	
	public TimestampedObject<T> getMostRecent(T v) {
		Iterator<TimestampedObject<T>> iter = this.descendingIterator();
		while (iter.hasNext()) {
			TimestampedObject<T> o = iter.next();
			if (o.v == v)
				return o;
		}
//		for (TimestampedObject<T> o : this)
//			if (o.v == v)
//				return o;
		return null;
	}
	
	public TimestampedObject<T> getMostCurrentV(T v) {
		Iterator<TimestampedObject<T>> i = descendingIterator();
		while (i.hasNext()) {
			TimestampedObject<T> t = i.next();
			if (t.v == v)
				return t;
		}
		return null;
	}
	
	
	
	public TimestampedObject<T> getMostCurrentVbutNotBeyondCutoff(T v, int cutOff) {
		Iterator<TimestampedObject<T>> i = descendingIterator();
		int position = 0;
		while (i.hasNext() && position++ <= cutOff) {
			TimestampedObject<T> t = i.next();
			if (t.v == v)
				return t;
		}
		return null;
	}
	
	/**
	 * latest: 1 = 100%
	 * @param v
	 * @param cutOff
	 * @return in [0, 1]
	 */
	public float getPercentisedPosition(T v, int cutOff) {
		int position = 0;
		Iterator<TimestampedObject<T>> i = descendingIterator();
		while (i.hasNext() && position <= cutOff) {
			position++;
			if (i.next().v == v)
				break;
		}
		return 1-(float) Util.percentiseIn(position, cutOff);
	}
	
	public boolean removeV(T v) {
		for (TimestampedObject<T> o : this) {
			if (o.v == v)
				return remove(v);
		}
		return false;
	}
	
	
	private static final String delimLevel1 = "---";
	private static final String delimLevel2 = "<>";
	
	public String toString() {
		String result = "";
		for (TimestampedObject<T> t : this) {
			result += t.toString(delimLevel2)+ (!t.equals(getLast()) ? delimLevel1 : "");
		}
		return result;
	}
	
	public File saveLog(String append) {
		String currentDate = new SimpleDateFormat("yy-MM-dd HH.mm.ss").format(new Date());
		File file = new File(Params.pathToInteractionTraces
			+getClass().getSimpleName()+"."+append+"_"+currentDate+".txt");
		
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
			bufferedWriter.write(toString());
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Saved log (size: "+size()+") to: "+file);
		return file;
	}
	
	public void clearLog() {
		while(!isEmpty()) remove();
		System.out.println("log cleared");
	}
	
	public void loadLog(ScreenElementSet<T> kwds, String append, boolean lastOnly, boolean shiftLogToNow) throws IOException {
		File[] files = Util.getAllFilesSortedByModificationDate(Params.pathToInteractionTraces,
				getClass().getSimpleName()+"."+append, ".txt");
		if (files.length >= 1) {
			if (lastOnly) {
				loadLog(files[files.length-1], kwds);
			} else {
				for (File file : files)
					loadLog(file, kwds);
			}
			sortLogByTimestamps();
			if (shiftLogToNow)
				shiftLogToNow();
		}
	}
	
	private File loadLog(File file, ScreenElementSet<T> ts) throws IOException {
		String str = StringHandling.readFileAsString(file);
		for (String oneEntry : str.split(delimLevel1)) {
			if (!oneEntry.equals("")) {
				String[] elems = oneEntry.split(delimLevel2);
				if (elems.length == 2) {
					addV(ts.get(elems[0]), Long.parseLong(elems[1]));
				} else {
					System.err.println("could not be split into two: "+oneEntry);
				}
			}
		}
		System.out.println("loaded log (size: "+size()+"): "+file);
		return file;
	}
	
	public void shiftLogToNow() {
		if (!isEmpty()) {
			long deltaTilNow = getLast().getTimeDeltaTilNow();
			for (TimestampedObject<T> t : this) {
				t.timestamp += deltaTilNow;
			}			
		}
	}
	
	public void shiftLogTime(long ticks) {
		if (!isEmpty()) {
			for (TimestampedObject<T> t : this) {
				t.timestamp += ticks;
				assertTrue(t.timestamp <= System.currentTimeMillis());
			}
		}
	}
	
	public TimestampedObject<T> getSecondLast() {
		return getFromTail(1);
	}
	
	public TimestampedObject<T> getFromTail(int i) {
		assertTrue(i >= 0);
		if (size() >= i+1) {
			return get(size()-(i+1));
		} else {
			throw new NoSuchElementException();
		}
	}
	
	public boolean aBookIsSelected() {
		boolean aBookIsSelected = false;
		int current = 0;
		try {
			TimestampedObject<T> previous = getLast();
			aBookIsSelected = true;
			while (getFromTail(++current).v == previous.v) {
				aBookIsSelected = !aBookIsSelected;
				previous = getFromTail(current);
			}
		} catch (NoSuchElementException e) {}
		return aBookIsSelected;
	}
	
	public int getOccurenceCount(T val) {
		int result = 0;
		for (TimestampedObject<T> t : this)
			if (t.v == val)
				result++;
		return result;
	}
	
	private void sortLogByTimestamps() {
		Collections.sort(this, new Comparator<TimestampedObject<T>>() {
			@Override
			public int compare(TimestampedObject<T> o1, TimestampedObject<T> o2) {
				return (int) (o1.timestamp - o2.timestamp);
			}
		});
	}
	
	public void addRandomInteractionToLog(int numberOfNewRandomEntries, long maxTimeInHistory, ScreenElementSet<T> ts) {
		while(numberOfNewRandomEntries-- > 0) {
			addV(ts.getRandom(), (long) (System.currentTimeMillis()-(Math.random()*maxTimeInHistory)));
		}
		sortLogByTimestamps();
	}
	
	public int getClickedCount(T k) {
		int count = 0;
		for (TimestampedObject<T> t : this) {
			if (t.v == k) count++;
		}
		return count;
	}
	
	public int getMaxCount(T k) {
		int count = 0;
		for (T j : k.getParent()) {
			int localCount = getClickedCount(j);
			if (localCount > count) count = localCount;
		}
		return count;
	}

}
