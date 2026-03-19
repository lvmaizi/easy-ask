package com.lvmaizi.easy.ask.agent.reader;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class PdfReader implements Reader {
    @Override
    public boolean support(String path) {
        return path.endsWith(".pdf");
    }

    @Override
    public String read(String path) {
        try {
            var pdfDocument = Loader.loadPDF(new File(path));
            var stripper = new PDFTextStripper();
            String text = stripper.getText(pdfDocument);
            pdfDocument.close();
            return text;
        } catch (IOException e) {
            log.info("Error reading file: {}", path, e);
        }
        return "";
    }
}
