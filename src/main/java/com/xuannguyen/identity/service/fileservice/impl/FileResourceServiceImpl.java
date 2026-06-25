package com.xuannguyen.identity.service.fileservice.impl;

import com.xuannguyen.identity.dto.request.filerequest.FileResourceUpdateRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.fileresponse.FileResourceResponse;
import com.xuannguyen.identity.entity.FileResource;
import com.xuannguyen.identity.repository.FileResourceRepository;
import com.xuannguyen.identity.service.fileservice.FileResourceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileResourceServiceImpl implements FileResourceService {

    FileResourceRepository fileResourceRepository;

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 20MB

    @Override
    @Transactional
    public FileResourceResponse uploadFile(MultipartFile file) {
        validateFile(file);

        try {
            String fileName = normalizeFileName(file.getOriginalFilename());

            if (fileResourceRepository.existsByName(fileName)) {
                throw new RuntimeException("Tên file đã tồn tại: " + fileName);
            }

            FileResource fileResource = FileResource.builder()
                    .name(fileName)
                    .type(file.getContentType())
                    .size(file.getSize())
                    .data(file.getBytes())
                    .build();

            FileResource savedFile = fileResourceRepository.save(fileResource);

            return toResponse(savedFile);
        } catch (Exception e) {
            throw new RuntimeException("Upload file thất bại: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FileResourceResponse getFileInfo(String id) {
        FileResource fileResource = findById(id);
        return toResponse(fileResource);
    }

    @Override
    @Transactional(readOnly = true)
    public FileResource getFileData(String id) {
        return findById(id);
    }

    @Override
    @Transactional
    public FileResourceResponse updateFileInfo(String id, FileResourceUpdateRequest request) {
        FileResource fileResource = findById(id);

        if (request == null) {
            throw new RuntimeException("Request không được null");
        }

        if (isBlank(request.getName())) {
            throw new RuntimeException("Tên file không được để trống");
        }

        String newName = normalizeFileName(request.getName());

        if (!newName.equals(fileResource.getName())
                && fileResourceRepository.existsByName(newName)) {
            throw new RuntimeException("Tên file đã tồn tại: " + newName);
        }

        validateFileExtension(newName);

        fileResource.setName(newName);

        FileResource updatedFile = fileResourceRepository.save(fileResource);

        return toResponse(updatedFile);
    }

    @Override
    @Transactional
    public FileResourceResponse replaceFile(String id, MultipartFile file) {
        FileResource fileResource = findById(id);

        validateFile(file);

        try {
            String fileName = normalizeFileName(file.getOriginalFilename());

            if (!fileName.equals(fileResource.getName())
                    && fileResourceRepository.existsByName(fileName)) {
                throw new RuntimeException("Tên file đã tồn tại: " + fileName);
            }

            fileResource.setName(fileName);
            fileResource.setType(file.getContentType());
            fileResource.setSize(file.getSize());
            fileResource.setData(file.getBytes());

            FileResource updatedFile = fileResourceRepository.save(fileResource);

            return toResponse(updatedFile);
        } catch (Exception e) {
            throw new RuntimeException("Cập nhật file thất bại: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FileResourceResponse> getAllFilePagination(int page, int size) {
        if (page < 1) {
            page = 1;
        }

        if (size < 1) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(
                page - 1,
                size,
                Sort.by(Sort.Direction.DESC, "name")
        );

        Page<FileResource> filePage = fileResourceRepository.findAll(pageable);

        List<FileResourceResponse> data = filePage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.<FileResourceResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(filePage.getTotalPages())
                .totalElements(filePage.getTotalElements())
                .data(data)
                .build();
    }

    @Override
    @Transactional
    public void deleteFile(String id) {
        FileResource fileResource = findById(id);
        fileResourceRepository.delete(fileResource);
    }

    private FileResource findById(String id) {
        if (isBlank(id)) {
            throw new RuntimeException("File id không được để trống");
        }

        return fileResourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy file với id: " + id));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File không được để trống");
        }

        String fileName = normalizeFileName(file.getOriginalFilename());

        validateFileExtension(fileName);
        validateContentType(file.getContentType());

        if (file.getSize() <= 0) {
            throw new RuntimeException("File không hợp lệ");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File không được vượt quá 20MB");
        }
    }

    private void validateFileExtension(String fileName) {
        String lowerName = fileName.toLowerCase();

        if (!lowerName.endsWith(".pdf")
                && !lowerName.endsWith(".doc")
                && !lowerName.endsWith(".docx")) {
            throw new RuntimeException("Chỉ hỗ trợ file PDF, DOC, DOCX");
        }
    }

    private void validateContentType(String contentType) {
        if (isBlank(contentType)) {
            throw new RuntimeException("Không xác định được loại file");
        }

        boolean valid = contentType.equals("application/pdf")
                || contentType.equals("application/msword")
                || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        if (!valid) {
            throw new RuntimeException("Loại file không hợp lệ. Chỉ chấp nhận PDF, DOC, DOCX");
        }
    }

    private String normalizeFileName(String fileName) {
        if (isBlank(fileName)) {
            throw new RuntimeException("Tên file không được để trống");
        }

        String normalized = fileName.trim();

        if (normalized.contains("..") || normalized.contains("/") || normalized.contains("\\")) {
            throw new RuntimeException("Tên file không hợp lệ");
        }

        return normalized;
    }

    private FileResourceResponse toResponse(FileResource fileResource) {
        return FileResourceResponse.builder()
                .id(fileResource.getId())
                .name(fileResource.getName())
                .type(fileResource.getType())
                .size(fileResource.getSize())
                .url("/file-resources/" + fileResource.getId() + "/download")
                .build();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

