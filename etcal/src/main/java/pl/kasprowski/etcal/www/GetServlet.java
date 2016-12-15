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
import pl.kasprowski.etcal.dataunits.DataUnits;

@WebServlet("/get")
public class GetServlet extends HttpServlet{
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
		try {
			DataUnits dus = etcal.get(dataUnits);
			dus.save(resp.getWriter());
			//resp.getWriter().append()
		} catch (Exception e) {
			e.printStackTrace();
		}

		session.setAttribute("etcal", etcal);
		System.out.println("Status: "+etcal.getStatusInfo());
		//resp.getWriter().write("ETCAL Status: "+etcal.getStatusInfo());
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("GET!");
	}
}
