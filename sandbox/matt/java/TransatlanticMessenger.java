package matt.java;

import java.math.BigInteger;
import java.util.Date;

/** 
 * @author Matthias
 */
public class TransatlanticMessenger {
	
	/**
	 * Implements the one and only. May become unstable with age. Handle with care!
	 * And remember: Its not a bug, its a feature!
	 * @category A very good friend.
	 */
	protected class Steffen extends AnIncrediblePerson {
		BigInteger age;
		boolean ageDoesNotMatterAnymore = false;
		final boolean isAGreatGuy = true;
		boolean hasBirthday;
		
		@SuppressWarnings("deprecation")
		Steffen(String age) {
			try {
				this.age = new BigInteger(age);
			} catch (NumberFormatException e) {
				ageDoesNotMatterAnymore = true;
			}
			hasBirthday = new Date().getMonth()+1 == 2
				&& new Date().getDate() == 22;
		}
		
		public void mail(String msg) {
			System.out.println(msg);
		}
	}
	
	class AnIncrediblePerson {
		// needs no more additives
	}
	
	public static void main(String[] args) {
		new TransatlanticMessenger().run();
	}
	
	void run() {
		Steffen theOneAndOnly = new Steffen("far too old");
		assert real(theOneAndOnly);
		theOneAndOnly.mail("Hallo "+theOneAndOnly.getClass().getSimpleName()+",\n");
		theOneAndOnly.mail("you are "+theOneAndOnly.getClass().getSuperclass().getSimpleName()+"!");
		if (theOneAndOnly.hasBirthday) {
			theOneAndOnly.mail("Hey, its your birthday btw!");
			theOneAndOnly.mail("You are "+ (!theOneAndOnly.ageDoesNotMatterAnymore ? theOneAndOnly.age :
				"... umm, old enough to know, that age is not what matters, because of who you are."));
		}
	}
	
	boolean real(Steffen theOneAndOnly) {
		try {
			assert theOneAndOnly.getClass().getSuperclass() == AnIncrediblePerson.class;
			assert theOneAndOnly.isAGreatGuy == true;
			return true;
		} catch (AssertionError e) {
			System.err.println("Get me the real one!");
		}
		return false;
	}
}
