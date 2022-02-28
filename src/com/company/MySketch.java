package com.company;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

public class MySketch extends PApplet{
    // player x position
    public static float px = 500;
    // player y position
    public static float py = 375;
    // are you in the shop menu
    public boolean shop = false;
    // upgrades:
    public static ArrayList<Hullbreaker> hullbreakers = new ArrayList<>();
    public static HashMap<String, Integer> upgrades = new HashMap<>();
    // player heading in radians
    public static float ph = 0;
    // player velocity x
    public static float pvx = 0;
    // player velocity y
    public static float pvy = 0;
    public static int score = 300;
    public boolean lost = true;
    // recylables
    public ArrayList<Vector<Float>> re = new ArrayList<>();
    // life-up asteroid
    public Asteriod life = new Asteriod();
    // lives
    public int lives = 3;
    // ticks before next shot
    public int t = 20;
    // all asteroids that harm you
    public static ArrayList<Asteriod> asteroids = new ArrayList<>();
    // offsets from px, py for things to be drawn at
    public static ArrayList<Float> offsetPoints = new ArrayList<>();
    // hashmap of whether a letter key is being pressed
    public static HashMap<Character, Boolean> keys = new HashMap<>();
    // list of points for drawing the trail
    public ArrayList<Vector<Float>> trail = new ArrayList<>();
    // list of active shots, as (x, y, vx, vy)
    public ArrayList<Vector<Float>> cannon = new ArrayList<>();
    // setup processing
    public void settings(){
        size(1000, 750);
    }
    // called every tick
    public void draw(){
        //if I haven't lost
        if(!lost) {
            // erase the previous tick
            background(0);
            // set my color to white
            fill(255);
            // if active shots
            if (cannon.size() > 0) {
                for (Vector<Float> v2 : cannon) {
                    // update position based on velocity
                    v2.set(0, v2.get(2) + v2.get(0));
                    v2.set(1, v2.get(3) + v2.get(1));
                    // if out of bounds, mark for removal
                    if ((v2.get(0) > 1000 || v2.get(0) < 0 || v2.get(1) > 750 || v2.get(1) < 0)&& !re.contains(v2)) {
                        re.add(v2);
                    } else if(!re.contains(v2)) {
                        // otherwise, draw them at the new position
                        fill(255, 0, 0);
                        ellipse(v2.get(0), v2.get(1), 10, 10);
                    }
                }
            }
            // move and check for collisions
            this.updateAsteroids();
            // move the life-up asteroid and check for collisions
            this.updateLife();
            // handle key presses, update velocity, deal with screen wrapping
            this.updatePlayer();
            // compute the rotated offset points for drawing the ship
            this.updateHullbreakers();
            ArrayList<Float> offsetRotated = new ArrayList<>();
            for (int i = 0; i < offsetPoints.size(); i += 2) {
                float x1 = offsetPoints.get(i);
                float y1 = offsetPoints.get(i + 1);
                offsetRotated.add(x1 * cos(ph) - y1 * sin(ph));
                offsetRotated.add(x1 * sin(ph) + y1 * cos(ph));
            }
            // setup for drawing trail
            stroke(255, 0, 0, 25);
            strokeWeight(10);
            // add position as a new point in trail
            Vector<Float> t = new Vector<>();
            t.add(px);
            t.add(py);
            trail.add(t);
            if (trail.size() > 2) {
                for (int i = 1; i < trail.size(); i++) {
                    // draw a line from every point in the trail to the following and preceding
                    Vector<Float> v1 = trail.get(i);
                    Vector<Float> v2 = trail.get(i - 1);
                    line(v2.get(0), v2.get(1), v1.get(0), v1.get(1));
                }
            }
            if (trail.size() > 10) {
                // if there is more than 10 points, remove the last
                trail.remove(0);
            }
            strokeWeight(0);
            // draw the ship using the rotated offset points
            triangle(px + offsetRotated.get(4), py + offsetRotated.get(5), px + offsetRotated.get(6),
                    py + offsetRotated.get(7), px + offsetRotated.get(8), py + offsetRotated.get(9));
            triangle(px + offsetRotated.get(10), py + offsetRotated.get(11), px + offsetRotated.get(12),
                    py + offsetRotated.get(13), px + offsetRotated.get(14), py + offsetRotated.get(15));
            ellipse(px, py, 10, 10);
            for (int i = 0; i < lives; i++) {
                // display lives remaining
                ellipse(990-i*20, 30, 10, 10);
            }
        }
        else if(shop){
            background(0);
            this.updateAsteroids();
            fill(0,0,120);
            rect(10, 10, 100, 100);
            if(mouseX<100 && mouseX>20 && mouseY<100&&mouseY>20){
                fill(0,0,255);
                if (mousePressed){
                    shop = false;
                }
            }
            rect(20, 20, 80, 80);
            fill(0,0,120);
            rect(120, 10, 600, 60);
            if(mouseX<710 && mouseX>130 && mouseY<60&&mouseY>20){
                fill(0,0,255);
                if (mousePressed && score >= 10*upgrades.get("Damage") + 10){
                    upgrades.replace("Damage", upgrades.get("Damage")+1);
                    score -= 10*upgrades.get("Damage");
                }
            }
            rect(130, 20, 580, 40);
            fill(0,0,120);
            rect(120, 70, 600, 60);
            if(mouseX<710 && mouseX>130 && mouseY<120&&mouseY>80){
                fill(0,0,255);
                if (mousePressed && score >= 10*upgrades.get("Fire Rate") + 10){
                    upgrades.replace("Fire Rate", upgrades.get("Fire Rate")+1);
                    score -= 10*upgrades.get("Fire Rate");
                }
            }
            rect(130, 80, 580, 40);
            fill(0,0,120);
            rect(120, 130, 600, 60);
            if(mouseX<710 && mouseX>130 && mouseY<180&&mouseY>140){
                fill(0,0,255);
                if (mousePressed && score >= Math.pow(upgrades.get("HullbreakerNum"), 2)*10 + 30){
                    upgrades.replace("HullbreakerNum", upgrades.get("HullbreakerNum")+1);
                    score -= 10*Math.pow(upgrades.get("HullbreakerNum")-1, 2) + 30;
                    hullbreakers.add(new Hullbreaker());
                }
            }
            rect(130, 140, 580, 40);
            fill(0,0,120);
            rect(120, 190, 600, 60);
            if(mouseX<710 && mouseX>130 && mouseY<240&&mouseY>200){
                fill(0,0,255);
                if (mousePressed && score >= upgrades.get("BreakerDamage")*10 - 20){
                    upgrades.replace("BreakerDamage", upgrades.get("BreakerDamage")+1);
                    score -= upgrades.get("BreakerDamage")*10 - 30;
                }
            }
            rect(130, 200, 580, 40);
            fill(0,0,120);
            rect(120, 250, 600, 60);
            if(mouseX<710 && mouseX>130 && mouseY<300&&mouseY>260){
                fill(0,0,255);
                if (mousePressed && score >= upgrades.get("BreakerHitbox")*10){
                    upgrades.replace("BreakerHitbox", upgrades.get("BreakerHitbox")+1);
                    score -= upgrades.get("BreakerHitbox")*10-10;
                }
            }
            rect(130, 260, 580, 40);
            fill(255);
            textSize(40);
            text("back", 20, 75);
            text(score, 900, 75);
            textSize(30);
            text("Damage:" + upgrades.get("Damage") + " Cost: " + (upgrades.get("Damage")*10+10), 130, 50);
            text("Fire Rate:" + upgrades.get("Fire Rate") + " Cost: " + (upgrades.get("Fire Rate")*10+10), 130, 110);
            text("Hullbreaker Number: " + upgrades.get("HullbreakerNum") + " Cost: " + (Math.pow(upgrades.get("HullbreakerNum"),2)*10+30), 130, 170);
            text("Hullbreaker Damage:" + upgrades.get("BreakerDamage") + " Cost: " + (upgrades.get("BreakerDamage")*10-20), 130, 230);
            text("Hullbreaker Hitbox:" + upgrades.get("BreakerHitbox") + " Cost: " + (upgrades.get("BreakerHitbox")*10), 130, 290);
        }
        else{
            // if I not in the shop or playing, display the main menu
            background(0, 0, 0);
            this.updateAsteroids();
            fill(0,0,120);
            rect(240, 240, 520, 70);
            if(mouseX < 750 && mouseX > 250 && mouseY < 300 && mouseY > 250){
                fill(0,0,255);
                if (mousePressed){
                    lost=false;
                    lives = 3;
                    px = 500;
                    py = 375;
                    pvx = 0;
                    pvy = 0;
                }
            }
            rect(250, 250, 500, 50);
            fill(0,0,120);
            rect(240, 340, 520, 70);
            if(mouseX<750 && mouseX>250 && mouseY<400&&mouseY>350){
                fill(0,0,255);
                if (mousePressed){
                    shop = true;
                }
            }
            rect(250, 350, 500, 50);
            fill(255);
            textSize(50);
            text("play", 450, 290);
            text("shop", 450, 390);
        }
    }
    public void mousePressed(){
    }

    @Override
    public void keyPressed() {
        // change the requisite entry in the hashmap to true.
        super.keyPressed();
        String k = str(key);
        k = k.toLowerCase(Locale.ROOT);
        key = k.toCharArray()[0];
        if (96 < parseInt(key) && 122 > parseInt(key)){
            keys.replace(key, true);
        }
    }
    public void keyReleased(){
        // change the requisite entry in the hashmap to false.
        super.keyReleased();
        String k = str(key);
        k = k.toLowerCase(Locale.ROOT);
        key = k.toCharArray()[0];
        if (96 < parseInt(key) && 122 > parseInt(key)){
            keys.replace(key, false);
        }
    }

    public static void main(String[] args){
        // set up the offset points array
        offsetPoints.add(parseFloat(0));
        offsetPoints.add(parseFloat(-5));
        offsetPoints.add(parseFloat(0));
        offsetPoints.add(parseFloat(5));
        offsetPoints.add(parseFloat(5));
        offsetPoints.add(parseFloat(2));
        offsetPoints.add(parseFloat(-5));
        offsetPoints.add(parseFloat(2));
        offsetPoints.add(parseFloat(-5));
        offsetPoints.add(parseFloat(20));
        offsetPoints.add(parseFloat(5));
        offsetPoints.add(parseFloat(-2));
        offsetPoints.add(parseFloat(-5));
        offsetPoints.add(parseFloat(-2));
        offsetPoints.add(parseFloat(-5));
        offsetPoints.add(parseFloat(-20));
        upgrades.put("Fire Rate", 0);
        upgrades.put("Damage", 0);
        upgrades.put("BreakerDamage", 3);
        upgrades.put("HullbreakerNum", 0);
        upgrades.put("BreakerHitbox", 1);
        for (int i = 0; i < 15; i++) {
            // set up the asteroids
            asteroids.add(new Asteriod());
        }
        for (int i = 0; i < 26; i++) {
            // set up the keys array
            keys.put(parseChar(97+i), false);
        }
        // setup application and processing
        String[] processingArgs = {"MySketch"};
        MySketch mySketch = new MySketch();
        PApplet.runSketch(processingArgs, mySketch);
    }
    public void updateLife(){
        life.update();
        for (Asteriod a2 : asteroids) {
            if (life.collide(a2.x, a2.y, a2.radii)) {
                life.vy += a2.vy * a2.radii / life.radii;
                life.vx += a2.vx * a2.radii / life.radii;
                a2.vx -= life.vx * life.radii / a2.radii;
                a2.vy -= life.vy * life.radii / a2.radii;
                a2.radii -= 5;
                life.radii -= 5;
                if (life.radii < 5) {
                    life.setup();
                }
                if (a2.radii < 5) {
                    a2.setup();
                }
            }
        }
        if (life.x > 1000 || life.x < 0 || life.y > 750 || life.y < 0) {
            life.setup();
        }
        if (life.collide(px, py, 20)){
            life.setup();
            lives++;
        }
        fill(0, 255, 0);
        ellipse(life.x, life.y, life.radii, life.radii);
        fill(255);
    }
    public void updateAsteroids(){
        for (int i = 0; i < asteroids.size(); i++) {
            Asteriod asteriod = asteroids.get(i);
            asteriod.update();
            for (Vector<Float> v3 : cannon) {
                if (asteriod.collide(v3.get(0), v3.get(1), 10)) {
                    asteriod.radii = asteriod.radii - 20 + 5*upgrades.get("Damage");
                    asteriod.vx += v3.get(2);
                    asteriod.vy += v3.get(3);
                    if (asteriod.radii < 5) {
                        score++;
                        asteriod.setup();
                    }
                    v3.set(0, 2000F);
                    re.add(v3);
                }
            }
            for (int j = i + 1; j < asteroids.size(); j++) {
                Asteriod a2 = asteroids.get(j);
                if (asteriod.collide(a2.x, a2.y, a2.radii)) {
                    asteriod.vy += a2.vy * a2.radii / asteriod.radii;
                    asteriod.vx += a2.vx * a2.radii / asteriod.radii;
                    a2.vx -= asteriod.vx * asteriod.radii / a2.radii;
                    a2.vy -= asteriod.vy * asteriod.radii / a2.radii;
                    a2.radii -= 5;
                    asteriod.radii -= 5;
                    if (asteriod.radii < 5) {
                        asteriod.setup();
                    }
                    if (a2.radii < 5) {
                        a2.setup();
                    }
                }
            }
            if (asteriod.x > 1000 || asteriod.x < 0 || asteriod.y > 750 || asteriod.y < 0) {
                asteriod.setup();
            }
            if (asteriod.collide(px, py, 10)){
                if(lives == 0) {
                    lost = true;
                }
                else{
                    lives = lives - 1;
                    asteriod.setup();
                }
            }
            fill(255);
            ellipse(asteriod.x, asteriod.y, asteriod.radii, asteriod.radii);
        }
    }
    public void updatePlayer(){
        if (keys.get('w')) {
            pvy += sin(ph) * 0.5;
            pvx += cos(ph) * 0.5;
        }
        if (keys.get('s')) {
            pvy -= sin(ph) * 0.25;
            pvx -= cos(ph) * 0.25;
        }
        if (keys.get('a')) {
            ph -= 0.1;
        }
        if (keys.get('d')) {
            ph += 0.1;
        }
        if (keys.get('e')) {
            if (t > 20 - upgrades.get("Fire Rate")) {
                t = 0;
                Vector<Float> v1;
                if (re.size() == 0) {
                    v1 = new Vector<>();
                    v1.add(px);
                    v1.add(py);
                    float b = ph;
                    v1.add(5 * cos(b) + pvx);
                    v1.add(5 * sin(b) + pvy);
                    cannon.add(v1);
                }
                else {
                    v1 = re.get(0);
                    v1.set(0, px);
                    v1.set(1, py);
                    v1.set(2, 5 * cos(ph) + pvx);
                    v1.set(3, 5 * sin(ph) + pvy);
                    re.remove(v1);
                }
            } else {
                t++;
            }
        }
        if (pow(pvx, 2) + pow(pvy, 2) > 225) {
            float r = atan2(pvy, pvx);
            pvx = 15 * cos(r);
            pvy = 15 * sin(r);
        }
        if (py < 0) {
            py = 750;
            trail = new ArrayList<>();
            Vector<Float> t = new Vector<>();
            t.add(px);
            t.add(py);
            trail.add(t);
        }
        if (px < 0) {
            px = 1000;
            trail = new ArrayList<>();
            Vector<Float> t = new Vector<>();
            t.add(px);
            t.add(py);
            trail.add(t);
        }
        if (py > 750) {
            py = 0;
            trail = new ArrayList<>();
            Vector<Float> t = new Vector<>();
            t.add(px);
            t.add(py);
            trail.add(t);
        }
        if (px > 1000) {
            px = 0;
            trail = new ArrayList<>();
            Vector<Float> t = new Vector<>();
            t.add(px);
            t.add(py);
            trail.add(t);
        }
        py = pvy + py;
        px = pvx + px;
    }
    public void updateHullbreakers(){
        for(Hullbreaker hullbreaker : hullbreakers){
            hullbreaker.AI();
            float h = hullbreaker.getH();
            fill(255);
            triangle(hullbreaker.getX() + 5 * sin(h), hullbreaker.getY() - 5 * cos(h),
                    hullbreaker.getX() + 5 * cos(h) - 5 * sin(h), 5 * sin(h) + hullbreaker.getY() + 5 * cos(h),
                    hullbreaker.getX() - 5 * cos(h) - 5 * sin(h), -5 * sin(h) + hullbreaker.getY() + 5 * cos(h));
            fill(0,0);
            strokeWeight(2);
            stroke(100,100,255);
            ellipse(hullbreaker.getX(), hullbreaker.getY(), upgrades.get("BreakerHitbox")*10, upgrades.get("BreakerHitbox")*10);
            strokeWeight(0);
            fill(255,255);
        }
    }
}