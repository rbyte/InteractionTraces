package matt.util;

import static org.junit.Assert.*;

import java.awt.geom.Rectangle2D;

import org.junit.Test;

public class UtilTest {
	
	@Test
	public void flattenArray1() {
		Integer[] arr = {1,2,3};
		assertTrue(FlattenArray.determineDeepSize(arr) == arr.length);
		assertArrayEquals(FlattenArray.flattenArray(Integer.class, arr), arr);
	}
	
	@Test
	public void flattenArray2() {
		Integer[][] arr = {{1,2,3,4},{5,6}};
		Integer[] expected = {1,2,3,4,5,6};
		assertTrue(FlattenArray.determineDeepSize(arr) == expected.length);
		assertArrayEquals(FlattenArray.flattenArray(Integer.class, arr), expected);
	}
	
	@Test
	public void flattenArray3() {
		Integer[][][] arr = {{{1},{2},{3,4},{5}},{{6},{7}},{{8}}};
		Integer[] expected = {1,2,3,4,5,6,7,8};
		assertTrue(FlattenArray.determineDeepSize(arr) == expected.length);
		assertArrayEquals(FlattenArray.flattenArray(Integer.class, arr), expected);
	}
	
	@Test
	public void flattenArray4() {
		Integer[][] ints4 = {{1,2,3,4},{5,6}};
		Object[][] arr = {(Object[]) ints4, {7,8,9,10}};
		Object[] expected = {1,2,3,4,5,6,7,8,9,10};
		assertTrue(FlattenArray.determineDeepSize(arr) == expected.length);
		assertArrayEquals(FlattenArray.flattenArray(Integer.class, arr), expected);
	}
	
	@Test
	public void flattenArray5() {
		Integer[] arr = {};
		assertTrue(FlattenArray.determineDeepSize(arr) == 0);
		assertArrayEquals(FlattenArray.flattenArray(Integer.class, arr), arr);
	}
	
	@Test
	public void flattenArray6() {
		Object[] ints1 = {1,2,3};
		Object[] ints2 = {ints1, 4};
		Object[] ints3 = {ints2, 5};
		Object[] arr = {6, ints3};
		Object[] expected = {6,1,2,3,4,5};
		assertTrue(FlattenArray.determineDeepSize(arr) == expected.length);
		assertArrayEquals(FlattenArray.flattenArray(Integer.class, arr), expected);
	}
	
	@Test
	public void resizeRectangle() {
		assertTrue(Util.resizeRectangle(new Rectangle2D.Float(0, 0, 15, 10), 0.5f)
			.equals(new Rectangle2D.Float(-2.5f, -2.5f, 20, 15)));
		System.out.println(Util.resizeRectangle(new Rectangle2D.Float(10, 10, 20, 20), -0.5f));
		assertTrue(Util.resizeRectangle(new Rectangle2D.Float(10, 10, 20, 20), -0.5f)
			.equals(new Rectangle2D.Float(15, 15, 10, 10)));
	}
	
}
