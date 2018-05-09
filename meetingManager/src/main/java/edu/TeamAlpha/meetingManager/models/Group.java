package edu.TeamAlpha.meetingManager.models;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Document(collection = "groups")
public class Group {
    @Id
    private String id;
    private String name;
    @DBRef
    private CustomUserDetails groupCreator;
    @DBRef
    private List<CustomUserDetails> members;

    public Group(){
        members = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGroupCreator(CustomUserDetails groupCreator) {
        this.groupCreator = groupCreator;
    }

    public void setMembers(List<CustomUserDetails> members) {
        this.members = members;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CustomUserDetails> getMembers() {
        return members;
    }

    public void addMember(CustomUserDetails newMember) {
        if (this.members.indexOf(newMember) == -1) {
            this.members.add(newMember);
        }
    }

    public CustomUserDetails getGroupCreator() {
        return groupCreator;
    }

    public String getName() {
        return name;
    }

}
