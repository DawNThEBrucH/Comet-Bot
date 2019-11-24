package io.github.starwishsama.namelessbot.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import io.github.starwishsama.namelessbot.BotMain;
import io.github.starwishsama.namelessbot.objects.BotUser;
import io.github.starwishsama.namelessbot.objects.Shop;
import io.github.starwishsama.namelessbot.utils.FileProcess;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    public static List<BotUser> botUsers = new ArrayList<>();
    public static Map<String, Shop> shopItems = new HashMap<>();

    public static long ownerID;
    public static int autoSaveTime = 15;
    public static List<Long> botAdmins;
    public static int postPort = 5700;
    public static String postUrl = "127.0.0.1";
    public static String botName = "Bot";
    public static int botPort = 5703;
    public static String rconUrl;
    public static int rconPort;
    public static byte[] rconPwd;
    public static String netEaseApi;
    public static String[] cmdPrefix = new String[]{"/", "#"};
    public static boolean isBindMCAccount = false;
    
    private static File userCfg = new File(BotMain.jarPath + "users.json");
    private static File cfg = new File(BotMain.jarPath + "config.json");

    public static void loadCfg(){
        if (BotMain.jarPath != null) {
            if (userCfg.exists() && cfg.exists()){
                load();
            } else {
                try {
                    JSONObject configObject = new JSONObject();
                    configObject.put("ownerID", 0);
                    configObject.put("autoSaveTime", 15);
                    configObject.put("botAdmins", new ArrayList<>());
                    configObject.put("postPort", 5700);
                    configObject.put("postUrl", "127.0.0.1");
                    configObject.put("botName", "Bot");
                    configObject.put("botPort", 5702);
                    configObject.put("rconUrl", "127.0.0.1");
                    configObject.put("rconPort", "25575");
                    configObject.put("rconPwd", "password");
                    configObject.put("netEaseApi", "http://localhost:3000/");
                    configObject.put("cmdPrefix", new String[]{"/", "#"});
                    configObject.put("isBindMCAccount", false);
                    FileProcess.createFile(cfg.toString(), configObject.toJSONString());
                    load();
                    System.out.println("[配置] 已自动生成新的配置文件.");
                } catch (Exception e){
                    System.err.println("[配置] 在生成配置文件时发生了错误, 错误信息: " + e.getMessage());
                }
            }
        }
    }

    public static void saveCfg(){
        try {
            JSONObject checkInObject = new JSONObject();
            checkInObject.put("checkinUsers", botUsers);
            checkInObject.put("shopItems", shopItems);
            FileProcess.createFile(userCfg.toString(), checkInObject.toJSONString());

            JSONObject configObject = new JSONObject();
            configObject.put("ownerID", ownerID);
            configObject.put("autoSaveTime", autoSaveTime);
            configObject.put("botAdmins", botAdmins);
            configObject.put("postPort", postPort);
            configObject.put("postUrl", postUrl);
            configObject.put("botName", botName);
            configObject.put("botPort", botPort);
            configObject.put("rconUrl", rconUrl);
            configObject.put("rconPort", rconPort);
            configObject.put("rconPwd", rconPwd);
            configObject.put("netEaseApi", netEaseApi);
            configObject.put("isBindMCAccount", isBindMCAccount);
            FileProcess.createFile(cfg.toString(), configObject.toJSONString());
        } catch (Exception e){
            System.err.println("[配置] 在保存配置文件时发生了问题, 错误信息: ");
            e.printStackTrace();
        }
    }

    private static void load(){
        try {
            JSONObject checkInObject = JSONObject.parseObject(FileProcess.readFile(userCfg.toString()));
            if (JSON.parseObject(checkInObject.getString("checkinUsers"), new TypeReference<Map<Long, BotUser>>(){}) != null)
                botUsers = JSON.parseObject(checkInObject.getString("checkinUsers"), new TypeReference<List<BotUser>>() {
                });

            shopItems = JSON.parseObject(checkInObject.getString("shopItems"), new TypeReference<Map<String, Shop>>() {
            });

            JSONObject configObject = JSONObject.parseObject(cfg.toString());
            ownerID = configObject.getLong("ownerID");
            autoSaveTime = configObject.getInteger("autoSaveTime");
            botAdmins = JSON.parseObject(configObject.getString("botAdmins"), new TypeReference<List<Long>>(){
            });
            postPort = configObject.getInteger("postPort");
            postUrl = configObject.getString("postUrl");
            botName = configObject.getString("botName");
            botPort = configObject.getInteger("botPort");
            rconUrl = configObject.getString("rconUrl");
            rconPort = configObject.getInteger("rconPort");
            rconPwd = configObject.getString("rconPwd").getBytes();
            netEaseApi = configObject.getString("netEaseApi");
            isBindMCAccount = configObject.getBoolean("isBindMCAccount");
        } catch (Exception e) {
            System.err.println("[配置] 在加载配置文件时发生了问题, 错误信息: " + e);
        }
    }
}