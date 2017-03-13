package com.android.application.music;

import org.litepal.crud.DataSupport;

/**
 * Created by Lot on 2017/3/4.
 */

public class Music extends DataSupport {
    private String name;
    private String author;
    private String source;

    public Music() {
    }

    public Music(String name, String author, String source) {
        this.name = name;
        this.author = author;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSourece() {
        return source;
    }

    public void setSourece(String sourece) {
        this.source = sourece;
    }

}
