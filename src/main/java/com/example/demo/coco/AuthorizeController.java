package com.example.demo.coco;

import javax.servlet.http.HttpServletRequest;

import com.example.demo.coco.mapper.UserMapper;
import com.example.demo.coco.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.coco.dto.AccessTokenDTO;
import com.example.demo.coco.dto.GithubUser;
import com.example.demo.provider.GithubProvider;

import java.util.UUID;

@Controller
public class AuthorizeController {
	
	@Autowired
	private GithubProvider githubProvider;
	@Value("${github.client.id}")
	private String clientId;
	@Value("${github.client.secret}")
	private String clientSecret;
	@Value("${github.redirect.url}")
	private String redirectUrl;


	@Autowired
	private UserMapper userMapper;


	@GetMapping("/callback")
	public String callback (@RequestParam(name ="code") String code,
			               @RequestParam(name ="state") String state,
			               HttpServletRequest request) {
	    AccessTokenDTO accessTokenDTO =new AccessTokenDTO();
	    accessTokenDTO.setClient_id(clientId);
	    accessTokenDTO.setClient_secret(clientSecret);
	    accessTokenDTO.setCode(code);
	    accessTokenDTO.setRedirect_url(redirectUrl);
	    accessTokenDTO.setState(state);
		String accessToken = githubProvider.getAccessToken(accessTokenDTO);
		GithubUser githubUser =githubProvider.getUser(accessToken);
		if(githubUser!=null) {
			User user=new User();
			user.setToken(UUID.randomUUID().toString());
			user.setName(githubUser.getName());
			user.setAccountId(String.valueOf(githubUser.getId()));
			user.setGmtCreate(System.currentTimeMillis());
			user.setGmtModified(user.getGmtCreate());
			userMapper.insert(user);
			request.getSession().setAttribute("user",githubUser);
			return "redirect:/";
			//登录成功 写cookie和session
		}else
		{
			///登录失败，重新登录
			return "redirect:/";

		}
	}
	
}
