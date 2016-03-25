package net.dflmngr.clients.testing;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class TestDS {
	
	public static void main(String[] args) {
		
		
		try {
			InitialContext initCtx = new InitialContext();
			
			NamingEnumeration<NameClassPair> list = initCtx.list("");
			while (list.hasMore()) {
			  System.out.println(list.next().getName());
			}
			
			//Context envCtx = (Context) initCtx.lookup("java:comp/env");
			//DataSource ds = (DataSource) envCtx.lookup("jdbc/dflmngrDB");
			
			DataSource ds = (DataSource) initCtx.lookup("java:/comp/env/jdbc/dflmngrDB");
			//DataSource ds = (DataSource) initCtx.lookup("jdbc/dflmngrDB");
			
			System.out.println(ds.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
		
		
	}
}
