import KinectPV2.*;
KinectPV2 kinect;
ArrayList<KSkeleton> skeletonArray;
import java.util.Queue;
import java.util.LinkedList;

//next frame of data
frameData nextFrame;


void setup() {
  size(512, 424, P3D);
  kinect = new KinectPV2(this);
  //kinect.enableBodyTrackImg(true);
  //kinect.enableDepthImg(true);
  //kinect.enableSkeletonColorMap(true);
  //kinect.enableSkeleton3DMap(true);
  kinect.enableSkeletonDepthMap(true);
  //kinect.enableDepthMaskImg(true);
  kinect.init();
  nextFrame = new frameData();
}

void draw() {

  background(0);

  // Before we deal with pixels
  loadPixels();  

  //raw body data 0-6 users 255 nothing
  //int [] rawData = kinect.getRawBodyTrack();
  skeletonArray = kinect.getSkeletonDepthMap(); 
  //skeletonArray = kinect.getSkeletonColorMap(); 
  //int [] rawDepthData = kinect.getRawDepthData();

  if (skeletonArray.size() > 0) {
    KSkeleton skeleton = (KSkeleton) skeletonArray.get(0);
    if (skeleton.isTracked()) {

      KJoint[] joints = skeleton.getJoints();
      PVector limb_len = PVector.sub(joints[KinectPV2.JointType_HandTipRight].getPosition(), joints[KinectPV2.JointType_ElbowRight].getPosition());
      PVector arm_len = PVector.sub(joints[KinectPV2.JointType_HandTipRight].getPosition(), joints[KinectPV2.JointType_ShoulderRight].getPosition());
      PVector shoul_len = PVector.sub(joints[KinectPV2.JointType_ShoulderRight].getPosition(), joints[KinectPV2.JointType_ShoulderLeft].getPosition());

      float add_length = pow(pow(limb_len.x, 2) + pow(limb_len.y, 2), 0.5);
      float add_arm_len = pow(pow(arm_len.x, 2) + pow(arm_len.y, 2), 0.5);
      float add_shoul_len = pow(pow(shoul_len.x, 2) + pow(shoul_len.y, 2), 0.5);

      float right_hand_Y = joints[KinectPV2.JointType_HandTipRight].getY();
      float right_hand_X = joints[KinectPV2.JointType_HandTipRight].getX();
      float left_hand_Y = joints[KinectPV2.JointType_HandTipLeft].getY();
      float left_hand_X = joints[KinectPV2.JointType_HandTipLeft].getX();
      float head_Y = joints[KinectPV2.JointType_Head].getY();    

      float base_spine_Y = joints[KinectPV2.JointType_SpineBase].getY();

      float hip_left_X = joints[KinectPV2.JointType_HipLeft].getX();
      float hip_right_X = joints[KinectPV2.JointType_HipRight].getX();

      float shoul_left_X = joints[KinectPV2.JointType_ShoulderLeft].getX();
      float shoul_right_X = joints[KinectPV2.JointType_ShoulderRight].getX(); 

      float bod_range = base_spine_Y - head_Y + add_length; //add length is the length of the arm
      float bod_range_X = add_arm_len + add_shoul_len;  //
      float bod_range_X2 = 2*add_arm_len + add_shoul_len;

      //righthand Y
      if (right_hand_Y <= head_Y - add_length) {
        nextFrame.percent_R = 1;
      } else if (right_hand_Y >= base_spine_Y) {
        nextFrame.percent_R = 0;
      } else {
        float y_rel_right_hand = base_spine_Y - right_hand_Y;
        nextFrame.percent_R = y_rel_right_hand/bod_range;
      }

      //lefthand Y
      if (left_hand_Y <= head_Y - add_length) {
        nextFrame.percent_L = 1;
      } else if (left_hand_Y >= base_spine_Y) {
        nextFrame.percent_L = 0;
      } else {
        float y_rel_left_hand = base_spine_Y - left_hand_Y;
        nextFrame.percent_L = y_rel_left_hand/bod_range;
      }

      //righthand X
      if (right_hand_X >= (shoul_left_X + bod_range_X)) {
        nextFrame.percent_R_X = 1;
      } else if (right_hand_X <= shoul_left_X - add_arm_len) {
        nextFrame.percent_R_X = 0;
      } else {
        //float y_rel_right_hand_X = right_hand_X - (shoul_left_X - add_arm_len);
        float rel_right_hand_X = right_hand_X - (hip_left_X - add_arm_len);
        nextFrame.percent_R_X = rel_right_hand_X/bod_range_X2;
      }

      //lefthand X
      if (left_hand_X <= shoul_right_X - bod_range_X) {
        nextFrame.percent_L_X = 1;
      } else if (left_hand_X >=  shoul_right_X + add_arm_len) {
        nextFrame.percent_L_X = 0;
      } else {
        float rel_left_hand_X = left_hand_X - (hip_right_X + add_arm_len); 
        nextFrame.percent_L_X = rel_left_hand_X/bod_range_X2;
      }

      nextFrame.close_R = checkHand(joints[KinectPV2.JointType_HandRight]);
      nextFrame.close_L = checkHand(joints[KinectPV2.JointType_HandLeft]);
      drawJoint(joints, KinectPV2.JointType_HandTipRight, nextFrame.close_R);
      drawJoint(joints, KinectPV2.JointType_HandTipLeft, nextFrame.close_L);
    }

    /*color newColor = color(0, 255, 0);
     for (int i = 0; i < rawData.length; i+=1) {
     if (rawData[i] != 255) {
     pixels[i] = newColor;
     }
     }*/

    // When we are finished dealing with pixels
    updatePixels();
  }
  //delay(100);
  //System.out.print("Hand Closed: "); 
  //System.out.print(nextFrame.close_R);  
  //System.out.print("  R_X: "); 
  //System.out.print(nextFrame.percent_R_X); 
  //System.out.print("  R_Y: "); 
  //System.out.println(nextFrame.percent_R);

  System.out.print("  L_X: ");
  System.out.println(nextFrame.percent_L_X);
  //System.out.println(percent_L);

  foo(nextFrame);
}


void drawJoint(KJoint[] joints, int jointType, boolean closed) {
  noStroke();
  pushMatrix();
  translate(joints[jointType].getX(), joints[jointType].getY(), joints[jointType].getZ());
  if (closed) fill(255, 0, 0);
  else fill(0, 0, 255);
  ellipse(0, 0, 25, 25);
  popMatrix();
}



//check handstate-----------------------------------------------------------------------------
boolean checkHand(KJoint hand) {
  return ((hand.getState() == KinectPV2.HandState_Closed) || (hand.getState() == KinectPV2.HandState_Lasso)); //|| (hand.getState() == KinectPV2.HandState_NotTracked)) ;
}

//attempting to debounce
boolean checkHand2(int bufferSize) {
  Queue<Boolean> q = new LinkedList<Boolean>();
  //Queue<Boolean> q = new LinkedList();
  while (true) {

    skeletonArray = kinect.getSkeletonColorMap(); 

    if (skeletonArray.size() > 0 ) {
      KSkeleton skeleton = skeletonArray.get(0);
      KJoint[] joints = skeleton.getJoints();
      
      boolean new_check = (joints[KinectPV2.JointType_HandLeft].getState() == KinectPV2.HandState_Closed) ||
        (joints[KinectPV2.JointType_HandLeft].getState() == KinectPV2.HandState_Lasso);

      q.add(new_check);

      if (q.size() > bufferSize) {
        q.remove();
        if (check_bool_queue(q)) {
          return q.peek();
        } else {
          for (boolean item : q) {
            System.out.print(item);
          }
          System.out.println();
        }
      }
    }
  }
}

boolean checkHand3L(int bufferSize) {

  Queue<Boolean> q = new LinkedList<Boolean>();
  //Queue<Boolean> q = new LinkedList();
  while (true) {
    q.add(checkHand2(bufferSize));
    if (q.size() > bufferSize) {
      q.remove();
      int check_val = check_bool_queue2(q);
      if (check_val == 1) {
        return q.peek();
      } else if (check_val == 2) {
        return !q.peek();
      }
      System.out.println();
    }
  }
}

//---------------------checking queue-------------------------------
boolean check_bool_queue(Queue<Boolean> bounce) {
  boolean temp = bounce.peek();

  for (boolean item : bounce) {
    if (item != temp) {
      return false;
    }
  }
  return true;
}

//try 2
int check_bool_queue2(Queue<Boolean> bounce) {
  int counter = 0;
  boolean temp = bounce.peek();

  for (boolean item : bounce) {
    if (item == temp) {
      counter++;
    }
  }

  if (counter > bounce.size()*0.8) {
    return 1;
  }

  if (counter < bounce.size()*0.2) {
    return 2;
  }

  return 0;
}
