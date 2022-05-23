package com.tyut.studentmanager.mapper;

import com.tyut.studentmanager.domain.Score;
import com.tyut.studentmanager.domain.ScoreStats;

import java.util.List;
import java.util.Map;

public interface ScoreMapper {

    List<Score> queryList(Map<String, Object> paramMap);

    Integer queryCount(Map<String, Object> paramMap);

    int addScore(Score score);

    Score isScore(Score score);

    int editScore(Score score);

    int deleteScore(Integer id);

    List<Score> getAll(Score score);

    ScoreStats getAvgStats(Integer courseid);
}
