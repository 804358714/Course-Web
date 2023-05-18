package com.example.web.controller.student;

import com.example.web.model.entity.Course;
import com.example.web.model.entity.Question;
import com.example.web.model.entity.User;
import com.example.web.model.mapper.CourseMapper;
import com.example.web.model.mapper.QuestionMapper;
import com.example.web.model.mapper.UserMapper;
import com.example.web.utils.MyBatisUtil;
import com.example.web.utils.ThymeleafUtil;
import org.apache.ibatis.session.SqlSession;
import org.thymeleaf.context.Context;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@WebServlet(name = "CourseQuestionServlet", value = "/CourseQuestion")
public class CourseQuestionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        req.setCharacterEncoding("utf-8");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        String teachername = req.getParameter("teachername");
        Integer courseId = Integer.valueOf(req.getParameter("courseid"));
        Course course = null;
        List <Question> questions = null;
        List <Question> questionList = new ArrayList<>();
        List<String > questionNameList = new ArrayList<>();
        try(SqlSession sqlSession = MyBatisUtil.getFactory().openSession(true)){
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            QuestionMapper questionMapper = sqlSession.getMapper(QuestionMapper.class);
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            course = courseMapper.getCourseById(courseId);
            questions = questionMapper.getQuestionByCourseid(courseId);
            for(Question question : questions){
                if(question.getPrivateCheck() == 1 && !Objects.equals(question.getUserid(), user.getUserid())){
                    continue;
                }
                questionList.add(question);
                questionNameList.add(userMapper.checkUserByRegister(question.getUserid()).getUsername());
            }
        }
        Context context = new Context();
        context.setVariable("username",user.getUsername());
        context.setVariable("teachername",teachername);
        context.setVariable("course",course);
        context.setVariable("questionList",questionList);
        context.setVariable("questionNameList",questionNameList);
        ThymeleafUtil.getEngine().process("templates/studentCourseQuestion.html", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        req.setCharacterEncoding("utf-8");
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        String teachername = req.getParameter("teachername");
        Integer courseId = Integer.valueOf(req.getParameter("courseid"));
        String searchContent = "%"+req.getParameter("searchContent")+"%";
        System.out.println(searchContent);
        Course course = null;
        List <Question> questions = null;
        List <Question> questionList = new ArrayList<>();
        List<String > questionNameList = new ArrayList<>();
        try(SqlSession sqlSession = MyBatisUtil.getFactory().openSession(true)){
            CourseMapper courseMapper = sqlSession.getMapper(CourseMapper.class);
            QuestionMapper questionMapper = sqlSession.getMapper(QuestionMapper.class);
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            course = courseMapper.getCourseById(courseId);

            questions = questionMapper.getQuestionsBySearch(courseId,searchContent);

            for(Question question : questions){
                if(question.getPrivateCheck() == 1 && !Objects.equals(question.getUserid(), user.getUserid())){
                    continue;
                }
                questionList.add(question);
                questionNameList.add(userMapper.checkUserByRegister(question.getUserid()).getUsername());
            }
        }
        Context context = new Context();
        context.setVariable("username",user.getUsername());
        context.setVariable("teachername",teachername);
        context.setVariable("course",course);
        context.setVariable("questionList",questionList);
        context.setVariable("questionNameList",questionNameList);
        ThymeleafUtil.getEngine().process("templates/studentCourseQuestion.html", context, resp.getWriter());
    }
}
