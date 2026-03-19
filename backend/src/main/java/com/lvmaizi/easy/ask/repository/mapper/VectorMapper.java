package com.lvmaizi.easy.ask.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvmaizi.easy.ask.repository.entity.VectorEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VectorMapper extends BaseMapper<VectorEntity> {

    @Select("SELECT id, content FROM t_embeddings WHERE embedding MATCH #{query} ORDER BY distance LIMIT #{topN}")
    List<VectorEntity> search(@Param("query") byte[] query, @Param("topN") int topN);

}
