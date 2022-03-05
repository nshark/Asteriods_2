package com.company;

import java.util.Random;

import static java.lang.Math.*;

@SuppressWarnings("ALL")
public class Asteriod {
    public float x;
    public float y;
    public float vx;
    public float vy;
    public float radii;
    final Random ran = new Random();
    Asteriod(){
        setup();
    }
    public void setup(){
        int rSide = ran.nextInt(3);
        if(rSide==0){
            x = 0;
            y = ran.nextFloat(750);
        }
        else if(rSide==1){
            x = 1000;
            y = ran.nextFloat(750);
        }
        else if(rSide==2){
            y = 0;
            x = ran.nextFloat(1000);
        }
        else{
            y = 750;
            x = ran.nextFloat(1000);
        }
        radii = ran.nextFloat(10,50);
        float toX = ran.nextFloat(1,999);
        float toY = ran.nextFloat(1, 999);
        float b = (float) atan2(toY-y, toX-x);
        vx = (float) ((6-(radii/10))*cos(b));
        vy = (float) ((6-(radii/10))*sin(b));
    }
    public void update(){
        x += vx;
        y += vy;
        if(Math.abs(this.radii) > 100){
            this.setup();
        }
    }
    public boolean collide(float ex, float ey, float eRadi){
        float dist = ((ex-x)*(ex-x) + (ey - y)*(ey - y));
        return dist <= (radii/2 + eRadi/2)*(radii/2 + eRadi/2);
    }
}
