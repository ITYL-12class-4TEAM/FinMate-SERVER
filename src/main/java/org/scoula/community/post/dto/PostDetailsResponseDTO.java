package org.scoula.community.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.community.comment.domain.CommentVO;
import org.scoula.community.post.domain.CategoryTag;
import org.scoula.community.post.domain.PostAttachmentVO;
import org.scoula.community.post.domain.PostStatus;
import org.scoula.community.post.domain.PostVO;
import org.scoula.community.post.domain.ProductTag;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "게시글 상세 조회 응답 DTO")
public class PostDetailsResponseDTO {

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

    @ApiModelProperty(value = "카테고리 태그 이름", example = "RECOMMEND", position = 17)
    private String categoryTag;

    @ApiModelProperty(value = "상품 태그 이름", example = "DEPOSIT", position = 17)
    private String productTag;

    @ApiModelProperty(value = "댓글 목록", position = 18)
    private List<CommentVO> comments;

//    @ApiModelProperty(value = "첨부파일 목록", position = 14)
//    private List<PostAttachmentVO> attaches;
//
//    @ApiModelProperty(value = "첨부파일 목록", position = 20)
//    List<MultipartFile> files = new ArrayList<>();;
//

    public static PostDetailsResponseDTO of(PostVO vo, List<CommentVO> comments) {
        return vo == null ? null : PostDetailsResponseDTO.builder()
                .postId(vo.getPostId())
                .boardId(vo.getBoardId())
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
                .categoryTag(vo.getCategoryTag().getCode())
                .productTag(vo.getProductTag().getCode())
                .commentCount(vo.getCommentCount())
                .comments(comments)
//                .attaches(vo.getAttachments())
                .build();
    }

    public PostVO toVo() {
        PostStatus postStatusEnum = PostStatus.fromCode(status);
        CategoryTag categoryTagEnum = CategoryTag.fromCode(categoryTag);
        ProductTag productTagEnum = ProductTag.fromCode(productTag);

        return PostVO.builder()
                .postId(postId)
                .boardId(boardId)
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
                .categoryTag(categoryTagEnum)
                .productTag(productTagEnum)
//                .attachments(attaches)
                .build();
    }
}
