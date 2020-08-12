package com.gitlab.cdagaming.craftpresence.utils.curse.impl;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CachedScan {

    @SerializedName("folderName")
    @Expose
    public String folderName;
    @SerializedName("fingerprint")
    @Expose
    public Integer fingerprint;
    @SerializedName("fileDateHash")
    @Expose
    public Integer fileDateHash;
    @SerializedName("sectionID")
    @Expose
    public Integer sectionID;
    @SerializedName("individualFingerprints")
    @Expose
    public List<Integer> individualFingerprints = new ArrayList<Integer>();
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("timestamp")
    @Expose
    public String timestamp;
    @SerializedName("lastWriteTimeUtc")
    @Expose
    public String lastWriteTimeUtc;
    @SerializedName("queryTimestamp")
    @Expose
    public String queryTimestamp;
    @SerializedName("fileCount")
    @Expose
    public Integer fileCount;
    @SerializedName("fileSize")
    @Expose
    public Integer fileSize;

}
