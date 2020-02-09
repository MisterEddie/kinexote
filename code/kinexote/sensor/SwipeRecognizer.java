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
	
	public void newFrame(Frame inFrame) {
		if(inFrame.getHand(Frame.Handedness.Left).x > outThreshold) {
			gestureStart.put(Frame.Handedness.Left, inFrame.getHand(Frame.Handedness.Left));
			inProgress.put(Frame.Handedness.Left, true);
		}
		if(inFrame.getHand(Frame.Handedness.Left).x < inThreshold) {
			inProgress.put(Frame.Handedness.Left, false);
			if(gestureStart.get(Frame.Handedness.Left).y > 0.5 && inFrame.getHand(Frame.Handedness.Left).y < 0.5) {
				callback.cb(new Swipe(Swipe.Handedness.Left, Swipe.Direction.Down));
			}
			if(gestureStart.get(Frame.Handedness.Left).y < 0.5 && inFrame.getHand(Frame.Handedness.Left).y > 0.5) {
				callback.cb(new Swipe(Swipe.Handedness.Left, Swipe.Direction.Up));
			}
		}
		

		if(inFrame.getHand(Frame.Handedness.Right).x > outThreshold) {
			gestureStart.put(Frame.Handedness.Right, inFrame.getHand(Frame.Handedness.Right));
			inProgress.put(Frame.Handedness.Right, true);
		}
		if(inFrame.getHand(Frame.Handedness.Right).x < inThreshold) {
			inProgress.put(Frame.Handedness.Right, false);
			if(gestureStart.get(Frame.Handedness.Right).y > 0.5 && inFrame.getHand(Frame.Handedness.Right).y < 0.5) {
				callback.cb(new Swipe(Swipe.Handedness.Right, Swipe.Direction.Down));
			}
			if(gestureStart.get(Frame.Handedness.Right).y < 0.5 && inFrame.getHand(Frame.Handedness.Right).y > 0.5) {
				callback.cb(new Swipe(Swipe.Handedness.Right, Swipe.Direction.Up));
			}
		}
	}
}
