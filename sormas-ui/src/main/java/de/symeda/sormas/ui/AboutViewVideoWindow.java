package de.symeda.sormas.ui;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Video;
import com.vaadin.ui.Window;

public class AboutViewVideoWindow extends UI {
    
	
	
    public String viewVid(String one, String two) {
        // Some other UI content
        setContent(new Label("Here's my UI"));

        // Create a sub-window and set the content
        Window subWindow = new Window("Sub-window");
        VerticalLayout subContent = new VerticalLayout();
        subContent.setMargin(true);
        subWindow.setContent(subContent);

        // Put some components in it
        subContent.addComponent(new Label("VIDEO TEST"));
        
        Video v = new Video( "video" ); // Instantiate video player widget.
		// Specify a list of your video in one or more formats.
		// Different browsers support various different video formats.
		v.setSources( 
		    new ThemeResource( "img/1_a_LoginLogout_subtitles.mp4" )
		    
		); 
		v.setWidth( "640px" ); // Set size of the video player's display area on-screen.
		v.setHeight( "360px" );
		
		
        subContent.addComponent(v);

        // Center it in the browser window
        subWindow.center();

        // Open it in the UI
        addWindow(subWindow);
        return "";
    }

	@Override
	protected void init(VaadinRequest request) {
		// TODO Auto-generated method stub
		
	}
}