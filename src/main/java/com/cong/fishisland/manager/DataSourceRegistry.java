package com.cong.fishisland.manager;

import com.cong.fishisland.datasource.DataSource;
import com.cong.fishisland.datasource.WeiBoDataSource;
import com.cong.fishisland.datasource.ZhiHuDataSource;
import com.cong.fishisland.model.enums.HotDataKeyEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源注册表
 *
 * @author 86188
 * @date 2023/03/20
 */
@Component
@RequiredArgsConstructor
public class DataSourceRegistry {


    private Map<String, DataSource> typeDataSourceMap;

    private final ZhiHuDataSource zhiHuDataSource;
    private final WeiBoDataSource weiBoDataSource;


    @PostConstruct
    public void doInit() {
        typeDataSourceMap = new HashMap() {{
            put(HotDataKeyEnum.ZHI_HU.getValue(), zhiHuDataSource);
            put(HotDataKeyEnum.WEI_BO.getValue(), weiBoDataSource);
        }};
    }


    public DataSource getDataSourceByType(String type) {
        if (typeDataSourceMap==null){
            return null;
        }
        return typeDataSourceMap.get(type);
    }
}