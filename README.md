How/Where I used AI:
Throughout development, AI served as a reference tool for Compose syntax and Navigation component APIs, 
helping me recall correct function signatures and parameter patterns while I focused on the overall architecture and user experience design.
I also used AI to help me generate initial boilerplate code for the basic screen layouts and data classes, which I then customized and refined to match my specific 
requirements for the Boston City Tour app. 

Where AI misunderstood navigation:

AI fundamentally misunderstood several critical navigation concepts that required me to debug, research, and implement proper solutions. 
The most significant issue was AI's complete failure to handle Int arguments in navigation properly; it consistently omitted the required NavType.
IntType declaration and attempted to extract integers using string methods, forcing me to research the Navigation documentation and implement the correct getInt() pattern with proper type declarations. 
