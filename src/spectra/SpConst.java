/*
 * Copyright 2016 jagrosh.
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
package spectra;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class SpConst {
    //bot items
    final public static String BOTNAME = "Spectra";
    final public static String VERSION = "3.0";
    final public static String PREFIX = "%%";
    final public static String ALTPREFIX = "//";
    
    
    //discord items
    final public static String JAGROSH_ID = "113156185389092864";
    final public static String JAGZONE_INVITE = "https://discord.gg/0p9LSGoRLu6Pet0k";
    
    //command responses
    final public static String SUCCESS = (char)9989+" ";
    final public static String WARNING = (char)9888+" ";
    final public static String ERROR   = (char)9940+" ";
    
    final public static String NEED_PERMISSION          = ERROR + "**I do not have the proper permissions to do that!**\n"
                                                                + "Please make sure I have the following permissions:\n%s";
    final public static String BANNED_COMMAND           = ERROR + "**That command is unavailable here!**";
    final public static String BANNED_COMMAND_IFADMIN   = "\nTo toggle this command on/off on this server, use `"+PREFIX+"toggle %s`\n"
                                                                + "Alternatively, add `{%s}` to a channel's topic to make it available there";
    final public static String NOT_VIA_DM               = ERROR + "**That command is not available via Direct Message!**";
    final public static String TOO_FEW_ARGS             = ERROR + "**Too few arguments provided**\nTry using `"+PREFIX+"%s help` for more information.";
    
    final private static String INVALID_VALUE            = ERROR + "**Invalid Value:**\n";
    final public static String INVALID_INTEGER          = INVALID_VALUE + "`%s` must be an integer between %s and %s";
    final public static String INVALID_TIME             = INVALID_VALUE + "`%s` must be a measure of time";
    final public static String INVALID_IN_DM            = INVALID_VALUE + "`%s` cannot be included via Direct Message";

    //final public static String ARGUMENT_ERROR_  = ERROR+"**Insufficient or incorrect arguments**:\n";
    //final public static String INVALID_VALUE_   = ERROR+"**Invalid command**\n";
    
    final public static String CANT_HELP                = WARNING + "Help could not be sent because you are blocking Direct Messages!";
    final public static String CANT_SEND                = WARNING + "The command could not be completed because I cannot send messages in %s";
    
    final public static String MULTIPLE_FOUND           = WARNING + "**Multiple %s found matching \"%s\":**";
    final public static String NONE_FOUND               = WARNING + "**No %s found matching \"%s\"**";
    
}