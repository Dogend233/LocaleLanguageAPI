package com.tripleying.qwq.LocaleLanguageAPI;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.inventory.ItemStack;

public class LocaleLanguageAPI {
    
    private static Main main;
    private static Class<?> obcItemStack = null;
    private static Class<?> nmsItemStack = null;
    private static Class<?> nmsChatBase = null;
    private static Class<?> nmsItem = null;
    private static Method asNMSCopyMethod = null;
    private static Method getNameMethod = null;
    private static Method getTextMethod = null;
    private static Method getItemMethod = null;
    private static Method getItemNameMethod = null;
    private static Method getItemNameSpecialMethod = null;
    
    /**
     * 初始化
     */
    public static void initial(){
        LocaleLanguageAPI.main = Main.instance;
        try {
            obcItemStack = Class.forName("org.bukkit.craftbukkit."+main.VERSION+".inventory.CraftItemStack");
            asNMSCopyMethod = obcItemStack.getMethod("asNMSCopy", ItemStack.class);
            nmsItemStack = Class.forName("net.minecraft.server."+main.VERSION+".ItemStack");
            getNameMethod = nmsItemStack.getMethod("getName");
        } catch (ClassNotFoundException | SecurityException | NoSuchMethodException ex) {}
        if(main.mode>1.12){
            try {
                nmsChatBase = Class.forName("net.minecraft.server."+main.VERSION+".IChatBaseComponent");
                getTextMethod = nmsChatBase.getMethod("getText");
            } catch (ClassNotFoundException | SecurityException | NoSuchMethodException ex) {}
            if(main.enable){
                try {
                    nmsItem = Class.forName("net.minecraft.server."+main.VERSION+".Item");
                    getItemMethod = nmsItemStack.getMethod("getItem");
                    getItemNameMethod = nmsItem.getMethod("getName");
                } catch (ClassNotFoundException | SecurityException | NoSuchMethodException ex) {}
                if(main.mode>1.13 && main.mode<1.16){
                    try {
                        getItemNameSpecialMethod = nmsItem.getMethod("f",nmsItemStack);
                    } catch (SecurityException | NoSuchMethodException ex) {}
                }
            }
        }
        
    }
    
    /**
     * 获取物品名
     * @param is 物品
     * @return 物品名
     */
    public static String getItemName(ItemStack is){
        if(is.getItemMeta().hasDisplayName()) return is.getItemMeta().getDisplayName();
        if(main.mode>1.12 && main.NEW!=null){
            try{
                Object nmsItemStackObj = asNMSCopyMethod.invoke(null, is);
                Object item = getItemMethod.invoke(nmsItemStackObj);
                String locaName;
                if(main.mode>1.13 && main.mode<1.16){
                    locaName = getItemNameSpecialMethod.invoke(item, nmsItemStackObj).toString();
                }else{
                    locaName = getItemNameMethod.invoke(item).toString();
                }
                if(main.NEW.containsKey(locaName)){
                    return main.NEW.get(locaName);
                }else{
                    Object nmsItemName = getNameMethod.invoke(nmsItemStackObj);
                    if(nmsItemName instanceof String){
                        return (String)nmsItemName;
                    }else{
                        return getTextMethod.invoke(nmsItemName).toString();
                    }
                }
            }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e){}
        }else{
            try {
                String name;
                Object nmsItemStackObj = asNMSCopyMethod.invoke(null, is);
                Object nmsItemName = getNameMethod.invoke(nmsItemStackObj);
                if(nmsItemName instanceof String){
                    name = (String)nmsItemName;
                }else{
                    name = getTextMethod.invoke(nmsItemName).toString();
                }
                if(main.mode<1.13 && main.OLD!=null && main.OLD.containsKey(name)) return main.OLD.get(name);
                return name;
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException  ex) {}
        }
        return is.getType().toString();
    }
    
}
