package org.dspace.app.rest.utils;

import com.spire.doc.*;
import com.spire.doc.fields.Field;
import com.spire.doc.interfaces.IDocumentObject;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlOptions;
import org.dspace.content.Bitstream;
import org.dspace.content.WorkFlowProcessComment;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MargedDocUtils {
    static String[] filePaths = new String[3];
    static List<String> documentPaths = new ArrayList<>();

    public static void main(String[] args) throws Exception {
       // DocOneWrite(1l);
       // InputStream input = new FileInputStream(new File("D://note2.docx"));
       // InputStream input3 = new FileInputStream(new File("D://note3.docx"));
      //  DocTwoWrite(input);
      //  DocTwoWrite2(input3);
          // DocthreWrite1();
          //writeMultipleFiles();
       // DocumentMerger("D://finalaaaaaaa.docx");


        DocthreWrite1();
    }

    public static void finalwriteDocument(String path) {
        DocumentMerger(path);
    }

    public static void DocOneWrite(Long notecount) {
        final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
        File oneFile = new File(TEMP_DIRECTORY, "file1.docx");
        if (!oneFile.exists()) {
            try {
                oneFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        filePaths[0] = oneFile.getAbsolutePath();
        documentPaths.add(oneFile.getAbsolutePath());
        try (XWPFDocument doc = new XWPFDocument()) {
            // create a paragraph
            XWPFParagraph p1 = doc.createParagraph();
            p1.setAlignment(ParagraphAlignment.CENTER);
            // set font
            XWPFRun r1 = p1.createRun();
            r1.setBold(true);
            r1.setText("Note#" + notecount);

            // save it to .docx file
            try (FileOutputStream out = new FileOutputStream(oneFile)) {
                doc.write(out);
                out.close();
                doc.close();
            }
            System.out.println("First doc save Done!" + oneFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error First doc save !" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String readInputStream(InputStream inputStream) throws IOException {
        // Create a byte array to store the data read from the InputStream.
        byte[] data = new byte[1024];
        int bytesRead;
        StringBuffer buffer = new StringBuffer();
        // Read the data from the InputStream and store it in the byte array.
        while ((bytesRead = inputStream.read(data)) != -1) {
            buffer.append(new String(data, 0, bytesRead));
        }
        // Return the data read from the InputStream as a String.
        return buffer.toString();
    }

    public static void DocTwoWrite(InputStream in) {
        System.out.println("in Seconned doc save Done!");
        final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
        File twoFile = new File(TEMP_DIRECTORY, "file2.docx");
        if (!twoFile.exists()) {
            try {
                twoFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        filePaths[1] = twoFile.getAbsolutePath();
        documentPaths.add(twoFile.getAbsolutePath());
        try (XWPFDocument doc = new XWPFDocument()) {
            FileOutputStream out = new FileOutputStream(twoFile);
            byte[] buf = new byte[8192];
            int length;
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
            System.out.println("Second doc save Done!" + twoFile.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Error Seconned doc save !" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void DocTwoWrite2(InputStream in) {
        System.out.println("in Seconned doc save Done!");
        final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
        File twoFile = new File(TEMP_DIRECTORY, "file3.docx");
        if (!twoFile.exists()) {
            try {
                twoFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        filePaths[2] = twoFile.getAbsolutePath();
        documentPaths.add(twoFile.getAbsolutePath());
        try (XWPFDocument doc = new XWPFDocument()) {
            FileOutputStream out = new FileOutputStream(twoFile);
            byte[] buf = new byte[8192];
            int length;
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
            System.out.println("Second doc save Done!" + twoFile.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Error Seconned doc save !" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void DocthreWrite1() {
        final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
        File oneFile = new File(TEMP_DIRECTORY, "file3.docx");
        if (!oneFile.exists()) {
            try {
                oneFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        filePaths[2] = oneFile.getAbsolutePath();
        documentPaths.add(oneFile.getAbsolutePath());
        try (XWPFDocument doc = new XWPFDocument()) {


            XWPFParagraph paragraph = doc.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("Paragraph 1 LTR");

            paragraph = doc.createParagraph();

            CTP ctp = paragraph.getCTP();
            CTPPr ctppr;
            if ((ctppr = ctp.getPPr()) == null) ctppr = ctp.addNewPPr();
            ctppr.addNewBidi().setVal(ctp);

            run = paragraph.createRun();
            run.setText("Reference Noting");
            paragraph = doc.createParagraph();
            run = paragraph.createRun();
            run.setText("Paragraph 3 LTR");

            // save it to .docx file
            try (FileOutputStream out = new FileOutputStream(oneFile)) {
                doc.write(out);
                out.close();
                doc.close();
            }
            System.out.println("three doc save Done!" + oneFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error First doc save !" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static void DocthreWrite(Map<String, Object> hashMap) {
        System.out.println("in 3 doc!");
        boolean position = false;
        List<String> creator = null;
        final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
        File threeFile = new File(TEMP_DIRECTORY, "file3.docx");
        if (!threeFile.exists()) {
            try {
                threeFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        filePaths[2] = threeFile.getAbsolutePath();
        documentPaths.add(threeFile.getAbsolutePath());

        try (XWPFDocument doc = new XWPFDocument()) {
            // create a paragraph
            int total = hashMap.entrySet().size();
            for (int i = 0; i < total; i++) {
                for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
                    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                    //creater
                    if (entry.getKey().contains("creator") && i == 0) {
                        List<String> list = (List<String>) entry.getValue();
                        for (String creators : list) {
                            XWPFParagraph p1 = doc.createParagraph();
                            p1.setAlignment(ParagraphAlignment.RIGHT);
                            XWPFRun r1 = p1.createRun();
                            r1.setText(creators);
                        }
                    }


                    if (entry.getKey().contains("Reference Noting") && i == 2) {
                        XWPFParagraph Noting = doc.createParagraph();
                        Noting.setAlignment(ParagraphAlignment.RIGHT);
                        XWPFRun r1 = Noting.createRun();
                        r1.setBold(true);
                        r1.setText("Reference Noting");

                        List<Map<String, String>> listreferencenotting = (List<Map<String, String>>) entry.getValue();
                        if (listreferencenotting != null) {
                            System.out.println(listreferencenotting.size());
                            for (Map<String, String> maps : listreferencenotting) {
                                int t = maps.entrySet().size();
                                for (int j = 0; j < t; j++) {
                                    for (Map.Entry<String, String> entry1 : maps.entrySet()) {

                                        if (entry1.getKey().contains("link") && j == 0) {
                                            XWPFParagraph p1 = doc.createParagraph();
                                            p1.setAlignment(ParagraphAlignment.RIGHT);
                                            XWPFRun rb = p1.createRun();
                                            p1.createHyperlinkRun(entry1.getValue().toString());
                                        }
                                        if (entry1.getKey().contains("subject") && j == 1) {
                                            XWPFParagraph p3 = doc.createParagraph();
                                            p3.setAlignment(ParagraphAlignment.RIGHT);
                                            XWPFRun r3 = p3.createRun();
                                            r3.setText(entry1.getValue().toString());

                                        }
                                        if (entry1.getKey().contains("fullname") && j == 2) {
                                            XWPFParagraph p3 = doc.createParagraph();
                                            p3.setAlignment(ParagraphAlignment.RIGHT);
                                            XWPFRun r3 = p3.createRun();
                                            r3.setText(entry1.getValue().toString());
                                        }
                                        if (entry1.getKey().contains("designation") && j == 3) {
                                            XWPFParagraph p3 = doc.createParagraph();
                                            p3.setAlignment(ParagraphAlignment.RIGHT);
                                            XWPFRun r3 = p3.createRun();
                                            r3.setText(entry1.getValue().toString());
                                        }
                                        if (entry1.getKey().contains("date") && j == 4) {
                                            XWPFParagraph p3 = doc.createParagraph();
                                            p3.setAlignment(ParagraphAlignment.RIGHT);
                                            XWPFRun r3 = p3.createRun();
                                            r3.setText(entry1.getValue().toString());
                                        }
                                        if (entry1.getKey().contains("filename") && j == 5) {
                                            XWPFParagraph p3 = doc.createParagraph();
                                            p3.setAlignment(ParagraphAlignment.RIGHT);
                                            XWPFRun r3 = p3.createRun();
                                            r3.setText(entry1.getValue().toString());
                                        }
                                    }
                                }

                            }
                        }
                    }

                    if (entry.getKey().contains("Reference Documents") && i == 1) {
                        XWPFParagraph Documents = doc.createParagraph();
                        Documents.setAlignment(ParagraphAlignment.LEFT);
                        XWPFRun ra = Documents.createRun();
                        ra.setBold(true);
                        ra.setText("Reference Documents");
                        String name = "";
                        String link = "";
                        Map<String, String> map = (Map<String, String>) entry.getValue();
                        for (Map.Entry<String, String> entry1 : map.entrySet()) {

                            if (entry1.getKey().contains("link")) {
                                link = entry1.getValue().toString();
                            }
                            if (entry1.getKey().contains("name")) {
                                name = entry1.getValue().toString();
                            }
                        }
                        XWPFParagraph p1 = doc.createParagraph();
                        p1.setAlignment(ParagraphAlignment.LEFT);
                        XWPFRun rb = p1.createRun();
                        p1.createHyperlinkRun(link);
                        rb.setText(name);
                    }

                    if (entry.getKey().contains("comment") && i == 3) {
                        List<WorkFlowProcessComment> list = (List<WorkFlowProcessComment>) entry.getValue();
                        XWPFParagraph p1 = doc.createParagraph();
                        p1.setAlignment(ParagraphAlignment.LEFT);
                        XWPFRun rc = p1.createRun();
                        rc.setText("[" + list.size() + "]");
                        int index = 1;
                        for (WorkFlowProcessComment b : list) {
                            //comment count
                            XWPFParagraph p2 = doc.createParagraph();
                            p2.setAlignment(ParagraphAlignment.LEFT);
                            XWPFRun r2 = p2.createRun();
                            r2.setBold(true);
                            r2.setText("Comment #" + index + "");

                            //Comment
                            if (b.getComment() != null) {
                                XWPFParagraph p3 = doc.createParagraph();
                                p3.setAlignment(ParagraphAlignment.LEFT);
                                XWPFRun r3 = p3.createRun();
                                r3.setText(b.getComment());
                            }
                            if (b.getSubmitter().getFullName() != null) {
                                XWPFParagraph p3 = doc.createParagraph();
                                p3.setAlignment(ParagraphAlignment.RIGHT);
                                XWPFRun r3 = p3.createRun();
                                r3.setText(b.getSubmitter().getFullName());
                            }
                            if (b.getSubmitter().getDesignation() != null) {
                                XWPFParagraph p3 = doc.createParagraph();
                                p3.setAlignment(ParagraphAlignment.RIGHT);
                                XWPFRun r3 = p3.createRun();
                                r3.setText(b.getSubmitter().getDesignation().getPrimaryvalue());
                            }
                            if (b.getWorkFlowProcessHistory().getActionDate() != null) {
                                XWPFParagraph p3 = doc.createParagraph();
                                p3.setAlignment(ParagraphAlignment.RIGHT);
                                XWPFRun r3 = p3.createRun();
                                r3.setText(DateFormate(b.getWorkFlowProcessHistory().getActionDate()));
                            }
                            index++;
                        }
                    }
                }
            }

            // save it to .docx file
            try (FileOutputStream out = new FileOutputStream(threeFile)) {
                doc.write(out);
            }
            System.out.println("3 doc save Done!");
        } catch (IOException e) {
            System.out.println("Error First doc save !" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void DocumentMerger(String finalpathe) {
        System.out.println("in marged doc 1 to 2 ");
        //File path of the first document
        //Load the first document
        Document document1 = new Document(filePaths[0]);
        //Load the second document
        Document document2 = new Document(filePaths[1]);
        //Get the last section of the first document
        Section lastSection = document1.getLastSection();

        //Add the sections of the second document to the last section of the first document
        for (Section section : (Iterable<Section>) document2.getSections()) {
            for (DocumentObject obj : (Iterable<DocumentObject>) section.getBody().getChildObjects()
            ) {
                lastSection.getBody().getChildObjects().add(obj.deepClone());
            }
        }
        final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
        File file1andfile2 = new File(TEMP_DIRECTORY, "file1andfile2.docx");
        //Save the resultant document
        document1.saveToFile(file1andfile2.getAbsolutePath(), FileFormat.Docx_2013);
        System.out.println("in marged doc 1 to 2 done");
        DocumentMerger2(file1andfile2.getAbsolutePath(), finalpathe);

    }

    public static void DocumentMerger2(String mrgedoneandtwo, String finalpathe) {
        Document document = new Document();
        System.out.println("in marged doc 2 to 3 ");
        //File path of the first document
        //Load the first document
        Document document1 = new Document(mrgedoneandtwo);
        //Load the second document
        Document document2 = new Document(filePaths[2]);
        //Get the last section of the first document
        Section lastSection = document1.getLastSection();
        //Add the sections of the second document to the last section of the first document
        for (Section section : (Iterable<Section>) document2.getSections()) {
            for (DocumentObject obj : (Iterable<DocumentObject>) section.getBody().getChildObjects()
            ) {
                lastSection.getBody().getChildObjects().add(obj.deepClone());
            }
        }
        //Save the resultant document
        document1.saveToFile(finalpathe, FileFormat.Docx_2013);
        System.out.println("in marged doc 2 to 3 done");
    }

    private static String DateFormate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        return formatter.format(date);
    }
}
