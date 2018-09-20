package com.jamie.streamAnalysis.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		try {
			
			String action = request.getParameter("filePath");
			logger.info(action);

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

			CwFileUtils.createExcelFile(workbook, reportName, destination);

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
				logger.info(e);
			} finally {
				if (sos != null) {
					sos.close();
				}
				response.flushBuffer();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
