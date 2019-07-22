package com.shareniu.v6.ch7;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultDeploymentCacheTest {
	public static void main(String[] args) {
		int limit = 3;
		Map<String, Object> map = Collections
				.synchronizedMap(new LinkedHashMap<String, Object>(limit + 1, 0.75f, false) {
					@Override
					protected boolean removeEldestEntry(java.util.Map.Entry<String, Object> eldest) {
						boolean removeEldest = size() > limit;
						return removeEldest;
					}
				});
		map.put("1", 1);
		map.put("2", 2);
		map.put("3", 3);
		map.put("4", 4);
		map.put("5", 5);
		map.get("1");
		map.get("1");
		System.out.println(map);
	}
}
