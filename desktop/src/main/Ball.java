package main;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

/**
 * This class is in charge of the creation and movements of the ball
 */
public class Ball extends ModelInstance {
    private float xPos;
    private float yPos;
    private float zPos;
    private double[] stateVector;
    private double XVelocity;
    private double ZVelocity;
    private static final double ballRadius = 0.05;

    /**
     * Creates a new ball object
     * @param model the model for the ball containing the color, size etc.
     * @param x the x-position of the ball
     * @param y the y-position of the ball
     * @param z the z-position of the ball
     */
    public Ball(Model model, float x, float y, float z) {
        super(model, x, y, z);
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
        stateVector = new double[5];
    }

    /**
     * Initialize physics with initial velocities
     * @param xStartVelocity the initial velocity in the x-direction
     * @param zStartVelocity the initial velocity in the z-direction
     */
    public void setPhysics(double xStartVelocity, double zStartVelocity){
        XVelocity = xStartVelocity;
        ZVelocity = zStartVelocity;
        stateVector[0] = getXPos();
        stateVector[1] = getZPos();
        stateVector[2] = xStartVelocity;
        stateVector[3] = zStartVelocity;
        stateVector[4] = 0;
        Physics.setBallMoving(true);
    }
    /**
     * Resets the state vector to the correct state with no velocities
     */
    public void resetStateVector(){
        stateVector[0] = getXPos();
        stateVector[1] = getZPos();
        stateVector[2] = 0;
        stateVector[3] = 0;
        stateVector[4] = 0;
    }

    /**
     * Simulates the movement of the ball with a low step size. Is used to visualize the movement of the ball.
     */
    public synchronized void engine(){
        if(!FramesQueue.isEmpty()||!FramesQueue.isDone) {
            if(FramesQueue.isEmpty()){
                return;
            }

            stateVector = FramesQueue.poll();
            setXPos((float) stateVector[0]);
            setZPos((float) stateVector[1]);
            setYPos((float) HeightFunction.calculateHeight(stateVector[0], stateVector[1]));
            if(FramesQueue.isEmpty()&&FramesQueue.isDone){
                FramesQueue.clear();
                Physics.setBallMoving(false);
            }
            if (getYPos() < 0) {
                Physics.setBallMoving(false);
            } else {
                setYPos(getYPos() + InputReader.getR() / 2 + Terrain.getTerrainModelSize() / 2);
                if(FramesQueue.isEmpty()&&FramesQueue.isDone){
                    Physics.setBallMoving(false);
                    FramesQueue.clear();
                }
            }
        }
        else{
            FramesQueue.clear();

            Physics.setBallMoving(false);
            Launch.setCurrentState(1);
        }
    }

    /**
     * Simulates the movement of the ball with a higher step size. Is used by the bots to simulate the trajectory of the ball.
     */
    public void engineBot(){
        if(Physics.getBallMoving()) {

            switch (InputReader.getOdeSolver()) {
                case "Euler": {
                    ODESolvers.eulerCalculation(stateVector, 0.0004, 0.016666666 - stateVector[4]);
                    break;
                }
                case "Runge2": {
                    ODESolvers.rungeKutta2(stateVector, 0.0004, 0.016666666 - stateVector[4]);
                    break;
                }
                case "Runge4": {
                    ODESolvers.rungeKutta4(stateVector, 0.0004, 0.016666666 - stateVector[4]);
                    break;
                }
            }

            setXPos((float) stateVector[0]);
            setZPos((float) stateVector[1]);
            setYPos((float) HeightFunction.calculateHeight(stateVector[0], stateVector[1]));

            if (getYPos() < 0) {
                Physics.setBallMoving(false);
            } else {
                setYPos(getYPos() + InputReader.getR() / 2 + Terrain.getTerrainModelSize() / 2);
            }
        }
    }

    public Vector3 getPosition(){
        return this.transform.getTranslation(new Vector3());
    }

    public void setPosition(float xPos, float yPos, float zPos){
        setXPos(xPos);
        setYPos(yPos);
        setZPos(zPos);
    }
    public void setXPos(float xPos) {
        this.transform.translate(-this.xPos+xPos, 0f, 0f);
        this.xPos = xPos;
    }
    public void setYPos(float yPos) {
        this.transform.translate(0f, -this.yPos+yPos, 0f);
        this.yPos = yPos;
    }
    public void setZPos(float zPos) {
        this.transform.translate(0f, 0f, -this.zPos+zPos);
        this.zPos = zPos;
    }
    public float getXPos() {
        return this.xPos;
    }
    public float getYPos() {
        return this.yPos;
    }
    public float getZPos() {
        return this.zPos;
    }
    public double[] getStateVector(){
        return stateVector;
    }
    public double getXVelocity() {
        return XVelocity;
    }
    public double getZVelocity() {
        return ZVelocity;
    }

    public static double getBallRadius() {
        return ballRadius;
    }
}
