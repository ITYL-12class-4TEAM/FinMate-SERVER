package org.scoula.wmti.dto.survey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WMTIRequestDTO {
    private Long userId;   //나중에 JWT에서 추출(?)
    private List<Integer> answers;  //20개문항, 각 1점~5점
}
