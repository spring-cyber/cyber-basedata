package com.cyber.basedata.presentation.rest;

import cn.hutool.core.lang.tree.Tree;
import com.alibaba.fastjson.JSONObject;
import com.cyber.application.controller.AuthingTokenController;
import com.cyber.basedata.application.service.ApprovalLogService;
import com.cyber.basedata.application.service.BaseDataService;
import com.cyber.basedata.domain.request.TableRequest;
import com.cyber.domain.constant.HttpResultCode;
import com.cyber.domain.entity.*;
import com.cyber.domain.request.*;
import com.cyber.log.annotation.Log;
import com.cyber.log.enums.BusinessType;
import com.cyber.security.infrastructure.toolkit.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/basedata")
@RestController
@RequiredArgsConstructor
public class BaseDataRest extends AuthingTokenController{

	private final BaseDataService baseDataService;

	private final ApprovalLogService approvalLogService;

	@GetMapping("/search")
	public Response searchBaseData(@Valid BaseDataRequest request) {
		DataResponse<PagingData<BaseData>> response = new DataResponse<>();
        BaseData  basedata = request.toEvent(request.getTenantCode());
		PagingData<BaseData> baseDataPage = baseDataService.selectPage(basedata);
		response.setData(baseDataPage);
		return response;
	}
	@GetMapping("/table/data")
	public Response searchTableData(@Valid TableRequest request) {
		DataResponse<PagingData<JSONObject>> response = new DataResponse<>();
		PagingData<JSONObject> tableData = baseDataService.searchTableData(request);
		response.setData(tableData);
		return response;
	}

	@GetMapping("/table/column")
	public Response searchTableColumn(@Valid TableRequest request) {
		DataResponse<List<TableColumn>> response = new DataResponse<>();
		List<TableColumn> tableColumn = baseDataService.searchTableColumn(request);
		response.setData(tableColumn);
		return response;
	}


	@GetMapping("/approvallog/search")
	public Response searchApprovalLog(@Valid ApprovalLogRequest request) {
		DataResponse<PagingData<ApprovalLog>> response = new DataResponse<>();
		ApprovalLog  approvallog = request.toEvent(request.getTenantCode());
		PagingData<ApprovalLog> approvalLogPage = approvalLogService.selectPage(approvallog);
		response.setData(approvalLogPage);
		return response;
	}

	@GetMapping("/select")
	public Response selectBaseData(@Valid BaseDataRequest request) {
		DataResponse<List<BaseData>> response = new DataResponse<>();
        BaseData  basedata = request.toEvent(request.getTenantCode());
		List<BaseData> baseDataList = baseDataService.selectBaseData(basedata);
		response.setData(baseDataList);
		return response;
	}

	@GetMapping("/tree")
	public Response searchBaseDataTree(@Valid BaseDataRequest request) {
		DataResponse<List<Tree<String>>> response = new DataResponse<>();
        BaseData  basedata = request.toEvent(request.getTenantCode());
		List<Tree<String>> baseDataTree = baseDataService.searchBaseDataTree(basedata);
		response.setData(baseDataTree);
		return response;
	}


	@GetMapping("")
	public Response selectOneBaseData(@Valid IdRequest idRequest) {
		DataResponse<BaseData> response = new DataResponse<>();

		BaseData baseData = new BaseData();
		baseData.setId(idRequest.getId());
        baseData.setTenantCode(idRequest.getTenantCode());
		baseData = baseDataService.selectOne(baseData);

		response.setData(baseData);
		return response;
	}


	@GetMapping("/approvallog")
	public Response selectOneApprovalLog(@Valid IdRequest idRequest) {
		DataResponse<ApprovalLog> response = new DataResponse<>();

		ApprovalLog approvalLog = new ApprovalLog();
		approvalLog.setId(idRequest.getId());
		approvalLog.setTenantCode(idRequest.getTenantCode());
		approvalLog = approvalLogService.selectOne(approvalLog);

		response.setData(approvalLog);
		return response;
	}

	@Log(title = "主数据管理", businessType = BusinessType.INSERT)
	@PostMapping("")
	public Response saveBaseData(@RequestBody @Valid CreateBaseDataRequest request) {
	    BaseData  basedata = request.toEvent(SecurityUtils.getUsername(),request.getTenantCode());

		if (!baseDataService.checkBaseDataNameUnique(basedata)) {
			return Response.fail(HttpResultCode.RECORD_EXIST.getCode(),
					"新增主数据组/表'" + basedata.getName() + "'失败，新增主数据组/表名称已存在");

		} else if (!baseDataService.checkBaseDataCodeUnique(basedata)) {
			return Response.fail(HttpResultCode.RECORD_EXIST.getCode(),
					"新增主数据组/表'" + basedata.getCode() + "'失败，新增主数据组/表编码已存在");
		}

		int result = baseDataService.save(basedata);
		if (result < 1) {
			return Response.fail(HttpResultCode.SERVER_ERROR);
		}
		return Response.success();
	}

	@Log(title = "主数据管理", businessType = BusinessType.UPDATE)
	@PutMapping("")
	public Response updateBaseData(@RequestBody @Valid UpdateBaseDataRequest request) {
	    BaseData  basedata = request.toEvent(SecurityUtils.getUsername(),request.getTenantCode());

		if (!baseDataService.checkBaseDataNameUnique(basedata)) {
			return Response.fail(HttpResultCode.RECORD_EXIST.getCode(),
					"编辑主数据组/表'" + basedata.getName() + "'失败，主数据组/表名称已存在");

		} else if (!baseDataService.checkBaseDataCodeUnique(basedata)) {
			return Response.fail(HttpResultCode.RECORD_EXIST.getCode(),
					"编辑主数据组/表'" + basedata.getCode() + "'失败，主数据组/表编码已存在");
		}

		int result = baseDataService.updateById(basedata);
		if (result < 1) {
			return Response.fail(HttpResultCode.SERVER_ERROR);
		}
		return Response.success();
	}

	@Log(title = "主数据表变更审批", businessType = BusinessType.UPDATE)
	@PutMapping("/approvallog")
	public Response updateApprovalLog(@RequestBody @Valid UpdateApprovalLogRequest request) {
		ApprovalLog  approvallog = request.toEvent(SecurityUtils.getUsername(),request.getTenantCode());
		int result = approvalLogService.updateById(approvallog);
		if (result < 1) {
			return Response.fail(HttpResultCode.SERVER_ERROR);
		}
		return Response.success();
	}

	@Log(title = "主数据管理", businessType = BusinessType.DELETE)
	@DeleteMapping("")
	public Response deleteBaseData(@Valid IdRequest idRequest) {
		BaseData baseData = new BaseData();
		baseData.setId(idRequest.getId());

		if (baseDataService.hasChildByBaseDataId(idRequest.getId())) {
			return Response.fail(HttpResultCode.RECORD_EXIST.getCode(), "主数据组不为空,不允许删除");
		}

		int result = baseDataService.deleteById(baseData);
		if (result < 1) {
			return Response.fail(HttpResultCode.SERVER_ERROR);
		}
		return Response.success();
	}
}
