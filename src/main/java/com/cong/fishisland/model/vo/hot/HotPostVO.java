package com.cong.fishisland.model.vo.hot;

import cn.hutool.json.JSONUtil;
import com.cong.fishisland.model.entity.hot.HotPost;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 热榜视图
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
@Data
public class HotPostVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 排行榜名称
     */
    private String name;

    /**
     *  热点类型
     */
    private String type;

    /**
     *  热点类型名称
     */
    private String typeName;

    /**
     * 图标地址
     */
    private String iconUrl;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 数据
     */
    private List<HotPostDataVO> data;

    /**
     * 对象转包装类
     *
     * @param hotPost 热门帖子
     * @return {@link HotPostVO }
     */
    public static HotPostVO objToVo(HotPost hotPost) {
        if (hotPost == null) {
            return null;
        }
        HotPostVO hotPostVO = new HotPostVO();
        BeanUtils.copyProperties(hotPost, hotPostVO);
        hotPostVO.setData(JSONUtil.toList(hotPost.getHostJson(), HotPostDataVO.class));
        return hotPostVO;
    }

}
