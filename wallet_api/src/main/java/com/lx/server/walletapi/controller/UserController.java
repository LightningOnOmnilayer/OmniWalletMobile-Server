package com.lx.server.walletapi.controller;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.NoSuchPaddingException;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lx.server.bean.ResultTO;
import com.lx.server.enums.EnumFolderURI;
import com.lx.server.pojo.UserClient;
import com.lx.server.utils.RSAEncrypt;
import com.lx.server.utils.Tools;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/user")
@Api(tags = {"用户接口"})
public class UserController extends AbstractController{
	
	@GetMapping("getUserInfo")
	@ApiOperation("获取用户信息")
	public ResultTO getUserInfo() throws NoSuchAlgorithmException {
		UserClient userClient = getUser();
		return ResultTO.newSuccessResult(userClient);
	}
	
	@GetMapping("getUserRSAEncrypt")
	@ApiOperation("获取用户公钥")
	public ResultTO getUserRSAEncrypt() throws NoSuchAlgorithmException, NoSuchPaddingException {
		String publicKey =  RSAEncrypt.genKeyPair(getUserId());
		return ResultTO.newSuccessResult("success",publicKey);
	}
	
	@SuppressWarnings("serial")
	@PostMapping("updateUserNickname")
	@ApiOperation("更新用户昵称")
	public ResultTO updateUserNickname(@RequestParam String nickname) {
		Assert.isTrue(Tools.checkStringExist(nickname), "昵称为空");
		userClientService.update(new HashMap<String,Object>() {{
			put("id", getUserId());
			put("nickname", nickname);
		}});
		
		return ResultTO.newSuccessResult("成功");
	}
	
	@SuppressWarnings("serial")
	@PostMapping("updateUserFace")
	@ApiOperation("更新用户头像")
	public ResultTO updateUserFace(@RequestParam MultipartFile faceFile) {
		Assert.notNull(faceFile, "图片不能为空");
		String faceUrl  = this.uploadImage(EnumFolderURI.Image_userface.value, faceFile);
		userClientService.update(new HashMap<String,Object>() {{
			put("id", getUserId());
			put("faceUrl", faceUrl);
		}});
		
		return ResultTO.newSuccessResult("成功",new HashMap<String,Object>(){{
			put("faceUrl", faceUrl);
		}});
	}
}
