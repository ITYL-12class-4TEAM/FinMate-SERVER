package org.scoula.security.account.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class CustomUser extends User {

    private MemberVO member;

    public CustomUser(MemberVO vo) {
        super(vo.getEmail(), vo.getPassword(),
                AuthorityUtils.createAuthorityList("ROLE_" + vo.getRole()));
        this.member = vo;
    }

    public MemberVO getMember() {
        return member;
    }
}

