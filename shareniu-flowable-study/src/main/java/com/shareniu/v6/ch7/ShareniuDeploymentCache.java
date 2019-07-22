package com.shareniu.v6.ch7;

import org.flowable.engine.common.impl.persistence.deploy.DeploymentCache;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;

public class ShareniuDeploymentCache<T> implements DeploymentCache<T> {

	Jedis jedis = new Jedis("127.0.0.1", 6379);

	@Override
	public T get(String id) {
		System.out.println(id);
		return (T) jedis.get(id.getBytes());
	}

	@Override
	public boolean contains(String id) {
		T t = get(id);

		return t == null ? false : true;
	}

	@Override
	public void add(String id, T object) {
		if (!id.equals("")&& id!=null && object!=null) {
			Object json = JSON.toJSON(object);
			jedis.set(id.getBytes(), ObjectToArrayUtils.toByteArray(json));
		}
		

	}

	@Override
	public void remove(String id) {
		jedis.del(id.getBytes());
	}

	@Override
	public void clear() {

	}

}
