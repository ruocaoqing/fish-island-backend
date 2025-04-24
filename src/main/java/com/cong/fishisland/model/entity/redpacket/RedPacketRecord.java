package com.cong.fishisland.model.entity.redpacket;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 红包记录实体类
 * 注意：这个类只用于在内存中表示红包记录，不存储到数据库
 * @author cong
 */
@Data
public class RedPacketRecord implements Serializable {
    
    /**
     * 记录ID
     */
    private String id;
    
    /**
     * 红包ID
     */
    private String redPacketId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 抢到的金额
     */
    private Integer amount;
    
    /**
     * 抢红包时间
     */
    private Date grabTime;
    
    private static final long serialVersionUID = 1L;
} 