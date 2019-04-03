/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cost_MAP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class costSolver {
//    
    int[] rows = getRowPositions();
    int[] cols = getColPositions();
    
    public Dictionary getHeader(String path) throws FileNotFoundException, IOException {
        
//        ArrayList header = new ArrayList();
        Dictionary header = new Hashtable();
        BufferedReader  br = new BufferedReader(new FileReader(path));
        //initiate reader
        String line = br.readLine();
        //define delimeters
        String delims = "[ ]+";
        //parse first line to get number of columns
        String[] columnLine = line.split(delims);
        int columns = Integer.parseInt(columnLine[1]);
        header.put("Columns", columns);
        //read next line and then get number of rows
        line = br.readLine();
        String[] rowLine = line.split(delims);
        int rows = Integer.parseInt(rowLine[1]);
        header.put("Rows", rows);
        //create empty matrix
        line = br.readLine();
        String[] xllCornerLine = line.split(delims);
        double xllCorner = Double.parseDouble(xllCornerLine[1]);
        header.put("xllCorner", xllCorner );
        line = br.readLine();
        String[] yllCornerLine = line.split(delims);
        double yllCorner = Double.parseDouble(yllCornerLine[1]);
        header.put("yllCorner", yllCorner );
        line = br.readLine();
        String[] cellSizeLine = line.split(delims);
        double cellSize = Double.parseDouble(cellSizeLine[1]);
        header.put("CellSize", cellSize);
        line = br.readLine();
        String[] noDataline = line.split(delims);
        int noData = Integer.parseInt(noDataline[1]);
        header.put("NoData", noData);
        
        return header;
    }

    public double[][] getDetails(Dictionary headerInfo, String path) throws FileNotFoundException, IOException {

        BufferedReader br = new BufferedReader(new FileReader(path));
        
        double [][] aoiMatrix = new double[(int) headerInfo.get("Rows")][(int) headerInfo.get("Columns")];
        //initiate reader
        String line = br.readLine();
        //read next line and then get number of rows
        line = br.readLine();

        line = br.readLine();

        line = br.readLine();

        line = br.readLine();

        line = br.readLine();

        line = br.readLine();
        while (line != null) {
            for (double[] aoiMatrix1 : aoiMatrix) {
                String[] values = line.split("[ ]+");
                for (int j = 0; j < values.length; j++) {
                    aoiMatrix1[j] = Double.parseDouble(values[j]);

                }
                line = br.readLine();
            }
        }
        br.close();

        return aoiMatrix;

    }
    
    public Dictionary landcoverInput(boolean isSelectedPop, String path) throws FileNotFoundException, IOException, Exception {

        //Read in landcover weighting
        ArrayList weights = new ArrayList();
        BufferedReader br1 = new BufferedReader(new FileReader("Datasets/weights/landcover.txt"));
        String line = br1.readLine();
        while ((line = br1.readLine()) != null) {
            String[] splited = line.split("\\s");
            weights.add(splited[2]);
        }
        br1.close();

        Dictionary headerInfo = getHeader(path);
        double[][] nlcdMatrix = getDetails(headerInfo, path);
        

        //Create Output matrix
        double[][] tempMatrix = new double[nlcdMatrix.length][nlcdMatrix[0].length];
        double[][] popMatrix = null;
        
        //Read in population file if it exists
        if (isSelectedPop == true) {
            System.out.println("Importing Population Data ...");
            popMatrix = getDetails(headerInfo, "Datasets/ASCII/population.asc");
        }
        
        for (int i = 0; i < nlcdMatrix.length; i++) {

            for (int j = 0; j < nlcdMatrix[0].length; j++) {
                //no data using mask
                int value = (int) nlcdMatrix[i][j];
                int a = (int) headerInfo.get("NoData");
                switch(value){
                    case 11:
                        tempMatrix[i][j] = Double.parseDouble((String) weights.get(0));
                        break;
                    case 12:
                        
                        tempMatrix[i][j] = Double.parseDouble((String) weights.get(1));
                        break;   
                    case 21:
                    case 22:
                    case 23:
                    case 24: 
                        if (isSelectedPop != false) {
                        double cellPop = cellPop(headerInfo,popMatrix, i, j);
                        if (cellPop == 0) {
                            tempMatrix[i][j] = 0.75;
                        } else if (cellPop > 0 & cellPop <= 5) {
                            tempMatrix[i][j] = 1;
                        } else if (cellPop > 5 & cellPop <= 25) {
                            tempMatrix[i][j] = 1.5;
                        } else if (cellPop > 25 & cellPop <= 100) {

                            tempMatrix[i][j] = 2.5;
                        } else {
                            tempMatrix[i][j] = 5;

                        }}
                        else{
                        tempMatrix[i][j] = 5;
                        }
                    break;
                    case 31:
                        tempMatrix[i][j] = Double.parseDouble((String) weights.get(2));
                        break;
                    case 41:
                    case 42:
                    case 43:
                        tempMatrix[i][j] = Double.parseDouble((String) weights.get(3));
                        break;
                        
                    case 52:
                        tempMatrix[i][j] = Double.parseDouble((String) weights.get(4));
                        break;
                        
                    case 71:
                        tempMatrix[i][j] = Double.parseDouble((String) weights.get(5));
                        break;
                    case 81:
                        tempMatrix[i][j] = Double.parseDouble((String) weights.get(6));
                        break;
                    case 82:
                        tempMatrix[i][j] = Double.parseDouble((String) weights.get(7));
                        break;
                    case 90:
                    case 95:
                        tempMatrix[i][j] = Double.parseDouble((String) weights.get(8));
                        break;
                    default:
                        tempMatrix[i][j] = (int) headerInfo.get("NoData");

                }

        }
        }
        Dictionary costList = new Hashtable();
        int cell = 0;
        for (int i = 0; i < tempMatrix.length; i++) {
            for (int j = 0; j < tempMatrix[0].length; j++) {
                    cell = cell +1;
                    
                    double[] landKernel = kernel(tempMatrix, i, j);
                    double[] costs = solveLand(landKernel);
                    costList.put(cell, costs);
            }
        }
        
        
       return costList;
    }
    
    public Dictionary landRowInput(boolean isSelectedPop, String path) throws FileNotFoundException, IOException, Exception {

        //Read in landcover weighting
        ArrayList weights = new ArrayList();
        BufferedReader br1 = new BufferedReader(new FileReader("Datasets/weights/landrows.txt"));
        String line = br1.readLine();
        while ((line = br1.readLine()) != null) {
            String[] splited = line.split("\\s");
            weights.add(splited[2]);
        }
        br1.close();

        Dictionary headerInfo = getHeader(path);
        double[][] nlcdMatrix = getDetails(headerInfo, path);
        double[][] fedMatrix = getDetails(headerInfo, "Datasets/ASCII/fed.asc");
        

        //Create Output matrix
        double[][] tempMatrix = new double[nlcdMatrix.length][nlcdMatrix[0].length];
        double[][] popMatrix = null;
        
        //Read in population file if it exists
        if (isSelectedPop == true) {
            System.out.println("Importing Population Data ...");
            popMatrix = getDetails(headerInfo, "Datasets/ASCII/population.asc");
        }

        for (int i = 0; i < nlcdMatrix.length; i++) {

            for (int j = 0; j < nlcdMatrix[0].length; j++) {
                //no data using mask
                int value = (int) nlcdMatrix[i][j];
//                int a = (int) headerInfo.get("NoData");
                switch(value){
                    case 11:
                        tempMatrix[i][j] = Double.parseDouble((String) weights.get(0));
                        break;
                    case 12:
                        tempMatrix[i][j] = Double.parseDouble((String) weights.get(1));
                        break; 
                    case 21:
                    case 22:
                    case 23:
                    case 24: 
                        if (isSelectedPop != false) {
                        double cellPop = cellPop(headerInfo,popMatrix, i, j);
                        if (cellPop == 0) {
                            tempMatrix[i][j] = 0.75;
                        } else if (cellPop > 0 & cellPop <= 5) {
                            tempMatrix[i][j] = 1;
                        } else if (cellPop > 5 & cellPop <= 25) {
                            tempMatrix[i][j] = 1.5;
                        } else if (cellPop > 25 & cellPop <= 100) {

                            tempMatrix[i][j] = 2.5;
                        } else {
                            tempMatrix[i][j] = 5;

                        }}
                        else{
                        tempMatrix[i][j] = 5;
                        }
                    break;
                    case 31:
                        tempMatrix[i][j] = Double.parseDouble((String) weights.get(2));
                        break;
                    case 41:
                    case 42:
                    case 43:
                        tempMatrix[i][j] = Double.parseDouble((String) weights.get(3));
                        break;
                        
                    case 52:
                        tempMatrix[i][j] = Double.parseDouble((String) weights.get(4));
                        break;
                        
                    case 71:
                        tempMatrix[i][j] = Double.parseDouble((String) weights.get(5));
                        break;
                    case 81:
                        tempMatrix[i][j] = Double.parseDouble((String) weights.get(6));
                        break;
                    case 82:
                        tempMatrix[i][j] = Double.parseDouble((String) weights.get(7));
                        break;
                    case 90:
                    case 95:
                        tempMatrix[i][j] = Double.parseDouble((String) weights.get(8));
                        break;
                    default:
                        tempMatrix[i][j] = (int) headerInfo.get("NoData");

                }
            
            }
        }
        
        for (int i = 0; i < fedMatrix.length; i++) {

            for (int j = 0; j < fedMatrix[0].length; j++) {
                
                    int value = (int) fedMatrix[i][j];
//                    int a = (int) headerInfo.get("NoData");
                    
                    switch(value){

                    case 1:
                        tempMatrix[i][j] = tempMatrix[i][j] * 0.5;
                        break;
                    
                    case 2:
                        tempMatrix[i][j] = tempMatrix[i][j] * 0.5;
                        break;
                    case 3: 
                    
                        tempMatrix[i][j] = tempMatrix[i][j] * 2;
                        break;
                    case 4:
                        tempMatrix[i][j] = tempMatrix[i][j] * 1.5;
                        break;
                    case 5:
                        tempMatrix[i][j] = tempMatrix[i][j] * 2.5;
                        break;
                    case 6:
                        tempMatrix[i][j] = tempMatrix[i][j] * 2;
                        break;
//                        System.out.println(tempMatrix[i][j]);
                    case 7:
                        tempMatrix[i][j] = tempMatrix[i][j] * 3;
                        break;
                    case 8:
                        tempMatrix[i][j] = tempMatrix[i][j] * 0.75;
                        break;
                    case 9:
                        tempMatrix[i][j] = tempMatrix[i][j] * 2;
                        break;
                    case 10:
                        tempMatrix[i][j] = tempMatrix[i][j] * 50;
                        break;
                    
                    default:
                        tempMatrix[i][j] = (int) headerInfo.get("NoData");
                    }
                }
            }
        
        Dictionary costList = new Hashtable();
        int cell = 0;
        for (int i = 0; i < tempMatrix.length; i++) {
            for (int j = 0; j < tempMatrix[0].length; j++) {
                    cell = cell +1;
                    
                    double[] landKernel = kernel(tempMatrix, i, j);
                    double[] costs = solveLand(landKernel);
                    costList.put(cell, costs);
            }
        }
        
        
       return costList;
    }
    
    
    private double cellPop(Dictionary headerInfo,double[][] array, int i, int j) {
        double pop = array[i][j];
        double lon = (Double) headerInfo.get("xllCorner");
        double lat = (Double) headerInfo.get("xllCorner") + (((int) headerInfo.get("Rows") - (i + 1)) * (Double) headerInfo.get("CellSize")) + ((Double) headerInfo.get("CellSize")/ 2);
        double cHeight = haversineDistance(lat, lon, lat + (Double) headerInfo.get("CellSize"), lon);
        double cWidth = haversineDistance(lat, lon, lat, lon + (Double) headerInfo.get("CellSize"));
        double cArea = cWidth * cHeight;
        return pop / cArea;
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {

        double earthRadius = 6369.15;
        double latDistance = toRad(lat2 - lat1);
        double lonDistance = toRad(lon2 - lon1);
        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(toRad(lat1)) * Math.cos(toRad(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2));
        double c = (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
        double distance = earthRadius * c;
        return distance;
    }

    //convert to radians
    private double toRad(double latChange) {
        return (double) (latChange * Math.PI / 180);
    }
    
    public Dictionary solveDistance(Dictionary headerInfo, double[][] distMult, Dictionary costList, String path, Boolean ASC) throws IOException {
        double[][] nlcdMatrix = getDetails(headerInfo, path);
        int rows = (int) headerInfo.get("Rows");
        int cols = (int) headerInfo.get("Columns");
        double[][] constructionGrid = new double[rows][cols];
        int indexNew = 0;
        for (int z = 0; z < nlcdMatrix.length; z++) {
            for (int j = 0; j < nlcdMatrix[0].length; j++) {
                indexNew = indexNew + 1;
                if (indexNew < costList.size()) {
                    double[] costs = (double[]) costList.get(indexNew);
                    for (int i = 0; i < costs.length; i++) {
                        if (i == 2 || i == 4) {
                            costs[i] = round((costs[i]) * distMult[i][0], 2);
                        } else if (i == 1 || i == 3) {
                            costs[i] = round((costs[i]) * distMult[i][1], 2);
                        } else if (i == 5 || i == 6) {
                            costs[i] = round((costs[i]) * distMult[i][2], 2);
                        } else {
                            costs[i] = round((costs[i]) * distMult[i][3], 2);
                        }
                        constructionGrid[z][j] = calculateAverage(costs); 
                        
                        costList.put(indexNew, costs);
                        
                    }

                }
            }
        }
        if(ASC == false){
            System.out.println("Writing Image files...");
            writeRaster outRaster = new writeRaster();
//            writeASC(constructionGrid);
            outRaster.writeToRaster(constructionGrid);
        }
        return costList;

    }
    

    
    
    public Dictionary slopeInput(Dictionary costList, boolean isSelectedAspect, String path) throws IOException {
        
        Dictionary headerInfo = getHeader(path);
        double[][] slopeMatrix = getDetails(headerInfo, path);
        double[][] tempMatrixSlope = new double[slopeMatrix.length][slopeMatrix[0].length];
        double[][] aspectMatrix = new double[slopeMatrix.length][slopeMatrix[0].length];

        if (isSelectedAspect = true) {
            System.out.println("Importing Aspect Data ...");
            Dictionary headerAspect = getHeader(path);
            aspectMatrix = getDetails(headerAspect, path);
        } else {
            for (int i = 0; i < aspectMatrix.length; i++) {
                for (int j = 0; j < aspectMatrix[0].length; j++) {
                    aspectMatrix[i][j] = -9999;
                }
            }
        }
        
        for (int i = 0; i < slopeMatrix.length; i++) {
            for (int j = 0; j < slopeMatrix[0].length; j++) {
                if (slopeMatrix[i][j] == (int) headerInfo.get("NoData")) {
                    tempMatrixSlope[i][j] = -9999;
                } else if (slopeMatrix[i][j] <= 0.5) {
                    tempMatrixSlope[i][j] = 0;

                } else if (slopeMatrix[i][j] > 0.5 & slopeMatrix[i][j] <= 1) {
                    tempMatrixSlope[i][j] = 0.1;

                } else if (slopeMatrix[i][j] > 1 & slopeMatrix[i][j] <= 1.5) {
                    tempMatrixSlope[i][j] = 0.2;

                } else if (slopeMatrix[i][j] > 1.5 & slopeMatrix[i][j] <= 2) {
                    tempMatrixSlope[i][j] = 0.3;

                } else if (slopeMatrix[i][j] > 2 & slopeMatrix[i][j] <= 2.5) {
                    tempMatrixSlope[i][j] = 0.4;

                } else if (slopeMatrix[i][j] > 2.5 & slopeMatrix[i][j] <= 3) {
                    tempMatrixSlope[i][j] = 0.5;

                } else if (slopeMatrix[i][j] > 3) {
                    tempMatrixSlope[i][j] = 1;
                }
            }
        }
        
        int indexNew = 0;
        for (int i = 0; i < tempMatrixSlope.length; i++) {
            for (int j = 0; j < tempMatrixSlope[0].length; j++) {
                indexNew = indexNew + 1;
                if (indexNew < costList.size()) {
                    double[] costs = (double[]) costList.get(indexNew);
                    double[] slopeKernel = kernel(tempMatrixSlope, j, j);
                    double[] aspectKernel = kernel(aspectMatrix, j, j);
                    costs = solveSlope(costs, slopeKernel,aspectKernel);
                    costList.put(indexNew, costs);
                }

            }
        }
        return costList;
       
    }
    
    private int[][] cellCount() throws FileNotFoundException, IOException {
        
        Dictionary headerInfo = getHeader("Datasets/ASCII/landcover.asc");
        int[][] cellMatrix = new int[(int)headerInfo.get("Rows")][(int)headerInfo.get("Columns")];
        int z = 1;
        for (int i = 0; i < cellMatrix.length; i++) {
            for (int j = 0; j < cellMatrix[0].length; j++) {
                cellMatrix[i][j] = z;
                
                z = z + 1;
            }
        }
        return cellMatrix;
    }
    
    
    public ArrayList cells() throws IOException {
        ArrayList cellList = new ArrayList();
        Dictionary headerInfo = getHeader("Datasets/ASCII/landcover.asc");
        int rows = (int)headerInfo.get("Rows");
        int columns = (int)headerInfo.get("Columns");
//        int cellAmount = rows * columns; 
        int[][] cellNumber = cellCount();
//        for (int  = 0; i < 9; i++)
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int[] cellKernel = cellKernel(cellNumber, i, j);
                cellList.add(cellKernel);
            }
        }

        return cellList;
    }
    
    
    
     private double[] solveSlope(double[] costs, double[] slopeKernel, double[] aspectKernel){
        double[] kernel = slopeKernel;
      
        for (int j = 1; j < 9; j++){ 
                    if(slopeKernel[0] >0){
                        if (slopeKernel[0] > 1 & (j == 1 || j == 2 || j == 3 || j == 4)) {

                            int zAdjust = (j - 1) * 2 + 1;

                            double aspectDifference = Math.abs(aspectKernel[0] - zAdjust);
                            //System.out.println(aspectDifference);
                            if (aspectDifference == 0 || aspectDifference == 4) {
                                kernel[0] = slopeKernel[0] + 0.2;

                            } else if (aspectDifference == 1 || aspectDifference == 3 || aspectDifference == 5 || aspectDifference == 7) {
                                kernel[0] = slopeKernel[0] + 0.1;

                            } else {
                                kernel[0] = slopeKernel[0];
                            }

                        }
//
                        else if (slopeKernel[j] != -9999 & slopeKernel[j] > 1 & (j == 1 || j == 2 || j == 3 || j == 4)) {

                            int zAdjust = (j - 1) * 2 + 1;

                            double aspectDifference = Math.abs(aspectKernel[j] - zAdjust);
                            if (aspectDifference == 0 || aspectDifference == 4) {
                                kernel[j] = slopeKernel[j] + 0.2;
                            } else if (aspectDifference == 1 || aspectDifference == 3 || aspectDifference == 5 || aspectDifference == 7) {
                                kernel[j] = slopeKernel[j] + 0.1;

                            } else {
                                kernel[j] = slopeKernel[j];
                            }

                        }
                        else if (slopeKernel[0] > 1 & (j == 5 || j == 6 || j == 7 || j == 8)) {

                            int zAdjust = (j - 4) * 2 + 1;
                            double aspectDifference = Math.abs(aspectKernel[0] - zAdjust);
                            if (aspectDifference == 0 || aspectDifference == 4) {
                                kernel[0] = slopeKernel[0] + 0.2;
                            } else if (aspectDifference == 1 || aspectDifference == 3 || aspectDifference == 5 || aspectDifference == 7) {
                                kernel[0] = slopeKernel[0] + 0.1;

                            } else {
                                kernel[0] = slopeKernel[0];
                            }

                        }

                        else if (slopeKernel[j] != -9999 & slopeKernel[j] > 1 & (j == 5 || j == 6 || j == 7 || j == 8)) {

                            int zAdjust = (j - 4) * 2;
                            double aspectDifference = Math.abs(aspectKernel[j] - zAdjust);
                            if (aspectDifference == 0 || aspectDifference == 4) {
                                kernel[j] = slopeKernel[j] + 0.2;
                            } else if (aspectDifference == 1 || aspectDifference == 3 || aspectDifference == 5 || aspectDifference == 7) {
                                kernel[j] = slopeKernel[j] + 0.1;

                            } else {
                                kernel[j] = slopeKernel[j];
                            }
                        }
                        
                    double value = (kernel[0] + kernel[j]) / 2;

                    costs[j] = costs[j] + value;
                    }
                }
        return costs;
    }
    
    public double [][] aspectInput() throws FileNotFoundException, IOException {

            Dictionary headerInfo = getHeader("Datasets/ASCII/aspect.asc");
            double[][] maskMatrix = getDetails(headerInfo, "Datasets/ASCII/aspect.asc");
            double[][] tempMatrix = new double[(int)headerInfo.get("Rows")][(int)headerInfo.get("Columns")];

        for (int i = 0; i < maskMatrix.length; i++) {
            for (int j = 0; j < maskMatrix[0].length; j++) {
                if (maskMatrix[i][j] == (int) headerInfo.get("NoData")) {
                    tempMatrix[i][j] = -9999;
                } else if (maskMatrix[i][j] <= 22.5) {
                    tempMatrix[i][j] = 1;

                } else if (maskMatrix[i][j] > 22.5 & maskMatrix[i][j] <= 67.5) {
                    tempMatrix[i][j] = 2;

                } else if (maskMatrix[i][j] > 67.5 & maskMatrix[i][j] <= 112.5) {
                    tempMatrix[i][j] = 3;

                } else if (maskMatrix[i][j] > 112.5 & maskMatrix[i][j] <= 157.5) {
                    tempMatrix[i][j] = 4;

                } else if (maskMatrix[i][j] > 157.5 & maskMatrix[i][j] <= 202.5) {
                    tempMatrix[i][j] = 5;

                } else if (maskMatrix[i][j] > 202.5 & maskMatrix[i][j] <= 247.5) {
                    tempMatrix[i][j] = 6;

                } else if (maskMatrix[i][j] > 247.5 & maskMatrix[i][j] <= 292.5) {
                    tempMatrix[i][j] = 7;

                } else if (maskMatrix[i][j] > 292.5 & maskMatrix[i][j] <= 337.5) {
                    tempMatrix[i][j] = 8;

                } else {
                    tempMatrix[i][j] = 1;
                }
            }
        }
        System.out.println(Arrays.deepToString(tempMatrix));
        return tempMatrix;
    }

    public ArrayList solveConstruction(ArrayList costList, String path) throws IOException {
        Dictionary headerInfo = getHeader(path);
        double[][] landcoverMatrix = getDetails(headerInfo, "Datasets/ASCII/roads.asc");
        System.out.println("Construction Costs continue ...");
        return costList;
    }
    
    public Dictionary addRiverCrossings(Dictionary costList, Dictionary headerInfo, String path) throws IOException {
        Dictionary roadInfo = getHeader(path);
        double[][] roadMatrix = getDetails(roadInfo, path);
        double[][] tempMatrix = new double[(int)headerInfo.get("Rows")][(int)headerInfo.get("Columns")];
        int indexNew = 0;
        double weight = 1.25;
        for (int i = 0; i < tempMatrix.length; i++) {
            for (int j = 0; j < tempMatrix[0].length; j++) {
                indexNew = indexNew + 1;
                if (indexNew < costList.size()) {
                    double[] costs = (double[]) costList.get(indexNew);
                    double[] newCosts = crossIncrease(costs,roadMatrix, i, j, weight);
                    costList.put(indexNew, newCosts);
                }

            }
        }
        return costList;
    }
    
    public Dictionary addRoadCrossings(Dictionary costList, Dictionary headerInfo, String path) throws IOException {
        Dictionary roadInfo = getHeader(path);
        double[][] roadMatrix = getDetails(roadInfo, path);
        double[][] tempMatrix = new double[(int)headerInfo.get("Rows")][(int)headerInfo.get("Columns")];
        int indexNew = 0;
        double weight = 1.25;
        for (int i = 0; i < tempMatrix.length; i++) {
            for (int j = 0; j < tempMatrix[0].length; j++) {
                indexNew = indexNew + 1;
                if (indexNew < costList.size()) {
                    double[] costs = (double[]) costList.get(indexNew);
                    double[] newCosts = crossIncrease(costs,roadMatrix, i, j, weight);
                    costList.put(indexNew, newCosts);
                }

            }
        }
        return costList;
    }
    
    public Dictionary addRailCrossings(Dictionary costList, Dictionary headerInfo, String path) throws IOException {
        Dictionary roadInfo = getHeader(path);
        double[][] roadMatrix = getDetails(roadInfo, path);
        double[][] tempMatrix = new double[(int)headerInfo.get("Rows")][(int)headerInfo.get("Columns")];
        int indexNew = 0;
        double weight = 1.25;
        for (int i = 0; i < tempMatrix.length; i++) {
            for (int j = 0; j < tempMatrix[0].length; j++) {
                indexNew = indexNew + 1;
                if (indexNew < costList.size()) {
                    double[] costs = (double[]) costList.get(indexNew);
                    double[] newCosts = crossIncrease(costs,roadMatrix, i, j, weight);
                    costList.put(indexNew, newCosts);
                }

            }
        }
        return costList;
    }
    
    public Dictionary addPipelineCorridor(Dictionary costList, Dictionary headerInfo, String path) throws IOException {
        Dictionary pipelineInfo = getHeader(path);
        double[][] pipeMatrix = getDetails(pipelineInfo, path);
        int indexNew = 0;
        double weight = .75;
        for (int i = 0; i < pipeMatrix.length; i++) {
            for (int j = 0; j < pipeMatrix[0].length; j++) {
                indexNew = indexNew + 1;
                if (indexNew < costList.size()) {
                    double[] costs = (double[]) costList.get(indexNew);
                    double[] newCosts = rowDecrease(pipeMatrix,costs, j, j, weight);
                    costList.put(indexNew, newCosts);
                }

            }
        }
        return costList;
    }
    

   
    public double[] rowDecrease(double[][] matrix, double[]costs, int i, int j, double weight) {

        
        int z = (i * 3) + 1;
        int d = (j * 3) + 1;

        double[] kernel = kernel(matrix, z, d);

        for (int r = 1; r < 9; r++) {


            double[] kernel4 = kernel(matrix, (z + rows[r] * 3), (d + cols[r] * 3));

            //case 1 slide 2
            if ((kernel[1] > 0 && kernel4[3] > 0) || (kernel[3] < 0 && kernel4[1] < 0)) {
                costs[r] = costs[r] * weight;

            } else if ((kernel[2] > 0 && kernel4[4] > 0) || (kernel[4] < 0 && kernel4[2] < 0)) {
                costs[r] = costs[r] * weight;

            } else if ((kernel[6] > 0 && kernel4[8] > 0) || (kernel[8] < 0 && kernel4[6] < 0)) {
                costs[r] = costs[r] * weight;

            } else if ((kernel[5] > 0 && kernel4[7] > 0) || (kernel[7] < 0 && kernel4[5] < 0)) {
                costs[r] = costs[r] * weight;

            } else if ((kernel[5] > 0 && kernel4[6] > 0) || (kernel[6] < 0 && kernel4[5] < 0)) {
                costs[r] = costs[r] * weight;

            } else if ((kernel[7] > 0 && kernel4[8] > 0) || (kernel[8] < 0 && kernel4[7] < 0)) {
                costs[r] = costs[r] * weight;

            } else {
                costs[r] = costs[r] * 1;
            }

        }

        return  costs;

    }
    
    public double[] crossIncrease(double[] costs, double[][] matrix, int i, int j, double weight) {

        double[] increaseValue = new double[9];
        int z = (i * 3) + 1;
        int d = (j * 3) + 1;
        double[] kernel = kernel(matrix, z, d);
        
        for (int r = 1; r < 9; r++) {
            
            switch(r){
                
            case 1:
                double[] kernel2 = kernel(matrix, z - 3, d);
                //Case 1
                if ((kernel[0] < 0 && kernel[1] < 0 && kernel2[0] < 0 && kernel2[3] < 0)) {

                }
                //Case 2
                else if ((kernel[0] < 0 && kernel[2] < 0 && kernel[6] < 0 && kernel2[0] < 0 && kernel2[2] < 0 && kernel[7] < 0)) {

                }
                //Case 3
                else if ((kernel[0] < 0 && kernel[4] < 0 && kernel[5] < 0 && kernel2[0] < 0 && kernel2[4] < 0 && kernel[8] < 0)) {

                }
                //Case 4
                else if ((kernel[0] < 0 && kernel[2] < 0 && kernel[6] < 0 && kernel2[0] < 0 && kernel2[3] < 0 && kernel[7] < 0)) {

                }
                //Case 5
                else if ((kernel[0] < 0 && kernel[4] < 0 && kernel[5] < 0 && kernel2[0] < 0 && kernel2[3] < 0 && kernel[8] < 0)) {

                } else {
                    costs[r] = costs[r] * weight;
                }
            break;

            case 2:
                double[] kernel3 = kernel(matrix, z, d + 3);
                //Case 1
                if ((kernel[0] < 0 && kernel[2] < 0 && kernel[0] < 0 && kernel3[4] < 0)) {

                }
                //Case 2
                else if ((kernel[0] < 0 && kernel[3] < 0 && kernel[7] < 0 && kernel3[0] < 0 && kernel3[3] < 0 && kernel[8] < 0)) {

                }
                //Case 3
                else if ((kernel[0] < 0 && kernel[1] < 0 && kernel[6] < 0 && kernel3[0] < 0 && kernel3[1] < 0 && kernel[5] < 0)) {

                }

                //Case4
                else if ((kernel[0] < 0 && kernel[1] < 0 && kernel[6] < 0 && kernel3[0] < 0 && kernel3[4] < 0 && kernel[5] < 0)) {

                }
                //Case5
                else if ((kernel[0] < 0 && kernel[3] < 0 && kernel[7] < 0 && kernel3[0] < 0 && kernel3[4] < 0 && kernel[8] < 0)) {

                } else {
                    costs[r] = costs[r] * weight;
                }

            break;
            
            case 3:
                double[] kernel4 = kernel(matrix, z + 3, d);
                //Case 1
                if ((kernel[0] < 0 && kernel[3] < 0 && kernel4[0] < 0 && kernel4[1] < 0)) {

                }
                //Case 2
                else if ((kernel[0] < 0 && kernel[2] < 0 && kernel[7] < 0 && kernel4[0] < 0 && kernel4[2] < 0 && kernel[6] < 0)) {

                }
                //Case 3
                else if ((kernel[0] < 0 && kernel[4] < 0 && kernel[8] < 0 && kernel4[0] < 0 && kernel4[4] < 0 && kernel[5] < 0)) {

                }
                //Case 4
                else if ((kernel[0] < 0 && kernel[2] < 0 && kernel[6] < 0 && kernel4[0] < 0 && kernel4[1] < 0 && kernel[7] < 0)) {

                }
                //Case 5
                else if ((kernel[0] < 0 && kernel[4] < 0 && kernel[8] < 0 && kernel4[0] < 0 && kernel4[1] < 0 && kernel[5] < 0)) {

                } else {
                    costs[r] = costs[r] * weight;
                }
            break;
            
            case 4:
                  double[] kernel5 = kernel(matrix, z, d - 3);
                  //Case 1
                  if ((kernel[0] < 0 && kernel[2] < 0 && kernel5[0] < 0 && kernel5[4] < 0)) {

                  }
                  //Case 2
                  else if ((kernel[0] < 0 && kernel[3] < 0 && kernel[8] < 0 && kernel5[0] < 0 && kernel5[3] < 0 && kernel[7] < 0)) {

                  }
                  //Case 3
                  else if ((kernel[0] < 0 && kernel[1] < 0 && kernel[5] < 0 && kernel5[0] < 0 && kernel5[1] < 0 && kernel[6] < 0)) {

                  }

                  //Case4
                  else if ((kernel[0] < 0 && kernel[1] < 0 && kernel[5] < 0 && kernel5[0] < 0 && kernel5[2] < 0 && kernel[6] < 0)) {

                  }
                  //Case5
                  else if ((kernel[0] < 0 && kernel[3] < 0 && kernel[3] < 0 && kernel5[0] < 0 && kernel5[7] < 0 && kernel[3] < 0)) {

                  } else {
                      costs[r] = costs[r] * weight;
                  }
            break;
//            
            case 5:
                  double[] kernel6 = kernel(matrix, z, d - 3);
                  //Case 1
                  if ((kernel[0] < 0 && kernel[5] < 0 && kernel6[0] < 0 && kernel6[7] < 0)) {

                  } else {
                      costs[r] = costs[r] * weight;
                  }

            break;
            
            case 6:
                double[] kernel7 = kernel(matrix, z, d - 3);
                //Case 1
                if ((kernel[0] < 0 && kernel[6] < 0 && kernel7[8] < 0 && kernel7[0] < 0)) {

                } else {
                    costs[r] = costs[r] * weight;
                }

            break;
            case 7:
                double[] kernel8 = kernel(matrix, z, d - 3);
                //Case 1
                if ((kernel[0] < 0 && kernel[7] < 0 && kernel8[5] < 0 && kernel8[0] < 0)) {

                } else {
                    costs[r] = costs[r] * weight;
                }

            break;
            case 8:
                double[] kernel9 = kernel(matrix, z, d - 3);
                //Case 1
                if ((kernel[0] < 0 && kernel[8] < 0 && kernel9[6] < 0 && kernel9[0] < 0)) {

                } else {
                    costs[r] = costs[r] * weight;
                }
            break;
            
        
            }
        }
        
        return costs;
    }

    public double[] solveLand(double[] costKernel) {
        double[] costs = new double[9];
        for (int j = 0; j < 9; j++) {
            if (costKernel[j] == -9999) {
                costs[j] = -9999;
            } else {
                costs[j] = (costs[j] + ((costKernel[0] + costKernel[j]) / 2));
            
        }
        
    }
        return costs;
    }
  
    private int[] getColPositions() {

        int[] xPosition = new int[9];
        xPosition[0] = 0;
        xPosition[1] = 0;
        xPosition[2] = 1;
        xPosition[3] = 0;
        xPosition[4] = -1;
        xPosition[5] = -1;
        xPosition[6] = 1;
        xPosition[7] = 1;
        xPosition[8] = -1;
        return xPosition;

    }

    private int[] getRowPositions() {

        int[] yPosition = new int[9];
        yPosition[0] = 0;
        yPosition[1] = -1;
        yPosition[2] = 0;
        yPosition[3] = 1;
        yPosition[4] = 0;
        yPosition[5] = -1;
        yPosition[6] = -1;
        yPosition[7] = 1;
        yPosition[8] = 1;
        return yPosition;

    }
    
    
    //Kernel for calculations
    public double[] kernel(double[][] array, int i, int j) {

        double[] kernel = new double[9];

        for (int r = 0; r < 9; r++) {

            int row = rows[r];
            int col = cols[r];

            if ((i + row) < 0 || (i + row) > array.length - 1 || (j + col) < 0 || (j + col) > array[0].length - 1) {
                kernel[r] = -9999;
            } else {
                kernel[r] = array[i + row][j + col];

            }

        }

        return kernel;
    }
    
    //Kernel for calculations
    public int[] cellKernel(int[][] array, int i, int j) {

        int[] kernelInt = new int[9];

        for (int r = 0; r < 9; r++) {

            int row = rows[r];
            int col = cols[r];

            if ((i + row) < 0 || (i + row) > array.length - 1 || (j + col) < 0 || (j + col) > array[0].length - 1) {
                kernelInt[r] = -9999;
            } else {
                kernelInt[r] = array[i + row][j + col];

            }

        }
        return kernelInt;
    }
    
    private int getActiveNodes(Dictionary headerInfo, double[][] matrix) {
        int activeNodes = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] != (int) headerInfo.get("NoData")) {
                    activeNodes++;
                }
            }
        }

        return activeNodes;

    }
    
    public double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    private double calculateAverage(double[] conCost) {

        //Prints the costs
        List<Double> costs = new ArrayList<>();

        for (int z = 1; z < 9; z++) {
            if (conCost[z] > 0) {
                costs.add(round(conCost[z], 2));

            }
        }

        double sum = 0;
        if (!costs.isEmpty()) {
            for (Double mark : costs) {
                sum += mark;
            }
            return sum / costs.size();
        }
        return sum;
    }
    
    private void writeASC(double[][] constructionGrid) throws IOException {
        BufferedWriter constructionASC = new BufferedWriter(new FileWriter("Outputs/construction.asc"));
        Dictionary headerInfo = getHeader("Datasets/ASCII/landcover.asc");
//        double[][] cellMatrix = getDetails(headerInfo, "Datasets/ASCII/landcover.asc");
        constructionASC.write("nCols" + "\t" + headerInfo.get("Columns"));
        constructionASC.newLine();
        constructionASC.write("nRows" + "\t" + headerInfo.get("Rows"));
        constructionASC.newLine();
        constructionASC.write("xllCorner" + "\t" + headerInfo.get("xllCorner"));
        constructionASC.newLine();
        constructionASC.write("yllCorner" + "\t" + headerInfo.get("yllCorner"));
        constructionASC.newLine();
        constructionASC.write("cellSize" + "\t" + headerInfo.get("CellSize"));
        constructionASC.newLine();
        constructionASC.write("NODATA_value" + "\t" + headerInfo.get("NoData"));
        constructionASC.newLine();
        
//        System.out.println(Arrays.deepToString(constructionGrid).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));
        
        for (int i = 0; i < constructionGrid.length; i++) {
            for (int j = 0; j < constructionGrid[0].length; j++) {
                if (j == constructionGrid[0].length - 1) {
                    constructionASC.newLine();
                }
                if (constructionGrid[i][j] <= 0) {
                    String str = "-9999";
                    constructionASC.write(str + " ");
                } else {
                    String str = Double.toString(round(constructionGrid[i][j], 2));

                    constructionASC.write(str + " ");
                }
            }

        }
        

    }
    
    
    public void writeTxt(Dictionary costList, Dictionary headerInfo, BufferedWriter outPut) throws IOException {
        
        double[][] cellMatrix = getDetails(headerInfo, "Datasets/ASCII/landcover.asc");
        outPut.write("All Nodes" + "\t" + ((int) headerInfo.get("Rows") * (int) headerInfo.get("Columns")));
        outPut.newLine();
        outPut.write("Active Nodes" + "\t" + getActiveNodes(headerInfo, cellMatrix));
        outPut.newLine();
        outPut.write("nCols" + "\t" + headerInfo.get("Columns"));
        outPut.newLine();
        outPut.write("nRows" + "\t" + headerInfo.get("Rows"));
        outPut.newLine();
        outPut.write("xllCorner" + "\t" + headerInfo.get("xllCorner"));
        outPut.newLine();
        outPut.write("yllCorner" + "\t" + headerInfo.get("yllCorner"));
        outPut.newLine();
        outPut.write("cellSize" + "\t" + headerInfo.get("CellSize"));
        outPut.newLine();
        outPut.write("NODATA_value" + "\t" + headerInfo.get("NoData"));
        outPut.newLine();
        int rows = (int) headerInfo.get("Rows");
        int cols = (int) headerInfo.get("Columns");
        
        

        for (int i = 1; i < costList.size(); i++) {

            double[] costs = (double[]) costList.get(i);
            
            int[] cells = new int[9];
            if (costs[0] != (int) headerInfo.get("NoData")) {

                cells[0] = i;
                cells[1] = i - cols;
                cells[2] = i + 1;
                cells[3] = i + cols;
                cells[4] = i - 1;
                cells[5] = i - (cols + 1);
                cells[6] = i - (cols - 1);
                cells[7] = i + (cols + 1);
                cells[8] = i + (cols - 1);

//                System.out.println(Arrays.toString(cells));
//                System.out.println(Arrays.toString(costs));

                //Prints the cells
                ArrayList<Integer> printCells = new ArrayList<Integer>();
                printCells.add(cells[0]);
                for (int z = 1; z < 9; z++) {
                    if (costs[z] > 0) {
                        printCells.add(cells[z]);

                    }}
                    // printing the cells
                    if (printCells.size() > 1) {
                        String x = printCells.toString().replace('[', ' ');
                        x = x.replace(']', ' ');
                        x = x.trim().replace(',', '\t');
                        outPut.write(x);
                        outPut.newLine();
                    }
                }

//
//            //Prints the costs
                List<Double> printCosts = new ArrayList<>();

                for (int z = 1; z < 9; z++) {

                    if (costs[z] > 0) {
                        printCosts.add(round(costs[z], 2));

                    }
                }
//
                if (printCosts.size() > 0) {
                    String x = printCosts.toString().replace('[', ' ');

                    x = x.replace(']', ' ');
                    x = x.replace(',', '\t');
                    outPut.write("\t");
                    outPut.write(x);
                    outPut.newLine();
                }
            }

        
        outPut.close();
    }
    
    public double[][] distanceMultiplier(Dictionary headerInfo) {

        int rows = (int) headerInfo.get("Rows");
        double cellSize = (double) headerInfo.get("CellSize");
        double yllCorner = (double) headerInfo.get("yllCorner");
        double xllCorner = (double) headerInfo.get("xllCorner");
        
        double[][] cellMatrix = new double[rows][4];

        //x multiplier
        for (int i = 0; i < rows; i++) {

            double lat1 = yllCorner + ((rows - (i + 1)) * cellSize) + (cellSize / 2);
            double lat2 = yllCorner + ((rows - (i + 1)) * cellSize) + (cellSize / 2);
            double lon1 = xllCorner;
            double lon2 = xllCorner + cellSize;
            cellMatrix[i][0] = haversineDistance(lat1, lon1, lat2, lon2);

        }

        //y multiplier
        for (int i = 0; i < rows; i++) {

            double lat1 = yllCorner;
            double lat2 = yllCorner + cellSize;
            double lon1 = xllCorner;
            double lon2 = xllCorner;

            cellMatrix[i][1] = haversineDistance(lat1, lon1, lat2, lon2);
        }

        //xy-up
        for (int i = 0; i < rows; i++) {

            double lat1 = yllCorner + ((rows - (i + 1)) * cellSize) + (cellSize / 2);
            double lat2 = (yllCorner + ((rows - (i + 1)) * cellSize) + (cellSize / 2)) + cellSize;
            double lon1 = xllCorner;
            double lon2 = xllCorner + cellSize;

            cellMatrix[i][2] = haversineDistance(lat1, lon1, lat2, lon2);

        }

        //XY-down
        for (int i = 0; i < rows; i++) {

            double lat1 = yllCorner + ((rows - (i + 1)) * cellSize) + (cellSize / 2);
            double lat2 = (yllCorner + ((rows - (i + 1)) * cellSize) + (cellSize / 2)) - cellSize;
            double lon1 = xllCorner;
            double lon2 = xllCorner + cellSize;

            cellMatrix[i][3] = haversineDistance(lat1, lon1, lat2, lon2);

        }

        return cellMatrix;

    }
    
    
    
}
