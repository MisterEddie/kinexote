package kinexote.sensor;

import java.util.Map;
import java.util.Queue;
import java.util.HashMap;
import java.util.LinkedList;

public class SlideRecognizer implements GestureRecognizer {
	Callback<Slide> callback;
	
	Map<Frame.Handedness,Boolean> grabbed = new HashMap<Frame.Handedness,Boolean>();
	Queue<Frame> buffer = new LinkedList<Frame>();
	int debounceFrames = 3;
	
	Map<Frame.Handedness,Frame.Hand> gestureStart = new HashMap<Frame.Handedness,Frame.Hand>();
	
	public SlideRecognizer(Callback<Slide> callback, int debounceFrames) {
		this.callback = callback;
		this.debounceFrames = debounceFrames;
		grabbed.put(Frame.Handedness.Left, false);
		grabbed.put(Frame.Handedness.Right, false);
	}
	
	public void newFrame(Frame inFrame) {
		buffer.add(inFrame);
		if(buffer.size() > debounceFrames) {
			buffer.remove(inFrame);
		}
		
		boolean grabL = getGrabState(Frame.Handedness.Left);
		if(grabL != grabbed.get(Frame.Handedness.Left)) {
			grabbed.put(Frame.Handedness.Left, grabL);
			if(grabL) {
				gestureStart.put(Frame.Handedness.Left, inFrame.getHand(Frame.Handedness.Left));
			} else {
				double deltaX = gestureStart.get(Frame.Handedness.Left).x - inFrame.getHand(Frame.Handedness.Left).x;
				double deltaY = gestureStart.get(Frame.Handedness.Left).y - inFrame.getHand(Frame.Handedness.Left).y;
				
				if(deltaX > deltaY) {
					callback.cb(new Slide(Slide.Handedness.Left,Slide.Axis.Horizontal,deltaX));
				} else {
					callback.cb(new Slide(Slide.Handedness.Left,Slide.Axis.Vertical,deltaY));
				}
			}
		}

		boolean grabR = getGrabState(Frame.Handedness.Right);
		if(grabR != grabbed.get(Frame.Handedness.Right)) {
			grabbed.put(Frame.Handedness.Right, grabR);
			if(grabR) {
				gestureStart.put(Frame.Handedness.Right, inFrame.getHand(Frame.Handedness.Right));
			} else {
				double deltaX = gestureStart.get(Frame.Handedness.Right).x - inFrame.getHand(Frame.Handedness.Right).x;
				double deltaY = gestureStart.get(Frame.Handedness.Right).y - inFrame.getHand(Frame.Handedness.Right).y;

				if(deltaX > deltaY) {
					callback.cb(new Slide(Slide.Handedness.Right,Slide.Axis.Horizontal,deltaX));
				} else {
					callback.cb(new Slide(Slide.Handedness.Right,Slide.Axis.Vertical,deltaY));
				}
			}
		}
	}
	
	private boolean getGrabState(Frame.Handedness hand) {
		boolean noTrue = true;
		boolean noFalse = true;
		
		for(Frame frame : buffer) {
			if(frame.getHand(hand).grab == true) {
				noTrue = false;
			} else {
				noFalse = false;
			}
		}
		
		if(noTrue) {
			return false;
		}
		if(noFalse) {
			return true;
		}
		return grabbed.get(hand);
	}
}
