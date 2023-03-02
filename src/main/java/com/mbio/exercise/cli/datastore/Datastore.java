package com.mbio.exercise.cli.datastore;

import com.mbio.exercise.cli.datastore.impls.DatastoreImpl;
import com.mbio.exercise.cli.datastore.obj.HttpResponseData;

import java.io.IOException;
import java.util.List;

public interface Datastore {

    HttpResponseData addNew(HttpResponseData data) throws IOException;
    List<HttpResponseData> getAllHistory();
    List<HttpResponseData> getHistoryByURL(String url);

    void backup(String filePath, DatastoreImpl.BackupType backupType) throws IOException;

    void restore(List<HttpResponseData> data);

}
