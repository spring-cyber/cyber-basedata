package com.cyber.basedata.application.service;

import cn.hutool.core.lang.tree.Tree;
import com.cyber.application.service.BaseService;
import com.cyber.domain.entity.Area;

import java.util.List;

public interface AreaService extends BaseService<Area> {


    List<Area> selectArea(Area area);

    List<Tree<String>> selectTree(Area area);

    boolean hasChildByAreaId(String id);
}
