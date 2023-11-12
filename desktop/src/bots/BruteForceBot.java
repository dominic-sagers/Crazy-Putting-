package bots;

import main.Ball;
import main.InputReader;
import main.Physics;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This class contains the brute force bot that tries to find the initial velocities of the ball in order to score.
 * This bot is based on a brute force approach, meaning it will try all possible options in a systematic order.
 */
public class BruteForceBot implements Bot{

    private final Ball ball;
    private final double xStartPos;
    private final double zStartPos;
    private int tries = 0; //The amount of simulations that the bot performs before finding a solution
    private double totalRotation = 0;
    private double velocity = 0.1;
    private static final double rotationStepSize = 0.01;
    private static final double velocityChangeStepSize = 0.1;

    /**
     * Creates a new BruteForceBot object
     * @param ball the Ball object that the bot works on
     */
    public BruteForceBot(Ball ball){
        this.ball = ball;

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
     * Calculates the initial velocities of the ball in order to score, using a brute force approach.
     * @return double[] containing the initial xVelocity on index [0] and zVelocity on index [1]
     */
    public double[] calculateStartVelocities(){
        double pointerX = xStartPos + 0.6f; //+0.6 is the default length of the arrow
        double pointerZ = zStartPos + 0.6f; //+0.6 is the default length of the arrow
        double xLength = pointerX - xStartPos;
        double zLength = pointerZ - zStartPos;

        double velocityLength = Math.sqrt(xLength*xLength + zLength*zLength);
        double xVelocity = 0.1 * xLength / velocityLength;
        double zVelocity = 0.1 * zLength / velocityLength;

        ball.setPhysics(xVelocity, zVelocity);

        do{
            ball.engineBot();

            if(!Physics.getBallMoving()) {
                if(Physics.isInTargetRegion(ball.getStateVector())) break;

                tries++;

                ball.setXPos((float) xStartPos);
                ball.setZPos((float) zStartPos);

                totalRotation += rotationStepSize;

                pointerX = xStartPos + (float) ((pointerX - xStartPos) * Math.cos(rotationStepSize) - (pointerZ - zStartPos) * Math.sin(rotationStepSize));
                pointerZ = zStartPos + (float) ((pointerX - xStartPos) * Math.sin(rotationStepSize) + (pointerZ - zStartPos) * Math.cos(rotationStepSize));

                xLength = pointerX - xStartPos;
                zLength = pointerZ - zStartPos;

                if (totalRotation >= 2 * Math.PI) {
                    if (velocity + velocityChangeStepSize <= 5.0) {
                        pointerX = xStartPos + 0.6f; //reset direction to default
                        pointerZ = zStartPos + 0.6f;
                        totalRotation = 0;
                        velocity += velocityChangeStepSize;
                    } else {
                        System.out.println("Could not find a solution after " + tries + " tries");
                        return new double[]{999, 999};
                    }
                }

                velocityLength = Math.sqrt(xLength * xLength + zLength * zLength);
                xVelocity = velocity * xLength / velocityLength;
                zVelocity = velocity * zLength / velocityLength;

                ball.setPhysics(xVelocity, zVelocity);
            }
        }while(!Physics.isInTargetRegion(ball.getStateVector()));

        //Introduce errors after calculation if selected
        if(InputReader.getErrorType().equals("FoundVelocities")){
            return BotsGeneral.addVelocityError(new double[]{xVelocity, zVelocity});
        }

        return new double[]{xVelocity, zVelocity};
    }

    @Override
    public int getTries(){
        return tries;
    }
}
