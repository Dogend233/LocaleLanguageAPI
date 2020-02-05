package com.tripleying.qwq.LocaleLanguageAPI;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    
    public static Main instance;
    public double mode;
    public boolean enable;
    public String VERSION;
    public String reloadMsg = "";
    public Map<String,String> NEW = null;
    public Map<String,String> OLD = null;
    
    @Override
    public void onEnable(){
        instance = this;
        String name = Bukkit.getServer().getClass().getPackage().getName();
        VERSION = name.substring(name.lastIndexOf('.') + 1);
        String version = Bukkit.getServer().getVersion();
        version = version.substring(version.indexOf("MC")+3, version.length()-1).trim();
        version = version.substring(0, version.lastIndexOf('.'));
        String v = version.substring(version.indexOf('.')+1);
        if(v.length()==1) v = ".0"+v;
        mode = Double.parseDouble(version.substring(0, version.indexOf('.'))+v);
        load();
    }
    
    public void load(){
        File f = getDataFolder();
        if(!f.exists())f.mkdir();
        f = new File(f, "/config.yml");
        if (!f.exists()) saveDefaultConfig();
        reloadConfig();
        reloadMsg = getConfig().getString("reload","§a[%name%]: OK!").replace("%name%", getName());
        f = new File("plugins/LocaleLanguageAPI/lang");
        if(!f.exists())f.mkdir();
        if(enable = getConfig().getBoolean("enable",false)){
            if(mode<1.13){
                List<String> l = Arrays.asList(f.list((File dir, String file) -> file.endsWith(".lang")));
                if(!l.isEmpty()){
                    Map<String,String> New = LangLang.read(l);
                    NEW = New.isEmpty()?null:New;
                    if(!New.isEmpty()){
                        try {
                            Class <?>clazzI18n = Class.forName("net.minecraft.server."+VERSION+".LocaleI18n");
                            Field aI18n = clazzI18n.getDeclaredField("a");
                            aI18n.setAccessible(true);
                            Object lll = aI18n.get(new Object());
                            Class <?>clazzLL = lll.getClass();
                            Field nameField = clazzLL.getDeclaredField("d");
                            nameField.setAccessible(true);
                            if(OLD==null) OLD = (Map)nameField.get(lll);
                            Field modifiers = nameField.getClass().getDeclaredField("modifiers");
                            modifiers.setAccessible(true);
                            modifiers.setInt(nameField, nameField.getModifiers() & ~Modifier.FINAL);
                            nameField.set(lll, New);
                        }catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException | ClassNotFoundException ex) {}
                    }
                }
            }else{
                List<String> l = Arrays.asList(f.list((File dir, String file) -> file.endsWith(".json")));
                if(!l.isEmpty()){
                    Map<String,String> New = LangJson.read(l);
                    NEW = New.isEmpty()?null:New;
                    try {
                        Class <?>clazzLL = Class.forName("net.minecraft.server."+VERSION+".LocaleLanguage");
                        Object ll = clazzLL.newInstance();
                        Field nameField = ll.getClass().getDeclaredField("d");
                        nameField.setAccessible(true);
                        if(OLD==null) OLD = (Map)nameField.get(ll);
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {}
                }
            }
        }else{
            unload();
        }
        LocaleLanguageAPI.initial();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (label.equals("localang") || label.equals("ll")){
            if(sender.hasPermission("localang.reload") && args.length>0 && args[0].equals("reload")){
                load();
                sender.sendMessage(reloadMsg);
            }
        }
        return true;
    }
    
    public void unload(){
        if(mode<1.13){
            NEW = null;
            try {
                if(OLD!=null){
                    Class <?>clazzI18n = Class.forName("net.minecraft.server."+VERSION+".LocaleI18n");
                    Field aI18n = clazzI18n.getDeclaredField("a");
                    aI18n.setAccessible(true);
                    Object lll = aI18n.get(new Object());
                    Class <?>clazzLL = lll.getClass();
                    Field nameField = clazzLL.getDeclaredField("d");
                    nameField.setAccessible(true);
                    Field modifiers = nameField.getClass().getDeclaredField("modifiers");
                    modifiers.setAccessible(true);
                    modifiers.setInt(nameField, nameField.getModifiers() & ~Modifier.FINAL);
                    nameField.set(lll, OLD);
                }
            }catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException | ClassNotFoundException ex) {}
        }else{
            NEW = null;
            OLD = null;
        }
    }
    
    @Override
    public void onDisable(){
        unload();
    }
    
}
