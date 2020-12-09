package com.revature;

import com.revature.service.PasswordUtil;

public class Driver {

	public static void main(String[] args) {
		
		String pass = "password";
		String hash = PasswordUtil.hash(pass);

		System.out.println(PasswordUtil.checkPassword(pass, hash));
	}
}
