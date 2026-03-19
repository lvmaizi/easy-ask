package com.lvmaizi.easy.ask.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvmaizi.easy.ask.repository.entity.BaseEntity;

import java.util.List;
import java.util.Map;

public class SimpleQuery {

    public static <R extends BaseEntity> R getOne(Map<String, Object> params, Class<R> rClass) {
        BaseMapper<R> mapper = (BaseMapper<R>) MybatisMappers.getMapper(rClass);

        QueryWrapper<R> queryWrapper = new QueryWrapper<R>();
        queryWrapper.allEq(params);

        return mapper.selectOne(queryWrapper, false);
    }

    public static <R extends BaseEntity> List<R> list(Map<String, Object> params, Class<R> rClass) {
        BaseMapper<R> mapper = MybatisMappers.getMapper(rClass);

        QueryWrapper<R> queryWrapper = new QueryWrapper<R>();
        queryWrapper.allEq(params);

        return mapper.selectList(queryWrapper);
    }
}
