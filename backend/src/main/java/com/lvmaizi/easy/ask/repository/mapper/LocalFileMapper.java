package com.lvmaizi.easy.ask.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lvmaizi.easy.ask.repository.entity.FileEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LocalFileMapper extends BaseMapper<FileEntity> {
}
