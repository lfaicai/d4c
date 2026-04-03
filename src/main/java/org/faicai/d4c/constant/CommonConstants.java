package org.faicai.d4c.constant;

public class CommonConstants {

	/**
	 * 删除
	 */
	public static final Integer STATUS_DEL = -1;

	/**
	 * 禁用
	 */
	public static final Integer STATUS_BAN = 1;
	/**
	 * 正常
	 */
	public static final Integer STATUS_NORMAL = 0;


	/**
	 * 成功标记
	 */
	public static final Integer SUCCESS = STATUS_NORMAL;

	/**
	 * 失败标记
	 */
	public static final Integer FAIL = STATUS_BAN;


	public static final Integer ERROR = STATUS_DEL;


	public static final String ROLE_PREFIX = "ROLE_";



}