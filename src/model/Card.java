package model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Card extends JLabel{
    public String suit;
    public int level;
    public static Image imgCard;
    public int status;
    public int point;
    
    

    public Card(String suit, int level, BufferedImage image) {
        super(new ImageIcon(image.getScaledInstance(60, 75,Image.SCALE_SMOOTH)));
        this.suit = suit;
        this.level = level;
        this.status = 0;
        switch (level) {
            case 11:
                this.point = 10;
                break;
            case 12:
                this.point = 10;
                break;
            case 13:
                this.point = 10;
                break;
            default:
                this.point = level;
        }
        
    }
    
    public static void loadImage() {
    }
    
    public void setStatus() {
        status = 1;
    }

    public static Image getImgCard() {
        return imgCard;
    }

    public int getStatus() {
        return status;
    }

    public  String getSuit() {
        return suit;
    }

    public int getLevel() {
        return level;
    }
    
    public String displayCard() {
        return "" + this.level + this.suit;
    }
    
    
    
    
}
