package com.tripleying.qwq.LocaleLanguageAPI;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class LangJson {
    
    private static Class <?>clazzChatDeserializer = null;
    private static Method methodM = null;
    private static Method methodA = null;
    private static final Pattern P = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    
    public static Map<String,String> read(List<String> file){
        if(clazzChatDeserializer==null) initial();
        Map<String,String> New = new HashMap();
        file.forEach(f -> {
            try {
                InputStream var0 = new FileInputStream(new File("plugins/LocaleLanguageAPI/lang", f));
                JsonElement var2 = new Gson().fromJson(new InputStreamReader(var0, StandardCharsets.UTF_8), JsonElement.class);
                JsonObject var3 = (JsonObject)methodM.invoke(null, var2, "strings");
                for (final Map.Entry<String, JsonElement> var4 : var3.entrySet()) {
                    final String var5 = P.matcher((String)methodA.invoke(null,var4.getValue(), var4.getKey())).replaceAll("%$1s");
                    New.put(var4.getKey(), var5);
                }
            }catch (JsonIOException | JsonSyntaxException | FileNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {}
        });
        return New;
    }
    
    public static void initial(){
        try {
            clazzChatDeserializer = Class.forName("net.minecraft.server."+Main.instance.VERSION+".ChatDeserializer");
            methodM = clazzChatDeserializer.getMethod("m", JsonElement.class, String.class);
            methodA = clazzChatDeserializer.getMethod("a", JsonElement.class, String.class);
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException ex) {}
    }
    
}
