package org.faicai.d4c.service;

import lombok.RequiredArgsConstructor;
import org.faicai.d4c.constant.ResponseCode;
import org.faicai.d4c.exception.BusinessException;
import org.faicai.d4c.pojo.dto.UserLoginDTO;
import org.faicai.d4c.pojo.entity.D4cUser;
import org.faicai.d4c.pojo.vo.UserLoginVO;
import org.faicai.d4c.utils.TokenJwtUtil;
import org.faicai.d4c.pojo.vo.TokenPair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class AuthService {

    private final D4cUserService d4cUserService;

    private final PasswordEncoder passwordEncoder;


    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        D4cUser user = d4cUserService.getByAccount(userLoginDTO.getAccount());
        if (user == null) throw new BusinessException(ResponseCode.USER_NOT_EXIST);

        if (!passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) throw new BusinessException(ResponseCode.INCORRECT_PASSWORD);

        TokenPair tokenPair = TokenJwtUtil.createTokenPair(user.getAccount());
        return UserLoginVO.builder().token(tokenPair).userId(user.getId()).email(user.getEmail()).iconUrl(user.getIconUrl()).build();
    }


}
