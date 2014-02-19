
package se.rhel.gradletest;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import se.rhel.CodeName;

public class DesktopLauncher {
	public static void main (String[] arg) {

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.title = "CodeName";
        config.useGL20 = true;
        config.width = 1280;
        config.height = 720;
        config.vSyncEnabled = true;
        config.resizable = false;

        if(arg.length > 0) {
            new LwjglApplication(new CodeName(Integer.valueOf(arg[0])), config);
        } else {
            new LwjglApplication(new CodeName(), config);
        }
	}
}
