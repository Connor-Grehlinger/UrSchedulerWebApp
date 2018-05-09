package edu.TeamAlpha.meetingManager.controllers;
import com.mongodb.MongoClient;
import edu.TeamAlpha.meetingManager.repositories.UserDetailsRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import edu.TeamAlpha.meetingManager.Service.EmailService;
import edu.TeamAlpha.meetingManager.models.Mail;
import edu.TeamAlpha.meetingManager.models.Meeting;
import edu.TeamAlpha.meetingManager.models.MeetingPrototype;
import edu.TeamAlpha.meetingManager.models.MeetingPrototype.TimeSlot;
import edu.TeamAlpha.meetingManager.repositories.MeetingPrototypeRepository;
import edu.TeamAlpha.meetingManager.repositories.MeetingRepository;

import org.springframework.data.mongodb.core.MongoTemplate;

@Controller
public class ScheduleMeetingController {
    @Autowired
    EmailService emailService;
    @Autowired
    MeetingRepository meetingRepo;
	@Autowired
    MeetingPrototypeRepository meetingPrototypeRepo;
    @Autowired
	UserDetailsRepository userDetailsRepository;
    MongoTemplate mongoTemplate = new MongoTemplate(new MongoClient(), "meetingManager");
    @RequestMapping("/schedule/{name}/{id}")
    public String schedule(@PathVariable String id, @PathVariable String name, Model model){
    		Query query = new Query();
    		query.addCriteria(Criteria.where("creator").is(userDetailsRepository.findByUsername(name)));
    		query.addCriteria(Criteria.where("url").is(id));
    		MeetingPrototype prototype = mongoTemplate.findOne(query, MeetingPrototype.class);
    		model.addAttribute("prototype", prototype);
    		model.addAttribute("name", name);
    		model.addAttribute("id", id);
    		return "scheduleNoLogin";
    }

    @RequestMapping(value = "/schedule/{name}/{id}", method = RequestMethod.POST)
    public String setSchedule(@PathVariable String id, @PathVariable String name, Model model,
                              @RequestParam("date") String date, @RequestParam("startTime") String startTime,
                              @RequestParam("endTime") String endTime, @RequestParam("message") String message,
                              @RequestParam("email") String email){
		Query query = new Query();
		query.addCriteria(Criteria.where("creator").is(userDetailsRepository.findByUsername(name)));
		query.addCriteria(Criteria.where("url").is(id));
		MeetingPrototype prototype = mongoTemplate.findOne(query, MeetingPrototype.class);
		LocalDate Date = LocalDate.parse(date);
		DateTimeFormatter f2 = DateTimeFormatter.ofPattern("hh:mm a");
		LocalTime startDate = LocalTime.parse(startTime, f2);
		LocalTime endDate = LocalTime.parse(endTime, f2);
		List<TimeSlot> l = prototype.getSlots();
		boolean check = false;
		TimeSlot tmp = null;
		for(TimeSlot t : l) {
			if(Date.isEqual(t.date)){
				if(startDate.isAfter(t.startTime) && endDate.isBefore(t.endTime)){
					check = true;
					tmp = t;
					break;
				}
			}
		}
		if(check) {
			prototype.addSlots(Date, tmp.startTime, startDate);
			prototype.addSlots(Date, endDate, tmp.endTime);
			prototype.deleteSlot(tmp);
			meetingPrototypeRepo.save(prototype);
			Meeting meeting = new Meeting();
            meeting.setMeetingPrototype(prototype);
            meeting.setStartTime(startDate);
            meeting.setEndTime(endDate);
            meeting.setStatus(1);
            meeting.setMessage(message);
            meeting.setScheduleType(1);
            meeting.setContact(email);
            Mail mail = new Mail();
            mail.setTo(email);
            mail.setSubject("Meeting Confirmation");
            mail.setContent("Dear Our Customer, " + '\n' + '\n' + "You just scheduled a meeting with " + name + " in " +
            					Date + " from " + startDate + " to " + endDate + ". Please be aware that " + name +
            					" may contact you via this email address and of course, don't forget this meeting!" + '\n' +
                      '\n'+"UrScheduler");
            emailService.sendSimpleMessage(mail);
            meetingRepo.save(meeting);
		} else {
			model.addAttribute("name", name);
			model.addAttribute("id", id);
			return "scheduleFailure";
		}
    		return "scheduleSuccess";
    }

    @RequestMapping("/schedule/{name}")
    public String getMeetingTypes(@PathVariable String name, Model model){
        Query query = new Query();
        query.addCriteria(Criteria.where("creator").is(userDetailsRepository.findByUsername(name)));
        model.addAttribute("type_list",  mongoTemplate.find(query, MeetingPrototype.class));
    		model.addAttribute("name", name);
    		return "scheduleMain";
    }

}
