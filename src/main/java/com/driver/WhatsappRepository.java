package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String  createUser(String name, String phno) throws Exception {
        if(userMobile.contains(phno)){
            throw new Exception("User already exists");
        }
        User user = new User(name,phno);
        userMobile.add(phno);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users){
        String groupName = "";
        if(users.size()>2)
        {
            customGroupCount++;
            groupName="Group "+ customGroupCount;
        }
        else {
            groupName = users.get(1).getName();
        }
        Group group=new Group(groupName,users.size());
        groupUserMap.put(group,users);
        adminMap.put(group,users.get(0));
        return group;
    }

    public int createMessage(String content) {
        messageId++;
        Message message=new Message(messageId,content);
        return message.getId();
    }

    public int sendMessage(Message message, User sender, Group group)throws Exception {
        if(!groupUserMap.containsKey(group))
        {
            throw new Exception("Group does not exist");
        }
        List<User>groupList=groupUserMap.get(group);
        boolean flag=false;
        for(User user:groupList) {
            if (user.equals(sender)) {
                flag=true;
                break;
            }
        }
        if (flag==false)
        {
            throw new Exception("You are not allowed to send message");
        }
        List<Message>msgList=new ArrayList<>();
        for(Group g:groupMessageMap.keySet())
        {
            msgList=groupMessageMap.get(group);
        }
        msgList.add(message);
        groupMessageMap.put(group,msgList);
        return msgList.size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if(!groupUserMap.containsKey(group))
        {
            throw new Exception("Group does not exist");
        }
        User val=adminMap.get(group);
        if(!val.equals(approver))
        {
            throw new Exception("Approver does not have rights");
        }
        boolean userFlag=false;
        List<User>participantList=groupUserMap.get(group);
        for(User u:participantList)
        {
            if(u.equals(user))
            {
                userFlag=true;
                break;
            }
        }
        if ((userFlag==false))
        {
            throw new Exception("User is not a participant");
        }
        User old=adminMap.get(group);
        adminMap.replace(group,old,user);
        return "SUCCESS";
    }

    public int removeUser(User user) throws Exception {
        boolean userFound=false;
        Group userGroup=null;
        for(Group g: groupUserMap.keySet())
        {
            if(groupUserMap.get(g).contains(user))
            {
                userGroup=g;
                if(adminMap.get(userGroup).equals(user))
                {
                    throw new Exception("Cannot remove admin");
                }
            }
            userFound=true;
            break;
        }
        if(userFound==false)
        {
            throw new Exception("User not found");
        }
        List<User> userList = groupUserMap.get(userGroup);
        List<User> updatedUserList = new ArrayList<>();

        for(User user1 : userList){
            if(user1.equals(user)){
                continue;
            }
            updatedUserList.add(user1);
        }
        groupUserMap.put(userGroup, updatedUserList);

        List<Message> messageList = groupMessageMap.get(userGroup);
        List<Message> updatedMessageList = new ArrayList<>();

        for(Message message : messageList){
            if(senderMap.get(message).equals(user)){
                continue;
            }
            updatedMessageList.add(message);
        }
        groupMessageMap.put(userGroup, updatedMessageList);

        HashMap<Message, User> updatedSenderMap = new HashMap<>();
        for(Message message : senderMap.keySet()){
            if(senderMap.get(message).equals(user)){
                continue;
            }
            updatedSenderMap.put(message, senderMap.get(message));
        }

        senderMap = updatedSenderMap;

        return updatedUserList.size() + updatedMessageList.size() + updatedSenderMap.size();
    }

    public String findMessage(Date start, Date end, int K) throws Exception {
        List<Message> messageList = new ArrayList<>();
        for(Group group : groupUserMap.keySet()){
            messageList.addAll(groupMessageMap.get(group));
        }

        List<Message> filteredMessageList = new ArrayList<>();
        for(Message message : messageList){
            if(message.getTimestamp().after(start) && message.getTimestamp().before(end)){
                filteredMessageList.add(message);
            }
        }

        if(filteredMessageList.size()< K){
            throw new Exception("K is greater than the number of messages");
        }

        Collections.sort(filteredMessageList, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                return m2.getTimestamp().compareTo(m1.getTimestamp());
            }
        });

        return filteredMessageList.get(K-1).getContent();
    }

    public String deleteGroup(Group group, User user){

        if(!groupUserMap.containsKey(group)) return "not possible";

        if(!adminMap.get(group).equals(user)) return "not possible";

        groupUserMap.remove(group);
        groupMessageMap.remove(group);
        adminMap.remove(group);

        return "Success";
    }
}
