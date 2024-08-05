package com.bugboo.CareerConnect.domain.dto.response;

import com.bugboo.CareerConnect.type.apiResponse.MetaData;
import lombok.Data;

@Data
public class ResponsePagingResultDTO {
    MetaData metaData;
    Object result;
}
