package com.example.whoami.oauth2;

import java.util.Map;

public class KakaoOAuth2User extends OAuth2UserInfo {

    private long id;

    public KakaoOAuth2User(Map<String, Object> attributes) {
        super((Map<String, Object>) attributes.get("kakao_account"));
        this.id = (long) attributes.get("id");
    }

    @Override
    public String getId() {
        return Long.toString(this.id);
    }
}
