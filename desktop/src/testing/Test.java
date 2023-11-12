package testing;

import main.ODESolvers;
import main.Physics;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.io.IOException;

public class Test {
    public static double timer = 0;
    public static void test (float x0, float y0, float Vx, float Vy, float muk, float mus, float Xt, float Yt, float r, String function, String whichAlgorithm) throws IOException {
        Physics.loadInitialValuesTest(muk, mus, Xt, Yt, r, function, x0, y0);

        if(true){
            Physics.loadInitialValuesTest(muk, mus, Xt, Yt, r, function, x0, y0);

            double[] vector = new double[]{x0,y0,Vx,Vy,0};

            while (timer<16) {
                saveScore1(vector,"desktop/src/ScoresRunge.txt", timer);

                if (Physics.getBallMoving()) {
                    break;
                }

                ODESolvers.rungeKutta4(vector, 0.0004, 0.0004 - vector[4]);

                timer+=0.0004;
            }
            saveScore1(vector,"desktop/src/ScoresRunge.txt", timer);

            System.out.println(Arrays.toString(vector)+" "+timer);

        }
        if(true) {
            timer=0;
            Physics.loadInitialValuesTest(muk, mus, Xt, Yt, r, function, x0, y0);

            double[] vector = new double[]{x0,y0,Vx,Vy,0};

            while (timer<16) {
                saveScore2(vector,"desktop/src/Scores.txt", timer);
                if (Physics.getBallMoving()) {
                    break;
                }

                ODESolvers.rungeKutta2(vector, 0.0004, 0.0004 - vector[4]);

                timer+=0.0004;


            }
            saveScore2(vector,"desktop/src/Scores.txt", timer);
            System.out.println(Arrays.toString(vector)+" "+timer);

        }
        if(true) {
            timer=0;
            Physics.loadInitialValuesTest(muk, mus, Xt, Yt, r, function, x0, y0);

            double[] vector = new double[]{x0,y0,Vx,Vy,0};

            while (timer<16) {
                saveScore3(vector,"desktop/src/euler.txt", timer);
                if (Physics.getBallMoving()) {
                    break;
                }

                vector = ODESolvers.eulerCalculation(vector, 0.0004, 0.0004-vector[4]);

                timer+=0.0004;

            }
            saveScore3(vector,"desktop/src/euler.txt", timer);
            System.out.println(Arrays.toString(vector)+" "+timer);
        }
    }
    public static void saveScore1(double[] vector, String FilePath, double Time) throws IOException {
        FileWriter file = new FileWriter(FilePath, true);
        PrintWriter out = new PrintWriter(file);

        out.println(Time+ ", "+vector[0] + ", " + vector[1] + ", "+vector[2] + ", " + vector[3]);

        out.close();
    }

    public static void saveScore2(double[] vector, String FilePath, double Time) throws IOException {
        FileWriter file = new FileWriter(FilePath, true);
        PrintWriter out = new PrintWriter(file);

        out.println(Time+ ", "+vector[0] + ", " + vector[1] + ", "+vector[2] + ", " + vector[3]);

        out.close();
    }
    public static void saveScore3(double[] vector, String FilePath, double Time) throws IOException {
        FileWriter file = new FileWriter(FilePath, true);
        PrintWriter out = new PrintWriter(file);

        out.println(Time+ ", "+vector[0] + ", " + vector[1] + ", "+vector[2] + ", " + vector[3]);

        out.close();
    }
    public static void main(String[] args) throws IOException {
        test(0f,0f,-4f,2f,0.1f,0.3f,2f,1f,1f,"0.5*(sin((x-y)/7)+0.9)", "Runge4");
    }
}