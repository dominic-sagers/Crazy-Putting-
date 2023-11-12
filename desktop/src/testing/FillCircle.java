package testing;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class FillCircle extends Frame {
    public Color ten = new Color(51, 0, 0);
    public Color nine = new Color(102,0,0);
    public Color eight = new Color(153, 0, 0);
    public Color seven = new Color(204,0,0);
    public Color six = new Color(255, 0, 0);
    public Color five = new Color(255,128,0);
    public Color four = new Color(255, 255, 0);
    public Color three = new Color(128,255,0);
    public Color two = new Color(0, 255, 128);
    public Color one = new Color(51,255,255);
    public void paint(Graphics g) {
        Graphics2D ga = (Graphics2D)g;
        ga.setPaint(Color.red);
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("desktop/src/testing/heatMapManhatan3.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        scanner.useDelimiter(",");
        double x;
        double y;
        double colorID;
        while(scanner.hasNextLine()){

            try {
                x = Double.parseDouble(scanner.next());

                y = Double.parseDouble(scanner.next());
                colorID = Double.parseDouble(scanner.next());}
            catch(Exception e) {
                break;
            }
            Color color = null;


            if(colorID>=10){
                color = ten;
            }
            if(colorID==9){
                color = nine;
            }
            if(colorID==8){
                color = eight;
            }
            if(colorID==7){
                color = seven;
            }
            if(colorID==6){
                color = six;
            }if(colorID==5){
                color = five;
            }if(colorID==4){
                color = four;
            }
            if(colorID==3){
                color = three;
            }
            if(colorID==2){
                color = two;
            }
            if(colorID==1){
                color = one;
            }




            int[] coordiantes = translate(new double[]{x,y});
            ga.setColor(color);
            ga.fillOval(coordiantes[0],coordiantes[1],8,8);


        }
        scanner.close();
        try {
            scanner = new Scanner(new File("desktop/src/testing/heatMapNewton3.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        scanner.useDelimiter(",");

        while(scanner.hasNextLine()){

            try {
                x = Double.parseDouble(scanner.next());

                y = Double.parseDouble(scanner.next());
                colorID = Double.parseDouble(scanner.next());}
            catch(Exception e) {
                break;
            }
            Color color = null;


            if(colorID>=10){
                color = ten;
            }
            if(colorID==9){
                color = nine;
            }
            if(colorID==8){
                color = eight;
            }
            if(colorID==7){
                color = seven;
            }
            if(colorID==6){
                color = six;
            }if(colorID==5){
                color = five;
            }if(colorID==4){
                color = four;
            }
            if(colorID==3){
                color = three;
            }
            if(colorID==2){
                color = two;
            }
            if(colorID==1){
                color = one;
            }




            int[] coordiantes = translate2(new double[]{x,y});
            ga.setColor(color);
            ga.fillOval(coordiantes[0],coordiantes[1],8,8);


        }
        scanner.close();
        try {
            scanner = new Scanner(new File("desktop/src/testing/heatMapHill3.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        scanner.useDelimiter(",");

        while(scanner.hasNextLine()){

            try {
                x = Double.parseDouble(scanner.next());

                y = Double.parseDouble(scanner.next());
                colorID = Double.parseDouble(scanner.next());}
            catch(Exception e) {
                break;
            }
            Color color = null;


            if(colorID>=10){
                color = ten;
            }
            if(colorID==9){
                color = nine;
            }
            if(colorID==8){
                color = eight;
            }
            if(colorID==7){
                color = seven;
            }
            if(colorID==6){
                color = six;
            }if(colorID==5){
                color = five;
            }if(colorID==4){
                color = four;
            }
            if(colorID==3){
                color = three;
            }
            if(colorID==2){
                color = two;
            }
            if(colorID==1){
                color = one;
            }




            int[] coordinates = translate3(new double[]{x,y});
            ga.setColor(color);
            ga.fillOval(coordinates[0],coordinates[1],8,8);


        }
        scanner.close();
    }

    public int[] translate(double[] coordinate){
        int[] answer = new int[2];
        answer[0] = (int)((coordinate[0]+11)*30);
        answer[1] = (int)((coordinate[1]+11)*30);
        return answer;
    }
    public int[] translate2(double[] coordinate){
        int[] answer = new int[2];
        answer[0] = (int)((coordinate[0]+11)*30+640);
        answer[1] = (int)((coordinate[1]+11)*30);
        return answer;
    }
    public int[] translate3(double[] coordinate){
        int[] answer = new int[2];
        answer[0] = (int)((coordinate[0]+11)*30+1280);
        answer[1] = (int)((coordinate[1]+11)*30);
        return answer;
    }



    public static void main(String[] args)
    {
        FillCircle frame = new FillCircle();
        frame.addWindowListener(
                new WindowAdapter()
                {
                    public void windowClosing(WindowEvent we)
                    {
                        System.exit(0);
                    }
                }
        );

        frame.setSize(800, 800);
        frame.setVisible(true);
    }
}