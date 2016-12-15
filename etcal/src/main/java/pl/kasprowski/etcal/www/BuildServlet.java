package pl.kasprowski.etcal.www;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pl.kasprowski.etcal.ETCal;
import pl.kasprowski.etcal.helpers.ObjDef;

@WebServlet("/build")
public class BuildServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
	
		HttpSession session = req.getSession();
		
		ETCal etcal;
		if(session.getAttribute("etcal")==null) 
			etcal = new ETCal();
		else
			etcal = (ETCal)session.getAttribute("etcal");
		
//		String data = req.getParameter("params");
//		if(data!=null) {
//			InputStream is = new ByteArrayInputStream(data.getBytes());
//			BufferedReader dataStream = new BufferedReader(new InputStreamReader(is));
//			ObjDef params = ObjDef.load(dataStream);
//			try {
//				etcal.buildAsync(params,null);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		
//		String data = req.getParameter("params");
//		InputStream is;
//		if(data!=null) {
//			is = new ByteArrayInputStream(data.getBytes());
//		}
//		else
//			is = req.getInputStream();
//		BufferedReader dataStream = new BufferedReader(new InputStreamReader(is));

		BufferedReader reader = Helper.getReader(req,"params");

		ObjDef params = ObjDef.load(reader);
		try {
			etcal.buildAsync(params,null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		
		session.setAttribute("etcal", etcal);
		System.out.println("Status: "+etcal.getStatusInfo());
		resp.getWriter().write(etcal.getStatusInfoJSON());
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("GET!");
	}
}
