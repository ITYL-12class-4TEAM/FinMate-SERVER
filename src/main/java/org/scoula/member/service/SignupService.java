package org.scoula.member.service;

import lombok.RequiredArgsConstructor;
import org.scoula.member.dto.RegisterDTO;
import org.scoula.member.dto.SignupResponseDTO;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.security.account.domain.MemberVO;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@RequiredArgsConstructor
public class SignupService {
    private final MemberMapper memberMapper;


    public boolean isValidPassword(String password) {
        // 8자 이상, 영문/숫자/특수문자 각각 1개 이상
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-]).{8,}$";
        return password != null && password.matches(pattern);
    }

    public SignupResponseDTO register(RegisterDTO dto) {
        Date birthDate = null;
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        try {
            if (dto.getBirthDate() != null && !dto.getBirthDate().isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                birthDate = sdf.parse(dto.getBirthDate());
            }
        } catch (Exception e) {
            return new SignupResponseDTO(false, "생년월일 형식이 올바르지 않습니다.");
        }


        // 비밀번호 확인
        if (!isValidPassword(dto.getPassword())) {
            return new SignupResponseDTO(false, "비밀번호는 8자 이상, 영문/숫자/특수문자를 포함해야 합니다.");
        }

        // 비밀번호 확인
        if (!dto.getPassword().equals(dto.getPasswordCheck())) {
            return new SignupResponseDTO(false, "비밀번호가 일치하지 않습니다.");
        }
        if (!Boolean.TRUE.equals(dto.getTermsRequired1()) || !Boolean.TRUE.equals(dto.getTermsRequired2())) {
            return new SignupResponseDTO(false, "필수 약관에 동의해 주세요.");
        }
        if (dto.getUsername() == null || dto.getUsername().isEmpty() ||
                dto.getPassword() == null || dto.getPassword().isEmpty() ||
                dto.getPasswordCheck() == null || dto.getPasswordCheck().isEmpty() ||
                dto.getNickname() == null || dto.getNickname().isEmpty() ||
                dto.getEmail() == null || dto.getEmail().isEmpty() ||
                dto.getPhoneNumber() == null || dto.getPhoneNumber().isEmpty() ||
                dto.getBirthDate() == null || dto.getBirthDate().isEmpty() ||
                dto.getGender() == null || dto.getGender().isEmpty())
                 {
            return new SignupResponseDTO(false, "모든 필수 입력값을 입력해 주세요.");
        }

        // MemberVO 생성 및 저장
        MemberVO member = MemberVO.builder()
                .username(dto.getUsername())
                .password(encoder.encode(dto.getPassword())) // 실제로는 암호화 필요
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .birthDate(birthDate) // dto에서 birthDate를 받아옴
                .gender(dto.getGender())
                .receivePushNotification(dto.getReceive_push_notification() != null && dto.getReceive_push_notification().equalsIgnoreCase("true"))
                .build();

        memberMapper.insert(member);

        return new SignupResponseDTO(true, "회원가입이 완료되었습니다.");
    }
}