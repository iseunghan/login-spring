package me.isunghan.loginspring.security;

import me.isunghan.loginspring.domain.Member;

import java.io.Serializable;

/**
 * 인증된 사용자를 저장하기 위한 DTO, HttpSession에 저장하기 위해 직렬화를 해줍니다.
 */
public class SessionMemer implements Serializable {
    private String name;
    private String email;
    private String picture;

    public SessionMemer(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
        this.picture = member.getPicture();
    }

    public SessionMemer() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
