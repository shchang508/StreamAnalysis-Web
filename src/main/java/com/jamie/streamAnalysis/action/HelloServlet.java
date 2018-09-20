package com.jamie.streamAnalysis.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value="/hello", name="helloServlet")
public class HelloServlet extends BaseHttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("message", "Hello world!");
		request.getServletContext().getRequestDispatcher("/main.jsp").forward(request, response);
	}
}
