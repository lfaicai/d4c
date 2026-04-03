package org.faicai.d4c.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class BaseEntity implements Serializable {

	/**
	 * 创建者
	 */
	@TableField(fill = FieldFill.INSERT, value = "created_by")
	private Long createdBy;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT, value = "created_at")
	private LocalDateTime createdAt;

	/**
	 * 更新者
	 */
	@TableField(fill = FieldFill.UPDATE, value = "updated_by")
	private Long updatedBy;

	/**
	 * 更新时间
	 */
	@TableField(fill = FieldFill.UPDATE, value = "updated_at")
	private LocalDateTime updatedAt;


	/**
	 * 是否删除 1：已删除 0：正常
	 */
	@TableLogic(value = "false", delval = "true")
	@TableField(value = "deleted",jdbcType = JdbcType.BOOLEAN)
	private Boolean deleted;

}