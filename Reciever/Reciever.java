import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

import java.io.DataInputStream;
import java.io.IOException;

public class Reciever {
    public static final int pow = 70;
    public static boolean check = false;

    public static String connected = "Connected";
    public static String waiting = "Waiting...";
    public static String closing = "Closing...";

    public static char prev = "";

    public static void main(String[] args){
        NXTMotor motrA = new NXTMotor(MotorPort.A);
        NXTMotor motrB = new NXTMotor(MotorPort.B);
        NXTMotor motrC = new NXTMotor(MotorPort.C);

        boolean stop = false;
        boolean clawOpen = false;

        // start Bluetooth connection
        LCD.drawString(waiting,0,0);
        NXTConnection conn = Bluetooth.waitForConnection();
        LCD.clear();
        LCD.drawString(connected,0,0);


        
        DataInputStream dataIn = conn.openDataInputStream();
        while(!stop){
            backPressed();
            //processFullStop(dataIn.readChar());
            processSemiFlow(dataIn.readChar());
        }

        // Closing
        LCD.clear();
        LCD.drawString(closing,0,0);
        conn.close();
        LCD.clear();
    }

    public void processSemiFlow(char c){
        prev = c;
        try{
            switch (c) {
                case 'w':   // motorB forward
                    if(prev != 'w'){
                        stopMotors();
                        motorB.power(pow);
                        motorB.forward();
                    }
                    break;
                case 's':   // motorB backward
                    if(prev != 's'){
                        stopMotors();
                        motorB.power(pow);
                        motorB.backward();
                    }
                    break;
                case 'a':   // motorA backward
                    if(prev != 'a'){
                        stopMotors();
                        motorA.power(pow);
                        motorA.backward();
                    }
                    break;
                case 'd':   // motorA forward
                    if(prev != 'd'){
                        stopMotors();
                        motorA.power(pow);
                        motorA.forward();
                    }
                    break;
                case ' ':
                    motorC.power(pow);
                    if(clawOpen){
                        motorC.backward();
                    } else {
                        motorC.forward();
                    }
                    // prev = prev;
                    break;
                case '.':
                    // ignore
                    // prev = prev;
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            System.out.println("[Error] IOError \t" + e);
        }
    }

    public void processFullStop(char c){
        try{
            switch (c) {
                case 'w':
                    motorB.power(pow);
                    motorB.forward();
                    break;
                case 's':
                    motorB.power(pow);
                    motorB.backward();
                    break;
                case 'a':
                    motorA.power(pow);
                    motorA.backward();
                    break;
                case 'd':
                    motorA.power(pow);
                    motorA.forward();
                    break;
                case ' ':
                    motorC.power(pow);
                    if(clawOpen){
                        motorC.backward();
                    } else {
                        motorC.forward();
                    }
                    break;
                case '.':
                    motorA.stop();
                    motorB.stop();
                    motorC.stop();
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            System.out.println("[Error] IOError \t" + e);
        }
    }

    public void stopMotors(){
        motorA.stop();
        motorB.stop();
        motorC.stop();
    }

    public void backPressed(){
        int t = Button.readButtons(); // 1: orange, 4: right, 2: left, 8: down
        if(t == 8)
            stop = true;
    }
}
