package com.cyber.basedata.application.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.IdUtil;
import com.cyber.basedata.application.service.DictService;
import com.cyber.basedata.domain.repository.DictMapper;
import com.cyber.domain.constant.CacheConstants;
import com.cyber.domain.constant.Constants;
import com.cyber.domain.entity.Dict;
import com.cyber.domain.entity.PagingData;
import com.cyber.infrastructure.toolkit.RedisService;
import com.cyber.infrastructure.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DictServiceImpl implements DictService {

    private final DictMapper dictMapper;

    private final RedisService redisService;

    /**
     * 字典类型 字典组
     */
    public static final Integer DICT_GROUP = 0;

    /**
     * 字典类型 字典类型
     */
    public static final Integer DICT_TYPE = 1;

    /**
     * 字典类型 字典值
     */
    public static final Integer DICT_DATA = 2;

    /**
     * 项目启动时，初始化字典到缓存
     */
    @PostConstruct
    public void init() {
        loadingDictCache();
    }

    @Override
    @Transactional
    public Integer save(Dict dict) {

        if (null == dict) {
            log.warn("save dict, but dict is null...");
            return 0;
        }

        dict.setId(IdUtil.simpleUUID());
        Integer result = dictMapper.save(dict);
        if (result > 0) {
            saveDictCache(dict);
        }

        return result;
    }

    private void saveDictCache(Dict dict) {
        if (DICT_DATA.equals(dict.getType())) {
            Dict dictQ = new Dict();
            dictQ.setId(dict.getParentId());
            dictQ.setType(DICT_TYPE);
            dict = dictMapper.selectDictByTypeId(dictQ);
            List<Dict> dictList = dictMapper.selectDictDataByType(dict.getDictGroupCode(), dict.getDictTypeCode());
            redisService.setCacheObject(getCacheKey(dict.getDictGroupCode(), dict.getDictTypeCode()), dictList);
        }
    }


    @Override
    @Transactional
    public Integer deleteById(Dict dict) {

        if (null == dict) {
            log.warn("delete dict, but dict is null  or dict id is null...");
            return 0;
        }
        Dict temp = new Dict();
        temp.setId(dict.getId());
        temp = dictMapper.selectOne(temp);
        if (StringUtils.isNull(temp)) {
            return 0;
        }

        Integer result = dictMapper.deleteById(dict);

        if (result > 0) {
            saveDictCache(temp);
        }
        return result;
    }

    @Override
    @Transactional
    public Integer updateById(Dict dict) {

        if (null == dict) {
            log.warn("update dict, but dict is null  or dict id is null...");
            return 0;
        }

        Integer result = dictMapper.updateById(dict);
        if (result > 0) {
            saveDictCache(dict);
        }
        return result;
    }

    @Override
    public Dict selectOne(Dict dict) {
        if (null == dict) {
            log.warn("select dict one, but dict is null ...");
            return null;
        }
        dict = dictMapper.selectOne(dict);
        return dict;
    }


    @Override
    public PagingData<Dict> selectPage(Dict dict) {
        PagingData<Dict> PagingData = new PagingData<>();

        if (null == dict) {
            log.warn("select dict page, but dict is null...");
            return PagingData;
        }

        Integer queryCount = dictMapper.selectByIndexCount(dict);
        PagingData.setRow(queryCount);

        if (queryCount <= 0) {
            log.info("select dict page , but count {} == 0 ...", queryCount);
            return PagingData;
        }

        List<Dict> dicts = selectByIndex(dict);
        PagingData.setData(dicts);
        return PagingData;
    }

    @Override
    public List<Dict> selectByIndex(Dict dict) {
        List<Dict> dicts = new ArrayList<>();
        if (null == dict) {
            log.warn("select dict by index, but dict is null ...");
            return dicts;
        }

        dicts = dictMapper.selectByIndex(dict);

        return dicts;
    }

    @Override
    public List<Tree<String>> searchTreeDict(Dict dict) {
        // 查询 类型为组 和字典类型
        dict.setTypes(Arrays.asList(0, 1));
        List<Dict> dictList = dictMapper.selectList(dict);

        List<TreeNode<String>> collect = dictList.stream()
                .sorted(Comparator.comparingInt(Dict::getOrderNum)).map(dictMap -> {
                    TreeNode<String> treeNode = new TreeNode<>();
                    treeNode.setId(dictMap.getId());
                    treeNode.setParentId(dictMap.getParentId());
                    treeNode.setName(dictMap.getName());
                    treeNode.setWeight(dictMap.getOrderNum());
                    treeNode.setExtra(new HashMap<String, Object>() {{
                        put("type", dictMap.getType());
                        put("code", dictMap.getCode());
                        put("color", dictMap.getColor());
                        put("icon", dictMap.getIcon());
                        put("description", dictMap.getDescription());
                    }});
                    return treeNode;
                }).collect(Collectors.toList());

        return TreeUtil.build(collect, "0");
    }

    @Override
    public boolean checkDictNameUnique(Dict dict) {
        String dictId = StringUtils.isNull(dict.getId()) ? "" : dict.getId();
        Dict info = dictMapper.checkDictNameUnique(dict.getName(), dict.getType(), dict.getParentId());
        if (StringUtils.isNotNull(info) && !Objects.equals(info.getId(), dictId)) {
            return Constants.NOT_UNIQUE;
        }
        return Constants.UNIQUE;
    }

    @Override
    public boolean checkDictCodeUnique(Dict dict) {
        String dictId = StringUtils.isNull(dict.getId()) ? "" : dict.getId();
        Dict info = dictMapper.checkDictCodeUnique(dict.getCode(), dict.getType(), dict.getParentId());
        if (StringUtils.isNotNull(info) && !Objects.equals(info.getId(), dictId)) {
            return Constants.NOT_UNIQUE;
        }
        return Constants.UNIQUE;
    }

    @Override
    public boolean hasChildByDictId(String dictId) {
        int result = dictMapper.hasChildByDictId(dictId);
        return result > 0;
    }

    @Override
    public List<Dict> selectDict(Dict dict) {
        return dictMapper.selectList(dict);
    }

    @Override
    public List<Dict> selectDictDataByType(String dictGroupCode, String dictTypeCode) {
        String cacheKey = getCacheKey(dictGroupCode, dictTypeCode);
        List<Dict> dictDatas = redisService.getCacheObject(cacheKey);
        if (!CollectionUtils.isEmpty(dictDatas)) {
            dictDatas = ListUtil.sortByProperty(dictDatas, "orderNum");
            return dictDatas;
        }
        dictDatas = dictMapper.selectDictDataByType(dictGroupCode, dictTypeCode);
        if (!CollectionUtils.isEmpty(dictDatas)) {
            dictDatas = ListUtil.sortByProperty(dictDatas, "orderNum");
            redisService.setCacheObject(cacheKey, dictDatas);
            return dictDatas;
        }
        return null;
    }

    @Override
    public void loadingDictCache() {
        Dict dictQ = new Dict();
        dictQ.setType(2);
        dictQ.setStatus(0);
        Map<String, List<Dict>> dictDataMap = dictMapper.selectDictDataList(dictQ).stream().collect(Collectors.groupingBy(dict -> getCacheKey(dict.getDictGroupCode(), dict.getDictTypeCode())));
        for (Map.Entry<String, List<Dict>> entry : dictDataMap.entrySet()) {
            redisService.setCacheObject(entry.getKey(), entry.getValue().stream().sorted(Comparator.comparing(Dict::getOrderNum)).collect(Collectors.toList()));
        }
    }

    @Override
    public void clearDictCache() {
        Collection<String> keys = redisService.keys(CacheConstants.SYS_DICT_KEY + "*");
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }

        redisService.deleteObject(keys);
    }

    @Override
    public void resetDictCache() {
        clearDictCache();
        loadingDictCache();
    }

    /**
     * 设置cache key
     *
     * @return 缓存键key
     */
    public static String getCacheKey(String dictGroupCode, String dictTypeCode) {
        return CacheConstants.SYS_DICT_KEY + dictGroupCode + ":" + dictTypeCode;
    }
}
