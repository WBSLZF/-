package com.tyut.studentmanager.mapper;

import com.tyut.studentmanager.domain.Attendance;

import java.util.List;
import java.util.Map;

public interface AttendanceMapper {
    List<Attendance> queryList(Map<String, Object> paramMap);

    Integer queryCount(Map<String, Object> paramMap);

    int addAtendance(Attendance attendance);

    Attendance isAttendance(Attendance attendance);

    int deleteAttendance(Integer id);
}
