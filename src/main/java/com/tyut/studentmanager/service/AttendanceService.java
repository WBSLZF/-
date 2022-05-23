package com.tyut.studentmanager.service;

import com.tyut.studentmanager.domain.Attendance;
import com.tyut.studentmanager.util.PageBean;

import java.util.Map;

public interface AttendanceService {
    PageBean<Attendance> queryPage(Map<String, Object> paramMap);

    boolean isAttendance(Attendance attendance);

    int addAtendance(Attendance attendance);

    int deleteAttendance(Integer id);
}
