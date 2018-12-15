package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import model.Card;
import model.Deck;

public class Client extends JFrame implements ActionListener{
    private JButton send,clear,exit,login,logout,open, start;
    private JPanel p_login,p_chat, p_board, mySeat, cards;
    private JTextField nick,nick1,message;
    private JTextArea msg,online;
    
    private Socket client;
    private DataStream dataStream;
    private DataOutputStream dos;
    private DataInputStream dis;
    private ArrayList<Card> currentCard;
    
    
    public Client(){
        super("Chat chit : Client");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                        exit();
                }	
        });
        setSize(900, 600);
        addItem();
        setVisible(true);
    }
    private void addItem() {
        setLayout(new BorderLayout());

        exit = new JButton("Exit");
        exit.addActionListener(this);
        send = new JButton("Send");
        send.addActionListener(this);
        clear = new JButton("Clear");
        clear.addActionListener(this);
        login= new JButton("Login");
        login.addActionListener(this);
        logout= new JButton("Exit");
        logout.addActionListener(this);
        start= new JButton("New game");
        start.addActionListener(this);

        
        
        p_chat = new JPanel();
        p_chat.setLayout(new BorderLayout());

        JPanel p1 = new JPanel();
        p1.setLayout(new FlowLayout(FlowLayout.LEFT));
        nick = new JTextField(20);
        p1.add(new JLabel("Nickname : "));
        p1.add(nick);
        p1.add(exit);

        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());

        JPanel p22 = new JPanel();
        p22.setLayout(new FlowLayout(FlowLayout.CENTER));
        p22.add(new JLabel("Online list"));
        p2.add(p22,BorderLayout.NORTH);

        online = new JTextArea(10,10);
        online.setEditable(false);
        p2.add(new JScrollPane(online),BorderLayout.CENTER);
        p2.add(new JLabel("     "),BorderLayout.SOUTH);
        p2.add(new JLabel("     "),BorderLayout.EAST);
        p2.add(new JLabel("     "),BorderLayout.WEST);

        JPanel p_mess = new JPanel();
        msg = new JTextArea(40,20);
        msg.setEditable(false);
        p_mess.add(new JScrollPane(msg), BorderLayout.CENTER);

        JPanel p3 = new JPanel();
        p3.setLayout(new FlowLayout(FlowLayout.LEFT));
        p3.add(new JLabel("Message"));
        message = new JTextField(30);
        p3.add(message);
        p3.add(send);
        p3.add(clear);
        p3.add(start);

        p_board = new JPanel();
        p_board.setLayout(new BorderLayout());
        p_board.setBackground(Color.white);

        p_chat.add(p_mess,BorderLayout.WEST);
        p_chat.add(p1,BorderLayout.NORTH);
        p_chat.add(p2,BorderLayout.EAST);
        p_chat.add(p3,BorderLayout.SOUTH);
        p_chat.add(p_board, BorderLayout.CENTER);
//		p_chat.add(new JLabel("     "),BorderLayout.WEST);

        p_chat.setVisible(false);
        add(p_chat,BorderLayout.CENTER);
        //-------------------------
        p_login = new JPanel();
        p_login.setLayout(new FlowLayout(FlowLayout.CENTER));
        p_login.add(new JLabel("Nickname : "));
        nick1=new JTextField(20);
        p_login.add(nick1);
        p_login.add(login);
        p_login.add(logout);

        add(p_login,BorderLayout.NORTH);
	}
//---------[ Socket ]-----------//	
    private void go() {
        try {
            client = new Socket("localhost",6666);
            dos=new DataOutputStream(client.getOutputStream());
            dis=new DataInputStream(client.getInputStream());

            //client.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Connection errors","Message Dialog",JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }
	
    public static void main(String[] args) {
            new Client().go();
	}
    private void sendMSG(String data){
        try {
            dos.writeUTF(data);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getMSG(){
        String data=null;
        try {
            data=dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
	
	public void getMSG(String msg1, String msg2){
            System.out.println("" + cards.isDisplayable());
            int stt = Integer.parseInt(msg1);
            switch (stt) {
            // msg from another one
            case 3:
                
                this.msg.append("\n"+msg2);
                break;
            // update online list
            case 4:
                System.out.println("to online list");
                this.online.setText(msg2);
                break;
            // stop a client
            case 5:
                dataStream.stopThread();
                exit();
                break;
            case 6: 
                this.msg.append("Ready");
                break;
            case 7:
                this.msg.append(msg2);
                loadCard(getCardsList(msg2));
                p_board.revalidate();
                start.setEnabled(true);
                break;
            case 8:
                this.msg.append(msg2);
                break;
            default:
                break;
            }
	}
//----------------------------------------------
    private void checkSend(String msg){
        if(msg.compareTo("\n")!=0){
            this.msg.append("\nMe : "+msg);
            sendMSG("1");
            sendMSG(msg);
        }
    }
    private boolean checkLogin(String nick){
            if(nick.compareTo("")==0)
                    return false;
            else if(nick.compareTo("0")==0){
                    return false;
            }
            else{
                sendMSG(nick);
                int sst = Integer.parseInt(getMSG());
                if(sst==0) {
                         return false; }
                else return true;
            }
    }

    private void exit(){
            try {
                    sendMSG("0");
                    dos.close();
                    dis.close();
                    client.close();
            } catch (IOException e1) {
                    e1.printStackTrace();
            }
            System.exit(0);
    }
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==exit){
            dataStream.stopThread();
            exit();
        }
        else if(e.getSource()==clear){
            message.setText("");
        }
        else if(e.getSource()==send){
            checkSend(message.getText()+"\n");
            message.setText("");
        }
        else if(e.getSource()==login){
            if(checkLogin(nick1.getText())){
                System.out.println("been to 236");
                p_chat.setVisible(true);
                p_login.setVisible(false);
                nick.setText(nick1.getText());
                nick.setEditable(false);
                this.setTitle(nick1.getText());
                msg.append("Login successfully\n");
                loadMySeat();
                dataStream = new DataStream(this, this.dis);
            }
            else{
                JOptionPane.showMessageDialog(this,"Nickname is used. Try another again.","Message Dialog",JOptionPane.WARNING_MESSAGE);
            }
        }
        else if (e.getSource()==open) {
            sendMSG("10");
        }
        else if (e.getSource()== start) {
            start.setEnabled(false);
            mySeat.remove(cards);
            p_board.revalidate();
            sendMSG("11");
        }
        else if(e.getSource()==logout){
            exit();
        }
    }
    
    public JLabel loadImage(String path, int width, int height) {
        JLabel label = new JLabel();
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image dimg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(dimg));
        return label;
    }
    
    public void loadMySeat() {
        mySeat = new JPanel();
        mySeat.setBackground(null);
        mySeat.setLayout(new FlowLayout(FlowLayout.LEFT));
        String avtPath = "C:\\Users\\Administrator\\Documents\\NetBeansProjects\\TripleCard\\src\\image\\user.png";
        JLabel avt = loadImage(avtPath, 50, 50);
        JLabel name = new JLabel(nick1.getText());
        open = new JButton("Open");
        open.addActionListener(this);
        cards = new JPanel();
        mySeat.add(avt);
        mySeat.add(name);
        mySeat.add(open);
        mySeat.add(cards);
        p_board.add(mySeat, BorderLayout.SOUTH);
        
    }
    
    public ArrayList<Card> getCardsList(String string) {
        Deck deck = new Deck();
        System.out.println("In getcardsIndex:" + string);
        currentCard = new ArrayList<>();
        String s[] = string.split(" ");
        for (int i = 0; i<3; i++) {
            currentCard.add(deck.deakArr.get(Integer.parseInt(s[i])));
        }
        return currentCard;
    }
    
    public void loadCard(ArrayList<Card> cardsList) {
        cards.removeAll();
        for (int i = 0; i<3; i++) {
            cards.add(cardsList.get(i));
        }
        mySeat.add(cards);
    }
    
    public int getPoints(ArrayList<Card> cardsList) {
        int point=0;
        for (int i = 0; i<3; i++) {
            point+=cardsList.get(i).getLevel();
        }
        return point%10;
    }
    
//    private void sendPoints(int points) {
//        sendMSG("10");
//	sendMSG(points + "");
//    }

}

