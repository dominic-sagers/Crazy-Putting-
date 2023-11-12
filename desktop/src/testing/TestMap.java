package testing;

import bots.HillClimbingBot;
import bots.ManhattanBot;
import bots.NewtonBot;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import main.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TestMap {
    public static void testHill() throws IOException {
        InputReader.setInputs(new File("assets/inputFile.txt"));
        Physics.loadInitialValues();

        int count;
        for (int i = 0; i <= 50; i+=1) {
            for (int j = 0; j <= 50; j+=1) {


                count = 0;
                ModelBuilder modelBuilder = new ModelBuilder();
                if(HeightFunction.calculateHeight((i-25)*0.4,(j-25)*0.4)<0){
                    continue;
                }

                float ballRadius = (float) Ball.getBallRadius();
                Model ballModel = modelBuilder.createSphere(ballRadius, ballRadius, ballRadius, 15, 15, new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
                Ball ball = new Ball(ballModel, InputReader.getX0(), (float) HeightFunction.calculateHeight(InputReader.getX0(), InputReader.getY0()) +InputReader.getR()/2+Terrain.getTerrainModelSize()/2, InputReader.getY0());
                ball.setXPos((float)((i-25)*0.4));
                ball.setZPos((float)((j-25)*0.4));
                ball.setYPos((float)HeightFunction.calculateHeight((i-25)*0.4,(j-25)*0.4));
                Vector3 previousPosition;
                HillClimbingBot bot;

                boolean didHit = false;
                while(!didHit&&count<100){


                    previousPosition = ball.getPosition();
                    FramesQueue.clear();

                    count+=1;
                    bot = new HillClimbingBot(ball);
                    double[] velocities = bot.calculateStartVelocities();
                    double[] stateVector = new double[]{previousPosition.x, previousPosition.z, velocities[0], velocities[1]};

                    stateVector[2] = velocities[0];
                    stateVector[3] = velocities[1];
                    Physics.setBallMoving(true);

                    ODESolvers.rungeKutta4Frames(stateVector,0.0004, 20);
                    double[] frame = FramesQueue.getLastOne();
                    ball.setXPos((float)frame[0]);
                    ball.setZPos((float)frame[1]);
                    ball.setYPos((float)HeightFunction.calculateHeight(ball.getXPos(),ball.getZPos()));

                    ball.setPhysics(velocities[0], velocities[1]);

                    if(Physics.isInTargetRegion(frame)){
                        didHit= true;
                    }


                }
                saveScore1(i, j, count);

            }
        }
    }
    public static void testNewt() throws IOException {
        InputReader.setInputs(new File("assets/inputFile.txt"));
        Physics.loadInitialValues();

        int count;
        for (int i = 0; i <= 50; i+=1) {
            for (int j = 0; j <= 50; j+=1) {


                count = 0;
                ModelBuilder modelBuilder = new ModelBuilder();
                if(HeightFunction.calculateHeight((i-25)*0.4,(j-25)*0.4)<0){
                    continue;
                }

                float ballRadius = (float) Ball.getBallRadius();
                Model ballModel = modelBuilder.createSphere(ballRadius, ballRadius, ballRadius, 15, 15, new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
                Ball ball = new Ball(ballModel, InputReader.getX0(), (float) HeightFunction.calculateHeight(InputReader.getX0(), InputReader.getY0()) +InputReader.getR()/2+Terrain.getTerrainModelSize()/2, InputReader.getY0());
                ball.setXPos((float)((i-25)*0.4));
                ball.setZPos((float)((j-25)*0.4));
                ball.setYPos((float)HeightFunction.calculateHeight((i-25)*0.4,(j-25)*0.4));
                Vector3 previousPosition;
                NewtonBot bot;

                boolean didHit = false;
                while(!didHit&&count<100){


                    previousPosition = ball.getPosition();
                    FramesQueue.clear();

                    count+=1;
                    bot = new NewtonBot(ball);

                    double[] velocities = bot.calculateStartVelocities();
                    double[] stateVector = new double[]{previousPosition.x, previousPosition.z, velocities[0], velocities[1]};

                    stateVector[2] = velocities[0];
                    stateVector[3] = velocities[1];
                    Physics.setBallMoving(true);

                    ODESolvers.rungeKutta4Frames(stateVector,0.0004, 20);
                    double[] frame = FramesQueue.getLastOne();
                    ball.setXPos((float)frame[0]);
                    ball.setZPos((float)frame[1]);
                    ball.setYPos((float)HeightFunction.calculateHeight(ball.getXPos(),ball.getZPos()));

                    ball.setPhysics(velocities[0], velocities[1]);

                    if(Physics.isInTargetRegion(frame)){
                        didHit= true;
                    }


                }
                saveScore3(i, j, count);

            }
        }
    }
    public static void testManh() throws IOException {
        InputReader.setInputs(new File("assets/inputFile.txt"));
        Physics.loadInitialValues();

        int count;
        for (int i = 0; i <= 50; i+=1) {
            for (int j = 0; j <= 50; j+=1) {


                count = 0;
                ModelBuilder modelBuilder = new ModelBuilder();
                if(HeightFunction.calculateHeight((i-25)*0.4,(j-25)*0.4)<0){
                    break;
                }

                float ballRadius = (float) Ball.getBallRadius();
                Model ballModel = modelBuilder.createSphere(ballRadius, ballRadius, ballRadius, 15, 15, new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
                Ball ball = new Ball(ballModel, InputReader.getX0(), (float) HeightFunction.calculateHeight(InputReader.getX0(), InputReader.getY0()) +InputReader.getR()/2+Terrain.getTerrainModelSize()/2, InputReader.getY0());
                ball.setXPos((float)((i-25)*0.4));
                ball.setZPos((float)((j-25)*0.4));
                ball.setYPos((float)HeightFunction.calculateHeight((i-25)*0.4,(j-25)*0.4));
                Vector3 previousPosition;
                ManhattanBot bot;

                boolean didHit = false;
                while(!didHit&&count<100){


                    previousPosition = ball.getPosition();
                    FramesQueue.clear();

                    count+=1;
                    bot = new ManhattanBot(ball);

                    double[] velocities = bot.calculateStartVelocities();
                    double[] stateVector = new double[]{previousPosition.x, previousPosition.z, velocities[0], velocities[1]};

                    stateVector[2] = velocities[0];
                    stateVector[3] = velocities[1];
                    Physics.setBallMoving(true);

                    ODESolvers.rungeKutta4Frames(stateVector,0.0004, 20);
                    double[] frame = FramesQueue.getLastOne();
                    ball.setXPos((float)frame[0]);
                    ball.setZPos((float)frame[1]);
                    ball.setYPos((float)HeightFunction.calculateHeight(ball.getXPos(),ball.getZPos()));

                    ball.setPhysics(velocities[0], velocities[1]);

                    if(Physics.isInTargetRegion(frame)){
                        didHit= true;
                    }


                }
                saveScore2(i, j, count);

            }
        }
    }
    public static void test() throws IOException {
        testManh();
        testNewt();
        testHill();

    }
    public static void saveScore3(double x, double y, int tries) throws IOException {
        FileWriter file = new FileWriter("desktop/src/testing/heatMapNewton3.txt", true);
        PrintWriter out = new PrintWriter(file);

        out.println((x-25)*0.4+","+(y-25)*0.4 + ","+tries+",");

        out.close();
    }
    public static void saveScore2(double x, double y, int tries) throws IOException {
        FileWriter file = new FileWriter("desktop/src/testing/heatMapManhatan3.txt", true);
        PrintWriter out = new PrintWriter(file);

        out.println((x-25)*0.4+","+(y-25)*0.4 + ","+tries+",");

        out.close();
    }
    public static void saveScore1(double x, double y, int tries) throws IOException {
        FileWriter file = new FileWriter("desktop/src/testing/heatMapHill3.txt", true);
        PrintWriter out = new PrintWriter(file);

        out.println((x-25)*0.4+","+(y-25)*0.4 + ","+tries+",");

        out.close();
    }
}