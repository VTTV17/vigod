package utilities.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Utility class for working with Excel files using Apache POI.
 */
public class ExcelUtils {

    private final String filePath;
    private final Workbook workbook;

    /**
     * Initializes the utility with the specified Excel file path.
     *
     * @param filePath the path to the Excel file.
     */
    public ExcelUtils(String filePath) {
        this.filePath = filePath;
        this.workbook = loadWorkbook(filePath);
    }

    /**
     * Loads the Excel workbook from the given file path.
     *
     * @param filePath the path to the Excel file.
     * @return the Workbook object.
     */
    private Workbook loadWorkbook(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            return new XSSFWorkbook(fis);
        } catch (IOException e) {
            throw new RuntimeException("Error loading Excel file", e);
        }
    }

    /**
     * Reads a cell value from a specific sheet, row, and column.
     *
     * @param sheetIndex  the sheet index.
     * @param rowIndex    the row index.
     * @param columnIndex the column index.
     * @return the cell value as a string, or null if empty.
     */
    public String readCellValue(int sheetIndex, int rowIndex, int columnIndex) {
        return getCellValueAsString(getCell(sheetIndex, rowIndex, columnIndex));
    }

    /**
     * Reads all values from a column.
     *
     * @param sheetIndex  the sheet index.
     * @param columnIndex the column index.
     * @return a list of column values as strings.
     */
    public List<String> readColumn(int sheetIndex, int columnIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        List<String> columnData = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
            columnData.add(getCellValueAsString(getCell(sheetIndex, rowIndex, columnIndex)));
        }
        return columnData;
    }

    /**
     * Reads all values from a row.
     *
     * @param sheetIndex the sheet index.
     * @param rowIndex   the row index.
     * @return a list of row values as strings.
     */
    public List<String> readRow(int sheetIndex, int rowIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        Row row = sheet.getRow(rowIndex);
        List<String> rowData = new ArrayList<>();
        if (row != null) {
            for (int columnIndex = 0; columnIndex < row.getLastCellNum(); columnIndex++) {
                rowData.add(getCellValueAsString(getCell(sheetIndex, rowIndex, columnIndex)));
            }
        }
        return rowData;
    }

    /**
     * Writes values to a column starting from a specific row index.
     *
     * @param sheetIndex    the sheet index.
     * @param columnIndex   the column index.
     * @param startRowIndex the starting row index.
     * @param values        the values to write.
     */
    public void writeColumn(int sheetIndex, int columnIndex, int startRowIndex, List<String> values) {
        IntStream.range(0, values.size())
                .forEachOrdered(rowIndex -> writeCell(sheetIndex, rowIndex + startRowIndex + 1, columnIndex, values.get(rowIndex)));
        saveChanges();
    }


    /**
     * Writes values to a column identified by a specific cell value.
     *
     * @param sheetIndex    the sheet index.
     * @param cellValue     the value to identify the column.
     * @param values        the values to write.
     */
    public void writeColumnByValue(int sheetIndex, String cellValue, List<String> values) {
        int[] cellDecimal = findCellByValue(sheetIndex, cellValue);
        writeColumn(sheetIndex, cellDecimal[1], cellDecimal[0], values);
    }

    /**
     * Writes values to a row starting from a specific column index.
     *
     * @param sheetIndex    the sheet index.
     * @param rowIndex   the row index.
     * @param startColumn the starting column index.
     * @param values        the values to write.
     */
    public void writeRow(int sheetIndex, int rowIndex, int startColumn, List<String> values) {
        IntStream.range(0, values.size())
                .forEachOrdered(columnIndex -> writeCell(sheetIndex, columnIndex + startColumn + 1, rowIndex, values.get(columnIndex)));
        saveChanges();
    }

    /**
     * Writes values to a row identified by a specific cell value.
     *
     * @param sheetIndex    the sheet index.
     * @param cellValue     the value to identify the column.
     * @param values        the values to write.
     */
    public void writeRowByValue(int sheetIndex, String cellValue, List<String> values) {
        int[] cellDecimal = findCellByValue(sheetIndex, cellValue);
        writeRow(sheetIndex, cellDecimal[0], cellDecimal[1], values);
    }

    /**
     * Finds the first occurrence of a cell with a specific value.
     *
     * @param sheetIndex the sheet index.
     * @param value      the value to search for.
     * @return the row and column index as [rowIndex, columnIndex].
     * @throws RuntimeException if the value is not found.
     */
    public int[] findCellByValue(int sheetIndex, String value) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (value.equals(cell.toString())) {
                    return new int[]{row.getRowNum(), cell.getColumnIndex()};
                }
            }
        }
        throw new RuntimeException("Cell with value \"" + value + "\" not found.");
    }

    /**
     * Retrieves a cell at the specified sheet, row, and column index.
     *
     * @param sheetIndex  the sheet index.
     * @param rowIndex    the row index.
     * @param columnIndex the column index.
     * @return the cell at the specified position, or null if it doesn't exist.
     */
    private Cell getCell(int sheetIndex, int rowIndex, int columnIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        Row row = sheet.getRow(rowIndex);
        if (row == null) return null;
        return row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    }

    /**
     * Writes a value to a specific cell.
     *
     * @param sheetIndex  the sheet index.
     * @param rowIndex    the row index.
     * @param columnIndex the column index.
     * @param value       the value to write.
     */
    public void writeCell(int sheetIndex, int rowIndex, int columnIndex, String value) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        // Check if the row exists, if not create it
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex); // Create the row if it doesn't exist
        }
        Cell cell = row.createCell(columnIndex);
        setCellValue(cell, value);
    }


    /**
     * Sets the cell value based on its type.
     *
     * @param cell  the cell to write to.
     * @param value the value to write.
     */
    private void setCellValue(Cell cell, String value) {
        cell.setCellValue(value != null ? value : "");
    }

    /**
     * Retrieves the value of a cell and converts it to a string.
     *
     * @param cell the cell to extract the value from.
     * @return the cell value as a string.
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getDateCellValue().toString()
                    : String.format("%.10f", cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    /**
     * Saves changes to the file.
     */
    private void saveChanges() {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        } catch (IOException e) {
            throw new RuntimeException("Error saving Excel file", e);
        }
    }

    /**
     * Closes the workbook and releases resources.
     */
    public void close() {
        try {
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing workbook", e);
        }
    }
}