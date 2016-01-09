package com.aayaffe.sailingracecoursemanager.communication;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.general.GeneralUtils;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.location.QBLocations;
import com.quickblox.location.model.QBLocation;
import com.quickblox.location.request.QBLocationRequestBuilder;
import com.quickblox.location.request.SortField;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by aayaffe on 22/09/2015.
 */
public class QuickBlox implements ICommManager {

    private static final String TAG = "QuickBlox";
    private Context c;
    private Resources r;
    QBChatService chatService;
    List<AviObject> aviObjects = new ArrayList<>();
    //Location otherLocation;
    public QuickBlox(Context c, Resources r){
        this.c = c;
        this.r = r;
    }


    @Override
    public int login(String user, String password, String nickname) {
        QBSettings.getInstance().fastConfigInit("28642", "GmfbggxbOfzby7R", "9hJert3mjFyZcKu");
        final QBUser u = new QBUser();
        u.setLogin(user);
        u.setPassword(password);
        QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle params) {
                QBUsers.signIn(u, new QBEntityCallbackImpl<QBUser>() {
                    @Override
                    public void onSuccess(QBUser user, Bundle args) {
                        Log.d(TAG, "signIn success");
                    }

                    @Override
                    public void onError(List<String> errors) {
                        Log.e(TAG, "Error Signing in");
                    }
                });
                chatLogin();


            }
            @Override
            public void onError(List<String> errors) {
                Log.e(TAG, "Error creating session");
            }
        });
        return 0;
    }

    @Override
    public int writeBoatObject(AviObject o) {
        return 0;
    }

    @Override
    public int writeBuoyObject(AviObject o) {
        return 0;
    }

//    @Override
//    public int sendLoc(AviLocation l) {
//        if (l==null)
//            return -1;
//        //Random randomGenerator = new Random();
//        double latitude = l.lat;//randomGenerator.nextDouble()+32.9;
//        double longitude = l.lon;//randomGenerator.nextDouble()+34.9;
//        String status = "";
//        final QBLocation location = new QBLocation(latitude, longitude, status);
//        QBLocations.createLocation(location, new QBEntityCallbackImpl<QBLocation>() {
//            @Override
//            public void onSuccess(QBLocation qbLocation, Bundle args) {
//                Log.d(TAG, "Success sending Loc");
//            }
//
//            @Override
//            public void onError(List<String> errors) {
//                Log.e(TAG, "Error sending Loc");
//                for (String error : errors) {
//                    Log.e(TAG, error);
//                }
//            }
//        });
//        return 0;
//    }


    @Override
    public List<AviObject> getAllLocs() {
        QBLocationRequestBuilder getLocationsBuilder = new QBLocationRequestBuilder();
        getLocationsBuilder.setPerPage(10);
        getLocationsBuilder.setLastOnly();
        getLocationsBuilder.setHasStatus();
        getLocationsBuilder.setSort(SortField.CREATED_AT);
        QBLocations.getLocations(getLocationsBuilder, new QBEntityCallbackImpl<ArrayList<QBLocation>>() {
            @Override
            public void onSuccess(ArrayList<QBLocation> locations, Bundle params) {
                for(QBLocation l:locations){
                    AviObject o = new AviObject();
                    o.lastUpdate = GeneralUtils.parseDate(l.getFUpdatedAt());
                    o.name = l.getUser().getLogin();
                    o.location = GeoUtils.toAviLocation(GeoUtils.createLocation(l.getLatitude(), l.getLongitude()));
                    o.color = "red";
                    if(o.name.contains("1")) o.color="blue";
                    if(o.name.contains("2")) o.color="cyan";
                    if(o.name.contains("3")) o.color="orange";
                    if(o.name.contains("4")) o.color="pink";
                    o.type = ObjectTypes.Other;
                    if (o.name.contains("Manager")) o.type = ObjectTypes.RaceManager;
                    if (o.name.contains("Worker")) o.type = ObjectTypes.WorkerBoat;
                    Log.d(TAG,"Got Location of user: " + l.getUser().getLogin());
                    if (aviObjects.contains(o)){
                        aviObjects.remove(o);
                        aviObjects.add(o);
                    }
                    else aviObjects.add(o);
                }
            }

            @Override
            public void onError(List<String> errors) {

            }
        });



//        QBLocations.getLocation(7031588, new QBEntityCallbackImpl<QBLocation>() {
//            @Override
//            public void onSuccess(QBLocation qbLocation, Bundle args) {
//                Log.d(TAG, "getLocation Success" + qbLocation.toString());
//                otherLocation = new Location("QuickBlox 5546553");
//                otherLocation.setLongitude(qbLocation.getLongitude());
//                otherLocation.setLatitude(qbLocation.getLatitude());
//
//            }
//
//            @Override
//            public void onError(List<String> errors) {
//                Log.e(TAG, "Error getting Loc");
//                for (String error: errors){
//                    Log.e(TAG, error);
//                }
//            }
//        });
        return aviObjects;
    }

    @Override
    public int sendAction(RaceManagerAction a, AviObject o) {
        return 0;
    }

    private void chatLogin(){
        if (!QBChatService.isInitialized()) {
            QBChatService.init(c);

        }
        if (chatService==null) {
            chatService = QBChatService.getInstance();
        }
        final QBUser u = new QBUser();
        u.setLogin("Worker1");
        u.setPassword("Aa123456z");
        u.setId(5707646);
        chatService.login(u, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Chat signIn success = "+ chatService.isLoggedIn());
                newDialoug();
            }

            @Override
            public void onError(List errors) {
                // errror
            }
        });
    }
    private void newDialoug(){
        ArrayList<Integer> occupantIdsList = new ArrayList<Integer>();
        occupantIdsList.add(5707644);
        occupantIdsList.add(5707647);
        occupantIdsList.add(5707651);
        occupantIdsList.add(5750816);


        QBDialog dialog = new QBDialog();
        dialog.setName("Chat with Garry and John");
        dialog.setPhoto("1786");
        dialog.setType(QBDialogType.GROUP);
        dialog.setOccupantsIds(occupantIdsList);

        QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
        groupChatManager.createDialog(dialog, new QBEntityCallbackImpl<QBDialog>() {
            @Override
            public void onSuccess(QBDialog dialog, Bundle args) {
                for (Integer userID : dialog.getOccupants()) {

                    QBChatMessage chatMessage = createChatNotificationForGroupChatCreation(dialog);
                    Calendar c = Calendar.getInstance();
                    long time = c.get(Calendar.SECOND);
                    chatMessage.setProperty("date_sent", time + "");

                    QBPrivateChat chat = QBChatService.getInstance().getPrivateChatManager().getChat(userID);
                    if (chat == null) {
                        chat = chatService.getPrivateChatManager().createChat(userID, null);
                    }

                    try {
                        chat.sendMessage(chatMessage);
                        Log.d(TAG,"Send message Success" + chat.getDialogId());
                    } catch (Exception e) {
                        Log.e(TAG, "Send message FAil" + chat.getDialogId(), e);
                    }
                }
            }

            @Override
            public void onError(List<String> errors) {
                Log.e(TAG, "Dialog creation error");
                for (String s: errors){
                    Log.e(TAG, "Dialog creation error: " + s);
                }
            }
        });

    }
    public static QBChatMessage createChatNotificationForGroupChatCreation(QBDialog dialog) {
        String dialogId = String.valueOf(dialog.getDialogId());
        String roomJid = dialog.getRoomJid();
        String occupantsIds = TextUtils.join(",", dialog.getOccupants());
        String dialogName = dialog.getName();
        String dialogTypeCode = String.valueOf(dialog.getType().ordinal());

        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody("optional text");

        // Add notification_type=1 to extra params when you created a group chat
        //
        chatMessage.setProperty("notification_type", "1");

        chatMessage.setProperty("_id", dialogId);
        if (!TextUtils.isEmpty(roomJid)) {
            chatMessage.setProperty("room_jid", roomJid);
        }
        chatMessage.setProperty("occupants_ids", occupantsIds);
        if (!TextUtils.isEmpty(dialogName)) {
            chatMessage.setProperty("name", dialogName);
        }
        chatMessage.setProperty("type", dialogTypeCode);

        return chatMessage;
    }




}


