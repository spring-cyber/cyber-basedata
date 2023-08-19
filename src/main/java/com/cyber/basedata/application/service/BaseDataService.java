package com.cyber.basedata.application.service;

import cn.hutool.core.lang.tree.Tree;
import com.alibaba.fastjson.JSONObject;
import com.cyber.application.service.BaseService;
import com.cyber.basedata.domain.request.TableRequest;
import com.cyber.domain.entity.BaseData;
import com.cyber.domain.entity.PagingData;
import com.cyber.domain.entity.TableColumn;

import java.util.List;

public interface BaseDataService extends BaseService<BaseData> {


    List<Tree<String>> searchBaseDataTree(BaseData basedata);

    List<BaseData> selectBaseData(BaseData basedata);

    boolean checkBaseDataNameUnique(BaseData basedata);

    boolean checkBaseDataCodeUnique(BaseData basedata);

    boolean hasChildByBaseDataId(String baseDataId);

    PagingData<JSONObject> searchTableData(TableRequest tableRequest);

    List<TableColumn> searchTableColumn(TableRequest request);

    int changeTableData(TableRequest request);
}
