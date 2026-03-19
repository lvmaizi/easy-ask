package com.lvmaizi.easy.ask.agent.dataset;

import java.util.Set;

public class SupportFiles {

    private static final Set<String> SUPPORT_FILES = Set.of(
            ".txt",
            ".md",
            ".doc",
            ".docx",
            ".pdf",
            ".ppt",
            ".pptx",
            ".xls",
            ".xlsx",
            ".csv",
            ".xml",
            ".yaml",
            ".yml",
            ".sh"
    );

    public static boolean support(String path) {
        for (String supportFile : SUPPORT_FILES) {
            if (path.endsWith(supportFile)) {
                return true;
            }
        }
        return false;
    }
}
