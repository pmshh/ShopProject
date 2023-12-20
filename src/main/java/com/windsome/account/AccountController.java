package com.windsome.account;

import com.windsome.account.form.SignUpForm;
import com.windsome.account.validator.SignUpFormValidator;
import com.windsome.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up";
        }

        Account account = accountService.processNewAccount(signUpForm);
        accountService.login(account);
        return "redirect:/";
    }

    @PostMapping("/check-id")
    @ResponseBody
    public String checkId(String userId) {
        if (accountRepository.existsByUserIdentifier(userId)) {
            return "fail";
        } else {
            return "success";
        }
    }

    @GetMapping("/check-email")
    @ResponseBody
    public String checkEmail(String email) throws Exception {
        return accountService.sendSignUpConfirmEmail(email);
    }

    @GetMapping("/find-pass")
    public String findPass() {
        return "account/find-password";
    }

    @PostMapping("/update-pass")
    public @ResponseBody boolean updatePass(String email, String name) throws MessagingException {
        boolean result = accountService.userEmailCheck(email, name);
        accountRepository.findByEmail(email);
        if (result) {
            accountService.updatePassword(email, name);
        }
        return result;
    }
}
