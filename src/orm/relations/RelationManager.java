package orm.relations;

import orm.Model;

public abstract class RelationManager {
    
    private Class<? extends Model> referer;
    private Class<? extends Model> refered;

    public Class<? extends Model> getReferer() {
        return referer;
    }

    public void setReferer(Class<? extends Model> referer) {
        this.referer = referer;
    }

    public Class<? extends Model> getRefered() {
        return refered;
    }

    public void setRefered(Class<? extends Model> refered) {
        this.refered = refered;
    }
    
    public abstract <T extends Model> T[] all();

}
