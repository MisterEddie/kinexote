/**
 * 
 */
package kinexote;

import java.util.ArrayList;
import processing.core.PApplet;

/**
 * @author billie
 *
 */
public class Application extends PApplet {

	private ArrayList<Ball> balls = new ArrayList<>();
	
	public void settings(){
		size(500, 500);
		balls.add(new Ball(this, width/2, height/2));
	}
	
	public void draw(){
		background(64);
		for(Ball b : balls){
			b.step();
			b.render();
		}
	}
	
	public void mouseDragged(){
		balls.add(new Ball(this, mouseX, mouseY));
	}
	
	public static void main(String[] args){
		String[] processingArgs = {"application"};
		Application application = new Application();
		PApplet.runSketch(processingArgs, application);
	}
}
