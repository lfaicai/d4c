package org.faicai.d4c;


import org.faicai.d4c.pojo.dto.UserPasswordUpdateDTO;
import org.faicai.d4c.service.D4cUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = D4CApplication.class)
public class D4CApplicationTest {

    @Autowired
    D4cUserService d4cUserService;

    @Test
    public void updatePasswordTest() {
        UserPasswordUpdateDTO userPasswordUpdateDTO = new UserPasswordUpdateDTO();
        userPasswordUpdateDTO.setAccount("admin");
        userPasswordUpdateDTO.setNewPassword("admin");
        d4cUserService.updatePassword(userPasswordUpdateDTO);
    }
}
