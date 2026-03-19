package com.lvmaizi.easy.ask.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"com.lvmaizi.easy.ask.repository.mapper"})
public class MapperConfig {
}
