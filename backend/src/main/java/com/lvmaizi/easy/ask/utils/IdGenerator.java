package com.lvmaizi.easy.ask.utils;

import java.util.UUID;

public class IdGenerator {

    public static String id() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
