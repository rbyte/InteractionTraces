package matt.ui;

import java.awt.Color;

import org.junit.Test;
import static org.junit.Assert.*;

public class PToolboxTest {
	
	@Test
	public void setLightnessMultiplyTest() {
		assertTrue(PToolbox.setLightnessMultiply(new Color(0, 0, 0, 125), 1.0f).getAlpha() == 125);
	}

}
