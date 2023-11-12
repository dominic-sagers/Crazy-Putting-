package main;

/**
 * This class contains all of our ODE solvers and their calculations
 */
public class ODESolvers {
    public static double currentVelX = 0;
    public static double currentVelY = 0;

    /**
     * Euler calculation. This method takes vector, stepsize, and howlong parameters.
     * It calculates new velocity and new acceleration for every step and keeps working until the time simulated is greater then howlomg
     * then it returns new state vector
     * @param vector original state
     * @param h stepsize
     * @param howLong how long should it run (0.01666) for 60 fps
     * @return new state vector
     */
    public static double[] eulerCalculation(double[] vector,double h, double howLong) {
        //Count number of iterations using step size or step height h
        double time = 0;
        double[] acceleration = new double[2];
        while (time - howLong < 0) {
            time += h;
            if (Physics.endGame(vector)) {
                Physics.setBallMoving(false);
                break;
            }
            double oldVx = vector[2];
            double oldVy = vector[3];


            if(InputReader.getPhysicsType().equals("Advanced")) {
                acceleration = Physics.accelerationV2((vector));
            }
            else if(InputReader.getPhysicsType().equals(("Normal"))){
                acceleration = Physics.accelerationV1OldCalclation(vector);
            }
            vector[0] += h * oldVx;
            vector[1] += h * oldVy;
            vector[2] += h *  acceleration[0];
            vector[3] += h *  acceleration[1];




        }
        vector[4] = time-howLong;

        return vector;
    }

    /**
     * This method contains the entire Runge Kutta 2nh order algorithm. For each iteration it starts by calculating the acceleration at that point and time.
     * It then calculates the next position of the ball using a combination of predictions from K1 and K2 .
     * It updates the vector state and runs until either an end-game condition is met or howLong time is smaller than simulated time.
     * This Runge Kutta second order method has a midpoint at 2/3 and is also known as the Ralston method.
     * @param stateVector state of the ball at the beginning
     * @param h the step size of the Runge Kutta algorithm
     * @param howLong how much time should this code simulate, to link with the framerate (0.016666666 for 60fps)
     * @return double[]: new updated vector after howLong time-span, or at end-game point
     * For more information, see https://en.wikipedia.org/wiki/Runge%E2%80%93Kutta_methods#Second-order_methods_with_two_stages
     */
    public static double[] rungeKutta2(double[] stateVector, double h, double howLong){
        double x0 = stateVector[0];
        double y0 = stateVector[1];
        double Vx = stateVector[2];
        double Vy = stateVector[3];

        double[] acceleration = new double[2];

        double time = 0;
        double k1,k2,b1,b2,kv1,kv2,bv1,bv2;


        while (time - howLong < 0) {
            time += h;

            if (Physics.endGame(stateVector)) {
                Physics.setBallMoving(false);
                break;
            }

            //Update velocity
            currentVelX = Vx;
            currentVelY = Vy;

            //Calculations of the Runge Kutta 2

            //Calculate acceleration at the start point
            if(InputReader.getPhysicsType().equals("Advanced")) {
                acceleration = Physics.accelerationV2(stateVector);
            }
            else if(InputReader.getPhysicsType().equals(("Normal"))){
                acceleration = Physics.accelerationV1OldCalclation(stateVector);
            }
            //Calculate position delta from initial velocity and velocity delta from acceleration at that point
            kv1 = h * acceleration[0];
            bv1 = h * acceleration[1];
            k1 = h * stateVector[2];
            b1 = h * stateVector[3];


            //Calculate acceleration at the end point of the interval
            if(InputReader.getPhysicsType().equals("Advanced")) {
                acceleration = Physics.accelerationV2(new double[] {x0 + k1, y0+b1, Vx + kv1, Vy + bv1});
            }
            else if(InputReader.getPhysicsType().equals(("Normal"))){
                acceleration = Physics.accelerationV1OldCalclation(new double[] {x0 + k1, y0+b1, Vx + kv1, Vy + bv1});
            }
            //Calculate position delta from initial velocity + velocity shift and velocity delta from acceleration at that point

            kv2 = h * acceleration[0];
            bv2 = h * acceleration[1];
            k2 = h * (stateVector[2]+ kv1);
            b2 = h * (stateVector[3]+ bv1);

            // Update next value of x and y
            x0 = x0 + k1/2 + k2/2;
            y0 = y0 + b1/2 + b2/2;


            // Update new velocities
            Vx = Vx + kv1/2 + kv2/2;
            Vy = Vy + bv1/2 + bv2/2;

            if(Math.abs(Vx)<0.001&&Math.abs(Vy)<0.001){
                if(Physics.endGameFriction(stateVector)){
                    stateVector[2]=0;
                    stateVector[3]=0;
                    Physics.setBallMoving(false);

                    return stateVector;
                }
            }
            //Update vector

            stateVector[0] = x0;
            stateVector[1] = y0;
            stateVector[2] = Vx;
            stateVector[3] = Vy;

        }

        stateVector[4] = time-howLong;

        return stateVector;
    }

    /**
     * This method contains the entire Runge Kutta 4th order algorithm ("Classic Runge-Kutta Method"). For each iteration it starts by calculating the acceleration at that point and time.
     * It then calculates the next position of the ball using a combination of predictions from K1 to K4.
     * It updates the vector state and runs until either an end-game condition is met or howLong time is smaller than simulated time.
     * @param stateVector state of the ball at the beginning
     * @param h the step size of the Runge Kutta algorithm
     * @param howLong how much time should this code simulate, to link with the framerate (0.016666666 for 60fps)
     * @return double[]: new updated vector after howLong time-span, or at end-game point
     * For more info, see https://en.wikipedia.org/wiki/Runge%E2%80%93Kutta_methods
     */
    public static double[] rungeKutta4(double[] stateVector, double h, double howLong) {
        double x0 = stateVector[0];
        double y0 = stateVector[1];
        double Vx = stateVector[2];
        double Vy = stateVector[3];

        double time = 0;
        double k1,k2,k3,k4,b1,b2,b3,b4,kv1,kv2,kv3,kv4,bv1,bv2,bv3,bv4;
        double[] acceleration = new double[2];

        while (time - howLong < 0) {
            time += h;
            //Simulate framerate (You can also call the method with variable howLong which should be set to match 1/fps
            if (Physics.endGame(stateVector)) {
                Physics.setBallMoving(false);
                break;
            }
            //Calculate acceleration at the start point
            if(InputReader.getPhysicsType().equals("Advanced")) {
                acceleration = Physics.accelerationV2(stateVector);
            }
            else if(InputReader.getPhysicsType().equals(("Normal"))){
                acceleration = Physics.accelerationV1OldCalclation(stateVector);
            }
            //Calculate position delta from initial velocity and velocity delta from acceleration at that point
            kv1 = h * acceleration[0];
            bv1 = h * acceleration[1];
            k1 = h * stateVector[2];
            b1 = h * stateVector[3];

            //Calculate acceleration at updated point
            if(InputReader.getPhysicsType().equals("Advanced")) {
                acceleration = Physics.accelerationV2(new double[] {stateVector[0] + k1/2, stateVector[1]+b1/2, stateVector[2] + kv1/2, stateVector[3]+bv1/2});
            }
            else if(InputReader.getPhysicsType().equals(("Normal"))){
                acceleration = Physics.accelerationV1OldCalclation(new double[] {stateVector[0] + k1/2, stateVector[1]+b1/2, stateVector[2] + kv1/2, stateVector[3]+bv1/2});
            }

            //Calculate position delta from initial velocity and velocity delta from acceleration at that point
            kv2 = h * acceleration[0];
            bv2 = h * acceleration[1];
            k2 = h * (stateVector[2]+ kv1/2);
            b2 = h * (stateVector[3]+bv1/2);
            if(InputReader.getPhysicsType().equals("Advanced")) {
                acceleration = Physics.accelerationV2(new double[] {stateVector[0] + k2/2, stateVector[1]+b2/2, stateVector[2] + kv2/2, stateVector[3]+bv2/2});
            }
            else if(InputReader.getPhysicsType().equals(("Normal"))){
                acceleration = Physics.accelerationV1OldCalclation(new double[] {stateVector[0] + k2/2, stateVector[1]+b2/2, stateVector[2] + kv2/2, stateVector[3]+bv2/2});
            }

            kv3 = h * acceleration[0];
            bv3 = h * acceleration[1];
            k3 = h * (stateVector[2]+ kv2/2);
            b3 = h * (stateVector[3]+bv2/2);
            if(InputReader.getPhysicsType().equals("Advanced")) {
                acceleration = Physics.accelerationV2(new double[] {stateVector[0] + k3, stateVector[1]+b3, stateVector[2] + kv3, stateVector[3]+bv3});
            }
            else if(InputReader.getPhysicsType().equals(("Normal"))){
                acceleration = Physics.accelerationV1OldCalclation(new double[] {stateVector[0] + k3, stateVector[1]+b3, stateVector[2] + kv3, stateVector[3]+bv3});
            }

            kv4 = h * acceleration[0];
            bv4 = h * acceleration[1];
            k4 = h * (stateVector[2]+ kv3);
            b4 = h * (stateVector[3]+bv3);


            //Update position and velocity
            Vx = Vx + (kv1+2*(kv2+kv3)+kv4)/6;
            Vy = Vy + (bv1+2*(bv2+bv3)+bv4)/6;
            x0 = x0 + (k1+2*(k2+k3)+k4)/6;
            y0 = y0 + (b1+2*(b2+b3)+b4)/6;
            //update vector
            stateVector[0] = x0;
            stateVector[1] = y0;
            stateVector[2] = Vx;
            stateVector[3] = Vy;
            if(Math.abs(Vx)<0.001&&Math.abs(Vy)<0.001){
                if(Physics.endGameFriction(stateVector)){
                    stateVector[2]=0;
                    stateVector[3]=0;
                    Physics.setBallMoving(false);

                    return stateVector;
                }
            }

        }
        //Update vector before returning
        stateVector[0] = x0;
        stateVector[1] = y0;
        stateVector[2] = Vx;
        stateVector[3] = Vy;
        stateVector[4] = time-howLong;
        return stateVector;
    }
    /**
     * Euler calculation. This method takes vector, stepsize, and howlong parameters.
     * It calculates new velocity and new acceleration for every step and keeps working until the end game condition adding new frame every 0.016 sec.
     * @param vector original state
     * @param h stepsize
     * @param howLong how long should it run (0.01666) for 60 fps
     */
    public static void eulerCalculationFrames(double[] vector,double h, double howLong) {
        //Count number of iterations using step size or step height h
        double time;
        double timer = 0;
        double oldVx;
        double oldVy;
        double Vx = vector[2];
        double Vy = vector[3];
        double x = vector[0];
        double y = vector[1];
        double[] acceleration = new double[2];
        while(true) {
            time = 0;
            while (time - howLong < 0) {
                time += h;
                timer += h;
                if (Physics.endGame(vector)) {
                    FramesQueue.add(new double[]{x, y, Vx, Vy, timer}, true);

                    FramesQueue.isDone = true;

                    return;
                }

                oldVx = Vx;
                oldVy = Vy;


                if(InputReader.getPhysicsType().equals("Advanced")) {
                    acceleration = Physics.accelerationV2((new double[]{x, y, Vx, Vy, timer}));
                }
                else if(InputReader.getPhysicsType().equals(("Normal"))){
                    acceleration = Physics.accelerationV1OldCalclation((new double[]{x, y, Vx, Vy, timer}));
                }
                x += h * Vx;
                y += h * Vy;
                Vx =Vx + h *  acceleration[0];
                Vy =Vy + h*  acceleration[1];
                vector[0] = x;
                vector[1] = y;
                vector[2] = Vx;
                vector[3] = Vy;

                if ((oldVx * Vx <= 0 && oldVy * Vy <= 0) || (Math.abs(Vx) < 0.001 && Math.abs(Vy) < 0.001)) {
                    if (Physics.endGameFriction(vector)) {
                        FramesQueue.add((new double[]{x, y, Vx, Vy, timer} ), true);
                        FramesQueue.isDone = true;
                        return;
                    }
                }


            }
            vector[4] = time - howLong;
            FramesQueue.add(new double[]{x, y, Vx, Vy, timer}, true);

        }
    }

    /**
     * This method contains the entire Runge Kutta 2nh order algorithm. For each iteration it starts by calculating the acceleration at that point and time.
     * It then calculates the next position of the ball using a combination of predictions from K1 and K2 .
     * It updates the vector state and runs until an end-game condition is met adding frames to FramesQueue every 0.016666 sec.
     * This Runge Kutta second order method has a midpoint at 2/3 and is also known as the Ralston method.
     * @param stateVector state of the ball at the beginning
     * @param h the step size of the Runge Kutta algorithm
     * @param howLong how much time should this code simulate, to link with the frame rate (0.016666666 for 60fps)
     * For more information, see https://en.wikipedia.org/wiki/Runge%E2%80%93Kutta_methods#Second-order_methods_with_two_stages
     */
    public static void rungeKutta2Frames(double[] stateVector, double h, double howLong){
        double x0 = stateVector[0];
        double y0 = stateVector[1];
        double Vx = stateVector[2];
        double Vy = stateVector[3];

        double[] acceleration = new double[2];

        double time = 0;
        double k1,k2,b1,b2,kv1,kv2,bv1,bv2;
        double timer = 0;
        double oldVx;
        double oldVy;
        FramesQueue.add(new double[] {x0,y0,Vx,Vy, timer}, true);

        while(true){

            while (time - howLong < 0) {
                time += h;
                timer+= h;
                if (Physics.endGame(stateVector)) {
                    FramesQueue.add(stateVector, true);

                    FramesQueue.isDone=true;

                    return;
                }

                //Update velocity
                currentVelX = Vx;
                currentVelY = Vy;

                //Calculations of the Runge Kutta 2

                //Calculate acceleration at the start point
                if(InputReader.getPhysicsType().equals("Advanced")) {
                    acceleration = Physics.accelerationV2(stateVector);
                }
                else if(InputReader.getPhysicsType().equals(("Normal"))){
                    acceleration = Physics.accelerationV1OldCalclation(stateVector);
                }
                //Calculate position delta from initial velocity and velocity delta from acceleration at that point
                kv1 = h * acceleration[0];
                bv1 = h * acceleration[1];
                k1 = h * stateVector[2];
                b1 = h * stateVector[3];


                //Calculate acceleration at the end point of the interval
                if(InputReader.getPhysicsType().equals("Advanced")) {
                    acceleration = Physics.accelerationV2(new double[] {x0 + k1, y0+b1, Vx + kv1, Vy + bv1});
                }
                else if(InputReader.getPhysicsType().equals(("Normal"))){
                    acceleration = Physics.accelerationV1OldCalclation(new double[] {x0 + k1, y0+b1, Vx + kv1, Vy + bv1});
                }
                //Calculate position delta from initial velocity + velocity shift and velocity delta from acceleration at that point

                kv2 = h * acceleration[0];
                bv2 = h * acceleration[1];
                k2 = h * (stateVector[2]+ kv1);
                b2 = h * (stateVector[3]+ bv1);

                // Update next value of x and y
                x0 = x0 + k1/2 + k2/2;
                y0 = y0 + b1/2 + b2/2;


                // Update new velocities
                oldVx = Vx;
                oldVy = Vy;
                Vx = Vx + kv1/2 + kv2/2;
                Vy = Vy + bv1/2 + bv2/2;


                if((oldVx*Vx<=0&&oldVy*Vy<=0)||(Math.abs(Vx)<0.001&&Math.abs(Vy)<0.001)){
                    if (Physics.endGameFriction(stateVector)) {
                        FramesQueue.add(new double[] {x0,y0,Vx,Vy, timer}, true);
                        FramesQueue.isDone=true;
                        return;
                    }
                }
                //update vector
                stateVector[0] = x0;
                stateVector[1] = y0;
                stateVector[2] = Vx;
                stateVector[3] = Vy;
            }
            FramesQueue.add(new double[] {x0,y0,Vx,Vy,timer}, true);

            time = time-howLong;
        }
    }

    /**
     * This method contains the entire Runge Kutta 4th order algorithm ("Classic Runge-Kutta Method"). For each iteration it starts by calculating the acceleration at that point and time.
     * It then calculates the next position of the ball using a combination of predictions from K1 to K4.
     * It updates the vector state and runs until an end-game condition is met adding frames to FramesQueue every 0.016666 sec.
     * @param stateVector state of the ball at the beginning
     * @param h the step size of the Runge Kutta algorithm
     * @param howLong how much time should this code simulate, to link with the frame rate (0.016666666 for 60fps)
     * For more info, see https://en.wikipedia.org/wiki/Runge%E2%80%93Kutta_methods
     */
    public static void rungeKutta4Frames(double[] stateVector, double h, double howLong) {
        double x0 = stateVector[0];
        double y0 = stateVector[1];
        double Vx = stateVector[2];
        double Vy = stateVector[3];
        double timer = 0;

        double time = 0;
        double k1, k2, k3, k4, b1, b2, b3, b4, kv1, kv2, kv3, kv4, bv1, bv2, bv3, bv4;
        double[] acceleration;
        double oldVx;
        double oldVy;
        FramesQueue.add(new double[] {x0,y0,Vx,Vy, timer}, true);

        while(true){

            while (time - howLong < 0) {
                double[] frame = {x0,y0,Vx,Vy, timer};
                time += h;
                timer+=h;


                //Simulate frame rate (You can also call the method with variable howLong which should be set to match 1/fps
                if (Physics.endGame(stateVector)) {
                    FramesQueue.add(stateVector, true);

                    FramesQueue.isDone=true;

                    return;
                }
                //Calculate acceleration at the start point
                acceleration = Physics.accelerationV2(stateVector);

                //Calculate position delta from initial velocity and velocity delta from acceleration at that point
                kv1 = h * acceleration[0];
                bv1 = h * acceleration[1];
                k1 = h * stateVector[2];
                b1 = h * stateVector[3];

                //Calculate acceleration at updated point
                if(InputReader.getPhysicsType().equals("Advanced")) {
                    acceleration = Physics.accelerationV2(new double[]{stateVector[0] + k1 / 2, stateVector[1] + b1 / 2, stateVector[2] + kv1 / 2, stateVector[3] + bv1 / 2});
                }
                else if(InputReader.getPhysicsType().equals(("Normal"))){
                    acceleration = Physics.accelerationV1OldCalclation(new double[]{stateVector[0] + k1 / 2, stateVector[1] + b1 / 2, stateVector[2] + kv1 / 2, stateVector[3] + bv1 / 2});
                }

                //Calculate position delta from initial velocity and velocity delta from acceleration at that point
                kv2 = h * acceleration[0];
                bv2 = h * acceleration[1];
                k2 = h * (stateVector[2] + kv1 / 2);
                b2 = h * (stateVector[3] + bv1 / 2);

                if(InputReader.getPhysicsType().equals("Advanced")) {
                    acceleration = Physics.accelerationV2(new double[]{stateVector[0] + k2 / 2, stateVector[1] + b2 / 2, stateVector[2] + kv2 / 2, stateVector[3] + bv2 / 2});
                }
                else if(InputReader.getPhysicsType().equals(("Normal"))){
                    acceleration = Physics.accelerationV1OldCalclation(new double[]{stateVector[0] + k2 / 2, stateVector[1] + b2 / 2, stateVector[2] + kv2 / 2, stateVector[3] + bv2 / 2});
                }
                kv3 = h * acceleration[0];
                bv3 = h * acceleration[1];
                k3 = h * (stateVector[2] + kv2 / 2);
                b3 = h * (stateVector[3] + bv2 / 2);

                if(InputReader.getPhysicsType().equals("Advanced")) {
                    acceleration = Physics.accelerationV2(new double[]{stateVector[0] + k3, stateVector[1] + b3, stateVector[2] + kv3, stateVector[3] + bv3});
                }
                else if(InputReader.getPhysicsType().equals(("Normal"))){
                    acceleration = Physics.accelerationV1OldCalclation(new double[]{stateVector[0] + k3, stateVector[1] + b3, stateVector[2] + kv3, stateVector[3] + bv3});
                }
                kv4 = h * acceleration[0];
                bv4 = h * acceleration[1];
                k4 = h * (stateVector[2] + kv3);
                b4 = h * (stateVector[3] + bv3);
                oldVx = Vx;
                oldVy = Vy;
                //Update position and velocity
                Vx = Vx + (kv1 + 2 * (kv2 + kv3) + kv4) / 6;
                Vy = Vy + (bv1 + 2 * (bv2 + bv3) + bv4) / 6;
                x0 = x0 + (k1 + 2 * (k2 + k3) + k4) / 6;
                y0 = y0 + (b1 + 2 * (b2 + b3) + b4) / 6;
                if((oldVx*Vx<=0&&oldVy*Vy<=0)||(Math.abs(Vx)<0.001&&Math.abs(Vy)<0.001)){
                    if (Physics.endGameFriction(stateVector)) {
                        FramesQueue.add(new double[] {x0,y0,Vx,Vy, timer}, true);
                        FramesQueue.isDone=true;
                        return;
                    }
                }
                //update vector
                stateVector[0] = x0;
                stateVector[1] = y0;
                stateVector[2] = Vx;
                stateVector[3] = Vy;
            }
            FramesQueue.add(new double[] {x0,y0,Vx,Vy,timer}, true);

            time = time-howLong;
        }

    }
}
