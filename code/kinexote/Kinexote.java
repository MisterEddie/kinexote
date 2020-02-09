/**
 * 
 */
package kinexote;

import ddf.minim.Minim;
import kinexote.audio.SoundBoard;
import kinexote.sensor.Frame;
import processing.core.PApplet;

/**
 * @author billie
 *
 */
public class Kinexote extends PApplet {

	private Minim minim;
	private SoundBoard soundboard;

	public void settings() {
		size(500, 500);
	}

	public void setup() {
		minim = new Minim(this);
		soundboard = new SoundBoard(minim);
	}

	public void draw() {
		this.delay(50);
		newFrame(new Frame());
	}
	
	public void newFrame(Frame frame) {
		System.out.println("New Frame");
	}

	public static void main(String[] args) {
		String[] processingArgs = { "application" };
		Kinexote application = new Kinexote();
		PApplet.runSketch(processingArgs, application);
	}
}
