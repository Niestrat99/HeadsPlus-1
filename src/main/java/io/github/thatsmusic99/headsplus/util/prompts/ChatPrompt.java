package io.github.thatsmusic99.headsplus.util.prompts;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class ChatPrompt extends StringPrompt {


    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return HeadsPlus.getInstance().getMessagesConfig().getString("commands.heads.chat-input", (Player) conversationContext.getForWhom());
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        conversationContext.setSessionData("term", s);
        return END_OF_CONVERSATION;
    }
}
