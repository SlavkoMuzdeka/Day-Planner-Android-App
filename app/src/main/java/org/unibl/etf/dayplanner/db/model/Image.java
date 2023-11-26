package org.unibl.etf.dayplanner.db.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.unibl.etf.dayplanner.utils.Constants;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = Constants.TABLE_NAME_IMAGE)
public class Image implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String uri;

    public void setId(long id) {
        this.id = id;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return id == image.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
