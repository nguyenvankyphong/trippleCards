package Server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;  
import java.util.*;  
import java.net.*;  
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import model.Deck;
/*Chuong trinh chat don gian  
 Server nhan message tu Client  
 */  
public class Server extends JFrame implements ActionListener{  
    private JButton close, setPort, stop;
    public JTextArea user;
    private ServerSocket server;
    public Hashtable<String, ClientConnect> listUser;
    private DataOutputStream dos;
    private DataInputStream dis;
    public int numOfUser;
    public int numOfStartingUser;
    public int numOfOpeningUser;
    ArrayList<Integer> currentScore;
    ArrayList<String> currentCards;
    
    public Server()  {
        super("Triple Cards : Server");
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
        
        JTextField port = new JTextField(20);
        setPort = new JButton("Set");
        setPort.addActionListener(this);
        
        JPanel p_head = new JPanel(new BorderLayout());
        JPanel p_port = new JPanel();
        
        p_port.add(new JLabel("Port:"));
        p_port.add(port);
        p_port.add(setPort);
        p_head.add(p_port, BorderLayout.NORTH);
        
        
        p_head.add(new JLabel("Status: \n"), BorderLayout.SOUTH, SwingConstants.CENTER);
        p_head.add(new JPanel(), BorderLayout.EAST);
        p_head.add(new JPanel(), BorderLayout.WEST);
        add(p_head, BorderLayout.NORTH);
        
        
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
        
        stop = new JButton("Stop server");
        stop.addActionListener(this);
        p2.add(stop);
        
        add(p2, BorderLayout.SOUTH);
        
        user.append("Server has opened.\n");
    }
    
    private void go () {
        try {
            System.out.println("Server.Server.go()");
            listUser = new Hashtable<String, ClientConnect>();
            server = new ServerSocket(6666);
            
            numOfUser = 0;
            numOfStartingUser = 0;
            numOfOpeningUser = 0;
            
            user.append("Server started.\n");
            
            while (true) {
                
                Socket soc = server.accept();
                System.out.println("test");
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
        if (e.getSource() == close) {
            try {
            server.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        } else if (e.getSource() == setPort) {
//            this.go();
        } 
        else if (e.getSource() == stop) {
            
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
    
    public void sendEach() {
        System.out.println("been to sendEach()");
        Enumeration e = listUser.keys();
        String name = null;
        Deck deck = new Deck();
        ArrayList<Integer> hand = new ArrayList<>();
        Random rand = new Random();
        currentScore = new ArrayList<>();
        currentCards = new ArrayList<>();
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
            currentScore.add(score%10);
            String string = hand.get(0) + " " +hand.get(1) + " " + hand.get(2);
            name = (String) e.nextElement();
            listUser.get(name).sendMSG("7", string);
            currentCards.add(string);
            string = "";
            hand.clear();
            score = 0;
        }
    }
    
    public int haveHighestScore() {
        int a = 0;
        for (int i = 0; i<numOfUser; i++) {
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
                listUser.get(name).sendMSG("9", from +" "+ getHand(from) + " " + getScore(from));
            }
            else listUser.get(name).sendMSG("9", "Yours "+ getHand(from) + " " + getScore(from));
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
    
    public String getHand(String player) {
        Enumeration e = listUser.keys();
        int i = 0;
        String name = null;
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            if (name.compareTo(player) == 0) 
                return currentCards.get(i);
            i++;
        }
        return null;
    }
    
    public void sendResult() {
        Enumeration e = listUser.keys();
        String name = null;
        int i =0;
        while (e. hasMoreElements()) {
            name = (String) e.nextElement();
            if (currentScore.get(i)< this.haveHighestScore())
                listUser.get(name).sendMSG("6", "You lose");
            else listUser.get(name).sendMSG("6", "You win");
            i++;
        }
    }
}

