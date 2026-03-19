package com.lvmaizi.easy.ask.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("t_embeddings")
public class VectorEntity extends BaseEntity {

    private Long id;

    private String query;

    private String content;

    private int priority;

    private byte[] embedding;

}
