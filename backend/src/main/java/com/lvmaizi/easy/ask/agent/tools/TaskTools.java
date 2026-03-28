package com.lvmaizi.easy.ask.agent.tools;

import jakarta.annotation.Resource;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskTools {


    @Resource
    private ListFiles listFiles;

    @Resource
    private ReadFileChunk readFileChunk;

    @Resource
    private SearchRag searchRag;

    @Resource
    private RetrievalTask retrievalTask;

    @Tool(name = "list_files", description = """
            
            This tool retrieves a paginated list of files in a specified directory, along with concise summaries or metadata for each file (e.g., first few lines, embedded description, or auto-generated abstract). The summaries are designed to help infer the content and relevance of each file without loading its full text. \s
            
            Use this tool when you need to: \s
            Explore the contents of a directory efficiently \s
            Identify which files might contain information relevant to the user’s query \s
            Narrow down candidates for deeper inspection (e.g., via full file reading) \s
            
            Note: File summaries may be derived from headers, comments, README snippets, or initial content—depending on file type. Use this tool early in your reasoning to assess which files warrant further examination. \s
            You should read through the entire directory listing page by page, incrementing the page number sequentially, to ensure no potentially relevant file is overlooked—especially when the user’s query requires comprehensive coverage. Do not assume completeness from a single page; continue fetching subsequent pages until you reach the end of the listing or have gathered sufficient evidence to answer the query.
            """)
    public String listFiles(@ToolParam(description = "Page number for pagination (default: 1)") int page) {
        return listFiles.run(page);
    }

    @Tool(name = "read_file_chunk", description = """
            
            This tool reads a specific chunk (page) of content from a given file, enabling efficient inspection of large files without loading the entire document into memory. Each chunk typically corresponds to a fixed number of lines or a logical section (e.g., paragraphs or code blocks), allowing you to progressively scan the file for relevance to the user's query. \s
            
            Use this tool when you need to: \s
            Determine whether a file contains information related to the user’s question \s
            Navigate through large files in a controlled, paginated manner \s
            Avoid unnecessary full-file reads when early chunks already provide sufficient context \s
            
            Notes: \s
            Page size is fixed (e.g., 50 lines per page) and handled internally. \s
            If the requested page exceeds the file’s length, the tool returns an empty result or end-of-file indicator. \s
            Always start with page=1 and incrementally fetch more pages only if earlier chunks do not resolve the query. \s
            You must read the file page by page in sequential order—do not skip pages—even if some chunks appear irrelevant. Skipping pages risks missing critical information, especially for queries requiring exhaustive verification. Continue reading until you reach the end of the file or have gathered conclusive evidence to answer the user’s question.
            
            This tool is ideal for triaging file relevance before committing to a full read or extracting specific details.
            
            """)
    public String readFileChunk(@ToolParam(description = "The absolute or relative path to the target file") String path,
                                @ToolParam(description = "page (integer): The page (chunk) number to retrieve, starting from 1 ") int page) {
        return readFileChunk.run(path, page);
    }

    public List<ToolCallback> getTools() {
        ToolCallback[] toolCallbacks = MethodToolCallbackProvider.builder()
                .toolObjects(this)
                .build()
                .getToolCallbacks();
        return List.of(toolCallbacks);
    }

}
