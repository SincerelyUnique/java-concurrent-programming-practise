/*
 * Copyright (C) 2017 IFlyTek. All rights reserved.
 */
package com.example.demo.chapter_01;

/**
 * <p>
 * <code>ConcurrencyTest</code>
 * </p>
 * Description: 并发并不一定比串行块，因为线程有创建和上下文切换的开销
 * 1.count取值为百万级别时，串行和并发耗时差不多；
 * 2.count>百万级别时，并行耗时少；
 * 3.count<百万级别时，串行耗时少；
 *
 * @author Mcchu
 * @date 2018/5/4 10:11
 */
public class ConcurrencyTest {

	private static final long count = 10000001;

	public static void main(String[] args) throws InterruptedException{
		concurrency();
		serial();
	}

	private static void concurrency() throws InterruptedException {
		long start = System.currentTimeMillis();

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				int a = 0;
				for (long i=0;i<count;i++){
					a += 5;
				}
			}
		});

		thread.start();

		int b = 0;
		for (long i =0;i<count;i++){
			b--;
		}
		thread.join();	//主线程等待子线程执行结束后再往下执行
		long time = System.currentTimeMillis()-start;
		System.out.println("concurrency :" + time + "ms,b="+b);
	}

	private static void serial(){
		long start = System.currentTimeMillis();
		int a = 0;
		for (long i=0;i<count;i++){
			a += 5;
		}

		int b = 0;
		for (long i=0;i<count;i++){
			b--;
		}

		long time = System.currentTimeMillis()-start;
		System.out.println("serial: "+time+"ms,b="+b+",a="+a);
	}
}
