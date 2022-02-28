package com.company;

import java.util.ArrayList;
import java.util.Vector;

import static java.lang.Math.*;

public class Hullbreaker {
    private float x = 500;

    public float getH() {
        return hAP;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
    private int targetr = 12;
    private float y = 375;
    private float vx = 0;
    private float vy = 0;
    private float h = 0;
    private float hAP = 0;
    public Asteriod target = new Asteriod();
    public void AI(){
        x += vx;
        y += vy;
        if (y < 0) {
            y = 750;
        }
        if (x < 0) {
            x = 1000;
        }
        if (y > 750) {
            y = 0;
        }
        if (x > 1000) {
            x = 0;
        }
        Asteriod closest = new Asteriod();
        float cl = 100000;
        if (target.radii != targetr) {
            for (Asteriod asteriod : MySketch.asteroids) {
                if (asteriod.collide(x, y, MySketch.upgrades.get("BreakerHitbox") * 10 + 10)) {
                    if (asteriod.radii < MySketch.upgrades.get("BreakerDamage") * 5) {
                        asteriod.setup();
                        MySketch.score++;
                    } else {
                        asteriod.radii -= MySketch.upgrades.get("BreakerDamage") * 5;
                        asteriod.vx += vx;
                        asteriod.vy += vy;
                    }
                }
                if (pow(asteriod.x - x, 2) + pow(asteriod.y - y, 2) < cl) {
                    closest = asteriod;
                    cl = (float) (pow(asteriod.x - x, 2) + pow(asteriod.y - y, 2));
                }
            }
            target = closest;
            targetr = (int) closest.radii;
        }
        h = (float) Math.atan2(y-target.y, x-target.x);
        vy += sin(h);
        vx += cos(h);
        if (pow(vx, 2) + pow(vy, 2) > 400) {
            float r = (float) atan2(vy, vx);
            vx = (float) (cos(r)*20);
            vy = (float) (sin(r)*20);
        }
        if(hAP != h){
            hAP +=h/10;
        }
    }
}
