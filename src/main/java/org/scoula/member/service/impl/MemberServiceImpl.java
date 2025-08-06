package org.scoula.member.service.impl;

import lombok.RequiredArgsConstructor;
import org.scoula.member.dto.MemberDTO;
import org.scoula.member.exception.MemberNotFoundException;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.member.service.MemberService;
import org.scoula.response.ResponseCode;
import org.scoula.security.account.domain.MemberVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberMapper memberMapper;

    @Override
    public boolean isEmailAvailable(String email) {
        return memberMapper.selectByEmail(email) == null;
    }

    @Override
    public boolean isNicknameAvailable(String nickname) {
        return memberMapper.selectByNickname(nickname) == null;
    }

    @Override
    public MemberDTO getCurrentUser(String email) {
        MemberVO memberVO = memberMapper.selectByEmail(email);
        if (memberVO == null) {
            throw new MemberNotFoundException(ResponseCode.MEMBER_NOT_FOUND);
        }
        return MemberDTO.of(memberVO);
    }
    @Override
    public String uploadProfileImage(String username, MultipartFile file) {
        validateImageFile(file);
        String fileName = generateFileName(username, file.getOriginalFilename());

        try {
            Path uploadDir = Paths.get("uploads/profiles");
            Files.createDirectories(uploadDir);

            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/uploads/profiles/" + fileName;

            // memberId 변수 오류 수정 - username으로 memberId 조회 후 사용
            Long memberId = memberMapper.findIdByUsername(username);
            memberMapper.updateProfileImage(memberId, imageUrl);

            return imageUrl;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        String contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 5MB를 초과할 수 없습니다.");
        }
    }

    private String generateFileName(String username, String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return username +"_" + System.currentTimeMillis() + extension;
}

    }
