package com.tyut.studentmanager.service.Impl;

import com.tyut.studentmanager.domain.Clazz;
import com.tyut.studentmanager.mapper.ClazzMapper;
import com.tyut.studentmanager.service.ClazzService;
import com.tyut.studentmanager.util.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
public class ClazzServiceImpl implements ClazzService {

    @Autowired
    private ClazzMapper clazzMapper;

    @Override
    public PageBean<Clazz> queryPage(Map<String, Object> paramMap) {
        PageBean<Clazz> pageBean = new PageBean<>((Integer) paramMap.get("pageno"),(Integer) paramMap.get("pagesize"));

        Integer startIndex = pageBean.getStartIndex();
        paramMap.put("startIndex",startIndex);
        List<Clazz> datas = clazzMapper.queryList(paramMap);
        pageBean.setDatas(datas);

        Integer totalsize = clazzMapper.queryCount(paramMap);
        pageBean.setTotalsize(totalsize);
        return pageBean;
    }

    @Override
    public int addClazz(Clazz clazz) {
        return clazzMapper.addClazz(clazz);
    }

    @Override
    @Transactional
    public int deleteClazz(List<Integer> ids) {
        return clazzMapper.deleteClazz(ids);
    }

    @Override
    public int editClazz(Clazz clazz) {
        return clazzMapper.editClazz(clazz);
    }

    @Override
    public Clazz findByName(String clazzName) {
        return clazzMapper.findByName(clazzName);
    }

}
