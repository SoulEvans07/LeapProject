package bluetooth;


import com.leapmotion.leap.Controller;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class KeyFunc extends JFrame {
    public boolean open = false;

    public JPanel panel;
    public JPanel bluetoothPanel;
    public JPanel leapPanel;

    public JLabel blueConnLabel;
    public JLabel leapConnLabel;

    public NXTInfo brick;
    public NXTComm nxtComm;

    public LeapListener listener;
    public Controller controller;

    public KeyFunc() throws InterruptedException {
        this.setLocationRelativeTo(null);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new Exit());
        initPanel();

        this.setSize(300 + this.getInsets().right + this.getInsets().left,
                100 + this.getInsets().top + this.getInsets().bottom);
        this.setResizable(false);

        this.setVisible(true);
        boolean search = true;
        // TODO : enable after test
        try {
            nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
            blueConnLabel.setText(References.bWaiting);
            System.out.println("search start:");
            NXTInfo nxtList[] = null;
            do {
                nxtList = nxtComm.search(References.nxt_name);
            }while(nxtList.length == 0);

            brick = new NXTInfo(nxtList[0].protocol, nxtList[0].name, nxtList[0].deviceAddress);

            nxtComm.open(brick);

            blueConnLabel.setText(References.bConnected + nxtList[0].name);
            open = true;

            /*OutputStream stopOut = nxtComm.getOutputStream();
            try {
                while(open){
                    stopOut.write('.');
                    stopOut.write('.');
                    stopOut.flush();
                    Thread.sleep(500);
                }
            } catch (IOException e) {
                System.out.println("[Error] IOError in stopOut w/ char: .");
            }*/

            OutputStream leapOut = nxtComm.getOutputStream();
            controller = new Controller();
            listener = new LeapListener(leapConnLabel, leapOut);
            controller.addListener(listener);

        } catch (NXTCommException e) {
            System.out.println("[Error] NXTComError \t" + e);
        }
    }

    public void initPanel(){
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        initBluetoothPanel();
        panel.add(bluetoothPanel, BorderLayout.PAGE_START);

        initLeapPanel();
        panel.add(leapPanel, BorderLayout.PAGE_END);

        this.setContentPane(panel);
        this.addKeyListener(new KeyList());
    }

    public void initBluetoothPanel(){
        bluetoothPanel= new JPanel();
        blueConnLabel = new JLabel("blue");
        bluetoothPanel.add(blueConnLabel);
    }

    public void initLeapPanel(){
        leapPanel = new JPanel();
        leapConnLabel = new JLabel(References.lWaiting);
        leapPanel.add(leapConnLabel);
    }

    public class KeyList extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e){
            if(open) {
                if("wasd. yx-".contains(""+e.getKeyChar())) {
                    //System.out.print(e.getKeyChar() + "\n");
                    OutputStream dataOut = nxtComm.getOutputStream();
                    OutputStreamWriter writer = new OutputStreamWriter(dataOut);
                    try {
                        dataOut.write(e.getKeyChar());
                        dataOut.write(e.getKeyChar());
                        dataOut.flush();
                    } catch (IOException e1) {
                        System.out.println("[Error] IOError \t" + e1 + "\n char: " + e.getKeyChar());
                    }
                }
            }
        }
    }

    public class Exit extends WindowAdapter{
        @Override
        public void windowClosing(WindowEvent e) {
            blueConnLabel.setText(References.bClosing);
            try {
                if(open) {
                    OutputStream finalOut = nxtComm.getOutputStream();
                    // NXT leallitas
                    finalOut.write('-');
                    finalOut.write('-');
                    finalOut.flush();

                    // kapcsolatbontás
                    open = false;
                    System.out.println();
                    nxtComm.close();
                }
                controller.removeListener(listener);
            } catch (IOException e1) {
                System.out.println("[Error] IOError \t" + e);
            }
            Main.exit();
        }
    }
}
