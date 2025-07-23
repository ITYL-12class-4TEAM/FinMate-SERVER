package org.scoula.community.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.community.post.domain.PostAttachmentVO;
import org.scoula.community.post.domain.PostStatus;
import org.scoula.community.post.domain.PostVO;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "게시글 리스트 응답 DTO")
public class PostListResponseDTO {

    @ApiModelProperty(value = "게시글 고유 ID", example = "1", position = 1)
    private Long postId;

    @ApiModelProperty(value = "게시판 ID (연관관계)", example = "1", required = true, position = 2)
    private Long boardId;

    @ApiModelProperty(value = "WMTI ID (연관관계)", example = "10", position = 3)
    private Long wmtiId;

    @ApiModelProperty(value = "작성자 회원 ID", example = "100", required = true, position = 4)
    private Long memberId;

    @ApiModelProperty(value = "게시글 제목", example = "게시글 제목 예시", required = true, position = 5)
    private String title;

    @ApiModelProperty(value = "게시글 내용", example = "게시글 내용 예시", required = true, position = 6)
    private String content;

    @ApiModelProperty(value = "게시글 생성 시간", example = "2025-07-23T02:29:35", position = 7)
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "게시글 마지막 수정 시간", example = "2025-07-23T02:45:00", position = 8)
    private LocalDateTime lastUpdated;

    @ApiModelProperty(value = "핫게시판 등록 시간", example = "2025-07-23T03:00:00", position = 9)
    private LocalDateTime hotBoardTime;

    @ApiModelProperty(value = "익명 여부", example = "true", position = 10)
    private boolean isAnonymous;

    @ApiModelProperty(value = "좋아요 수", example = "15", position = 11)
    private int likeCount;

    @ApiModelProperty(value = "댓글 수", example = "5", position = 12)
    private int commentCount;

    @ApiModelProperty(value = "게시글 상태 코드 (NORMAL, DELETED 등)", example = "NORMAL", position = 13)
    private String status;

    @ApiModelProperty(value = "첨부파일 목록", position = 14)
    private List<PostAttachmentVO> attaches;

    @ApiModelProperty(value = "업로드할 파일 목록", dataType = "java.util.List", position = 15, notes = "MultipartFile 리스트")
    private List<MultipartFile> files;

    public static PostListResponseDTO of(PostVO vo) {
        return vo == null ? null : PostListResponseDTO.builder()
                .postId(vo.getPostId())
                .boardId(vo.getBoardId())
                .wmtiId(vo.getWmtiId())
                .memberId(vo.getMemberId())
                .title(vo.getTitle())
                .content(vo.getContent())
                .createdAt(vo.getCreatedAt())
                .lastUpdated(vo.getLastUpdated())
                .hotBoardTime(vo.getHotBoardTime())
                .isAnonymous(vo.isAnonymous())
                .likeCount(vo.getLikeCount())
                .commentCount(vo.getCommentCount())
                .status(vo.getStatus() != null ? vo.getStatus().getCode() : PostStatus.NORMAL.getCode())
                .attaches(vo.getAttaches())
                .build();
    }

    public PostVO toVo() {
        PostStatus postStatusEnum = PostStatus.fromCode(status);
        return PostVO.builder()
                .postId(postId)
                .boardId(boardId)
                .wmtiId(wmtiId)
                .memberId(memberId)
                .title(title)
                .content(content)
                .createdAt(createdAt)
                .lastUpdated(lastUpdated)
                .hotBoardTime(hotBoardTime)
                .isAnonymous(isAnonymous)
                .likeCount(likeCount)
                .commentCount(commentCount)
                .status(postStatusEnum)
                .attaches(attaches)
                .build();
    }
}
