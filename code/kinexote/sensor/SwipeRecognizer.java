package kinexote.sensor;

import java.util.Map;

import java.util.HashMap;

public class SwipeRecognizer implements GestureRecognizer {
	protected Callback<Swipe> callback;
	
	Map<Frame.Handedness,Boolean> inProgress = new HashMap<Frame.Handedness,Boolean>();
	Map<Frame.Handedness,Frame.Hand> gestureStart = new HashMap<Frame.Handedness,Frame.Hand>();
	
	double outThreshold, inThreshold;
	
	public SwipeRecognizer(Callback<Swipe> callback, double outThreshold, double inThreshold) {
		this.callback = callback;
		this.outThreshold = outThreshold;
		this.inThreshold = inThreshold;
		this.inProgress.put(Frame.Handedness.Left, false);
		this.inProgress.put(Frame.Handedness.Right, false);
	}
	
	public void debug(Frame inFrame) {
		if(inFrame.getHand(Frame.Handedness.Left).x > outThreshold) {
			System.out.printf("Out: %f,%f\t", inFrame.getHand(Frame.Handedness.Left).x, inFrame.getHand(Frame.Handedness.Left).y);
		} else {
			System.out.printf("In: %f,%f\t", inFrame.getHand(Frame.Handedness.Left).x, inFrame.getHand(Frame.Handedness.Left).y);
		}
		
		if(inFrame.getHand(Frame.Handedness.Right).x > outThreshold) {
			System.out.printf("Out: %f,%f\n", inFrame.getHand(Frame.Handedness.Right).x, inFrame.getHand(Frame.Handedness.Right).y);
		} else {
			System.out.printf("In: %f,%f\n", inFrame.getHand(Frame.Handedness.Right).x, inFrame.getHand(Frame.Handedness.Right).y);
		}
	}
	
	public void newFrame(Frame inFrame) {
		double lx = inFrame.getHand(Frame.Handedness.Left).x;
		double ly = inFrame.getHand(Frame.Handedness.Left).y;
		
		if(lx > outThreshold) {
			System.out.print("Out\t");
			gestureStart.put(Frame.Handedness.Left, inFrame.getHand(Frame.Handedness.Left));
			inProgress.put(Frame.Handedness.Left, true);
		} else if(lx < outThreshold) {
			System.out.print("In,\t");
			inProgress.put(Frame.Handedness.Left, false);
			if(gestureStart.get(Frame.Handedness.Left) != null && gestureStart.get(Frame.Handedness.Left).y > 0.7 && inFrame.getHand(Frame.Handedness.Left).y < 0.7) {
				callback.cb(new Swipe(Swipe.Handedness.Left, Swipe.Direction.Down));
			}
			if(gestureStart.get(Frame.Handedness.Left) != null && gestureStart.get(Frame.Handedness.Left).y < 0.7 && inFrame.getHand(Frame.Handedness.Left).y > 0.7) {
				callback.cb(new Swipe(Swipe.Handedness.Left, Swipe.Direction.Up));
			}
		}
		

		double rx = -inFrame.getHand(Frame.Handedness.Right).x;
		double ry = inFrame.getHand(Frame.Handedness.Right).y;
		
		if(rx > outThreshold) {
			System.out.print("Out\n");
			gestureStart.put(Frame.Handedness.Right, inFrame.getHand(Frame.Handedness.Right));
			inProgress.put(Frame.Handedness.Right, true);
		} else if(rx < outThreshold) {
			System.out.print("In\n");
			inProgress.put(Frame.Handedness.Right, false);
			if(gestureStart.get(Frame.Handedness.Right) != null && gestureStart.get(Frame.Handedness.Right).y > 0.7 && inFrame.getHand(Frame.Handedness.Right).y < 0.7) {
				callback.cb(new Swipe(Swipe.Handedness.Right, Swipe.Direction.Down));
			}
			if(gestureStart.get(Frame.Handedness.Right) != null && gestureStart.get(Frame.Handedness.Right).y < 0.7 && inFrame.getHand(Frame.Handedness.Right).y > 0.7) {
				callback.cb(new Swipe(Swipe.Handedness.Right, Swipe.Direction.Up));
			}
		}
	}
}
