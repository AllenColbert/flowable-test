package com.power.controller;

import com.power.service.IdmService;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author xuyunfeng
 *
 */
@RestController
@RequestMapping("test")
public class IdmController {


	@Autowired
	private IdmService idmService;


	/**
	 * 注册用户
	 * @param user userEntityImpl对象
	 * @return user or String
	 * user JSON示例：
	 * {
	 * 	"id":"zhangsan",
	 * 	"firstName":"san",
	 * 	"lastName":"zhang",
	 * 	"displayName":"Mrs.Zhang",
	 * 	"email":"ZhangSan@gmail.com",
	 * 	"password":"1234",
	 * 	"tenantId":"11",
	 * 	"revision":0
	 * }
	 *
	 */
	 @PostMapping("userRegister")
	public ResponseEntity userRegister(@RequestBody UserEntityImpl user){
	 	Object result = idmService.saveUser(user);
		 return ResponseEntity.ok(result);
	 }


	/**
	 * 注册组织
	 * @param group GroupEntityImpl
	 * @return group or String
	 * 示例：
	 *{
	 * 	"id":"开发组",
	 * 	"name":"开发部",
	 * 	"type":"开发",
	 * }
	 *
	 */
	 @PostMapping("groupRegister")
	public ResponseEntity groupRegister(@RequestBody GroupEntityImpl group){
	 	Object result = idmService.saveGroup(group);
	 	return ResponseEntity.ok(result);
	 }

	/**
	 * 关联用户与组织
	 * @param userId 用户id
	 * @param groupId 组织id
	 * @return success
	 */
	 @PostMapping("createMembership")
	public ResponseEntity createMembership(@RequestParam("userId")String userId,
								   @RequestParam("groupId")String groupId){
	 Object result = idmService.createMembership(userId,groupId);

	 return ResponseEntity.ok(result);
	 }

	 @DeleteMapping("deleteUser")
	public ResponseEntity deleteUserById(@RequestParam("userId")String userId){
		 Object result = idmService.deleteUserById(userId);
		 return ResponseEntity.ok(result);
	 }

	 @DeleteMapping("deleteGroup")
	public ResponseEntity deleteGroupById(@RequestParam("groupId")String groupId){
		 Object result = idmService.deleteGroupById(groupId);
		 return ResponseEntity.ok(result);
	 }
}
