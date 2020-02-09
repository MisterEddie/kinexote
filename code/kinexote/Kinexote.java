/**
 * 
 */
package kinexote;

import ddf.minim.AudioInput;
import ddf.minim.AudioOutput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import ddf.minim.ugens.FilePlayer;
import kinexote.audio.SoundBoard;
import kinexote.sensor.Frame;
import kinexote.sensor.Slide;
import kinexote.sensor.Slide.Axis;
import kinexote.sensor.SlideRecognizer;
import kinexote.sensor.Swipe;
import kinexote.sensor.SwipeRecognizer;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import KinectPV2.*;

/**
 * @author billie
 *
 */
public class Kinexote extends PApplet {

	KinectPV2 kinect;
	private Minim minim;
	private SoundBoard soundboard;
	private SlideRecognizer slideRec;
	private Frame lastFrame = new Frame(0.0, 0.0, false, 0.0, 0.0, false);
	private int xcenter = 0;
	private int ycenter = 0;

	private class SlideHandler implements SlideRecognizer.Callback<Slide> {

		@Override
		public void cb(Slide gesture) {
			System.out.printf("%b Slide %b: %b\n", gesture.hand == Slide.Handedness.Left,
					gesture.axis == Slide.Axis.Horizontal, gesture.value);
		}

	}

	private SwipeRecognizer swipeRec;
	private FFT fft;
	private FilePlayer filePlayer;
	private AudioOutput out;
	private AudioInput in;
	private AudioPlayer fftAudio;
	private float angle;

	private class SwipeHandler implements SlideRecognizer.Callback<Swipe> {

		@Override
		public void cb(Swipe gesture) {
			System.out.printf("%b Swipe %b\n", gesture.hand == Swipe.Handedness.Left,
					gesture.direction == Swipe.Direction.Up);
		}

	}

	public void settings() {
		fullScreen(P3D);
	}

	public void setup() {
		minim = new Minim(this);

		xcenter = displayWidth / 2;
		ycenter = displayHeight / 2;
		
		soundboard = new SoundBoard(minim, "Track1.wav");
		in = minim.getLineIn(Minim.MONO, 2048);
		filePlayer = new FilePlayer(minim.loadFileStream("Track1.wav"));
		// sampler = new Sampler("myrecording.wav", 1, minim);
		out = minim.getLineOut(Minim.MONO);
		filePlayer.patch(out);
		filePlayer.loop();
		textFont(createFont("Arial", 12));
		fftAudio = minim.loadFile("Track1.wav");
		fft = new FFT(1024, 44100);
		fft.window(FFT.HANN);
		slideRec = new SlideRecognizer(new SlideHandler(), 7);
		swipeRec = new SwipeRecognizer(new SwipeHandler(), 0.35, 0.5);
		kinect = new KinectPV2(this);
		kinect.enableSkeletonDepthMap(true);
		kinect.init();
	}

	public void draw() {
		background(0);
		loadPixels();

		ArrayList<KSkeleton> skeletonArray = kinect.getSkeletonDepthMap();

		if (skeletonArray.size() > 0) {
			KSkeleton skeleton = (KSkeleton) skeletonArray.get(0);
			if (skeleton.isTracked()) {

				KJoint[] joints = skeleton.getJoints();
				PVector limb_len = PVector.sub(joints[KinectPV2.JointType_HandTipRight].getPosition(),
						joints[KinectPV2.JointType_ElbowRight].getPosition());
				PVector arm_len = PVector.sub(joints[KinectPV2.JointType_HandTipRight].getPosition(),
						joints[KinectPV2.JointType_ShoulderRight].getPosition());
				PVector shoul_len = PVector.sub(joints[KinectPV2.JointType_ShoulderRight].getPosition(),
						joints[KinectPV2.JointType_ShoulderLeft].getPosition());

				double add_length = pow(pow(limb_len.x, 2) + pow(limb_len.y, 2), 0.5f);
				double add_arm_len = pow(pow(arm_len.x, 2) + pow(arm_len.y, 2), 0.5f);
				double add_shoul_len = pow(pow(shoul_len.x, 2) + pow(shoul_len.y, 2), 0.5f);

				double right_hand_Y = joints[KinectPV2.JointType_HandTipRight].getY();
				double right_hand_X = joints[KinectPV2.JointType_HandTipRight].getX();
				double left_hand_Y = joints[KinectPV2.JointType_HandTipLeft].getY();
				double left_hand_X = joints[KinectPV2.JointType_HandTipLeft].getX();
				double head_Y = joints[KinectPV2.JointType_Head].getY();

				double base_spine_Y = joints[KinectPV2.JointType_SpineBase].getY();

				double hip_left_X = joints[KinectPV2.JointType_HipLeft].getX();
				double hip_right_X = joints[KinectPV2.JointType_HipRight].getX();

				double bod_range_Y = base_spine_Y - head_Y + add_length; // add length is the length of the arm
				double bod_range_X = add_arm_len + add_shoul_len * 2;

				double ry = Math.min(Math.max(((base_spine_Y - right_hand_Y) / bod_range_Y), 0.0), 1.0);
				double rx = Math.min(Math.max(((right_hand_X - (hip_left_X - add_arm_len)) / bod_range_X), 0.0), 1.0);
				double ly = Math.min(Math.max(((base_spine_Y - left_hand_Y) / bod_range_Y), 0.0), 1.0);
				double lx = Math.min(Math.max(((left_hand_X - (hip_right_X - add_arm_len)) / bod_range_X), 0.0), 1.0);

				boolean rg = joints[KinectPV2.JointType_HandRight].getState() == KinectPV2.HandState_Closed;
				boolean lg = joints[KinectPV2.JointType_HandLeft].getState() == KinectPV2.HandState_Closed;

				drawJoint(joints, KinectPV2.JointType_HandTipRight, rg);

				drawJoint(joints, KinectPV2.JointType_HandTipLeft, lg);

				newFrame(new Frame(lx, ly, lg, rx, ry, rg));
			}
		}

		updatePixels();

		stroke(255, 20);
		// println(redVal);

		fft.forward(out.mix);
		// println(fft.specSize());

		stroke(255);
		for (int i = 0; i < out.bufferSize() - 1; i++) {
			line(3*i, 100 + out.left.get(i) * 500, 3*i + 1, 100 + out.left.get(i + 1) * 500);
//			line(i, ycenter + out.left.get(i) * 500, i + 1, ycenter + out.left.get(i + 1) * 500);
			line(3*i, ycenter + out.left.get(i) * 500, 3*i + 1, ycenter + out.left.get(i + 1) * 500);
			line(3*i, displayHeight - 100 + out.left.get(i) * 500, 3*i + 1, displayHeight - 100 + out.left.get(i + 1) * 500);
		}

		for (int i = 0; i < fft.specSize(); i++) {
			// draw the line for frequency band i, scaling it by 4 so we can see it a bit
			// better
			// line(i, 100, i, 100 - fft.getBand(i) * 50);
			float angle = map(i, 0, fft.specSize(), 0, TWO_PI);
			float colour = map(i, 0, fft.specSize(), 0, 255);
			fill(colour, 255 - colour, colour - 128, colour);
			arc(xcenter, ycenter, 250 + fft.getBand(i / 6) * 50, 250 + fft.getBand((i + 30) / 6) * 50, -angle, 0);
			fill(0);
			ellipse(xcenter, ycenter, 250, 240);

		}

		drawOrbit(xcenter / 2, color(random(0,255), random(0,255), random(0,255)), 0.1f, xcenter / 14, 1, false, false);
		drawOrbit(xcenter / 2.5f, color(random(0,255), random(0,255), random(0,255)), 0.1f, xcenter / 14, 1, true, false);
		drawOrbit(xcenter / 3.5f, color(random(0,255), random(0,255), random(0,255)), 0.1f, xcenter / 14, 1, false, false);
		drawOrbit(xcenter / 5, color(random(0,255), random(0,255), random(0,255)), 0.1f, xcenter / 14, 1, true, false);
	}

	public void drawJoint(KJoint[] joints, int jointType, boolean closed) {
		noStroke();
		pushMatrix();
		translate(joints[jointType].getX(), joints[jointType].getY(), joints[jointType].getZ());

		if (closed)
			fill(255, 0, 0);

		else
			fill(0, 0, 255);

		ellipse(0, 0, 25, 25);
		popMatrix();
	}

	public void newFrame(Frame frame) {
		if (frame.getHand(Frame.Handedness.Right).grab != lastFrame.getHand(Frame.Handedness.Right).grab) {
			if (frame.getHand(Frame.Handedness.Right).grab) {
				soundboard.record();
			} else {
				soundboard.save();
				soundboard.play();
			}
		}
		soundboard.setReverb((int) (127 * frame.getHand(Frame.Handedness.Right).x));
		soundboard.setFilterFreq((int) (127 * frame.getHand(Frame.Handedness.Right).y));
		lastFrame = frame;
	}

	public void drawOrbit(float orbitRadius, int colour, float time, float sphereRadius, float effect, boolean clockwise,
			boolean selected) {
		// float effects = random(0, 127);

		// creates orbit
		stroke(colour);
		if (selected) {
			strokeWeight(25);

			fill(color(0, 255, 255, 0));
			ellipse(xcenter, ycenter, orbitRadius * 2, orbitRadius * 2);

			// creates clockwise orbitals
			if (clockwise) {
				float x = (xcenter) + sin(angle) * orbitRadius;
				float y = (ycenter) - cos(angle) * orbitRadius;
				fill(colour);
				ellipse(x, y, sphereRadius, sphereRadius);
				angle = angle + time;
			}

			// creates counterclockwise orbitals
			else {
				float x = (xcenter) + (sin(angle)) * orbitRadius;
				float y = (ycenter) + cos(angle) * orbitRadius;
				// fill(colour);
				spheres(sphereRadius, colour, x, y, effect);
				angle = angle + time;
			}
		}

		else {
			strokeWeight(1);
			fill(color(0, 255, 255, 0));
			ellipse(xcenter, ycenter, orbitRadius * 2, orbitRadius * 2);
			// creates clockwise orbitals
			if (clockwise) {
				float x = (xcenter) + sin(angle) * orbitRadius;
				float y = (ycenter) - cos(angle) * orbitRadius;
				fill(red(colour), green(colour), blue(colour));
				ellipse(x, y, sphereRadius, sphereRadius);
				angle = angle + time;
			}

			// creates counterclockwise orbitals
			else {
				float x = (xcenter) + (sin(angle)) * orbitRadius;
				float y = (ycenter) + cos(angle) * orbitRadius;
				// fill(colour);
				spheres(sphereRadius, colour, x, y, effect);
				angle = angle + time;
			}
		}
	}

	public void spheres(float sphereRadius, int colour, float x, float y, float effect) {
		fill(colour);
		ellipse(x, y, sphereRadius + effect, sphereRadius - effect);
	}

	public static void main(String[] args) {
		String[] processingArgs = { "application" };
		Kinexote application = new Kinexote();
		PApplet.runSketch(processingArgs, application);
	}
}
