package com.example.demo.provider;
import  okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import com.example.demo.coco.dto.AccessTokenDTO;
import com.example.demo.coco.dto.GithubUser;
@Component
public class GithubProvider {
	 
	@SuppressWarnings("deprecation")
	public String getAccessToken(AccessTokenDTO accessTokenDTO) {
    MediaType mediaType  = MediaType.get("application/json; charset=utf-8");
	OkHttpClient client = new OkHttpClient();
	
	  RequestBody body =RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
	  Request request = new Request.Builder()
	      .url("https://github.com/login/oauth/access_token")
	      .post(body)
	      .build();
	  try (Response response = client.newCall(request).execute()) {
	    String string=response.body().string();
	    String[] split=string.split("&");
	    String tokenstr =split[0];
	    String token=tokenstr.split("=")[1];
	    return token;
	  } catch(Exception e) {
	  }
	  return null;
 	   
	}
	public GithubUser getUser(String accessToken) {
		OkHttpClient client =new OkHttpClient();
		Request request =new Request.Builder()
				.url("https://api.github.com/user?access_token="+accessToken)
				.build();
		try {
			Response reponse=client.newCall(request).execute();
			String string =reponse.body().string();
			GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
			return githubUser;
		}catch(IOException e) {
		}
		return null;
		
	}
}
