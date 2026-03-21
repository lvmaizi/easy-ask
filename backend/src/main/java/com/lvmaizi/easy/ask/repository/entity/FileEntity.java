package com.lvmaizi.easy.ask.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("t_file")
public class FileEntity extends BaseEntity {

    @TableId
    private String path;

    private String name;

    private Date lastModified;

    private Long length;

    private String summary;

}
