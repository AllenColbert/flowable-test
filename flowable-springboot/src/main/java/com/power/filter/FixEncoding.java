package com.power.filter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 解决之前出现的编码错误  --废弃待删除
 * @author : xuyunfeng
 * @date :   2019/8/8 15:35
 */
public class FixEncoding extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("拦截到请求"+req.getLocalName());
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException{
        doGet(req,resp);
    }

    public String changeCharset(String str, String sourceCharset, String targetCharset) throws UnsupportedEncodingException {
        if (str == null) {
            return null;
        }
        //用旧的字符编码解码字符串。解码可能会出现异常。
        byte[] bs = str.getBytes(sourceCharset);
        //用新的字符编码生成字符串
        return new String(bs, targetCharset);
    }
}
