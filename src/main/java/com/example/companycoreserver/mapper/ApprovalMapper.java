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
        response.setAttachmentPath(approval.getAttachmentPath());
        return response;
    }

    private ApprovalResponse.RequesterInfo toRequesterInfo(User user) {
        ApprovalResponse.RequesterInfo requesterInfo = new ApprovalResponse.RequesterInfo();
        requesterInfo.setUserId(user.getUserId());
        requesterInfo.setEmployeeCode(user.getEmployeeCode());
        requesterInfo.setUsername(user.getUsername());
        requesterInfo.setPosition(user.getPosition().getPositionName());
        requesterInfo.setDepartment(user.getDepartment().getDepartmentName());
        return requesterInfo;
    }

    private ApprovalResponse.ApproverInfo toApproverInfo(User user) {
        ApprovalResponse.ApproverInfo approverInfo = new ApprovalResponse.ApproverInfo();
        approverInfo.setUserId(user.getUserId());
        approverInfo.setEmployeeCode(user.getEmployeeCode());
        approverInfo.setUsername(user.getUsername());
        approverInfo.setPosition(user.getPosition().getPositionName());
        approverInfo.setDepartment(user.getDepartment().getDepartmentName());
        return approverInfo;
    }
}
