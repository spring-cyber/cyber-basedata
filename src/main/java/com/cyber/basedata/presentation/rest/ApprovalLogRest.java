package com.cyber.basedata.presentation.rest;

import com.cyber.application.controller.AuthingTokenController;
import com.cyber.basedata.application.service.ApprovalLogService;
import com.cyber.domain.constant.HttpResultCode;
import com.cyber.domain.entity.*;
import com.cyber.domain.request.ApprovalLogRequest;
import com.cyber.domain.request.CreateApprovalLogRequest;
import com.cyber.domain.request.UpdateApprovalLogRequest;
import com.cyber.security.infrastructure.toolkit.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

@RestController
@RequiredArgsConstructor
public class ApprovalLogRest extends AuthingTokenController{

	private final ApprovalLogService approvalLogService;

	@GetMapping("/approvallog/search")
	public Response searchApprovalLog(@Valid ApprovalLogRequest request) {
		DataResponse<PagingData<ApprovalLog>> response = new DataResponse<>();
        ApprovalLog  approvallog = request.toEvent(request.getTenantCode());
		PagingData<ApprovalLog> approvalLogPage = approvalLogService.selectPage(approvallog);
		response.setData(approvalLogPage);
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

	@PostMapping("/approvallog")
	public Response saveApprovalLog(@RequestBody @Valid CreateApprovalLogRequest request) {
	    ApprovalLog  approvallog = request.toEvent(SecurityUtils.getUsername(),request.getTenantCode());

		int result = approvalLogService.save(approvallog);
		if (result < 1) {
			return Response.fail(HttpResultCode.SERVER_ERROR);
		}
		return Response.success();
	}

	@PutMapping("/approvallog")
	public Response updateApprovalLog(@RequestBody @Valid UpdateApprovalLogRequest request) {
	    ApprovalLog  approvallog = request.toEvent(SecurityUtils.getUsername(),request.getTenantCode());
		int result = approvalLogService.updateById(approvallog);
		if (result < 1) {
			return Response.fail(HttpResultCode.SERVER_ERROR);
		}
		return Response.success();
	}

	@DeleteMapping("/approvallog")
	public Response deleteApprovalLog(@Valid IdRequest idRequest) {
		ApprovalLog approvalLog = new ApprovalLog();
		approvalLog.setId(idRequest.getId());

		approvalLog.setTenantCode(idRequest.getTenantCode());
		approvalLog.setUpdator(SecurityUtils.getUsername());
        approvalLog.setUpdateTime(new Date());

		int result = approvalLogService.deleteById(approvalLog);
		if (result < 1) {
			return Response.fail(HttpResultCode.SERVER_ERROR);
		}
		return Response.success();
	}
}
