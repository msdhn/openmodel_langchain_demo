package eu.msdhn.openmodel_langchain.demo.rag.chunking;

import java.util.List;

public interface TextChunker {

    List<String> chunk(String content);
}
