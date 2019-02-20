package main;

import java.sql.Timestamp;
import orm.Model;
import orm.annotations.Column;
import orm.annotations.Table;
import orm.relations.onetomany.OneToManyInverseManager;

@Table(name="registros")
public class Registro extends Model {

    @Column
    public String accion;
    @Column
    public String descripcion;
    @Column
    public Timestamp fecha;
    
    public OneToManyInverseManager usuario() {
        return this.belongsToOne(Usuario.class);
    }
    
}
