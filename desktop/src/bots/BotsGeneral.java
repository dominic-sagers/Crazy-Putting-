package bots;

import main.HeightFunction;
import main.Physics;

import java.util.concurrent.ThreadLocalRandom;

public class BotsGeneral {

    private static final double velocityErrorBounds = 0.3;
    private static final double ballPositionErrorBounds = 0.3;

    /**
     * Adds a random error to the velocities.
     * The error is based on the velocityErrorBounds field variable
     * -velocityErrorBounds < error < velocityErrorBounds
     * @param velocities the array containing the xVelocity on index 0 and the zVelocity on index 1
     * @return the array of velocities containing errors.
     * When the total velocity goes out of the bounds of 5.0 m/s, it recurs again until it finds a solution that is allowed.
     */
    public static double[] addVelocityError(double[] velocities){
        double errorXVelocity = velocities[0];
        double errorZVelocity = velocities[1];
        errorXVelocity += ThreadLocalRandom.current().nextDouble(-velocityErrorBounds, velocityErrorBounds);
        errorZVelocity += ThreadLocalRandom.current().nextDouble(-velocityErrorBounds, velocityErrorBounds);
        if(Math.sqrt(errorXVelocity*errorXVelocity + errorZVelocity*errorZVelocity) <= 5.0){
            return new double[]{errorXVelocity, errorZVelocity};
        }
        else return addVelocityError(velocities);
    }

    /**
     * Adds a random error to the initial ball position
     * The error is based on the ballPositionErrorBounds field variable
     * -ballPositionErrorBounds < error < ballPositionErrorBounds
     * @param ballXPos the X-Position of the ball
     * @param ballZPos the Z-Position of the ball
     * @return double[] containing the position of the ball with error with X on index 0 and Z on index 1
     */
    public static double[] addBallPositionError(float ballXPos, float ballZPos){
        double errorBallXPos = ballXPos;
        double errorBallZPos = ballZPos;
        errorBallXPos += ThreadLocalRandom.current().nextDouble(-ballPositionErrorBounds, ballPositionErrorBounds);
        errorBallZPos += ThreadLocalRandom.current().nextDouble(-ballPositionErrorBounds, ballPositionErrorBounds);

        if(!Physics.outOfMap(new double[]{errorBallXPos, errorBallZPos}) && HeightFunction.calculateHeight(errorBallXPos, errorBallZPos) >= 0){
            return new double[]{errorBallXPos, errorBallZPos};
        }
        else return new double[]{ballXPos, ballZPos};
    }

    public static double getVelocityErrorBounds() {
        return velocityErrorBounds;
    }

    public static double getBallPositionErrorBounds() {
        return ballPositionErrorBounds;
    }
}
