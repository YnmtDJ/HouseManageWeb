package hm_filter;

import hm_bean.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

//@WebFilter(filterName = "PostFilter", urlPatterns = {"abbbbbbb"})
public class PostFilter /*implements Filter*/{
    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(true);
        resp.setContentType("text/html;");
        resp.setCharacterEncoding("utf-8");
        //PrintWriter out = resp.getWriter();
        String request_url = req.getRequestURI();
        String ctxPath = req.getContextPath();
        String url = request_url.substring(ctxPath.length());
        User user=(User)req.getSession().getAttribute("user");
        String C_id=req.getParameter("C_id");

        if(user==null){//未登录
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
        else if (!user.U_type.equals("普通用户")) {//非普通用户，可以发布
            chain.doFilter(request, response);
        }
        else {//普通用户不可发布
            request.setAttribute("C_id",C_id);
            request.setAttribute("Flag","Failed");
            request.getRequestDispatcher("CommunityDetail.jsp").forward(request, response);
        }
    }

    public void init(FilterConfig fConfig) throws ServletException {
    }
}
