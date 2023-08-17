package com.cyber.basedata.presentation.rest;

import cn.hutool.core.lang.tree.Tree;
import com.cyber.application.controller.AuthingTokenController;
import com.cyber.basedata.application.service.DictService;
import com.cyber.domain.constant.HttpResultCode;
import com.cyber.domain.entity.*;
import com.cyber.domain.request.CreateDictRequest;
import com.cyber.domain.request.DictRequest;
import com.cyber.domain.request.UpdateDictRequest;
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
public class DictRest extends AuthingTokenController{

	private final DictService dictService;

	@GetMapping("/dict/search")
	public Response searchDict(@Valid DictRequest request) {
		DataResponse<PagingData<Dict>> response = new DataResponse<>();
        Dict  dict = request.toEvent(request.getTenantCode());
		PagingData<Dict> dictPage = dictService.selectPage(dict);
		response.setData(dictPage);
		return response;
	}

	@GetMapping("/dict/select")
	public Response selectDict(@Valid DictRequest request) {
		DataResponse<List<Dict>> response = new DataResponse<>();
        Dict  dict = request.toEvent(request.getTenantCode());
		List<Dict> dictList = dictService.selectDict(dict);
		response.setData(dictList);
		return response;
	}

	@GetMapping("/dict/tree")
	public Response searchTreeDict(@Valid DictRequest request) {
		DataResponse<List<Tree<String>>> response = new DataResponse<>();
        Dict  dict = request.toEvent(request.getTenantCode());
		List<Tree<String>> treeDict = dictService.searchTreeDict(dict);
		response.setData(treeDict);
		return response;
	}


	@GetMapping("/dict")
	public Response selectOneDict(@Valid IdRequest idRequest) {
		DataResponse<Dict> response = new DataResponse<>();

		Dict dict = new Dict();
		dict.setId(idRequest.getId());
        dict.setTenantCode(idRequest.getTenantCode());
		dict = dictService.selectOne(dict);

		response.setData(dict);
		return response;
	}

	@GetMapping(value = "/open/dict/data/{dictGroupCode}/{dictTypeCode}")
	public Response selectDictDataByType(@PathVariable String dictGroupCode, @PathVariable String dictTypeCode) {
		DataResponse<List<Dict>> response = new DataResponse<>();
		List<Dict> dictList = dictService.selectDictDataByType(dictGroupCode, dictTypeCode);
		response.setData(dictList);
		return response;
	}

	@Log(title = "字典管理", businessType = BusinessType.INSERT)
	@PostMapping("/dict")
	public Response saveDict(@RequestBody @Valid CreateDictRequest request) {
	    Dict  dict = request.toEvent(SecurityUtils.getUsername(),request.getTenantCode());

		if (!dictService.checkDictNameUnique(dict)) {
			return Response.fail(HttpResultCode.RECORD_EXIST.getCode(),
					"新增字典'" + dict.getName() + "'失败，字典名称已存在");

		} else if (!dictService.checkDictCodeUnique(dict)) {
			return Response.fail(HttpResultCode.RECORD_EXIST.getCode(),
					"新增字典'" + dict.getCode() + "'失败，字典编码已存在");
		}

		int result = dictService.save(dict);
		if (result < 1) {
			return Response.fail(HttpResultCode.SERVER_ERROR);
		}
		return Response.success();
	}

	@Log(title = "字典管理", businessType = BusinessType.UPDATE)
	@PutMapping("/dict")
	public Response updateDict(@RequestBody @Valid UpdateDictRequest request) {
	    Dict  dict = request.toEvent(SecurityUtils.getUsername(),request.getTenantCode());

		if (!dictService.checkDictNameUnique(dict)) {
			return Response.fail(HttpResultCode.RECORD_EXIST.getCode(),
					"编辑字典'" + dict.getName() + "'失败，字典名称已存在");

		} else if (!dictService.checkDictCodeUnique(dict)) {
			return Response.fail(HttpResultCode.RECORD_EXIST.getCode(),
					"编辑字典'" + dict.getCode() + "'失败，字典编码已存在");
		}

		int result = dictService.updateById(dict);
		if (result < 1) {
			return Response.fail(HttpResultCode.SERVER_ERROR);
		}
		return Response.success();
	}

	@Log(title = "字典管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/dict")
	public Response deleteDict(@Valid IdRequest idRequest) {
		Dict dict = new Dict();
		dict.setId(idRequest.getId());
		if (dictService.hasChildByDictId(idRequest.getId())) {
			return Response.fail(HttpResultCode.RECORD_EXIST.getCode(), "字典组或字典类型不为空,不允许删除");
		}

		dict.setTenantCode(idRequest.getTenantCode());
		dict.setUpdator(SecurityUtils.getUsername());
        dict.setUpdateTime(new Date());

		int result = dictService.deleteById(dict);
		if (result < 1) {
			return Response.fail(HttpResultCode.SERVER_ERROR);
		}
		return Response.success();
	}

	@Log(title = "字典管理", businessType = BusinessType.CLEAN)
	@DeleteMapping("/dict/refreshCache")
	public Response refreshCache() {
		dictService.resetDictCache();
		return Response.success();
	}
}
