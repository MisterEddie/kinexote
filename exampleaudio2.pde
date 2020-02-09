import ddf.minim.*;
import ddf.minim.ugens.*;
import ddf.minim.analysis.*;

Minim         minim;
AudioInput    in;
AudioOutput   out;
AudioRecorder recorder;
FilePlayer    filePlayer;
Sampler       sampler;
FFT  fft;
AudioPlayer fftAudio;
Oscil wave;

void setup()
{
  size(500, 500, P3D);
  minim = new Minim(this);
  in = minim.getLineIn(Minim.MONO, 2048);
  recorder = minim.createRecorder(in, "myrecording.wav");
  filePlayer = new FilePlayer( minim.loadFileStream( "myrecording.wav" ) );
  sampler = new Sampler("myrecording.wav", 1, minim);
  out = minim.getLineOut(Minim.MONO);
  filePlayer.patch(out);
  filePlayer.loop();
  textFont(createFont("Arial", 12));
  fftAudio = minim.loadFile("myrecording.wav");
  fft = new FFT(1024, 44100);
  fft.window(FFT.HANN);
}

void draw()
{
  background(0); 
  stroke(255, 20);
  //println(redVal);
  
  fft.forward(out.mix);
  //println(fft.specSize());
  
  for (int i = 0; i < fft.specSize(); i++){
      // draw the line for frequency band i, scaling it by 4 so we can see it a bit better
      //line(i, 100, i, 100 - fft.getBand(i) * 50);
      float angle = map(i, 0, fft.specSize(), 0, TWO_PI);
      float colour = map(i, 0, fft.specSize(), 0, 255);
      fill(colour,255-colour,colour-128,colour);
      arc(250,250,250+fft.getBand(i/6)*50,250+fft.getBand((i+30)/6)*50,-angle,0);
      fill(0);
      ellipse(250,250,250,240);
      
  }
  
  stroke(255);
  for(int i = 0; i < in.bufferSize() - 1; i++)
  {
    line(i, 100 + in.left.get(i)*500,  i+1, 100  + in.left.get(i+1)*500);
    line(i, 400 + in.left.get(i)*500,  i+1, 400  + in.left.get(i+1)*500);
  }
    /*
    float angle = map(i, 0, in.bufferSize(), 0, TWO_PI);
    fill(255,0,128,0);
    arc(250, 250, 250 + in.left.get(i)*50, 250 + in.left.get(i+1)*50, -angle, 0);
    fill(0);
    ellipse(250,250,200,190);
  }*/
}
