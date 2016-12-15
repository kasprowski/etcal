package pl.kasprowski.etcal.www;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pl.kasprowski.etcal.ETCal;

@WebServlet("/reset")
public class ResetServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		HttpSession session = req.getSession();
		
		ETCal etcal;
		if(session.getAttribute("etcal")==null) 
			etcal = new ETCal();
		else
			etcal = (ETCal)session.getAttribute("etcal");

		etcal.reset();
		session.setAttribute("etcal", etcal);
		System.out.println("Status: "+etcal.getStatusInfo());
		resp.getWriter().write(etcal.getStatusInfoJSON());
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req,resp);
	}
}
