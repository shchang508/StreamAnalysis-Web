package com.jamie.streamAnalysis.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
		
		try {
			
//			String str = null;
//			str.toString();
			
			
			String action = request.getParameter("filePath");
			logger.info(action);
			
			if (StringUtils.isBlank(action)) {
				throw new Exception("Please input a path");
			}

			StreamConvertion convert = new StreamConvertion(action + "\\");

			String path = "C:\\xml\\";

			FileReader fr = new FileReader();
			ArrayList<String> nameList = fr.readFile(path);

			TestService service = new TestService();
			MPEG_TABLES table = null;
			XSSFWorkbook workbook = new XSSFWorkbook();

			ArrayList<MPEG_TABLES> mpegList = new ArrayList<MPEG_TABLES>();
			ArrayList<String> fileList = new ArrayList<String>();

			for (String fName : nameList) {
				table = (MPEG_TABLES) service.genTable(path + fName, MPEG_TABLES.class);
				mpegList.add(table);
				fileList.add(fName);
			}

			service.genMpegExcel(workbook, mpegList, fileList);

			String reportName = "report_" + new Date().getTime();
			String destination = "D:\\Stream Analysis";

			//Generate file to D:
//			CwFileUtils.createExcelFile(workbook, reportName, destination);
			logger.info("Report name : " + reportName + ".xlsx");
			logger.info("--------------------END--------------------"); 
			
			/************************************* Export to Browser ************************************/
			ServletOutputStream sos =null;
			
			try {
				response.setContentType("application/vnd.ms-excel; charset=MS950");
				response.setHeader("Content-disposition", "attachment; filename=" + reportName + ".xlsx");
				
				//write the output to a file
				sos = response.getOutputStream();
				workbook.write(sos);
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

		} catch (Exception e) {
			e.printStackTrace();
//			logger.info( ExceptionUtils.getCause(e));
			errorMessage = "System error!\\r\\n" + e.getMessage();
			
			logger.error(e.getMessage(), e);
		}
		
		if(StringUtils.isNotBlank(errorMessage)) {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println("<script language=\"javascript\">");
			out.println("alert(\"" + errorMessage + "\");");
			out.println("</script>");
		}

	}

}
