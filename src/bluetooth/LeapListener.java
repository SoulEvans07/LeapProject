package bluetooth;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Frame;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;

public class LeapListener extends Listener {
    JLabel text;
    OutputStream out;
    Robot test;

    float maxX, maxY, maxZ;
    float minX, minY, minZ;
    float x, y, z;

    boolean started = false;
    char flag = '.';

    Vector max, min;

    public LeapListener(JLabel label, OutputStream leapOut){
        text = label;
        out = leapOut;
    }

    @Override
    public void onConnect(Controller controller) {
        System.out.println(References.lConnected);
        text.setText(References.lConnected);
        controller.setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
        maxX = maxY = maxZ = minX = minY = minZ = 0;
    }

    public void onFrame(Controller controller) {
        Frame frame = controller.frame();
        Hand firstHand;
        Hand secondHand;

        if(frame.hands().count() != 0) {
            firstHand = frame.hands().get(0);
            boolean valid = firstHand.fingers().extended().count() != 0;
            //secondHand = frame.hands().get(1);
            //System.out.println(secondHand.toString());
            //System.out.println(secondHand.palmPosition());

            //Vector thumb = firstHand.fingers().get(0).tipPosition();
            //Vector index = firstHand.fingers().get(1).tipPosition();

            Vector palm = firstHand.palmPosition();
            //Vector clawPalm;
            //float clawDist = -1;
            /*if(secondHand != null && secondHand.isValid()) {
                System.out.print("2 in\n");
                clawPalm = secondHand.palmPosition();
                clawDist = palm.distanceTo(clawPalm);
            } else {
                clawDist = -1;
                System.out.print("2 out\n");
            }*/

            x = palm.getX();
            y = palm.getY();
            z = palm.getZ();

            max = new Vector(maxX, maxY, maxZ);
            min = new Vector(minX, minY, minZ);

            if(minX > x) {
                minX = x;
//                System.out.print("min " + min.toString() + " ");
//                System.out.print("max " + max.toString() + "\n");
            }
            if(minY > y) {
                minY = y;
//                System.out.print("min " + min.toString() + " ");
//                System.out.print("max " + max.toString() + "\n");
            }
            if(minZ > z) {
                minZ = z;
//                System.out.print("min " + min.toString() + " ");
//                System.out.print("max " + max.toString() + "\n");
            }

            if(maxX < x) {
                maxX = x;
//                System.out.print("min " + min.toString() + " ");
//                System.out.print("max " + max.toString() + "\n");
            }
            if(maxY < y) {
                maxY = y;
//                System.out.print("min " + min.toString() + " ");
//                System.out.print("max " + max.toString() + "\n");
            }
            if(maxZ < z) {
                maxZ = z;
//                System.out.print("min " + min.toString() + " ");
//                System.out.print("max " + max.toString() + "\n");

            }

            int down = 200;
            int up = 350;
            int left = 100;
            int right = -100;
            int forw = 100;
            int back = -80;

            if(!started && right < x && x < left && down < y && y < up && back < z && z < forw)
                started = true;


            if(started && valid) {
                if (x < right)
                    send('d');
                else if (x > left)
                    send('a');
                else if (y > up)
                    send('w');
                else if (y < down)
                    send('s');
                else if(z > forw)
                    send('x');
                else if(z < back)
                    send('y');
                else
                    send('.');
            } else
                send('.');


            //System.out.print(palm.toString() + "\n");
            //if(clawDist > 0)
            //    System.out.print(clawDist + "\n");
            try {
                Thread.sleep(17);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.print("min " + min.toString() + " ");
            //System.out.print("max " + max.toString() + "\n");
        } else {
            firstHand = null;
            send('.');
            started = false;
        }
    }

    public void send(char s){
        if(flag != s) {
            flag = s;
            try {
                out.write(s);
                out.write(s);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
