package com.ibedus;

import br.support.PreProcessing;
import com.ibedus.pojos.University;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
//import org.junit.Test;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static io.restassured.RestAssured.config;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

public class TestingPreProcessingFromAPI {

    //private static final String SERVICO = new String("https://api.testeib.com.br/api"); //endereço
    private static final String SERVICO = new String("https://api.ibedus.com.br/api");
    private static final String UNIVERSIDADE_STRING = new String("/universidades");
    private static final PreProcessing preProcessingURL = new PreProcessing();

    private static JSONObject objNull = new JSONObject(); // ok
    private static JSONArray  arrayIDsNull = new JSONArray(); //ok

    private static JSONObject objHTTPSwww = new JSONObject(); // ok
    private static JSONArray  arrayIDsHTTPSwww = new JSONArray(); //ok


    private static JSONObject objHTTPS = new JSONObject(); // ok
    private static JSONArray  arrayIDsHTTPS = new JSONArray(); //ok

    private static JSONObject objHTTPwww = new JSONObject(); // ok
    private static JSONArray  arrayIDsHTTPwww = new JSONArray(); //ok

    private static JSONObject objHTTP = new JSONObject(); // ok
    private static JSONArray  arrayIDsHTTP = new JSONArray(); //ok

    private static JSONObject objDown = new JSONObject(); // ok
    private static JSONArray  arrayIDsDown = new JSONArray(); //ok

    private static JSONObject objTimeout = new JSONObject(); // ok
    private static JSONArray  arrayIDsTimeout = new JSONArray(); //ok




    @BeforeAll
    static void setUpJsonFiles()
    {
        //initiate JSONObjects -  Null
        objNull.put("Type", "Null");
        objNull.put("Description", "trash or nothing filled in URL");

        // initiate JSONObjects -  HTTPSwww
        objHTTPSwww.put("Type", "HTTPS www");
        objHTTPSwww.put("Description", "best url with HTTPS + www");


        // initiate JSONObjects -  HTTPS
        objHTTPS.put("Type", "HTTPS");
        objHTTPS.put("Description", "best url with HTTPS");


        // initiate JSONObjects -  HTTP www
        objHTTPwww.put("Type", "HTTP www");
        objHTTPwww.put("Description", "best url with HTTP with www");

        // initiate JSONObjects -  HTTP
        objHTTP.put("Type", "HTTP");
        objHTTP.put("Description", "best url with HTTP");

        // initiate JSONObjects -  Down
        objDown.put("Type", "Down");
        objDown.put("Description", "domains be down/false positive");

        // initiate JSONObjects - Timeout
        objTimeout.put("Type", "Timeout");
        objTimeout.put("Description", "timeout");



    }

    @AfterAll
    static void closingReports()
    {
        objNull.put("ids", arrayIDsNull);
        objHTTPSwww.put("ids", arrayIDsHTTPSwww);
        objHTTPS.put("ids", arrayIDsHTTPS);
        objHTTPwww.put("ids", arrayIDsHTTPwww);
        objHTTP.put("ids", arrayIDsHTTP);
        objDown.put("ids", arrayIDsDown);
        objTimeout.put("ids", arrayIDsTimeout);


        JSONArray completeReport = new JSONArray();
        completeReport.add(objNull);
        completeReport.add(objHTTPSwww);
        completeReport.add(objHTTPS);
        completeReport.add(objHTTPwww);
        completeReport.add(objHTTP);
        completeReport.add(objDown);
        completeReport.add(objTimeout);

        //Write JSON file
        try (FileWriter file = new FileWriter("reportCompleteIbedusComplete.json"))
        {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(completeReport.toJSONString());
            file.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }


    @ParameterizedTest(name = "#{index} - EndPoint Universidades id ={0}")
    @CsvFileSource(resources = "/TestDataIbedus-thirty.csv") // 30 registers
    //@CsvFileSource(resources = "/TestDataIbedus-5Hundred.csv") // 500 registers
    //@CsvFileSource(resources = "/TestDataIbedus-Tousand.csv") // 1k registers
    //@CsvFileSource(resources = "/TestDataIbedus-Universities.csv") // 4k+ registers
   // @ValueSource(ints = {2300, 2345,  2346, 1800, 2300, 567, 978}) // some registers
   // @ValueSource(ints = { 2346}) // some registers
   // @ValueSource(ints = {2300})
   // @Timeout(value = 10, unit = TimeUnit.SECONDS)
    @DisplayName("Quando eu buscar um curso valido pelo endpoint"+
            " Entao a API retorna uma string valida ...")
    @Test
    public void itHasURL(int idIbedus)
    {
                    RestAssured.baseURI = SERVICO+UNIVERSIDADE_STRING;
                    RequestSpecification httpRequest = RestAssured.given();//.relaxedHTTPSValidation().when();
                    Response response = httpRequest.get("/"+Integer.toString(idIbedus));

                    //RestAssured.given().useRelaxedHTTPSValidation().contentType(ContentType.JSON).when().get()
                    University university = response.as(University.class);
                    // verificar se consigo puxar a serialização somente do campo website ...

                    //  System.out.println("---------------------------------------------------");
                    //  System.out.println("Id:"+university.getID()+" Name:"+university.getName());
                    //  System.out.println("\t Domain: "+university.getWebsite());


                    // System.out.println(" aqui :\""+university.getWebsite()+"\"");
                    if (university.getWebsite().equals("null")  ||
                            university.getWebsite().contains("null") ||
                            university.getWebsite().equals("")  ||
                            university.getWebsite().equals(" ") ||
                            university.getWebsite().equals(null)
                    ) {
                        //devo incluir um ID no nothing
                        // System.out.println("CASO 1");
                        arrayIDsNull.add("id: "+university.getID());
                        fail();
                        return;
                    } else {
                        //  System.out.println("CASO 2");
                        String pureURL = preProcessingURL.getPureURL(university.getWebsite());
                        // System.out.println("Clean domain: " + pureURL);
                        //tentar com HTTPS+WWW

                        try{
                          //  Assertions.assertTimeoutPreemptively(
                             //       Duration.ofSeconds(1), () -> {
                                        if (validURL("https://www." + pureURL))
                                        {
                                            //   System.out.println("CASO 2.1");
                                            arrayIDsHTTPSwww.add("id: "+university.getID());
                                            return;
                                            // add no array de HTTPS+WWW
                                        }
                                 //   },
                                //    "Timeout aqui "+idIbedus);
                                //arrayIDsTimeout.add("id: "+university.getID());
                            }
                        catch (Exception e){
                           // if (true)
                             //   System.out.println("Pegamos 1");
                        }

                        try {
                            if (validURL("https://" + pureURL)) {
                                //   System.out.println("CASO 2.2");
                                arrayIDsHTTPS.add("id: " + university.getID());
                                return;
                            }}
                        catch (Exception e)
                        {}

                        try{
                            if (validURL2("http://www." + pureURL))
                            {
                                // System.out.println("CASO 2.3");
                                arrayIDsHTTPwww.add("id: "+university.getID());
                                return ;
                            }}
                        catch(Exception e) {}
                        try{
                            if (validURL2("http://" + pureURL))
                            {
                                //  System.out.println("CASO 2.4");
                                arrayIDsHTTP.add("id: "+university.getID());
                                return ;
                            }}
                        catch(Exception e) {}

                        // System.out.println("CASO 2.5");
                        arrayIDsDown.add("id: "+university.getID());
                        fail();


                        // System.out.println("\t Pure domain: " + "\t" + reportComplement(pureURL));
                        //  System.out.println("\t www.:        " + "\t" + reportComplement("www." + pureURL));
                        //  System.out.println("\t https://" + "\t" + reportComplement("https://" + pureURL));
                        //  System.out.println("\t https://www." + "\t" + reportComplement("https://www." + pureURL));
                        // System.out.println("\t http://" + "\t" + reportComplement("http://" + pureURL));
                        // System.out.println("\t http://www." + "\t" + reportComplement("http://www." + pureURL));
                    }
                    //  System.out.println("---------------------------------------------------");
                    //  }catch (Exception e)
                    //  {
                    //   System.out.println("Null domain 2");
                    //     e.printStackTrace();
                    //    System.out.println("CASO 3");
                    //      arrayIDsNull.add("id: "+university.getID());
                    //  fail();
                    //    return ;
                    //  System.out.println("---------------------------------------------------");
                    // }




    }

   /* private String reportComplement(String url)
    {
        try {
            if (validURL(url))
                return "PASS " + url;
            else
                return "FAIL " + url;
        }catch (Exception e)
        {
            return "FAIL " + url;
        }
    }*/


    private boolean validURL(String urlUniversity)
    {
         RestAssured.baseURI = urlUniversity;
        // Response webSiteResponse = RestAssured.given().relaxedHTTPSValidation().when().get();
        Response webSiteResponse = RestAssured.given().get();
         //System.out.println(" url \""+urlUniversity+" "+":  status: "+ webSiteResponse.getStatusCode());
        if (webSiteResponse.getStatusCode() == 200)
            return true;
        else
            return false;
    }
    private boolean validURL2(String urlUniversity)
    {
        RestAssured.baseURI = urlUniversity;
        Response webSiteResponse = RestAssured.given().relaxedHTTPSValidation().when().get();
        //System.out.println(" url \""+urlUniversity+" "+":  status: "+ webSiteResponse.getStatusCode());
        if (webSiteResponse.getStatusCode() == 200)
            return true;
        else
            return false;
    }
}
