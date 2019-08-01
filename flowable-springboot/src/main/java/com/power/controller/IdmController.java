package com.power.controller;

import com.power.service.IdmService;
import org.flowable.idm.api.User;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author xuyunfeng
 *
 */
@Controller
@RequestMapping("idm")
public class IdmController {


	@Autowired
	private IdmService idmService;

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
	 	Object result = idmService.saveUser(user);
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

	/**
	 * 登录
	 * @param userId 用户id
	 * @param password 密码
	 * @return Object类型
	 * 示例：
	  localhost:9100/idm/login?userId=zhangsan&password=1234
	 */
	 @PostMapping("login")
	public ResponseEntity login(@RequestParam("userId") String userId,
								@RequestParam("password") String password,
								HttpServletRequest request,
								HttpServletResponse response){
	 	Object result = idmService.login(userId,password,request,response);
	 	return ResponseEntity.ok(result);
	 }

	/**
	 * 登出，清除session
	 * @return Object
	 */
	@GetMapping("logout")
	public String logout(){
	 	Object result = idmService.logout();

	 	return "logout";
	 }

	/**
	 * 测试用
	 * @return session.id
	 */
	@GetMapping("checkSession")
	public ResponseEntity checkSession(){
		 System.out.println("IdmController处sessionId："+session.getId());
		Object user = session.getAttribute("user");
		if (user==null){
			return ResponseEntity.ok("当前没有用户登陆");
		}
		User user1 = 	(User) user;
		return ResponseEntity.ok("输出的"+user1.getId());
	 }
}
