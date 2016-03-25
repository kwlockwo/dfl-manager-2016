package net.dflmngr.jndi;

import java.io.InputStream;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.dbcp.BasicDataSource;

public class JndiProvider {
	
	private static final String JNDI = "java:comp/env/jdbc/dflmngrDB";
	
    public static void bind() throws Exception {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
        
        Properties properties = new Properties();
        
        InputStream stream = JndiProvider.class.getClassLoader().getResourceAsStream("jndiProvider.properties");
        properties.load(stream);
        stream.close();

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
        dataSource.setUsername(properties.getProperty("username"));
        dataSource.setPassword(properties.getProperty("password"));
        dataSource.setUrl(properties.getProperty("url"));
        dataSource.setValidationQuery("SELECT 1");

        final Context context = new InitialContext();
        try {
            context.createSubcontext("java:");
            context.createSubcontext("java:comp");
            context.createSubcontext("java:comp/env");
            context.createSubcontext("java:comp/env/jdbc");
            context.bind(JNDI, dataSource);
        } finally {
            context.close();
        }
    }
}
