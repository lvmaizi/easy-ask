package com.lvmaizi.easy.ask.agent.tools;

import jakarta.annotation.Resource;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AskTools {

    @Resource
    private ListFiles listFiles;

    @Resource
    private GetContent getContent;

    @Resource
    private GetRag getRag;

    @Tool(name = "list_files", description = "Get the list of all document files in the repository")
    public String listFiles(@ToolParam(description = "Page number") int page) {
        return listFiles.run(page);
    }

    @Tool(name = "get_content", description = "Get file content by file path")
    public String getContent(@ToolParam(description = "File path") String path) {
        return getContent.run(path);
    }

    @Tool(name = "get_rag", description = "Query the Q&A knowledge base based on user question vector search. If this tool does not return relevant answers, you need to call other tools to complete relevant tasks")
    public String getRag(@ToolParam(description = "User input question (allow appropriate rewriting)") String prompt) {
        return getRag.run(prompt);
    }
    public List<ToolCallback> getTools() {
        ToolCallback[] toolCallbacks = MethodToolCallbackProvider.builder()
                .toolObjects(this)
                .build()
                .getToolCallbacks();
        return List.of(toolCallbacks);
    }

    public static String getName(String name) {
        if ("list_files".equals( name)) {
            return "检索文件列表";
        }
        if ("get_content".equals( name)) {
            return "获取文件内容";
        }
        if ("get_rag".equals( name)) {
            return "获取本地知识库";
        }
        return "";
    }
}
