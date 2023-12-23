package com.windsome.controller;

import com.windsome.config.security.CurrentAccount;
import com.windsome.dto.ProfileDto;
import com.windsome.dto.SignUpDto;
import com.windsome.dto.validator.ProfileDtoValidator;
import com.windsome.repository.AccountRepository;
import com.windsome.service.AccountService;
import com.windsome.dto.validator.SignUpDtoValidator;
import com.windsome.entity.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final SignUpDtoValidator signUpDtoValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    @InitBinder("signUpForm")
    public void initBinderForSignUpForm(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpDtoValidator);
    }

    @InitBinder("profileForm")
    public void initBinderForProfileForm(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new ProfileDtoValidator());
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute(new SignUpDto());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpDto signUpDto, Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up";
        }

        Account account = accountService.processNewAccount(signUpDto);
        accountService.login(signUpDto.getUserIdentifier(), signUpDto.getPassword());
        return "redirect:/";
    }

    @PostMapping("/check/id")
    @ResponseBody
    public String checkId(String userId) {
        if (accountRepository.existsByUserIdentifier(userId)) {
            return "fail";
        } else {
            return "success";
        }
    }

    @GetMapping("/check/email")
    @ResponseBody
    public String checkEmail(String email) throws Exception {
        return accountService.sendSignUpConfirmEmail(email);
    }

    @GetMapping("/find/pass")
    public String findPassGet() {
        return "account/find-pass";
    }

    @PostMapping("/find/pass")
    public @ResponseBody boolean findPassPost(String email, String name, Model model) throws MessagingException {
        boolean result = accountService.userEmailCheck(email, name);
        if (result) {
            accountService.updatePassword(email, name);
        }
        return result;
    }

    @GetMapping("/find/id")
    public String findIdGet() {
        return "account/find-id";
    }

    @PostMapping("/find/id")
    public @ResponseBody String findIdPost(String email, String name) {
        boolean userEmailCheckResult = accountService.userEmailCheck(email, name);
        return accountService.findId(email, name);
    }

    @GetMapping("/account/profile")
    public String updateProfileForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, ProfileDto.class));
        return "account/profile";
    }

    @PostMapping("/account/profile")
    public String updateProfile(@CurrentAccount Account account, @Valid ProfileDto profileDto, Errors errors,
                                Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "account/profile";
        }

        accountService.updateProfile(account, profileDto);
        attributes.addFlashAttribute("message", "프로필을 수정했습니다.");
        return "redirect:/account/profile";
    }
}
