package edu.TeamAlpha.meetingManager.models;
import org.bson.types.ObjectId;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

import java.util.Collection;
import java.util.List;
@CompoundIndexes(value = {
		@CompoundIndex(name = "unique_url",
				unique = true,
				def = "{'url' : 1, 'creator' : 1}")
})
@Document(collection = "meetingPrototypes")
public class MeetingPrototype {
	public class TimeSlot {
		public LocalTime startTime;
		public LocalTime endTime;
		public LocalDate date;
		public TimeSlot(LocalDate date, LocalTime startTime, LocalTime endTime) {
			this.date = date;
			this.startTime = startTime;
			this.endTime = endTime;
		}
		
		LocalDate getDate() {
			return date;
		}
		
		LocalTime getStartTime() {
			return startTime;
		}
		LocalTime getEndTime() {
			return endTime;
		}
	}
	@Id
	private String id;
	private String name;
	private boolean isOneOnOne;
	private String location;
	private String instruction;
	private int duration;
	private List<TimeSlot> slots;
	private String url;
	@DBRef
	private CustomUserDetails creator;
	//private Date meeetingDate; // future work will extend this class with MeetingDate class to add more fields

	public MeetingPrototype(){
		slots = new ArrayList<>();
	}

	// public void setId(int id) {
	// 	this.id = id;
	// }
	//
	public String getId() {
		return id;
	}

	public void setIsOneOnOne(boolean isOneOnOne) {
		this.isOneOnOne = isOneOnOne;
	}

	public boolean getIsOneOnOne() {
		return isOneOnOne;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getDuration() {
		return duration;
	}

	public void setCreator(CustomUserDetails creator) {
		this.creator = creator;
	}

	public CustomUserDetails getCreator() {
		return creator;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public List<TimeSlot> getSlots() {
		return slots;
	}

	public void addSlots(LocalDate date, LocalTime startTime, LocalTime endTime) {
		this.slots.add(new TimeSlot(date, startTime, endTime));
	}
	
	public void deleteSlot(TimeSlot t) {
		slots.remove(t);
	}

	public String getUrl() {
		return "localhost:8080/schedule/" + creator.getUsername() + "/" + url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
