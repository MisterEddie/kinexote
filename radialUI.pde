float dummyAmp;
ArrayList<Integer> frequency;
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
void drawOrbit(float orbitRadius, color colour, float time, float sphereRadius, int dumbamp, boolean clockwise, boolean selected)
{
  float amp = random(0, 255);
  //ArrayList<Float> floatList = makeRandomFloatList();

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
      baller();
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
      baller();
      angle = angle + time;
    }
  }
}

void baller()
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
    
}

//creates dummy array of integers for frequency
ArrayList<Float> makeRandomFloatList()
{
  ArrayList<Float> list = new ArrayList<Float>();

  for (int i = 0; i < 10; i++)
  {
    list.add((random(0, 255)));
  }
  return list;
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


float changeInSlider(float initial, float end)
{
  return initial - end;
}
  

//sets up screen
void setup() {
  fullScreen(); 
  xcenter = displayWidth/2;
  ycenter = displayHeight/2;

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

//continuous drawing of orbitals
void draw() {
  background(0);
  drawOrbit(xcenter/2, color(255, 0, 0), .1, xcenter/14, 1, false, false);
  drawOrbit(xcenter/2.5, color(0, 255, 0), .1, xcenter/14, 1, true, true);
  drawOrbit(xcenter/3.5, color(0, 0, 255), .1, xcenter/14, 1, false, false);
  drawOrbit(xcenter/5, color(255, 255, 0), .1, xcenter/14, 1, true, false);
  drawDial(false, mouseX, mouseY);
  
}
