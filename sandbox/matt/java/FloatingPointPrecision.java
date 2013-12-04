package matt.java;

import static org.junit.Assert.*;

import org.junit.Test;

public class FloatingPointPrecision {
//
//	@Test
//	public void test() {
//		// http://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html
//		// http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.2
//		// http://en.wikipedia.org/wiki/Unit_in_the_last_place
//		
//		// 0x = Hex Indicator
//		// A,B,C,D,E,F = Hex
//		assertTrue(0x1A == 26);
//		assertTrue(0x1F == 31);
//		long hexBytes = 0xFF_EC_DE_5E;
//		long hexWords = 0xCAFE_BABE;
//		// 0b = Binary Indicator
//		assertTrue(0b11010 == 26);
//		byte nybbles = 0b0010_0101;
//		long bytes = 0b11010010_01101001_10010100_10010010;
//		// E = Exponent Indicator
//		assertTrue(1.234e2 == 123.4);
//		// L,D,F = Long, Double, Float
//		assertTrue(123.4f == (float) 123.4d);
//		// P = Binary Exponent Indicator
//		assertTrue(0x1p3 == 0b1000 && 0b1000 == 8);
//		// _ = visually pleasing formatting
//		assertTrue(999_99_9999L == 999999999l);
//		assertTrue(3.14_15F == 3.1415f);
//		assertTrue(5_______2 == 52);
//		
//		assertTrue(-0d == 0d);
//		
//		assertTrue(5/3 == 1);
//		
//		assertTrue(Double.MIN_NORMAL == 0x1.0p-1022);
//		assertTrue(Double.MIN_NORMAL == 2.2250738585072014E-308);
//		assertTrue(Double.MIN_NORMAL == Double.longBitsToDouble(0x0010_0000_0000_0000L));
//		
//		assertTrue(Double.MAX_VALUE == 0x1.fffffffffffffP+1023);
//		assertTrue(Double.MAX_VALUE == 1.7976931348623157e+308);
//		
//		assertTrue(Double.MIN_VALUE == 0x0.0000000000001P-1022);
//		assertTrue(Double.MIN_VALUE == 4.9e-324);
//		assertTrue(Double.MIN_VALUE == Math.ulp(0d));
//		
//		// http://en.wikipedia.org/wiki/Double_precision_floating-point_format
//		assertTrue(Double.POSITIVE_INFINITY == Double.longBitsToDouble(0x7ff0_0000_0000_0000L));
//		assertTrue(Double.POSITIVE_INFINITY == Double.longBitsToDouble(
//			0b0111_1111_1111_0000___0000_0000_0000_0000___0000_0000_0000_0000___0000_0000_0000_0000L));
//		// +0eMAX_Exponent
//		
//		assertTrue(Long.MIN_VALUE == 0x8000000000000000L);
//		assertTrue(Long.MAX_VALUE == 0x7fff_ffff_ffff_ffffL);
//		
//		assertTrue( (0xFF_FF_FF_FF & 0x00_00_FF_00) == 0x00_00_FF_00);
//		assertTrue( 0x7F_FF_FF_FF == Integer.MAX_VALUE);
//		assertTrue( 0b0111_1111_1111_1111___1111_1111_1111_1111 == Integer.MAX_VALUE);
//		assertTrue( 0x80_00_00_00 == Integer.MIN_VALUE);
//		assertTrue( 0b1000_0000_0000_0000___0000_0000_0000_0000 == Integer.MIN_VALUE);
//		assertTrue( ((0xFF_FF_FF_FF >> 24) & 0xFF) == 0xFF_00_00_00);
//		
//	}
}
