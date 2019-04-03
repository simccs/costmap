/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cost_MAP;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import javax.imageio.ImageIO;

/**
 *
 * @author BHooverAdmin
 */
public class writeRaster {
    
    /**
     *
     * @param constructionGrid
     */
    costSolver costs = new costSolver();
    public void writeToRaster(double[][] constructionGrid) throws IOException{
        Dictionary headerInfo = costs.getHeader("Datasets/ASCII/landcover.asc");
        int rows = (int)headerInfo.get("Rows");
        int columns = (int)headerInfo.get("Columns");
        String path = "Outputs/" + "construction" + ".png";
        BufferedImage image = new BufferedImage(constructionGrid.length, constructionGrid[0].length, BufferedImage.TYPE_INT_ARGB_PRE);
        for (int i = 0; i < constructionGrid.length; i++) {
            for (int j = 0; j < constructionGrid[0].length; j++) {
                image.setRGB(i, j, (int) constructionGrid[i][j]*170);
            }
        }
        File ImageFile = new File(path);
        
        ImageIO.write(image, "png", ImageFile);
    }
}
    

    
    

