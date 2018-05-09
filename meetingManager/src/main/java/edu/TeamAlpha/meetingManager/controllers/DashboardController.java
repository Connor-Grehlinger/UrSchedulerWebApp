package edu.TeamAlpha.meetingManager.controllers;

import com.mongodb.MongoClient;
import edu.TeamAlpha.meetingManager.Service.EmailService;
import edu.TeamAlpha.meetingManager.models.Meeting;
import edu.TeamAlpha.meetingManager.models.MeetingPrototype;
import edu.TeamAlpha.meetingManager.repositories.MeetingPrototypeRepository;
import edu.TeamAlpha.meetingManager.repositories.MeetingRepository;
import edu.TeamAlpha.meetingManager.repositories.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {
    @Autowired
    MeetingPrototypeRepository meetingPrototypeRepo;
    @Autowired
    UserDetailsRepository userDetailsRepo;
    @Autowired
    MeetingRepository meetingRepo;
    @Autowired
    EmailService emailService;

    MongoTemplate mongoTemplate = new MongoTemplate(new MongoClient(), "meetingManager");

    @RequestMapping(value = {"/home", "/"})
    public String home(Model model, Principal principal) {
        model.addAttribute("myname", principal.getName());
        Query query = new Query();
        query.addCriteria(Criteria.where("creator").is(userDetailsRepo.findByUsername(principal.getName())));
        model.addAttribute("meetingprototypes_list",  mongoTemplate.find(query, MeetingPrototype.class));
        return "meetingprototypes_list";
    }

    @RequestMapping(value = {"/pending_invitations"})
    public String pending_invitations(Model model, Principal principal) {
        Query query = new Query();
        query.addCriteria(Criteria.where("status").is(0));
        query.addCriteria(Criteria.where("invitee").is(userDetailsRepo.findByUsername(principal.getName())));
        model.addAttribute("pending_meetings", mongoTemplate.find(query, Meeting.class));
        return "pending_invitations";
    }

    @RequestMapping("/create_meeting")
    public String create_meeting(Model model, Principal principal) {
        model.addAttribute("url", "/schedule/"+principal.getName()+"/");
        return "create_meeting";
    }

    @RequestMapping(value = {"/my_meetings"})
    public String upcoming_meetings(Model model, Principal principal) {
        model.addAttribute("myname", principal.getName());
        Query query = new Query();
        query.addCriteria(Criteria.where("creator").is(userDetailsRepo.findByUsername(principal.getName())));
        List<MeetingPrototype> meetingPrototypeList = mongoTemplate.find(query, MeetingPrototype.class);
        List<Meeting> meetings_list = new ArrayList<>();
        for (MeetingPrototype meetingPrototype : meetingPrototypeList) {
            query = new Query();
            query.addCriteria(Criteria.where("status").is(1));//accepted meeting
//            query.addCriteria(Criteria.where("endTime").le())
            //To do: new criteria that make sure the meeting is in the future
            query.addCriteria(Criteria.where("meetingPrototype").is(meetingPrototype));
            meetings_list.addAll(mongoTemplate.find(query, Meeting.class));
        }
        model.addAttribute("meetings_list", meetings_list);
        return "meetings_list";
    }
}
