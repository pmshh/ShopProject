package com.windsome.controller;

import com.windsome.WithAccount;
import com.windsome.entity.member.Address;
import com.windsome.entity.member.Member;
import com.windsome.repository.member.AddressRepository;
import com.windsome.repository.member.MemberRepository;
import com.windsome.service.member.MemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
class MemberControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired AddressRepository addressRepository;

    @Test
    @DisplayName("회원 가입 화면 보이는지 테스트")
    void signUpForm() throws Exception {
        mockMvc.perform(get("/members/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("member/register"))
                .andExpect(model().attributeExists("signUpRequestDTO"));
    }

    @Test
    @DisplayName("회원 가입 처리 - 입력값 오류")
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/members/new")
                        .param("userIdentifier", "test1234")
                        .param("email", "invalid_email") // 잘못된 이메일 형식
                        .param("name", "gildong")
                        .param("password", "test1234")
                        .param("passwordConfirm", "test1234")
                        .param("address1", "test")
                        .param("address2", "test")
                        .param("address3", "test")
                        .with(csrf()))
                .andExpect(status().isOk()) // 서버에서 200 OK를 반환하는 대신
                .andExpect(view().name("member/register")); // 회원 등록 페이지로 리다이렉트되어야 함
    }

    @Test
    @DisplayName("회원 가입 처리 - 입력값 정상")
    void signUpSubmit() throws Exception {
        mockMvc.perform(post("/members/new")
                        .param("userIdentifier", "test1234")
                        .param("email", "email@email.com")
                        .param("name", "gildong")
                        .param("password", "test1234")
                        .param("passwordConfirm", "test1234")
                        .param("tel", "010-5555-5555")
                        .param("zipcode", "test")
                        .param("addr", "test")
                        .param("addrDetail", "test")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        Member member = memberRepository.findByUserIdentifier("test1234");
        assertNotNull(member);
        assertNotEquals(member.getPassword(), "test1234");
    }

    @Test
    @DisplayName("프로필 수정 화면 보이는지 테스트")
    @WithAccount("USER")
    void updateProfileForm() throws Exception {
        Member member = memberRepository.findByUserIdentifier("USER");
        Address address = new Address();
        address.setMember(member);
        address.setDefault(true);
        addressRepository.save(address);

        mockMvc.perform(get("/members/" + member.getId() + "/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("member"));
    }

    @Test
    @DisplayName("프로필 수정 - 입력값 정상")
    @WithAccount("USER")
    void updateProfile() throws Exception {
        Member member = memberRepository.findByUserIdentifier("USER");

        mockMvc.perform(post("/members/" + member.getId() + "/update")
                        .param("userIdentifier", member.getUserIdentifier())
                        .param("password", "change1234")
                        .param("passwordConfirm", "change1234")
                        .param("name", "이순신")
                        .param("email", "test@email.com")
                        .param("tel", "01012341234")
                        .param("zipcode", "53212")
                        .param("addr", "서울")
                        .param("addrDetail", "401호")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/" + member.getId() + "/edit"))
                .andExpect(flash().attributeExists("message"));

        assertEquals("이순신", member.getName());
        assertTrue(passwordEncoder.matches("change1234", member.getPassword()));
    }

    @Test
    @DisplayName("프로필 수정 - 입력값 에러")
    @WithAccount("USER")
    void updateProfile_with_error() throws Exception {
        Member member = memberRepository.findByUserIdentifier("USER");

        mockMvc.perform(post("/members/" + member.getId() + "/update")
                        .param("userIdentifier", member.getUserIdentifier())
                        .param("name", "name")
                        .param("password", "changePassword123")
                        .param("passwordConfirm", "changePassword123")
                        .param("email", "test12.com")
                        .param("tel", "01012341234")
                        .param("zipcode", "53212")
                        .param("addr", "서울")
                        .param("addrDetail", "401호")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("member/update-member"))
                .andExpect(model().attributeExists("member"))
                .andExpect(model().attributeExists("message"));

        assertNotEquals("test12.com", member.getEmail());
        assertFalse(passwordEncoder.matches("changePassword123", member.getPassword()));
    }

    @Test
    @DisplayName("이메일 중복 체크 후 이메일 인증 - 성공")
    @WithAccount("USER")
    void confirmEmail() throws Exception {
        mockMvc.perform(get("/members/email-verification")
                        .param("email", "newemail@test.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")));
    }

    @Test
    @DisplayName("이메일 중복 체크 후 이메일 인증 - 실패")
    @WithAccount("USER")
    void confirmEmailFail() throws Exception {
        Member member = memberRepository.findByUserIdentifier("USER");
        mockMvc.perform(get("/members/email-verification")
                        .param("email", member.getEmail()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("이미 사용중인 이메일입니다."));
    }

    @Test
    @DisplayName("아이디 중복 체크 - 성공")
    @WithAccount("USER")
    void duplicateCheckEmail() throws Exception {
        mockMvc.perform(post("/members/check-userid")
                        .param("userId", "test12345678")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("사용 가능한 아이디입니다."));
    }

    @Test
    @DisplayName("아이디 중복 체크 - 실패")
    @WithAccount("USER")
    void duplicateCheckEmailFail() throws Exception {
        Member member = memberRepository.findByUserIdentifier("USER");
        mockMvc.perform(post("/members/check-userid")
                        .param("userId", member.getUserIdentifier())
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("이미 사용중인 아이디입니다."));
    }

    @Test
    @DisplayName("아이디/비밀번호 찾기 화면 보이는지 테스트")
    @WithAccount("USER")
    void findAccount() throws Exception {
        mockMvc.perform(get("/forgot-credentials")
                        .param("action", "find-id"))
                .andExpect(model().attributeExists("action"))
                .andExpect(view().name("member/find-account"));
    }

    @Test
    @DisplayName("아이디 찾기 화면 (쿼리 파라미터 숨기기 위한 용도) 보이는지 테스트")
    @WithAccount("USER")
    void findIdRedirect() throws Exception {
        mockMvc.perform(get("/forgot-credentials/userid-lookup-result-redirect")
                        .param("name", "test")
                        .param("email", "test@naver.com"))
                .andExpect(redirectedUrl("/forgot-credentials/userid-lookup-result"));
    }

    @Test
    @DisplayName("아이디 찾기 화면 보이는지 테스트")
    @WithAccount("USER")
    void findId() throws Exception {
        mockMvc.perform(get("/forgot-credentials/userid-lookup-result"))
                .andExpect(view().name("member/userid-lookup-result"));
    }

    @Test
    @DisplayName("비밀번호 변경 화면 (쿼리 파라미터 숨기기 위한 용도) 보이는지 테스트")
    @WithAccount("USER")
    void updatePwRedirect() throws Exception {
        mockMvc.perform(get("/forgot-credentials/password-lookup-result-redirect")
                        .param("userIdentifier", "test")
                        .param("name", "test")
                        .param("email", "test@naver.com"))
                .andExpect(redirectedUrl("/forgot-credentials/password-lookup-result"));
    }

    @Test
    @DisplayName("비밀번호 변경 화면 보이는지 테스트")
    @WithAccount("USER")
    void updatePwForm() throws Exception {
        mockMvc.perform(get("/forgot-credentials/password-lookup-result"))
                .andExpect(view().name("member/password-lookup-result"));
    }

    @Test
    @DisplayName("비밀번호 변경 테스트")
    @WithAccount("USER")
    void updatePw() throws Exception {
        mockMvc.perform(post("/forgot-credentials/reset-password")
                        .param("userIdentifier", "USER")
                        .param("password", "testtest1234@")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("비밀번호가 변경되었습니다."));
    }

    @Test
    @DisplayName("아이디/비밀번호 찾기 화면 - 이메일 인증 테스트")
    @WithAccount("USER")
    void findIdPwEmailConfirm() throws Exception {
        mockMvc.perform(post("/forgot-credentials/email-verification")
                        .param("email", "test1234@email.com")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("마이 페이지 화면 보이는지 테스트")
    @WithAccount("USER")
    void mypage() throws Exception {
        mockMvc.perform(get("/mypage"))
                .andExpect(model().attributeExists("orderStatusCounts"))
                .andExpect(model().attributeExists("userSummary"))
                .andExpect(view().name("member/mypage"));
    }
}