package matt.util;

import java.util.Properties;

import org.contract4j5.configurator.properties.PropertiesConfigurator;
import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Pre;
import static org.junit.Assert.*;

public class Testing {
	
	public static float assertIn0to1andReturnLast(float... fs) {
		assertTrue( fs.length >= 1);
		for (float f : fs) {
			assertTrue( 0 <= f && f <= 1);
		}
		return fs[fs.length-1];
	}
	
	public static boolean assertionsAreEnabled() {
		try {
			assert false;
			return false;
		} catch (AssertionError e) {
			// they are enabled ... good
			return true;
		}
	}
	
	/**
	 * @return true if assertions are enabled, an AssertionError if not.
	 */
	public static boolean checkForEnabledAssertion() throws AssertionError {
		try {
			assert false;
		} catch (AssertionError e) {
			// they are enabled ... good
			return true;
		}
		throw new AssertionError("Assertions are diabled!");
	}
	
	@Contract
	public class TriggerContractFailure {
		int val = 4;
		@Pre(value = "$this.val == 1", message = "Contract enforcement working.")
		TriggerContractFailure() {}
	}
	
	public static void setContractErrorReporting(boolean bool) {
		setContract4JProperty("org.contract4j5.ContractEnforcerReportErrors", Boolean.toString(bool));
	}
	
	public static void setContractEnforcement(boolean bool) {
		setContract4JProperty("org.contract4j5.Contract", Boolean.toString(bool));
	}
	
	private static void setContract4JProperty(String key, String value) {
		Properties props = new Properties();
		props.setProperty(key, value);
		PropertiesConfigurator configurator = new PropertiesConfigurator(props);
		configurator.configure();
	}
	
	public static boolean checkForContractEnforcement() {
		// http://code.google.com/p/developer-sandbox/source/browse/trunk/dbc/src/main/resources/Contract4J.properties
		try {
			// on a contract error, even if it is caught, Contract4J prints a [FATAL] err to the console,
			// which we would not like to see because it is expected.
			setContractErrorReporting(false);
			new Testing().new TriggerContractFailure();
		} catch (org.contract4j5.errors.ContractError e) {
			// contract enforcement enabled ... good
			setContractErrorReporting(true);
			return true;
		} catch (NoClassDefFoundError e) {
			// AspectJ is not working, so neighter is Contract4J
			assertTrue( e.getMessage().equals("org/aspectj/lang/NoAspectBoundException"));
		}
		return false;
	}

}
