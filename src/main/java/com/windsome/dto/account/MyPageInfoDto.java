package com.windsome.dto.account;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MyPageInfoDto {

    private Long accountId; // 회원 기본키

    private String name; // 회원 이름

    private int totalOrderPrice; // 총 주문 금액

    private int point; // 현재 보유 포인트

    private int totalPoint; // 총 포인트

    private int totalUsePoint; // 사용한 포인트

    private int availPoint; // 사용 가능한 포인트

    public MyPageInfoDto(Long accountId, String name, int totalOrderPrice, int point, int totalPoint, int totalUsePoint) {
        this.accountId = accountId;
        this.name = name;
        this.totalOrderPrice = totalOrderPrice;
        this.point = point;
        this.totalPoint = totalPoint;
        this.totalUsePoint = totalUsePoint;
        this.availPoint = point - totalUsePoint;
    }
}
