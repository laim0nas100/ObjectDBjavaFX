/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.objectdbjavafx;

import java.net.URL;
import java.util.ResourceBundle;
import lt.lb.commons.javafx.scenemanagement.Frame;
import lt.lb.commons.javafx.scenemanagement.InjectableController;

/**
 *
 * @author laim0nas100
 */
public class LauncherController implements InjectableController{

    @Override
    public void inject(Frame frame, URL url, ResourceBundle rb) {
    }

    @Override
    public void initialize() {
    }
    
    public void begin(){
        
        this.exit();
    }
    
    public void bootstrap(){
        Bootstrap.bootstrapMeta();
        Bootstrap.bootstrapFileSystem();
    }
    
}
