package kinexote.audio;

import java.util.ArrayList;

import ddf.minim.Minim;

public class SoundBoard {
	
	private Minim minim;

	public SoundBoard(Minim minim) {
		this.minim = minim;
	}
	
	public void play() {
		System.out.println("Play");
	}

	public void rec() {
		System.out.println("Rec");
	}

	public void stop() {
		System.out.println("Stop");
	}


	/**
	 * 
	 * @param gain 0-127
	 */
	public void setGain(int gain) {
		System.out.printf("Gain: %d\n", gain);
	}


	/**
	 * 
	 * @param reverb 0-127
	 */
	public void setReverb(int reverb) {
		System.out.printf("Reverb: %d\n", reverb);
	}

	/**
	 * 
	 * @param filt 0-127
	 */
	public void setFilt(int filt) {
		System.out.printf("Filt: %d\n", filt);
	}

	public ArrayList<Float> getWav() {
		ArrayList<Float> wav = new ArrayList<Float>();
		for (int i = 0; i < 1024; i++)
			wav.add((float) 0.5);
		return wav;
	}

	public ArrayList<Float> getFFT() {
		ArrayList<Float> wav = new ArrayList<Float>();
		for (int i = 0; i < 1024; i++)
			wav.add((float) 0.5);
		return wav;
	}
}
