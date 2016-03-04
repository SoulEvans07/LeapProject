package bluetooth;


import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.IOException;

public class KeyFunc extends JFrame {
    public static String nxt_name = "LEGO_06";
    public static String connected = "Connected";
    public static String waiting = "Waiting...";
    public static String closing = "Closing...";

    public JPanel panel;
    public JLabel connLabel;

    public NXTInfo brick;
    public NXTComm nxtComm;

    public KeyFunc(){
        this.setLocationRelativeTo(null);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new Exit());
        initPanel();

        this.setSize(100 + this.getInsets().right + this.getInsets().left,
                     100 + this.getInsets().top + this.getInsets().bottom);
        this.setResizable(false);

        boolean search = true;
        try {
            nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
            connLabel.setText(waiting);
            NXTInfo nxtList[] = nxtComm.search("NXT");
            while(search){
                if(nxtList.length != 0)
                    for(int i = 0; i < nxtList.length; i++) {
                        System.out.println("Device found: ");
                        System.out.println("Name: " + nxtList[i].name);
                        System.out.println("Address: " + nxtList[i].deviceAddress);
                        if (nxtList[i].name.equals(nxt_name)) {
                            brick = new NXTInfo(nxtList[i].protocol, nxtList[i].name, nxtList[i].deviceAddress);
                            search = false;
                        }
                    }
                if(search)
                    nxtList = nxtComm.search("NXT");
            }

            nxtComm.open(brick);
            connLabel.setText(connected);

        } catch (NXTCommException e) {
            System.out.println("[Error] NXTComError \t" + e);
        }

        this.addKeyListener(new KeyList());

        this.setVisible(true);
    }

    public void initPanel(){
        panel = new JPanel();
        connLabel = new JLabel("start");
        panel.add(connLabel);

        this.setContentPane(panel);
    }

    public class KeyList extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e){
            System.out.print(e.getKeyChar());
            DataOutputStream dataOut = (DataOutputStream) nxtComm.getOutputStream();

            try {
                dataOut.writeChar(e.getKeyChar());
            } catch (IOException e1) {
                System.out.println("[Error] IOError \t" + e1 + "\n char: " + e);
            }
        }
    }

    public class Exit extends WindowAdapter{
        @Override
        public void windowClosing(WindowEvent e) {
            connLabel.setText(closing);
            try {
                nxtComm.close();
            } catch (IOException e1) {
                System.out.println("[Error] IOError \t" + e);
            }
            Main.exit();
        }
    }
}
