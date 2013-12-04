package matt.instantiation;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import matt.instantiation.Parent.Inner;
import matt.instantiation.Parent.Nested;

import org.junit.Test;

public class InstantiationTest {

	public class GClassExtInner extends GClass<String> {
	}
	
	@Test
	public void test() throws InstantiationException, IllegalAccessException {
        String d = String.class.newInstance(); // OKay !
        assertTrue(new GClass<String>().getClass() == GClass.class);
        
        GClass<String> val2 = GClass.class.newInstance();
        GClass<String> val3 = getInstance3(new GClassExt());
		GClass<String> val4 = getInstance2(GClassExt.class);
	}
	
	@Test(expected = java.lang.InstantiationException.class)
	public void test2() throws InstantiationException, IllegalAccessException {
        GClass<String> val4 = getInstance2(GClassExtInner.class);
	}
	
	@Test(expected = java.lang.InstantiationException.class)
	public void test4() throws InstantiationException, IllegalAccessException {
		Boolean.class.newInstance();
	}
	
	public static <T> T getInstance2(Class<T> t) throws InstantiationException, IllegalAccessException {
		return t.newInstance();
	}
	
	public static <T> T getInstance3(T t) throws InstantiationException, IllegalAccessException {
		return (T) t.getClass().newInstance();
	}
	
	@Test
	public void test5() throws InstantiationException, IllegalAccessException {
		Parent h = new Parent();
		Parent.Inner hi = h.new Inner();
	}
	
	@Test(expected = java.lang.InstantiationException.class)
	public void test6() throws InstantiationException, IllegalAccessException {
		Parent.Inner hi = Parent.Inner.class.newInstance();
	}
	
	@Test
	public void test7() throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		Parent parentInstance = new Parent();
		Inner inner = Inner.class.getDeclaredConstructor(Parent.class).newInstance(parentInstance);
		Inner inner2 = Inner.class.getConstructor(Parent.class).newInstance(parentInstance);
		Parent.class.getConstructor().newInstance();
	}
	
	@Test
	public void test8() throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		Class<?> enclosingClass = Inner.class.getEnclosingClass();
		Inner inner = Inner.class.getDeclaredConstructor(enclosingClass).newInstance(enclosingClass.newInstance());
	}
	
	public static <T> T getInstance4(Class<T> t) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if (t.isMemberClass() && !Modifier.isStatic(t.getModifiers())) {
			Class<?> enclosingClass = t.getEnclosingClass();
			return t.getDeclaredConstructor(enclosingClass).newInstance(enclosingClass.newInstance());
		} else {
			return t.newInstance();
		}
	}
	
	@Test
	public void test10() throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		getInstance4(Parent.class);
		getInstance4(Inner.class);
		getInstance4(Nested.class);
	}
	
}
