//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.melloware.jintellitype;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public final class JIntellitype implements JIntellitypeConstants {
    private static JIntellitype jintellitype = null;
    private static boolean isInitialized = false;
    private static String libraryLocation = null;
    private final List<HotkeyListener> hotkeyListeners = Collections.synchronizedList(new CopyOnWriteArrayList());
    private final List<IntellitypeListener> intellitypeListeners = Collections.synchronizedList(new CopyOnWriteArrayList());
    private final int handler = 0;
    private final HashMap<String, Integer> keycodeMap;

    private JIntellitype() {
        try {
            System.loadLibrary("JIntellitype");
        } catch (Throwable var8) {
            try {
                if (getLibraryLocation() != null) {
                    System.load(getLibraryLocation());
                } else {

                    //String jarPath = "com/melloware/jintellitype/";
                    String jarPath = "/dll/";
                    String tmpDir = System.getProperty("java.io.tmpdir");

                    try {
                        String dll = "JIntellitype.dll";
                        this.fromJarToFs(jarPath + dll, tmpDir + dll);
                        System.load(tmpDir + dll);
                    } catch (UnsatisfiedLinkError | Exception var6) {
                        String dll = "JIntellitype64.dll";
                        this.fromJarToFs(jarPath + dll, tmpDir + dll);
                        System.load(tmpDir + dll);
                    }
                }
            } catch (Throwable var7) {
                throw new JIntellitypeException("Could not load JIntellitype.dll from local file system or from inside JAR", var7);
            }
        }

        this.initializeLibrary();
        this.keycodeMap = this.getKey2KeycodeMapping();
    }

    private void fromJarToFs(String jarPath, String filePath) throws IOException {
        InputStream is = null;
        FileOutputStream os = null;

        try {
            File file = new File(filePath);
            if (file.exists()) {
                boolean success = file.delete();
                if (!success) {
                    throw new IOException("Could not delete file: " + filePath);
                }
            }

            is = this.getClass().getResourceAsStream(jarPath);
            //is = ClassLoader.getSystemClassLoader().getResourceAsStream(jarPath);
            os = new FileOutputStream(filePath);
            byte[] buffer = new byte[8192];

            int bytesRead;
            while((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception var11) {
            throw new IOException("FromJarToFileSystem could not load DLL: " + jarPath, var11);
        } finally {
            if (is != null) {
                is.close();
            }

            if (os != null) {
                os.close();
            }

        }

    }

    public static JIntellitype getInstance() {
        if (!isInitialized) {
            Class var0 = JIntellitype.class;
            synchronized(JIntellitype.class) {
                if (!isInitialized) {
                    jintellitype = new JIntellitype();
                    isInitialized = true;
                }
            }
        }

        return jintellitype;
    }

    public void addHotKeyListener(HotkeyListener listener) {
        this.hotkeyListeners.add(listener);
    }

    public void addIntellitypeListener(IntellitypeListener listener) {
        this.intellitypeListeners.add(listener);
    }

    public void cleanUp() {
        try {
            this.terminate();
        } catch (UnsatisfiedLinkError var2) {
            throw new JIntellitypeException("JIntellitype DLL Error", var2);
        } catch (RuntimeException var3) {
            throw new JIntellitypeException(var3);
        }
    }

    public void registerHotKey(int identifier, int modifier, int keycode) {
        try {
            int modifiers = swingToIntelliType(modifier);
            if (modifiers == 0) {
                ;
            }

            this.regHotKey(identifier, modifier, keycode);
        } catch (UnsatisfiedLinkError var5) {
            throw new JIntellitypeException("JIntellitype DLL Error", var5);
        } catch (RuntimeException var6) {
            throw new JIntellitypeException(var6);
        }
    }

    public void registerSwingHotKey(int identifier, int modifier, int keycode) {
        try {
            this.regHotKey(identifier, swingToIntelliType(modifier), keycode);
        } catch (UnsatisfiedLinkError var5) {
            throw new JIntellitypeException("JIntellitype DLL Error", var5);
        } catch (RuntimeException var6) {
            throw new JIntellitypeException(var6);
        }
    }

    public void registerHotKey(int identifier, String modifierAndKeyCode) {
        String[] split = modifierAndKeyCode.split("\\+");
        int mask = 0;
        int keycode = 0;

        for(int i = 0; i < split.length; ++i) {
            if ("ALT".equalsIgnoreCase(split[i])) {
                ++mask;
            } else if (!"CTRL".equalsIgnoreCase(split[i]) && !"CONTROL".equalsIgnoreCase(split[i])) {
                if ("SHIFT".equalsIgnoreCase(split[i])) {
                    mask += 4;
                } else if ("WIN".equalsIgnoreCase(split[i])) {
                    mask += 8;
                } else if (this.keycodeMap.containsKey(split[i].toLowerCase())) {
                    keycode = (Integer)this.keycodeMap.get(split[i].toLowerCase());
                }
            } else {
                mask += 2;
            }
        }

        this.registerHotKey(identifier, mask, keycode);
    }

    public void removeHotKeyListener(HotkeyListener listener) {
        this.hotkeyListeners.remove(listener);
    }

    public void removeIntellitypeListener(IntellitypeListener listener) {
        this.intellitypeListeners.remove(listener);
    }

    public void unregisterHotKey(int identifier) {
        try {
            this.unregHotKey(identifier);
        } catch (UnsatisfiedLinkError var3) {
            throw new JIntellitypeException("JIntellitype DLL Error", var3);
        } catch (RuntimeException var4) {
            throw new JIntellitypeException(var4);
        }
    }

    public static boolean checkInstanceAlreadyRunning(String appTitle) {
        return getInstance().isRunning(appTitle);
    }

    public static boolean isJIntellitypeSupported() {
        boolean result = false;
        String os = "none";

        try {
            os = System.getProperty("os.name").toLowerCase();
        } catch (SecurityException var4) {
            System.err.println("Caught a SecurityException reading the system property 'os.name'; the SystemUtils property value will default to null.");
        }

        if (os.startsWith("windows")) {
            try {
                getInstance();
                result = true;
            } catch (Exception var3) {
                result = false;
            }
        }

        return result;
    }

    public static String getLibraryLocation() {
        return libraryLocation;
    }

    public static void setLibraryLocation(String libraryLocation) {
        File dll = new File(libraryLocation);
        if (!dll.isAbsolute()) {
            JIntellitype.libraryLocation = dll.getAbsolutePath();
        } else {
            JIntellitype.libraryLocation = libraryLocation;
        }

    }

    public static void setLibraryLocation(File libraryFile) {
        if (!libraryFile.isAbsolute()) {
            libraryLocation = libraryFile.getAbsolutePath();
        }

    }

    protected void onHotKey(final int identifier) {
        Iterator i$ = this.hotkeyListeners.iterator();

        while(i$.hasNext()) {
            final HotkeyListener hotkeyListener = (HotkeyListener)i$.next();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    hotkeyListener.onHotKey(identifier);
                }
            });
        }

    }

    protected void onIntellitype(final int command) {
        Iterator i$ = this.intellitypeListeners.iterator();

        while(i$.hasNext()) {
            final IntellitypeListener intellitypeListener = (IntellitypeListener)i$.next();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    intellitypeListener.onIntellitype(command);
                }
            });
        }

    }

    protected static int swingToIntelliType(int swingKeystrokeModifier) {
        int mask = 0;
        if ((swingKeystrokeModifier & 1) == 1 || (swingKeystrokeModifier & 64) == 64) {
            mask |= 4;
        }

        if ((swingKeystrokeModifier & 8) == 8 || (swingKeystrokeModifier & 512) == 512) {
            mask |= 1;
        }

        if ((swingKeystrokeModifier & 2) == 2 || (swingKeystrokeModifier & 128) == 128) {
            mask |= 2;
        }

        if ((swingKeystrokeModifier & 4) == 4 || (swingKeystrokeModifier & 256) == 256) {
            mask |= 8;
        }

        return mask;
    }

    private HashMap<String, Integer> getKey2KeycodeMapping() {
        HashMap<String, Integer> map = new HashMap();
        map.put("first", 400);
        map.put("last", 402);
        map.put("typed", 400);
        map.put("pressed", 401);
        map.put("released", 402);
        map.put("enter", 13);
        map.put("back_space", 8);
        map.put("tab", 9);
        map.put("cancel", 3);
        map.put("clear", 12);
        map.put("pause", 19);
        map.put("caps_lock", 20);
        map.put("escape", 27);
        map.put("space", 32);
        map.put("page_up", 33);
        map.put("page_down", 34);
        map.put("end", 35);
        map.put("home", 36);
        map.put("left", 37);
        map.put("up", 38);
        map.put("right", 39);
        map.put("down", 40);
        map.put("comma", 188);
        map.put("minus", 109);
        map.put("period", 110);
        map.put("slash", 191);
        map.put("accent `", 192);
        map.put("0", 48);
        map.put("1", 49);
        map.put("2", 50);
        map.put("3", 51);
        map.put("4", 52);
        map.put("5", 53);
        map.put("6", 54);
        map.put("7", 55);
        map.put("8", 56);
        map.put("9", 57);
        map.put("semicolon", 186);
        map.put("equals", 187);
        map.put("a", 65);
        map.put("b", 66);
        map.put("c", 67);
        map.put("d", 68);
        map.put("e", 69);
        map.put("f", 70);
        map.put("g", 71);
        map.put("h", 72);
        map.put("i", 73);
        map.put("j", 74);
        map.put("k", 75);
        map.put("l", 76);
        map.put("m", 77);
        map.put("n", 78);
        map.put("o", 79);
        map.put("p", 80);
        map.put("q", 81);
        map.put("r", 82);
        map.put("s", 83);
        map.put("t", 84);
        map.put("u", 85);
        map.put("v", 86);
        map.put("w", 87);
        map.put("x", 88);
        map.put("y", 89);
        map.put("z", 90);
        map.put("open_bracket", 219);
        map.put("back_slash", 220);
        map.put("close_bracket", 221);
        map.put("numpad0", 96);
        map.put("numpad1", 97);
        map.put("numpad2", 98);
        map.put("numpad3", 99);
        map.put("numpad4", 100);
        map.put("numpad5", 101);
        map.put("numpad6", 102);
        map.put("numpad7", 103);
        map.put("numpad8", 104);
        map.put("numpad9", 105);
        map.put("multiply", 106);
        map.put("add", 107);
        map.put("separator", 108);
        map.put("subtract", 109);
        map.put("decimal", 110);
        map.put("divide", 111);
        map.put("delete", 46);
        map.put("num_lock", 144);
        map.put("scroll_lock", 145);
        map.put("f1", 112);
        map.put("f2", 113);
        map.put("f3", 114);
        map.put("f4", 115);
        map.put("f5", 116);
        map.put("f6", 117);
        map.put("f7", 118);
        map.put("f8", 119);
        map.put("f9", 120);
        map.put("f10", 121);
        map.put("f11", 122);
        map.put("f12", 123);
        map.put("f13", 61440);
        map.put("f14", 61441);
        map.put("f15", 61442);
        map.put("f16", 61443);
        map.put("f17", 61444);
        map.put("f18", 61445);
        map.put("f19", 61446);
        map.put("f20", 61447);
        map.put("f21", 61448);
        map.put("f22", 61449);
        map.put("f23", 61450);
        map.put("f24", 61451);
        map.put("printscreen", 44);
        map.put("insert", 45);
        map.put("help", 47);
        map.put("meta", 157);
        map.put("back_quote", 192);
        map.put("quote", 222);
        map.put("kp_up", 224);
        map.put("kp_down", 225);
        map.put("kp_left", 226);
        map.put("kp_right", 227);
        map.put("dead_grave", 128);
        map.put("dead_acute", 129);
        map.put("dead_circumflex", 130);
        map.put("dead_tilde", 131);
        map.put("dead_macron", 132);
        map.put("dead_breve", 133);
        map.put("dead_abovedot", 134);
        map.put("dead_diaeresis", 135);
        map.put("dead_abovering", 136);
        map.put("dead_doubleacute", 137);
        map.put("dead_caron", 138);
        map.put("dead_cedilla", 139);
        map.put("dead_ogonek", 140);
        map.put("dead_iota", 141);
        map.put("dead_voiced_sound", 142);
        map.put("dead_semivoiced_sound", 143);
        map.put("ampersand", 150);
        map.put("asterisk", 151);
        map.put("quotedbl", 152);
        map.put("less", 153);
        map.put("greater", 160);
        map.put("braceleft", 161);
        map.put("braceright", 162);
        map.put("at", 512);
        map.put("colon", 513);
        map.put("circumflex", 514);
        map.put("dollar", 515);
        map.put("euro_sign", 516);
        map.put("exclamation_mark", 517);
        map.put("inverted_exclamation_mark", 518);
        map.put("left_parenthesis", 519);
        map.put("number_sign", 520);
        map.put("plus", 521);
        map.put("right_parenthesis", 522);
        map.put("underscore", 523);
        map.put("context_menu", 525);
        map.put("final", 24);
        map.put("convert", 28);
        map.put("nonconvert", 29);
        map.put("accept", 30);
        map.put("modechange", 31);
        map.put("kana", 21);
        map.put("kanji", 25);
        map.put("alphanumeric", 240);
        map.put("katakana", 241);
        map.put("hiragana", 242);
        map.put("full_width", 243);
        map.put("half_width", 244);
        map.put("roman_characters", 245);
        map.put("all_candidates", 256);
        map.put("previous_candidate", 257);
        map.put("code_input", 258);
        map.put("japanese_katakana", 259);
        map.put("japanese_hiragana", 260);
        map.put("japanese_roman", 261);
        map.put("kana_lock", 262);
        map.put("input_method_on_off", 263);
        map.put("cut", 65489);
        map.put("copy", 65485);
        map.put("paste", 65487);
        map.put("undo", 65483);
        map.put("again", 65481);
        map.put("find", 65488);
        map.put("props", 65482);
        map.put("stop", 65480);
        map.put("compose", 65312);
        map.put("alt_graph", 65406);
        map.put("begin", 65368);
        return map;
    }

    private synchronized native void initializeLibrary() throws UnsatisfiedLinkError;

    private synchronized native void regHotKey(int var1, int var2, int var3) throws UnsatisfiedLinkError;

    private synchronized native void terminate() throws UnsatisfiedLinkError;

    private synchronized native void unregHotKey(int var1) throws UnsatisfiedLinkError;

    private synchronized native boolean isRunning(String var1);
}
