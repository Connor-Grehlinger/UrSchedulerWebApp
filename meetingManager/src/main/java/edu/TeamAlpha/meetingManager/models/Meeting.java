package edu.TeamAlpha.meetingManager.models;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalTime;
import java.util.Date;

@Document(collection = "meetings")
public class Meeting {
    @Id
    private String id;
    private LocalTime startTime;
    private LocalTime endTime;
    private String message;
    private String contact;
    private int scheduleType;     //1 for anonymous meeting setup. 0 for non-anonymous
    @DBRef
    private MeetingPrototype meetingPrototype;
    @DBRef
    private CustomUserDetails invitee;
    private int status;//0: waiting for response. 1: accepted 2: rejected
//    @DBRef
//    private CustomUserDetails interviewer;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setStartTime(LocalTime startDate) {
        this.startTime = startDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setMeetingPrototype(MeetingPrototype meetingPrototype) {
        this.meetingPrototype = meetingPrototype;
    }

    public MeetingPrototype getMeetingPrototype() {
        return meetingPrototype;
    }

    public CustomUserDetails getInvitee() {
        return invitee;
    }

    public void setInvitee(CustomUserDetails invitee) {
        this.invitee = invitee;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	public int getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(int scheduleType) {
		this.scheduleType = scheduleType;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

    //    public CustomUserDetails getInterviewer() {
//        return interviewer;
//    }
//
//    public void setInterviewer(CustomUserDetails interviewer) {
//        this.interviewer = interviewer;
//    }
}
