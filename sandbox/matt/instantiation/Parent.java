package matt.instantiation;

import java.lang.reflect.Modifier;

public class Parent {
	
	// Nested and Inner Classes are together called Member Classes
	
    public static class Nested {
        public Nested() {
            System.out.println("Nested constructed");
        }
    }

    public class Inner {
        public Inner() {
            System.out.println("Inner constructed");
        }
    }

    public static void main(String... args) throws Exception {
        // Construct nested class the normal way:
        Nested nested = new Nested();

        // Construct inner class the normal way:
        Inner inner = new Parent().new Inner();

        // Construct nested class by reflection:
        Class.forName("matt.instantiation.Parent$Nested").newInstance();

        // Construct inner class by reflection:
        Object parent = Class.forName("matt.instantiation.Parent").newInstance();
        for (Class<?> cls : parent.getClass().getDeclaredClasses()) {
            if (!Modifier.isStatic(cls.getModifiers())) {
                // This is an inner class. Pass the parent class in.
                cls.getDeclaredConstructor(new Class[] { parent.getClass() }).newInstance(new Object[] { parent });
            } else {
                // This is a nested class. You can also use it here as follows:
                cls.getDeclaredConstructor(new Class[] {}).newInstance(new Object[] {});
            }
        }
    }
}