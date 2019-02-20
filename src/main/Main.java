package main;

import java.sql.SQLException;
import orm.Model;
import orm.connection.DatabaseConnection;

public class Main {
    
    public static void main(String[] args) {
        Permiso permiso1 = new Permiso();
        permiso1.nombre = "leer";
        permiso1.save();
        Permiso permiso2 = new Permiso();
        permiso2.nombre = "escribir";
        permiso2.save();
        
        Usuario usuario = Model.find(2, Usuario.class);
        usuario.permisos().attach(permiso1);
        usuario.permisos().attach(permiso2);
        
        try {
            String sql = "INSERT INTO permiso_usuario (usuario_id, permiso_id) VALUES (?, ?)";
            DatabaseConnection.getInstance().execute(sql, new Object[] {2, 2});
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
}
