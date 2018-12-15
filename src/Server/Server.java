package Server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;  
import java.util.*;  
import java.net.*;  
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import model.Card;
import model.Deck;
/*Chuong trinh chat don gian  
 Server nhan message tu Client  
 */  
public class Server extends JFrame implements ActionListener{  
    private JButton close;
    private JButton sendName, startGame;
    public JTextArea user;
    private ServerSocket server;
    public Hashtable<String, ClientConnect> listUser;
    private DataOutputStream dos;
    private DataInputStream dis;
    public int numOfUser;
    public int numOfStartingUser;
    ArrayList<Integer> currentScore;
    
    public Server()  {
        super("Chat : Server");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    server.close();
                    System.exit(0);
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
        });
        setSize(400, 400);
        addItem();
        setVisible(true);
    }
    
    private void addItem() {
        setLayout(new BorderLayout());
        
        add(new JLabel("Status: \n"), BorderLayout.NORTH);
        add(new JPanel(), BorderLayout.EAST);
        add(new JPanel(), BorderLayout.WEST);
        
        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        
        user = new JTextArea(10, 20);
        user.setEditable(false);
        p1.add(new JScrollPane(user), BorderLayout.CENTER);
        add(p1, BorderLayout.CENTER);
        
        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());
        close = new JButton("Close Server");
        close.addActionListener(this);
        p2.add(close, BorderLayout.NORTH);
        
        sendName = new JButton("Send the name");
        sendName.addActionListener(this);
        p2.add(sendName, BorderLayout.SOUTH);
        
        startGame = new JButton("Start game");
        startGame.addActionListener(this);
        p2.add(startGame);
        
        add(p2, BorderLayout.SOUTH);
        
        user.append("Server has opened.\n");
    }
    
    private void go () {
        try {
            listUser = new Hashtable<String, ClientConnect>();
            server = new ServerSocket(6666);
            numOfUser = 0;
            numOfStartingUser = 0;
            
            user.append("Server started.\n");
            while (true) {
                Socket soc = server.accept();
                dos = new DataOutputStream(soc.getOutputStream());
                dis = new DataInputStream(soc.getInputStream());
                System.out.println("connected");
                new ClientConnect(this, soc);
                numOfUser++;
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] s) {
        new Server().go();
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendName) {
            sendEach();
        } else if (e.getSource() == close) {
            try {
            server.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        } else if (e.getSource() == startGame) {
            System.out.println("button start game");
            sendStartingNotif();
        }
        
        
    }
    
    public void sendAll(String from, String msg) {
        
        Enumeration e = listUser.keys();
        String name = null;
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            if (name.compareTo(from) != 0) {
                listUser.get(name).sendMSG("3", msg);
            }
        }
    }
    
    public void requestAddSeat(String from) {
        Enumeration e = listUser.keys();
        String name = null;
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            if (name.compareTo(from) != 0) {
                listUser.get(name).sendMSG("7", from);
            }
        }
    }
    
    public void sendAllUpdate(String from) {
        Enumeration e = listUser.keys();
        String name = null;
        while (e. hasMoreElements()) {
            name = (String) e.nextElement();
            if (name.compareTo(from) != 0) {
                listUser.get(name).sendMSG("4", getAllName());
                
            }
        }
    }
    
    public String getAllName () {
        Enumeration e = listUser.keys();
        String name = "";
        while (e. hasMoreElements()) {
            name+= (String) e.nextElement() + "\n";
        }
        return name;
    }
    
    public void sendNotif(String data) {
        try {
            dos.writeUTF(data);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void SendNotif(String msg1, String msg2) {
        sendNotif(msg1);
        sendNotif(msg2);
    }
    
    public void sendName() {
        SendNotif("6", getAllName());
    }
    
    public void sendEach() {
        System.out.println("been to sendEach()");
        Enumeration e = listUser.keys();
        String name = null;
        Deck deck = new Deck();
        ArrayList<Integer> hand = new ArrayList<>();
        Random rand = new Random();
        currentScore = new ArrayList<>();
        int score = 0;
        
        while (e.hasMoreElements()) {
            int i = 0;
            while (i<3) {
                int a = rand.nextInt(52);
                if (deck.deakArr.get(a).getStatus() ==0) {
                    hand.add(a);
                    System.out.println("hand.size: " + hand.size());
                    deck.deakArr.get(a).setStatus();
                    score+= deck.deakArr.get(a).point;
                    i++;
                } 
            }
            String string = hand.get(0) + " " +hand.get(1) + " " + hand.get(2);
            name = (String) e.nextElement();
            listUser.get(name).sendMSG("7", string);
            string = "";
            hand.clear();
            currentScore.add(score%10);
            score = 0;
        }
    }
    
    public void sendStartingNotif() {
        Enumeration e = listUser.keys();
        String name = null;
        while (e. hasMoreElements()) {
            name = (String) e.nextElement();
            listUser.get(name).sendMSG("", "");
            }
        }
    
    public void sendReady() {
        Enumeration e = listUser.keys();
        String name = null;
        while (e. hasMoreElements()) {
            name = (String) e.nextElement();
            listUser.get(name).sendMSG("6", "");
        }
    }
    
    public int getHighestScore() {
        int a = 0;
        for (int i = 0; i<3; i++) {
            a = (currentScore.get(i)<a) ? a : currentScore.get(i);
        }
        return a;
    }
    
//    public String getWinner() {
//        
//    }
    
    public void sendScore(String from) {
        int i = 0;
        
        Enumeration e = listUser.keys();
        String name = null;
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            if (name.compareTo(from) != 0) {
                listUser.get(name).sendMSG("3", from +" : "+ getScore(from));
            }
            else listUser.get(name).sendMSG("3", "Your score : "+ getScore(from));
            i++;
        }
    }
    
    public int getScore(String player) {
        Enumeration e = listUser.keys();
        int i = 0;
        String name = null;
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            if (name.compareTo(player) == 0) 
                return currentScore.get(i);
            i++;
        }
        return -1;
    }
    
//    public void sendWinner() {
//        Enumeration e = listUser.keys();
//        int i = 0;
//        String name = null;
//        while (e.hasMoreElements()) {
//            name = (String) e.nextElement();
//            if (currentScore.get(i) == getHighestScore())
//                listUser.get(name).sendMSG("3", name +" : "+ score);
//            if (name.compareTo(from) != 0) {
//                listUser.get(name).sendMSG("3", name +" : "+ score);
//            }
//            i++;
//        }
//    }
}

