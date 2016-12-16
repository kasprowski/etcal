package pl.kasprowski.etcal.www;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pl.kasprowski.etcal.ETCal;
import pl.kasprowski.etcal.helpers.ObjDef;

@WebServlet("/optimize")
public class OptimizeServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {

			ETCal etcal = Helper.getEtCal(req);
			BufferedReader reader = Helper.getReader(req,"params");
			ObjDef params = ObjDef.load(reader);
			System.out.println(params);
			etcal.optimizeAsync(params,null);
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
