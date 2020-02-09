float dummyAmp;
ArrayList<Integer> frequency;
float dummyTime;

float xcenter;
float ycenter;

float angle = 0;
void drawOrbit(float orbitRadius, color colour, float time, float sphereRadius, ArrayList<Float> lister, int dumbamp, boolean clockwise, boolean selected)
{
  float amp = random(0, 255);
  ArrayList<Float> floatList = makeRandomFLoatList();

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
      spheres(sphereRadius, amp, colour, x, y);
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
      spheres(sphereRadius, amp, colour, x, y);
      angle = angle + time;
    }
  }
}

void spheres(float sphereRadius, float amp, color colour, float x, float y)
{
  fill(red(colour), green(colour), blue(colour), amp);
  ellipse(x, y, sphereRadius, sphereRadius);
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

//sets up screen
void setup() {
  fullScreen(); 
  xcenter = displayWidth/2;
  ycenter = displayHeight/2;
  dummyTime = .01;
}

//continuous drawing of orbitals
void draw() {
  background(0);
  drawOrbit(xcenter/2, color(255, 0, 0), dummyTime, xcenter/14, 1, false, false);
  drawOrbit(xcenter/2.5, color(0, 255, 0), dummyTime, xcenter/14, 1, true, true);
  drawOrbit(xcenter/3.5, color(0, 0, 255), dummyTime, xcenter/14, 1, false, false);
  drawOrbit(xcenter/5, color(255, 255, 0), dummyTime, xcenter/14, 1, true, false);
}
