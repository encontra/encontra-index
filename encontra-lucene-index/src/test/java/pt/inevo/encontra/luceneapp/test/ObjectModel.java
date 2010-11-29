package pt.inevo.encontra.luceneapp.test;

import pt.inevo.encontra.index.annotation.Indexed;
import pt.inevo.encontra.storage.IEntity;

public class ObjectModel implements IEntity<Long> {

    private Long id;
    private String content;

    public ObjectModel(String content) {
        this.content = content;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Indexed
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MetaTestModel{"
                + "id=" + id
                + ", content='" + content + '\''
                + '}';
    }
}
