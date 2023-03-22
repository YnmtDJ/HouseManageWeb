package hm_controller;

import hm_bean.User;
import hm_dao.Comment_Dao;
import hm_db.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/CommentServlet")
public class CommentServlet extends HttpServlet {

    /**
     * 处理post请求
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        request.setCharacterEncoding("utf-8");
        //获取参数
        String comment=request.getParameter("comment");
        User user=(User)request.getSession().getAttribute("user");
        String H_id=request.getParameter("H_id");
        String Type=request.getParameter ("Type");
        Connection connection= DBUtil.getConnection();
        Comment_Dao commentDao=new Comment_Dao(connection);
        PrintWriter out = response.getWriter();
        //插入评论记录
        try {
            out.print("<script type=\"text/javascript\">");
            if(commentDao.InsertComment(user.U_id,H_id,comment)){//评论成功
                out.print("confirm('Comment Success!');");
            }
            else{//评论失败
                out.print("alert('Error!');");
            }
            if(Type.contains("二手房")) {
                out.print("window.location.href='SecHouseDetail.jsp?H_id=" + H_id + "';");
            }
            else if(Type.contains("租房")){
                out.print("window.location.href='RentDetail.jsp?H_id=" + H_id + "';");
            }
            out.print("</script>");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
