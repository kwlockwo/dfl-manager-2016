package net.dflmngr.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class PingServlet extends HttpServlet {
	private static final long serialVersionUID = 3960411517947984098L;
    private static final String CONTENT_TYPE = "text/plain";
    private static final String CONTENT = "ok";
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String NO_CACHE = "must-revalidate,no-cache,no-store";

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setHeader(CACHE_CONTROL, NO_CACHE);
        resp.setContentType(CONTENT_TYPE);
        final PrintWriter writer = resp.getWriter();
        try {
            writer.println(CONTENT);
        } finally {
            writer.close();
        }
    }
}
