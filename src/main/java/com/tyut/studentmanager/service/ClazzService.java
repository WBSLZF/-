package com.tyut.studentmanager.service;

import com.tyut.studentmanager.domain.Clazz;
import com.tyut.studentmanager.util.PageBean;

import java.util.List;
import java.util.Map;


public interface ClazzService {
    PageBean<Clazz> queryPage(Map<String, Object> paramMap);

    int addClazz(Clazz clazz);

    int deleteClazz(List<Integer> ids);

    int editClazz(Clazz clazz);

    Clazz findByName(String clazzName);

}
