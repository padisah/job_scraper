package com.padicrawler.extractors

import org.apache.commons.httpclient._
import org.apache.commons.httpclient.methods._
import org.apache.commons.httpclient.protocol._
import java.util._
import java.util.zip.GZIPInputStream
import org.apache.commons.httpclient.util.EncodingUtil
import scala.collection.mutable.ArrayBuffer
import java.io.FileOutputStream
import java.io.PrintStream


object ExtractJobsFromKite {
    val connectionManager = new MultiThreadedHttpConnectionManager();
    val http = new Protocol("http", new DefaultProtocolSocketFactory(), 80)
    
    def main(args : Array[String]) {
        Protocol.registerProtocol("http", http)
        val client = getClient(http)
        val method = new PostMethod("http://www.kite.hu/php/allasajanlat_feldolgozo.php");
        method.setParameter("value", "karrier2 selected=osszes;karrier1 selected=osszes;")
        
        val responseCode = client.executeMethod(method)
        if (responseCode==200) {
          val responseBody = getZippedContent(method)
          val urlCollections = new ArrayBuffer[String]()
          var index = responseBody.indexOf("href=");
          while (index > 0) {
            var endIndex = responseBody.indexOf(" ", index + "href=".length())
            if (endIndex > 0 && endIndex < responseBody.length() - 2) {
              urlCollections.+=(responseBody.substring(index + "href=".length() + 1, endIndex -1))
            }
            index = responseBody.indexOf("href=", endIndex);
          }
          try {
              val out = new PrintStream(new FileOutputStream("kite.txt"))
              urlCollections.map((s : String) => "http://www.kite.hu" + s).map((s1: String) => out.println(s1))
              out.close()
          } catch {
            case e : Exception => { e.printStackTrace()}
          }
        } else {
            System.out.println("Error: " + responseCode)
        }
    }
    
    def getZippedContent(method : HttpMethod) : String  = {
        val gzipStream = new GZIPInputStream(method.getResponseBodyAsStream())
        val buffer = new Array[Byte](1000000)
        var readBytes = 0
        var position = 0
        while (readBytes >= 0) {
            readBytes = gzipStream.read(buffer, position, buffer.length - position)
            position += readBytes
        }
        method.releaseConnection()
        return EncodingUtil.getString(buffer, 0, position, "UTF-8")
    }
    
    def getClient(protocol : Protocol) : HttpClient = {
        val retVal = new HttpClient(connectionManager)
        val params = connectionManager.getParams()
        params.setConnectionTimeout(100000)
        params.setSoTimeout(100000)
        params.setSendBufferSize(10000)
        params.setReceiveBufferSize(10000)
        params.setMaxTotalConnections(100)
        params.setDefaultMaxConnectionsPerHost(1)
        retVal.getParams().setConnectionManagerTimeout(3000)
        val hostConf = retVal.getHostConfiguration()
        val headers = new ArrayList[Header]();
        headers.add(new Header("User-Agent",
                "Mozilla/5.0 (compatible; JobKeresoBot; +http://www.kozvetlen-allasok.hu/help.jsp; info@kozvetlen-allasok.hu; 5.0"));
        headers.add(new Header("Referer", "http://www.kozvetlen-allasok.hu"))
        headers.add(new Header("Accept-Encoding", "gzip,deflate,sdch"))
        headers.add(new Header("Accept-Language","hu,en-US;q=0.8,en;q=0.6,fr;q=0.4,de;q=0.2"))
        headers.add(new Header("Origin", "http://www.kite.hu"))
        hostConf.getParams().setParameter("http.default-headers", headers)
        return retVal;
    }
    
}