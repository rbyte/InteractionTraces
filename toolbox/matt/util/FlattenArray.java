package matt.util;

import java.lang.reflect.Array;
import java.util.Iterator;

public class FlattenArray {
	
	private static class FlattenArrayIterator<T> implements Iterator<T> {
		private T[] arr;
		private int position = 0;
		private FlattenArrayIterator<T> child = null;
		FlattenArrayIterator(T[] arr) { this.arr = arr; }
		@Override public boolean hasNext() { return position < arr.length || (child != null && child.hasNext()); }
		@Override public void remove() { /* not available */ }
		
		@SuppressWarnings("unchecked") @Override public T next() {
			if (child != null && child.hasNext())
				return child.next();
			return arr[position] instanceof Object[]
				? (child = new FlattenArrayIterator<T>((T[]) arr[position++])).next()
				: arr[position++];
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] flattenArray(Class<T> c, Object[] arr) {
		T[] result = (T[]) Array.newInstance(c, determineDeepSize(arr));
//		T[] result = (T[]) new Object[determineDeepSize(arr)];
		FlattenArrayIterator<T> fai = new FlattenArrayIterator<T>((T[]) arr);
		for (int i=0; i<result.length; i++) {
			if (!fai.hasNext()) throw new AssertionError();
			result[i] = fai.next();
		}
		return result;
	}
	
	public static int determineDeepSize(Object[] arr) {
		int result = 0;
		for (Object o : arr)
			result += o instanceof Object[] ? determineDeepSize((Object[]) o) : 1;
		return result;
	}
}
