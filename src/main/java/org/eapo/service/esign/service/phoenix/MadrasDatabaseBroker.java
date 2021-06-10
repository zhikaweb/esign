package org.eapo.service.esign.service.phoenix;


import gryphon.common.Logger;
import madras.database.DatabaseBroker;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

 @Service
public class MadrasDatabaseBroker extends DatabaseBroker {
    public MadrasDatabaseBroker() {
        String cn = DatabaseBroker.class.getName();
        setPackageName(cn.substring(0, cn.lastIndexOf('.')));
    }

    public List<Object[]> getValuesList(String sql, Object[] params) throws Exception {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        List<Object[]> values = new ArrayList<Object[]>();
        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Object[] value = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    value[i] = rs.getObject(i + 1);
                }
                values.add(value);
            }
        } catch (Exception ex) {
            Logger.logThrowable(ex);
            throw ex;
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                Logger.logThrowable(e);
                throw e;
            }
        }
        return values;
    }
}

