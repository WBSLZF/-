package com.tyut.studentmanager.service;

import com.tyut.studentmanager.domain.Score;
import com.tyut.studentmanager.domain.ScoreStats;
import com.tyut.studentmanager.util.PageBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public interface ScoreService {
    PageBean<Score> queryPage(Map<String, Object> paramMap);

    boolean isScore(Score score);

    int addScore(Score score);

    int editScore(Score score);

    int deleteScore(Integer id);

    List<Score> getAll(Score score);

    ScoreStats getAvgStats(Integer courseid);
}
