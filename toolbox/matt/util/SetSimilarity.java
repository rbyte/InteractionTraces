package matt.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class SetSimilarity {
	
	public static void main(String args[]) {
		
		int d = 10;
		int a = 2;
		int b = 2;
		int x = 0;
		
		int i = 5000;
		ArrayList<Integer> hist = new ArrayList<Integer>(i);
		while (i-- > 0) {
			hist.add(new SetSimilarity().runOnce(d, a, b));
		}
		
		System.out.println(propOcc(hist, x));
		
		long comb = combinations(d,x)*combinations(d-x,Math.max(a,b)-x)*combinations(d-x-Math.max(a,b),Math.min(a,b)-x);
		long all = combinations(d,a)*combinations(d,b);
		
		System.out.println(comb+" / "+all);
		System.out.println((double) comb / (double) all);
	
	}
	
	public Integer runOnce(int d, int a, int b) {
		Set<Integer> domain = createDomain(d);
		Set<Integer> A = createRandomSet(domain, a);
		Set<Integer> B = createRandomSet(domain, b);
		
		double intersection_h = Math.min(A.size(), B.size());
		double intersection_l = Math.max(0, A.size() + B.size() - domain.size());
		double intersection_a = intersection(A,B).size();
		double intersection_expected = A.size() * B.size() / domain.size();
		assert(intersection_l <= intersection_a);
		assert(intersection_a <= intersection_h);
		assert(intersection_l <= intersection_expected);
		assert(intersection_expected <= intersection_h);
		
		double symDiff_h = Math.min(2*domain.size() - A.size() - B.size(), A.size() + B.size());
		double symDiff_l = Math.abs(A.size() - B.size());
		double symDiff_a = symDifference(A,B).size();
		assert(symDiff_l <= symDiff_a);
		assert(symDiff_a <= symDiff_h);
		
		double percentageSimilar = intersection_h == intersection_l ? 0 : (intersection_a - intersection_l) / (intersection_h-intersection_l);
		double percentageDisSimilar = symDiff_h == symDiff_l ? 1 : (symDiff_a - symDiff_l) / (symDiff_h-symDiff_l);
		
		double jaccard = union(A,B).size() == 0 ? 1 : (float) intersection(A,B).size() / (float) union(A,B).size();
		
		
//		System.out.println(domain);
//		System.out.println(A);
//		System.out.println(B);
//		System.out.println(intersection_l+" "+intersection_expected+" "+intersection_h);
//		System.out.println(intersection(A,B));
//		System.out.println(symDifference(A,B));
//		System.out.println(percentageSimilar+", j: "+jaccard);
//		System.out.println(percentageDisSimilar);
		assert(percentageSimilar+percentageDisSimilar == 1d);
		
		return intersection(A,B).size();
	}
	
	static double propOcc(ArrayList<Integer> arr, Integer val) {
		int count = 0;
		for (Integer integer : arr) {
			if (integer == val)
				count++;
		}
		return (double) count / arr.size();
	}
	
	static double mean(ArrayList<Integer> arr) {
		Integer sum = 0;
		for (Integer integer : arr) {
			sum += integer;
		}
		return (double) sum / arr.size();
	}
	
	static long combinations(int n, int k) {
		if (n<=0)
			return 1;
		long coeff = 1;
		for (int i = n - k + 1; i <= n; i++) {
			coeff *= i;
		}
		for (int i = 1; i <= k; i++) {
			coeff /= i;
		}
		return coeff;
	}
	
	private Set<Integer> createDomain(int size) {
		HashSet<Integer> A = new HashSet<Integer>();
		while (size > 0) {
			A.add(size--);
		}
		return A;
	}
	
	private <T> Set<T> createRandomSet(Set<T> domain) {
		Random random = new Random();
		HashSet<T> A = new HashSet<T>();
		for (T integer : domain) {
			if (random.nextBoolean()) {
				A.add(integer);
			}
		}
		return A;
	}
	
	private void createRandomSetTest() {
		Set<Integer> domain = createDomain(10);
		
		int size = 8;
		int count = 2000;
		
		while (count-- > 0) {
			Set<Integer> A = createRandomSet(domain, size);
			assert(A.size() == size);
		}
	}
	
	private <T> Set<T> createRandomSet(Set<T> domain, int size) {
		assert(size <= domain.size());
		Random random = new Random();
		HashSet<T> A = new HashSet<T>();
		int i = 0;
		for (T integer : domain) {
			if (random.nextDouble() < (double) (size-A.size()) / (double) (domain.size()-(i++))) {
				A.add(integer);
			}
		}
		return A;
	}

	public static <T> Set<T> union(Set<T> setA, Set<T> setB) {
		Set<T> tmp = new TreeSet<T>(setA);
		tmp.addAll(setB);
		return tmp;
	}

	public static <T> Set<T> intersection(Set<T> setA, Set<T> setB) {
		Set<T> tmp = new TreeSet<T>();
		for (T x : setA)
			if (setB.contains(x))
				tmp.add(x);
		return tmp;
	}

	public static <T> Set<T> difference(Set<T> setA, Set<T> setB) {
		Set<T> tmp = new TreeSet<T>(setA);
		tmp.removeAll(setB);
		return tmp;
	}

	public static <T> Set<T> symDifference(Set<T> setA, Set<T> setB) {
		Set<T> tmpA;
		Set<T> tmpB;

		tmpA = union(setA, setB);
		tmpB = intersection(setA, setB);
		return difference(tmpA, tmpB);
	}

	public static <T> boolean isSubset(Set<T> setA, Set<T> setB) {
		return setB.containsAll(setA);
	}

	public static <T> boolean isSuperset(Set<T> setA, Set<T> setB) {
		return setA.containsAll(setB);
	}

}
