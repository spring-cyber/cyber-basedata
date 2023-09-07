package com.cyber.basedata.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
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
import org.jetbrains.annotations.NotNull;
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
        // 数据表如果存在数据库提示用户
        if (BASE_DATA_TABLE.equals(baseData.getType()) && !Objects.isNull(baseDataMapper.hasTableForDatabase(baseData.getCode()))) {
            throw new BusinessException("数据表已存在", HttpResultCode.PARAM_ERROR.getCode());
        }
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
        // 数据表如果存在数据库 不直接删除 添加到审批表
        if (BASE_DATA_TABLE.equals(baseData.getType()) && !Objects.isNull(baseDataMapper.hasTableForDatabase(baseData.getCode()))) {
            //根据模本生成删除数据表sql
            Template template = templateEngine.getTemplate("mysql_table_delete.ftl");
            BaseData finalBaseData = baseData;
            String tableSql = template.render(new HashMap<String, Object>() {{
                put("tableName", finalBaseData.getCode());
            }});
            baseData.setColumnList(new ArrayList<>());
            baseData.setIndexList(new ArrayList<>());
            baseData.setFkList(new ArrayList<>());

            saveApprovalLog(baseData.getCode(), baseData, tableSql);
            return baseDataMapper.deleteById(baseData);
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

        // 数据表
        if (BASE_DATA_TABLE.equals(baseData.getType())) {
            if (CollectionUtil.isNotEmpty(baseData.getIndexList())) {
                tableIndexMapper.deleteByTableCode(baseData.getCode());
                baseData.getIndexList().forEach(tableColumn -> tableColumn.setId(IdUtil.simpleUUID()));
                tableIndexMapper.saveBatch(baseData.getIndexList());
            }
            if (CollectionUtil.isNotEmpty(baseData.getFkList())) {
                tableFkMapper.deleteByTableCode(baseData.getCode());
                baseData.getFkList().forEach(tableColumn -> tableColumn.setId(IdUtil.simpleUUID()));
                tableFkMapper.saveBatch(baseData.getFkList());
            }
            if (CollectionUtil.isNotEmpty(baseData.getColumnList())) {
                tableColumnMapper.deleteByTableCode(baseData.getCode());
                baseData.getColumnList().forEach(tableColumn -> tableColumn.setId(IdUtil.simpleUUID()));
                tableColumnMapper.saveBatch(baseData.getColumnList());
            }
            baseDataMapper.updateById(baseData);
            return generateSqlByTemplate(baseData);

        }
        return baseDataMapper.updateById(baseData);
    }

    /**
     * 根据模板生成Sql 并添加审批记录
     *
     * @param baseData
     * @return
     */

    private Integer generateSqlByTemplate(BaseData baseData) {
        String tableSql;
        //查询当前表是否创建
        //列不存在则代表创建表
        TableRequest tableRequest = new TableRequest();
        tableRequest.setTableCode(baseData.getCode());
        List<TableColumn> oldColumns = searchTableColumn(tableRequest);
        if (CollUtil.isEmpty(oldColumns)) {
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

            TableIndex tableIndex = new TableIndex();
            tableIndex.setTableCode(baseData.getCode());
            List<TableIndex> oldIndices = tableIndexMapper.selectList(tableIndex);

            TableFk tableFk = new TableFk();
            tableFk.setTableCode(baseData.getCode());
            List<TableFk> oldFks = tableFkMapper.selectList(tableFk);

            //存在则更新表
            List<TableColumn> changeColumnList = handleColumns(baseData, oldColumns);
            List<TableFk> changeFkList = handleFks(baseData, oldFks);
            List<TableIndex> changeIndexList = handleIndexes(baseData, oldIndices);

            BaseData temp = new BaseData();
            temp.setId(baseData.getId());
            temp = selectOne(temp);

            //检测是否需要更新表结构信息 不需要直接更新表格基础信息 不需要生成sql
            if (!baseData.checkTableChange(temp)
                    && CollUtil.isEmpty(changeColumnList)
                    && CollUtil.isEmpty(changeFkList)
                    && CollUtil.isEmpty(changeIndexList)) {

                //可能有列只改变了排序
                if (CollectionUtil.isNotEmpty(baseData.getColumnList())) {
                    tableColumnMapper.deleteByTableCode(baseData.getCode());
                    tableColumnMapper.saveBatch(baseData.getColumnList());
                }
                return baseDataMapper.updateById(baseData);
            }


            //根据模本生成更新数据表sql
            Template template = templateEngine.getTemplate("mysql_table_update.ftl");
            tableSql = template.render(new HashMap<String, Object>() {{
                put("tableName", baseData.getCode());
                put("comment", baseData.getDescription());
                put("engine", baseData.getEngine());
                put("collate", baseData.getCollation());
                put("changeColumnList", changeColumnList);
                put("changeFkList", changeFkList);
                put("changeIndexList", changeIndexList);
            }});

        }

        return saveApprovalLog(baseData.getCode(), baseData, tableSql);
    }

    @NotNull
    private List<TableIndex> handleIndexes(BaseData baseData, List<TableIndex> oldIndices) {
        List<TableIndex> indexList = baseData.getIndexList();
        if (CollectionUtil.isEmpty(indexList)) {
            return new ArrayList<>();
        }
        //新增的索引
        List<TableIndex> addIndex = indexList.stream()
                .filter(index -> {
                    if (index.getId() == null) {
                        index.setWay("add");
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());
        //获取需要删除的索引
        List<TableIndex> dropIndex = indexList.stream()
                .filter(oldIndex -> {
                            if (indexList.stream().noneMatch(newColumn -> newColumn.getId() != null && newColumn.getId().equals(oldIndex.getId()))) {
                                oldIndex.setWay("drop");
                                return true;
                            }
                            return false;
                        }
                )
                .collect(Collectors.toList());
        //变更的索引
        indexList.forEach(newIndex ->
                oldIndices.forEach(oldIndex -> {
                            if (oldIndex.getId().equals(newIndex.getId()) && !newIndex.equals(oldIndex)) {
                                TableIndex tableIndex = new TableIndex();
                                tableIndex.setWay("drop");
                                tableIndex.setName(oldIndex.getName());
                                dropIndex.add(tableIndex);

                                newIndex.setWay("add");
                                addIndex.add(newIndex);
                            }
                        }
                )
        );

        dropIndex.addAll(addIndex);
        return dropIndex;
    }

    @NotNull
    private List<TableFk> handleFks(BaseData baseData, List<TableFk> oldFks) {
        List<TableFk> fkList = baseData.getFkList();
        if (CollectionUtil.isEmpty(fkList)) {
            return new ArrayList<>();
        }
        //新增的外键
        List<TableFk> addFks = fkList.stream()
                .filter(fk -> {
                    if (fk.getId() == null) {
                        fk.setWay("add");
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());
        //获取需要删除的外键
        List<TableFk> dropFks = oldFks.stream()
                .filter(oldFk -> {
                            if (fkList.stream().noneMatch(newColumn -> newColumn.getId() != null && newColumn.getId().equals(oldFk.getId()))) {
                                oldFk.setWay("drop");
                                return true;
                            }
                            return false;
                        }
                )
                .collect(Collectors.toList());
        //变更的外键
        fkList.forEach(newFk ->
                oldFks.forEach(oldFk -> {
                            if (oldFk.getId().equals(newFk.getId()) && !newFk.equals(oldFk)) {
                                TableFk tableFk = new TableFk();
                                tableFk.setWay("drop");
                                tableFk.setName(oldFk.getName());
                                dropFks.add(tableFk);

                                newFk.setWay("add");
                                addFks.add(newFk);
                            }
                        }
                )
        );

        dropFks.addAll(addFks);
        return dropFks;
    }

    @NotNull
    private static List<TableColumn> handleColumns(BaseData baseData, List<TableColumn> oldColumns) {
        List<TableColumn> columnList = baseData.getColumnList();

        if (CollectionUtil.isEmpty(columnList)) {
            return new ArrayList<>();
        }
        //新增的列
        List<TableColumn> addColumn = columnList.stream()
                .filter(column -> {
                    if (column.getId() == null) {
                        column.setWay("add");
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());

        //获取需要删除的列
        List<TableColumn> dropColumn = oldColumns.stream()
                .filter(oldColumn -> {
                            if (columnList.stream().noneMatch(newColumn -> newColumn.getId() != null && newColumn.getId().equals(oldColumn.getId()))) {
                                oldColumn.setWay("drop");
                                return true;
                            }
                            return false;
                        }

                )
                .collect(Collectors.toList());

        //变更的列
        List<TableColumn> changeColumn = columnList.stream()
                .filter(newColumn ->
                        oldColumns.stream()
                                .anyMatch(oldColumn -> {
                                            if (oldColumn.getId().equals(newColumn.getId()) && !newColumn.equals(oldColumn)) {
                                                newColumn.setWay("change");
                                                newColumn.setCode(newColumn.getCode());
                                                newColumn.setOldCode(oldColumn.getCode());
                                                return true;
                                            } else {
                                                return false;
                                            }
                                        }

                                )
                )
                .collect(Collectors.toList());

        dropColumn.addAll(addColumn);
        dropColumn.addAll(changeColumn);
        return dropColumn;
    }

    /**
     * @param baseData 操作数据表表
     * @param tableSql sql脚本
     * @return
     */
    private Integer saveApprovalLog(String tableCode, BaseData baseData, String tableSql) {
        ApprovalLog approvalLog = new ApprovalLog();
        approvalLog.setChangeSql(tableSql);
        approvalLog.setId(IdUtil.simpleUUID());
        approvalLog.setTableCode(tableCode);
        approvalLog.setInitData((JSONObject) JSONObject.toJSON(baseData));
        approvalLog.setStatus(ApprovalLogServiceImpl.APPROVAL_LOG_UNTREATED);
        approvalLog.setCreator(SecurityUtils.getUsername());
        approvalLog.setCreateTime(new Date());
        approvalLogService.executeSql(approvalLog, tableSql.split(";"));
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
            tableColumn.setSortBy("order_num");
            tableColumn.setSortType("asc");

            List<TableColumn> tableColumns = tableColumnMapper.selectList(tableColumn);

            TableIndex tableIndex = new TableIndex();
            tableIndex.setTableCode(baseData.getCode());

            List<TableIndex> tableIndices = tableIndexMapper.selectList(tableIndex);

            TableFk tableFk = new TableFk();
            tableFk.setTableCode(baseData.getCode());

            List<TableFk> tableFks = tableFkMapper.selectList(tableFk);

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
        tableColumn.setSortType(tableRequest.getSortType());
        tableColumn.setSortBy(tableRequest.getSortBy());
        tableColumn.setTableCode(tableRequest.getTableCode());

        return tableColumnMapper.selectList(tableColumn);
    }

    @Override
    @Transactional
    public int changeTableData(TableRequest request) {
        //根据模本生成删除数据表sql
        Template template = templateEngine.getTemplate("mysql_column_change.ftl");
        String tableSql = template.render(new HashMap<String, Object>() {{
            put("tableName", request.getTableCode());
            put("columnNames", request.getColumnNames());
            put("addDataList", request.getAddDataList());
            put("removeIdList", request.getRemoveIdList());
        }});

        BaseData temp = new BaseData();
        temp.setCode(request.getTableCode());
        temp = selectOne(temp);

        return saveApprovalLog(request.getTableCode(), temp, tableSql);
    }

    @Override
    public List<Object> selectTableColumnData(String tableCode, String columnCode) {
        BaseData temp = new BaseData();
        temp.setType(BASE_DATA_TABLE);
        temp.setCode(tableCode);
        if (checkBaseDataCodeUnique(temp)) {
            throw new BusinessException("数据表 '" + tableCode + "' 不存在", HttpResultCode.PARAM_ERROR.getCode());
        }
        return baseDataMapper.selectTableColumnData(tableCode, columnCode);
    }
}
