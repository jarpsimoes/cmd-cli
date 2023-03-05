package com.mbio.exercise.cli.datastore;

import com.mbio.exercise.cli.datastore.impls.DatastoreImpl;
import com.mbio.exercise.cli.datastore.obj.HttpResponseData;

import java.io.IOException;
import java.util.List;

public interface Datastore {

    HttpResponseData addNew(HttpResponseData data) throws IOException;

    List<HttpResponseData> getAllHistory() throws IOException;

    List<HttpResponseData> getHistoryByURL(String url) throws IOException;

    List<HttpResponseData> getHistoryByGroup(String group) throws IOException;

    void backup(String filePath, Datastore.BackupType backupType) throws
            IOException;

    void restore(List<HttpResponseData> data);
    enum BackupType {
        TXT,
        JSON,
        CSV
    }

}
