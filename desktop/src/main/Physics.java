package main;

import java.util.ArrayList;

public class Physics {

    private static final double g = 9.81; // Gravity

    private static double muk; // Kinetic friction (grass)
    private static double mus; // static friction (grass)
    public static double Xt; // x-position of target
    public static double Yt; // y-position of target
    private static double r; // target radius
    private static boolean ballMoving;


    /**
     * Loads the initial values from the inputReader
     */
    public static void loadInitialValues(){
        muk = InputReader.getMuk();
        mus = InputReader.getMus();
        Xt = InputReader.getXt();
        Yt = InputReader.getYt();
        r = InputReader.getR();

    }

    //TODO: Add documentation
    public static void loadInitialValuesTest(double mukSet, double musSet, double Xtset, double Ytset, double rset, String heightFunctionSet, double x0, double y0){
        muk = mukSet;
        mus = musSet;
        Xt = Xtset;
        Yt = Ytset;
        r = rset;
        InputReader.setInitialBallPos((float) x0,(float) y0);
        InputReader.setGoalPos((float)Xtset,(float)Ytset);
    }

    /**
     * Calculates the slope at the given coordinates.
     * @param coordination coordinates to be checked
     * @return Array of length 2
     */
    public static double[] calculateSlope(double[] coordination) {

        double[] Slope = new double[2];
        double x = coordination[0];
        double y = coordination[1];
        double z = HeightFunction.calculateHeight(x , y);

        // Step size constant
        double h = 0.00000001;
        Slope[0] = (-z +
                HeightFunction.calculateHeight(x + h, y)) /
                (h);

        Slope[1] = (-z +
                HeightFunction.calculateHeight(x, y + h)) /
                (h);

        return Slope;
    }

    /**
     * This method is used to calculate acceleration. It uses slope function to calculate the normal force and also calculates
     * friction.
     * @param vector to be calculated
     * @return acceleration in x direction and in y direction
     */
    public static double[] accelerationV1OldCalclation(double[] vector) {

        double[] acceleration = new double[2];
        double[] slope;
        slope = calculateSlope(vector);


        acceleration[0] = -g * slope[0] - muk * g * (vector[2] / Math.sqrt((Math.pow(vector[2], 2) + Math.pow(vector[3], 2))));
        acceleration[1] = -g * slope[1] - muk * g * (vector[3] / Math.sqrt((Math.pow(vector[2], 2) + Math.pow(vector[3], 2))));


        return acceleration;
    }
    /**
     * This method is used to calculate acceleration. It uses slope function to calculate the normal force and also calculates
     * friction.
     * @param vector to be calculated
     * @return acceleration in x direction and in y direction
     */
    public static double[] accelerationV2(double[] vector) {

        double[] acceleration = new double[2];
        double[] slope;
        slope = calculateSlope(vector);


        acceleration[0] = -(g * slope[0])/(calculateSloppiness(slope)) - (muk * g)/Math.sqrt(calculateSloppiness(slope)) * (vector[2]/(calculateFrictions(slope, vector)));
        acceleration[1] = -(g * slope[1])/(calculateSloppiness(slope)) - (muk * g)/Math.sqrt(calculateSloppiness(slope)) * (vector[3]/(calculateFrictions(slope, vector)));


        return acceleration;
    }
    /**
     * This method is used to calculate one part of new acceleration
     * @param slope to be calculated
     * @return part of acceleration
     */
    private static double calculateSloppiness(double[] slope){
        return (1+slope[0]*slope[0]+slope[1]*slope[1]);
    }
    /**
     * This method is used to calculate one part of new acceleration
     * @param slope to be calculated
     * @param vector where the ball is
     * @return part of acceleration
     */
    private static double calculateFrictions(double[] slope, double[] vector){
        return (Math.sqrt(Math.pow(vector[2], 2)+Math.pow(vector[3], 2)+Math.pow(slope[0]*vector[2]+slope[1]*vector[3], 2)));
    }



    /**
     * This method checks whether static friction is big enough to stop the ball
     * @param stateVector The stateVector of the ball
     * @return true if the static friction will stop the ball, false otherwise
     */
    public static boolean endGameFriction(double[] stateVector) {
        double[] slope = calculateSlope(stateVector);
        return mus >= Math.sqrt(slope[0] * slope[0] +
                slope[1] * slope[1]);
    }
    /**
     * Checks if the ball hit an obstacle during simulation.
     * @param stateVector the stateVector of the ball
     * @return true if a stopping condition has been met, false otherwise
     */
    public static boolean hitObstacle(double[] stateVector){
        if(outOfMap(stateVector)) return true;
        if(hitWater(stateVector)) return true;
        if(hitTree(stateVector, Launch.getTreeList())) return true;
        return false;
    }

    /**
     * Checks all the possible endings of a simulation to see if one is met
     * @param stateVector the stateVector of the ball
     * @return true if a stopping condition has been met, false otherwise
     */
    public static boolean endGame(double[] stateVector) {

        if(!ballMoving) return true;
        if(outOfMap(stateVector)) return true;
        if(hitWater(stateVector)) return true;
        if(hitTree(stateVector, Launch.getTreeList())) return true;

        return isInTargetRegion(stateVector);
    }

    /**
     * Checks whether the ball is within the region of the target
     * @param stateVector the stateVector of the ball
     * @return true if the ball is within the target region, false otherwise
     */
    public static boolean isInTargetRegion(double[] stateVector) {
        double firstExp = Math.pow(Xt - stateVector[0], 2);
        double secondExp = Math.pow(Yt - stateVector[1], 2);
        return r >= ((Math.sqrt(firstExp + secondExp)));
    }

    /**
     * Checks whether the ball has gone below y=0 (hit the water)
     * @param stateVector the stateVector of the ball
     * @return true if the height of the ball is below 0, false otherwise
     */
    public static boolean hitWater(double[] stateVector) {
        return (HeightFunction.calculateHeight(stateVector[0], stateVector[1]) < 0);
    }

    /**
     * Checks whether the ball is in contact with one of the trees
     * @param stateVector the stateVector of the ball
     * @param treeList the list of tree objects
     * @return true if the ball is in contact with one of the trees, false otherwise
     */
    public static boolean hitTree(double[] stateVector, ArrayList<Tree> treeList) {
        float xDistance;
        float zDistance;

        for (Tree tree : treeList) {
            //Check if the ball touches the tree
            xDistance = (float)stateVector[0] - tree.getXPos();
            zDistance = (float)stateVector[1] - tree.getZPos();
            if(Math.sqrt(xDistance * xDistance + zDistance * zDistance) <= Tree.getTreeTrunkRadius() + Ball.getBallRadius()){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the ball has gone outside the map's bounds
     * @param stateVector the stateVector of the ball
     * @return true if the ball is out of bounds and false if it's within.
     */
    public static boolean outOfMap(double[] stateVector){
        return stateVector[0] < -10 || stateVector[0] > 10 || stateVector[1] < -10 || stateVector[1] > 10;
    }

    public static void setBallMoving(boolean state){
        ballMoving = state;
    }

    public static boolean getBallMoving(){
        return ballMoving;
    }
}