package kinexote.sensor;

public interface GestureRecognizer {
	public interface Callback<T> {
		void cb(T gesture);
	}
	
	public void newFrame(Frame inFrame);

}
