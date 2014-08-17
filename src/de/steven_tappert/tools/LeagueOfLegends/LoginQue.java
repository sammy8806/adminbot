package de.steven_tappert.tools.LeagueOfLegends;

import static de.steven_tappert.tools.http.html.getHTML;

public class LoginQue {

    public static String getLoginQue() {
        String html = getHTML("https://lq.eu.lol.riotgames.com/login-queue/");
        // html = html.replaceAll("<body>(.*)</body>","#");
        html = html.replaceAll("(.*)<body>", "");
        html = html.replaceAll("</body>(.*)", "");
        return html;
    }

}
