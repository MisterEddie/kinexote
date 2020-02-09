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

float dummyAmp;
float dummyTime;

float xcenter;
float ycenter;

float angle = 0;
void drawOrbit(float orbitRadius, color colour, float time, float sphereRadius, float effect, boolean clockwise, boolean selected)
{
  //float effects = random(0, 127);

  //creates orbit
  stroke(colour);   
  if (selected)
  {   
    strokeWeight(25);

    fill(color(0, 255, 255, 0));
    ellipse(xcenter, ycenter, orbitRadius*2, orbitRadius*2);

    //creates clockwise orbitals
    if (clockwise)
    {
      float x = (xcenter) + sin(angle) * orbitRadius;
      float y = (ycenter) - cos(angle) * orbitRadius;
      fill(colour);
      ellipse(x, y, sphereRadius, sphereRadius);
      angle = angle + time;
    } 

    //creates counterclockwise orbitals
    else
    {
      float x = (xcenter) + (sin(angle)) * orbitRadius;
      float y = (ycenter) + cos(angle) * orbitRadius;
      //fill(colour);
      spheres(sphereRadius, colour, x, y, effect);
      angle = angle + time;
    }
  } 
  
  
  else
  {   
    strokeWeight(1);  
    fill(color(0, 255, 255, 0));
    ellipse(xcenter, ycenter, orbitRadius*2, orbitRadius*2);
      //creates clockwise orbitals
      if (clockwise)
    {
      float x = (xcenter) + sin(angle) * orbitRadius;
      float y = (ycenter) - cos(angle) * orbitRadius;
      fill(red(colour), green(colour), blue(colour));
      ellipse(x, y, sphereRadius, sphereRadius);
      angle = angle + time;
    } 

    //creates counterclockwise orbitals
    else
    {
      float x = (xcenter) + (sin(angle)) * orbitRadius;
      float y = (ycenter) + cos(angle) * orbitRadius;
      //fill(colour);
      spheres(sphereRadius, colour, x, y, effect);
      angle = angle + time;
    }
  }
}

void spheres(float sphereRadius, color colour, float x, float y, float effect)
{
  fill(red(colour), green(colour), blue(colour));
  ellipse(x, y, sphereRadius+effect, sphereRadius-effect);
}

//creates dummy array of integers for frequency
color randoColor()
{
  return color(random(0, 255), random(0, 255), random(0, 255));
}

//sets up screen
void setup() {
  fullScreen(); 
  xcenter = displayWidth/2;
  ycenter = displayHeight/2;
  dummyTime = .01;
  minim = new Minim(this);
  in = minim.getLineIn(Minim.MONO, 2048);
  filePlayer = new FilePlayer( minim.loadFileStream( "./../myrecording.wav" ) );
  //sampler = new Sampler("myrecording.wav", 1, minim);
  out = minim.getLineOut(Minim.MONO);
  filePlayer.patch(out);
  filePlayer.loop();
  textFont(createFont("Arial", 12));
  fftAudio = minim.loadFile("./../myrecording.wav");
  fft = new FFT(1024, 44100);
  fft.window(FFT.HANN);
}

//continuous drawing of orbitals
void draw() {
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
      arc(xcenter,ycenter,250+fft.getBand(i/6)*50,250+fft.getBand((i+30)/6)*50,-angle,0);
      fill(0);
      ellipse(xcenter,ycenter,250,240);
      
  }
  
  stroke(255);
  for(int i = 0; i < in.bufferSize() - 1; i++)
  {
    line(i, 100 + in.left.get(i)*500,  i+1, 100 + in.left.get(i+1)*500);
    line(i, ycenter + in.left.get(i)*500,  i+1, ycenter + in.left.get(i+1)*500);
    line(i, displayHeight-100 + in.left.get(i)*500,  i+1, displayHeight-100  + in.left.get(i+1)*500);
  }
  
  
  color c1 =randoColor();
  color c2 =randoColor();
  color c3 =randoColor();
  color c4 =randoColor();
  
  drawOrbit(xcenter/2, c1, dummyTime, xcenter/14, 1, false, false);
  drawOrbit(xcenter/2.5, c2, dummyTime, xcenter/14, 1, true, false);
  drawOrbit(xcenter/3.5, c3, dummyTime, xcenter/14, 1, false, false);
  drawOrbit(xcenter/5, c4, dummyTime, xcenter/14, 1, true, false);
}
