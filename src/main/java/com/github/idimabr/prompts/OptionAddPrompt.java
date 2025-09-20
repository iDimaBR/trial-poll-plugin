package com.github.idimabr.prompts;

import com.github.idimabr.TrialPoll;
import com.github.idimabr.util.ConfigUtil;
import lombok.AllArgsConstructor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class OptionAddPrompt extends StringPrompt {

    private TrialPoll plugin;

    @Override
    public String getPromptText(ConversationContext context) {
        List<String> options = (List<String>) context.getSessionData("options");
        if (options == null) {
            options = new ArrayList<>();
            context.setSessionData("options", options);
        }

        final ConfigUtil messages = plugin.getMessages();
        List<String> finalOptions = options;
        return String.join("\n",
                messages.getStringList("options-prompt")
                        .stream()
                        .map(line -> line
                                .replace("{options}", String.join("\n", finalOptions))
                                .replace("{count}", String.valueOf(finalOptions.size())))
                        .toList()
        );
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (input == null) return this;

        input = input.trim();
        if (input.equalsIgnoreCase("done")) {
            return Prompt.END_OF_CONVERSATION;
        }

        if(input.equalsIgnoreCase("exit")){
            context.setSessionData("options", null);
            return Prompt.END_OF_CONVERSATION;
        }

        List<String> options = (List<String>) context.getSessionData("options");
        if (options == null) {
            options = new ArrayList<>();
        }

        options.add(input);
        context.setSessionData("options", options);
        return this;
    }
}
