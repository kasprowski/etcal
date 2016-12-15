package pl.kasprowski.etcal.www;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pl.kasprowski.etcal.ETCal;
import pl.kasprowski.etcal.dataunits.DataUnits;

@WebServlet("/add")
public class AddServlet extends HttpServlet{
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
		
		BufferedReader reader = Helper.getReader(req,"data");
		DataUnits dataUnits = DataUnits.load(reader);
		etcal.add(dataUnits);

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
