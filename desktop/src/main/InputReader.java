package main;

import java.io.*;
import java.util.Scanner;

public class InputReader{

    private static float x0 = 1337f;
    private static float y0 = 1337f;
    private static float xt = 1337f;
    private static float yt = 1337f;
    private static float r = 1337f;
    private static float muk = 1337f;
    private static float mus = 1337f;
    private static String heightProfile = "Oops!";
    private static String gameType = "Oops!";
    private static String botType = "Oops!";
    private static String odeSolver = "Oops!";
    private static String physicsType = "Oops!";
    private static String errorType = "Oops!";
    private static int amountOfTrees = 9999;

    public static void setInitialBallPos(float x0, float y0){
        InputReader.x0 = x0;
        InputReader.y0 = y0;
    }

    public static void setGoalPos(float xt, float yt){
        InputReader.xt = xt;
        InputReader.yt = yt;
    }

    public static void setScoreRadius(float r){
        InputReader.r = r;
    }

    public static void setFrictions(float muk, float mus){
        InputReader.muk = muk;
        InputReader.mus = mus;
    }

    public static void setPlayer(String gameType, String botType, String odeSolver){
        InputReader.gameType = gameType;
        InputReader.botType = botType;
        InputReader.odeSolver = odeSolver;
    }

    public static void setHeightProfile(String heightProfile){
        InputReader.heightProfile = heightProfile;
    }

    public static void setPhysicsType(String physicsType){
        InputReader.physicsType = physicsType;
    }

    public static void setErrorType(String errorType){
        InputReader.errorType = errorType;
    }



    public static float getX0(){
        if(x0==1337f){
            System.out.println("WARNING: The 'x0' field has not been properly set, returning 0");
            return 0;
        }
        return x0;
    }

    public static float getY0() {
        if(y0==1337f){
            System.out.println("WARNING: The 'y0' field has not been properly set, returning 0");
            return 0;
        }
        return y0;
    }

    public static float getXt() {
        if(xt==1337f){
            System.out.println("WARNING: The 'xt' field has not been properly set, returning 0");
            return 0;
        }
        return xt;
    }

    public static float getYt() {
        if(yt==1337f){
            System.out.println("WARNING: The 'yt' field has not been properly set, returning 0");
            return 0;
        }
        return yt;
    }

    public static float getR() {
        if(r==1337f){
            System.out.println("WARNING: The 'r' field has not been properly set, returning 0");
            return 0;
        }
        return r;
    }

    public static float getMuk() {
        if(muk==1337f){
            System.out.println("WARNING: The 'muk' field has not been properly set, returning 0");
            return 0;
        }
        return muk;
    }

    public static float getMus() {
        if(mus==1337f){
            System.out.println("WARNING: The mus field has not been properly set, returning 0");
            return 0;
        }
        return mus;
    }

    public static String getHeightProfile() {
        if(heightProfile.equals("Oops!")){
            System.out.println("WARNING: The 'heightProfile' field has not been properly set, returning 0");
            return "0";
        }
        return heightProfile;
    }

    public static String getGameType() {
        if(gameType.equals("Oops!")){
            System.out.println("WARNING: The 'playerOrBot' field has not been properly set, returning 0");
            return "0";
        }
        return gameType;
    }

    public static String getBotType() {
        if(botType.equals("Oops!")){
            System.out.println("WARNING: The 'playerType' field has not been properly set, returning 0");
            return "0";
        }
        return botType;
    }

    public static String getOdeSolver() {
        if(odeSolver.equals("Oops!")){
            System.out.println("WARNING: The 'algorithmType' field has not been properly set, returning 0");
            return "0";
        }
        return odeSolver;
    }

    public static String getPhysicsType() {
        if(odeSolver.equals("Oops!")){
            System.out.println("WARNING: The 'physicsType' field has not been properly set, returning 0");
            return "0";
        }
        return physicsType;
    }

    public static String getErrorType(){
        if(errorType.equals("Oops!")){
            System.out.println("WARNING: The 'errorType' field has not been properly set, returning 0");
            return "0";
        }
        return errorType;
    }

    public static int getAmountOfTrees() {
        if(amountOfTrees == 9999){
            System.out.println("WARNING: The 'errorType' field has not been properly set, returning 0");
            return 0;
        }
        return amountOfTrees;
    }

    public static void setAmountOfTrees(int amountOfTrees) {
        InputReader.amountOfTrees = amountOfTrees;
    }

    public static void createHeightFunctionClass(String heightProfile){
        try(PrintWriter writer = new PrintWriter("desktop/src/main/HeightFunction.java")){
            writer.write(
                    "package main;\n" +

                            "/**\n" +
                            " * This class is generated by the inputReader after the program is launched.\n" +
                            " * The calculateHeight function is replaced by the function that given in the configurations within the menu\n" +
                            " */\n" +

                    "public class HeightFunction{\n" +

                            "\t/**\n" +
                            "\t * Calculates the height of the terrain at the given x and y position\n" +
                            "\t * @param x the x position\n" +
                            "\t * @param y the y position\n" +
                            "\t * @return the height of the terrain at the given x and y position\n" +
                            "\t */\n" +

                            "\tpublic static double calculateHeight(double x, double y){\n" +
                            "\t\treturn " + heightProfile + ";\n" +
                            "\t}" + "\n" +
                            "}");
        }
        catch (FileNotFoundException e){
            System.out.println("Could not create the HeightFunction class, make sure the function has been properly loaded by the inputReader");
            e.printStackTrace();
        }
    }

    /**
     * This method overwrites the inputFile.txt with the newly updated values.
     */
    public static void updateInputFile(){
        try(PrintWriter w = new PrintWriter("assets/inputFile.txt")){
            w.write(
                            "x0 = " + x0 + "\n"+
                            "y0 = " + y0 + "\n"+
                            "xt = " + xt + "\n"+
                            "yt = " + yt + "\n"+
                            "r = " + r + "\n"+
                            "muk = " + muk + "\n"+
                            "mus = " + mus + "\n"+
                            "heightProfile = " + heightProfile + "\n"+
                            "playerOrBot = " + gameType +"\n" +
                            "playerType = " + botType + "\n"+
                            "algorithmType = " + odeSolver + "\n" +
                            "physicsType = " + physicsType + "\n" +
                            "errorType = " + errorType + "\n" +
                            "amountOfTrees = " + amountOfTrees
            );

        }catch(FileNotFoundException e){
            System.out.println("Could not overwrite inputFile.txt, make sure the file path is correct.");
            e.printStackTrace();
        }
    }
    //Sets class values when given a valid path for an Input.txt file
    //Checks if all values are mapped, if not, it opens the file chooser again
    public static void setInputs(File inputFile) {
        //Throws FileNotFoundException if path is invalid. Throws IOException if file is empty or corrupted
        try{
            //Retrieving file from given 'filePath'
            FileReader reader = new FileReader(inputFile);
            BufferedReader in = new BufferedReader(reader);

            //Scanner and temporary strings for reading loop
            String currentLine;
            Scanner newLine;
            StringBuilder checkString = new StringBuilder();

            //Until there are no more lines in the input file, retrieve the next line, split the String into parts/substrings using the Delimiter with " " as a pattern,
            //then check if the line matches a necessary input field, if so parse it correctly and set the input, else continue to next line.
            while(in.ready()){

                currentLine = in.readLine();
                newLine = new Scanner(currentLine);
                newLine.useDelimiter(" ");

                checkString.append(newLine.next());

                if(checkString.toString().equals("x0")){
                    newLine.next();
                    x0 = Float.parseFloat(newLine.next());
                }
                if(checkString.toString().equals("y0")){
                    newLine.next();
                    y0 = Float.parseFloat(newLine.next());
                }
                if(checkString.toString().equals("xt")){
                    newLine.next();
                    xt = Float.parseFloat(newLine.next());
                }
                if(checkString.toString().equals("yt")){
                    newLine.next();
                    yt = Float.parseFloat(newLine.next());
                }
                if(checkString.toString().equals("r")){
                    newLine.next();
                    r = Float.parseFloat(newLine.next());
                }
                if(checkString.toString().equals("muk")){
                    newLine.next();
                    muk = Float.parseFloat(newLine.next());
                }
                if(checkString.toString().equals("mus")){
                    newLine.next();
                    mus = Float.parseFloat(newLine.next());
                }
                if(checkString.toString().equals("heightProfile")){
                    newLine.next();
                    heightProfile = newLine.next();
                }
                if(checkString.toString().equals("playerOrBot")){
                    newLine.next();
                    gameType = newLine.next();
                }
                if(checkString.toString().equals("playerType")){
                    newLine.next();
                    botType = newLine.next();
                }
                if(checkString.toString().equals("algorithmType")){
                    newLine.next();
                    odeSolver = newLine.next();
                }
                if(checkString.toString().equals("physicsType")){
                    newLine.next();
                    physicsType = newLine.next();
                }
                if(checkString.toString().equals("errorType")){
                    newLine.next();
                    errorType = newLine.next();
                }
                if(checkString.toString().equals("amountOfTrees")){
                    newLine.next();
                    amountOfTrees = Integer.parseInt(newLine.next());
                }
                checkString = new StringBuilder();
            }


        }catch(FileNotFoundException e){
            System.out.println("File not Found, try to input an absolute path.");
        }catch(IOException e){
            System.out.println("IOException, file is empty or corrupted.");
        }

        //Checks if all values are mapped
        if(!inputsMapped()){
            System.out.println("Error: Not all values have been properly set within the file, please check the input file");
        }
    }
    //Returns false if any inputs remain their default values
    public static boolean inputsMapped(){
        if(x0 == 1337f){return false;}
        else if(y0 == 1337f){return false;}
        else if(xt == 1337f){return false;}
        else if(yt == 1337f){return false;}
        else if(r == 1337f){return false;}
        else if(muk == 1337f){return false;}
        else if(mus == 1337f){return false;}
        else return !heightProfile.equals("Oops!");
    }
}
