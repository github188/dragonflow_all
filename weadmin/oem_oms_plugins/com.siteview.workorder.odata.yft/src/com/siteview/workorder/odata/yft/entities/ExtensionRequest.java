package com.siteview.workorder.odata.yft.entities;

/**
 * 延期单
 * 
 * @author Administrator
 *
 */
public class ExtensionRequest {
	private String id; // 申请单ID
	private String requestTime; // 申请时间
	private String requester; // 申请人
	private String requesterPhone; // 申请人电话
	private String status; // 状态
	private String approver; // 审批人
	private String approvalTime;// 审批时间
	private String serialNumber; // 申请单号
	private String workOrderNum; // 工单号
	private String extendTime; // 延长时间
	private String reason; // 申请原因
	private String opinion; // 审批意见

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	public String getRequester() {
		return requester;
	}

	public void setRequester(String requester) {
		this.requester = requester;
	}

	public String getRequesterPhone() {
		return requesterPhone;
	}

	public void setRequesterPhone(String requesterPhone) {
		this.requesterPhone = requesterPhone;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}

	public String getApprovalTime() {
		return approvalTime;
	}

	public void setApprovalTime(String approvalTime) {
		this.approvalTime = approvalTime;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getWorkOrderNum() {
		return workOrderNum;
	}

	public void setWorkOrderNum(String workOrderNum) {
		this.workOrderNum = workOrderNum;
	}

	public String getExtendTime() {
		return extendTime;
	}

	public void setExtendTime(String extendTime) {
		this.extendTime = extendTime;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

}
