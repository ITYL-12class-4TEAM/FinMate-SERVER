package org.scoula.common.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
public class UploadFiles {

    /**
     * 파일을 지정된 디렉토리에 업로드하는 메서드
     * @param baseDir 기본 저장 디렉토리
     * @param part 업로드된 파일 객체
     * @return 저장된 파일의 전체 경로
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    public static String upload(String baseDir, MultipartFile part) throws IOException {
        // 기본 디렉토리 존재 여부 확인 및 생성
        File base = new File(baseDir);
        if (!base.exists()) {
            base.mkdirs();  // 중간 디렉토리까지 모두 생성
        }

        // 원본 파일명 획득
        String fileName = part.getOriginalFilename();

        // 고유한 파일명으로 대상 파일 생성
        File dest = new File(baseDir, UploadFileName.getUniqueName(fileName));

        // 업로드된 파일을 지정된 경로로 이동
        part.transferTo(dest);

        // 저장된 파일의 전체 경로 반환
        return dest.getPath();
    }

    /**
     * 파일 크기를 사용자 친화적 형태로 변환
     * @param size 바이트 단위 파일 크기
     * @return 포맷된 문자열 (예: 1.2 MB)
     */
    public static String getFormatSize(Long size) {
        if (size <= 0) return "0";

        final String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#")
                .format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * 파일 다운로드 처리 (기존 메서드)
     * @param response HTTP 응답 객체
     * @param file 다운로드할 파일
     * @param orgName 원본 파일명 (다운로드 시 표시될 이름)
     * @throws Exception
     */
    public static void download(HttpServletResponse response, File file, String orgName)
            throws Exception {

        /* *** 응답 헤더 설정 *** */

        // application/download
        // - 범용 다운로드 타입을 나타내는 MIME TYPE
        // - 브라우저가 미리보기를 시도하지 않고 다운로드 시도
        response.setContentType("application/download");

        // Content-Length
        // - 브라우저에게 전송될 데이터의 크기를 미리 알려주는 중요 응답 헤더
        // - 다운로드 진행율 표시 가능, 연결 최적화(HTTP Keep-Alive), 브라우저 메모리 최적화
        response.setContentLength((int) file.length());

        // 한글 파일명 인코딩 (UTF-8)
        String filename = URLEncoder.encode(orgName, "UTF-8");

        // Content-disposition
        // - 브라우저가 응답을 어떻게 처리하지 지정하는 HTTP 헤더

        // attachment;filename="파일명"
        // - 지정된 "파일명"으로 다운로드 처리를 지시
        response.setHeader("Content-disposition",
                "attachment;filename=\"" + filename + "\"");

        // 파일 스트림을 응답으로 전송
        try (OutputStream os = response.getOutputStream();
             BufferedOutputStream bos = new BufferedOutputStream(os)) {

            Files.copy(Paths.get(file.getPath()), bos);
        }
    }

    /**
     * ResponseEntity를 사용한 파일 다운로드 (Spring Boot 권장 방식)
     * @param filePath 파일 경로
     * @param originalFileName 원본 파일명
     * @return ResponseEntity<Resource>
     * @throws Exception
     */
    public static ResponseEntity<Resource> downloadFile(String filePath, String originalFileName)
            throws Exception {

        Path path = Paths.get(filePath);
        Resource resource = new FileSystemResource(path);

        if (!resource.exists()) {
            log.warn("다운로드 요청된 파일이 존재하지 않음: {}", filePath);
            return ResponseEntity.notFound().build();
        }

        // 한글 파일명 인코딩
        String encodedFilename = URLEncoder.encode(originalFileName, "UTF-8")
                .replaceAll("\\+", "%20"); // 공백 처리

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(resource.contentLength());
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + encodedFilename + "\"");

        log.info("파일 다운로드: {} -> {}", originalFileName, filePath);
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    /**
     * 파일 삭제
     * @param filePath 삭제할 파일 경로
     * @return 삭제 성공 여부
     */
    public static boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("파일 삭제 성공: {}", filePath);
                return true;
            } else {
                log.warn("삭제하려는 파일이 존재하지 않음: {}", filePath);
                return true; // 파일이 없어도 성공으로 처리
            }
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", filePath, e);
            return false;
        }
    }

    /**
     * 안전한 파일명 생성 (보안을 위해 경로 탐색 방지)
     * @param filename 원본 파일명
     * @return 안전한 파일명
     */
    public static String sanitizeFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "unknown_file";
        }

        // 경로 구분자 제거 및 특수문자 처리
        return filename.replaceAll("[/\\\\:*?\"<>|]", "_")
                .replaceAll("\\.{2,}", "_") // 연속된 점 제거
                .trim();
    }

    /**
     * 디렉토리 생성
     * @param dirPath 생성할 디렉토리 경로
     * @return 생성 성공 여부
     */
    public static boolean createDirectory(String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("디렉토리 생성 성공: {}", dirPath);
            }
            return true;
        } catch (Exception e) {
            log.error("디렉토리 생성 실패: {}", dirPath, e);
            return false;
        }
    }

    /**
     * 파일 존재 여부 확인
     * @param filePath 확인할 파일 경로
     * @return 파일 존재 여부
     */
    public static boolean exists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * 파일 크기 조회
     * @param filePath 파일 경로
     * @return 파일 크기 (바이트)
     */
    public static long getFileSize(String filePath) {
        try {
            return Files.size(Paths.get(filePath));
        } catch (Exception e) {
            log.error("파일 크기 조회 실패: {}", filePath, e);
            return 0;
        }
    }

    /**
     * MIME 타입 추측
     * @param filePath 파일 경로
     * @return MIME 타입
     */
    public static String getMimeType(String filePath) {
        try {
            String mimeType = Files.probeContentType(Paths.get(filePath));
            return mimeType != null ? mimeType : "application/octet-stream";
        } catch (Exception e) {
            log.error("MIME 타입 추측 실패: {}", filePath, e);
            return "application/octet-stream"; // 기본값
        }
    }

    /**
     * 파일 확장자 추출
     * @param filename 파일명
     * @return 확장자 (점 포함, 예: ".jpg")
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex);
        }
        return "";
    }

    /**
     * 파일명에서 확장자 제거
     * @param filename 파일명
     * @return 확장자가 제거된 파일명
     */
    public static String getFileNameWithoutExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return filename;
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(0, lastDotIndex);
        }
        return filename;
    }
}