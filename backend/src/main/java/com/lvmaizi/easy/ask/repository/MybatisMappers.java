package com.lvmaizi.easy.ask.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvmaizi.easy.ask.repository.entity.BaseEntity;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MybatisMappers implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static final Map<Class<?>, BaseMapper<?>> baseMapperMap = new HashMap<>();

    @PostConstruct
    public void init() {
        var beans = applicationContext.getBeansOfType(BaseMapper.class);

        for (BaseMapper<?> baseMapper : beans.values()) {
            Class<?> c = BaseMapperGenericUtil.getEntityClass(baseMapper.getClass());
            baseMapperMap.put(c, baseMapper);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MybatisMappers.applicationContext = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseEntity, R extends BaseMapper<T>> R getMapper(Class<T> clazz) {
        return (R) baseMapperMap.get(clazz);
    }
}
