package bots;

import main.Ball;
import main.InputReader;
import main.Launch;
import main.Physics;

import java.util.ArrayList;
public class ManhattanBot implements Bot{

    public Ball ball; //Ball object that we will simulate, and find the result.
    public ArrayList<double[]> list; //List of velocity pairs. Every (double[]) object contains Vx(double),Vy(double) pairs.
    private int tries;
    double stepSizeForXvelocity;
    double stepSizeForYvelocity;
    double leftSideOfX;
    double rightSideOfX;
    double leftSideOfY;
    double rightSideofY;
    double initial_posX;
    double initial_posY;
    int numberOfRecursion;
    public double[] closestStopDistance = new double[3];


    public ManhattanBot(Ball ball){
        this.ball = ball;
        numberOfRecursion = 0;
        referenceVelocities();
    }

    /**
     * Manhattan bot is a local optimization algorithm not a global optimization.So, in order to find
     * global minimum point, we must give a reference point that algorithms can reach global minimum point.
     * The inner for loop crates an array that contains velocity pairs. It arranges parameters according to
     * the ball ,and target location.
     * After having a list, simulates all of the pair and chooses the best one as a reference point.
     */
    public void referenceVelocities(){
        stepSizeForXvelocity = -1;
        stepSizeForYvelocity = -1;
        leftSideOfX = 4.9;
        rightSideOfX = -0.1;
        leftSideOfY = 4.9;
        rightSideofY = -0.1;
        initial_posX = ball.getXPos();
        initial_posY = ball.getZPos();

        if(Physics.Xt - initial_posX < 0 && Physics.Yt - initial_posY > 0){
            double left = leftSideOfX;
            double right = rightSideOfX;

            leftSideOfX = -1*right;
            rightSideOfX = -1*left;
        }
        else if(Physics.Xt - initial_posX > 0 && Physics.Yt - initial_posY < 0){
            double left = leftSideOfY;
            double right = rightSideofY;

            leftSideOfY = -1*right;
            rightSideofY = -1*left;
        }
        else if(Physics.Xt - initial_posX < 0 && Physics.Yt - initial_posY < 0){
            double left = leftSideOfX;
            double right = rightSideOfX;

            leftSideOfX = -1*right;
            rightSideOfX = -1*left;

            leftSideOfY = -1*right;
            rightSideofY = -1*left;
        }
    }

    /**
     * Manhattan bot is our own unique algorithm. Tries to find hole in one shot recursively.
     * Manhattan bots.Bot aims to find the solution in smaller intervals ,and with smaller step size for each recursive iteration.
     * @param stepSizeForXvelocity step size of for loop for Vx.
     * @param stepSizeForYvelocity step size of for loop for Vy.
     * @param leftSideOfX left side of the for loop for Vx. (leftSideOfX)
     * @param rightSideOfX right side of the for loop for Vx. (rightSideOfX)
     * @param leftSideOfY left side of the for loop for Vy. (leftSideOfY)
     * @param rightSideOfY right side of the for loop for Vy. (rightSideOfY)
     * For now, initial parameters are;
     *              double stepSizeForXvelocity = -1;
     * 				double stepSizeForYvelocity = -1;
     * 				double leftSideOfX = 4.9;
     * 				double rightSideOfX = -0.1;
     * 				double leftSideOfY = 4.9;
     * 				double rightSideOfY = -0.1;
     * These parameters are obtained experimentally. And, the signs of parameters depends on target's ,and ball's positions.
     */
    public double[] botEngine(double stepSizeForXvelocity, double stepSizeForYvelocity, double leftSideOfX, double rightSideOfX, double leftSideOfY, double rightSideOfY){
        numberOfRecursion++;

        double[] initialVelocities = new double[2];
        list = new ArrayList<>();

        if(InputReader.getErrorType().equals("BallPosition")){
            double[] errorBallPos = BotsGeneral.addBallPositionError(ball.getXPos(), ball.getZPos());
            this.initial_posX = errorBallPos[0];
            this.initial_posY = errorBallPos[1];
        }
        else{
            this.initial_posX = ball.getXPos();
            this.initial_posY = ball.getZPos();
        }


        for (double Vx = leftSideOfX; Vx >= rightSideOfX; Vx+=stepSizeForXvelocity){
            for (double Vy = leftSideOfY; Vy >= rightSideOfY; Vy+=stepSizeForYvelocity){
                if(Math.sqrt(Vx*Vx+Vy*Vy) <= 5){ // Checks whether combination of velocities smaller than 5.
                    list.add(new double[]{Vx,Vy});
                }
            }
        }

        double velocityA = 0; // Vx for smallest distance that ball passed in each simulation.
        double velocity = 0; // Vy for smallest distance that ball passed in each simulation.
        double smallest; // The closest distance where the ball passed for one iteration.
        double closest = 0; // The closest distance where the ball passed for every iteration.
        double distance; // Parameter to check distance in every point.
        double stopDistance;

        for (double[] doubles : list) {
            ball.setPhysics(doubles[0], doubles[1]); // Sets initial velocities.
            smallest = 0;


            while (Physics.getBallMoving()) { // Simulates with given initial velocities till ball stop.
                ball.engineBot();
                distance = Math.sqrt((ball.getXPos() - Physics.Xt) * (ball.getXPos() - Physics.Xt) + (ball.getZPos() - Physics.Yt) * (ball.getZPos() - Physics.Yt));
                if (smallest == 0 || distance < smallest) { // Saves closest distance for one simulation.
                    smallest = distance;
                }
            }
            tries++;

            if(!Physics.outOfMap(new double[]{ball.getXPos(),ball.getYPos()})){
                if(!Physics.hitTree(ball.getStateVector(), Launch.getTreeList())){
                    if(ball.getYPos() > 0.2){
                        stopDistance = Math.sqrt((ball.getXPos() - Physics.Xt)*(ball.getXPos() - Physics.Xt) + (ball.getZPos() - Physics.Yt)*(ball.getZPos() - Physics.Yt));
                        if(closestStopDistance[0] == 0 || stopDistance < closestStopDistance[0]){
                            closestStopDistance[0] = stopDistance;
                            closestStopDistance[1] = doubles[0];
                            closestStopDistance[2] = doubles[1];
                        }
                    }
                }
            }


            if (closest == 0 || smallest < closest) { // Saves closest distance for every simulation.
                closest = smallest;
                velocityA = doubles[0];
                velocity = doubles[1];
            }

            if (Physics.isInTargetRegion(ball.getStateVector())) { // If the ball reaches target point. Returns velocities as an answer.
                velocityA = doubles[0];
                velocity = doubles[1];
                initialVelocities[0] = velocityA;
                initialVelocities[1] = velocity;
                ball.setPosition((float) initial_posX, 0, (float) initial_posY); // Sets position to initial position since it found the answer..
                Physics.setBallMoving(true);
                return initialVelocities;
            }
            else {
                ball.setPosition((float) initial_posX, 0, (float) initial_posY); // Sets position to initial position for next simulation.
                Physics.setBallMoving(true);
            }
        }

        if(numberOfRecursion == 5){
            numberOfRecursion = 0;
            initialVelocities[0] = closestStopDistance[1];
            initialVelocities[1] = closestStopDistance[2];
            return initialVelocities;
        }
        //Recursive Part:
        //If the closest is not enough to achieve it. Recursively, algorithm searches again with smaller step size from VelocityA(Vx),Velocity(Vy) that has the closest distance to target.
        initialVelocities  = botEngine(stepSizeForXvelocity/4, stepSizeForYvelocity/4, velocityA-stepSizeForXvelocity, velocityA+stepSizeForXvelocity, velocity-stepSizeForYvelocity, velocity+stepSizeForYvelocity);
        return initialVelocities;
    }

    /**
     * Calculates the initial velocities of the ball in order to score.
     * @return double[] containing the initial xVelocity on index [0] and zVelocity on index [1]
     */
    public double[] calculateStartVelocities() {

        //Introduce errors after calculation if selected
        if(InputReader.getErrorType().equals("FoundVelocities")){
            double[] velocities = botEngine(stepSizeForXvelocity, stepSizeForYvelocity, leftSideOfX, rightSideOfX, leftSideOfY, rightSideofY);
            return BotsGeneral.addVelocityError(new double[]{velocities[0], velocities[1]});
        }

        return botEngine(stepSizeForXvelocity, stepSizeForYvelocity, leftSideOfX, rightSideOfX, leftSideOfY, rightSideofY);
    }

    @Override
    public int getTries() {
        return tries;
    }
}

