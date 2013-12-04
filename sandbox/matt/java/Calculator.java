package matt.java;

import edu.emory.mathcs.backport.java.util.Arrays;

public class Calculator {
	
	public static void main(String[] args) {
		int[] arr = new int[] {1,2,3};
		int[] arr2 = arr;
		arrayCopy(arr);
		System.out.println(Arrays.toString(arr));
		System.out.println(Arrays.toString(arr2));
	}
	
	public static void arrayCopy(int[] arr) {
		arr[0] = 8;
	}

}
