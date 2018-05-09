package edu.TeamAlpha.meetingManager.controllers;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.util.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mongodb.MongoClient;
import edu.TeamAlpha.meetingManager.Service.EmailService;
import edu.TeamAlpha.meetingManager.models.CustomUserDetails;
import edu.TeamAlpha.meetingManager.models.Mail;
import edu.TeamAlpha.meetingManager.models.Meeting;
import edu.TeamAlpha.meetingManager.repositories.MeetingRepository;
import edu.TeamAlpha.meetingManager.repositories.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import edu.TeamAlpha.meetingManager.models.MeetingPrototype;
import edu.TeamAlpha.meetingManager.repositories.MeetingPrototypeRepository;

import org.springframework.data.mongodb.core.MongoTemplate;

@Controller
public class MeetingTypeController {

	@Autowired
    MeetingPrototypeRepository meetingPrototypeRepo;
    @Autowired
	UserDetailsRepository userDetailsRepo;
    @Autowired
    MeetingRepository meetingRepo;
    @Autowired
    EmailService emailService;

    MongoTemplate mongoTemplate = new MongoTemplate(new MongoClient(), "meetingManager");

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLoginPage() {
        return "login";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String showSignUpPage() {
        return "signupFailure";
    }


    @RequestMapping(value = "/finalize_meeting", method = RequestMethod.GET)
    public String finalize_meeting(Principal principal, Model model, @RequestParam("id") String meetingprototype_id) {
        Optional<MeetingPrototype> meetingPrototype = meetingPrototypeRepo.findById(meetingprototype_id);
        if (meetingPrototype.isPresent()) {
            System.out.println(meetingPrototype);
            model.addAttribute("prototype", meetingPrototype.get());
            model.addAttribute("name", principal.getName());
            model.addAttribute("id", meetingprototype_id);
            return "finalize_meeting";
        } else {
            System.out.println("can not find the meeting prototype");
            // To do: if prototype == null deal with error!!!!!!
            return "redirect:pending_invitations";
        }
    }

    @RequestMapping(value = "/meetingprototype_detail", method = RequestMethod.GET)
    public String meetingprototype_detail(Model model, @RequestParam("id") String meetingprototype_id) {
        Optional<MeetingPrototype> meetingPrototype = meetingPrototypeRepo.findById(meetingprototype_id);
        if (meetingPrototype.isPresent()) {
//            model.addAttribute("meetingprototype_id", meetingprototype_id);
            model.addAttribute("meetingPrototype", meetingPrototype.get());
        }
        return "meetingprototype_detail";
    }

    @RequestMapping(value = "/addMeetingPrototype", method = RequestMethod.POST)
    public String addMeetingPrototype(Principal principal, @ModelAttribute MeetingPrototype meetingPrototype, @RequestParam("slots") String slots, @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) throws ParseException {
    		String[] periods = slots.split(";;");
    		for(String s : periods) {
    			String[] dateTime = s.split("SPLIT");
    			String date = dateTime[0];
    			String start = dateTime[1];
    			String end = dateTime[2];
					System.out.println(date + " " + start + " " + end);
    			LocalDate Date = LocalDate.parse(date);
				DateTimeFormatter f2 = DateTimeFormatter.ofPattern("hh:mm a");
				LocalTime startDate = LocalTime.parse(start, f2);
				LocalTime endDate = LocalTime.parse(end, f2);
				meetingPrototype.addSlots(Date, startDate, endDate);
    		}
		meetingPrototype.setCreator(userDetailsRepo.findByUsername(principal.getName()));
        try {
            meetingPrototypeRepo.save(meetingPrototype);
        } catch (Exception e){
            return "redirect:/error";
        }
        return "redirect:/home";
    }

    @RequestMapping(value = "/finalize_meeting", method = RequestMethod.POST)
    public String finalizeAMeeting(@RequestParam("id") String meeting_id, Principal principal, Model model,
                                   @RequestParam("date") String date, @RequestParam("startTime") String startTime,
                                   @RequestParam("endTime") String endTime, @RequestParam("message") String message) {
        Optional<MeetingPrototype> meetingPrototype = meetingPrototypeRepo.findById(meeting_id);
        if (meetingPrototype.isPresent()) {
            MeetingPrototype prototype = meetingPrototype.get();
            LocalDate Date = LocalDate.parse(date);
            DateTimeFormatter f2 = DateTimeFormatter.ofPattern("hh:mm a");
            LocalTime startDate = LocalTime.parse(startTime, f2);
            LocalTime endDate = LocalTime.parse(endTime, f2);
            List<MeetingPrototype.TimeSlot> l = prototype.getSlots();
            boolean check = false;
            MeetingPrototype.TimeSlot tmp = null;
            for(MeetingPrototype.TimeSlot t : l) {
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
                CustomUserDetails invitee = userDetailsRepo.findByUsername(principal.getName());
                Query query = new Query();
                query.addCriteria(Criteria.where("meetingPrototype").is(meetingPrototype.get()));
                query.addCriteria(Criteria.where("invitee").is(invitee));
                Meeting meeting = mongoTemplate.findOne(query, Meeting.class);
                meeting.setMeetingPrototype(prototype);
                meeting.setStartTime(startDate);
                meeting.setEndTime(endDate);
                meeting.setStatus(1);
                meeting.setMessage(message);
                meeting.setScheduleType(0);
                meeting.setContact(principal.getName());
                meetingRepo.save(meeting);
                return "acceptSuccess";
            } else {
                return "acceptFailure";
            }
        } else {
            // deal with error!!!!!!
        }
        return "redirect:home";
    }


    @RequestMapping(value = "/reject_meeting", method = RequestMethod.POST)
    public String reject_meeting(@RequestParam("id") String meeting_id, Principal principal) {
        Optional<MeetingPrototype> meetingPrototype = meetingPrototypeRepo.findById(meeting_id);
        if (meetingPrototype.isPresent()) {
            CustomUserDetails invitee = userDetailsRepo.findByUsername(principal.getName());
            Query query = new Query();
            query.addCriteria(Criteria.where("meetingPrototype").is(meetingPrototype.get()));
            query.addCriteria(Criteria.where("invitee").is(invitee));
            Meeting meeting = mongoTemplate.findOne(query, Meeting.class);
            meeting.setStatus(2);//rejected
            meetingRepo.save(meeting);
        }
        return "redirect:pending_invitations";
    }

    @RequestMapping(value = "/invite_people", method = RequestMethod.POST)
    public String invite_people(@RequestParam("id") String meeting_id, Principal principal, @RequestParam("invitee_username") String invitee_username) {
        Optional<MeetingPrototype> meetingPrototype = meetingPrototypeRepo.findById(meeting_id);
        if (meetingPrototype.isPresent()) {
            System.out.println(invitee_username);
            Meeting meeting = new Meeting();
            meeting.setMeetingPrototype(meetingPrototype.get());
            CustomUserDetails invitee = userDetailsRepo.findByUsername(invitee_username);
            if (invitee == null) {
                //throw exception
            } else {
                meeting.setInvitee(invitee);
                meeting.setStatus(0);//set to waiting for response
                meetingRepo.save(meeting);
                Mail mail = new Mail();
                mail.setTo(invitee.getEmail());
                mail.setSubject("Invitation From " + principal.getName());
                mail.setContent("You have been invited to meeting! " +
                        "Please login to to select a time that best works fou you!\n" +
                        "localhost:8080/login");
                emailService.sendSimpleMessage(mail);
            }
        }
        return "redirect:home";
    }
}
