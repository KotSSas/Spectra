/*
 * Copyright 2016 John Grosh (jagrosh).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spectra.commands;

import java.util.List;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.utils.PermissionUtil;
import spectra.Argument;
import spectra.Command;
import spectra.FeedHandler;
import spectra.PermLevel;
import spectra.Sender;
import spectra.SpConst;
import spectra.datasources.Feeds;
import spectra.datasources.Settings;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class Ban extends Command {
    final FeedHandler handler;
    final Settings settings;
    public Ban(FeedHandler handler, Settings settings)
    {
        this.handler = handler;
        this.settings = settings;
        this.command = "ban";
        this.help = "bans a user from the server and deletes all messages in the past 7 days";
        this.arguments = new Argument[]{
            new Argument("username",Argument.Type.LOCALUSER,true),
            new Argument("for <reason>",Argument.Type.LONGSTRING,false)
        };
        this.separatorRegex = "\\s+for\\s+";
        this.availableInDM=false;
        this.level = PermLevel.MODERATOR;
        this.requiredPermissions = new Permission[] {
            Permission.BAN_MEMBERS
        };
        this.children = new Command[]{
            new BanHack(),
            new BanList()
        };
    }
    @Override
    protected boolean execute(Object[] args, MessageReceivedEvent event) {
        User target = (User)(args[0]);
        String reason = args[1]==null?null:(String)(args[1]);
        if(reason==null)
            reason = "[no reason specified]";
        PermLevel targetLevel = PermLevel.getPermLevelForUser(target, event.getGuild(),settings.getSettingsForGuild(event.getGuild().getId()));
        //check perm level of other user
        if(targetLevel.isAtLeast(level))
        {
            Sender.sendResponse(SpConst.WARNING+"**"+target.getUsername()+"** cannot be banned because they are listed as "+targetLevel, event);
            return false;
        }
        
        //check if bot can interact with the other user
        if(!PermissionUtil.canInteract(event.getJDA().getSelfInfo(), target, event.getGuild()))
        {
            Sender.sendResponse(SpConst.WARNING+"I cannot ban **"+target.getUsername()+"** due to permission hierarchy", event);
            return false;
        }
        
        //attempt to ban
        try{
            String id = target.getId();
            event.getGuild().getManager().ban(id, 7);
            Sender.sendResponse(SpConst.SUCCESS+"**"+target.getUsername()+"** was banned from the server \uD83D\uDD28", event);
            handler.submitText(Feeds.Type.MODLOG, event.getGuild(), 
                    "\uD83D\uDD28 **"+event.getAuthor().getUsername()+"** banned **"+target.getUsername()+"** for "+reason);
            return true;
        }catch(Exception e)
        {
            Sender.sendResponse(SpConst.ERROR+"Failed to ban **"+target.getUsername()+"**", event);
            return false;
        }
    }
    
    private class BanHack extends Command {
        public BanHack()
        {
            this.command = "hack";
            this.help = "bans a user by id, even if they aren't on the server";
            this.arguments = new Argument[]{
                new Argument("id",Argument.Type.SHORTSTRING,true),
                new Argument("for <reason>",Argument.Type.LONGSTRING,false)
            };
            this.separatorRegex = "\\s+for\\s+";
            this.availableInDM=false;
            this.level = PermLevel.MODERATOR;
            this.requiredPermissions = new Permission[] {
                Permission.BAN_MEMBERS
            };
        }
        @Override
    protected boolean execute(Object[] args, MessageReceivedEvent event) {
        String id = (String)(args[0]);
        String reason = args[1]==null?null:(String)(args[1]);
        if(reason==null)
            reason = "[no reason specified]";
        User target = event.getJDA().getUserById(id);
        if(target!=null && event.getGuild().isMember(target))
        {
            PermLevel targetLevel = PermLevel.getPermLevelForUser(target, event.getGuild(),settings.getSettingsForGuild(event.getGuild().getId()));
            //check perm level of other user
            if(targetLevel.isAtLeast(level))
            {
                Sender.sendResponse(SpConst.WARNING+"**"+target.getUsername()+"** cannot be banned because they are listed as "+targetLevel, event);
                return false;
            }
        
            //check if bot can interact with the other user
            if(!PermissionUtil.canInteract(event.getJDA().getSelfInfo(), target, event.getGuild()))
            {
                Sender.sendResponse(SpConst.WARNING+"I cannot ban **"+target.getUsername()+"** due to permission hierarchy", event);
                return false;
            }
        }
        //attempt to ban
        try{
            event.getGuild().getManager().ban(id, 7);
            Sender.sendResponse(SpConst.SUCCESS+(target==null ? "User with ID:"+id : "**"+target.getUsername()+"**")+" was banned from the server \uD83D\uDD28", event);
            handler.submitText(Feeds.Type.MODLOG, event.getGuild(), 
                    "\uD83D\uDD28 **"+event.getAuthor().getUsername()+"** banned "+(target==null ? "User with ID:"+id : "**"+target.getUsername()+"**")+" for "+reason);
            return true;
        }catch(Exception e)
        {
            Sender.sendResponse(SpConst.ERROR+"Failed to ban "+(target==null ? "User with ID:"+id : "**"+target.getUsername()+"**"), event);
            return false;
        }
    }
    }
    
    private class BanList extends Command {
        public BanList()
        {
            this.command = "list";
            this.help = "lists the users currently banned from the server";
            this.availableInDM=false;
            this.level = PermLevel.MODERATOR;
            this.requiredPermissions = new Permission[] {
                Permission.BAN_MEMBERS
            };
        }
        @Override
        protected boolean execute(Object[] args, MessageReceivedEvent event) {
            List<User> list = event.getGuild().getManager().getBans();
            if(list.isEmpty())
            {
                Sender.sendResponse(SpConst.WARNING+"There are no banned users!", event);
                return true;
            }
            StringBuilder builder = new StringBuilder(SpConst.SUCCESS).append("**").append(list.size()).append("** users banned on **").append(event.getGuild().getName()).append("**:");
            list.stream().forEach((u) -> {
                builder.append("\n**").append(u.getUsername()).append("** (ID:").append(u.getId()).append(")");
            });
            Sender.sendResponse(builder.toString(), event);
            return true;
        }
        
    }
}
