package com.equivi.demailer.web.constant;


public enum MenuUrlPathMapping {

    MENU_EMAIL_COLLECTOR("/main/emailcollector", MenuConstant.EMAIL_COLLECTOR, ""),
    MENU_EMAIL_VERIFIER("/main/emailverifier", MenuConstant.EMAIL_VERIFIER, ""),
    MENU_REPORT("/main/report", MenuConstant.REPORT, ""),
    MENU_USER_MANAGEMENT("/main/admin/users", MenuConstant.MENU_ADMINISTRATION, MenuConstant.SUB_MENU_USER_MANAGEMENT);

    private String urlPath;

    private String menu;

    private String subMenu;

    MenuUrlPathMapping(String urlPath, String menu, String subMenu) {
        this.urlPath = urlPath;
        this.menu = menu;
        this.subMenu = subMenu;
    }

    public static MenuUrlPathMapping getMenuUrlPathMapping(String urlPath) {
        for (MenuUrlPathMapping menuUrlPathMapping : MenuUrlPathMapping.values()) {
            if (urlPath.contains(menuUrlPathMapping.getUrlPath())) {
                return menuUrlPathMapping;
            }
        }

        return null;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getSubMenu() {
        return subMenu;
    }

    public void setSubMenu(String subMenu) {
        this.subMenu = subMenu;
    }
}
