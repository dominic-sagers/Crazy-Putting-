package testing;

import bots.*;
import com.badlogic.gdx.math.Vector3;
import main.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class contains every method that is necessary to perform the testing on the influence of the errors on the bots performances.
 */
public class ErrorTesting {

    private static Ball ball;
    private static Goal goal;
    private static Vector3 previousBallPos;
    private static Vector3 initialBallPos;
    private static int tries;
    private static int scored;
    private static FileWriter file;
    private static PrintWriter writer;

    /**
     * Starts a new test for the errors. Creates a file containing the results. Might take some time
     * @param ball the Ball object
     * @param goal the Goal object
     */
    public static void testErrors(Ball ball, Goal goal){
        ErrorTesting.ball = ball;
        ErrorTesting.goal = goal;
        initialBallPos = ball.getPosition();

        System.out.println("Testing Configurations:");
        System.out.println("Bot Type: " + InputReader.getBotType());
        System.out.println("Error Type: " + InputReader.getErrorType());
        if(InputReader.getErrorType().equals("BallPosition")) System.out.println("BallPositionError: " + BotsGeneral.getBallPositionErrorBounds() + "\n");
        if(InputReader.getErrorType().equals("FoundVelocities")) System.out.println("FoundVelocitiesError: " + BotsGeneral.getVelocityErrorBounds() + "\n");
        System.out.println("Terrain: " + InputReader.getHeightProfile());
        System.out.println("Ball X: " + InputReader.getX0() + "\tBall Z: " + InputReader.getY0());
        System.out.println("Goal X: " + InputReader.getXt() + "\tGoal Z: " + InputReader.getYt());
        System.out.println("Static friction: " + InputReader.getMus() + "\tKinetic friction: " + InputReader.getMuk());
        System.out.println("ODE Solver: " + InputReader.getOdeSolver());
        System.out.println("Physics type: " + InputReader.getPhysicsType());
        System.out.println("\nStart Testing:\n");

        try {
            file = new FileWriter("desktop/src/testing/errorTestResult.csv", true);
            writer = new PrintWriter(file);
            writer.println("\n\n\n" +  "Bot Type: " + InputReader.getBotType());
            writer.println("Error Type: " + InputReader.getErrorType());
            if(InputReader.getErrorType().equals("BallPosition")) writer.println("BallPositionError: " + BotsGeneral.getBallPositionErrorBounds());
            if(InputReader.getErrorType().equals("FoundVelocities")) writer.println("FoundVelocitiesError: " + BotsGeneral.getVelocityErrorBounds());
            writer.println("Terrain: " + InputReader.getHeightProfile());
            writer.println("Ball X: " + InputReader.getX0() + "\tBall Z: " + InputReader.getY0());
            writer.println("Goal X: " + InputReader.getXt() + "\tGoal Z: " + InputReader.getYt());
            writer.println("Static friction: " + InputReader.getMus() + "\tKinetic friction: " + InputReader.getMuk());
            writer.println("ODE Solver: " + InputReader.getOdeSolver());
            writer.println("Physics type: " + InputReader.getPhysicsType());

            writer.println("Tries:" + "\tScored: ");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 1; i <= 100; i++) {
            System.out.println("Completed " + i + " runs");
            newTest();
        }
        writer.close();
    }

    /**
     * Creates a single test case
     */
    public static void newTest(){
        tries = 0;
        scored = 0;

        ball.setPosition(initialBallPos.x, initialBallPos.y, initialBallPos.z);

        previousBallPos = ball.getPosition();
        double[] startVelocities = newShot();
        ball.setPosition(previousBallPos.x, previousBallPos.y, previousBallPos.z);
        ball.setPhysics(startVelocities[0],startVelocities[1]);

        do{
            ball.engineBot();

            if(!Physics.getBallMoving()){
                if(Physics.isInTargetRegion(ball.getStateVector())){
                    scored = 1;
                }
                break;
            }
        } while(!Physics.isInTargetRegion(ball.getStateVector()));

        writer.println(tries + "\t" + scored);
    }

    /**
     * Creates a new shot based on the selected bot
     * @return double[] containing the starting X Velocity on index 0 and Z Velocity on index 1
     */
    public static double[] newShot(){
        Bot bot;
        switch(InputReader.getBotType()){
            case("Brute-Force"):
                bot = new BruteForceBot(ball);
                break;
            case("Rule-Based"):
                bot = new RuleBasedBot(ball, goal.getXPos(), goal.getZPos());
                break;
            case("Hill-Climbing"):
                bot = new HillClimbingBot(ball);
                break;
            case("Manhattan"):
                bot = new ManhattanBot(ball);
                break;
            case("Newton"):
                bot = new NewtonBot(ball);
                break;
            default: bot = new RandomBot(ball);
        }
        double[] startVelocities = bot.calculateStartVelocities();
        tries += bot.getTries();
        return startVelocities;
    }
}
