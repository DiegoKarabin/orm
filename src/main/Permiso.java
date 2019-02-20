package main;

import orm.Model;
import orm.annotations.Column;
import orm.annotations.Table;
import orm.relations.manytomany.ManyToManyManager;

@Table(name="permisos")
public class Permiso extends Model {
    
    @Column
    public String nombre;
    
    public ManyToManyManager usuarios() {
        return this.belongsToMany(Usuario.class);
    }
    
}
