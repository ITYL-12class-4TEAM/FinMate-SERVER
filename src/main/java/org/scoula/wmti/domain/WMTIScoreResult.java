package org.scoula.wmti.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WMTIScoreResult {
    private double aScore;
    private double pScore;
    private double mScore;
    private double lScore;
}

