package org.scoula.community.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.community.post.domain.PostStatus;
import org.scoula.community.post.domain.PostVO;
import org.scoula.community.post.domain.ProductTag;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "게시글 생성 요청 DTO")
public class PostUpdateRequestDTO {

    @ApiModelProperty(value = "게시판 ID (연관관계)", example = "1", required = true, position = 2)
    private Long boardId;

    @ApiModelProperty(value = "게시글 제목", example = "게시글 제목 예시", required = true, position = 5)
    private String title;

    @ApiModelProperty(value = "게시글 내용", example = "게시글 내용 예시", required = true, position = 6)
    private String content;

    @ApiModelProperty(value = "익명 여부", example = "true", position = 10)
    private boolean isAnonymous;

    @ApiModelProperty(value = "게시글 상태 코드 (NORMAL, DELETED 등)", example = "NORMAL", position = 13)
    private String status;

    @ApiModelProperty(value = "상품 태그 이름", example = "DEPOSIT", position = 17)
    private String productTag;

//    @ApiModelProperty(value = "첨부파일 목록", position = 14)
//    private List<PostAttachmentVO> attaches;
//
//    @ApiModelProperty(value = "첨부파일 목록", position = 20)
//    List<MultipartFile> files = new ArrayList<>();;

    public static PostUpdateRequestDTO of(PostVO vo) {
        return vo == null ? null : PostUpdateRequestDTO.builder()
                .boardId(vo.getBoardId())
                .title(vo.getTitle())
                .content(vo.getContent())
                .isAnonymous(vo.isAnonymous())
                .status(vo.getStatus() != null ? vo.getStatus().getCode() : PostStatus.NORMAL.getCode())
                .productTag(vo.getProductTag().getCode())
//                .attaches(vo.getAttachments())
                .build();
    }

    public PostVO toVo() {
        PostStatus postStatusEnum = PostStatus.fromCode(status);
        ProductTag productTagEnum = ProductTag.fromCode(productTag);

        return PostVO.builder()
                .boardId(boardId)
                .title(title)
                .content(content)
                .isAnonymous(isAnonymous)
                .status(postStatusEnum)
                .productTag(productTagEnum)
//                .attachments(attaches)
                .build();
    }
}
