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
            
            This tool is ideal for triaging file relevance before committing to a full read or extracting specific details.
            
            """)
    public String readFileChunk(@ToolParam(description = "The absolute or relative path to the target file") String path,
                             @ToolParam(description = "page (integer): The page (chunk) number to retrieve, starting from 1 ") int page) {
        return readFileChunk.run(path, page);
    }

    @Tool(name = "search_rag", description = """
           
            This tool performs a retrieval-augmented generation (RAG)-based search over a curated knowledge base of frequently asked questions (FAQs) and their official answers. It is designed to quickly surface relevant, pre-verified responses to common user inquiries—such as onboarding steps, configuration guidelines, or standard procedures.
            
            Use this tool early in your reasoning process when the user’s question appears general, procedural, or likely covered in documentation. The underlying RAG system encodes FAQ entries into semantic vectors and retrieves the top-k most relevant Q&A pairs based on the user's query.
            
            Important Notes: \s
            The FAQ knowledge base is limited in scope: It only covers well-established, recurring topics. It does not contain project-specific files, logs, or custom user data. \s
            A successful match is not guaranteed: If no relevant FAQ entry is found (e.g., low similarity score or empty results), you must fall back to other file-based tools (e.g., list_directory_with_summaries, read_file_chunk) to search the local document corpus. \s
            Do not treat this as a definitive answer source for novel or context-specific questions—use it as a first-pass helper, not a replacement for direct evidence from user-provided files.
            
            Returns: \s
            A list of relevant FAQ entries (question + answer) ranked by semantic relevance, or an empty list if nothing matches.
            
            """)
    public String searchRag(@ToolParam(description = "User input question (allow appropriate rewriting)") String prompt) {
        return searchRag.run(prompt);
    }

    @Tool(name = "create_deep_retrieval_task", description = """
          
            This tool initiates an asynchronous, in-depth retrieval task to comprehensively analyze the entire local document corpus when simpler or targeted tool calls (e.g., FAQ search, file listing, or chunk reading) fail to resolve the user’s query. Due to the potentially large volume of files and computational cost, this process runs in the background and cannot return results immediately.
            
            The deep retrieval task systematically scans relevant directories, reads applicable files (including full content if needed), applies semantic analysis, and synthesizes a thorough answer based on all available evidence. It is designed for complex, open-ended, or highly specific questions that require broad contextual understanding beyond surface-level inspection.
            
            When to Use: \s
            Only after multiple attempts with faster tools (list_files, read_file_chunk) have failed to yield a confident answer \s
            When the user’s question implies a need for exhaustive search (e.g., “What does our project say about X across all documents?”) \s
            Always request explicit user consent before invoking this tool, as it may take significant time to complete
            
            Workflow: \s
            The assistant asks the user:“I couldn’t find a clear answer with quick checks. Would you like me to start a deep scan of all documents? This may take a few minutes—results will be available later.” \s
            If the user agrees, call this tool. \s
            Immediately inform the user:“Deep retrieval task started. Please check back in a few minutes for a detailed answer.”
            
            Returns: \s
            A task ID and estimated completion time (not the final answer). The final response will be delivered asynchronously via a separate channel or follow-up query.
            
            Note: Do not use this tool for simple or time-sensitive questions. Reserve it for cases where completeness outweighs latency.
            
            """)
    public String deepRetrievalTask(@ToolParam(description = "User input question (allow appropriate rewriting)") String prompt) {
        return retrievalTask.run(prompt);
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
        if ("read_file_chunk".equals( name)) {
            return "获取文件内容";
        }
        if ("search_rag".equals( name)) {
            return "获取本地知识库";
        }
        if ("create_deep_retrieval_task".equals( name)) {
            return "创建深度检索任务";
        }
        return "";
    }
}
