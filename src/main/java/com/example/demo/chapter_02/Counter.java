/*
 * Copyright (C) 2017 IFlyTek. All rights reserved.
 */
package com.example.demo.chapter_02;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * <code>Counter</code>
 * </p>
 * Description:
 *
 * @author Mcchu
 * @date 2018/5/4 15:27
 */
public class Counter {

	private int i = 0;

	private AtomicInteger atomicI = new AtomicInteger(0);

	public static void main(String[] args) {
		final Counter cas = new Counter();
		List<Thread> ts = new ArrayList<>(600);
		long start = System.currentTimeMillis();
		for (int j=0;j<100;j++){
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i=0; i<10000; i++){
						cas.count();
						cas.safeCount();
					}
				}
			});

			ts.add(t);
		}

		for (Thread t: ts){
			t.start();
		}

		// 等待所有线程执行完成
		for (Thread t: ts){
			try {
				t.join();
			}catch (InterruptedException e){
				e.printStackTrace();
			}
		}

		System.out.println(cas.i);
		System.out.println(cas.atomicI.get());
		System.out.println(System.currentTimeMillis()-start);
	}

	/**
	 * 使用CAS实现线程安全计算器
	 */
	private void safeCount(){
		for (;;){
			int i = atomicI.get();
			boolean suc = atomicI.compareAndSet(i,++i);
			if (suc){
				break;
			}
		}
	}

	/**
	 * 非线程安全计算器
	 */
	private void count(){
		i++;
	}
}
