package	com.cyber.basedata.application.service.impl;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import com.cyber.basedata.application.service.AreaService;
import com.cyber.basedata.domain.repository.AreaMapper;
import com.cyber.domain.entity.Area;
import com.cyber.domain.entity.PagingData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AreaServiceImpl implements AreaService {

    private final AreaMapper areaMapper;

    @Override
    @Transactional
    public Integer save(Area area) {

        if( null == area ) {
            log.warn("save area, but area is null...");
            return 0;
        }

        return areaMapper.save( area );
    }

    @Override
    @Transactional
    public Integer deleteById(Area area) {

        if( null == area ) {
            log.warn("delete area, but area is null  or area id is null...");
            return 0;
        }

        return areaMapper.deleteById( area );
    }

    @Override
    @Transactional
    public Integer updateById(Area area) {

        if( null == area ) {
            log.warn("update area, but area is null  or area id is null...");
            return 0;
        }

        return areaMapper.updateById( area );
    }

    @Override
    public Area selectOne(Area area) {
        if( null == area ) {
            log.warn("select area one, but area is null ...");
            return null;
        }
        area = areaMapper.selectOne( area );
        return area;
    }


    @Override
    public PagingData<Area> selectPage(Area area) {
        PagingData<Area> PagingData = new PagingData<>();

        if( null == area ) {
            log.warn("select area page, but area is null...");
            return PagingData;
        }

        Integer queryCount = areaMapper.selectByIndexCount( area );
        PagingData.setRow( queryCount );

        if( queryCount <= 0 ) {
            log.info("select area page , but count {} == 0 ...",queryCount);
            return PagingData;
        }

        List<Area> areas =  selectByIndex( area );
        PagingData.setData( areas );
        return PagingData;
    }

    @Override
    public List<Area> selectByIndex(Area area) {
        List<Area> areas = new ArrayList<>();
        if( null == area ) {
            log.warn("select area by index, but area is null ...");
            return areas;
        }

        areas = areaMapper.selectByIndex( area );

        return areas;
    }

    @Override
    public List<Area> selectArea(Area area) {
        List<Area> areas = new ArrayList<>();
        if( null == area ) {
            log.warn("select area by select, but area is null ...");
            return areas;
        }

        areas = areaMapper.selectArea( area );

        return areas;
    }

    @Override
    public List<Tree<String>> selectTree(Area area) {
        List<Area> areaList = selectArea(area);

        List<TreeNode<String>> collect = areaList.stream()
                .map(areaMap -> {
                    TreeNode<String> treeNode = new TreeNode<>();
                    treeNode.setId(areaMap.getId());
                    treeNode.setParentId(areaMap.getParentId());
                    treeNode.setName(areaMap.getName());
                    treeNode.setWeight(areaMap.getId());
                    return treeNode;
                }).collect(Collectors.toList());

        return TreeUtil.build(collect, "0");
    }

    @Override
    public boolean hasChildByAreaId(String areaId) {

        int result = areaMapper.hasChildByAreaId(areaId);
        return result > 0;
    }
}
