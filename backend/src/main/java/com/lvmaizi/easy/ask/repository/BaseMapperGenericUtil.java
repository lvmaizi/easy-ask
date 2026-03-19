package com.lvmaizi.easy.ask.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 获取 MyBatis-Plus BaseMapper 泛型实体类型的工具类
 */
public class BaseMapperGenericUtil {

    /**
     * 获取 BaseMapper 声明的泛型实体类型
     * @param mapperClass 自定义的 Mapper 接口（如 UserMapper.class）
     * @return 泛型实体类的 Class 对象（如 User.class）
     * @throws IllegalArgumentException 若未找到 BaseMapper 泛型信息则抛出异常
     */
    public static <T> Class<T> getEntityClass(Class<?> mapperClass) {
        // 1. 获取当前接口的所有父接口（包括泛型信息）
        Type[] genericInterfaces = mapperClass.getGenericInterfaces();

        // 2. 遍历父接口，找到 BaseMapper 接口
        for (Type genericInterface : genericInterfaces) {
            // 判断是否为参数化类型（即带泛型的 BaseMapper<T>）
            if (genericInterface instanceof ParameterizedType parameterizedType) {
                Type rawType = parameterizedType.getRawType();
                // 确认是 BaseMapper 接口
                if (rawType instanceof Class && BaseMapper.class.isAssignableFrom((Class<?>) rawType)) {
                    // 3. 获取 BaseMapper 的泛型参数（T）
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class) {
                        // 4. 转换为实体类 Class 对象并返回
                        return (Class<T>) actualTypeArguments[0];
                    }
                }
            }
        }

        // 处理多层继承的情况（比如 Mapper 继承了自定义的 BaseXxxMapper，再继承 BaseMapper）
        for (Class<?> superInterface : mapperClass.getInterfaces()) {
            if (!superInterface.equals(BaseMapper.class)) {
                Class<T> result = getEntityClass(superInterface);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

}

