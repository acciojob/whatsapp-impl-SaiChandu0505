package com.driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WhatsappService {
    //    @Autowired
    WhatsappRepository wr = new WhatsappRepository();
    public String createUser(String name, String mobile) throws Exception {
        return wr.createUser(name,mobile);

    }
    public Group createGroup(List<User> users) {
        return wr.createGroup(users);
    }

    public int createMessage(String content) {
        return wr.createMessage(content);
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        return wr.sendMessage(message, sender, group);
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        return wr.changeAdmin(approver,user,group);
    }

    public int removeUser(User user)throws Exception {
        return removeUser(user);
    }

    public String findMessage(Date start, Date end, int k) throws Exception{
        return findMessage(start,end,k);
    }
}