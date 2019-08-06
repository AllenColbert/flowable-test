package com.power.controller;

import com.power.service.PowerIdmService;
import com.power.util.Result;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xuyunfeng
 *
 */
@Controller
@RequestMapping("idm")
public class IdmController {


	@Autowired
	private PowerIdmService powerIdmService;

	@Autowired
	private HttpSession session;

	/**
	 * 注册用户
	 * @param user userEntityImpl对象
	 * @return user or String
	 * user JSON示例：
	 * {
	  	"id":"zhangsan",
	  	"firstName":"san",
	  	"lastName":"zhang",
	  	"displayName":"Mrs.Zhang",
	  	"email":"ZhangSan@gmail.com",
	  	"password":"1234",
	  	"tenantId":"11",
	  	"revision":0
	  }
	 *
	 */
	 @PostMapping("userRegister")
	public ResponseEntity userRegister(@RequestBody UserEntityImpl user){
	 	Object result = powerIdmService.saveUser(user);
		 return ResponseEntity.ok(result);
	 }

	/**
	 * 注册组织
	 * @param group GroupEntityImpl
	 * @return group or String
	 * 示例：
	 {
	  	"id":"开发组",
	  	"name":"开发部",
	  	"type":"开发"
	  }
	 *
	 */
	 @PostMapping("groupRegister")
	public ResponseEntity groupRegister(@RequestBody GroupEntityImpl group){
	 	Object result = powerIdmService.saveGroup(group);
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
	 Object result = powerIdmService.createMembership(userId,groupId);

	 return ResponseEntity.ok(result);
	 }

	 @DeleteMapping("deleteUser")
	public ResponseEntity deleteUserById(@RequestParam("userId")String userId){
		 Object result = powerIdmService.deleteUserById(userId);
		 return ResponseEntity.ok(result);
	 }

	 @DeleteMapping("deleteGroup")
	public ResponseEntity deleteGroupById(@RequestParam("groupId")String groupId){
		 Object result = powerIdmService.deleteGroupById(groupId);
		 return ResponseEntity.ok(result);
	 }

	/**
	 * 登录
	 * @param userId 用户id
	 * @param password 密码
	 * @return Object类型
	 * 示例：
	  localhost:9100/idm/login?userId=zhangsan&password=1234
	 */
	 @PostMapping("login")
	 @ResponseBody
	public Result login(@RequestParam("userId") String userId,
						@RequestParam("password") String password,
						HttpServletRequest request,
						HttpServletResponse response){
		 return  powerIdmService.login(userId,password,request,response);
	 }

	/**
	 * 登出，清除session
	 * @return Object
	 */
	@GetMapping("logout")
	public ResponseEntity logout(){
	 	Object result = powerIdmService.logout();
	 	return ResponseEntity.ok(result);
	 }

	/**
	 * 查询当前登录状态
	 * @return result
	 */
	@GetMapping("checkCurrentUser")
	@ResponseBody
	public Result checkCurrentUser(){
		return powerIdmService.checkCurrentUser();
	 }
}
