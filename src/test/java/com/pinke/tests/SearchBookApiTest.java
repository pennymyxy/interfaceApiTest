package com.pinke.tests;

import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pinke.entities.Authorization;
import com.pinke.entities.SearchBookResult;
import com.pinke.utils.HttpPostUtil;

public class SearchBookApiTest {
	

	private HttpPostUtil postUtil;
	private Authorization auth;
	
	
	@BeforeClass
	public void doLogin() {
		
		Gson gson = new GsonBuilder().create();
		postUtil = new HttpPostUtil();
		/*
		 * 1、进行登录的操作，获取cookie，获取token
		 */
		String url = "http://localhost:8080/pink_api/login";
		List<NameValuePair> params = new ArrayList<>(); // 创建准备传送的post请求的body参数集合
		params.add(new BasicNameValuePair("user", "liudao"));
		params.add(new BasicNameValuePair("pwd", "123456"));
		
		String responseStr = postUtil.doPost(url, params, null);
		auth = gson.fromJson(responseStr, Authorization.class);
	}
	
	@DataProvider(name="searchBookParams")
	public  Object[][] getData() throws UnsupportedEncodingException{
		return new Object[][]{
			{"liudao","","","","",5},
			{"liudao","3",null,null,null,1},
			{"liudao",null,"思想",null,null,1},
			{"liudao",null,null,"Mark",null,1},
			{"liudao",null,null,null,"100.00",2}
		};
	}
	
	
	@Test(dataProvider = "searchBookParams")
	public void testSearchBook(String user,String id,String name,String author,String price,int count) {
		
		Gson gson = new GsonBuilder().create();
		/*
		 * 2、进行查询操作，需要提供Authorization头，cookie，以及传送参数user
		 */
		SearchBookResult sbr = null;
		String url = "http://localhost:8080/pink_api/sbook"; // 创建post请求对象
		List<NameValuePair> params = new ArrayList<>(); // 创建准备传送的post请求的body参数集合
		params.add(new BasicNameValuePair("user", user));
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("author", author));
		params.add(new BasicNameValuePair("price", price));
		Header header = new BasicHeader("Authorization", "Bearer "+auth.getToken()); // 准备添加用于token认证的头信息
		List<Header> headers = new ArrayList<>();
		headers.add(header);
		String responseStr = postUtil.doPost(url, params, headers);
		sbr = gson.fromJson(responseStr, SearchBookResult.class);
		assertEquals(sbr.getCount(), count);
	}
}
