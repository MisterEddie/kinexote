# kinexote
An innovative, multimedia product aimed to connecting body movements with visuals and sounds for an immersive experience.

## What it Does
Kinexote tracks arm movements and gestures to select and customize the audio you input to your liking—for example, applying reverb, filters, or different amounts of gain. The audio output then affects visuals on screen, from various amplitude representations to different colors and shapes based on frequency content.

## How We Built It
An Xbox Kinect was interfaced with the open source Processing programming platform to track the user's arm and hand position to alter the audio. We also tracked certain specific gestures, such as closing and opening the fist to start/stop recording audio. After the tracking data is received, the Minim library contained within Processing was used for Digital Signal Processing, e.g. manipulating sound clips that are inputted into the program. We used a Shure SM57 and Focusrite Scarlett 2i2 for the hardware to input audio.
