package com.mbio.exercise.cli.datastore.obj;

import java.net.URL;

public class FetchUrl {

    URL url;
    String origin;

    public FetchUrl(URL url, String origin) {
        this();
        this.url = url;
        this.origin = origin;
    }

    public FetchUrl() {
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
