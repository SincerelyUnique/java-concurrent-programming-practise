/*
 * Copyright (C) 2017 IFlyTek. All rights reserved.
 */
package com.example.demo.chapter_03;

/**
 * <p>
 * <code>FinalReferenceExample</code>
 * </p>
 * Description: final域的引用类型
 *
 * @author Mcchu
 * @date 2018/5/9 9:17
 */
public class FinalReferenceExample {

	final int[] intArray;

	static FinalReferenceExample obj;

	public FinalReferenceExample(){			//构造函数
		intArray = new int[1];				//1
		intArray[0] = 1;					//2
	}

	public static void writeOne(){			//写线程A执行
		obj = new FinalReferenceExample();	//3
	}

	public static void writeTwo(){			//写线程B执行
		obj.intArray[0] = 2;				//4
	}

	public static void reader(){			//读线程C执行
		if (obj!=null){						//5
			int temp1 = obj.intArray[0];	//6
		}
	}
}
