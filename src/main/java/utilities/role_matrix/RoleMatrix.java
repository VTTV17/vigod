package utilities.role_matrix;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import utilities.excel.Excel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleMatrix {

    /**
     * @param fileName: testData.xlsx
     * @param sheetID:  staff Sheet ID (0,1,2..)
     * @return map of [permission, list of(permission per page)]
     */
    public Map<Integer, List<Integer>> staffPermissions(String fileName, int sheetID) throws IOException {
        Map<Integer, List<Integer>> rolePage = new HashMap<>();
        Sheet sheet = new Excel().getSheet(fileName, sheetID);
        int maxRow = sheet.getLastRowNum();
        int maxCell = sheet.getRow(0).getLastCellNum();

        List<Integer> roleList;
        List<Integer> rowCellBegin = new Excel().getRowCellByKey(fileName, sheetID, "PageTitleVI/PermissionsVI");
        for (int cellNum = rowCellBegin.get(1) + 1; cellNum < maxCell; cellNum++) {
            roleList = new ArrayList<>();
            for (int rowNum = rowCellBegin.get(0) + 1; rowNum <= maxRow; rowNum++) {
                roleList.add(Integer.valueOf(new DataFormatter().formatCellValue(sheet.getRow(rowNum).getCell(cellNum))));
            }
            rolePage.put(cellNum - rowCellBegin.get(1) - 1, roleList);
        }
        return rolePage;
    }

    /**
     * @param fileName: testData.xlsx
     * @param sheetID:  staff Sheet ID (0,1,2..)
     * @return list of [permission text - ENG]
     */
    public Map<Integer, String> permissionTextEN(String fileName, int sheetID) throws IOException {
        Map<Integer, String> permissionText = new HashMap<>();
        Sheet sheet = new Excel().getSheet(fileName, sheetID);
        Row row = sheet.getRow(0);
        List<Integer> rowCellBegin = new Excel().getRowCellByKey(fileName, sheetID, "PermissionsEN");
        for (int cellNum = rowCellBegin.get(1) + 1; cellNum <= row.getLastCellNum(); cellNum++) {
            permissionText.put(cellNum - rowCellBegin.get(1) - 1, new DataFormatter().formatCellValue(sheet.getRow(rowCellBegin.get(0)).getCell(cellNum)));
        }
        return permissionText;
    }

    /**
     * @param fileName: testData.xlsx
     * @param sheetID:  staff Sheet ID (0,1,2..)
     * @return list of [permission text - VIE]
     */
    public Map<Integer, String> permissionTextVI(String fileName, int sheetID) throws IOException {
        Map<Integer, String> permissionText = new HashMap<>();
        Sheet sheet = new Excel().getSheet(fileName, sheetID);
        Row row = sheet.getRow(0);
        List<Integer> rowCellBegin = new Excel().getRowCellByKey(fileName, sheetID, "PageTitleVI/PermissionsVI");
        for (int cellNum = rowCellBegin.get(1) + 1; cellNum <= row.getLastCellNum(); cellNum++) {
            permissionText.put(cellNum - rowCellBegin.get(1) - 1, new DataFormatter().formatCellValue(sheet.getRow(rowCellBegin.get(0)).getCell(cellNum)));
        }
        return permissionText;
    }

    /**
     * @param fileName: testData.xlsx
     * @param sheetID:  staff Sheet ID (0,1,2..)
     * @return list of [page Title - VIE]
     */
    public Map<Integer, String> pageTitleVI(String fileName, int sheetID) throws IOException {
        Map<Integer, String> pageTitle = new HashMap<>();
        Sheet sheet = new Excel().getSheet(fileName, sheetID);
        List<Integer> rowCellBegin = new Excel().getRowCellByKey(fileName, sheetID, "PageTitleVI/PermissionsVI");
        for (int rowID = rowCellBegin.get(0) + 1; rowID <= sheet.getLastRowNum(); rowID++) {
            pageTitle.put(rowID - rowCellBegin.get(0) - 1, new DataFormatter().formatCellValue(sheet.getRow(rowID).getCell(rowCellBegin.get(1))));
        }
        return pageTitle;
    }

    /**
     * @param fileName: testData.xlsx
     * @param sheetID:  staff Sheet ID (0,1,2..)
     * @return list of [page Title - ENG]
     */
    public Map<Integer, String> pageTitleEN(String fileName, int sheetID) throws IOException {
        Map<Integer, String> pageTitle = new HashMap<>();
        Sheet sheet = new Excel().getSheet(fileName, sheetID);
        List<Integer> rowCellBegin = new Excel().getRowCellByKey(fileName, sheetID, "PageTitleEN");
        for (int rowID = rowCellBegin.get(0) + 1; rowID <= sheet.getLastRowNum(); rowID++) {
            pageTitle.put(rowID - rowCellBegin.get(0) - 1, new DataFormatter().formatCellValue(sheet.getRow(rowID).getCell(rowCellBegin.get(1))));
        }
        return pageTitle;
    }

    /**
     * @param fileName: testData.xlsx
     * @param sheetID:  staff Sheet ID (0,1,2..)
     * @return list of [page path]
     */
    public Map<Integer, String> pagePath(String fileName, int sheetID) throws IOException {
        Map<Integer, String> pageTitle = new HashMap<>();
        Sheet sheet = new Excel().getSheet(fileName, sheetID);
        List<Integer> rowCellBegin = new Excel().getRowCellByKey(fileName, sheetID, "PagePath");
        for (int rowID = rowCellBegin.get(0) + 1; rowID <= sheet.getLastRowNum(); rowID++) {
            pageTitle.put(rowID - rowCellBegin.get(0) - 1, new DataFormatter().formatCellValue(sheet.getRow(rowID).getCell(rowCellBegin.get(1))));
        }
        return pageTitle;
    }

    /**
     * @param fileName: testData.xlsx
     * @param sheetID:  domain Sheet ID (0,1,2..)
     * @param env:      domain (ca, stg, prod)
     * @return list of [domain URL, domain Title]
     */
    public List<String> getDomain(String fileName, int sheetID, String env) throws IOException {
        List<String> list = new ArrayList<>();
        Sheet sheet = new Excel().getSheet(fileName, sheetID);
        List<Integer> rowCellBegin = new Excel().getRowCellByKey(fileName, sheetID, env);
        for (int rowID = rowCellBegin.get(0); rowID < rowCellBegin.get(0) + 2; rowID++) {
            list.add(new DataFormatter().formatCellValue(sheet.getRow(rowID).getCell(rowCellBegin.get(1) + 2)));
        }
        return list;
    }
}