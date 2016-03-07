package net.dflmngr.servlet;

import javax.servlet.ServletConfig;
import javax.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

import net.dflmngr.webservice.impl.DflMngrWebservicesImpl;

public class SimpleCXFNonSpringServlet extends CXFNonSpringServlet {

  private static final long serialVersionUID = 1L;

  @Override
  public void loadBus(ServletConfig servletConfig) {
    super.loadBus(servletConfig);
    Bus bus = getBus();
    BusFactory.setDefaultBus(bus);
    Endpoint.publish("/webservices", new DflMngrWebservicesImpl());
  }
}
