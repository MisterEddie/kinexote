/**
 * 
 */
package kinexote;

import ddf.minim.Minim;
import kinexote.audio.SoundBoard;
import kinexote.sensor.Frame;
import kinexote.sensor.Slide;
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

	private class SlideHandler implements SlideRecognizer.Callback<Slide> {

		@Override
		public void cb(Slide gesture) {
			System.out.printf("%d Slide %d: %d\n", gesture.hand, gesture.axis, gesture.value);
		}

	}

	private SwipeRecognizer swipeRec;

	private class SwipeHandler implements SlideRecognizer.Callback<Swipe> {

		@Override
		public void cb(Swipe gesture) {
			System.out.printf("%d Swipe %d: %d\n", gesture.hand, gesture.direction);
		}

	}

	public void settings() {
		size(500, 500);
	}

	public void setup() {
		minim = new Minim(this);
		soundboard = new SoundBoard(minim, "Track1.wav");
		slideRec = new SlideRecognizer(new SlideHandler(), 3);
		swipeRec = new SwipeRecognizer(new SwipeHandler(), 0.4, 0.6);
		kinect = new KinectPV2(this);
		kinect.enableSkeletonDepthMap(true);
		kinect.init();
	}

	public void draw() {
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

				newFrame(new Frame(lx, ly, lg, rx, ry, rg));
			}
		}

		updatePixels();
	}

	public void newFrame(Frame frame) {
		slideRec.newFrame(frame);
		swipeRec.newFrame(frame);
	}

	public static void main(String[] args) {
		String[] processingArgs = { "application" };
		Kinexote application = new Kinexote();
		PApplet.runSketch(processingArgs, application);
	}
}
