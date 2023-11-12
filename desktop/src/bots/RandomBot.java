package bots;

import java.util.concurrent.ThreadLocalRandom;
import main.Ball;
import main.InputReader;
import main.Physics;

/**
 * This class contains the random bot that tries to find the initial velocities of the ball in order to score.
 * This bot is based on a random direction approach, meaning it will shoot in different directions randomly.
 */
public class RandomBot implements Bot{
    private final Ball ball;
    private final double xStartPos;
    private final double zStartPos;
    private int tries = 0;
    private double velocity = 0.1;
    private static final double velocityChangeStepSize = 0.1;

    /**
     * Creates a new RandomBot object
     * @param ball the Ball object that the bot works on
     */
    public RandomBot(Ball ball){
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
     * Calculates the initial velocities of the ball in order to score, using a random direction as approach.
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

            if(!Physics.getBallMoving()){
                if(Physics.isInTargetRegion(ball.getStateVector())) break;

                tries++;

                if(velocity + velocityChangeStepSize <= 5.0){
                    velocity += velocityChangeStepSize;
                }
                else{
                    //Choose a new direction and reset velocity
                    pointerX = xStartPos + ThreadLocalRandom.current().nextDouble(-1, 1);
                    pointerZ = zStartPos + ThreadLocalRandom.current().nextDouble(-1, 1);
                    xLength = pointerX - xStartPos;
                    zLength = pointerZ - zStartPos;
                    velocityLength = Math.sqrt(xLength*xLength + zLength*zLength);
                    velocity = 0.1;
                }

                ball.setXPos((float)xStartPos);
                ball.setZPos((float)zStartPos);

                xVelocity = velocity * xLength / velocityLength;
                zVelocity = velocity * zLength / velocityLength;

                ball.setPhysics(xVelocity, zVelocity);
            }
        } while(!Physics.isInTargetRegion(ball.getStateVector()));

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
