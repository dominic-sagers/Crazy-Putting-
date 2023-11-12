package bots;

import main.Ball;
import main.InputReader;
import main.Physics;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This class contains the rule based bot that tries to find the initial velocities of the ball in order to score.
 * This bot is based on a rule based approach, meaning it will change the initial velocities, based on a conditions.
 */
public class RuleBasedBot implements Bot{

    private double closestXDistance = Double.MAX_VALUE;
    private double closestZDistance = Double.MAX_VALUE;
    private double closestXPos = Double.MAX_VALUE;
    private double closestZPos = Double.MAX_VALUE;
    private double xLength;
    private double zLength;
    private final Ball ball;
    private final double goalXPos;
    private final double goalZPos;
    private final double xStartPos;
    private final double zStartPos;
    private static final double directionChangeStepSize = 0.005;
    private int tries;

    /**
     * Creates a new RuleBasedBot object
     * @param ball the Ball object that the bot works on
     * @param goalXPos the xPosition of the goal
     * @param goalZPos the zPosition of the goal
     */
    public RuleBasedBot(Ball ball, double goalXPos, double goalZPos){
        this.ball = ball;
        this.goalXPos = goalXPos;
        this.goalZPos = goalZPos;

        //Add an error the initial ball position if selected
        if(InputReader.getErrorType().equals("BallPosition")){
            double[] errorBallPos = BotsGeneral.addBallPositionError(ball.getXPos(), ball.getZPos());
            this.xStartPos = errorBallPos[0];
            this.zStartPos = errorBallPos[1];
        }
        else{
            this.xStartPos = ball.getXPos();
            this.zStartPos = ball.getZPos();
        }
    }

    /**
     * Calculates the initial velocities of the ball in order to score, using a rule based approach.
     * @return double[] containing the initial xVelocity on index [0] and zVelocity on index [1]
     */
    public double[] calculateStartVelocities(){
        xLength = goalXPos - xStartPos;
        zLength = goalZPos - zStartPos;
        double velocityLength = Math.sqrt(xLength*xLength + zLength*zLength);
        double xVelocity = 5.0 * xLength / velocityLength;
        double zVelocity = 5.0 * zLength / velocityLength;
        double[] newVelocities;

        ball.setPhysics(xVelocity, zVelocity);
        tries++;

        do{
            ball.engineBot();
            storeClosestPosition();

            if(!Physics.getBallMoving()){
                if(Physics.isInTargetRegion(ball.getStateVector())) break;

                tries++;

                newVelocities = getAdjustedVelocities();
                xVelocity = newVelocities[0];
                zVelocity = newVelocities[1];

                ball.setXPos((float)xStartPos);
                ball.setZPos((float)zStartPos);
                ball.setPhysics(xVelocity, zVelocity);
            }
        }while(!Physics.isInTargetRegion(ball.getStateVector()));

        //Introduce errors after calculation if selected
        if(InputReader.getErrorType().equals("FoundVelocities")){
             return BotsGeneral.addVelocityError(new double[]{xVelocity, zVelocity});
        }

        return new double[]{xVelocity, zVelocity};
    }

    public double[] referenceVelocities(){
        double pointerX = xStartPos + 0.6f; //+0.6 is the default length of the arrow
        double pointerZ = zStartPos + 0.6f; //+ is the default length of the arrow
        double xLength;
        double zLength;

        double velocityLength;
        double xVelocity;
        double zVelocity;

        final int directions = 8;

        double[] closestVelocities = new double[]{Double.MAX_VALUE, Double.MAX_VALUE};
        double closestDistance = Double.MAX_VALUE;

        for(int i = 0; i < directions; i++){
            pointerX = xStartPos + (float) ((pointerX - xStartPos) * Math.cos(2*Math.PI/directions) - (pointerZ - zStartPos) * Math.sin(2*Math.PI/directions));
            pointerZ = zStartPos + (float) ((pointerX - xStartPos) * Math.sin(2*Math.PI/directions) + (pointerZ - zStartPos) * Math.cos(2*Math.PI/directions));

            xLength = pointerX - xStartPos;
            zLength = pointerZ - zStartPos;

            velocityLength = Math.sqrt(xLength*xLength + zLength*zLength);
            xVelocity = 2.5 * xLength / velocityLength;
            zVelocity = 2.5 * zLength / velocityLength;
            ball.setPhysics(xVelocity, zVelocity);
            tries++;

            do{

                ball.engineBot();
                storeClosestPosition();

            }while(Physics.getBallMoving());

            if(Math.sqrt(closestXDistance*closestXDistance + closestZDistance*closestZDistance) < closestDistance){
                closestDistance = closestXDistance*closestXDistance + closestZDistance*closestZDistance;
                closestVelocities[0] = xVelocity;
                closestVelocities[1] = zVelocity;
            }

            ball.setXPos((float)xStartPos);
            ball.setZPos((float)zStartPos);
        }
        System.out.println("XVel: " + closestVelocities[0] + "\nZVel: " + closestVelocities[1]);
        return closestVelocities;
    }

    /**
     * Checks if the current X and Z positions of the ball are closer than the stored ones and updates them if this is the case
     */
    public void storeClosestPosition(){
        if(Math.abs(goalXPos-ball.getXPos()) < closestXDistance){
            closestXDistance = Math.abs(goalXPos-ball.getXPos());
            closestXPos = ball.getXPos();
        }
        if(Math.abs(goalZPos-ball.getZPos()) < closestZDistance){
            closestZDistance = Math.abs(goalZPos-ball.getZPos());
            closestZPos = ball.getZPos();
        }
    }

    /**
     * Adjusts the velocities based on the closest X and Z positions of the ball after a simulation
     * @return double[] containing the updated xVelocity on index [0] and updated zVelocity on index [1]
     */
    public double[] getAdjustedVelocities(){

        if(closestXPos < goalXPos){ //-,?
            xLength += directionChangeStepSize;
        }
        else if(closestXPos > goalXPos){ //+,?
            xLength -= directionChangeStepSize;
        }
        if(closestZPos < goalZPos){ //?,-
            zLength += directionChangeStepSize;
        }
        else if(closestZPos > goalZPos){ //?,+
            zLength -= directionChangeStepSize;
        }

        double velocityLength = Math.sqrt(xLength*xLength + zLength*zLength);
        double xVelocity = 5.0 * xLength / velocityLength;
        double zVelocity = 5.0 * zLength / velocityLength;

        return new double[]{xVelocity, zVelocity};
    }

    @Override
    public int getTries() {
        return tries;
    }
}
