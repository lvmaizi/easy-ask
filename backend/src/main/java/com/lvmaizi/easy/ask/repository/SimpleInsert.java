package com.lvmaizi.easy.ask.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvmaizi.easy.ask.repository.entity.BaseEntity;
import com.lvmaizi.easy.ask.utils.Converts;
import lombok.Builder;


@Builder
public class SimpleInsert<T extends BaseEntity> {

    public static <R extends BaseEntity> void save(R baseEntity) {
        @SuppressWarnings("unchecked")
        BaseMapper<R> mapper = (BaseMapper<R>) MybatisMappers.getMapper(baseEntity.getClass());
        mapper.insert(baseEntity);
    }

    public static <R extends BaseEntity> void save(Object target, Class<R> rClass) {
        R copy = Converts.copy(target, rClass);
        assert copy != null;
        save(copy);
    }
}
