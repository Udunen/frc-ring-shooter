import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
    RootFinder rootFinder = new RootFinder();
    public static void main(String[] args) {
        
        // everything is in meters, radians, and seconds

        // anything divided by 39.37 is inches converted to meters
        // anything divided by 3.281 is feet converted to meters
        
        double arm_length = 24.727914/39.37;
        double shooter_length = 5.639686/39.37;
        
        double a_y = -9.8; // gravity
        double v_y = 0;
        double p_y = -10; // vertical distance from target
        double a_x = 0; // pretend this doesn't exist because I don't feel like doing air resistance
        double v_x = 0;
        double p_x = -12; // horizontal distance from target
        
        double shootingvelo_y = Math.sqrt(p_y * a_y * 2);
        double shootingvelo_x = p_x/(shootingvelo_y/a_y);
        double shootingvelo = Math.sqrt(Math.pow(shootingvelo_x, 2) + Math.pow(shootingvelo_y, 2));
        if(shootingvelo > 5700) {
            shootingvelo = 5700;
        }
        double rpm = (30*shootingvelo)/(Math.PI*(2.0/39.37));
        // double shootingvelo = (2.0*Math.PI/60.0)*(2.0/39.37)*1280.0; // right
        
        double t4 = (Math.pow(a_x, 2) + Math.pow(a_y, 2))/4;
        double t3 = (a_x*v_x + a_y*v_y);
        double t2 = (Math.pow(v_x, 2) + p_x*a_x + Math.pow(v_y, 2) + p_y*a_y - Math.pow(shootingvelo, 2));
        double t1 = 2*(p_x*v_x + p_y*v_y);
        double t0 = (Math.pow(p_x, 2) + Math.pow(p_y, 2));


        List<Double> roots = RootFinder.rootFinder(Arrays.asList(t4, t3, t2, t1, t0), 0.01);
        for(double root: roots) {
            root = Math.abs(root);
        }
        double t = -1;

        for (int i = roots.size() - 1; i >= 0; i--) {
            if (!(roots.get(i) > 0.0)) {
                roots.set(i,0.0);
            }
        }

        double minRoot = Double.MAX_VALUE;
        for (double root : roots) {
            if (root < minRoot && root > 0.0) {
                minRoot = root;
            }
        }


        if (minRoot == Double.MAX_VALUE) {
            System.out.println("no solutions");
            return;
        }
        t = minRoot;

        // double maxRoot = Double.MIN_VALUE;
        // System.out.println(roots);
        // for (double root : roots) {
        //     if (root > maxRoot) {
        //         maxRoot = root;
        //     }
        // }


        // if (maxRoot == Double.MIN_VALUE) {
        //     System.out.println("no solutions");
        //     return;
        // }
        // t = maxRoot;

        double p_aimX = -(p_x + v_x*t + (a_x*(Math.pow(t, 2)))/2.0);
        double p_aimY = -(p_y + v_y*t + (a_y*(Math.pow(t, 2)))/2.0);
        double shooting_theta = Math.atan(p_aimY / p_aimX);
        double arm_theta = shooting_theta + (35.0*Math.PI/180.0);
        // System.out.println(t);

        try {
            FileWriter desmos = new FileWriter("desmos_stuffs.txt");
            desmos.write("(" + p_aimX + ", " + p_aimY + ")\n");
            desmos.write("(" + -p_x + ", " + -p_y + ")\n");
            desmos.write("f\\left(x\\right)=" + p_aimY / p_aimX + "x-" + "\\frac{" + (-a_y) + "x^{2}}{2\\cdot\\left(" + shootingvelo + "\\right)^{2}\\cdot\\cos^{2}\\left(" + shooting_theta + "\\right)}\n");
            desmos.write("a_{degrees}=" + (arm_theta*(180.0/Math.PI)) + "\n");
            desmos.write("\\operatorname{polygon}((0,0),(" + -shooter_length * Math.cos(shooting_theta) + "," + -shooter_length * Math.sin(shooting_theta) + "))\n");
            desmos.write("\\operatorname{polygon}((" + -shooter_length * Math.cos(shooting_theta) + "," + -shooter_length * Math.sin(shooting_theta) + "),(" + arm_length * Math.cos(arm_theta) + "," + -arm_length * Math.sin(arm_theta) + "))\n");
            desmos.write("r_{pm}=" + rpm);
            desmos.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
