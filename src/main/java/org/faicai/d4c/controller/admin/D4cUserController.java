package org.faicai.d4c.controller.admin;

import lombok.RequiredArgsConstructor;
import org.faicai.d4c.pojo.dto.UserCreateDTO;
import org.faicai.d4c.pojo.dto.UserPasswordUpdateDTO;
import org.faicai.d4c.pojo.dto.UserQueryDTO;
import org.faicai.d4c.pojo.entity.D4cUser;
import org.faicai.d4c.pojo.vo.PageResult;
import org.faicai.d4c.pojo.vo.UserVO;
import org.faicai.d4c.service.D4cUserService;
import org.faicai.d4c.utils.CreateValidationGroup;
import org.faicai.d4c.utils.R;
import org.faicai.d4c.utils.UpdateValidationGroup;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/uc")
public class D4cUserController {

    private final D4cUserService d4cUserService;

    @PostMapping("/register")
    private R<Boolean> register(@Validated(CreateValidationGroup.class) @RequestBody UserCreateDTO userCreateDTO) {
        return R.ok(d4cUserService.create(userCreateDTO));
    }

    @PostMapping("/pageQuery")
    private R<PageResult<UserVO>> pageQuery(@RequestBody UserQueryDTO userQueryDTO) {
        return R.ok(d4cUserService.pageQuery(userQueryDTO));
    }

    @DeleteMapping("/{id}")
    private R<Boolean> delete(@PathVariable Long id) {
        return R.ok(d4cUserService.removeById(id));
    }
    @PutMapping("/update")
    private R<D4cUser> update(@Validated(UpdateValidationGroup.class) @RequestBody UserCreateDTO userCreateDTO) {
        return R.ok(d4cUserService.updateById(userCreateDTO));
    }

    @PutMapping("/updatePassword")
    private R<Boolean> updatePassword(@Validated @RequestBody UserPasswordUpdateDTO userPasswordUpdateDTO) {
        return R.ok(d4cUserService.updatePassword(userPasswordUpdateDTO));
    }

}
