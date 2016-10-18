package de.symeda.sormas.backend.task;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.User;

@Entity
public class Task extends AbstractDomainObject {

	private static final long serialVersionUID = -4754578341242164661L;

	public static final String ASSIGNEE_REPLY = "assigneeReply";
	public static final String ASSIGNEE_USER = "assigneeUser";
	public static final String CAZE = "caze";
	public static final String CONTACT = "contact";
	public static final String CREATOR_COMMENT = "creatorComment";
	public static final String CREATOR_USER = "creatorUser";
	public static final String PRIORITY = "priority";
	public static final String DUE_DATE = "dueDate";
	public static final String SUGGESTED_START = "suggestedStart";
	public static final String EVENT = "event";
	public static final String PERCEIVED_START = "perceivedStart";
	public static final String STATUS_CHANGE_DATE = "statusChangeDate";
	public static final String TASK_CONTEXT = "taskContext";
	public static final String TASK_STATUS = "taskStatus";
	public static final String TASK_TYPE = "taskType";
	
	private TaskContext taskContext;
	private Case caze;
	
	private TaskType taskType;
	private TaskPriority priority;
	private Date dueDate;
	private Date suggestedStart;
	private TaskStatus taskStatus;
	private Date statusChangeDate;
	private Date perceivedStart;
	
	private User creatorUser;
	private String creatorComment;
	private User assigneeUser;
	private String assigneeReply;
	
	@Enumerated(EnumType.STRING)
	public TaskContext getTaskContext() {
		return taskContext;
	}
	public void setTaskContext(TaskContext taskContext) {
		this.taskContext = taskContext;
	}
	
	@ManyToOne(cascade = {})
	public Case getCaze() {
		return caze;
	}
	public void setCaze(Case caze) {
		this.caze = caze;
	}
	
	@Enumerated(EnumType.STRING)
	public TaskType getTaskType() {
		return taskType;
	}
	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	
	@Enumerated(EnumType.STRING)
	public TaskStatus getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getStatusChangeDate() {
		return statusChangeDate;
	}
	public void setStatusChangeDate(Date statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getPerceivedStart() {
		return perceivedStart;
	}
	public void setPerceivedStart(Date perceivedStart) {
		this.perceivedStart = perceivedStart;
	}
	
	@ManyToOne(cascade = {})
	public User getCreatorUser() {
		return creatorUser;
	}
	public void setCreatorUser(User creatorUser) {
		this.creatorUser = creatorUser;
	}
	
	@Column(length=512)
	public String getCreatorComment() {
		return creatorComment;
	}
	public void setCreatorComment(String creatorComment) {
		this.creatorComment = creatorComment;
	}
	
	@ManyToOne(cascade = {})
	public User getAssigneeUser() {
		return assigneeUser;
	}
	public void setAssigneeUser(User assigneeUser) {
		this.assigneeUser = assigneeUser;
	}
	
	@Column(length=512)
	public String getAssigneeReply() {
		return assigneeReply;
	}
	public void setAssigneeReply(String assigneeReply) {
		this.assigneeReply = assigneeReply;
	}
	
	@Enumerated(EnumType.STRING)
	public TaskPriority getPriority() {
		return priority;
	}
	public void setPriority(TaskPriority priority) {
		this.priority = priority;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getSuggestedStart() {
		return suggestedStart;
	}
	public void setSuggestedStart(Date suggestedStart) {
		this.suggestedStart = suggestedStart;
	}
}
