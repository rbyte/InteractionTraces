package matt.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import matt.util.Testing;

public class MethodAsMethodArg {
	
	int someField = 5;
	static int staticField;
	
	MethodAsMethodArg() {
		for (Field field : this.getClass().getDeclaredFields()) {
			System.out.println(field);
		}
		
		for (Method method : this.getClass().getDeclaredMethods()) {
			System.out.println(method);
		}
		
		try {
			Method doitMethod = this.getClass().getMethod("addOne", int.class);
			Method hasMethodAsArgMethod = this.getClass().getMethod("hasMethodAsArg", Method.class, int.class);
			{
				int arg = 6;
				int returnedVal = (int) doitMethod.invoke(this, arg);
				assert returnedVal == ++arg;
				System.out.println("DID IT!");
			}
			{
				int arg = 6;
				int returnedVal = (int) hasMethodAsArgMethod.invoke(this, doitMethod, arg);
				assert returnedVal == ++arg+4;
				System.out.println("DID IT2!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Testing.checkForEnabledAssertion();
		MethodAsMethodArg thisInstance = new MethodAsMethodArg();
	}
	
	public int addOne(int i) {
		return ++i;
	}
	
	public int hasMethodAsArg(Method inject, int val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (int) inject.invoke(this, val+4);
	}

}
