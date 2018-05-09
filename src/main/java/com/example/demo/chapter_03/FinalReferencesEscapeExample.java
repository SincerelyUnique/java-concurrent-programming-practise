/*
 * Copyright (C) 2017 IFlyTek. All rights reserved.
 */
package com.example.demo.chapter_03;

/**
 * <p>
 * <code>FinalReferencesEscapeExample</code>
 * </p>
 * Description: 验证为什么final引用不能从构造函数内“溢出”
 *
 * @author Mcchu
 * @date 2018/5/9 9:31
 */
public class FinalReferencesEscapeExample {

	final int i;
	static FinalReferencesEscapeExample obj;

	public FinalReferencesEscapeExample(){
		i = 1;			//1 写final域
		obj = this;		//2 this引用在此“溢出”
	}

	public static void writer(){
		new FinalReferencesEscapeExample();
	}

	public static void reader(){
		if (obj!=null){		//3
			int temp = obj.i;  //4
		}
	}
}
