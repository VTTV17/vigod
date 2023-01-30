package utilities.excel;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utilities.file.FileNameAndPath.*;

public class Excel {
    private static XSSFWorkbook workbook;

    private static XSSFSheet sheet;
    private static XSSFCell cell;
    private static XSSFRow row;
    static String fileName;

    public Sheet getSheet(String fileName, int sheetID) throws IOException {
        FileInputStream fileInput = new FileInputStream("%s/src/main/resources/excels/%s".formatted(System.getProperty("user.dir"), fileName));
        return new XSSFWorkbook(fileInput).getSheetAt(sheetID);
    }

    public Row getRow(String fileName, int sheetID, int rowID) throws IOException {
        return getSheet(fileName, sheetID).getRow(rowID);
    }

    public Cell getCell(String fileName, int sheetID, int rowID, int cellID) throws IOException {
        return getRow(fileName, sheetID, rowID).getCell(cellID);
    }

    public String getCellValue(String fileName, int sheetID, int rowID, int cellID) throws IOException {
        return new DataFormatter().formatCellValue(getCell(fileName, sheetID, rowID, cellID));
    }

    public List<Integer> getRowCellByKey(String fileName, int sheetID, String searchKey) throws IOException {
        Sheet sheet = getSheet(fileName, sheetID);
        List<Integer> list = new ArrayList<>();
        for (int rowID = 0; rowID <= sheet.getLastRowNum(); rowID++) {
            for (int cellID = 0; cellID < sheet.getRow(0).getLastCellNum(); cellID++) {
                if (new DataFormatter().formatCellValue(sheet.getRow(rowID).getCell(cellID)).equals(searchKey)) {
                    list = List.of(rowID, cellID);
                    break;
                }
            }
        }
        return list;
    }

    public int getCellIndexByCellValue(Row row, String cellValue) {
        int index = -1;
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell == null || cell.getCellType() == CellType.BLANK) {
                continue;
            }
            if (cell.getCellType() == CellType.STRING) {
                String text = cell.getStringCellValue();
                if (cellValue.equalsIgnoreCase(text)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    public int getCellIndexByCellValue(String fileName, int sheetId, int rowId, String cellValue) throws IOException {
        Row row = getRow(fileName, sheetId, rowId);
        int index = -1;
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell == null || cell.getCellType() == CellType.BLANK) {
                continue;
            }
            if (cell.getCellType() == CellType.STRING) {
                String text = cell.getStringCellValue();
                if (cellValue.equalsIgnoreCase(text)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    public void writeCellValue(String fileName, int sheetId, int rowIndex, int columnIndex, String value) {
        String filePath = projectLocation + getDirectorySlash("src") + getDirectorySlash("main") + getDirectorySlash("resources") + getDirectorySlash("excels") + fileName;
        try {
            FileInputStream excelFile = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(excelFile);
            sheet = workbook.getSheetAt(sheetId);
            row = sheet.getRow(rowIndex);
            cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell == null) {
                cell = row.createCell(columnIndex);
                cell.setCellValue(value);
            } else {
                cell.setCellValue(value);
            }
            FileOutputStream fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
