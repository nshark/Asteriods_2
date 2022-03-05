package com.company;

import java.util.ArrayList;
import java.util.Vector;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class ResourceAsteriod extends Asteriod{
    public float Dspin = this.ran.nextFloat((float) Math.PI/3);
    public float h = 0;
    public boolean exist = false;
    public ArrayList<ArrayList<Float>> offsets = new ArrayList<>();
    ResourceAsteriod(){this.setup();}
    public void tick(MySketch g){
        if (x > 1000 || x < 0 || y > 750 || y < 0){
            this.setup();
        }
        this.update();
        ArrayList<Float> offsetRotated = new ArrayList<>();
        h += Dspin;
        for (Vector<Float> v3 : g.cannon) {
            if (collide(v3.get(0), v3.get(1), 10) && !g.lost) {
                g.score+=10;
                setup();
                v3.set(0, 2000F);
                g.re.add(v3);
            }
        }
        for (ArrayList<Float> offset : offsets) {
            float x1 = offset.get(0);
            float y1 = offset.get(1);
            offsetRotated.add((float) (x1 * cos(h) - y1 * sin(h)));
            offsetRotated.add((float) (x1 * sin(h) + y1 * cos(h)));
        }
        g.strokeWeight(0);
        g.fill(255, 255);
        g.ellipse(x, y, radii, radii);
        for (int i = 0; i < offsets.size(); i++) {
            g.fill(255,0,0);
            g.ellipse(offsetRotated.get(2*i) + x, offsetRotated.get(2*i + 1) + y, offsets.get(i).get(2), offsets.get(i).get(2));
        }
    }

    @Override
    public void setup() {
        this.exist = false;
        super.setup();
        this.radii = ran.nextInt(25,95);
        offsets = new ArrayList<>();
        for (int i = 0; i < this.radii/5; i++) {
            ArrayList<Float> t = new ArrayList<>();
            t.add(ran.nextFloat(this.radii/3*-1, this.radii/3));
            t.add(ran.nextFloat(this.radii/3*-1, this.radii/3));
            t.add(ran.nextFloat(10));
            offsets.add(t);
        }
        Dspin = this.ran.nextFloat((float) Math.PI/10);
    }
}
