package com.jamie.streamAnalysis.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jamie.streamAnalysis.domain.MPEG_TABLES;
import com.jamie.streamAnalysis.service.StreamConvertion;
import com.jamie.streamAnalysis.service.TestService;
import com.jamie.streamAnalysis.util.CwFileUtils;
import com.jamie.streamAnalysis.util.FileReader;

/**
 * Servlet implementation class Function001
 */
@WebServlet(value = "/f1", name = "Function001")
public class Function001 extends BaseHttpServlet {

	private static final Logger logger = Logger.getLogger(Function001.class);
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Function001() {
		super();
	}

	@Override
	public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String errorMessage = "";
		XSSFWorkbook workbook = new XSSFWorkbook();
		String reportName = null;

		try {

			String filePath = request.getParameter("filePath");
			logger.info("filePath : " + filePath);

			String action = request.getParameter("action");
			logger.info("action : " + action);

			HttpSession session = request.getSession();
			System.out.println("get sesstion...");

			if ("generate".equals(action)) {
				// for loading test
				Map<String, Object> resultMap = new HashMap<String, Object>();
				GsonBuilder gb = new GsonBuilder().serializeNulls();
				Gson gson = gb.create();
				try {
					resultMap.put("ERR_CDE", "00");
					resultMap.put("ERR_MSG", "success");

					StreamConvertion convert = new StreamConvertion(filePath + "\\");

					String path = "C:\\xml\\";

					FileReader fr = new FileReader();
					ArrayList<String> nameList = fr.readFile(path);

					TestService service = new TestService();
					MPEG_TABLES table = null;
//					XSSFWorkbook workbook = new XSSFWorkbook();

					ArrayList<MPEG_TABLES> mpegList = new ArrayList<MPEG_TABLES>();
					ArrayList<String> fileList = new ArrayList<String>();

					for (String fName : nameList) {
						table = (MPEG_TABLES) service.genTable(path + fName, MPEG_TABLES.class);
						mpegList.add(table);
						fileList.add(fName);
					}

					service.genMpegExcel(workbook, mpegList, fileList, filePath);

					session.setAttribute("workbook", workbook);

					// Generate file to D:
//					CwFileUtils.createExcelFile(workbook, reportName, destination);
					logger.info("Report name : " + reportName + ".xlsx");
					logger.info("--------------------END--------------------");

				} catch (Exception e) {
					e.printStackTrace();
					resultMap.put("ERR_CDE", "01");
					resultMap.put("ERR_MSG", "fail : " + e.getMessage());
				}
				System.out.println("resp : " + gson.toJson(resultMap));
				PrintWriter out = response.getWriter();
				out.write(gson.toJson(resultMap));
			} else if ("export".equals(action)){

				/************************************* Export to Browser ************************************/
				ServletOutputStream sos = null;

				try {
					reportName = "report_" + new Date().getTime();
					response.setContentType("application/vnd.ms-excel; charset=MS950");
					response.setHeader("Content-disposition", "attachment; filename=" + reportName + ".xlsx");
					XSSFWorkbook wb = (XSSFWorkbook) session.getAttribute("workbook");

					// write the output to a file
					sos = response.getOutputStream();
					wb.write(sos);
					sos.close();
				} catch (Exception e) {
					e.printStackTrace();
					logger.info(e);
					errorMessage = "Export error!\\r\\n" + ExceptionUtils.getCause(e);
				} finally {
					if (sos != null) {
						sos.close();
					}
					response.flushBuffer();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
//			logger.info( ExceptionUtils.getCause(e));
			errorMessage = "System error!\\r\\n" + e.getMessage();

			logger.error(e.getMessage(), e);
		}

		if (StringUtils.isNotBlank(errorMessage)) {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println("<script language=\"javascript\">");
			out.println("alert(\"" + errorMessage + "\");");
			out.println("</script>");
		}

	}

}
