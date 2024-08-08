package com.bugboo.CareerConnect.domain.dto.response;

import com.bugboo.CareerConnect.type.apiResponse.MetaData;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class ResponsePagingResultDTO {
    MetaData metaData;
    Object result;
    public static ResponsePagingResultDTO of(Page<?> pages) {
        ResponsePagingResultDTO responsePagingResultDTO = new ResponsePagingResultDTO();
        MetaData metaData = new MetaData();
        metaData.setCurrentPage(pages.getNumber() + 1);
        metaData.setTotalPages(pages.getTotalPages());
        metaData.setTotalElements(pages.getTotalElements());
        responsePagingResultDTO.setMetaData(metaData);
        responsePagingResultDTO.setResult(pages.getContent());
        return responsePagingResultDTO;
    }
}
