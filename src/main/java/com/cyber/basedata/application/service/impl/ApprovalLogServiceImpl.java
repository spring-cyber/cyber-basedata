package com.cyber.basedata.application.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.cyber.basedata.application.service.ApprovalLogService;
import com.cyber.basedata.domain.repository.*;
import com.cyber.domain.constant.HttpResultCode;
import com.cyber.domain.entity.ApprovalLog;
import com.cyber.domain.entity.BaseData;
import com.cyber.domain.entity.PagingData;
import com.cyber.domain.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApprovalLogServiceImpl implements ApprovalLogService {

    private final ApprovalLogMapper approvalLogMapper;

    private final TableColumnMapper tableColumnMapper;

    private final TableFkMapper tableFkMapper;

    private final TableIndexMapper tableIndexMapper;

    private final BaseDataMapper baseDataMapper;

    /**
     * 审批状态 未处理
     */
    public static final Integer APPROVAL_LOG_UNTREATED = 0;

    /**
     * 审批状态 通过
     */
    public static final Integer APPROVAL_LOG_PASS = 1;

    /**
     * 审批状态 驳回
     */
    public static final Integer APPROVAL_LOG_REJECT = 2;

    @Override
    @Transactional
    public Integer save(ApprovalLog approvalLog) {

        if (null == approvalLog) {
            log.warn("save approvalLog, but approvalLog is null...");
            return 0;
        }

        if (approvalLogMapper.hasUntreatedByTableCode(approvalLog.getTableCode()) > 0) {
            throw new BusinessException("当前数据表已冻结：存在未处理变更记录", HttpResultCode.RECORD_EXIST.getCode());
        }

        return approvalLogMapper.save(approvalLog);
    }

    @Override
    @Transactional
    public Integer deleteById(ApprovalLog approvalLog) {

        if (null == approvalLog) {
            log.warn("delete approvalLog, but approvalLog is null  or approvalLog id is null...");
            return 0;
        }

        return approvalLogMapper.deleteById(approvalLog);
    }

    @Override
    @Transactional
    public Integer updateById(ApprovalLog approvalLog) {

        if (null == approvalLog) {
            log.warn("update approvalLog, but approvalLog is null  or approvalLog id is null...");
            return 0;
        }
        //通过后执行变更
        if (APPROVAL_LOG_PASS.equals(approvalLog.getStatus())) {
            ApprovalLog temp = new ApprovalLog();
            temp.setId(approvalLog.getId());
            temp = selectOne(temp);

            tableColumnMapper.deleteByTableCode(temp.getTableCode());
            tableIndexMapper.deleteByTableCode(temp.getTableCode());
            tableFkMapper.deleteByTableCode(temp.getTableCode());

            BaseData baseData = JSONObject.toJavaObject(temp.getInitData(), BaseData.class);
            baseData.getColumnList().forEach(tableColumn -> tableColumn.setId(IdUtil.simpleUUID()));
            baseData.getIndexList().forEach(tableColumn -> tableColumn.setId(IdUtil.simpleUUID()));
            baseData.getFkList().forEach(tableColumn -> tableColumn.setId(IdUtil.simpleUUID()));

            tableColumnMapper.saveBatch(baseData.getColumnList());
            tableIndexMapper.saveBatch(baseData.getIndexList());
            tableFkMapper.saveBatch(baseData.getFkList());

            List<String> sqlList = Arrays.stream(temp.getChangeSql().split(";")).filter(StrUtil::isNotBlank).collect(Collectors.toList());
            for (String sql : sqlList) {
                baseDataMapper.executeSql(sql);
            }

        }

        return approvalLogMapper.updateById(approvalLog);
    }

    @Override
    public ApprovalLog selectOne(ApprovalLog approvalLog) {
        if (null == approvalLog) {
            log.warn("select approvalLog one, but approvalLog is null ...");
            return null;
        }
        approvalLog = approvalLogMapper.selectOne(approvalLog);
        return approvalLog;
    }


    @Override
    public PagingData<ApprovalLog> selectPage(ApprovalLog approvalLog) {
        PagingData<ApprovalLog> PagingData = new PagingData<>();

        if (null == approvalLog) {
            log.warn("select approvalLog page, but approvalLog is null...");
            return PagingData;
        }

        Integer queryCount = approvalLogMapper.selectByIndexCount(approvalLog);
        PagingData.setRow(queryCount);

        if (queryCount <= 0) {
            log.info("select approvalLog page , but count {} == 0 ...", queryCount);
            return PagingData;
        }

        List<ApprovalLog> approvalLogs = selectByIndex(approvalLog);
        PagingData.setData(approvalLogs);
        return PagingData;
    }

    @Override
    public List<ApprovalLog> selectByIndex(ApprovalLog approvalLog) {
        List<ApprovalLog> approvalLogs = new ArrayList<>();
        if (null == approvalLog) {
            log.warn("select approvalLog by index, but approvalLog is null ...");
            return approvalLogs;
        }

        approvalLogs = approvalLogMapper.selectByIndex(approvalLog);

        return approvalLogs;
    }
}