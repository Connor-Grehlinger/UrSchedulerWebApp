package edu.TeamAlpha.meetingManager.controllers;

import com.mongodb.MongoClient;
import edu.TeamAlpha.meetingManager.Service.EmailService;
import edu.TeamAlpha.meetingManager.models.*;
import edu.TeamAlpha.meetingManager.repositories.GroupRepository;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class GroupController {
    @Autowired
    UserDetailsRepository userDetailsRepo;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    MeetingPrototypeRepository meetingPrototypeRepo;
    @Autowired
    MeetingRepository meetingRepo;
    @Autowired
    EmailService emailService;

    MongoTemplate mongoTemplate = new MongoTemplate(new MongoClient(), "meetingManager");

    @RequestMapping(value = {"/my_groups"})
    public String my_groups(Model model, Principal principal) {
        Query query = new Query();
        query.addCriteria(Criteria.where("groupCreator").is(userDetailsRepo.findByUsername(principal.getName())));
        model.addAttribute("my_groups", mongoTemplate.find(query, Group.class));
        return "my_groups";
    }

    @RequestMapping(value = "/create_group", method = RequestMethod.POST)
    public String createGroup(Principal principal, @RequestParam("group_name") String group_name) {
        Group group = new Group();
        group.setGroupCreator(userDetailsRepo.findByUsername(principal.getName()));
        group.setName(group_name);
        groupRepository.save(group);
        return "redirect:/my_groups";
    }

    @RequestMapping(value = "/add_people_into_group", method = RequestMethod.POST)
    public String add_people_into_group(@RequestParam("id") String group_id, Principal principal, @RequestParam("member_username") String member_username) {
        Optional<Group> group = groupRepository.findById(group_id);
        CustomUserDetails newMember = userDetailsRepo.findByUsername(member_username);
        if (group.isPresent() && newMember != null) {
            Group currGroup = group.get();
            currGroup.addMember(newMember);
            groupRepository.save(currGroup);
        }
        return "redirect:my_groups";
    }


    @RequestMapping(value = "/invite_group", method = RequestMethod.POST)
    public String invite_people(@RequestParam("id") String meeting_id, Principal principal, @RequestParam("invitee_groupname") String invitee_groupname) {
        Optional<MeetingPrototype> meetingPrototype = meetingPrototypeRepo.findById(meeting_id);
        Query query = new Query();
        query.addCriteria(Criteria.where("groupCreator").is(userDetailsRepo.findByUsername(principal.getName())));
        query.addCriteria(Criteria.where("name").is(invitee_groupname));
        Group group = mongoTemplate.findOne(query, Group.class);
        if (group != null) {
            List<CustomUserDetails> invitees = group.getMembers();
            for (CustomUserDetails invitee : invitees) {
                Meeting meeting = new Meeting();
                meeting.setMeetingPrototype(meetingPrototype.get());
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
