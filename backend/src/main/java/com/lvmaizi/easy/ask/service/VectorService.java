package com.lvmaizi.easy.ask.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface VectorService {

    @GetMapping("/v1/vector/list")
    String list();

}
