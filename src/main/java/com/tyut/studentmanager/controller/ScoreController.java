package com.tyut.studentmanager.controller;

import com.tyut.studentmanager.domain.Score;
import com.tyut.studentmanager.domain.ScoreStats;
import com.tyut.studentmanager.domain.Student;
import com.tyut.studentmanager.service.CourseService;
import com.tyut.studentmanager.service.ScoreService;
import com.tyut.studentmanager.service.SelectedCourseService;
import com.tyut.studentmanager.service.StudentService;
import com.tyut.studentmanager.util.AjaxResult;
import com.tyut.studentmanager.util.Const;
import com.tyut.studentmanager.util.PageBean;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Controller
@RequestMapping("/score")
public class ScoreController {
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private SelectedCourseService selectedCourseService;


    @GetMapping("/score_list")
    public String scoreList(){
        return "/score/scoreList";
    }

    @RequestMapping("/getScoreList")
    @ResponseBody
    public Object getScoreList(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "100")Integer rows,
                               @RequestParam(value = "studentid", defaultValue = "0")String studentid,
                               @RequestParam(value = "courseid", defaultValue = "0")String courseid,
                               String from, HttpSession session){
        Map<String,Object> paramMap = new HashMap();
        paramMap.put("pageno",page);
        paramMap.put("pagesize",rows);
        if(!studentid.equals("0"))  paramMap.put("studentid",studentid);
        if(!courseid.equals("0"))  paramMap.put("courseid",courseid);

        //?????????????????????????????????
        Student student = (Student) session.getAttribute(Const.STUDENT);
        if(!StringUtils.isEmpty(student)){
            //?????????????????????????????????????????????
            paramMap.put("studentid",student.getId());
        }
        PageBean<Score> pageBean = scoreService.queryPage(paramMap);
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
     * ????????????
     */
    @PostMapping("/addScore")
    @ResponseBody
    public AjaxResult addScore(Score score){
        AjaxResult ajaxResult = new AjaxResult();
        //???????????????????????????
        if(scoreService.isScore(score)){
            //true????????????
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("?????????????????????????????????");
        }else{
            int count = scoreService.addScore(score);
            if(count > 0){
                //????????????
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("????????????");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("??????????????????????????????");
            }
        }
        return ajaxResult;
    }


    /**
     * ??????????????????
     */
    @PostMapping("/editScore")
    @ResponseBody
    public AjaxResult editScore(Score score){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            int count = scoreService.editScore(score);
            if(count > 0){
                //????????????
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("????????????");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("??????????????????????????????");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("??????????????????????????????");
        }
        return ajaxResult;
    }

    /**
     * ??????????????????
     */
    @PostMapping("/deleteScore")
    @ResponseBody
    public AjaxResult deleteScore(Integer id){
        AjaxResult ajaxResult = new AjaxResult();
        try {
            int count = scoreService.deleteScore(id);
            if(count > 0){
                ajaxResult.setSuccess(true);
                ajaxResult.setMessage("????????????");
            }else{
                ajaxResult.setSuccess(false);
                ajaxResult.setMessage("??????????????????????????????");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ajaxResult.setSuccess(false);
            ajaxResult.setMessage("??????????????????????????????");
        }
        return ajaxResult;
    }

    /**
     * ??????xlsx??? ??????????????????
     */
    @PostMapping("/importScore")
    @ResponseBody
    public void importScore(@RequestParam("importScore") MultipartFile importScore, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        try {
            InputStream inputStream = importScore.getInputStream();
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheetAt = xssfWorkbook.getSheetAt(0);
            int count = 0;
            String errorMsg = "";
            for(int rowNum = 1; rowNum <= sheetAt.getLastRowNum(); rowNum++){
                XSSFRow row = sheetAt.getRow(rowNum); // ?????????rowNum???
                //???0???
                XSSFCell cell = row.getCell(0); // ?????????rowNum?????????0??? ????????????rowNum???0???
                if(cell == null){
                    errorMsg += "???" + rowNum + "??????????????????\n";
                    continue;
                }
                //???1???
                cell = row.getCell(1);
                if(cell == null){
                    errorMsg += "???" + rowNum + "??????????????????\n";
                    continue;
                }
                //???2???
                cell = row.getCell(2);
                if(cell == null){
                    errorMsg += "???" + rowNum + "??????????????????\n";
                    continue;
                }
                double scoreValue = cell.getNumericCellValue();
                //???3???
                cell = row.getCell(3);
                String remark = null;
                if(cell != null){
                    remark = cell.getStringCellValue();
                }

                //???????????????????????????id,???????????????
                // 1)?????????????????????id
                int studentId = studentService.findByName(row.getCell(0).getStringCellValue());
                int courseId = courseService.findByName(row.getCell(1).getStringCellValue());
                // 2)?????????????????????????????????
                Score score = new Score();
                score.setStudentId(studentId);
                score.setCourseId(courseId);
                score.setScore(scoreValue);
                score.setRemark(remark);
                if(!scoreService.isScore(score)){
                    // 3)???????????????
                    int i = scoreService.addScore(score);
                    if(i > 0){
                        count ++ ;
                    }
                }else{
                    errorMsg += "???" + rowNum + "?????????????????????????????????\n";
                }
            }
            errorMsg += "????????????" + count + "??????????????????";
            response.getWriter().write("<div id='message'>"+errorMsg+"</div>");

        } catch (IOException e) {
            e.printStackTrace();
            try {
                response.getWriter().write("<div id='message'>????????????</div>");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }


    /**
     * ??????xlsx???
     */
    @RequestMapping("/exportScore")
    @ResponseBody
    private void exportScore(HttpServletResponse response,Score score,HttpSession session) {
        //??????????????????????????????
        Student student = (Student) session.getAttribute(Const.STUDENT);
        if(!StringUtils.isEmpty(student)){
            //?????????????????????????????????????????????
            score.setStudentId(student.getId());
        }
        try {
            response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode("score_list_sid_"+score.getStudentId()+"_cid_"+score.getStudentId()+".xls", "UTF-8"));
            response.setHeader("Connection", "close");
            response.setHeader("Content-Type", "application/octet-stream");
            ServletOutputStream outputStream = response.getOutputStream();
            List<Score> scoreList = scoreService.getAll(score);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
            XSSFSheet createSheet = xssfWorkbook.createSheet("????????????");
            XSSFRow createRow = createSheet.createRow(0);
            createRow.createCell(0).setCellValue("??????");
            createRow.createCell(1).setCellValue("??????");
            createRow.createCell(2).setCellValue("??????");
            createRow.createCell(3).setCellValue("??????");
            //????????????????????????excel?????????
            int row = 1;
            for( Score s:scoreList){
                createRow = createSheet.createRow(row++);
                createRow.createCell(0).setCellValue(s.getStudentName());
                createRow.createCell(1).setCellValue(s.getCourseName());
                createRow.createCell(2).setCellValue(s.getScore());
                createRow.createCell(3).setCellValue(s.getRemark());
            }
            xssfWorkbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * ??????????????????
     * @return
     */
    @RequestMapping("/scoreStats")
    public String scoreStats(){
        return "/score/scoreStats";
    }


    /**
     * ??????????????????
     */
    @RequestMapping("/getScoreStatsList")
    @ResponseBody
    public Object getScoreStatsList(@RequestParam(value = "courseid", defaultValue = "0")Integer courseid,
                                    String searchType){
        AjaxResult ajaxResult = new AjaxResult();
        if(searchType.equals("avg")){
            ScoreStats scoreStats = scoreService.getAvgStats(courseid);

            List<Double> scoreList = new ArrayList<Double>();
            scoreList.add(scoreStats.getMax_score());
            scoreList.add(scoreStats.getMin_score());
            scoreList.add(scoreStats.getAvg_score());

            List<String> avgStringList = new ArrayList<String>();
            avgStringList.add("?????????");
            avgStringList.add("?????????");
            avgStringList.add("?????????");

            Map<String, Object> retMap = new HashMap<String, Object>();
            retMap.put("courseName", scoreStats.getCourseName());
            retMap.put("scoreList", scoreList);
            retMap.put("avgList", avgStringList);
            retMap.put("type", "success");

            return retMap;
        }

        Score score = new Score();
        score.setCourseId(courseid);
        List<Score> scoreList = scoreService.getAll(score);


        List<Integer> numberList = new ArrayList<Integer>();
        numberList.add(0);
        numberList.add(0);
        numberList.add(0);
        numberList.add(0);
        numberList.add(0);

        List<String> rangeStringList = new ArrayList<String>();
        rangeStringList.add("60?????????");
        rangeStringList.add("60~70???");
        rangeStringList.add("70~80???");
        rangeStringList.add("80~90???");
        rangeStringList.add("90~100???");

        String courseName = "";

        for(Score sc : scoreList){
            courseName = sc.getCourseName();  //???????????????
            double scoreValue = sc.getScore();//????????????
            if(scoreValue < 60){
                numberList.set(0, numberList.get(0)+1);
                continue;
            }
            if(scoreValue <= 70 && scoreValue >= 60){
                numberList.set(1, numberList.get(1)+1);
                continue;
            }
            if(scoreValue <= 80 && scoreValue > 70){
                numberList.set(2, numberList.get(2)+1);
                continue;
            }
            if(scoreValue <= 90 && scoreValue > 80){
                numberList.set(3, numberList.get(3)+1);
                continue;
            }
            if(scoreValue <= 100 && scoreValue > 90){
                numberList.set(4, numberList.get(4)+1);
                continue;
            }
        }
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("courseName", courseName);
        retMap.put("numberList", numberList);
        retMap.put("rangeList", rangeStringList);
        retMap.put("type", "success");
        return retMap;
    }

}
