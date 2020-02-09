package kinexote.sensor;

public class Frame {
	public class Hand {
		public final double x;
		public final double y;
		public final boolean grab;
		
		public Hand(double x, double y, boolean grab) {
			this.x = x;
			this.y = y;
			this.grab = grab;
		}
	}

	public enum Handedness {
		Left, Right
	}
	
	private final Hand L;
	private final Hand R;
	
	public Hand getHand(Handedness hand) {
		if(hand == Handedness.Right) {
			return R;
		}else {
			return L;
		}
	}
	
	public Frame(double lx, double ly, boolean lg, double rx, double ry, boolean rg) {
		this.L = new Hand(lx, ly, lg);
		this.R = new Hand(rx, ry, rg);
	}
}
