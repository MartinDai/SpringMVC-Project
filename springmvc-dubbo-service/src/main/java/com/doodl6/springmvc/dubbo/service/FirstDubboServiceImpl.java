package com.doodl6.springmvc.dubbo.service;

import com.doodl6.springmvc.client.api.FirstDubboService;
import com.doodl6.springmvc.client.domain.DubboDomain;
import com.doodl6.springmvc.client.request.GetDubboInfoRequest;
import com.doodl6.springmvc.client.response.GetDubboInfoResponse;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class FirstDubboServiceImpl implements FirstDubboService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirstDubboServiceImpl.class);

    @Override
    public GetDubboInfoResponse getDubboInfo(GetDubboInfoRequest request) {
        GetDubboInfoResponse response = new GetDubboInfoResponse();
        try {
            Preconditions.checkArgument(request != null, "参数为空");
            Long id = request.getId();
            Preconditions.checkArgument(id != null, "id为空");

            DubboDomain dubboDomain = new DubboDomain();
            dubboDomain.setId(id);
            dubboDomain.setName("dubbo" + id);
            response.setDubboDomain(dubboDomain);
            response.setSuccess(true);
        } catch (IllegalArgumentException e) {
            response.setErrorMsg(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("获取dubbo信息出现异常", e);
            response.setErrorMsg("未知异常");
        }

        return response;
    }

}
