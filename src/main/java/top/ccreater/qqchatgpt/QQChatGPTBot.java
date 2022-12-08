package top.ccreater.qqchatgpt;
import io.github.cdimascio.dotenv.Dotenv;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.utils.BotConfiguration;

import java.io.File;

public class QQChatGPTBot {
    public static ChatGPT chatbot;
    public static Long qq;
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        qq = Long.parseLong(dotenv.get("QQ_NUMBER"));
        String password =  dotenv.get("QQ_PASSWORD");
        String device_config_path = dotenv.get("DEVICE_CONFIG_PATH");
        String token = dotenv.get("CHATGPT_TOKEN");
        if(device_config_path == null){
            device_config_path = "./device.json";
        }

        System.out.println(device_config_path);
        File f = new File(device_config_path);
        Bot bot;
        String finalDevice_config_path = device_config_path;
        BotConfiguration conf = new BotConfiguration() {{
            fileBasedDeviceInfo(finalDevice_config_path); // 使用 device.json 存储设备信息
            setProtocol(MiraiProtocol.ANDROID_PHONE); // 切换协议
        }};
        bot = BotFactory.INSTANCE.newBot(qq, password, conf);
        chatbot = ChatGPT.Instance(token);
        bot.login();

        QQChatGPTBot.afterLogin(bot);
    }

    public static void afterLogin(Bot bot) {
        bot.getEventChannel().subscribeAlways(FriendMessageEvent.class, (event) -> {
            System.out.println(event.getMessage().contentToString());
            Long sender = event.getSender().getId();
            if(chatbot==null){
                System.out.println("chatbot未初始化");
                return;
            }
            String message = chatbot.GetChatGPTMessage(sender, event.getMessage().contentToString());
            if(message==""){
                System.out.println("ChatGPT无反应");
                return;
            }else{
                System.out.println(message);
            }
            event.getSubject().sendMessage(new MessageChainBuilder()
                    .append(new QuoteReply(event.getMessage()))
                    .append(message)
                    .build()
            );

        });
        bot.getEventChannel().subscribeAlways(GroupMessageEvent.class, (event) -> {
            String QQMessage = event.getMessage().contentToString();
            Long sender = event.getSender().getId();
            if(!QQMessage.startsWith("@"+qq.toString())){
                return;
            }
            System.out.println(QQMessage);

            if(chatbot==null){
                System.out.println("chatbot未初始化");
                return;
            }
            String message = chatbot.GetChatGPTMessage(sender, QQMessage);
            if(message==""){
                System.out.println("ChatGPT无反应");
                return;
            }else{
                System.out.println(message);
            }
            event.getSubject().sendMessage(new MessageChainBuilder()
                    .append(new QuoteReply(event.getMessage()))
                    .append(message)
                    .build()
            );

        });
    }
}
