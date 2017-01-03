package my.module;


import com.sun.security.auth.UserPrincipal;
import my.util.DigestHelper;

import java.sql.*;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.util.Map;

public class RdbmsLoginModule implements LoginModule {

    // initial state
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, ?> sharedState;
    private Map<String, ?> options;

    // options
    private String url;
    private String driverClass;

    private boolean successful = false;

    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
        url         = (String)options.get("url");
        driverClass = (String)options.get("driver");
    }

    public boolean login() throws LoginException {
        final NameCallback nameCallback = new NameCallback("What is your name:");
        final PasswordCallback passwordCallback = new PasswordCallback("Enter your password:", false);
        try {
            callbackHandler.handle(new Callback[]{nameCallback, passwordCallback});
        } catch (IOException | UnsupportedCallbackException e) {
            e.printStackTrace();
            return false;
        }

        successful = rdbmsValidate(nameCallback.getName(), new String(passwordCallback.getPassword()));
        return successful;
    }

    public boolean commit() throws LoginException {
        return successful;
    }

    public boolean abort() throws LoginException {
        return false;
    }

    public boolean logout() throws LoginException {
        return false;
    }

    private boolean rdbmsValidate(final String name, final String pass) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean found = false;
        try {
            Class.forName(driverClass);
            conn = DriverManager.getConnection(url, (String)options.get("user"), (String)options.get("pass"));
            stmt = conn.prepareStatement("select 1 from mybatis.USER where NAME=? and PASSWORD_DIGEST = ?");
            stmt.setString(1, name);
            stmt.setString(2, DigestHelper.digest(pass));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                found = true;
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch(SQLException se){
            se.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        } finally{
            try{
                if(stmt!=null)
                    stmt.close();
            } catch(SQLException se2){ }
            try{
                if(conn!=null)
                    conn.close();
            } catch(SQLException se){
                se.printStackTrace();
            }
        }

        return found;
    }
}
