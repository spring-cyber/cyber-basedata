package com.cyber.basedata.application.service.impl;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateEngine;
import com.alibaba.fastjson.JSONObject;
import com.cyber.basedata.application.service.ApprovalLogService;
import com.cyber.basedata.application.service.BaseDataService;
import com.cyber.basedata.domain.repository.BaseDataMapper;
import com.cyber.basedata.domain.repository.TableColumnMapper;
import com.cyber.basedata.domain.repository.TableFkMapper;
import com.cyber.basedata.domain.repository.TableIndexMapper;
import com.cyber.basedata.domain.request.TableRequest;
import com.cyber.domain.constant.Constants;
import com.cyber.domain.constant.HttpResultCode;
import com.cyber.domain.entity.*;
import com.cyber.domain.exception.BusinessException;
import com.cyber.infrastructure.toolkit.StringUtils;
import com.cyber.security.infrastructure.toolkit.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BaseDataServiceImpl implements BaseDataService {

    private final BaseDataMapper baseDataMapper;

    private final TableColumnMapper tableColumnMapper;

    private final TableFkMapper tableFkMapper;

    private final TableIndexMapper tableIndexMapper;

    private final ApprovalLogService approvalLogService;

    private final TemplateEngine templateEngine;


    /**
     * 主数据类型 数据表
     */
    public static final Integer BASE_DATA_TABLE = 1;

    @Override
    @Transactional
    public Integer save(BaseData baseData) {

        if (null == baseData) {
            log.warn("save baseData, but baseData is null...");
            return 0;
        }
        baseData.setId(IdUtil.simpleUUID());
        return baseDataMapper.save(baseData);
    }

    @Override
    @Transactional
    public Integer deleteById(BaseData baseData) {

        if (null == baseData) {
            log.warn("delete baseData, but baseData is null  or baseData id is null...");
            return 0;
        }

        baseData = selectOne(baseData);

        if (null == baseData) {
            log.warn("delete baseData, but baseData is null  or baseData id is null...");
            return 0;
        }
        // 数据表 不直接删除 添加到审批表
        if (BASE_DATA_TABLE.equals(baseData.getType())) {
            //根据模本生成删除数据表sql
            Template template = templateEngine.getTemplate("mysql_table_delete.ftl");
            BaseData finalBaseData = baseData;
            String tableSql = template.render(new HashMap<>() {{
                put("tableName", finalBaseData.getCode());
            }});

            return saveApprovalLog(baseData, tableSql);

        }
        return baseDataMapper.deleteById(baseData);
    }

    @Override
    @Transactional
    public Integer updateById(BaseData baseData) {

        if (null == baseData) {
            log.warn("update baseData, but baseData is null  or baseData id is null...");
            return 0;
        }

        // 数据表 不直接更新 添加到审批表
        if (BASE_DATA_TABLE.equals(baseData.getType())) {
            String tableSql;
            //查询当前表是否存在数据库中
            //表不存在则代表创建表
            if (Objects.isNull(baseDataMapper.hasTableForDatabase(baseData.getCode()))) {
                //根据模本生成创建数据表sql
                Template template = templateEngine.getTemplate("mysql_table_create.ftl");
                tableSql = template.render(new HashMap<String, Object>() {{
                    put("tableName", baseData.getCode());
                    put("comment", baseData.getDescription());
                    put("engine", baseData.getEngine());
                    put("collate", baseData.getCollation());
                    put("autoIncrementVal", baseData.getAutoIncrementVal());
                    put("columnList", baseData.getColumnList());
                    put("indexList", baseData.getIndexList());
                    put("fkList", baseData.getFkList());
                }});

            } else {
                //存在则更新表
                //根据模本生成更新数据表sql
                Template template = templateEngine.getTemplate("mysql_table_update.ftl");
                tableSql = template.render(new HashMap<String, Object>() {{
                    put("tableName", baseData.getCode());
                    put("comment", baseData.getDescription());
                    put("engine", baseData.getEngine());
                    put("collate", baseData.getCollation());
                    put("changeColumnList", baseData.getChangeColumnList());
                }});

            }

            return saveApprovalLog(baseData, tableSql);

        }
        return baseDataMapper.updateById(baseData);
    }

    /**
     * @param baseData 操作数据表表
     * @param tableSql sql脚本
     * @return
     */
    private Integer saveApprovalLog(BaseData baseData, String tableSql) {
        ApprovalLog approvalLog = new ApprovalLog();
        approvalLog.setChangeSql(tableSql);
        approvalLog.setId(IdUtil.simpleUUID());
        approvalLog.setTableCode(baseData.getCode());
        approvalLog.setInitData((JSONObject) JSONObject.toJSON(baseData));
        approvalLog.setStatus(ApprovalLogServiceImpl.APPROVAL_LOG_UNTREATED);
        approvalLog.setCreator(SecurityUtils.getUsername());
        approvalLog.setCreateTime(new Date());
        return approvalLogService.save(approvalLog);
    }

    @Override
    public BaseData selectOne(BaseData baseData) {
        if (null == baseData) {
            log.warn("select baseData one, but baseData is null ...");
            return null;
        }
        baseData = baseDataMapper.selectOne(baseData);
        //数据表 带出当前表属性和表索引还有外键
        if (BASE_DATA_TABLE.equals(baseData.getType())) {
            TableColumn tableColumn = new TableColumn();
            tableColumn.setTableCode(baseData.getCode());
            tableColumn.setOffset(0);
            tableColumn.setLimit(Integer.MAX_VALUE);

            List<TableColumn> tableColumns = tableColumnMapper.selectByIndex(tableColumn);

            TableIndex tableIndex = new TableIndex();
            tableIndex.setTableCode(baseData.getCode());
            tableIndex.setOffset(0);
            tableIndex.setLimit(Integer.MAX_VALUE);

            List<TableIndex> tableIndices = tableIndexMapper.selectByIndex(tableIndex);

            TableFk tableFk = new TableFk();
            tableFk.setTableCode(baseData.getCode());
            tableFk.setOffset(0);
            tableFk.setLimit(Integer.MAX_VALUE);

            List<TableFk> tableFks = tableFkMapper.selectByIndex(tableFk);

            baseData.setColumnList(tableColumns);
            baseData.setIndexList(tableIndices);
            baseData.setFkList(tableFks);
        }

        return baseData;
    }


    @Override
    public PagingData<BaseData> selectPage(BaseData baseData) {
        PagingData<BaseData> PagingData = new PagingData<>();

        if (null == baseData) {
            log.warn("select baseData page, but baseData is null...");
            return PagingData;
        }

        Integer queryCount = baseDataMapper.selectByIndexCount(baseData);
        PagingData.setRow(queryCount);

        if (queryCount <= 0) {
            log.info("select baseData page , but count {} == 0 ...", queryCount);
            return PagingData;
        }

        List<BaseData> baseDatas = selectByIndex(baseData);
        PagingData.setData(baseDatas);
        return PagingData;
    }

    @Override
    public List<BaseData> selectByIndex(BaseData baseData) {
        List<BaseData> baseDatas = new ArrayList<>();
        if (null == baseData) {
            log.warn("select baseData by index, but baseData is null ...");
            return baseDatas;
        }

        baseDatas = baseDataMapper.selectByIndex(baseData);

        return baseDatas;
    }

    @Override
    public List<Tree<String>> searchBaseDataTree(BaseData baseData) {

        List<BaseData> baseDataList = baseDataMapper.selectBaseData(baseData);

        List<TreeNode<String>> collect = baseDataList.stream()
                .map(baseDataMap -> {
                    TreeNode<String> treeNode = new TreeNode<>();
                    treeNode.setId(baseDataMap.getId());
                    treeNode.setParentId(baseDataMap.getParentId());
                    treeNode.setName(baseDataMap.getName());
                    treeNode.setExtra(new HashMap<String, Object>() {{
                        put("type", baseDataMap.getType());
                        put("code", baseDataMap.getCode());
                        put("engine", baseDataMap.getEngine());
                        put("collation", baseDataMap.getCollation());
                        put("description", baseDataMap.getDescription());
                    }});
                    return treeNode;
                }).collect(Collectors.toList());

        return TreeUtil.build(collect, "0");
    }

    @Override
    public List<BaseData> selectBaseData(BaseData baseData) {
        List<BaseData> baseDatas = new ArrayList<>();
        if (null == baseData) {
            log.warn("select baseData by index, but baseData is null ...");
            return baseDatas;
        }

        baseDatas = baseDataMapper.selectBaseData(baseData);

        return baseDatas;
    }

    @Override
    public boolean checkBaseDataNameUnique(BaseData basedata) {

        String dictId = StringUtils.isNull(basedata.getId()) ? "" : basedata.getId();
        BaseData info = baseDataMapper.checkBaseDataNameUnique(basedata.getName(), basedata.getType());
        if (StringUtils.isNotNull(info) && !Objects.equals(info.getId(), dictId)) {
            return Constants.NOT_UNIQUE;
        }
        return Constants.UNIQUE;
    }

    @Override
    public boolean checkBaseDataCodeUnique(BaseData baseData) {

        String dictId = StringUtils.isNull(baseData.getId()) ? "" : baseData.getId();
        BaseData info = baseDataMapper.checkBaseDataCodeUnique(baseData.getCode(), baseData.getType());
        if (StringUtils.isNotNull(info) && !Objects.equals(info.getId(), dictId)) {
            return Constants.NOT_UNIQUE;
        }
        return Constants.UNIQUE;
    }

    @Override
    public boolean hasChildByBaseDataId(String baseDataId) {

        int result = baseDataMapper.hasChildByBaseDataId(baseDataId);
        return result > 0;
    }

    @Override
    public PagingData<JSONObject> searchTableData(TableRequest tableRequest) {
        PagingData<JSONObject> PagingData = new PagingData<>();

        if (null == tableRequest) {
            log.warn("select searchTableData page, but tableRequest is null...");
            return PagingData;
        }

        BaseData temp = new BaseData();
        temp.setType(BASE_DATA_TABLE);
        temp.setCode(tableRequest.getTableCode());
        if (checkBaseDataCodeUnique(temp)) {
            throw new BusinessException("数据表 '" + tableRequest.getTableCode() + "' 不存在", HttpResultCode.PARAM_ERROR.getCode());
        }

        Integer queryCount = baseDataMapper.selectTableDataCount(tableRequest);
        PagingData.setRow(queryCount);

        if (queryCount <= 0) {
            log.info("select baseData page , but count {} == 0 ...", queryCount);
            return PagingData;
        }

        List<JSONObject> baseDatas = selectTableDataByIndex(tableRequest);
        PagingData.setData(baseDatas);
        return PagingData;
    }

    public List<JSONObject> selectTableDataByIndex(TableRequest tableRequest) {
        List<JSONObject> baseDatas = new ArrayList<>();
        if (null == tableRequest) {
            log.warn("select tableData by index, but tableRequest is null ...");
            return baseDatas;
        }

        baseDatas = baseDataMapper.selectTableDataByIndex(tableRequest);

        return baseDatas;
    }

    @Override
    public List<TableColumn> searchTableColumn(TableRequest tableRequest) {
        if (null == tableRequest) {
            log.warn("select search table column , but tableRequest is null ...");
            return null;
        }
        TableColumn tableColumn = new TableColumn();
        tableColumn.setTableCode(tableRequest.getTableCode());
        tableColumn.setOffset(0);
        tableColumn.setLimit(Integer.MAX_VALUE);

        return tableColumnMapper.selectByIndex(tableColumn);
    }
}
