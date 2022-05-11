package de.symeda.sormas.ui;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Video;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

public class AboutViewVideoWindow extends Window {
	public AboutViewVideoWindow(String caption, String url) {
        super(caption); // Set window caption
        center();

        // Some basic content for the window
        VerticalLayout content = new VerticalLayout();
        
        Video v = new Video(); // Instantiate video player widget.
		// Specify a list of your video in one or more formats.
		// Different browsers support various different video formats.
		v.setSources( 
		    new ThemeResource( url )
		    
		); 
		//v.setResponsive(true);
		v.setWidth( "640px" ); // Set size of the video player's display area on-screen.
		v.setHeight( "360px" );
		
		
        content.addComponent(v);
        content.setMargin(true);
        setContent(content);

        // Disable the close button
        setClosable(true);

        // Trivial logic for closing the sub-window
        Button ok = new Button("Close Video");
        ok.addClickListener(e -> {
          
                close(); // Close the sub-window
            
        });
        
        
        content.addComponent(ok);
    }
}
