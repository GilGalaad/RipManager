package ripmanager;

import ripmanager.gui.RipManagerImpl;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Launcher {

    public static void main(String[] args) {
        var lookAndFeel = Arrays.stream(UIManager.getInstalledLookAndFeels()).filter(i -> i.getName().equals("Windows")).findFirst();
        if (lookAndFeel.isPresent()) {
            try {
                UIManager.setLookAndFeel(lookAndFeel.get().getClassName());
            } catch (Exception ex) {
                // falling back to default look and feel
            }
        }
        EventQueue.invokeLater(() -> {
            RipManagerImpl app = new RipManagerImpl();
            app.setLocationRelativeTo(null);
            app.setVisible(true);
        });
    }

}
