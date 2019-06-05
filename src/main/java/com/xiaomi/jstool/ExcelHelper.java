package com.xiaomi.jstool;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND;

public class ExcelHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelHelper.class);

    /**
     * en       ru      fr
     * key1     v11     v12      v13
     * key2     v21     v22      v23
     **/

    public static void fillExcel(Map<String, JSONObject> map, XSSFWorkbook workbook, XSSFSheet sheet) throws IOException, JSONException {
        //把文件名作为列表头（0行，从第一列开始）
        List countryList = new ArrayList();
        List jsonList = new ArrayList();
        int count = 1;
        XSSFRow rowTitle = sheet.createRow(0);
        for (String key : map.keySet()) {
            XSSFCell cellFileName = rowTitle.createCell(count++);
            cellFileName.setCellValue(key);
            countryList.add(key);
        }
        //把map的JONObject 中的key作为行表头，value作为值填充进去（0列，从第一行开始）
        int rowColloum = 1;
//        for (Map.Entry<String, JSONObject> entry : map.entrySet()) {

            JSONObject jsonObject = map.get(countryList.get(0));
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                XSSFRow row = sheet.createRow(rowColloum++);
                XSSFCell cellKey = row.createCell(0);
                String jsonKey = (String) iterator.next();
//                String value = entry.getValue().getString(jsonKey);//这里可以根据实际类型去获取
                cellKey.setCellValue(jsonKey);
                jsonList.add(jsonKey);
                System.out.println("jsonKey" + jsonKey);
            }
//        }

        for (int t = 0; t < jsonList.size(); t++) {
                List jsonValueList = new ArrayList();
            for (int r = 0; r < countryList.size(); r++) {
                String country = countryList.get(r).toString();
                String jsonKey = jsonList.get(t).toString();
                XSSFRow row = sheet.createRow(t + 1);
                if (country.equalsIgnoreCase("ch") || country.equalsIgnoreCase("en")) {
                    XSSFCell cellValue = row.createCell(r + 1);
                    cellValue.setCellValue(map.get(country).getString(jsonKey));
                    jsonValueList.add(map.get(country).getString(jsonKey));
                } else {
                    XSSFCell cellValue = row.createCell(r + 1);
                    cellValue.setCellValue(map.get(country).getString(jsonKey));
                    jsonValueList.add(map.get(country).getString(jsonKey));
                    if (map.get(country).getString(jsonKey).equals(map.get("zh").getString(jsonKey)) ||
                            map.get(country).getString(jsonKey).equals(map.get("en").getString(jsonKey))) {
                        XSSFCellStyle style = workbook.createCellStyle();
                        style.setFillForegroundColor((short) 40);
                        style.setFillPattern(SOLID_FOREGROUND);
                        cellValue.setCellStyle(style);
                    }
                }
            }
            System.out.println("jsonValueList is "+jsonValueList);
        }

//            for (int j = 0; j < entry.getValue().size(); j++) {
////                System.out.println("entry.getValue().size()"+entry.getValue().size()+"key"+entry.getKey());
//                if(j==MainEntry.EN ||j==MainEntry.ZH_CN){
//                    XSSFCell cellValue = row.createCell(j + 1);
//                    cellValue.setCellValue(entry.getValue().get(j));
//                }else {
//                    XSSFCell cellValue = row.createCell(j + 1);
//                    cellValue.setCellValue(entry.getValue().get(j));
//                    if(((MainEntry.EN) < entry.getValue().size()) && (MainEntry.ZH_CN < entry.getValue().size())) {
//                        if (entry.getValue().get(j).equals(entry.getValue().get(MainEntry.EN)) ||
//                                entry.getValue().get(j).equals(entry.getValue().get(MainEntry.ZH_CN))) {
//                            XSSFCellStyle style = workbook.createCellStyle();
//                            style.setFillForegroundColor((short) 40);
//                            style.setFillPattern(SOLID_FOREGROUND);
//                            cellValue.setCellStyle(style);
//                        }
//                    }
//                }
//            }

//        }
    }

    @Test
    public void testJSONObject() throws JSONException {
        String key = null;
        String value = null;
        String jsonString = "{\"rowTitle10\":\"新手引導\",\"rowTitle21\":\"裝置共用\",\"rowTitle20\":\"牙刷標籤\",\"rowTitle12\":\"健康知識\",\"rowTitle23\":\"刪除裝置\",\"rowTitle11\":\"快速指南\",\"rowTitle22\":\"檢查韌體更新\",\"title\":\"設定\",\"navTitle1\":\"使用條款\",\"navTitle2\":\"隱私政策\",\"tip_reRequestData\":\"重新請求數據...\",\"rowTitle14\":\"使用條款和隱私政策\",\"rowTitle13\":\"常見問題\",\"rowTitle15\":\"關於\",\"sectionTitle2\":\"一般設定\",\"tip_connectingDevice\":\"正在連接裝置...\",\"sectionTitle1\":\"mihomeStr\"}";
//然后用Iterator迭代器遍历取值，建议用反射机制解析到封装好的对象中
        JSONObject jsonObject = new JSONObject(jsonString);
        Iterator iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            value = jsonObject.getString(key);
            System.out.println("key is " + key);
            System.out.println("value is " + value);
        }
    }
    @Test
    public void test(){
        List list = new ArrayList();
        list .add(1);
        list .add(2);
        list .add(3);
        list .add(4);
        System.out.println(list.indexOf(3));

    }
    @Test
    public void test05(){
        String s = "zh123456string78922zh123";
        for (int i=0;;i++){
            if(s.startsWith("zh")){
                s= StringUtils.remove(s,"zh");
                System.out.println(s);
            }else {
                break;
            }

        }

    }
    @Test
    public void test06(){
        String s ="12:78\",";
        s = "\""+s.replaceFirst(":","\":");
        System.out.println(s.indexOf("\""));
        System.out.println(s);
    }
    @Test
    public void etst07(){
      char s = ' ';
        System.out.println((int) s);
        String ss = "\u00a0";
        System.out.println(ss);


    }
    @Test
    public void test08(){
        String s = "7891,";
        String kk=StringUtils.removeEnd(s,",");
        System.out.println(kk);
    }
    @Test
    public void test09(){
        String s = "7891,.js000.js";
        Boolean ss = StringUtils.containsAny(s,"en","tw");
        System.out.println(ss);

    }
    @Test
    public void test10() throws JSONException {
        String path = "/Users/huamiumiu/Desktop/rn框架/mapOut.txt";
        Map<String,JSONObject> map = new HashMap<String, JSONObject>();
        String jsonString1 ="{\"title\": \"Historia\",\"unit_score\": \"punkt\"}";
        String jsonString2 ="{\"dateUnit_year\": \"Rok\",\"dateUnit_month\": \"Miesiąc\"}";
        JSONObject jsonObject1 = new JSONObject(jsonString1);
        JSONObject jsonObject2 = new JSONObject(jsonString2);
        map.put("en",jsonObject1);
        map.put("ru",jsonObject2);
        try {
            FileOutputStream outStream = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
            objectOutputStream.writeObject(map);
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public  void testList() throws IOException {
        String path = "/Users/huamiumiu/Desktop/rn框架/listOut.txt";
        List list = new ArrayList();
        list.add("1");
        list.add("11");
        list.add("12");
        list.add("13");
        List list2 = new ArrayList();
        list2.add("21");
        list2.add("211");
//        List<List<String>> listList = new ArrayList<List<String>>();
//        listList.add(list);
//        listList.add(list2);
        BufferedWriter writer = new BufferedWriter(new FileWriter(path, false));
        for (int j = 0; j < list.size(); j++) {
            writer.write(list.get(j) + ",");
        }
        writer.newLine();
        writer.flush();
        for (int j = 0; j < list2.size(); j++) {
            writer.write(list2.get(j) + ",");
        }
        writer.close();
    }
    @Test
    public void testMap(){
        Map map = new HashMap();
            for (int  i= 4; i < 8; i++) {
//                map.put(i, );
                System.out.println("in"+map);
            }
            System.out.println("out"+map);

    }
    @Test
    public void testFile() throws IOException {
//        String path = "/Users/huamiumiu/Desktop/rn框架/LocalizedStrings";
        String path = "/Users/huamiumiu/Desktop/data";
        File file = new File(path);
//        File file2 = file.getParentFile();
//        System.out.println("file2: "+file2);
//        file2.setWritable(true);
//        file2.setExecutable(true);
//        file2.setReadable(true);
        if(file.exists()){
            //删除
        }
        Boolean flag = file.mkdirs();
        System.out.println(file.createNewFile());
        System.out.println(flag);

    }
    @Test
    public void testBase(){
       String str = "frBase = {\"setting\": \"Réglages\",\"featureSetting\": \"Paramètres de fonction\",\"commonSetting\": \"Réglages généraux\",\"deviceName\": \"Nom de l\\'appareil\",\"locationManagement\": \"Gérer les emplacements\",\"shareDevice\": \"Partager l\\'appareil\",\"ifttt\": \"Automatisation\",\"firmwareUpgrate\": \"Vérifier les mises à jour du micrologiciel\",\"moreSetting\": \"Plus de paramètres\",\"addToDesktop\": \"Ajouter à l\\'écran d\\'accueil\",\"resetDevice\": \"Supprimer l\\'appareil\",\"licenseAndPolicy\": \"Accord de l\\'utilisateur et politique de confidentialité\",\"feedback\": \"Aide\",\"picker_hour\": \"h\",\"picker_minite\": \"m\",\"app_name\": \"SmartPlug\",\"hello_world\": \"Bonjour le monde!\",\"gateway_magnet_location_updating\": \"Mise à jour en cours…\",\"minute\": \"minute\",\"hour\": \"heure \",\"day\": \"jour\",\"minutes\": \"minutes\",\"hours\": \"heures \",\"days\": \"jours\",\"later\": \"auparavant\",\"plug_timer_yesterday\": \"Hier\",\"plug_timer_after_tommorrow\": \"après-demain\",\"plug_timer_month_day\": \"%1$s-%2$s\",\"plug_switch\": \"Allumé/éteint\",\"plug_timer\": \"Régler la minuterie\",\"plug_count_down_timer\": \"Programmer\",\"time_line_start\": \"00:00\",\"time_line_end\": \"24:00\",\"power_on\": \"Mettre sous tension\",\"power_off\": \"Mettre hors tension\",\"usb_on\": \"Connecteur USB activé\",\"usb_off\": \"Connecteur USB désactivé\",\"close\": \"Se mettra hors tension dans \\n%1$s\",\"open\": \"Se mettra sous tension dans \\n%1$s\",\"count_down_msg_minute\": \"%2$s minutes plus tard%1$s\",\"count_down_msg_hour_minute\": \"%2$s heures%3$sminutes plus tard%1$s\",\"count_down_msg_hour\": \"%2$s heures plus tard%1$s\",\"count_down_msg_day\": \"%2$s jours%3$sheures%4$s minutes plus tard%1$s\",\"ok\": \"OK\",\"title_timer_power\": \"Régler la minuterie\",\"title_timer_usb\": \"Réglage de la minuterie du connecteur USB\",\"title_count_down_timer_power\": \"Programmer\",\"title_count_down_timer_usb\": \"Compte à rebours du connecteur USB\",\"set_timer_conflict\": \"Le réglage actuel de l’heure est en conflit avec l’heure définie précédemment. Appliquer l’heure actuelle?\",\"i_know\": \"Compris\",\"set_failed\": \"Impossible de terminer la configuration\",\"get_failed\": \"Mise à jour impossible\",\"delete_failed\": \"Impossible de supprimer\",\"temp_high_alert_title\": \"Le connecteur a surchauffé. Veuillez en déterminer la cause avant nouvelle utilisation.\",\"temp_high_alert_msg1\": \"1. \\ u0020 L’appareil peut consommer plus d’énergie que la limite max.\",\"temp_high_alert_msg2\": \"2. \\ u0020 Il est possible que la prise CA soit connectée à la prise smart\",\"temp_high_alert_msg5\": \"\\u0020\\u0020\\u0020 peut être mal branché ou rouillé.\",\"temp_high_alert_msg3\": \"3. \\ u0020Les fiches sur l’équipement utilisant l’énergie peuvent être anciennes ou rouillées.\",\"temp_high_alert_msg4\": \"4.\\u0020 La température ambiante peut être trop élevée.\",\"settings_sending_notification\": \"Rappels d’heure planifiés\",\"settings_sending_notification_tips\": \"Envoyer un message pour vous avertir de l’activation de la prise.\",\"settings_general\": \"Paramètres généraux\",\"about\": \"À propos\",\"more_function\": \"Plus de fonctions\",\"noti_overheat_title\": \"La fiche surchauffe\",\"noti_overheat_msg\": \"La fiche surchauffe, veuillez y remédier immédiatement.\",\"noti_device_title\": \"La fiche est%1$s est déjà%2s.\",\"noti_device_title_power\": \"Puissance\",\"noti_device_title_usb\": \"USB\",\"noti_device_title_open\": \"Allumer\",\"noti_device_title_close\": \"Éteindre\",\"one_minute\": \"1minute\",\"three_minute\": \"3minutes\",\"five_minute\": \"5minutes\",\"ten_minute\": \"10minutes\",\"twenty_minute\": \"20minutes\",\"forty_minute\": \"40minutes\",\"sixty_minute\": \"60minutes\",\"customize_minute\": \"Personnalisée\",\"start\": \"Début\",\"cancel\": \"Annuler\",\"stop\": \"Arrêt\",\"timer_emtpy\": \"Aucune minuterie réglée\",\"plug_seat_high_temp\": \"La fiche surchauffe.\",\"plug_seat_high_temp_tips\": \"Veuillez en déterminer la cause avant nouvelle utilisation.\",\"status_on\": \"Activé\",\"status_off\": \"Désactivé\",\"device_desc_220v\": \"Prise 220V:\",\"device_desc_USB\": \"USB:\",\"max_timer_count_tips\": \"Aucune autre minuterie ne peut être ajoutée. Veuillez supprimer la précédente avant de réessayer.\",\"device_scene\": \"Automatisation\",\"over_heat_tips\": \"La fiche surchauffe et le mode sans échec est activé.\",\"wifi_led_name\": \"Indicateur lumineux\",\"share\": \"Appareils partagés\",\"ok1\": \"OK\",\"virtual_dialog_title\": \"Impossible de fonctionner sur un périphérique virtuel\",\"virtual_dialog_positive\": \"J’en prendrai un aussi!\",\"plugseat_v1_keyword\": \"SmartPlugv1\",\"merge_old_timer_data\": \"Migration d’anciennes données...\",\"old_timer_upgrade_tips\": \"Conseils pour mise à jour d’heure planifiée\",\"old_timer_upgrade_msg\": \"Une fois la prise mise à jour, l’heure planifiée définie dans l’application peut être mémorisée dans la prise. La configuration prend effet même lorsque le réseau Wi-Fi n’est pas connecté.\",\"upgrade\": \"Mise à jour\",\"upgrading\": \"Mise à jour en cours…\",\"cancel_upgrading\": \"Annuler la mise à jour\",\"continue_upgrading\": \"Poursuivre la mise à jour\",\"cancel_upgrading_tips\": \"Annuler la mise à jour?\",\"cancel_upgrading_msg\": \"L’annulation de la mise à jour supprimera vos paramètres d’heure actuels et l’heure planifiée n’aura pas d’effet si aucun réseau Wi-Fi n’est connecté.\",\"upgrade_suc\": \"Mise à jour réussie\",\"upgrade_fail\": \"Mise à jour impossible\",\"retry\": \"Réessayez\",\"disclaim_title\": \"Conditions\",\"disclaim_dialog_title\": \"Conditions\",\"disclaim_content\": \"J’ai déjà lu le contrat d’utilisation et la politique de confidentialité.\",\"plug_disclaimer_detail_1\": \"L’utilisation des prises Smart de toute version avec cette application est considérée comme une acceptation volontaire des présents contrat d’utilisation et politique de confidentialité.\",\"plug_disclaimer_detail_2\": \"Nous ne pouvons pas garantir que les minuteries de prises, les télécommandes, les liaisons de périphériques, etc. fonctionneront de façon complètement normale. Nous ne recommandons pas de les utiliser dans des endroits où cela peut \n" +
               "ituer un danger pour la sécurité.\",\"plug_disclaimer_detail_3\": \"Lorsque vous activez la fonction «Mémoire de panne d’électricité de la prise», une fois l’alimentation rétablie, la fiche reprendra le statut qu’elle avait avant la panne, c’est-à-dire que si elle était éteinte avant la panne d’électricité, elle demeurera éteinte, et que si elle était allumée avant la panne d’électricité, elle se rallumera. Par conséquent, nous ne recommandons pas de l’utiliser dans des endroits où cela peut \n" +
               "ituer un danger pour la sécurité.\",\"plug_disclaimer_detail_4\": \"La société décline toute responsabilité quant à l’incapacité de la SmartPlug de fonctionner correctement si elle est causée par des facteurs tels que les cyberattaques, les pannes de réseau, les retards de réseau, les équipements hors ligne, etc. Cependant, nous ferons de notre mieux pour réduire toute perte ou toute incidence occasionnée pour l’utilisateur.\",\"plug_disclaimer_detail_5\": \"La société ne peut être tenue responsable des pertes résultant d’une utilisation incorrecte de ce produit.\",\"agree_and_continue\": \"Accepter et Continuer\",\"tips_share_read_only\": \"Veuillez ajuster les autorisations et réessayer.\",\"string_look_history_data\": \"Afficher plus\",\"string_now_all_power\": \"Puissance actuelle\",\"string_power_data\": \"Statistiques de puissance\",\"string_today_power_data\": \"Consommation électrique journalière (kWh):\",\"string_no_power_data\": \"Pas d’informations sur la consommation électrique\",\"string_month_power_data\": \"Consommation électrique mensuelle\",\"string_yesterday_power_data\": \"Électricité utilisée hier:\",\"string_today_open_time\": \"Durée d’utilisation quotidienne:\",\"string_today_switch_count\": \"Heures de marche/arrêt aujourd’hui: %s\",\"string_du\": \"kWh\",\"string_shi\": \"heures\",\"string_fen\": \"minutes\",\"string_ci\": \"fois\",\"string_month_data\": \"Transfert d’anciennes données…\",\"string_week_data\": \"Détails hebdomadaires sur l’alimentation\",\"string_day_data\": \"Détails quotidiens sur l’alimentation\",\"string_month\": \"Mois\",\"string_week\": \"Semaine\",\"string_day\": \"Jour\",\"string_power_du_data\": \"Consommation électrique (kWh)\",\"string_use_time\": \"Durée de la consommation (hh:mm)\",\"string_is_loading\": \"Chargement en cours…\",\"string_1\": \"Veuillez faire attention. Votre appareil est surchargé.\",\"string_2\": \"La fiche surchauffe et a été éteinte.\",\"string_3\": \"La fiche est trop chaude. Veuillez en déterminer la cause.\",\"string_4\": \"Une nouvelle version du micrologiciel disponible\",\"string_5\": \"Hier\",\"string_6\": \"Aujourd'hui\",\"string_7\": \"La semaine dernière\",\"string_8\": \"Cette semaine\",\"string_9\": \"Le mois dernier\",\"string_10\": \"Ce mois-ci\",\"string_11\": \"Mois M\",\"string_12\": \"Interrupteur 220V allumé\",\"string_13\": \"Interrupteur 220V éteint\",\"string_14\": \"CommutateurUSB activé\",\"string_15\": \"CommutateurUSB désactivé\",\"ios_sting_1\": \"Je l’ai déjà lu.\",\"ios_sting_2\": \"«Conditions générales de la SmartPlug»\",\"ios_sting_3\": \"Chargement en cours…\",\"ios_sting_4\": \"Installation en cours…\",\"ios_sting_5\": \"Paramètres du voyant\",\"ios_sting_6\": \"Installation en cours…\",\"ios_sting_7\": \"Commentaires\",\"agreement_title\": \"Contrat d'utilisation\",\"policy_title\": \"Politique de confidentialité\",\"day_sun\": \"Dim.\",\"day_mon\": \"Lun.\",\"day_tue\": \"Mar.\",\"day_wed\": \"Mer.\",\"day_thu\": \"Jeu.\",\"day_fri\": \"Ven.\",\"day_sat\": \"Sam.\",\"power_on_v3\": \"Plug power is on\",\"power_off_v3\": \"Plug power is off\",};";
        System.out.println(str.indexOf("="));
        System.out.println(str.indexOf("};"));
        System.out.println(str.substring(str.indexOf("="),str.indexOf("};")).trim().replace("=",""));
    }
    @Test
    public void testJO() throws JSONException {
        JSONObject jb = new JSONObject();
        for(int i=0;i<2;i++){
            jb.put(String.valueOf(i),String.valueOf(i));
        }
        System.out.println(jb);
    }






}


