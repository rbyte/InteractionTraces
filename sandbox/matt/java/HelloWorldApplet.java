package matt.java;

import java.applet.Applet;
import java.awt.Graphics;

public class HelloWorldApplet extends Applet {
	private static final long serialVersionUID = 8181667915843762682L;

	public void paint(Graphics g) {
        g.drawString("tra la la die Mouse ist da", 50, 25);
    }
}