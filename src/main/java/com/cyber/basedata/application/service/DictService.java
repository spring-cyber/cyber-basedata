package com.cyber.basedata.application.service;

import cn.hutool.core.lang.tree.Tree;
import com.cyber.application.service.BaseService;
import com.cyber.domain.entity.Dict;

import java.util.List;

public interface DictService extends BaseService<Dict> {


    List<Tree<String>> searchTreeDict(Dict dict);

    boolean checkDictNameUnique(Dict dict);

    boolean checkDictCodeUnique(Dict dict);

    boolean hasChildByDictId(String dictId);

    List<Dict> selectDict(Dict dict);

    List<Dict> selectDictDataByType(String dictGroupCode, String dictTypeCode);


    void loadingDictCache();

    void clearDictCache();

    void resetDictCache();
}
