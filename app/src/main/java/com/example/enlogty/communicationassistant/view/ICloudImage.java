package com.example.enlogty.communicationassistant.view;

import com.example.enlogty.communicationassistant.domain.CloudImage;

import java.util.List;

/**
 * Created by enlogty on 2017/11/30.
 */

public interface ICloudImage {
    void getDataFromRemoteService(List<CloudImage> cloudImageList);
}
