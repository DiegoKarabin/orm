package main;

import java.sql.Timestamp;
import orm.Model;
import orm.annotations.Column;
import orm.annotations.Table;
import orm.relations.manytomany.ManyToManyManager;
import orm.relations.onetomany.OneToManyManager;

/**
 *
 * @author Diego Karabin
 */
@Table(name="usuarios")
public class Usuario extends Model {
    
    @Column
    public String nombre;
    @Column
    public String apellido;
    @Column
    public int cedula;
    @Column
    public String usuario;
    @Column
    public String clave;
    @Column
    public Timestamp fecha;
    
    public OneToManyManager registros() {
        return this.hasMany(Registro.class);
    }
    
    public ManyToManyManager permisos() {
        return this.belongsToMany(Permiso.class);
    }
    
}
