package com.cyber.basedata.presentation.rest;

import com.cyber.application.controller.AuthingTokenController;
import com.cyber.basedata.application.service.PlaceService;
import com.cyber.domain.constant.HttpResultCode;
import com.cyber.domain.entity.*;
import com.cyber.domain.request.CreatePlaceRequest;
import com.cyber.domain.request.PlaceRequest;
import com.cyber.domain.request.UpdatePlaceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlaceRest extends AuthingTokenController{

	private final PlaceService placeService;

	@GetMapping("/place/search")
	public Response searchPlace(@Valid PlaceRequest request) {
		DataResponse<PagingData<Place>> response = new DataResponse<>();
        Place  place = request.toEvent(request.getTenantCode());
		PagingData<Place> placePage = placeService.selectPage(place);
		response.setData(placePage);
		return response;
	}

	@GetMapping("/place/select")
	public Response selectPlace(@Valid PlaceRequest request) {
		DataResponse<List<Place>> response = new DataResponse<>();
        Place  place = request.toEvent(request.getTenantCode());
		List<Place> placeList = placeService.selectPlace(place);
		response.setData(placeList);
		return response;
	}


	@GetMapping("/place")
	public Response selectOnePlace(@Valid IdRequest idRequest) {
		DataResponse<Place> response = new DataResponse<>();

		Place place = new Place();
		place.setId(idRequest.getId());
        place.setTenantCode(idRequest.getTenantCode());
		place = placeService.selectOne(place);

		response.setData(place);
		return response;
	}

	@PostMapping("/place")
	public Response savePlace(@RequestBody @Valid CreatePlaceRequest request) {
	    Place  place = request.toEvent(getSessionId(),request.getTenantCode());

		int result = placeService.save(place);
		if (result < 1) {
			return Response.fail(HttpResultCode.SERVER_ERROR);
		}
		return Response.success();
	}

	@PutMapping("/place")
	public Response updatePlace(@RequestBody @Valid UpdatePlaceRequest request) {
	    Place  place = request.toEvent(getSessionId(),request.getTenantCode());
		int result = placeService.updateById(place);
		if (result < 1) {
			return Response.fail(HttpResultCode.SERVER_ERROR);
		}
		return Response.success();
	}

	@DeleteMapping("/place")
	public Response deletePlace(@Valid IdRequest idRequest) {
		Place place = new Place();
		place.setId(idRequest.getId());

		place.setTenantCode(idRequest.getTenantCode());
		place.setUpdator(getSessionId());
        place.setUpdateTime(new Date());

		int result = placeService.deleteById(place);
		if (result < 1) {
			return Response.fail(HttpResultCode.SERVER_ERROR);
		}
		return Response.success();
	}
}
