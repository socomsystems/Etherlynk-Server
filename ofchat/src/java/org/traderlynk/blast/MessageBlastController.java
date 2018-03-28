package org.traderlynk.blast;

import java.util.*;
import java.util.Date;
import java.util.concurrent.*;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jivesoftware.openfire.plugin.rawpropertyeditor.RawPropertyEditor;
import org.jivesoftware.openfire.spi.XMPPServerInfoImpl;
import org.dom4j.Element;
import org.xmpp.packet.*;
import org.traderlynk.blast.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.jivesoftware.openfire.user.User;
import net.sf.json.*;
import java.util.concurrent.*;
import org.xmpp.packet.*;
import org.jivesoftware.openfire.user.UserManager;


public class MessageBlastController {

    private static final Logger Log = LoggerFactory.getLogger(MessageBlastController.class);
    public static final MessageBlastController INSTANCE = new MessageBlastController();
    public ConcurrentHashMap<String, MessageBlastSentEntity> blastSents = new ConcurrentHashMap<String, MessageBlastSentEntity>();

    public static MessageBlastController getInstance() {
        return INSTANCE;
    }

    public void RetryIncompleteBlasts() {
        Log.debug("MessageBlastController Retrying Incomplete Blasts");
        getIncompleteMessageBlasts();
    }

    public List<MessageBlastSentEntity> getIncompleteMessageBlasts() {
        Collection userCollection = XMPPServer.getInstance().getUserManager().getUsers();
        List<User> usersList;
        List<MessageBlastSentEntity> incompleteBlasts = new ArrayList<MessageBlastSentEntity>();

        if (userCollection instanceof List) {
            usersList = (List) userCollection;
        } else {
            usersList = new ArrayList(userCollection);
        }

        if (usersList.size() > 0) {
            for (int i = 0; i < usersList.size(); i++) {
                User user = usersList.get(i);
                for (String key : user.getProperties().keySet()) {
                    if (key.contains("belfry.blast.message")) {
                        try {
                            JSONObject bm = new JSONObject(user.getProperties().get(key));
                            if (bm.getBoolean("completed") == false) {
                                MessageBlastSentEntity mbse = new MessageBlastSentEntity();
                                mbse.setId(bm.getString("id"));
                                mbse.setTitle(bm.getString("title"));
                                mbse.setRecipientsCount(bm.getInt("recipientsCount"));
                                mbse.setSentCount(bm.getInt("sentCount"));
                                mbse.setRecieveCount(bm.getInt("recieveCount"));
                                mbse.setReadCount(bm.getInt("readCount"));
                                mbse.setRespondCount(bm.getInt("respondCount"));
                                mbse.setCompleted(bm.getBoolean("completed"));
                                mbse.setSentDate(bm.getString("sentDate"));
                                mbse.setOpenfireUser(bm.getString("openfireUser"));
                                MessageBlastEntity mbe = new MessageBlastEntity(bm.getJSONObject("mbe"));
                                mbse.setMessageBlastEntity(mbe);
                                incompleteBlasts.add(mbse);
                                routeMessageBlast(user.getUsername(), mbe, bm.getString("id"));
                            }
                        } catch (Exception e) {
                            Log.error("MessageBlastController Failed to parse an incomplete message blast:",
                                    e.getMessage());
                        }
                    }
                }
            }
        }
        return incompleteBlasts;
    }

    public JSONObject routeMessageBlast(String fromUser, MessageBlastEntity mbe, String id) {
        JSONObject response = new JSONObject();
        /*
            {
              "ackRequired": true,
              "dateToSend": "string",
              "highImportance": true,
              "message": "halt and catch fire",
              "messagehtml": "<b>halt</b> and catch <font color='red'>fire</font>",
              "recipients": [
                "mark.briant-evans@tlk.lan"
              ],
              "replyTo": "twopartycall@tlk.lan",
              "sender": "deleo@tlk.lan",
              "sendlater": "false",
              "dateToStart": "",
              "crontrigger": "",
              "cronstoptrigger": "",
              "dateToStop": "",
              "title": "Relaunch??"
            }
        */

        if(!mbe.hasNulls())
        {
            try {
                JSONObject mbejson = new JSONObject();
                mbejson.put("sendlater", mbe.getSendlater());
                mbejson.put("from", mbe.getSender());
                //sender is being lost
                mbejson.put("subject", mbe.getTitle());
                mbejson.put("replyTo", mbe.getReplyTo());

                if (mbe.getHighImportance()) {
                    mbejson.put("importance", "Urgent");
                } else {
                    mbejson.put("importance", "Normal");
                }

                mbejson.put("body", mbe.getMessage());
                mbejson.put("bodyhtml", mbe.getMessagehtml());
                mbejson.put("to", "");
                mbejson.put("action", "blast_message");

                //Set a user prop that is for this blast title, count,total,completed

                MessageBlastSentEntity mbse = new MessageBlastSentEntity();

                if (id != null) {
                    mbse.setId(id);
                } else {
                    mbse.setId("blast-" + System.currentTimeMillis());
                }

                mbse.setTitle(mbe.getTitle());
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                mbse.setSentDate(dateFormat.format(date));
                mbse.setMessageBlastEntity(mbe);
                mbse.setOpenfireUser(fromUser);

                // keep Message Blast Sent Entity for tracking

                blastSents.put(mbse.getId(), mbse);
                mbejson.put("id", mbse.getId()); /* BOA we need id to track */

                int recipientsSize = mbe.getRecipients().size();
                int recipientsCount = 0;

                // get count of recipients less groups

                for (int i = 1; i < recipientsSize; i++)
                {
                    if (mbe.getRecipients().get(i).contains("@")) recipientsCount++;
                }

                String hostname = XMPPServer.getInstance().getServerInfo().getHostname();
                String trustedUser = JiveGlobals.getProperty("skype.trusted.user." + hostname, null);

                // with trusted app, we send whole batch in order to perform
                // a bulk presence query in a single request

                if (trustedUser != null)
                {
                    String to = addSipUri(mbe.getRecipients().get(0));

                    for (int i = 1; i < recipientsSize; i++)
                    {
                        String target = addSipUri(mbe.getRecipients().get(i));
                        to = to + "|" + target;
                    }

                    mbejson.put("to", to);
                    dispatchMessage(fromUser, mbejson);

                    mbse.setSentCount(recipientsCount);

                } else {

                    mbse.setRecipientsCount(recipientsSize);

                    for (int i = 0; i < recipientsSize; i++)
                    {
                        int c = i;
                        mbse.setSentCount(c + 1);

                        if (c % 10 == 0) {
                            // TODO fix check pointing
                            //checkpointState(fromUser, mbse);
                            mbse.setMessageBlastEntity(mbe);
                        }

                        mbejson.remove("to");
                        String recipient = mbe.getRecipients().get(0);
                        mbejson.put("to", recipient);
                        mbe.getRecipients().remove(recipient);

                        try {
                            dispatchMessage(fromUser, mbejson);

                        } catch (Exception e) {
                            Log.error("MessageBlastController IQ Error MessageID:" + mbse.getId(), e.getMessage());
                            response.put("Type", "Error");
                            response.put("Message", "Server Error! " + e);
                            return response;
                        }
                    }
                }
                mbse.setMessageBlastEntity(mbe);
                checkpointState(fromUser, mbse);

            } catch (Exception e) {
                Log.error("MessageBlastController Failed to create json ", e);
                response.put("Type", "Error");
                response.put("Message", e.toString());
                return response;

            }
            response.put("Type", "Success");
            return response;
        }else{
            response.put("Type","Error");
            response.put("Message","Some or all required values no present in request");
            return response;
        }
    }

    private String addSipUri(String to)
    {
        String uri = to;

        if (uri.startsWith("sip:") == false && uri.indexOf("@") > -1)
        {
            uri = "sip:" + uri;
        }
        return uri;
    }

    private void dispatchMessage(String fromUser, JSONObject mbejson)
    {
        IQ iq = new IQ(IQ.Type.set);
        iq.setFrom(fromUser + "@" + XMPPServer.getInstance().getServerInfo().getXMPPDomain());
        iq.setTo("ofskype." + XMPPServer.getInstance().getServerInfo().getXMPPDomain());
        iq.setChildElement("request", "http://traderlynk.com/protocol/messageblast").setText(mbejson.toString());

        Log.debug("MessageBlastController Routing IQ Message " + mbejson.getString("id"));
        XMPPServer.getInstance().getIQRouter().route(iq);
    }

    private void checkpointState(String from, MessageBlastSentEntity mbse)
    {
        try {
            // TODO Write MessageBlast to network cache/database and not user properties
            // for now empty list
            mbse.getMessageBlastEntity().setRecipients(new ArrayList<String>());

            RawPropertyEditor.self.addProperties(from, "belfry.blast.message." + mbse.getId(), mbse.toJSONString());
        } catch (Exception e) {
            Log.error("MessageBlastController Failed to checkpoint State of blast message", e.getMessage());
        }
    }

    public MessageBlastSentEntities getSentBlasts(String from)
    {
        User user = RawPropertyEditor.self.getAndCheckUser(from);
        List<MessageBlastSentEntity> sentBlasts = new ArrayList<MessageBlastSentEntity>();

        for (String key : user.getProperties().keySet()) {

            if (key.contains("belfry.blast.message")) {
                JSONObject bm = new JSONObject(user.getProperties().get(key));

                MessageBlastSentEntity mbse = blastSents.get(bm.getString("id"));

                if (mbse == null) // no more in memory
                {
                    mbse = new MessageBlastSentEntity();
                    mbse.setId(bm.getString("id"));
                    mbse.setTitle(bm.getString("title"));
                    mbse.setRecipientsCount(bm.getInt("recipientsCount"));
                    mbse.setSentCount(bm.getInt("sentCount"));
                    mbse.setRecieveCount(bm.getInt("recieveCount"));
                    mbse.setReadCount(bm.getInt("readCount"));
                    mbse.setRespondCount(bm.getInt("respondCount"));
                    mbse.setCompleted(bm.getBoolean("completed"));
                    mbse.setSentDate(bm.getString("sentDate"));
                    try {
                        String mbejson = bm.getString("mbe");
                        JSONObject mbejo = new JSONObject(mbejson);
                        MessageBlastEntity mbe = new MessageBlastEntity(mbejo);
                        mbse.setMessageBlastEntity(mbe);
                    }catch(Exception e ){
                        Log.debug("MessageBlastController Failed parsing sent blasts message", e );

                    }

                } else
                    user.getProperties().put(key, mbse.toJSONString());

                sentBlasts.add(mbse);
            }
        }
        Log.debug("MessageBlastController Retrieving " + sentBlasts.size() + " sent Blasts");
        return new MessageBlastSentEntities(sentBlasts);
    }

    public String getSenders(String from) {
        User user = RawPropertyEditor.self.getAndCheckUser(from);
        String senderlist = user.getProperties().get("belfry.blast.sender");

        String hostname = XMPPServer.getInstance().getServerInfo().getHostname();
        String trustedUser = JiveGlobals.getProperty("skype.trusted.user." + hostname, null);

        String blastHelp = JiveGlobals.getProperty("skype.blast.help", "");

        JSONObject sendersJSON = new JSONObject();
        sendersJSON.put("help_url", blastHelp);

        if (senderlist.startsWith("["))
        {
            JSONArray senders = new JSONArray(senderlist);
            int i = senders.length();

            if (trustedUser != null)
            {
                senders.put(i++, from + "@" + JiveGlobals.getProperty("skype.domain",
                    XMPPServer.getInstance().getServerInfo().getXMPPDomain()));

                senders.put(i++, trustedUser + "@" + JiveGlobals.getProperty("skype.domain",
                    XMPPServer.getInstance().getServerInfo().getXMPPDomain()));
            }
            sendersJSON.put("senders", senders);
            Log.debug("MessageBlastController Retrieving \n" + sendersJSON.toString());

        } else if (senderlist.startsWith("{")) {
            JSONObject senders = new JSONObject(senderlist);

            if (trustedUser != null)
            {
                senders.put(from + "@" + JiveGlobals.getProperty("skype.domain",
                    XMPPServer.getInstance().getServerInfo().getXMPPDomain()), user.getName());

                senders.put(trustedUser + "@" + JiveGlobals.getProperty("skype.domain",
                    XMPPServer.getInstance().getServerInfo().getXMPPDomain()), "Default");
            }
            sendersJSON.put("senders", senders);
            Log.debug("MessageBlastController Retrieving \n" + sendersJSON.toString());
        }
        return sendersJSON.toString();
    }

    public JSONObject CreateSenderList(String mbs , String from){
        JSONObject returnobj = new JSONObject();

        try{
            Object validate = null;

            if (mbs.startsWith("["))
            {
                validate = new JSONArray(mbs);
            }
            else

            if (mbs.startsWith("{"))
            {
                validate = new JSONObject(mbs);
            }

            if (validate != null)
            {
                RawPropertyEditor.self.addProperties(from, "belfry.blast.sender", mbs);
                returnobj.put("Type","Success");
            } else {
                returnobj.put("Type","Error");
                returnobj.put("Message","Invalid JSON object sent to service");
            }

        }catch(Exception e){
            Log.error("MessageBlastController Failed Creating Senders List", e);
            returnobj.put("Type","Error");
            returnobj.put("Message","Invalid JSON object sent to service");
        }
        return returnobj;
    }

    public BlastGroups searchContacts(String username, String from, String query, String limit) throws ServiceException
    {
        Log.debug("searchContacts " + query + " " + limit);

        JSONArray searchResult = new JSONArray();

        ArrayList<BlastGroup> groups = new ArrayList<BlastGroup>();
        ArrayList<BlastEntity> contactList = new ArrayList<BlastEntity>();

        for (int i = 0; i < searchResult.length(); i++)
        {
            JSONObject contact = searchResult.getJSONObject(i);

            if (contact.has("group"))
            {
                String groupName = contact.getString("group");
                String description = contact.getString("desc");

                contactList.add(new BlastEntity(groupName, description));
            }
            else

            if (contact.has("sipuri"))
            {
                String sip = contact.getString("sipuri").substring(4).toLowerCase();
                String displayName = contact.getString("name");

                contactList.add(new BlastEntity(sip, displayName));
            }
        }
        groups.add(new BlastGroup(contactList, "Search: " + query));
        return new BlastGroups(groups);
    }

    private List<String> getSenders(String username)
    {
        List<String> senders = new ArrayList<String>();
        String domain = getDomain();

        senders.add(username + "@" + domain);

        User user = RawPropertyEditor.self.getAndCheckUser(username);
        String senderlist = user.getProperties().get("belfry.blast.sender");

        if (senderlist.startsWith("["))
        {
            JSONArray jaSenderlist = new JSONArray(senderlist);

            for (int i = 0; i < jaSenderlist.length(); i++) {
                String sender = jaSenderlist.getString(i);
                senders.add(sender);
            }

        } else if (senderlist.startsWith("{")) {
            JSONObject jaSenderlist = new JSONObject(senderlist);

            Iterator<?> keys = jaSenderlist.keys();

            while( keys.hasNext() )
            {
                String sender = (String)keys.next();
                senders.add(sender);
            }
        }
        return senders;
    }


    private String getDomain()
    {
        return JiveGlobals.getProperty("skype.domain", XMPPServer.getInstance().getServerInfo().getXMPPDomain());
    }

    private BlastGroups fetchGroups(String username, String from) throws ServiceException
    {
        Log.debug("fetchGroups " + username + " " + from);

        if (getSenders(username).contains(from) == false)
        {
            throw new ServiceException("User " + username + " not permitted to access " + from, "Permission", ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
        }

        try {
            String fromUser = from.split("@")[0];

            ArrayList<BlastGroup> groups = new ArrayList<BlastGroup>();
            ArrayList<BlastEntity> adGroups = null;

            User user = RawPropertyEditor.self.getAndCheckUser(username);

            if (user != null && user.getProperties().containsKey("belfry.distribution.lists"))
            {
                adGroups = new ArrayList<BlastEntity>();
                JSONArray distributionLists = new JSONArray(user.getProperties().get("belfry.distribution.lists"));

                for (int i = 0; i < distributionLists.length(); i++)
                {
                    String sip = distributionLists.getString(i);
                    BlastEntity blastEntity = new BlastEntity(sip, sip);
                    adGroups.add(blastEntity);
                }
            }

            groups.add(new BlastGroup(adGroups, "AD Groups"));


            Element contactList = null; // TODO

            for ( Iterator i = contactList.element("Groups").elementIterator( "GroupInfo" ); i.hasNext(); )
            {
                Element groupInfo = (Element) i.next();

                String id = groupInfo.element("Id").getText();
                String name = groupInfo.element("Name").getText();

                if ("~".equals(name)) name = "Other Contacts";

                ArrayList<BlastEntity> contacts = new ArrayList<BlastEntity>();

                for ( Iterator j = contactList.element("Contacts").elementIterator( "ContactInfo" ); j.hasNext(); )
                {
                    Element contactInfo = (Element) j.next();

                    String uri = contactInfo.element("Uri").getText().substring(4);
                    String sipName = contactInfo.element("Name").getText();

                    if (sipName == null || "".equals(sipName)) sipName = uri.split("@")[0];

                    for ( Iterator k = contactInfo.element("GroupIds").elementIterator( "int" ); k.hasNext(); )
                    {
                        String groupId = ((Element) k.next()).getText();

                        if (groupId.equals(id))
                        {
                            BlastEntity blastEntity = new BlastEntity(uri, sipName);
                            contacts.add(blastEntity);
                            break;
                        }
                    }
                }

                groups.add(new BlastGroup(contacts, name));
            }
            return new BlastGroups(groups);

        } catch (Exception e) {
            Log.error("fetchGroups", e);
            throw new ServiceException(e.toString(), "Exception", ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
        }
    }
}

