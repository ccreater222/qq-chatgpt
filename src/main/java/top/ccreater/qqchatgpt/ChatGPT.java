package top.ccreater.qqchatgpt;

import com.github.plexpt.chatgpt.Chatbot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatGPT {
    public static ChatGPT instance;
    public Chatbot chatbot;
    public HashMap<Long, String> conversations;
    public ChatGPT(String sessionToken){
        conversations = new HashMap<>();
        this.chatbot = new Chatbot(sessionToken);
    }
    public static ChatGPT Instance(String sessionToken){
        if (instance == null) {
            instance = new ChatGPT(sessionToken);
        }
        return instance;
    }
    public String GetConversation(Long id) {
        String conversation = conversations.get(id);
        if(conversation == null) {
            conversation = UUID.randomUUID().toString();
            conversations.put(id, conversation);
        }
        return conversation;
    }
    public String GetChatGPTMessage(Long id, String message) {
        try {
            if(this.FilterMessage(message)){
                return "";
            }
            this.chatbot.setParentId(this.GetConversation(id));
            Map<String, Object> result = this.chatbot.getChatResponse(message, "text");
            String result_text = (String) result.get("message");
            this.conversations.put(id, (String) result.get("parent_id"));
            return result_text;
        }catch (Exception e){
            return e.getMessage();
        }
    }
    public Boolean FilterMessage(String message) {
        String[] filted_message =new String[]{"<?xml "};
        for (int i=0;i< filted_message.length;i++) {
            if(message.contains(filted_message[i])){
                return true;
            }
        }
        return false;
    }

}
