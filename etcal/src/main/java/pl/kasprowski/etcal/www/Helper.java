package pl.kasprowski.etcal.www;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import pl.kasprowski.etcal.ETCal;

public class Helper {
	public static ETCal getEtCal(HttpServletRequest req) {
		HttpSession session = req.getSession();
		ETCal etcal;
		if(session.getAttribute("etcal")==null) { 
			etcal = new ETCal();
			session.setAttribute("etcal", etcal);
		}
		else
			etcal = (ETCal)session.getAttribute("etcal");
		return etcal;
	}
	
	public static BufferedReader getReader(HttpServletRequest req,String param) {
		BufferedReader dataStream = null;
		try{
			String data = req.getParameter(param);
			InputStream is;
			if(data!=null) {
				is = new ByteArrayInputStream(data.getBytes());
			}
			else
				is = req.getInputStream();
			dataStream = new BufferedReader(new InputStreamReader(is));
		}catch (IOException e) {e.printStackTrace();}
		return dataStream;
	}

	public static void showReader(BufferedReader dataStream) {
		try{
			String s = null;
			while ((s=dataStream.readLine())!=null)
			{
				System.out.println(s);
			}
			System.out.println(dataStream);
		}catch(IOException e) {e.printStackTrace();}
	}
}
