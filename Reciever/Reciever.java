import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.util.Delay;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.System;

public class Reciever {
    public static final int pow = 60;
    public static final int lowPow = 40;
    public static boolean check = false;

    public static String connected = "Connected";
    public static String waiting = "Waiting...";
    public static String closing = "Closing...";

    public static NXTMotor motorA;
    public static NXTMotor motorB;
    public static NXTMotor motorC;

    public static char prev = '.';
    public static boolean stop;
    public static boolean clawOpen;

    public static void main(String[] args) {
        motorA = new NXTMotor(MotorPort.A);
        motorB = new NXTMotor(MotorPort.B);
        motorC = new NXTMotor(MotorPort.C);

        stop = false;
        clawOpen = false;

        // start Bluetooth connection
        LCD.drawString(waiting, 0, 0);
        NXTConnection conn = Bluetooth.waitForConnection();
        LCD.clear();
        LCD.drawString(connected, 0, 0);


        DataInputStream dataIn = conn.openDataInputStream();
        System.out.println();
        System.out.println("startedStream:\n");
        while (!stop) {
            try {
                //processSemiFlow(dataIn.readChar());
                processFullStop(dataIn.readChar());
                //System.out.println("run " + stop + "\n");
            } catch (IOException e) {
                System.out.print("\n[Error] IOError 	" + e.getMessage());
                stop = true;
            }
        }
        System.out.println("\nSTOP!");

        // Closing
        LCD.clear();
        LCD.drawString(closing, 0, 0);
        conn.close();
        LCD.clear();
    }

    public static void processSemiFlow(char c) {
        prev = c;
        System.out.print(c);
        switch (c) {
            case 'w':   // motorB forward
                if (prev != 'w') {
                    stopMotors();
                    motorB.setPower(pow);
                    motorB.forward();
                }
                break;
            case 's':   // motorB backward
                if (prev != 's') {
                    stopMotors();
                    motorB.setPower(pow);
                    motorB.backward();
                }
                break;
            case 'a':   // motorA backward
                if (prev != 'a') {
                    stopMotors();
                    motorA.setPower(pow);
                    motorA.backward();
                }
                break;
            case 'd':   // motorA forward
                if (prev != 'd') {
                    stopMotors();
                    motorA.setPower(pow);
                    motorA.forward();
                }
                break;
            case ' ':
                motorC.setPower(pow);
                if (clawOpen) {
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
    }

    public static void processFullStop(char c) {
        int i = c;
        //System.out.print(c + " int: " + i + "\n");
        switch (i) {
            case 30583: //'w':
                motorB.setPower(pow);
                motorB.forward();
                System.out.print("w");
                break;
            case 29555: //'s':
                motorB.setPower(lowPow);
                motorB.backward();
                System.out.print("s");
                break;
            case 24929: //'a':
                motorA.setPower(pow);
                motorA.backward();
                System.out.print("a");
                break;
            case 25700: //'d':
                motorA.setPower(pow);
                motorA.forward();
                System.out.print("d");
                break;
            case 8224: //' ':
                motorC.setPower(pow);
                if (clawOpen) {
                    motorC.backward();
                    Delay.msDelay(600);
                    clawOpen = !clawOpen;
                    System.out.print("\nclose\n");
                } else {
                    motorC.forward();
                    Delay.msDelay(600);
                    clawOpen = !clawOpen;
                    System.out.print("\nopen\n");
                }
                break;
            case 30840: // 'x'
                motorC.setPower(lowPow);
                motorC.forward();
                System.out.print("\nopen\n");
                break;
            case 31097: // 'y'
                motorC.setPower(lowPow);
                motorC.backward();
                System.out.print("\nclose\n");
                break;
            case 11565: // '-'
                stop = true;
                break;
            case 11822: //'.':
                motorA.stop();
                motorB.stop();
                motorC.stop();
                System.out.print(".");
                break;
            default:
                System.out.println("\nerr: " + c + " int: " + i + "\n");
                stop = true;
                break;
        }
    }

    public static void stopMotors() {
        motorA.stop();
        motorB.stop();
        motorC.stop();
    }

    public static void backPressed() {
        int t = Button.readButtons(); // 1: orange, 4: right, 2: left, 8: down
        if (t == 8)
            stop = true;
    }
}
