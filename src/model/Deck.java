package model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Deck {
    public ArrayList<Card> deakArr = new ArrayList<>();
    public Deck() {
        ArrayList<String> suits = new ArrayList<>();
        suits.add("heart");
        suits.add("diamond");
        suits.add("club");
        suits.add("spade");
        String level;
        String path = "src/image/cards/200px-Playing_card_";
        String path1;
        File file;
        BufferedImage cardImage;
        for (int i=0; i<4; i++ ) {
            for (int j = 1; j<=13; j++) {
                switch (j) {
                    case 1:
                        level = "A";
                        break;
                    case 11:
                        level = "J";
                        break;
                    case 12:
                        level = "Q";
                        break;
                    case 13:
                        level = "K";
                        break;
                    default:
                        level = String.valueOf(j);
                }
                try {
                    file = new File(path+suits.get(i) + "_" + level +".svg.png");
                    cardImage = ImageIO.read(file);
    //              path = "C:\\Users\\Administrator\\Documents\\NetBeansProjects\\TripleCard\\src\\image\\cards\\200px-Playing_card_" + suits.get(i) + "_" + level +".svg";
//                    System.out.println(level + suits.get(i) + path +"");
                    Card c = new Card(suits.get(i), j, cardImage);
                    deakArr.add(c);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
    
    public static void main(String[] args) throws IOException {
        Deck deck = new Deck();
    }
    
    
}
