package kinexote.sensor;

public class Slide {
	public enum Axis {
		Horizontal, Vertical
	}

	public enum Handedness {
		Left, Right
	}

	public final Handedness hand;
	public final Axis axis;
	public final double value;
	
	public Slide(Handedness hand, Axis axis, double value) {
		this.hand = hand;
		this.axis = axis;
		this.value = value;
	}
}
