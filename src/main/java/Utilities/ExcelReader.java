package Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;


public class ExcelReader {

    public static void main(String args[]) {

        String userDateFilePath = "src/main/resources/datasheet/UserData.xlsx";
        int phoneNumberToReturn;

        try {

            File myFile = new File(userDateFilePath);
            FileInputStream fis = new FileInputStream(myFile);

            // Reading file fro local directory


            // Create Workbook instance holding reference to
            // .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(fis);

            // Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            // Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();

            //get total number of rows
            int rowCount = sheet.getLastRowNum()-sheet.getFirstRowNum();
            //to parameterize
            int depcolCount =-1;
            int colToReturn=0;



            outerLoop:
            // Till there is an element condition holds true
            while (rowIterator.hasNext()) {

                Row row = rowIterator.next();

                // For each row, iterate through all the
                // columns
                Iterator<Cell> cellIterator
                        = row.cellIterator();


                while (cellIterator.hasNext()) {

                    Cell cell = cellIterator.next();
                    depcolCount++;



                    if(cell.getStringCellValue().equals("PhoneNumber")) {

                        colToReturn++;


                    }
                    System.out.println("\n   Col value----"+cell.getStringCellValue());
                    if(cell.getStringCellValue().equals("isMerchant")) {

                        break outerLoop;


                    }

                    System.out.println("");
                }




                // Closing file output streams
                fis.close();
            }



            for(int i=1 ; i<=rowCount; i++) {
                Row row2 = sheet.getRow(i);
                Cell cell2 = row2.getCell(depcolCount);
                Cell cellToWrite = row2.getCell(depcolCount);
                if (cell2.toString().equals("FALSE")) {
                    cell2=row2.getCell(colToReturn);

                    String phoneNumber =  cell2.toString();

                    System.out.println("PHONE NUMBER GOT FROM EXCEL UTILITY ->>"+phoneNumber);
                    String obj = "true";
                    cellToWrite.setCellValue((String)obj);
                    break;


                }


            }

            //to check if all numbers are used then throw exception
            Row rowFinal = sheet.getRow(rowCount-1);
            Cell cellFinal = rowFinal.getCell(depcolCount);
            if (cellFinal.toString().equalsIgnoreCase("TRUE")) {
                throw new Exception("ALL PHONE NUMBERS are USED IN EXCEL , KINDLY ADD NEW");
            }


            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(userDateFilePath);
            workbook.write(out);
            out.close();
            System.out.println("Used number is saved in sheet");

        }



        // Catch block to handle exceptions
        catch (Exception e) {

            // Display the exception along with line number
            // using printStackTrace() method
            e.printStackTrace();
        }
    }



}
