package com.example.companycoreserver.mapper;

import com.example.companycoreserver.dto.ApprovalResponse;
import com.example.companycoreserver.entity.Approval;
import com.example.companycoreserver.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ApprovalMapper {

    public ApprovalResponse toResponse(Approval approval) {
        ApprovalResponse response = new ApprovalResponse();
        response.setId(approval.getId());
        response.setTitle(approval.getTitle());
        response.setContent(approval.getContent());
        response.setRequester(toRequesterInfo(approval.getRequester()));
        response.setApprover(toApproverInfo(approval.getApprover()));
        response.setRequestDate(approval.getRequestDate());
        response.setStatus(approval.getStatus());
        response.setRejectionReason(approval.getRejectionReason());
        response.setProcessedDate(approval.getProcessedDate());

        // 첨부파일 메타데이터 설정
        response.setAttachmentFilename(approval.getAttachmentFilename());
        response.setAttachmentContentType(approval.getAttachmentContentType());
        response.setAttachmentSize(approval.getAttachmentSize());

        // 첨부파일 내용 설정 (Base64 인코딩된 문자열)
        if (approval.getAttachmentContent() != null && !approval.getAttachmentContent().trim().isEmpty()) {
            response.setAttachmentContent(approval.getAttachmentContent());
        }

        // 🆕 생성/수정 시간 추가
        response.setCreatedAt(approval.getCreatedAt());
        response.setUpdatedAt(approval.getUpdatedAt());

        return response;
    }

    private ApprovalResponse.RequesterInfo toRequesterInfo(User user) {
        // 🛡️ null 체크 추가 (안전성 향상)
        if (user == null) {
            return null;
        }

        ApprovalResponse.RequesterInfo requesterInfo = new ApprovalResponse.RequesterInfo();
        requesterInfo.setUserId(user.getUserId());
        requesterInfo.setEmployeeCode(user.getEmployeeCode());
        requesterInfo.setUsername(user.getUsername());

        // 🛡️ Position과 Department null 체크 (Lazy Loading 고려)
        requesterInfo.setPosition(user.getPosition() != null ?
                user.getPosition().getPositionName() : null);
        requesterInfo.setDepartment(user.getDepartment() != null ?
                user.getDepartment().getDepartmentName() : null);

        return requesterInfo;
    }

    private ApprovalResponse.ApproverInfo toApproverInfo(User user) {
        // 🛡️ null 체크 추가 (안전성 향상)
        if (user == null) {
            return null;
        }

        ApprovalResponse.ApproverInfo approverInfo = new ApprovalResponse.ApproverInfo();
        approverInfo.setUserId(user.getUserId());
        approverInfo.setEmployeeCode(user.getEmployeeCode());
        approverInfo.setUsername(user.getUsername());

        // 🛡️ Position과 Department null 체크 (Lazy Loading 고려)
        approverInfo.setPosition(user.getPosition() != null ?
                user.getPosition().getPositionName() : null);
        approverInfo.setDepartment(user.getDepartment() != null ?
                user.getDepartment().getDepartmentName() : null);

        return approverInfo;
    }
}
