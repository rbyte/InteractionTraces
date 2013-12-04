package matt.instantiation;

public class GClass<A> implements Runnable {
	public A a;

	public GClass() {
	}

	public final GClass<A> set(A a) {
		this.a = a;
		return this;
	}

	@Override
	public void run() {
	}
}
