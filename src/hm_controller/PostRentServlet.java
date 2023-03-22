package hm_controller;

import hm_bean.Renting;
import hm_bean.Second_House;
import hm_bean.User;
import hm_dao.House_Dao;
import hm_db.DBUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/PostRentServlet")
public class PostRentServlet extends HttpServlet {

    /**
     * 处理post请求
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        //获取参数
        String H_type = null;
        float H_area=0;
        Date H_time=null;
        String H_decorate=null;
        String H_direct=null;
        String H_rate=null;
        int H_layer=0;
        String C_id=null;
        InputStream inputStream = null;
        int R_price=0;
        String B_id=null;
        User user=(User)request.getSession().getAttribute("user");

        //判断请求是否为multipar请求
        if(!ServletFileUpload.isMultipartContent(request))
        {
            throw new RuntimeException("当前请求不支持文件上传");
        }
        //为基于磁盘的文件项创建一个FileItem工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //设置临时文件的边界值，大于该值时，上传文件会先保存在临时文件中，否则，上传文件将直接写入到内存
        //单位：字节，设置边界值1M，一字节=1024M;
        factory.setSizeThreshold(1024 * 1024);
        //设置文件临时储存
        String temppath=this.getServletContext().getRealPath("/temp");
        File temp=new File(temppath);
        factory.setRepository(temp);
        //创建一个新的文件上传处理程序
        ServletFileUpload upload = new ServletFileUpload(factory);
        //设置每一个item的头部字符编码，其可以解决文件名中文乱码问题；
        upload.setHeaderEncoding("UTF-8");
        //设置单个文件的最大边界值(这里是2M)
        upload.setFileSizeMax(1024*1024*2);
        //设置一次上传所有文件总和的最大值(对上传多个文件起作用,这里最大为5M)
        upload.setSizeMax(1024*1024*5);
        //解析请求,获取所有的item
        try {
            //
            //调用ServletFileUpload.parseRequest方法解析request对象，
            //得到一个保存了所有上传内容的List对象。
            List<FileItem> items = upload.parseRequest(request);
            //遍历
            for(FileItem item:items){
                //若item为普通表单项
                if(item.isFormField()){
                    //获取表单中属性名称
                    String fieldName = item.getFieldName();
                    //获取表单属性的值
                    switch (fieldName) {
                        case "H_type" -> H_type = item.getString("UTF-8");
                        case "H_area" -> H_area = Float.parseFloat(item.getString("UTF-8"));
                        case "H_time" -> H_time = Date.valueOf(item.getString("UTF-8"));
                        case "H_decorate" -> H_decorate = item.getString("UTF-8");
                        case "H_direct" -> H_direct = item.getString("UTF-8");
                        case "H_rate" -> H_rate = item.getString("UTF-8");
                        case "H_layer" -> H_layer = Integer.parseInt(item.getString("UTF-8"));
                        case "C_id" -> C_id = item.getString("UTF-8");
                        case "R_price" -> R_price=Integer.parseInt(item.getString("UTF-8"));
                        case "B_id" -> B_id=item.getString("UTF-8");
                    }
                }
                else{//若item为文件表单项
                    //获取输入流,其实有上传文件的内容
                    inputStream = item.getInputStream();
                }
            }
            Renting house=new Renting(null,H_type,H_area,H_time,H_decorate,H_direct,H_rate,H_layer,C_id,inputStream,R_price);
            Connection connection= DBUtil.getConnection();
            House_Dao houseDao=new House_Dao(connection);
            //插入房源数据
            if(houseDao.InsertHouse(house,"租房",B_id, user.U_id)){//成功
                request.setAttribute("Flag","Success");
            }
            else{//失败
                request.setAttribute("Flag","Failed");
            }
            request.setAttribute("C_id",C_id);
            request.getRequestDispatcher("PostRent.jsp").forward(request,response);
            /*assert inputStream != null;
            inputStream.close();*/
        } catch (FileUploadException | SQLException e) {
            e.printStackTrace();
        }
    }
}
