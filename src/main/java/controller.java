import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PaneInformation;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;

import static java.lang.Math.floor;
import static java.lang.Math.pow;

public class controller implements Initializable {

    public Canvas canvas;
    public TextField alfa;
    public TextField r;
    public GraphicsContext g;
    @FXML
    public LineChart lineChart;
    @FXML
    public NumberAxis xAxis;
    @FXML
    public NumberAxis yAxis;
    private Stage stage;
    List<Double> dataX = new ArrayList<Double>();
    List<Double> dataY = new ArrayList<Double>();
    int columnSelected = 0;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void spirala() {

        XYChart.Series series = new XYChart.Series();
        series.setName("Data Chart");
        for (int i = 0; i < dataX.size(); i++) {
            series.getData().add(new XYChart.Data<>(dataX.get(i), dataY.get(i)));

        }
        lineChart.getData().addAll(series);

    }

    @FXML
    public void loadDataTxt() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(stage);


        int i = 0;
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (file != null) {
            String params = scanner.next();
            xAxis.setLabel(params);
            params = scanner.next();
            yAxis.setLabel(params);
            while (scanner.hasNext()) {
                dataX.add(Double.parseDouble(scanner.next()));
                dataY.add(Double.parseDouble(scanner.next()));
                i++;
            }

        }
        yAxis.setAutoRanging(false);
        List<Double> sorted = new ArrayList<>(dataY);
        Collections.sort(sorted);
        yAxis.setLowerBound(sorted.get(0));
        yAxis.setUpperBound(sorted.get(dataY.size() - 1));
    }

    @FXML
    public void expo() {
        double alf = Double.parseDouble(alfa.getText());
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Exponential " + '\u03B1' + ": " + alf);
        double[] exp = new double[dataY.size()];
        for (int i = 0; i < dataX.size(); i++) {
            if (i == 0) {
                exp[i] = dataY.get(i);
            } else {
                exp[i] = alf * dataY.get(i - 1) + (1 - alf) * exp[i - 1];
            }
            double x = dataX.get(i);
            double y = exp[i];
            series1.getData().add(new XYChart.Data<>(x, y));
        }
        lineChart.getData().addAll(series1);
    }

    @FXML
    public void mnk() {
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("MNK");
        List<Double> sorted1 = new ArrayList<>(dataX);
        Collections.sort(sorted1);
        int n = (int) floor(sorted1.size() - 1);
        double[] reg = new double[n];
        double xy = 0, xi = 0, yi = 0, x2 = 0;
        double a, b;
        for (int i = 0; i < dataX.size(); i++) {
            xy += dataX.get(i) * dataY.get(i);
            xi += dataX.get(i);
            yi += dataY.get(i);
            x2 += pow(dataX.get(i), 2);
        }
        double xsr = xi / dataX.size();
        double ysr = yi / dataY.size();
        double l = 0, m = 0;
        for (int i = 0; i < dataX.size(); i++) {
            l += (dataX.get(i) - xsr) * (dataY.get(i) - ysr);
            m += (dataX.get(i) - xsr) * (dataX.get(i) - xsr);
        }
        a = l / m;
        b = ysr - a * xsr;
        System.out.println(n + " " + a + " " + b);

        for (int i = 0; i < dataX.size() - 1; i++) {

            reg[i] = a * dataX.get(i) + b;
            double y = reg[i];
            series2.getData().add(new XYChart.Data<>(dataX.get(i), y));
        }
        yAxis.setAutoRanging(false);
        List<Double> sorted = new ArrayList<>(dataY);
        Collections.sort(sorted);


        lineChart.getData().addAll(series2);
    }

    public void loadDataXls(ActionEvent actionEvent) throws FileNotFoundException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(stage);
        FileInputStream fileInputStream = new FileInputStream(file);

        //HSSF

        HSSFWorkbook workbook = null;
        try {
            workbook = new HSSFWorkbook(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HSSFSheet sheet = workbook.getSheetAt(0);

        System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");


        DataFormatter dataFormatter = new DataFormatter();

        Iterator<Row> rowIterator = sheet.rowIterator();
        int k =0;
        int i = 0;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            // Now let's iterate over the columns of the current row
            Iterator<Cell> cellIterator = row.cellIterator();

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                String cellValue = dataFormatter.formatCellValue(cell);
                if(cellValue == "")
                    break;
                if (k == 0) {
                    xAxis.setLabel(cellValue);
                    cell = cellIterator.next();
                    cellValue = dataFormatter.formatCellValue(cell);
                    yAxis.setLabel(cellValue);
                    System.out.print(xAxis.getLabel() + "\t" + yAxis.getLabel());
                    k++;

                }
                else{
                    dataX.add(Double.parseDouble(cellValue));
                    cell = cellIterator.next();
                    cellValue = dataFormatter.formatCellValue(cell);
                    dataY.add(Double.parseDouble(cellValue));
                    System.out.print(dataX.get(i) + "\t" + dataY.get(i));
                    i++;
                }


                //System.out.print(cellValue + "\t");
            }
            System.out.println();
        }

       // yAxis.setAutoRanging(false);
        List<Double> sorted = new ArrayList<>(dataY);
        Collections.sort(sorted);
        yAxis.setLowerBound(sorted.get(0));
        yAxis.setUpperBound(sorted.get(dataY.size() - 1));


    }

    public void loadDataXlsx(ActionEvent actionEvent) throws FileNotFoundException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("XLSX files (*.txt)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(stage);
        FileInputStream fileInputStream = new FileInputStream(file);

        //XSSF

        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");

        XSSFSheet sheet = workbook.getSheetAt(0);
        int noOfCol = sheet.getRow(0).getPhysicalNumberOfCells();
        DataFormatter dataFormatter = new DataFormatter();



        if (noOfCol>2)
        {
            Row firstRow = sheet.getRow(0);
            Iterator<Cell> cellIterator = firstRow.cellIterator();
            ArrayList <String> dialogEntries = new ArrayList<String>();


            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                String cellValue = dataFormatter.formatCellValue(cell);
                dialogEntries.add(cellValue);
            }

            xAxis.setLabel(dialogEntries.get(0));
            dialogEntries.remove(0);


            ChoiceDialog<String> dialog = new ChoiceDialog<String>(dialogEntries.get(0), dialogEntries);

            dialog.setTitle("Variable chooser");
            dialog.setHeaderText("There are too many variables, choose one please:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(selectedColumn -> {
               System.out.println("Test "+selectedColumn +"o indexie "+dialogEntries.indexOf(selectedColumn));
                columnSelected = dialogEntries.indexOf(selectedColumn);

            });
            yAxis.setLabel(dialogEntries.get(columnSelected));

            columnSelected++;

        }

        System.out.println("Workbook has " + noOfCol + " Columns : ");

        for(Row r : sheet){
            Cell c = r.getCell(columnSelected);
            if(c!=null){
                if(c.getCellType() == Cell.CELL_TYPE_NUMERIC){
                    dataY.add(c.getNumericCellValue());
                } else if (c.getCellType() == Cell.CELL_TYPE_FORMULA && c.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC) {
                    dataY.add(c.getNumericCellValue());
                }
            }

        }

        for(Row r : sheet){
            Cell c = r.getCell(0);
            if(c!=null){
                if(c.getCellType() == Cell.CELL_TYPE_NUMERIC){
                    dataX.add(c.getNumericCellValue());
                } else if (c.getCellType() == Cell.CELL_TYPE_FORMULA && c.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC) {
                    dataX.add(c.getNumericCellValue());
                }
            }

        }

        List<Double> sorted = new ArrayList<>(dataY);
        Collections.sort(sorted);
        yAxis.setLowerBound(sorted.get(0));
        yAxis.setUpperBound(sorted.get(dataY.size() - 1));



        /*
        sheet.forEach(row -> {
            row.forEach(cell -> {
                String cellValue = dataFormatter.formatCellValue(cell);
                System.out.print(cellValue + "\t");
            });
            System.out.println();
        });*/
    }
}
