package matt.java;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Test;

public class CallByValueTest {
	
	@Test
	public void testInteger() {
		Integer val = new Integer(3);
		modifyInteger(val);
		// Integers are final. When modifying it, a new instance is created.
		assertTrue(val == 3);
		
		Integer val2 = val;
		val++;
		assertTrue(val != val2);
		
		int[] arr = {1,2,3};
		modifyArray(arr);
		int[] arr2 = {2,3,4};
		assertArrayEquals(arr, arr2);
	}
	
	public void modifyInteger(Integer val) {
		val++;
	}
	
	@Test
	public void testMyBox() {
		MyBox myBox = new MyBox();
		myBox.val = 3;
		modifyMyBox(myBox);
		assertTrue(myBox.val == 4);
		
		MyBox myBox2 = myBox;
		myBox.val = 9;
		assertTrue(myBox == myBox2);
	}
	
	class MyBox {
		public int val;
	}
	
	public void modifyMyBox(MyBox myBox) {
		myBox.val++;
	}
	
	@Test
	public void testPoints() {
		Point pnt1 = new Point(0, 0);
		Point pnt2 = new Point(0, 0);
		tricky(pnt1, pnt2);
		
		// pnt1 is modified, however no swapping is visible because
		// Java calls by value, however since objects are references,
		// only the references to the objects are copied when a function/method is called
		assertTrue(pnt1.x == 5 && pnt1.y == 5);
		assertTrue(pnt2.x == 0 && pnt2.y == 0);
	}
	
	public void tricky(Point arg1, Point arg2) {
		arg1.x = 5;
		arg1.y = 5;
		Point temp = arg1;
		arg1 = arg2;
		arg2 = temp;
	}
	
	public void modifyArray(int[] arr) {
		for (int i=0; i<arr.length; i++) {
			arr[i]++;
		}
	}

}
