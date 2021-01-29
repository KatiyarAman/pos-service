package com.ris.inventory.pos.util;

import com.ris.inventory.pos.config.ApplicationConfig;
import com.ris.inventory.pos.model.dto.DownloadDTO;
import com.ris.inventory.pos.util.enumeration.ReportType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class DocumentUtil {

    public static final String PDF_EXTENSION = ".pdf";

    public static final String EXCEL_EXTENSION = ".xlsx";

    public static final String CSV_EXTENSION = ".csv";

    private static final Logger logger = LoggerFactory.getLogger(DocumentUtil.class);

    @Autowired
    private ApplicationConfig config;

    private static String camelCaseToUpperCase(String column) {
        List<Character> characters = getChars();
        char[] chars = column.toCharArray();

        List<Integer> words = new ArrayList<>();
        for (char aChar : chars) {
            if (characters.contains(aChar)) {
                words.add(column.indexOf(aChar));
            }
        }

        StringBuilder builder = new StringBuilder();

        if (words.size() >= 1)
            builder.append(column, 0, words.get(0)).append(" ");

        for (int i = 0; i < words.size(); i++) {
            int j = i + 1;
            if (j < words.size())
                builder.append(column, words.get(i), words.get(j)).append(" ");
            else
                builder.append(column.substring(words.get(i)));

        }
        if (builder.length() != 0)
            column = builder.toString();
        return column.toUpperCase();
    }

    private static List<Character> getChars() {
        List<Character> characters = new ArrayList<>();

        for (int i = 65; i <= 90; i++)
            characters.add((char) i);

        return characters;
    }

    public static List<String> convertColumnNameCase(JSONObject jsonObject) {
        Set<String> keys = jsonObject.keySet();
        List<String> columns = new ArrayList<>();

        for (String column : keys) {
            columns.add(camelCaseToUpperCase(column));
        }
        return columns;
    }

    public String getPath(String fileName) throws IOException {
        logger.info("fetching class path for documents");
        String filePath = config.getDownloadPath() + fileName;
        logger.info("Class path for documents with file name {}", filePath);
        return filePath;
    }

    public void createPDF(String filePath, String titleString, List<String> columns, JSONArray data) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        PDFont font_bold = PDType1Font.TIMES_BOLD_ITALIC;
        PDFont font_normal = PDType1Font.TIMES_ROMAN;

        contentStream.beginText();
        contentStream.setFont(font_bold, 24);
        contentStream.addComment(titleString);

/*
        final int rows = data.length();
        final int cols = columns.size();
        final float rowHeight = 20f;
        final float tableWidth = page.getMediaBox().getWidth() - 50 - 50;
        final float tableHeight = rowHeight * rows;
        final float colWidth = tableWidth / (float) cols;
        final float cellMargin = 5f;

        //draw the rows
        float nexty = y;
        for (int i = 0; i <= rows; i++) {
// draw the columns
            contentStream.moveTo(margin, nexty);
            contentStream.lineTo(margin, nexty);
            contentStream.stroke();
            nexty -= rowHeight;
            nexty -= rowHeight;
        }

        //draw the columns
        float nextx = margin;
        for (int i = 0; i <= cols; i++) {
            contentStream.drawLine(nextx, y, nextx, y - tableHeight);
            nextx += colWidth;
        }

        //now add the text
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

        float textx = margin + cellMargin;
        float texty = y - 15;
        for (int i = 0; i < content.length; i++) {
            for (int j = 0; j < content[i].length; j++) {
                String text = content[i][j];
                contentStream.beginText();
                contentStream.moveTextPositionByAmount(textx, texty);
                contentStream.drawString(text);
                contentStream.endText();
                textx += colWidth;
            }
            texty -= rowHeight;
            textx = margin + cellMargin;
        }
*/
        contentStream.endText();

        contentStream.close();
        document.save(filePath);
        document.close();
    }

/*
    private void addTableHeader(PdfPTable table, List<String> columns) {
        List<String> subcolumns = columns.subList(0, 5);
        subcolumns.forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setNoWrap(true);
            header.setPadding(3f);
            header.setBackgroundColor(BaseColor.CYAN);
            header.setPhrase(new Phrase(columnTitle));
            table.addCell(header);
        });
    }
*/

    public void createExcel(String filePath, DownloadDTO downloadable) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        List<String> columns = downloadable.getColumns();
        List<String> actualColumns = downloadable.getActualColumns();
        JSONArray data = downloadable.getData();
        XSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.size(); i++) {
            headerRow.createCell(i).setCellValue(columns.get(i));
        }

        for (int i = 0; i < data.length(); i++) {
            XSSFRow actualRow = sheet.createRow(i + 1);
            JSONObject object = data.getJSONObject(i);
            for (int j = 0; j < actualColumns.size(); j++) {
                actualRow.createCell(j).setCellValue(String.valueOf(object.opt(actualColumns.get(j))));
            }
        }
        workbook.write(new FileOutputStream(filePath));
    }

    public void createCSV(String filePath, List<String> columns, JSONArray data) throws IOException {

        FileWriter writer = new FileWriter(filePath);
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Id", "Name", "Age", "Salary"));

       /* for (int i = 0; i < data.length(); i++) {
            JSONObject row = data.getJSONObject(i).toMap().forEach(
                    (Id, Name, Age, Salary) -> printer.printRecord(Id, Name, Age, Salary)
            );
        }*/

        printer.flush();
    }

    public String getFileName(ReportType reportType, String extension) {
        return reportType.getType() + "_" + new Date().getTime() + extension;
    }

    public void send(HttpServletResponse response, File file) throws IOException {
        logger.info("Sending file {}", file);

        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
        response.setContentLength((int) file.length());
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }
}
