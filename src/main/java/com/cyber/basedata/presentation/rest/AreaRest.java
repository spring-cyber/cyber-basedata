package com.cyber.basedata.presentation.rest;

import cn.hutool.core.lang.tree.Tree;
import com.cyber.application.controller.AuthingTokenController;
import com.cyber.basedata.application.service.AreaService;
import com.cyber.domain.constant.HttpResultCode;
import com.cyber.domain.entity.*;
import com.cyber.domain.request.AreaRequest;
import com.cyber.domain.request.CreateAreaRequest;
import com.cyber.domain.request.UpdateAreaRequest;
import com.cyber.log.annotation.Log;
import com.cyber.log.enums.BusinessType;
import com.cyber.security.infrastructure.toolkit.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AreaRest extends AuthingTokenController{

	private final AreaService areaService;

	@GetMapping("/area/search")
	public Response searchArea(@Valid AreaRequest request) {
		DataResponse<PagingData<Area>> response = new DataResponse<>();
        Area  area = request.toEvent(request.getTenantCode());
		PagingData<Area> areaPage = areaService.selectPage(area);
		response.setData(areaPage);
		return response;
	}

	@GetMapping("/area/select")
	public Response selectArea(@Valid AreaRequest request) {
		DataResponse<List<Area>> response = new DataResponse<>();
        Area  area = request.toEvent(request.getTenantCode());
		List<Area> areaList = areaService.selectArea(area);
		response.setData(areaList);
		return response;
	}

	@GetMapping("/area/tree")
	public Response selectTree(@Valid AreaRequest request) {
		DataResponse<List<Tree<String>>> response = new DataResponse<>();
        Area  area = request.toEvent(request.getTenantCode());
		List<Tree<String>> areaTree = areaService.selectTree(area);
		response.setData(areaTree);
		return response;
	}


	@GetMapping("/area")
	public Response selectOneArea(@Valid IdRequest idRequest) {
		DataResponse<Area> response = new DataResponse<>();

		Area area = new Area();
		area.setId(idRequest.getId());
        area.setTenantCode(idRequest.getTenantCode());
		area = areaService.selectOne(area);

		response.setData(area);
		return response;
	}

	@Log(title = "地址库管理", businessType = BusinessType.INSERT)
	@PostMapping("/area")
	public Response saveArea(@RequestBody @Valid CreateAreaRequest request) {
	    Area  area = request.toEvent(SecurityUtils.getUsername(),request.getTenantCode());

		int result = areaService.save(area);
		if (result < 1) {
			return Response.fail(HttpResultCode.SERVER_ERROR);
		}
		return Response.success();
	}

	@Log(title = "地址库管理", businessType = BusinessType.UPDATE)
	@PutMapping("/area")
	public Response updateArea(@RequestBody @Valid UpdateAreaRequest request) {
	    Area  area = request.toEvent(SecurityUtils.getUsername(),request.getTenantCode());
		int result = areaService.updateById(area);
		if (result < 1) {
			return Response.fail(HttpResultCode.SERVER_ERROR);
		}
		return Response.success();
	}

	@Log(title = "地址库管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/area")
	public Response deleteArea(@Valid IdRequest idRequest) {
		Area area = new Area();
		area.setId(idRequest.getId());

		if (areaService.hasChildByAreaId(idRequest.getId())) {
			return Response.fail(HttpResultCode.RECORD_EXIST.getCode(), "下级地址库不为空,不允许删除");
		}
		area.setTenantCode(idRequest.getTenantCode());
		area.setUpdator(SecurityUtils.getUsername());
        area.setUpdateTime(new Date());

		int result = areaService.deleteById(area);
		if (result < 1) {
			return Response.fail(HttpResultCode.SERVER_ERROR);
		}
		return Response.success();
	}
}
