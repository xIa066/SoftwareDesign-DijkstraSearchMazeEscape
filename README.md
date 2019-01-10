# Dijkstra-implementation-for-maze
Learn the map and Make decision while Learning until Escape

# The Task
Your task is to design, implement, integrate and test a car autocontroller that is able to successfully traverse the
map and its traps.
It must be capable of safely:
1. exploring the map and locating the keys
2. retrieving all the required keys
3. making its way to the exit

A key element is that your design should be modular and extensible, clearly separating out elements of
behaviour/strategy that the autocontroller deploys (see Design Rationale below). In particular, consideration
should be given to additional trap types, and the potential to vary behaviour (possibly in a future version) in
circumstances where options are available. You will not be assessed on how fast-traversing your algorithms are,
just on the design in which they are embedded and on whether they work to get the car to the exit.

The package you will start with contains:
+ The simulation of the world and the car
+ The car controller interface
+ A car manual controller that you can use to drive around a map
+ A simple car autocontroller that can navigate a maze but ignores traps and can get stuck in a loop if the
map is not a ‘perfect maze’ (if it has circuits).
+ Sample maps (created using the map editor Tiled)

If, at any point, you have any questions about how the simulation should operate or the interface specifications
are not clear enough to you, it is your responsibility to seek clarification from your tutor, or via the LMS forum.
Please endeavour to do this earlier rather than later so that you are not held up in completing the project in the
final stages.

Following are more details on specific aspects of your task.

# Overview
After seeing your design report on the Automail simulation system, the Robotic Mailing Solutions Inc. (RMS)
software developers were outraged at your implied criticism of their design. Seeking revenge, they organised to
have you kidnapped, and abandoned in a dangerous location, with the expectation that you would not survive
your attempt to return to civilisation.

Awaking in a strange vehicle in an unfamiliar location surrounded by dangerous traps, you quickly realised that
attempting to drive out manually would soon prove fatal. However, the RMS developers did not account for your
software design and development skills. You quickly connected your laptop (conveniently left with you) to the
vehicle, coupled it with the vehicles sensors and actuators, and integrated your tailor-designed vehicle auto-drive
system. In no time at all (well, before the due date at least) you were on your way, the vehicle automatically
navigating its way across the map to safety, while avoiding+ the traps.

# The Map
The area you find yourself in is constructed in the form of a simple grid of tiles consisting only of:
+ Roads
+ Walls
+ Start (one only)
+ Exit (requires a set of keys to open)
+ Traps

Some roads may lead to dead-ends or be impassible due to traps and the health of your vehicle.

# The Vehicle
The car you find yourself in includes a range of sensors for detecting obvious properties such as the car’s speed
and direction, and actuators for accelerating, braking and turning. It has only one speed (in both forward and
reverse) and can only turn towards one of the four compass points. The car also includes a sensor that can
detect/see four tiles in all directions (that’s right, it can see through walls), ie. a 9x9 area around the car. The
car can be damaged by events such as running into walls, or through traps (see below). It has limited health; if
the car’s health hits zero, the RMS developers win and you lose. If, however, your software takes the car from
the start to a succesful exit, you win and live on to write more critical software design reports.

# The Traps
There are different types of traps which have different effects on your vehicle; the effect continues as long as the
vehicle is in the trap:
+ Lava: damages your vehicle, but holds the key to your escape (pun intended).

A lava trap can destroy your vehicle, resulting in failure. However, some of the lava traps contain a key; the only
way to retrieve the key is to enter the lava. You need a copy of each distinct key to make it out; the car has
1 slots for the keys so you know how many you need to collect. In stereotypical fashion, the RMS developers are
confident of their plan, so confident they may even have left duplicates of some keys.
+ Grass: bit slippery, so can’t steer. The grass won’t damage the car, but what comes after the grass might.
+ Mud: bit sticky. In fact, the car can’t drive out of it. So basically, drive on it and you lose.
+ Health: repairs your vehicle. The villains provided these just to give you false hope.

Some traps might be avoidable (i.e. can be by-passed), but others will have to be traversed (e.g. those which
contain keys) with some consideration as to the resulting damage and to where the car ends up.

# Initialization 
![alt text](https://raw.githubusercontent.com/xIa066/Dijkstra-implementation-for-maze/master/sample_picture(1).jpg)

# Ending
![alt text](https://raw.githubusercontent.com/xIa066/Dijkstra-implementation-for-maze/master/sample_picture(2).jpg)
