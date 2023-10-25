package com.colphacy.service;

import com.colphacy.dto.location.request.GHNGetDistrictsRequest;
import com.colphacy.dto.location.request.GHNGetWardsRequest;
import com.colphacy.dto.location.response.*;
import com.colphacy.exception.GHNException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {
    private static final String GET_DISTRICTS_URL = "https://online-gateway.ghn.vn/shiip/public-api/master-data/district";
    private static final String GET_WARDS_URL = "https://online-gateway.ghn.vn/shiip/public-api/master-data/ward";
    private static final String GET_PROVINCES_URL = "https://online-gateway.ghn.vn/shiip/public-api/master-data/province";

    @Value("${colphacy.api.ghn.token}")
    private String ghnToken;

    public List<GHNProvinceDTO> getProvinces() {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(getGHNDefaultHeaders());
        ResponseEntity<GHNGetProvincesResponse> ghnGetProvincesRequestResponse =
                restTemplate.exchange(GET_PROVINCES_URL, HttpMethod.GET, entity, GHNGetProvincesResponse.class);

        int status = ghnGetProvincesRequestResponse.getStatusCodeValue();
        if (status == 200) {
            GHNGetProvincesResponse response = ghnGetProvincesRequestResponse.getBody();
            if (response != null) return response.getData();
        }
        throw new GHNException();
    }

    public List<GHNDistrictDTO> getDistricts(int provinceId) {
        RestTemplate restTemplate = new RestTemplate();
        GHNGetDistrictsRequest requestBody = new GHNGetDistrictsRequest();
        requestBody.setProvinceID(provinceId);
        HttpEntity<GHNGetDistrictsRequest> entity = getGHNRequestEntity(requestBody);

        ResponseEntity<GHNGetDistrictsResponse> ghnGetDistrictsRequestResponse =
                restTemplate.exchange(GET_DISTRICTS_URL, HttpMethod.POST, entity, GHNGetDistrictsResponse.class);

        int status = ghnGetDistrictsRequestResponse.getStatusCodeValue();
        if (status == 200) {
            GHNGetDistrictsResponse response = ghnGetDistrictsRequestResponse.getBody();
            if (response != null) return response.getData();
        }
        throw new GHNException();
    }

    @Override
    public List<GHNWardDTO> getWards(int districtId) {
        RestTemplate restTemplate = new RestTemplate();
        GHNGetWardsRequest requestBody = new GHNGetWardsRequest();
        requestBody.setDistrictID(districtId);
        HttpEntity<GHNGetWardsRequest> entity = getGHNRequestEntity(requestBody);

        ResponseEntity<GHNGetWardsResponse> ghnGetWardsRequestResponseEntity =
                restTemplate.exchange(GET_WARDS_URL, HttpMethod.POST, entity, GHNGetWardsResponse.class);

        int status = ghnGetWardsRequestResponseEntity.getStatusCodeValue();
        if (status == 200) {
            GHNGetWardsResponse response = ghnGetWardsRequestResponseEntity.getBody();
            if (response != null) return response.getData();
        }
        throw new GHNException();
    }

    private <T> HttpEntity<T> getGHNRequestEntity(T requestBody) {
        HttpHeaders headers = getGHNDefaultHeaders();
        HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);
        return entity;
    }

    private HttpHeaders getGHNDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("token", ghnToken);
        return headers;
    }
}
