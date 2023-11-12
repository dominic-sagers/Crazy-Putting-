package bots;

import main.Ball;
import main.InputReader;
import main.Launch;
import main.Physics;

import java.util.ArrayList;
public class NewtonBot implements Bot{

    Ball ball;
    private double Vx;
    private double Vy;
    private double h = 1; // Step size changes dynamically.
    private double initialVx;
    private double initialVy;
    public int tries;
    private int counter = 0;
    private int numberOfRecursion;
    private double[] closestStopDistance = new double[3]; // In case there is no solution with one shot, that array contains best velocities.

    public NewtonBot(Ball ball){
        this.ball = ball;
        numberOfRecursion = 0;
        referenceVelocities();
    }

    /**
     * Newton-Raphson is a local optimization algorithm not a global optimization.So, in order to find
     * global minimum point, we must give a reference point that algorithms can reach global minimum point.
     * The inner for loop crates an array that contains velocity pairs. It arranges parameters according to
     * the ball ,and target location.
     * After having a list, simulates all of the pair and chooses the best one as a reference point.
     */
    public void referenceVelocities(){
        ArrayList<double[]> list = new ArrayList<>();
        double stepSizeForXvelocity = -1;
        double stepSizeForYvelocity = -1;
        double leftSideOfX = 4.9;
        double rightSideOfX = -0.1;
        double leftSideOfY = 4.9;
        double rightSideofY = -0.1;
        double initial_posX = ball.getXPos();
        double initial_posY = ball.getZPos();

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

            left = leftSideOfY;
            right = rightSideofY;

            leftSideOfY = -1*right;
            rightSideofY = -1*left;
        }
        for (double i = leftSideOfX; i >= rightSideOfX; i+=stepSizeForXvelocity){
            for (double j = leftSideOfY; j >= rightSideofY; j+=stepSizeForYvelocity){
                if(Math.sqrt(i*i+j*j) <= 5){ // Checks whether combination of velocities smaller than 5.
                    list.add(new double[]{i,j});
                }
            }
        }
        double closest = 0;
        for (int j = 0; j < list.size(); j++) {
            double distance = function(list.get(j)[0],list.get(j)[1])[2];
            if(closest == 0 || distance < closest){
                closest = distance;
                initialVx = list.get(j)[0];
                initialVy = list.get(j)[1];
            }
        }
        this.Vx = initialVx;
        this.Vy = initialVy;
    }


    /**
     * Calculates the initial velocities of the ball in order to score, using the Newton-Raphson Method
     * @return double[] containing the initial xVelocity on index [0] and zVelocity on index [1]
     */
    public double[] calculateStartVelocities() {
        tries = 0;
        //Introduce errors after calculation if selected
        if(InputReader.getErrorType().equals("FoundVelocities")){
            double[] velocities = newtonEngine(this.Vx,this.Vy);
            return BotsGeneral.addVelocityError(new double[]{velocities[0], velocities[1]});
        }
        return newtonEngine(this.Vx,this.Vy);
    }

    /**
     * That method is the recursive part of algorithm, iterates the Newton-Raphson until it finds solution
     * Takes the Vx,Vy ,and calculates the next velocities
     * @return double[] contains; the next Velocities of Vx ,and Vy
     */
    private double[] newtonEngine(double x , double y){
        numberOfRecursion++;
        if(function(x, y)[2] <= InputReader.getR()){
            counter++;
                if(Math.sqrt(x*x+y*y) <= 5){
                    return new double[]{x,y};
                }
                else{
                   h = h/2;
                   x = initialVx;
                   y = initialVy;
                }
            }
        if(numberOfRecursion ==5){
            numberOfRecursion = 0;
            return new double[]{closestStopDistance[1],closestStopDistance[2]};
        }
        return newtonEngine(x-(function(x, y)[0]/derivativeFunction(x, y, true)), y - (function(x, y)[1]/derivativeFunction(x, y, false)));
    }

    /**
     * That method is the function that we want to find root.
     * Takes the Vx,Vy ,and simulates the shoot with these velocities.
     * @return double[] contains; the absolute X distance at the closest point where the ball crossed.
     *                            the absolute Y distance at the closest point where the ball crossed,
     *                            the closest distance.
     */
    public double[] function(double x, double y){
        tries++;
        double initial_posX;
        double initial_posY;

        if(InputReader.getErrorType().equals("BallPosition")){
            double[] errorBallPos = BotsGeneral.addBallPositionError(ball.getXPos(), ball.getZPos());
            initial_posX = errorBallPos[0];
            initial_posY = errorBallPos[1];
        }
        else{
            initial_posX = ball.getXPos();
            initial_posY = ball.getZPos();
        }

        ball.setPhysics(x, y);
        double smallest = 0;
        double closestX = 0;
        double closestY = 0;
        double stopDistance;

        while(Physics.getBallMoving()){
            ball.engineBot();
            if(smallest == 0 || Math.sqrt((ball.getXPos()- Physics.Xt)*(ball.getXPos()- Physics.Xt) + (ball.getZPos()- Physics.Yt)*(ball.getZPos()- Physics.Yt)) < smallest){
                smallest = Math.sqrt((ball.getXPos()- Physics.Xt)*(ball.getXPos()- Physics.Xt) + (ball.getZPos()- Physics.Yt)*(ball.getZPos()- Physics.Yt));
                closestX = Physics.Xt - ball.getXPos();
                closestY = Physics.Yt - ball.getZPos();
            }
        }
        // For multiple shots, saves the best velocities that ball stops where closest to the target.
        if(!Physics.outOfMap(new double[]{ball.getXPos(),ball.getYPos()})){
            if(!Physics.hitTree(ball.getStateVector(), Launch.getTreeList())){
                if(ball.getYPos() >= 0){
                    if(Math.sqrt(x*x+y*y) <= 5){
                        stopDistance = Math.sqrt((ball.getXPos() - Physics.Xt)*(ball.getXPos() - Physics.Xt) + (ball.getZPos() - Physics.Yt)*(ball.getZPos() - Physics.Yt));
                        if(closestStopDistance[0] == 0 || stopDistance < closestStopDistance[0]){
                            closestStopDistance[0] = stopDistance;
                            closestStopDistance[1] = x;
                            closestStopDistance[2] = y;
                        }
                    }
                }
            }
        }

        ball.setPosition((float)initial_posX,0,(float)initial_posY);
        Physics.setBallMoving (true);
        return new double[]{closestX,closestY,smallest};
    }

    /**
     * Calculates the derivative of the function by Centered  Differences method.
     * If XorY is true, then calculates derivative of X.In other case, calculates derivative of Y.
     * @return double, derivative at that point.
     */
    public double derivativeFunction(double x, double y , boolean XorY){
        if(XorY){
            return (function(x+h, y)[0] - function(x-h, y)[0])/(2*h);
        }
        else{
            return (function(x, y+h)[1] - function(x, y-h)[1])/(2*h);
        }
    }

    @Override
    public int getTries() {
        return tries;
    }
}
