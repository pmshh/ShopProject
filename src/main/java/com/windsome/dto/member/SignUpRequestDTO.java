package com.windsome.dto.member;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SignUpRequestDTO {

    @NotBlank
    @Length(min = 5,max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{5,20}$")
    private String userIdentifier;

    @NotBlank
    private String name;

    @NotBlank
    @Length(min = 8,max = 20)
    private String password;

    @NotBlank
    @Length(min = 8,max = 20)
    private String passwordConfirm;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String tel;

    @NotBlank
    private String zipcode;

    @NotBlank
    private String addr;

    @NotBlank
    private String addrDetail;

}
