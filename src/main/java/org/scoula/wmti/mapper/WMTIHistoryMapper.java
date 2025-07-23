package org.scoula.wmti.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.wmti.dto.survey.WMTIHistoryDTO;
import org.scoula.wmti.entity.WMTIHistory;

import java.util.List;

@Mapper
public interface WMTIHistoryMapper {
    // 설문 이력 저장
    void saveWMTIHistory(WMTIHistory wmtiHistory);

    // 사용자 ID로 설문 이력 조회
    List<WMTIHistory> findAllByMemberId(Long memberId);

    //사용자 ID와 HistoryID로 설문이력 조회
    WMTIHistory findByHistoryId(Long historyId);

    //historyId기준 설문이력 삭제
    int deleteByHistoryId(Long historyId);
}
