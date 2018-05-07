/*
 * Copyright (C) 2017 IFlyTek. All rights reserved.
 */
package com.example.demo.test;

/**
 * <p>
 * <code>TestFor</code>
 * </p>
 * Description:
 *
 * @author Mcchu
 * @date 2018/5/7 11:53
 */
public class TestFor {

	private static int counter = 1;

	public static void main(String[] args) {
		//deadCycleFor();
		//externalAssignmentFor();
		//noLimitFor();
	}

	/**
	 * 如果for循环后面括号中没有默认值，就是死循环
	 */
	private static void deadCycleFor(){
		int i = 0;
		for (;;){
			System.out.println(counter++);
		}
	}

	/**
	 * 外部赋值
	 */
	private static void externalAssignmentFor(){
		int j = 1;
		for (;j<10;j++){
			System.out.println(counter++);
		}
	}

	/**
	 * 死循环，必须在内部加限制条件
	 */
	private static void noLimitFor(){
		for (int i=0;i<10;){
			System.out.println(counter++);
			//i++;
		}
	}
}
