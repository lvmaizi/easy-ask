package com.lvmaizi.easy.ask.repository;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvmaizi.easy.ask.repository.entity.BaseEntity;

import java.util.Map;

public class SimpleUpdate {

    public static <R extends BaseEntity> void update(Map<String, Object> params, R entity) {
        @SuppressWarnings("unchecked")
        BaseMapper<R> mapper = (BaseMapper<R>) MybatisMappers.getMapper(entity.getClass());

        UpdateWrapper<R> updateWrapper = new UpdateWrapper<>();
        updateWrapper.allEq(params);
        mapper.update(entity, updateWrapper);
    }
}
