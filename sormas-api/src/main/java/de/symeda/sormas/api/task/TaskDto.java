package de.symeda.sormas.api.task;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.PreciseDateAdapter;

public class TaskDto extends DataTransferObject {

	private static final long serialVersionUID = 2439546041916003653L;

	public static final String I18N_PREFIX = "Task";
	
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
	private ReferenceDto caze;
	private ReferenceDto event;
	private ReferenceDto contact;
	
	private TaskType taskType;
	private TaskPriority priority;
	private Date dueDate;
	private Date suggestedStart;
	private TaskStatus taskStatus;
	private Date statusChangeDate;
	private Date perceivedStart;
	
	private ReferenceDto creatorUser;
	private String creatorComment;
	private ReferenceDto assigneeUser;
	private String assigneeReply;
	

	public TaskContext getTaskContext() {
		return taskContext;
	}
	public void setTaskContext(TaskContext taskContext) {
		this.taskContext = taskContext;
	}
	public ReferenceDto getCaze() {
		return caze;
	}
	public void setCaze(ReferenceDto caze) {
		this.caze = caze;
	}
	public ReferenceDto getEvent() {
		return event;
	}
	public void setEvent(ReferenceDto event) {
		this.event = event;
	}
	public ReferenceDto getContact() {
		return contact;
	}
	public void setContact(ReferenceDto contact) {
		this.contact = contact;
	}
	public TaskType getTaskType() {
		return taskType;
	}
	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getDueDate() {
		return dueDate;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getSuggestedStart() {
		return suggestedStart;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setSuggestedStart(Date suggestedStart) {
		this.suggestedStart = suggestedStart;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getStatusChangeDate() {
		return statusChangeDate;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setStatusChangeDate(Date statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getPerceivedStart() {
		return perceivedStart;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setPerceivedStart(Date perceivedStart) {
		this.perceivedStart = perceivedStart;
	}
	public ReferenceDto getCreatorUser() {
		return creatorUser;
	}
	public void setCreatorUser(ReferenceDto creatorUser) {
		this.creatorUser = creatorUser;
	}
	public String getCreatorComment() {
		return creatorComment;
	}
	public void setCreatorComment(String creatorComment) {
		this.creatorComment = creatorComment;
	}
	public ReferenceDto getAssigneeUser() {
		return assigneeUser;
	}
	public void setAssigneeUser(ReferenceDto assigneeUser) {
		this.assigneeUser = assigneeUser;
	}
	public String getAssigneeReply() {
		return assigneeReply;
	}
	public void setAssigneeReply(String assigneeReply) {
		this.assigneeReply = assigneeReply;
	}
	public TaskPriority getPriority() {
		return priority;
	}
	public void setPriority(TaskPriority priority) {
		this.priority = priority;
	}
}
