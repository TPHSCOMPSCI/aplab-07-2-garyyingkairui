import java.awt.*;
import java.util.*;

public class Steganography {
    public static void clearLow(Pixel p) {
        int red = (p.getRed() / 4) * 4;
        int green = (p.getGreen() / 4) * 4;
        int blue = (p.getBlue() / 4) * 4;
        p.setColor(new Color(red, green, blue));
    }

    public static Picture testClearLow(Picture lebron) {
        Picture copy = new Picture(lebron);
        Pixel[][] pixels = copy.getPixels2D();
        for (Pixel[] row : pixels) {
            for (Pixel pixel : row) {
                clearLow(pixel);
            }
        }
        return copy;
    }

    public static void setLow(Pixel p, Color c) {
        int currentRed = p.getRed();
        int currentGreen = p.getGreen();
        int currentBlue = p.getBlue();
      
        int newRed = c.getRed() / 64;
        int newGreen = c.getGreen() / 64;
        int newBlue = c.getBlue() / 64;
    
        currentRed = (currentRed / 4) * 4;
        currentGreen = (currentGreen / 4) * 4;
        currentBlue = (currentBlue / 4) * 4;
    
        currentRed += newRed;
        currentGreen += newGreen;
        currentBlue += newBlue;
    
        p.setColor(new Color(currentRed, currentGreen, currentBlue));
    }
    

    public static Picture testSetLow(Picture p, Color c) {
        Picture copy = new Picture(p);
        Pixel[][] pixels = copy.getPixels2D();
        for (Pixel[] row : pixels) {
            for (Pixel pixel : row) {
                setLow(pixel, c);
            }
        }
        return copy;
    }

    public static Picture revealPicture(Picture hidden) {
        Picture copy = new Picture(hidden);
        Pixel[][] pixels = copy.getPixels2D();
        Pixel[][] source = hidden.getPixels2D();
        
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                Color sourceColor = source[r][c].getColor();

                int red = sourceColor.getRed();
                int green = sourceColor.getGreen();
                int blue = sourceColor.getBlue();
                
                int newRed = (red % 4) * 64;   
                int newGreen = (green % 4) * 64; 
                int newBlue = (blue % 4) * 64;   
                
                pixels[r][c].setColor(new Color(newRed, newGreen, newBlue));
            }
        }
        return copy;
    }
    

    public static boolean canHide(Picture source, Picture secret) {
        return source.getWidth() >= secret.getWidth() && source.getHeight() >= secret.getHeight();
    }

    public static Picture hidePicture(Picture source, Picture secret, int startRow, int startCol) {
        Picture combined = new Picture(source);
        Pixel[][] sourcePixels = combined.getPixels2D();
        Pixel[][] secretPixels = secret.getPixels2D();
        for (int r = 0; r < secretPixels.length; r++) {
            for (int c = 0; c < secretPixels[0].length; c++) {
                int sourceR = startRow + r;
                int sourceC = startCol + c;
                if (sourceR < sourcePixels.length && sourceC < sourcePixels[0].length) {
                    Color secretColor = secretPixels[r][c].getColor();
                    Color sourceColor = sourcePixels[sourceR][sourceC].getColor();
                    int rNew = (sourceColor.getRed() / 4 * 4) + (secretColor.getRed() / 64);
                    int gNew = (sourceColor.getGreen() / 4 * 4) + (secretColor.getGreen() / 64);
                    int bNew = (sourceColor.getBlue() / 4 * 4) + (secretColor.getBlue() / 64);
                    sourcePixels[sourceR][sourceC].setColor(new Color(rNew, gNew, bNew));
                }
            }
        }
        return combined;
    }
    

    public static boolean isSame(Picture pic1, Picture pic2) {
        if (pic1.getWidth() != pic2.getWidth() || pic1.getHeight() != pic2.getHeight()) {
            return false;
        }
        Pixel[][] p1 = pic1.getPixels2D();
        Pixel[][] p2 = pic2.getPixels2D();
        for (int r = 0; r < p1.length; r++) {
            for (int c = 0; c < p1[0].length; c++) {
                if (!p1[r][c].getColor().equals(p2[r][c].getColor())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static ArrayList<Point> findDifferences(Picture pic1, Picture pic2) {
        ArrayList<Point> diffPoints = new ArrayList<>();
        if (pic1.getWidth() != pic2.getWidth() || pic1.getHeight() != pic2.getHeight()) {
            return diffPoints;
        }
        Pixel[][] p1 = pic1.getPixels2D();
        Pixel[][] p2 = pic2.getPixels2D();
        for (int r = 0; r < p1.length; r++) {
            for (int c = 0; c < p1[0].length; c++) {
                if (!p1[r][c].getColor().equals(p2[r][c].getColor())) {
                    diffPoints.add(new Point(c, r));
                }
            }
        }
        return diffPoints;
    }

    public static Picture showDifferentArea(Picture pic, ArrayList<Point> differences) {
        Picture highlighted = new Picture(pic);
        if (differences.isEmpty()) {
            return highlighted;
        }

        int minRow = Integer.MAX_VALUE, maxRow = Integer.MIN_VALUE;
        int minCol = Integer.MAX_VALUE, maxCol = Integer.MIN_VALUE;

        for (Point p : differences) {
            int row = p.y;
            int col = p.x;
            minRow = Math.min(minRow, row);
            maxRow = Math.max(maxRow, row);
            minCol = Math.min(minCol, col);
            maxCol = Math.max(maxCol, col);
        }

        Graphics2D g = highlighted.createGraphics();
        g.setColor(Color.BLUE);
        g.drawRect(minCol, minRow, maxCol - minCol, maxRow - minRow);
        g.dispose();

        return highlighted;
    }

    public static ArrayList<Integer> encodeString(String s) {
        s = s.toUpperCase();
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < s.length(); i++) {
            if (s.substring(i, i + 1).equals(" ")) {
                result.add(27);
            } else {
                result.add(alpha.indexOf(s.substring(i, i + 1)) + 1);
            }
        }
        result.add(0);
        return result;
    }

    public static String decodeString(ArrayList<Integer> codes) {
        String result = "";
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < codes.size(); i++) {
            if (codes.get(i) == 27) {
                result = result + " ";
            } else {
                result = result
                        + alpha.substring(codes.get(i) - 1, codes.get(i));
            }
        }
        return result;
    }

    private static int[] getBitPairs(int num) {
        int[] bits = new int[3];
        int code = num;
        for (int i = 0; i < 3; i++) {
            bits[i] = code % 4;
            code = code / 4;
        }
        return bits;
    }

public static void hideText(Picture source, String s) {
    ArrayList<Integer> encoded = encodeString(s); 
    Pixel[][] pixels = source.getPixels2D(); 
    int index = 0; 

    for (int row = 0; row < pixels.length && index < encoded.size(); row++) {
        for (int col = 0; col < pixels[0].length && index < encoded.size(); col++) {
            int num = encoded.get(index); 
            int redBit = (num % 4)/4; 
            int greenBit = (num % 4)/4;
            int blueBit = num % 4;

            Pixel p = pixels[row][col]; 

            int newRed = (p.getRed() / 4) * 4 + redBit; 
            int newGreen = (p.getGreen() / 4) * 4 + greenBit; 
            int newBlue = (p.getBlue() / 4) * 4 + blueBit;

            p.setColor(new Color(newRed, newGreen, newBlue)); 
            index++;
        }
    }
}


   public static String revealText(Picture source) {
    ArrayList<Integer> words = new ArrayList<>();
    Pixel[][] pixels = source.getPixels2D();
//DY taught me how to iterate through the pixels
    for (Pixel[] row : pixels) {
        for (Pixel p : row) {
            int letter = (p.getBlue() & 3) << 4 | (p.getGreen() & 3) << 2 | (p.getRed() & 3);
            if (letter == 0) {
                return decodeString(words);
            }
            words.add(letter);
        }
    }
    return decodeString(words);
}

    //ACTIVITY 5 CODE
    public static void randomBlack(Picture source, int width, int height) {
        Pixel[][] pixels = source.getPixels2D();
        int maxRow = pixels.length - width;
        int maxCol = pixels[0].length - height;
        if (maxRow < 0 || maxCol < 0) {
            System.out.println("Region too large for the image!");
            return;
        }
        // Generate random starting position
        int startRow = (int) (Math.random() * maxRow);
        int startCol = (int) (Math.random() * maxCol);
        // Traverse the selected region
        for (int r = startRow; r < startRow + height; r++) {
            for (int c = startCol; c < startCol + width; c++) {
                if (r < pixels.length && c < pixels[0].length) {
                    Pixel p = pixels[r][c];
                    int black = (p.getRed() + p.getGreen() + p.getBlue()) / 255;
                    p.setColor(new Color(black, black, black));
                }
            }
        }
    }

    public static void main(String[] args) {
        //test runs for activity one
        Picture beach = new Picture("beach.jpg");
        Picture arch = new Picture("arch.jpg");
        beach.explore();
        Picture copy2 = testSetLow(beach, Color.PINK);
        copy2.explore();
        Picture copy3 = revealPicture(copy2);
        copy3.explore();

        //test runs for activity two
        System.out.println(canHide(beach, arch));
        if (canHide(beach, arch)) {
            Picture hidden = hidePicture(beach, arch, 0, 0);
            hidden.explore();
            Picture revealed = revealPicture(hidden);
            revealed.explore();
        }
        // test runs for activity three
        Picture swan = new Picture("swan.jpg");
        Picture swan2 = new Picture("swan.jpg");
        System.out.println("Swan and swan2 are the same: " + isSame(swan, swan2));
        swan = testClearLow(swan);
        System.out.println("Swan and swan2 are the same (after clearLow run on swan): " + isSame(swan, swan2));
        Picture arch1 = new Picture("arch.jpg");
        Picture arch2 = new Picture("arch.jpg");
        Picture koala = new Picture("koala.jpg");
        Picture robot1 = new Picture("robot.jpg");
        ArrayList<Point> pointList = findDifferences(arch1, arch2);
        System.out.println("PointList after comparing two identical pictures has a size of " + pointList.size());
        pointList = findDifferences(arch1, koala);
        System.out.println("PointList after comparing two different sized pictures has a size of " + pointList.size());
        arch2 = hidePicture(arch1, robot1, 65, 102);
        pointList = findDifferences(arch1, arch2);
        System.out.println("Pointlist after hiding a picture has a size of " + pointList.size());
        arch1.show();
        arch2.show();
        Picture hall = new Picture("femaleLionAndHall.jpg");
        Picture robot2 = new Picture("robot.jpg");
        Picture flower2 = new Picture("flower1.jpg");
        Picture hall2 = hidePicture(hall, robot2, 50, 300);
        Picture hall3 = hidePicture(hall2, flower2, 115, 275);
        hall3.explore();
        if (!isSame(hall, hall3)) {
            Picture hall4 = showDifferentArea(hall, findDifferences(hall, hall3));
            hall4.show();
            Picture unhiddenHall3 = revealPicture(hall3);
            unhiddenHall3.show();
        }
        //This is the test run for activity four shawty :)
        Picture beach1 = new Picture("beach.jpg");
        hideText(beach1, "HELLO WORLD");
        String revealed = revealText(beach1);
        System.out.println("Hidden message: " + revealed);

        //Activity 5: Applying pure black filter to random regions, the region size is specified to be (400 x 450) shawty
        Picture motorcyle = new Picture("blueMotorcycle.jpg");
        motorcyle.explore(); 
        randomBlack(motorcyle, 400, 450);
        motorcyle.explore(); 

    }
}