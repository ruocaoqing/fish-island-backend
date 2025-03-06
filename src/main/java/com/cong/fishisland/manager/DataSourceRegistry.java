package com.cong.fishisland.manager;

import com.cong.fishisland.datasource.*;
import com.cong.fishisland.model.enums.HotDataKeyEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
    private final CodeFatherDataSource codeFatherDataSource;
    private final BiliBiliDataSource bilibiliDataSource;
    private final HuPuStreetDataSource huPuStreetDataSource;
    private final DouYinDataSource douYinDataSource;
    private final WYCloudDataSource wyCloudDataSource;
    private final CsdnDataSource csdnDataSource;


    @PostConstruct
    public void doInit() {
        typeDataSourceMap = new HashMap<String, DataSource>() {{
            put(HotDataKeyEnum.ZHI_HU.getValue(), zhiHuDataSource);
            put(HotDataKeyEnum.WEI_BO.getValue(), weiBoDataSource);
            put(HotDataKeyEnum.CODE_FATHER.getValue(), codeFatherDataSource);
            put(HotDataKeyEnum.BILI_BILI.getValue(), bilibiliDataSource);
            put(HotDataKeyEnum.HU_PU_STREET.getValue(), huPuStreetDataSource);
            put(HotDataKeyEnum.DOU_YIN.getValue(), wyCloudDataSource);
            put(HotDataKeyEnum.WY_CLOUD_MUSIC.getValue(), douYinDataSource);
            put(HotDataKeyEnum.CS_DN.getValue(), csdnDataSource);
        }};
    }


    public DataSource getDataSourceByType(String type) {
        if (typeDataSourceMap == null) {
            return null;
        }
        return typeDataSourceMap.get(type);
    }
}