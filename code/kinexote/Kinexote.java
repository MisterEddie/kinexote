/**
 * 
 */
package kinexote;

import java.util.ArrayList;

import ddf.minim.AudioSample;
import ddf.minim.Minim;

import processing.core.PApplet;
import processing.event.MouseEvent;

/**
 * @author billie
 *
 */
public class Kinexote extends PApplet {

	private Minim minim;
	private AudioSample snare;

	private ArrayList<Ball> balls = new ArrayList<>();

	public void settings() { 
		size(500, 500);
		balls.add(new Ball(this, width / 2, height / 2));
		minim = new Minim(this);
	}

	public void setup() {
		snare = minim.loadSample("res/SD.wav", 512);
		if (snare == null)
			System.out.println("Didn't get snare!");
	}

	public void draw() {
		background(64);
		for (Ball b : balls) {
			b.step();
			b.render();
		}
		this.circle(mouseX, mouseY, 10);
	}

	public void mousePressed(MouseEvent mouseEvent) {
		snare.trigger();
		balls.add(new Ball(this, mouseX, mouseY));
	}

	public static void main(String[] args) {
		String[] processingArgs = { "application" };
		Kinexote application = new Kinexote();
		PApplet.runSketch(processingArgs, application);
	}
}
