package org.faicai.d4c.controller;

import lombok.RequiredArgsConstructor;
import org.faicai.d4c.pojo.dto.UserLoginDTO;
import org.faicai.d4c.pojo.vo.UserLoginVO;
import org.faicai.d4c.service.AuthService;
import org.faicai.d4c.utils.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public R<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        return R.ok(authService.login(userLoginDTO));
    }

//    @PostMapping("/register")
//    public R<?> register(@RequestBody RegisterRequest registerRequest) {
//
//    }
}
