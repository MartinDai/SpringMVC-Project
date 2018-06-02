package com.doodl6.springmvc.client.api;

import com.doodl6.springmvc.client.request.GetDubboInfoRequest;
import com.doodl6.springmvc.client.response.GetDubboInfoResponse;

public interface FirstDubboService {

    GetDubboInfoResponse getDubboInfo(GetDubboInfoRequest getDubboInfoRequest);
}
