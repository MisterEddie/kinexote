package kinexote.audio;

import java.util.ArrayList;

import ddf.minim.Minim;

import Math;

public class SoundBoard {
	
	Minim minim;
	AudioInput    in;
	AudioOutput   out;
	AudioRecorder recorder;
  	FilePlayer    filePlayer;
  	Sampler       sampler;
  	MultiChannelBuffer sampleBuffer;
  	Delay         delay1;
  	MoogFilter    filter1;
  	Gain          gain1;
  	Bypass        bypassedDelay;
  	Bypass        bypassedFilter;
  	Bypass        bypassedGain;

	public SoundBoard(Minim minim, AudioInput in, AudioOutput out, 
                AudioRecorder recorder, String fileName) {
	    this.minim = minim;
	    this.in = in;
	    this.out = out;
	    this.recorder = recorder;
	    this.fileName = fileName;
	    delay1 = new Delay( 0.07, 0.8, true, true );
	    filter1 = new MoogFilter(500, 0.5, MoogFilter.Type.LP);
	    gain1 = new Gain(5);
	    sampleBuffer = new MultiChannelBuffer( 1, 1024 );
	    sampler = new Sampler( sampleBuffer, 44100, 1 );
	    bypassedDelay = new Bypass<Delay>( delay1 );
	    bypassedDelay.activate();
	    bypassedFilter = new Bypass<MoogFilter>( filter1 );
	    bypassedFilter.activate();
	    bypassedGain = new Bypass<Gain>( gain1 );
	    bypassedGain.activate();
	    sampler.patch(bypassedDelay).patch(bypassedFilter).patch(bypassedGain).patch(this.out);
	  }
	
	public void play() {
		sampler.trigger();
		System.out.println("Play");
	}

	public void record() {
		if (recorder.isRecording() ) {
        	recorder.endRecord();
        	System.out.println("Stopped recording")
      	} else {
        	recorder.beginRecord();
        	System.out.println("Recording")
      	}
	}

	public void recordState() {
		if (recorder.isRecording()) {
	      	System.out.println("Currently recording...");
	    } else {
	      	System.out.println("Not recording.");
	    }
	}

	public void save() {
		recorder.save();
		float sampleRate = this.minim.loadFileIntoBuffer(fileName, sampleBuffer);
    	int correctBufferSize = sampleBuffer.getBufferSize();
    	sampler.setSample(sampleBuffer, sampleRate);
    	sampler.end.setLastValue(correctBufferSize-1);
		System.out.println("Done Saving");
	}


	/**
	 * 
	 * @param gain 0-127
	 */
	public void setGain(int gain) {
		gain /= 4.2333333333;
		gain -= 15;

		System.out.printf("Gain: %f dB\n", gain);
		gain1.setValue(gain);
	}


	/**
	 * 
	 * @param reverb 0-127
	 */
	public void setReverb(int reverb) {
		reverb /= 141.1;
		System.out.printf("Reverb: %f factor \n", reverb);
		delay1.setDelAmp(reverb)
	}

	/**
	 * 
	 * @param freq 0-127
	 */
	public void setFilterFreq(int freq) {
		freq = 20 * Math.pow(10, freq/42.4)
		System.out.printf("Cutoff Freq: %d Hz\n", freq);
		filter1.frequency.setLastValue(freq)
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


