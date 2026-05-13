import ghidra.app.script.GhidraScript;
import ghidra.program.model.address.Address;
import ghidra.program.model.mem.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;

public class ObjcClassDumper extends GhidraScript {

    HashSet<String> seen = new HashSet<>();
    String outPath = System.getProperty("user.home") + "/Desktop/ObjC_RESOLVED.txt";

    private String readRaw(Address addr) {

        try {
            byte[] buf = new byte[256];
            currentProgram.getMemory().getBytes(addr, buf);

            StringBuilder sb = new StringBuilder();
            int letters = 0;
            int total = 0;

            for (byte b : buf) {

                int c = b & 0xFF;

                if (c == 0) break;

                if (c >= 0x20 && c <= 0x7E) {

                    sb.append((char)c);
                    total++;

                    if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))
                        letters++;

                } else {
                    break;
                }
            }

            if (sb.length() < 3) return null;

            return sb.toString();

        } catch (Exception e) {
            return null;
        }
    }

    // =========================
    // OBFUSCATION SCORE (NO contains)
    // =========================
    private boolean isLikelyReal(String s) {

        int len = s.length();
        int letters = 0;
        int digits = 0;
        int symbols = 0;

        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);

            if (Character.isLetter(c)) letters++;
            else if (Character.isDigit(c)) digits++;
            else symbols++;
        }

        double letterRatio = (double) letters / len;
        double symbolRatio = (double) symbols / len;

        if (letterRatio < 0.55) return false;

        // too many symbols → fake
        if (symbolRatio > 0.25) return false;

        // too short random mix
        if (len <= 4 && digits > 0) return false;

        return true;
    }

    // =========================
    // MAIN
    // =========================
    @Override
    public void run() throws Exception {

        println("=== OBJC OBFUSCATION RESOLVER START ===");

        File f = new File(outPath);
        PrintWriter out = new PrintWriter(f);

        Memory mem = currentProgram.getMemory();

        for (MemoryBlock b : mem.getBlocks()) {

            if (!b.isInitialized() || !b.isRead())
                continue;

            Address a = b.getStart();
            Address end = b.getEnd();

            while (a.compareTo(end) < 0 && !monitor.isCancelled()) {

                String s = readRaw(a);

                if (s != null) {

                    if (isLikelyReal(s)) {

                        if (!seen.contains(s)) {

                            seen.add(s);

                            String line = "[OBJC] " + s;

                            println(line);
                            out.println(line);
                        }

                        a = a.add(s.length() + 1);

                    } else {
                        a = a.add(1);
                    }
                } else {
                    a = a.add(1);
                }
            }
        }

        out.close();

        println("\nDONE");
        println("Unique resolved: " + seen.size());
        println("Dump saved to: " + outPath);
    }
}