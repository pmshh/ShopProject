package com.windsome.dto;

import com.windsome.constant.ItemSellStatus;
import lombok.Data;

@Data
public class CriteriaDto {

    private String page;

    private String searchDateType;

    private ItemSellStatus searchSellStatus;

    private String searchBy;

    private String searchQuery = "";
}
