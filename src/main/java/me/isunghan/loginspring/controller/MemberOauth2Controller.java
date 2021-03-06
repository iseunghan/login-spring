package me.isunghan.loginspring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.isunghan.loginspring.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Controller
@RequestMapping(value = "/login/oauth2/code")
public class MemberOauth2Controller {

    @GetMapping(value = "/kakao")
    public String kakaoOauthRedirect(@RequestParam String code, Model model,
                                     @Value("${spring.security.oauth2.client.registration.kakao.client-id}") String client_id,
                                     @Value("${spring.security.oauth2.client.registration.kakao.client-secret}") String client_secret,
                                     @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}") String authorization_grant_type,
                                     @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}") String redirect_uri) {

        // 카카오에 POST방식으로 key=value 데이터를 요청함. RestTemplate를 사용하면 요청을 편하게 할 수 있다.
        RestTemplate rt = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", authorization_grant_type);
        params.add("client_id", client_id);
        params.add("client_secret", client_secret);
        params.add("redirect_uri", redirect_uri);
        params.add("code", code);

        // HttpHeader와 HttpBody를 HttpEntity에 담기 (why? rt.exchange에서 HttpEntity객체를 받게 되어있다.)
        HttpEntity<MultiValueMap<String, String>> kakaoRequest = new HttpEntity<>(params, headers);

        // HTTP 요청 - POST방식 - response 응답 받기
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoRequest,
                String.class
        );

        // ObjectMapper로 응답받은 토큰 정보들을 KakaoOauthParams 클래스에 담아준다
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoOauthParams kakaoOauthParams = null;
        try {
            kakaoOauthParams = objectMapper.readValue(response.getBody(), KakaoOauthParams.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // 여기서부터, Access Token을 이용해서 사용자 정보를 응답 받는 코드이다.
        HttpHeaders headers1 = new HttpHeaders();
        headers1.add("Authorization", "Bearer " + kakaoOauthParams.getAccess_token());
        headers1.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<HttpHeaders> kakaoRequest1 = new HttpEntity<>(headers1);

        ResponseEntity<String> profileResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoRequest1,
                String.class
        );

        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(profileResponse.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        model.addAttribute("image", kakaoProfile.getKakao_account().getProfile().getProfile_image_url());
        model.addAttribute("name", kakaoProfile.getKakao_account().getProfile().getNickname());
        return "/login/login-success";
    }

    @GetMapping("/google")
    public String googleOAuthRedirect(@RequestParam String code, Model model,
                                      @Value("${spring.security.oauth2.client.registration.google.client-id") String client_id,
                                      @Value("${spring.security.oauth2.client.registration.google.client-secret}") String client_secret,
                                      @Value("${spring.security.oauth2.client.registration.google.authorization_code}") String authorization_grant_type,
                                      @Value("${spring.security.oauth2.client.registration.google.redirect_uri}") String redirect_uri) {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", client_id);
        params.add("client_secret", client_secret);
        params.add("code", code);
        params.add("grant_type", authorization_grant_type);
        params.add("redirect_uri", redirect_uri);

        HttpEntity<MultiValueMap<String, String>> accessTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                accessTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        GoogleOauthParams googleOauthParams = null;
        try {
            googleOauthParams = objectMapper.readValue(accessTokenResponse.getBody(), GoogleOauthParams.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // 여기서부터, 프로필 정보 얻어오는 요청
        HttpHeaders headers1 = new HttpHeaders();
        headers1.add("Authorization", "Bearer " + googleOauthParams.getAccess_token());

        HttpEntity profileRequest = new HttpEntity(headers1);

        ResponseEntity<String> profileResponse = rt.exchange(
                "https://oauth2.googleapis.com/tokeninfo?id_token=" + googleOauthParams.getId_token(),
                HttpMethod.GET,
                profileRequest,
                String.class
        );

        HashMap<String, String> profileParams = null;
        try {
            profileParams = objectMapper.readValue(profileResponse.getBody(), HashMap.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        model.addAttribute("name", profileParams.get("name"));
        model.addAttribute("image", profileParams.get("picture"));

        return "/login/login-success";
    }

    @GetMapping("/naver")
    public String naverOAuthRedirect(@RequestParam String code, @RequestParam String state, Model model,
                                     @Value("${spring.security.oauth2.client.registration.naver.client-id}") String client_id,
                                     @Value("${spring.security.oauth2.client.registration.naver.client-secret}") String client_secret,
                                     @Value("${spring.security.oauth2.client.registration.naver.authorization-grant-type}") String authorization_grant_type) {

        // RestTemplate 인스턴스 생성
        RestTemplate rt = new RestTemplate();

        HttpHeaders accessTokenHeaders = new HttpHeaders();
        accessTokenHeaders.add("Content-type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> accessTokenParams = new LinkedMultiValueMap<>();
        accessTokenParams.add("grant_type", authorization_grant_type);
        accessTokenParams.add("client_id", client_id);
        accessTokenParams.add("client_secret", client_secret);
        accessTokenParams.add("code" , code);
        accessTokenParams.add("state" , state);

        HttpEntity<MultiValueMap<String, String>> accessTokenRequest = new HttpEntity<>(accessTokenParams, accessTokenHeaders);

        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                accessTokenRequest,
                String.class
        );

        // json->객체 변환을 위한 ObjectMapper 생성
        ObjectMapper objectMapper = new ObjectMapper();
        NaverOauthParams naverOauthParams = null;
        try {
            naverOauthParams = objectMapper.readValue(accessTokenResponse.getBody(), NaverOauthParams.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpHeaders profileRequestHeader = new HttpHeaders();
        profileRequestHeader.add("Authorization", "Bearer " + naverOauthParams.getAccess_token());

        HttpEntity<HttpHeaders> profileHttpEntity = new HttpEntity<>(profileRequestHeader);

        ResponseEntity<String> profileResponse = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                profileHttpEntity,
                String.class
        );

        NaverProfile naverProfile = null;
        try {
             naverProfile = objectMapper.readValue(profileResponse.getBody(), NaverProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        model.addAttribute("name", naverProfile.getResponse().getName());
        model.addAttribute("image", naverProfile.getResponse().getProfile_image());

        return "/login/login-success";
    }
}
