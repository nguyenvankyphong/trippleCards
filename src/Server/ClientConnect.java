package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientConnect extends Thread {

    public Socket client;
    public Server server;
    private String nickName;
    private DataOutputStream dos;
    private DataInputStream dis;
    private boolean run;

    public ClientConnect(Server server, Socket client) {
        try {
            this.client = client;
            this.server = server;
            dos = new DataOutputStream(client.getOutputStream());
            dis = new DataInputStream(client.getInputStream());
            run = true;
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String msg = null;
        while (run) {
            nickName = getMSG();
            if (nickName.compareTo("0") == 0) {
                server.numOfUser--;
                logout();

            } else {
                if (checkNick(nickName)) {
                    sendMSG("0");
                } else {
                    server.user.append(nickName + " joined\n");
                    server.sendAll(nickName, nickName + " joined\n");
                    server.listUser.put(nickName, this);
                    server.sendAllUpdate(nickName);
//                    server.requestAddSeat(nickName);
                    sendMSG("1");
                    displayAllUser();
                    System.out.println("\nnumOfUser: " + server.numOfUser);
//                    if (server.numOfUser == 3) {
//                        server.sendReady();
//                        server.sendEach();
//                    }       
                    while (run) {
                        int stt = Integer.parseInt(getMSG());
                        switch (stt) {
                            case 0:
                                run = false;
                                server.listUser.remove(this.nickName);
                                server.numOfUser--;
                                exit();
                                break;
                            case 1:
                                msg = getMSG();
                                server.sendAll(nickName, nickName + ": " + msg);
                                break;
                            case 10:
                                server.sendScore(nickName);
                                server.numOfOpeningUser++;
                                if (server.numOfOpeningUser == server.numOfUser) {
                                    server.sendResult();
                                    server.numOfOpeningUser = 0;
                                }
                                break;
                            case 11:
                                server.numOfStartingUser++;

                                if (server.numOfStartingUser == server.numOfUser) {
                                    //                                server.sendReady();
                                    server.sendEach();
                                    server.numOfStartingUser = 0;
                                }
                        }
                    }
                }
            }
        }
    }

    private void logout() {
        try {
            dos.close();
            dis.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exit() {
        try {
            server.sendAllUpdate(nickName);
            dos.close();
            dis.close();
            client.close();
            server.user.append(nickName + " log out\n");
            server.sendAll(nickName, nickName + " logout\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkNick(String nick) {
        return server.listUser.containsKey(nick);
    }

    private void sendMSG(String Data) {
        try {
            dos.writeUTF(Data);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMSG(String msg1, String msg2) {
        sendMSG(msg1);
        sendMSG(msg2);
    }

    private String getMSG() {
        String data = null;
        try {
            data = dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void displayAllUser() {
        String name = server.getAllName();
        sendMSG("4");
        sendMSG(name);
    }

}
