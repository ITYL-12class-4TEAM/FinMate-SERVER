package org.scoula.security.account.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
public class CustomUser extends User implements OAuth2User {

    private MemberVO member;
    private Map<String, Object> attributes;

    // 일반 로그인용 생성자
    public CustomUser(MemberVO vo) {
        super(vo.getEmail(), vo.getPassword() != null ? vo.getPassword() : "",
                AuthorityUtils.createAuthorityList("ROLE_" + vo.getRole()));
        this.member = vo;
    }

    // OAuth2 로그인용 생성자
    public CustomUser(MemberVO vo, Map<String, Object> attributes) {
        super(vo.getEmail(), "",
                AuthorityUtils.createAuthorityList("ROLE_" + vo.getRole()));
        this.member = vo;
        this.attributes = attributes;
    }

    // OAuth2User 인터페이스 구현
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return member.getEmail();
    }

    public MemberVO getMember() {
        return member;
    }
}
