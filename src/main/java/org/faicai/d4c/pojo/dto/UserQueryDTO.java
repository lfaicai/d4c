package org.faicai.d4c.pojo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.faicai.d4c.pojo.vo.UserVO;

import java.util.List;

/**
 * @Describe：用户查询参数
 * @Author: faicai.lan
 * @Email: lanfaicai@163.com
 * @Date: 2025-10-11
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryDTO extends PageQueryBase<UserVO> {

    private String username;

    private String account;

    private Integer status;

    private List<Long> ids;

    private List<Long> excludeUserIds;
}
