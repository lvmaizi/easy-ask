package com.lvmaizi.easy.ask.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface FileScanService {

    void scan();

    @GetMapping("/v1/file/list")
    String list();

}
