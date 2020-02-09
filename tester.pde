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

float xcenter;
float ycenter;

float angle = 0;
void drawOrbit(float orbitRadius, color colour, float time, float sphereRadius,  boolean clockwise, boolean selected)
{
  float amp = random(0, 255);
  
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
      baller( x, y, colour);
      angle = angle + time;
    }
  } else
  {   
    strokeWeight(1);  
    fill(color(0, 255, 255, 0));
    ellipse(xcenter, ycenter, orbitRadius*2, orbitRadius*2);
    //creates clockwise orbitals
    if (clockwise)
    {
      float x = (xcenter) + sin(angle) * orbitRadius;
      float y = (ycenter) - cos(angle) * orbitRadius;
      fill(red(colour), green(colour), blue(colour), 50);
      ellipse(x, y, sphereRadius, sphereRadius);
      angle = angle + time;
    } 

    //creates counterclockwise orbitals
    else
    {
      float x = (xcenter) + (sin(angle)) * orbitRadius;
      float y = (ycenter) + cos(angle) * orbitRadius;
      //fill(colour);
      baller( x, y, colour);
      angle = angle + time;
    }
  }
}

void spheres(float sphereRadius, color colour, float x, float y)
{
  fill(red(colour), green(colour), blue(colour));
  ellipse(x, y, sphereRadius, sphereRadius);
}

//creates radial dial
void drawDial(boolean fistClosed, float x, float y)
{
  if (fistClosed)
  {
    fill(255);
    ellipse(x, y, 25, 25);
  }
  
  else
  {
  }
  
}

void baller(float x, float y, color colour )
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
      fill(colour);
      arc(xcenter,ycenter,250+fft.getBand(i/6)*50,250+fft.getBand((i+30)/6)*50,-angle,0);
      //fill(0);
      //ellipse(250,250,250,240);
      
  }
  
  stroke(255);
  for(int i = 0; i < in.bufferSize() - 1; i++)
  {
    line(i, 100 + in.left.get(i)*500,  i+1, 100  + in.left.get(i+1)*500);
    line(i, 400 + in.left.get(i)*500,  i+1, 400  + in.left.get(i+1)*500);
  }
    
}

//sets up screen
void setup() {
  //fullScreen(); 
  size(1000, 1000, P3D);
  xcenter = displayWidth/2;
  ycenter = displayHeight/2;

  minim = new Minim(this);
  in = minim.getLineIn(Minim.MONO, 2048);
  recorder = minim.createRecorder(in, "./../myrecording.wav");
  filePlayer = new FilePlayer( minim.loadFileStream( "./../myrecording.wav" ) );
  sampler = new Sampler("myrecording.wav", 1, minim);
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
  drawOrbit(xcenter/2, color(255, 0, 0), .1, xcenter/14, false, false);
  drawOrbit(xcenter/2.5, color(0, 255, 0), .1, xcenter/14,  true, true);
  drawOrbit(xcenter/3.5, color(0, 0, 255),  .1, xcenter/14,  false, false);
  drawOrbit(xcenter/5, color(255, 255, 0), .1, xcenter/14,  true, false);
  drawDial(false, mouseX, mouseY);
  
}
