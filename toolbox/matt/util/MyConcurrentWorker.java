package matt.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;

import matt.meta.AuthorInformation;

/**
 * Enables easy multithreaded processing.
 * 
 * Fundamentally works on a list of objects T. Every T needs to be processed - in parallel.
 * How it is processed is determined by a Worker, which must be an
 * extension of the WorkerTemplate<T> and can process any one T. Provide MyConcurrentWorker
 * with those two things, a list of objects T, and a Worker on T, and on run() it runs the workers
 * concurrently on all T.
 * 
 * @see http://en.wikipedia.org/wiki/Thread_pool_pattern
 * 
 * @author Matthias Graf
 */
@AuthorInformation
public class MyConcurrentWorker<T> {
	
	public static abstract class WorkerTemplate<G> implements Runnable {
		public G val;
		public final void set(G g) { this.val = g; }
		@Override public abstract void run();
		public final G get() { return val; }
	}
	
	private Iterable<? extends WorkerTemplate<T>> s;
	private int threadCount;
	private ProgressBar progressBar;
	private boolean hasBeenRunAtLeastOnce = false;
	
	public <ListOfT extends Iterable<T>, Worker extends WorkerTemplate<T>>
	MyConcurrentWorker(
			ListOfT listOfT,
			Class<Worker> workerOnT_Class)
				throws InterruptedException, InstantiationException, IllegalAccessException,
				IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this(
			listOfT,
			workerOnT_Class,
			null);
	}
	
	public <ListOfT extends Iterable<T>, Worker extends WorkerTemplate<T>>
	MyConcurrentWorker(
			ListOfT listOfT,
			Class<Worker> workerOnT_Class,
			ProgressBar progressBar)
				throws InterruptedException, InstantiationException, IllegalAccessException,
				IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this(
			listOfT,
			workerOnT_Class,
			null,
			progressBar);
	}
	
	public <ListOfT extends Iterable<T>, Worker extends WorkerTemplate<T>>
	MyConcurrentWorker(
			ListOfT listOfT,
			Class<Worker> workerOnT_Class,
			Object workerOnT_enclosingClassInstance)
				throws InterruptedException, InstantiationException, IllegalAccessException,
				IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this(
			listOfT,
			workerOnT_Class,
			workerOnT_enclosingClassInstance,
			null);
	}
	
	public <ListOfT extends Iterable<T>, Worker extends WorkerTemplate<T>>
	MyConcurrentWorker(
			ListOfT listOfT,
			Class<Worker> workerOnT_Class,
			Object workerOnT_enclosingClassInstance,
			ProgressBar progressBar)
				throws InterruptedException, InstantiationException, IllegalAccessException,
				IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this(
			listOfT,
			isInnerClassOf(workerOnT_Class, workerOnT_enclosingClassInstance)
				? workerOnT_Class.getDeclaredConstructor(workerOnT_Class.getEnclosingClass())
				: workerOnT_Class.getDeclaredConstructor(),
			isInnerClassOf(workerOnT_Class, workerOnT_enclosingClassInstance)
				? new Object[] {workerOnT_enclosingClassInstance}
				: new Object[] {},
			Runtime.getRuntime().availableProcessors(),
			progressBar);
	}
	
	private static boolean isInnerClassOf(Class<?> c, Object instanceOfEnclosingClass) {
		return instanceOfEnclosingClass == null ? false
			: c.isMemberClass() && !Modifier.isStatic(c.getModifiers()) && c.getEnclosingClass() == instanceOfEnclosingClass.getClass();
	}
	
	private <ListOfT extends Iterable<T>, Worker extends WorkerTemplate<T>>
	MyConcurrentWorker(
			ListOfT listOfT,
			Constructor<Worker> workerOnT_Constructor,
			Object[] initargs,
			int threadCount,
			ProgressBar progressBar)
				throws InterruptedException, InstantiationException, IllegalAccessException,
				IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		ArrayList<Worker> workunits = new ArrayList<Worker>();
		for (T a : listOfT) {
			Worker w = workerOnT_Constructor.newInstance(initargs);
			w.set(a);
			workunits.add(w);
		}
		init(workunits, threadCount, progressBar);
	}
	
	public <Worker extends WorkerTemplate<T>, S extends Iterable<Worker>>
	MyConcurrentWorker(S s) throws InterruptedException {
		this(s, null);
	}
	
	public <Worker extends WorkerTemplate<T>, S extends Iterable<Worker>>
	MyConcurrentWorker(S s, ProgressBar progressBar) throws InterruptedException {
		this(s, Runtime.getRuntime().availableProcessors(), progressBar);
	}
	
	public <Worker extends WorkerTemplate<T>, S extends Iterable<Worker>>
	MyConcurrentWorker(S s, int threadCount, ProgressBar progressBar) throws InterruptedException {
		init(s, threadCount, progressBar);
	}
	
	private <Worker extends WorkerTemplate<T>, S extends Iterable<Worker>>
	void init(S s, int threadCount, ProgressBar progressBar) throws InterruptedException {
		assert(threadCount > 0);
		this.s = s;
		this.threadCount = threadCount;
		if (progressBar != null) this.progressBar = progressBar;
	}
	
	public MyConcurrentWorker<T> runSequentially() throws InterruptedException {
		return run(1);
	}
	
	public MyConcurrentWorker<T> run() throws InterruptedException {
		return run(threadCount);
	}
	
	public MyConcurrentWorker<T> run(int threadCount) throws InterruptedException {
		if (progressBar != null && hasBeenRunAtLeastOnce)
			progressBar.reset();
		hasBeenRunAtLeastOnce = true;
		return run(s, threadCount, progressBar);
	}
	
	private synchronized <Worker extends WorkerTemplate<T>, S extends Iterable<Worker>>
	MyConcurrentWorker<T> run(S s, int threadCount, ProgressBar progressBar) throws InterruptedException {
		WorkunitProvider<Worker, S> workunitProvider = new WorkunitProvider<Worker, S>(s);
		ArrayList<Thread> threads = new ArrayList<Thread>(threadCount);
		
		for (int i=0; i<threadCount; i++)
			threads.add(new Thread(new WorkerThread<Worker, S>(workunitProvider)));
		for (Thread t : threads)
			t.start();
		for (Thread t : threads)
			t.join();
		
		if (progressBar != null) progressBar.done();
		return this;
	}
	
	public ArrayList<T> getResults() {
		ArrayList<T> ts = new ArrayList<T>();
		for (WorkerTemplate<T> worker : s) {
			ts.add(worker.get());
		}
		return ts;
	}
	
	private class WorkunitProvider<Worker extends WorkerTemplate<T>, S extends Iterable<Worker>> {
		private Iterator<Worker> workIterator;
		private S work;
		
		WorkunitProvider(S work) {
			this.work = work;
			restart();
		}
		
		public void restart() {
			workIterator = work.iterator();
		}
		
		public synchronized Worker getNext() {
			if (workIterator.hasNext()) {
				return workIterator.next();
			} else {
				return null;
			}
		}
	}
	
	private class WorkerThread<Worker extends WorkerTemplate<T>, S extends Iterable<Worker>> implements Runnable {
		WorkunitProvider<Worker, S> workunitProvider;
		int workunitsProcessed = 0;
		
		WorkerThread(WorkunitProvider<Worker, S> workunitProvider) {
			this.workunitProvider = workunitProvider;
		}
		
		public void run() {
			Worker workunit;
			while ((workunit = this.workunitProvider.getNext()) != null) {
				// Thread.currentThread().getId()
				workunit.run();
				if (progressBar != null) progressBar.progress();
				workunitsProcessed++;
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void threadMessage(String message) {
		System.out.format("%s: %s%n", Thread.currentThread().getName(), message);
	}
	
}