package com.cyber.basedata.application.service;

import cn.hutool.core.lang.tree.Tree;
import com.cyber.application.service.BaseService;
import com.cyber.domain.entity.BaseData;

import java.util.List;

public interface BaseDataService extends BaseService<BaseData> {


    List<Tree<String>> searchBaseDataTree(BaseData basedata);

    List<BaseData> selectBaseData(BaseData basedata);

    boolean checkBaseDataNameUnique(BaseData basedata);

    boolean checkBaseDataCodeUnique(BaseData basedata);

    boolean hasChildByBaseDataId(String baseDataId);
}
