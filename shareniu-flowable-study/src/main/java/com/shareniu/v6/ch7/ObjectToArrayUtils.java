package com.shareniu.v6.ch7;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;

public class ObjectToArrayUtils {
	public static byte[] toByteArray(Object obj) {
		byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			bytes = bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}
	
	public static Object toObject(byte[] bytes ){
		Object obj=null;
		try {
			ByteArrayInputStream bis=new ByteArrayInputStream(bytes);
			ObjectInputStream ois=new ObjectInputStream(bis);
			obj=ois.readObject();
			ois.close();
			bis.close();
		} catch (Exception e) {
		}
		return obj;
	}
}
