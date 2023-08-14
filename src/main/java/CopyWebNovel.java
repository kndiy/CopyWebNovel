import org.apache.poi.xwpf.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.List;
import java.util.TreeSet;

public class CopyWebNovel {
    private final TreeSet<String> UNWANTED_STRINGS = new TreeSet<>(List.of(
            "© 2018 BOXNOVEL. All rights reserved",
            "Username or Email Address *",
            "Password *",
            "Remember Me",
            "Lost your password?",
            "← Back to BoxNovel",
            "Register For This Site.",
            "Username *",
            "Email Address *",
            "Log in | Lost your password?",
            "Please enter your username or email address. You will receive a link to create a new password via email.",
            "Username or Email Address",
            "Tags:",
            "Sign in",
            "Sign Up",
            "",
            " ",
            "  ",
            "   "
    ));

    private void parseContent(Document doc, XWPFDocument outputDoc, int chapNum) {

        Elements elements = doc.select("p, h1, h2, h3, h4, h5, h6");

        boolean firstLineDone = false;

        for (Element element : elements) {

            String text = element.text();

            if (UNWANTED_STRINGS.contains(text)) {
                continue;
            }
            try {
                Integer.parseInt(text);
                continue;
            }
            catch (Exception ex) {
            }

            XWPFParagraph para = outputDoc.createParagraph();
            para.setAlignment(ParagraphAlignment.LEFT);

            if (!firstLineDone) {

                boolean hasChapterString = checkChapterString(text);
                boolean hasChapterNumber = checkChapterNumber(text);

                if (hasChapterNumber) {
                    text = "Chapter " + text;
                }
                if (!hasChapterNumber && !hasChapterString) {
                    text = "Chapter " + chapNum + "\n" + text;
                }

                para.setStyle("Heading1");
                XWPFRun formatHeader = para.createRun();
                formatHeader.setBold(true);
                formatHeader.setFontSize(14);
                formatHeader.setFontFamily("Bookerly");
                formatHeader.setText(text);

                firstLineDone = true;
            }
            else {
                para.setFirstLineIndent(360);
                para.setPageBreak(false);
                para.setStyle("Normal");
                XWPFRun formatBody = para.createRun();
                formatBody.setBold(false);
                formatBody.setFontSize(13);
                formatBody.setFontFamily("Bookerly");
                formatBody.setText(text);
            }
        }
    }

    private boolean checkChapterNumber(String text) {
        String firstWord = text.split(" ")[0];
        try {
            Integer.parseInt(firstWord);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    private boolean checkChapterString(String text) {
        String firstWord = text.split(" ")[0];
        return firstWord.equals("Chapter");
    }

    public void copyWebNovel(String outputName, int start, int end, String urlHead, String finalChapUrl) throws IOException {
        String urlTail = "/";

        try (XWPFDocument outputDoc = new XWPFDocument(new FileInputStream(outputName))) {

            for (int i = start; i <= end; i++) {

                String url = "";
                if (i == end && !finalChapUrl.equals("")) {
                    url = finalChapUrl;
                }
                else {
                    url = urlHead + i + urlTail;
                }

                System.out.println("Currently parsing url: " + url);

                Document doc = Jsoup.connect(url).get();

                parseContent(doc, outputDoc, i);

                XWPFParagraph breakPage = outputDoc.createParagraph();
                breakPage.setPageBreak(true);

                try (FileOutputStream out = new FileOutputStream(outputName)) {
                    outputDoc.write(out);
                }
            }
        }
    }



    public void parseHeader(Document doc, XWPFDocument outputDoc, int i) throws IOException {
        Elements headers = doc.select("h1, h2, h3, h4, h5, h6");

        if (headers.isEmpty()) {
            String headerText = "Chapter " + i + ":";
            XWPFParagraph headerPara = outputDoc.createParagraph();
            headerPara.setAlignment(ParagraphAlignment.LEFT);
            headerPara.setStyle("Heading1");
            XWPFRun formatHeader = headerPara.createRun();
            formatHeader.setBold(true);
            formatHeader.setFontSize(14);
            formatHeader.setFontFamily("Bookerly");
            formatHeader.setText(headerText);
        }
        else {
            for (Element h : headers) {
                String headerText = h.text();

                if (!UNWANTED_STRINGS.contains(headerText)) {
                    try {
                        Integer.parseInt(headerText);
                    } catch (Exception ex) {

                        boolean hasChapterString = checkChapterString(headerText);
                        if (!hasChapterString) {
                            headerText = "Chapter " + headerText;
                        }

                        XWPFParagraph headerPara = outputDoc.createParagraph();
                        headerPara.setAlignment(ParagraphAlignment.LEFT);
                        headerPara.setStyle("Heading1");
                        XWPFRun formatHeader = headerPara.createRun();
                        formatHeader.setBold(true);
                        formatHeader.setFontSize(14);
                        formatHeader.setFontFamily("Bookerly");
                        formatHeader.setText(headerText);
                    }
                }
            }
        }
    }

    public void parseBody(Document doc, XWPFDocument outputDoc) throws IOException {
        Elements paragraphs = doc.select("p");

        for (Element p : paragraphs) {
            String paragraphText = p.text();
            if (!UNWANTED_STRINGS.contains(paragraphText)) {
                try {
                    Integer.parseInt(paragraphText);
                }
                catch (Exception ex) {
                    XWPFParagraph bodyPara = outputDoc.createParagraph();
                    bodyPara.setAlignment(ParagraphAlignment.LEFT);
                    bodyPara.setFirstLineIndent(360);
                    bodyPara.setPageBreak(false);
                    bodyPara.setStyle("Normal");
                    XWPFRun formatBody = bodyPara.createRun();
                    formatBody.setBold(false);
                    formatBody.setFontSize(13);
                    formatBody.setFontFamily("Bookerly");
                    formatBody.setText(paragraphText);
                }
            }
        }
    }


}
