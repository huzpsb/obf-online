package obf.online;

import nano.http.bukkit.api.BukkitServerProvider;
import nano.http.d2.console.Logger;
import nano.http.d2.consts.Mime;
import nano.http.d2.consts.Status;
import nano.http.d2.core.Response;
import nano.http.d2.hooks.HookManager;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Scanner;

public class BukkitMain extends BukkitServerProvider {
    @Override
    public void onEnable(String name, File dir, String uri) {
        HookManager.fileHook = (f, u) -> u.equals("/upload");
        Logger.info("Welcome to use the sample online obfuscator!");
    }

    @Override
    public void onDisable() {
        Logger.info("Bye!");
    }

    @Override
    public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
        return null;
    }

    @Override
    public Response fallback(String uri, String method, Properties header, Properties parms, Properties files) {
        if (uri.equals("/")) {
            return new Response(Status.HTTP_OK, Mime.MIME_HTML, BukkitMain.class.getResourceAsStream("index.html"));
        } else if (uri.equals("/upload")) {
            try {
                String tmpdir = System.getProperty("java.io.tmpdir");
                File temp = File.createTempFile("NanoHTTPd", "", new File(tmpdir));
                String input = files.getProperty("file");
                String output = temp.getAbsolutePath();
                Process p = Runtime.getRuntime().exec(new String[]{"jre\\bin\\java", "-jar", "obfer.jar", input, output});
                Scanner sc = new Scanner(p.getErrorStream());
                while (sc.hasNextLine()) {
                    Logger.info(sc.nextLine());
                }
                p.waitFor();
                Response resp = new Response(Status.HTTP_OK, Mime.MIME_DEFAULT_BINARY, new FileInputStream(temp));
                resp.addHeader("Content-Disposition", "attachment; filename=\"obf.jar\"");
                return resp;
            } catch (Exception ignored) {
            }
        } else {
            return new Response(Status.HTTP_NOTFOUND, Mime.MIME_PLAINTEXT, "Not Found");
        }
        return new Response(Status.HTTP_INTERNALERROR, Mime.MIME_PLAINTEXT, "Internal Error");
    }
}
