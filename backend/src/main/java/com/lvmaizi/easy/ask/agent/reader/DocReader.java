package com.lvmaizi.easy.ask.agent.reader;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
public class DocReader implements Reader {
    @Override
    public boolean support(String path) {
        return path.endsWith(".doc") || path.endsWith(".docx");
    }

    @Override
    public String read(String path) {
        try (InputStream fis = new FileInputStream(path)) {
            if (path.toLowerCase().endsWith(".doc")) {
                // 处理 .doc 文件（旧格式）
                HWPFDocument doc = new HWPFDocument(fis);
                WordExtractor extractor = new WordExtractor(doc);
                String text = extractor.getText();
                extractor.close(); // 显式关闭以释放资源
                return text;
            } else if (path.toLowerCase().endsWith(".docx")) {
                // 处理 .docx 文件（新格式）
                XWPFDocument doc = new XWPFDocument(fis);
                XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
                String text = extractor.getText();
                extractor.close();
                return text;
            }
        } catch (IOException e) {
            log.warn("Error reading file: {}", path, e);
        }
        return "";
    }

}
