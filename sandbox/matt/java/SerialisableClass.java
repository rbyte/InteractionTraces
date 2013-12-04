package matt.java;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// saves and object to a file and reloads it.
public class SerialisableClass implements Serializable {

	private static final long serialVersionUID = -4759140161118670611L;
	private int val;
	// transient lets the secret not be transfered
	private transient int secret;

	public SerialisableClass(int val) {
		this.val = val;
		this.secret = val;
	}

	public int getVal() {
		return val;
	}
	
	public int getSecret() {
		return secret;
	}
	
	public static void main(String[] args) {
		int input = 10;
		String filename = "time.ser";
		SerialisableClass object = new SerialisableClass(input);
		saveObjectToFile(filename, object);
		SerialisableClass objectLoadedFromFile = loadObjectFromFile(filename, SerialisableClass.class);
		System.out.println(objectLoadedFromFile.getVal());
		System.out.println(objectLoadedFromFile.getSecret());
		assert input == objectLoadedFromFile.getVal();
		assert 0 == objectLoadedFromFile.getSecret();
	}

	public static <T extends Serializable> void saveObjectToFile(String filename, T t) {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(t);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static <T extends Serializable> T loadObjectFromFile(String filename, Class<T> ct) {
		T t = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			t = ct.cast(in.readObject());
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return t;
	}

}
