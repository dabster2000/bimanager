package dk.trustworks.bimanager.web;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import dk.trustworks.bimanager.web.widgets.ClientRevenueStatus;

/**
 * Created by hans on 23/09/15.
 */
@SuppressWarnings("serial")
@Theme("responsive_tutorial")
public class DashboardUI extends UI {

    class MenuLayout extends CssLayout {
        public MenuLayout() {
            setSizeFull();
            addStyleName("menulayout");
            Responsive.makeResponsive(this);

            VerticalLayout menu = new VerticalLayout();
            menu.setWidth(null);
            menu.setSpacing(true);
            menu.addStyleName("menu");
            addComponent(menu);

            for (int i = 1; i <= 5; i++) {
                menu.addComponent(new Button("Menu Item " + i));
            }
        }
    }

    class Dashboard extends CssLayout {
        public Dashboard() {
            setSizeFull();
            addStyleName("dashboard");
            Responsive.makeResponsive(this);

            for (int i = 1; i <= 1; i++) {
                addComponent(createPanel("Dashboard Widget " + i));
            }
        }
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        MenuLayout root = new MenuLayout();
        root.addComponent(new Dashboard());
        setContent(root);
    }

    private Component createPanel(String caption) {
        Panel panel = new Panel(caption);
        panel.setSizeUndefined();

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        panel.setContent(layout);

        //Label content = new Label("Content for " + caption.toLowerCase());
        layout.addComponent(ClientRevenueStatus.getChart());

        return panel;
    }
}
