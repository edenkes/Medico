package bredesh.medico;

/**
 * Created by edenkes on 3/3/2017.
 */

public class TabMessage {
    public static String get(int menuItemId, boolean isReselection) {
        String message = "Content for ";

        switch (menuItemId) {
            case R.id.tab_recents:
                message += "recents";
                break;
            case R.id.tab_favorites:
                message += "favorites";
                break;
            case R.id.tab_home:
                message += "home";
                break;
            case R.id.tab_personal:
                message += "personal";
                break;
            case R.id.tab_camera:
                message += "camera";
                break;
        }

        if (isReselection) {
            message += " WAS RESELECTED! YAY!";
        }

        return message;
    }
}
