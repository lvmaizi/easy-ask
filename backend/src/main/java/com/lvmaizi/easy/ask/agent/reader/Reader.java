package com.lvmaizi.easy.ask.agent.reader;

public interface Reader {

    boolean support(String path);

    String read(String path);
}
