package bots;

import main.Ball;
import main.InputReader;
import main.Physics;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
/*
Author: Dominic Sagers

        This Bot is based off of the basic principles of hill climbing. We generate our hills by shooting a small number of
    reference shots, then choose the shot which was closest to the goal in its path. The algorithm then hill climbs by choosing between increasing,
    decreasing, and rotating the velocity vector incrementally in the option which results in a closer shot to the goal, if in some
    iteration the shot is further than the goal, then we try another hill until all hills are exhausted. A specific hill is deemed failed when we have divided
    the step-size for the incremental change by a factor of two without improving the closest shot. We should find a goal within this first iteration of the 
    entire algorithm, if not, then we restart with a larger (more accurate) reference shot list up to a hardcoded amount of times until a solution is found.

*/

public class HillClimbingBot implements Bot {
    private Ball ball; // main.Ball object that we will simulate and find the result.
    private ArrayList<double[]> list; //List of velocity pairs. Every (double[]) object contains Vx(double),Vy(double) pairs.
    private ArrayList<double[]> attemptedHills;//List of possible solutions that have already been attempted.
    private int climbs; //Amount of times we have initiated a hill climb.
    private int zooms; //Amount of times we have expanded the search space of the referenece velocities (zoomed in :) )
    private int tries; //Amount of shots taken over the course of the entire algorithm.
    private final double STARTING_DIRSTEP = .5;
    private double dirStep = STARTING_DIRSTEP;//stepsize for the quadrant direction reference velocities
    private String shotMode = "multiple";//Set to "multiple" if the terrain is likely too complex for a one-shot solution
    private double[] bestShot; //A double[] of size 3 keeping track of the best distance-velocity pair over all iterations
    //The initial position of the ball when the algorithm is run.
    private double initial_posX; 
    private double initial_posY;


    /** Creates an HillClimbingBot instance
     * @param ball The Ball object the bot will use
     */
    public HillClimbingBot(Ball ball) {
        this.ball = ball;
    }


    /**
     *Algorithm Structure:
     * Part A: Create a list of possible shots based on referenceVelocities
     * Part B: Find the closest shot to the target within these possible shots
     * Part C: Use this shot to hill climb... Between increasing/decreasing and rotating the velocity vector, choose which choice brings the ball closest to the goal during or after its shot. Repeat until a solution is found.
     * Part D: If hill climbing fails AND we have not exhausted all shots, then expand the search space and start from Part A and try the next best shot.
     * Repeat until a solution is found
     * @param refVelType a string input which will decide how refrence velocities are made ("manhaattan" and "random" are valid inputs)
     * @return initialVelocities A double array containing the solution velocities.
     * */
    public double[] botEngine(String refVelType) {
        double[] initialVelocities = new double[2]; //The Velocities returned when the algorithm finishes
        attemptedHills = new ArrayList<double[]>(); //The list containing all attempts
        bestShot = new double[]{0,0,Double.MAX_VALUE}; //Instantiation of bestShot



        if(InputReader.getErrorType().equals("BallPosition")){
            double[] errorBallPos = BotsGeneral.addBallPositionError(ball.getXPos(), ball.getZPos());
            this.initial_posX = errorBallPos[0];
            this.initial_posY = errorBallPos[1];
        }
        else{
            this.initial_posX = ball.getXPos();
            this.initial_posY = ball.getZPos();
        }

        boolean failed = false;

        while (!failed) {//Condition which is broken if the algorithm has searched a hardcoded space of size
//Part A

            if (refVelType.equalsIgnoreCase("random")) {//Random directions and speed
                list = new ArrayList<double[]>();
                double numOfRandomVs = 25;

                if (zooms == 0 && climbs == 0) {
                    for (int i = 0; i < numOfRandomVs; i++) {
                        list.add(randomVelocity());
                    }
                } else if (climbs >= zooms * numOfRandomVs) {
                    list = new ArrayList<>();
                    for (int i = 0; i < numOfRandomVs; i++) {
                        list.add(randomVelocity());
                    }
                }
            } else if (refVelType.equalsIgnoreCase("quadrant")) {//Basic directions based on axis quadrants with step size dirStep
                this.list = new ArrayList<double[]>();
                double vectorScale = 2.5;
                for (double i = 1; i >= 0; i -= dirStep) {//quadrant 1
                    if (i != 1 && !hasBeenClimbed(shrinkVector(i, 1, vectorScale), attemptedHills)) {
                        list.add(shrinkVector(i, 1, vectorScale));
                    }
                    if(!hasBeenClimbed(shrinkVector(1, i, vectorScale), attemptedHills)) {
                        list.add(shrinkVector(1, i, vectorScale));
                    }
                }
                for (double i = 1; i >= 0; i -= dirStep) {//quadrant 3
                    if (i != 1 && !hasBeenClimbed(shrinkVector(-i, -1, vectorScale), attemptedHills)) {
                        list.add(shrinkVector(-i, -1, vectorScale));
                    }
                    if(!hasBeenClimbed(shrinkVector(-1, -i, vectorScale), attemptedHills)) {
                        list.add(shrinkVector(-1, -i, vectorScale));
                    }
                }
                for (double i = 1; i >= 0; i -= dirStep) {//quadrant 2
                    if (i != 1 && !hasBeenClimbed(shrinkVector(-i, 1, vectorScale), attemptedHills)) {
                        list.add(shrinkVector(-i, 1, vectorScale));
                    }
                    if(!hasBeenClimbed(shrinkVector(-1, i, vectorScale), attemptedHills)) {
                        list.add(shrinkVector(-1, i, vectorScale));
                    }
                }

                for (double i = 1; i >= 0; i -= dirStep) {//quadrant 4
                    if (i != 1 && !hasBeenClimbed(shrinkVector(i, -1, vectorScale), attemptedHills)){
                        list.add(shrinkVector(i, -1, vectorScale));
                    }
                    if(!hasBeenClimbed(shrinkVector(1, -i, vectorScale), attemptedHills)) {
                        list.add(shrinkVector(1, -i, vectorScale));
                    }
                }
                
            }


            double bestRefVx = 0; // Vx for smallest distance that ball passed in each simulation.
            double bestRefVy = 0; // Vy for smallest distance that ball passed in each simulation.
            double smallest; // The closest distance where the ball passed for one iteration.
            double closest = 0; // The closest distance where the ball passed for every iteration.
            double distance = 0; // Parameter to check distance in every point.
            double bestStoppedDistance = Double.MAX_VALUE;
            final double STARTING_STEP = 0.05;//Default stepsize for hill climbing.
            double step = STARTING_STEP; //Step by which we climb any hill incrementally

            //Part B
            for (double[] doubles : list) {
                ball.setPhysics(doubles[0], doubles[1]); // Sets initial velocities.
                smallest = 0;

                tries++;
                while (Physics.getBallMoving()) { // Simulates with given initial velocities till ball stop.
                    ball.engineBot();
                    distance = Math.sqrt((ball.getXPos() - Physics.Xt) * (ball.getXPos() - Physics.Xt) + (ball.getZPos() - Physics.Yt) * (ball.getZPos() - Physics.Yt));
                    if (smallest == 0 || distance < smallest) { // Saves closest distance for one simulation.
                        smallest = distance;
                    }
                }
                if(!Physics.hitObstacle(ball.getStateVector())){updateBestShot(doubles[0],doubles[1], distance);}

                //Check if a goal is scored in this process.
                if (Physics.isInTargetRegion(ball.getStateVector())) { // If the ball reaches target point. Returns velocities as an answer.
                    System.out.println("Solution found in reference velocities.");
                    bestRefVx = doubles[0];
                    bestRefVy = doubles[1];
                    initialVelocities[0] = bestRefVx;
                    initialVelocities[1] = bestRefVy;
                    ball.setPosition((float) initial_posX, 0, (float) initial_posY); // Sets position to initial position since it found the answer..
                    Physics.setBallMoving(true);
                    if(InputReader.getErrorType().equals("FoundVelocities")){
                        return BotsGeneral.addVelocityError(new double[]{initialVelocities[0], initialVelocities[1]});
                    }else{
                        return initialVelocities;
                    }
                } else {
                    ball.setPosition((float) initial_posX, 0, (float) initial_posY); // Sets position to initial position for next simulation.
                    Physics.setBallMoving(true);
                }
                //Saves closest distance for every simulation.
                //Hill climbing special edition: Also only passes only if the shot we are considering has not been used before
                if (((closest == 0 || smallest < closest) || distance < bestStoppedDistance) && (!hasBeenClimbed(doubles, attemptedHills))) {
                    if (distance < bestStoppedDistance) {
                        bestStoppedDistance = distance;
                    }
                    closest = smallest;
                    bestRefVx = doubles[0];
                    bestRefVy = doubles[1];
                }


                ball.setPosition((float) initial_posX, 0, (float) initial_posY);
            }
            //Once this loop concludes, we have our first/next hill to climb!


            //Part C


            attemptedHills.add(new double[]{bestRefVx, bestRefVy});
            boolean isStillHill = true;//Stopping condition wherein the stepsize is unreasonably small.
            //The velocities passed onto each iteration
            double nextVx = bestRefVx;
            double nextVy = bestRefVy;
            double distanceToHole = 0;//The best shortest distance to the goal radius over the whole iteration.
            double prevDistance = closest;//The closest distance from each previous iteration.
            bestStoppedDistance = Double.MAX_VALUE;
            double[] scaledVector; //Variable used when scaling a vector which has total velocity > 5
            int stepFactor = 2;

            while (isStillHill) {//Hill climb begins here
                int best = 69;//A variable keeping track of the best option to choose

                for (int i = 0; i < 4; i++) {//This loop simulates and compares all four shot types
                    if (!isStillHill) {
                        break;
                    }
                    double tempVx = 0;
                    double tempVy = 0;

                    switch (i) {
                        case 0://Rotate clockwise.
                            if (isTVLessThanOrEqualTo5(nextVx - step, nextVy + step)) {
                                tempVx = nextVx - step;
                                tempVy = nextVy + step;
                            } else {
                                scaledVector = shrinkVector(nextVx - step, nextVy + step, 5);
                                tempVx = scaledVector[0];
                                tempVy = scaledVector[1];
                            }
                            break;
                        case 1://Rotate counter-clockwise.
                            if (isTVLessThanOrEqualTo5(nextVx + step, nextVy - step)) {
                                tempVx = nextVx + step;
                                tempVy = nextVy - step;
                            } else {
                                scaledVector = shrinkVector(nextVx + step, nextVy - step, 5);
                                tempVx = scaledVector[0];
                                tempVy = scaledVector[1];
                            }
                            break;
                        case 2://Increase total velocity.
                            if (isTVLessThanOrEqualTo5(nextVx + step, nextVy + step)) {
                                tempVx = nextVx + step;
                                tempVy = nextVy + step;
                            } else {
                                scaledVector = shrinkVector(nextVx + step, nextVy + step, 5);
                                tempVx = scaledVector[0];
                                tempVy = scaledVector[1];
                            }
                            break;
                        case 3://Decrease total velocity
                            if (isTVLessThanOrEqualTo5(nextVx - step, nextVy - step)) {
                                tempVx = nextVx - step;
                                tempVy = nextVy - step;
                            } else {
                                scaledVector = shrinkVector(nextVx - step, nextVy - step, 5);
                                tempVx = scaledVector[0];
                                tempVy = scaledVector[1];
                            }
                            break;
                    }
                    if (!(tempVx == nextVx && tempVy == nextVy)) {//check if we changed the velocity

                        if(tempVx == 0 && tempVy == 0){tempVx+=.001;tempVy+=.001;}

                        ball.setPhysics(tempVx, tempVy);
                        //Shoot ball and find its shortest distance
                        double tempDist = 0;
                        tries++;

                        while (Physics.getBallMoving()) { // Simulates with given initial velocities till ball stop.
                            ball.engineBot();
                            tempDist = Math.sqrt((ball.getXPos() - Physics.Xt) * (ball.getXPos() - Physics.Xt) + (ball.getZPos() - Physics.Yt) * (ball.getZPos() - Physics.Yt));

                            if(Physics.isInTargetRegion(ball.getStateVector())&&(Math.sqrt(Math.pow(ball.getStateVector()[2],2)+Math.pow(ball.getStateVector()[3],2))<3)){
                                break;
                            }

                            if (distanceToHole == 0 || tempDist < distanceToHole) { // Saves closest distance for one simulation.
                                distanceToHole = tempDist;
                            }

                        }
                        if(!Physics.hitObstacle(ball.getStateVector())){updateBestShot(tempVx, tempVy, tempDist);}


                        if (Physics.isInTargetRegion(ball.getStateVector())) { // If the ball reaches target point. Returns velocities as an answer.
                            initialVelocities[0] = tempVx;
                            initialVelocities[1] = tempVy;
                            ball.setPosition((float) initial_posX, 0, (float) initial_posY); // Sets position to initial position since it found the answer..
                            Physics.setBallMoving(true);

                            if(InputReader.getErrorType().equals("FoundVelocities")){
                                return BotsGeneral.addVelocityError(new double[]{initialVelocities[0], initialVelocities[1]});
                            }else{
                                return initialVelocities;
                            }
                        } else {
                            ball.setPosition((float) initial_posX, 0, (float) initial_posY); // Sets position to initial position for next simulation.
                            Physics.setBallMoving(true);
                        }



                        if ((distanceToHole < prevDistance) || (tempDist < bestStoppedDistance)) {
                            prevDistance = distanceToHole;
                            if (tempDist < bestStoppedDistance) {
                                bestStoppedDistance = tempDist;
                            }
                            best = i;
                        } else if (distanceToHole == prevDistance && i == 3) {
                            best = 69;
                        }

                    }

                    switch (best) {//Based on the best shot, we set the shot for the next iteration of the hill climb.
                        case 0:
                            nextVx = nextVx - step;
                            nextVy = nextVy + step;
                            break;
                        case 1:
                            nextVx = nextVx + step;
                            nextVy = nextVy - step;
                            break;
                        case 2:
                            nextVx = nextVx + step;
                            nextVy = nextVy + step;
                            break;
                        case 3:
                            nextVx = nextVx - step;
                            nextVy = nextVy - step;
                            break;
                        case 69:
                            //If no options provide a better distance then we halve the stepsize, but if we have halved by a factor of stepFactor,
                            //abandon the hill.
                            if (step == STARTING_STEP * (1 / Math.pow(2, stepFactor))) {
                                isStillHill = false;
                            } else {
                                step = step / 2;
                            }
                            break;

                    }
                }
                step = STARTING_STEP;//Reset the stepsize to the default.
            }


            //Part D
            //No solution found, time to try a new hill :)
            climbs++;


            //If we have tried every shot/hill in the list, increase the amount of shots possible.
            switch (refVelType) {
                case "random":
                    if (zooms == 0) {
                        if (climbs == list.size() || climbs == list.size() * (zooms + 1)) {
                            zooms++;
                        }
                    }
                    break;
                case "quadrant":
                    if(list.size() == 0) {
                        zooms++;
                        dirStep /= 2;
                    }
                    break;
            }

            if(shotMode.equals("multiple") && zooms >= 1){
                if(!(bestShot[0]==0&&bestShot[1]==0)) {
                    initialVelocities[0] = bestShot[0];
                    initialVelocities[1] = bestShot[1];
                    if (InputReader.getErrorType().equals("FoundVelocities")) {
                        return BotsGeneral.addVelocityError(new double[]{initialVelocities[0], initialVelocities[1]});
                    } else {
                        return initialVelocities;
                    }
                }

            }

            if (zooms == 5) {//If we have hill climbed 5 times we abandon the algorithm
                if(shotMode.equals("multiple")){System.out.println("Could not find solution.");return new double[]{5,5};}
                System.out.println("Attempted to hill climb " + climbs + " times and could not find an answer");
                failed = true;
            }
        
        }
            //In the event of a failed algorithm we pass an empty shot.
        return new double[]{0,0};
    }

    /**
     * Calculates the initial velocities of the ball in order to score, using a hill climbing approach.
     * @return double[] containing the initial xVelocity on index [0] and zVelocity on index [1]
     */
    @Override
    public double[] calculateStartVelocities() {
        return botEngine("quadrant");
    }

    public boolean isTVLessThanOrEqualTo5(double vX, double vY){
        return !((Math.sqrt(Math.pow(vX, 2) + Math.pow(vY, 2))) > 5);
    }

    /**
     * This method will return a random velocity with total velocity smaller than 5;
     * @return double[] containing the X and Y of the new vector.
     */
    public static double[] randomVelocity(){

        boolean isValid = false;
        double randX = 0;
        double randY = 0;
        while(!isValid){
            randX = (Math.random() * 5);
            randY = (Math.random() * 5);

            if(Math.sqrt(randX * randX + randY * randY) <= 5){
                isValid = true;
            }
        }

        return new double[]{randX, randY};
    }

    /**
     * This method is used for during caluclation error, it takes a velocity vector's x/y componenets and adds a random value between a range of numbers to simulate error.
     * @param velocity1 The x component of the vector.
     * @param velocity2 The y componenet of the vector.
     * @return double[] containing the new x/y components with added error.
     */
    public double[] getRandomErrorOffset(double velocity1, double velocity2){
        double max = 0.25;
        double min = -0.25;
        while(true){
            double newV1 = velocity1 + ThreadLocalRandom.current().nextDouble(-0.25, 0.25);
            double newV2 = velocity2 + ThreadLocalRandom.current().nextDouble(-0.25, 0.25);

            if(Math.sqrt(newV1 * newV1 + newV2 * newV2) <= 5){
                return new double[]{newV1, newV2};
            }
        }
    }

    /**
     * This method shrinks a vector with a total velocity >= 5 to 5 by calculating its unit vector and scaling it down to equal 5
     * @param vX The x component of the vector being calculated.
     * @param vY The 7 component of the vector being calculated.
     * @return A double[] containing the new scaled velocities.
     */
    public double[] shrinkVector(double vX, double vY, double scale){
        if(vX == 0){
            vX+=.001;
        }
        if(vY == 0){
            vY +=.001;
        }
        if(vX==vY){vX+=.001;}
        double magnitude = Math.sqrt(Math.pow(vX,2)+Math.pow(vY,2));

        double newVx = (vX/magnitude)*scale;
        double newVy = (vY/magnitude)*scale;
        return new double[]{newVx, newVy};
    }

    public void updateBestShot(double vX, double vY,double stoppedDistance){
        if(stoppedDistance<bestShot[2]){
            bestShot[0] = vX;
            bestShot[1] = vY;
            bestShot[2] = stoppedDistance;
        }
    }


    public boolean hasBeenClimbed(double[] hill, ArrayList<double[]> attemptedHills) {
        for (int i = 0; i < attemptedHills.size(); i++) {
            if ((hill[0] == attemptedHills.get(i)[0]) && (hill[1] == attemptedHills.get(i)[1])) {
                return true;
            }
        }
        return false;
    }


    @Override
    public int getTries() {
        return tries;
    }
}
