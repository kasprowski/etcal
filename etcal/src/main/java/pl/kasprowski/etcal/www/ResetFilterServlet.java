package pl.kasprowski.etcal.www;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pl.kasprowski.etcal.ETCal;

@WebServlet("/resetfilter")
public class ResetFilterServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try{
			ETCal etcal = Helper.getEtCal(req);
			etcal.resetFilters();
			System.out.println("Status: "+etcal.getStatusInfo());
			resp.getWriter().write(etcal.getStatusInfoJSON());
		}catch(Exception e) {
			resp.getWriter().write("Exception: "+e.getMessage());
		}
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().write("No GET method for this url");
	}
}
