package com.shareniu.shareniu_flowable_study.ch1;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecurityTest {
	public static void main(String[] args) {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		// 加密
		String encodedPassword = bCryptPasswordEncoder.encode("7");
		System.out.println(encodedPassword);
		// $2a$10$IubW2dt0MJHpxfaej/h9r.4.mVdXVXdJ5m5ixVKIGAAZlxK2AUHLS
		String rawPassword = "7";
		boolean b = bCryptPasswordEncoder.matches("10", "$2a$10$uUvgC6sVrqa9WJzVNYw7EOe/Up.o2.cm/c4Eq45wl/sNT9oWljV0S");
		System.out.println(b);
		
		
	}
}
