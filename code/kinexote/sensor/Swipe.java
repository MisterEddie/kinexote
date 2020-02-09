package kinexote.sensor;

public class Swipe {
	public enum Direction {
		Up, Down
	}

	public enum Handedness {
		Left, Right
	}

	public final Handedness hand;
	public final Direction direction;
	
	public Swipe(Handedness hand, Direction direction) {
		this.hand = hand;
		this.direction = direction;
	}
}
