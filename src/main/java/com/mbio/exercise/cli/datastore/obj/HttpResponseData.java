package com.mbio.exercise.cli.datastore.obj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpResponseData {
    private String url;
    private String contentFile;
    private String content;
    private int resultCode;
    private String contentType;
    private String group;
    private long responseTime;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getContentFile() {
        return contentFile;
    }

    public void setContentFile(String contentFile) {
        this.contentFile = contentFile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    private StringBuilder buildLine() {
        final StringBuilder sb = new StringBuilder();

        sb.append("| ").append(group).append(" | ");
        sb.append(url).append(" | ");
        sb.append(resultCode).append(" | ");
        sb.append(contentType).append(" | ");
        sb.append(responseTime).append(" | ");

        return sb.append(System.lineSeparator());
    }
    @Override public String toString() {

        return buildLine().toString();
    }
    public String toStringWithContent() {
        return buildLine().append(content).append(System.lineSeparator()).toString();
    }
}