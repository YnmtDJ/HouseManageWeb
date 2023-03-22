package hm_filter;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


@WebFilter(filterName = "LoginFilter", urlPatterns = { "/*" })
public class LoginFilter implements Filter {

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
        //登录、注册页面请求
        if (url.contains("login.jsp") || url.contains("login")||
                url.contains("register.jsp")||url.contains("register")) {
            chain.doFilter(request, response);
        }
        else {
            if (session.getAttribute("user") != null) {//已登陆
                chain.doFilter(request, response);
            }
            else {//未登录
                //out.println("您没有登录，请先登录！3秒后回到登录页面。");
                //resp.setHeader("refresh", "3;url=" + ctxPath + "/login.jsp");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        }
    }

    public void init(FilterConfig fConfig) throws ServletException {
    }

}

