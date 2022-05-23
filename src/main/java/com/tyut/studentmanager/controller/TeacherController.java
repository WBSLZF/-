package com.tyut.studentmanager.controller;

import com.tyut.studentmanager.domain.Teacher;
import com.tyut.studentmanager.service.TeacherService;
import com.tyut.studentmanager.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;


    @RequestMapping("/teacher_list")
    public String teacherList(){
        return "/teacher/teacherList";
    }

    /**
     * 异步加载老师数据列表
     * @param page
     * @param rows
     * @param teacherName
     * @param clazzid
     * @param from
     * @param session
     * @return
     */
    @PostMapping("/getTeacherList")
    @ResponseBody
    public Object getTeacherList(@RequestParam(value = "page", defaultValue = "1")Integer page,
                                 @RequestParam(value = "rows", defaultValue = "100")Integer rows,
                                 String teacherName,
                                 @RequestParam(value = "clazzid", defaultValue = "0")String clazzid, String from, HttpSession session){
        Map<String,Object> paramMap = new HashMap();
        paramMap.put("pageno",page);
        paramMap.put("pagesize",rows);
        if(!StringUtils.isEmpty(teacherName))  paramMap.put("username",teacherName);
        if(!clazzid.equals("0"))  paramMap.put("clazzid",clazzid);

        //判断是老师还是学生权限
        Teacher teacher = (Teacher) session.getAttribute(Const.TEACHER);
        if(!StringUtils.isEmpty(teacher)){
            //是老师权限，只能查询自己的信息
            paramMap.put("teacherid",teacher.getId());
        }

        PageBean<Teacher> pageBean = teacherService.queryPage(paramMap);
        if(!StringUtils.isEmpty(from) && from.equals("combox")){
            return pageBean.getDatas();
        }else{
            Map<String,Object> result = new HashMap();
            result.put("total",pageBean.getTotalsize());
            result.put("rows",pageBean.getDatas());
            return result;
        }
    }

    /**
     * 删除教师
     * @param data
     * @return
     */
    @PostMapping("/deleteTeacher")
    @ResponseBody
    public AjaxResult deleteTeacher(Data data){
        AjaxResult ajaxResult = new AjaxResult();
        try {

            int count = teacherService.deleteTeacher(data.getIds());
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("全部删除成功");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("删除失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("删除失败");
        }
        return ajaxResult;
    }

    /**
     * 添加教师
     * @param teacher
     * @return
     * @throws IOException
     */
    @RequestMapping("/addTeacher")
    @ResponseBody
    public AjaxResult addTeacher(Teacher teacher) throws IOException {

        AjaxResult ajaxResult = new AjaxResult();
        teacher.setSn(SnGenerateUtil.generateTeacherSn(teacher.getClazzId()));

        //保存学生信息到数据库
        try{
            int count = teacherService.addTeacher(teacher);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("保存成功");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("保存失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("保存失败");
        }

        ajaxResult.setSuccess(true);
        return ajaxResult;
    }

    @PostMapping("/editTeacher")
    @ResponseBody
    public AjaxResult editTeacher(Teacher teacher){
        AjaxResult ajaxResult = new AjaxResult();



        try{
            int count = teacherService.editTeacher(teacher);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("修改成功");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("修改失败");
            }
        }catch(Exception e){
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("修改失败");
        }
        return ajaxResult;
    }
}
