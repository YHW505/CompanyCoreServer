package com.example.companycoreserver.util;

import java.util.Base64;

/**
 * 파일 처리 유틸리티 클래스
 * Base64 인코딩/디코딩 및 파일 관련 기능을 제공
 */
public class FileUtil {

    /**
     * 바이트 배열을 Base64 문자열로 인코딩
     * @param bytes 인코딩할 바이트 배열
     * @return Base64 인코딩된 문자열
     */
    public static String encodeToBase64(byte[] bytes) {
        if (bytes == null) return null;
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Base64 문자열을 바이트 배열로 디코딩
     * @param base64String 디코딩할 Base64 문자열
     * @return 디코딩된 바이트 배열
     */
    public static byte[] decodeFromBase64(String base64String) {
        if (base64String == null || base64String.trim().isEmpty()) return null;
        return Base64.getDecoder().decode(base64String);
    }

    /**
     * 파일 크기를 사람이 읽기 쉬운 형태로 변환
     * @param bytes 바이트 수
     * @return 변환된 문자열 (예: "1.5 MB")
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * 파일 확장자로부터 MIME 타입 추정
     * @param filename 파일명
     * @return MIME 타입
     */
    public static String getMimeType(String filename) {
        if (filename == null) return "application/octet-stream";
        
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        
        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt":
                return "text/plain";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 파일명이 유효한지 검사
     * @param filename 검사할 파일명
     * @return 유효 여부
     */
    public static boolean isValidFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        
        // 위험한 문자들 제외
        String dangerousChars = "<>:\"/\\|?*";
        for (char c : dangerousChars.toCharArray()) {
            if (filename.contains(String.valueOf(c))) {
                return false;
            }
        }
        
        // 파일명 길이 제한
        return filename.length() <= 255;
    }
} 