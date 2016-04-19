package bluetooth;

import com.leapmotion.leap.*;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

public class LeapListener extends Listener {
    JLabel text;
    OutputStream out;

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

            if(!started && -100 < x && x < 100 && 200 < y && y < 400 && -80 < z && z < 80)
                started = true;

            if(started && valid) {
                if (x < -100)
                    send('d');
                else if (x > 100)
                    send('a');
                else if (y > 350)
                    send('w');
                else if (y < 200)
                    send('s');
                else if(z > 100)
                    send('x');
                else if(z < -80)
                    send('y');
                else
                    send('.');
            } else
                send('.');


            //System.out.print(palm.toString() + "\n");
            //if(clawDist > 0)
            //    System.out.print(clawDist + "\n");
            try {
                Thread.sleep(300);
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
