package com.tripleying.qwq.LocaleLanguageAPI;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class LangLang {
    
    private static final Pattern P = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    private static final Splitter S = Splitter.on('=').limit(2);
    
    public static Map<String,String> read(List<String> file){
        Map<String,String> New = new HashMap();
        file.forEach(f -> {
            try {
                for (final String sequence : readLines(new FileInputStream(new File("plugins/LocaleLanguageAPI/lang", f)), StandardCharsets.UTF_8)) {
                    if (!sequence.isEmpty()) {
                        if (sequence.charAt(0) == '#') {
                            continue;
                        }
                        final String[] array = Iterables.toArray(S.split(sequence), String.class);
                        if (array == null) {
                            continue;
                        }
                        if (array.length != 2) {
                            continue;
                        }
                        New.put(array[0], P.matcher(array[1]).replaceAll("%$1s"));
                    }
                }
            }catch (IOException ex) {}
        });
        return New;
    }
    
    public static List<String> readLines(final InputStream inputStream, final Charset charset) throws IOException {
        Reader reader = new InputStreamReader(inputStream, charset);
        final BufferedReader bufferedReader = (BufferedReader)((reader instanceof BufferedReader) ? reader : new BufferedReader(reader));
        final List<String> list = new ArrayList<>();
        for (String s = bufferedReader.readLine(); s != null; s = bufferedReader.readLine()) {
            list.add(s);
        }
        return list;
    }
}
