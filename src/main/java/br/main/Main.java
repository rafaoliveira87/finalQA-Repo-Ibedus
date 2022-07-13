package br.main;

import br.support.PreProcessing;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String args[]) {

        /*
        String[] urls = {"",
        "www.novomilenio.br",
        "27 - 3207-5554",
        "www.fucape.br",
        "www.uninassau.edu.br",
        "www.fan.com.br",
        "http://funortejanauba.com.br",
        "null",
                " ",
                "www.unidom.com.br",
        "https://www.univem.edu.br/" };
        System.out.println("Meu pre processamento ... ");

        PreProcessing processing = new PreProcessing(urls);
        processing.setUrls();

         */

        JSONObject objNothing = new JSONObject();
        objNothing.put("Type", "Nothing");
        objNothing.put("Desc", "trash or nothing filled");

        JSONArray idsNothing = new JSONArray();
        idsNothing.add("id: "+123);
        idsNothing.add("id: "+122);
        idsNothing.add("id: "+124);

        objNothing.put("ids", idsNothing);

        JSONObject objHTTPs = new JSONObject();
        objHTTPs.put("Type", "HTTPS");
        objHTTPs.put("Desc", "domains that are working with HTTPS");

        JSONArray idsHTTPS = new JSONArray();
        idsHTTPS.add("id: "+111);
        idsHTTPS.add("id: "+222);
        idsHTTPS.add("id: "+333);

        objHTTPs.put("ids", idsHTTPS);

        JSONArray completeReport = new JSONArray();
        completeReport.add(objNothing);
        completeReport.add(objHTTPs);


        //Write JSON file
        try (FileWriter file = new FileWriter("reportIbedus")) {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(completeReport.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
            System.out.println("Done ...");
    }


}
