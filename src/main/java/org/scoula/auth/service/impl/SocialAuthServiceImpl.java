package org.scoula.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.auth.dto.request.SocialSignupCompleteRequest;
import org.scoula.auth.exception.AuthenticationException;
import org.scoula.auth.service.SocialAuthService;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.response.ResponseCode;
import org.scoula.security.account.domain.MemberVO;
import org.scoula.security.util.JwtProcessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SocialAuthServiceImpl implements SocialAuthService {

    private final MemberMapper memberMapper;
    private final JwtProcessor jwtProcessor;

    @Override
    public void completeSocialSignup(SocialSignupCompleteRequest request, String token) {
        if (token == null || token.isEmpty()) {
            throw new AuthenticationException(ResponseCode.INVALID_TOKEN);
        }

        String jwt = token.replace("Bearer ", "");
        if (jwt.isEmpty()) {
            throw new AuthenticationException(ResponseCode.INVALID_TOKEN);
        }

        if (!jwtProcessor.validateToken(jwt)) {
            throw new AuthenticationException(ResponseCode.INVALID_TOKEN);
        }

        String username = jwtProcessor.getUsername(jwt);
        if (username == null || username.isEmpty()) {
            throw new AuthenticationException(ResponseCode.INVALID_TOKEN);
        }

        MemberVO member = memberMapper.findByEmail(username);
        if (member == null) {
            throw new AuthenticationException(ResponseCode.MEMBER_NOT_FOUND);
        }

        if (member.getMemberId() == null) {
            throw new AuthenticationException(ResponseCode.SERVER_ERROR);
        }

        if (!Boolean.TRUE.equals(member.getIsNewMember())) {
            throw new AuthenticationException(ResponseCode.SERVER_ERROR);
        }

        if (request == null ||
                request.getNickname() == null || request.getNickname().isEmpty() ||
                request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty() ||
                request.getBirthDate() == null ||
                request.getGender() == null || request.getGender().isEmpty()) {
            throw new AuthenticationException(ResponseCode.MISSING_REQUIRED_FIELDS);
        }

        MemberVO existingMember = memberMapper.selectByNickname(request.getNickname());
        if (existingMember != null && !existingMember.getMemberId().equals(member.getMemberId())) {
            throw new AuthenticationException(ResponseCode.SERVER_ERROR);
        }

        MemberVO updatedMember = MemberVO.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(request.getNickname())
                .username(member.getUsername())
                .profileImage(member.getProfileImage())
                .socialType(member.getSocialType())
                .socialId(member.getSocialId())
                .role(member.getRole())
                .level(member.getLevel())
                .status(member.getStatus())
                .phoneNumber(request.getPhoneNumber())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .receivePushNotification(Boolean.TRUE.equals(request.getReceivePushNotification()))
                .isNewMember(false)
                .createdAt(member.getCreatedAt())
                .updatedAt(new Date())
                .build();

        memberMapper.completeSocialSignup(updatedMember);

        log.info("소셜 회원가입 완료: {}", member.getEmail());
    }

    @Override
    public boolean checkNewMember(String token) {
        if (token == null || token.isEmpty()) {
            throw new AuthenticationException(ResponseCode.INVALID_TOKEN);
        }

        String jwt = token.replace("Bearer ", "");
        if (jwt.isEmpty()) {
            throw new AuthenticationException(ResponseCode.INVALID_TOKEN);
        }

        if (!jwtProcessor.validateToken(jwt)) {
            throw new AuthenticationException(ResponseCode.INVALID_TOKEN);
        }

        String username = jwtProcessor.getUsername(jwt);
        if (username == null || username.isEmpty()) {
            throw new AuthenticationException(ResponseCode.INVALID_TOKEN);
        }

        MemberVO member = memberMapper.findByEmail(username);
        if (member == null) {
            throw new AuthenticationException(ResponseCode.MEMBER_NOT_FOUND);
        }

        if (member.getIsNewMember() == null) {
            throw new AuthenticationException(ResponseCode.SERVER_ERROR);
        }

        return Boolean.TRUE.equals(member.getIsNewMember());
    }
}