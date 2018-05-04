/*
 * Copyright (C) 2017 IFlyTek. All rights reserved.
 */
package com.example.demo.chapter_01;

/**
 * <p>
 * <code>DeadLockDemo</code>
 * </p>
 * Description: 线程t1和t2互相等待对方释放锁
 *
 * @author Mcchu
 * @date 2018/5/4 11:43
 */
public class DeadLockDemo {

	private static final String A = "A";

	private static final String B = "B";

	public static void main(String[] args) {
		new DeadLockDemo().deadLock();
	}

	private void deadLock(){
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (A){
					try {
						// 该行会有警告：Static member 'java.lang.Thread.sleep(long)' accessed via instance reference
						//Thread.currentThread().sleep(2000);
						Thread.sleep(2000);
					}catch (InterruptedException e){
						e.printStackTrace();
					}

					synchronized (B){
						System.out.println("1");
					}
				}
			}
		});

		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (B){
					synchronized (A){
						System.out.println("2");
					}
				}
			}
		});

		t1.start();
		t2.start();
	}
}
