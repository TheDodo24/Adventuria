package de.thedodo24.commonPackage.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ClickableText {

    private String hoverMessage;
    private String clickMessage;
    private HoverEvent.Action hoverEventAction;
    private ClickEvent.Action clickEventAction;
    private ChatColor chatColor;
    private String message;

    public ClickableText(String message) {
        this.message = message;
    }

    public TextComponent build() {
        TextComponent textComponent = new TextComponent(message);
        if(clickMessage != null) {
            textComponent.setClickEvent(new ClickEvent(clickEventAction, clickMessage));
        }
        if(hoverMessage != null) {
            textComponent.setHoverEvent(new HoverEvent(hoverEventAction, new ComponentBuilder(hoverMessage).create()));
        }
        textComponent.setColor(chatColor);
        return textComponent;
    }

    public ClickableText setHoverMessage(String msg) {
        hoverMessage = msg;
        return this;
    }
    public ClickableText setClickMessage(String msg) {
        clickMessage = msg;
        return this;
    }
    public ClickableText setHoverEventAction(HoverEvent.Action hoverEventAction) {
        this.hoverEventAction = hoverEventAction;
        return this;
    }
    public ClickableText setClickEventAction(ClickEvent.Action clickEventAction) {
        this.clickEventAction = clickEventAction;
        return this;
    }
    public ClickableText setChatColor(ChatColor chatColor) {
        this.chatColor = chatColor;
        return this;
    }

}
