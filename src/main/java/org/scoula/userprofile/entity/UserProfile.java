package org.scoula.userprofile.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfile {
    private Long memberId;     //사용자 고유식별자
    private String username;    //사용자 실명 또는 표기이름
    private Integer age;        //사용자의 나이
    private Long income;        //사용자 월수입
    private Long fixedCost;     //사용자의 월 고정지출
    private Long surplusAmount; //사용자의 월 잉여자산(운용가능자산)
    private String period;      //ENUM ('단기', '중기', '장기')
    private String purpose;     // ENUM('여행', '결혼', '자녀교육', '은퇴준비', '기타')
    private LocalDateTime createdAt;
}
