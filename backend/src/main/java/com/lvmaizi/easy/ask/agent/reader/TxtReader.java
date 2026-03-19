package com.lvmaizi.easy.ask.agent.reader;

import com.lvmaizi.easy.ask.utils.FileReadUtil;
import org.springframework.stereotype.Component;

@Component
public class TxtReader implements Reader {
    @Override
    public boolean support(String path) {
        return path.endsWith(".txt") ||
                path.endsWith(".md") ||
                path.endsWith(".markdown") ||
                path.endsWith(".json") ||
                path.endsWith(".xml") ||
                path.endsWith(".yaml") ||
                path.endsWith(".yml") ||
                path.endsWith(".csv") ||
                path.endsWith(".log") ||
                path.endsWith(".sql") ||
                path.endsWith(".js") ||
                path.endsWith(".ts") ||
                path.endsWith(".java") ||
                path.endsWith(".py") ||
                path.endsWith(".go") ||
                path.endsWith(".c") ||
                path.endsWith(".cpp") ||
                path.endsWith(".h") ||
                path.endsWith(".html") ||
                path.endsWith(".css") ||
                path.endsWith(".sh") ||
                path.endsWith(".bat") ||
                path.endsWith(".properties") ||
                path.endsWith(".conf") ||
                path.endsWith(".config")
                ;
    }

    @Override
    public String read(String path) {
        return FileReadUtil.getContent(path);
    }
}
