package org.scoula.community.postlike.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(description = "게시글 좋아요 토글 응답")
public class PostLikeToggleResponseDTO {

    @ApiModelProperty(value = "좋아요 여부", example = "true", notes = "true: 좋아요 됨, false: 좋아요 취소됨")
    private boolean liked;
}

