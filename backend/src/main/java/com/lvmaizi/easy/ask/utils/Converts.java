package com.lvmaizi.easy.ask.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Converts {

    public static <T> T copy(Object source, Class<T> target) {
        try {
            if (Objects.isNull(source)) {
                return null;
            }
            T t = target.newInstance();
            BeanUtils.copyProperties(source, t);
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> copyList(List<?> source, Class<T> target) {
        List<T> result = new ArrayList<>();
        for (Object object : source) {
            result.add(copy(object, target));
        }
        return result;
    }

}
