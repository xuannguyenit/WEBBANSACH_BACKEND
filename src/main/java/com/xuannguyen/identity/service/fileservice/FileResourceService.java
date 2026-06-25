package com.xuannguyen.identity.service.fileservice;

import com.xuannguyen.identity.dto.request.filerequest.FileResourceUpdateRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.fileresponse.FileResourceResponse;
import com.xuannguyen.identity.entity.FileResource;
import org.springframework.web.multipart.MultipartFile;

public interface FileResourceService {
    FileResourceResponse uploadFile(MultipartFile file);

    FileResourceResponse getFileInfo(String id);

    FileResource getFileData(String id);

    FileResourceResponse updateFileInfo(String id, FileResourceUpdateRequest request);

    FileResourceResponse replaceFile(String id, MultipartFile file);

    PageResponse<FileResourceResponse> getAllFilePagination(int page, int size);

    void deleteFile(String id);

}
