package kz.gamma.webra.services.client.test;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by y_makulbek
 * Date: 29.03.2010 9:53:20
 */
public class GenerateDName {

    public ArrayList<String> name = new ArrayList<String>();
    public ArrayList<String> sureName = new ArrayList<String>();
    public ArrayList<String> org = new ArrayList<String>();
    public ArrayList<String> iin = new ArrayList<String>();
    public Random rndIIN1 = new Random();
    public Random rndIIN2 = new Random();
    public Random rndorg = new Random();
    public Random rndname = new Random();
    public Random rndsurename = new Random();

    public GenerateDName() {
        try{
            readOrgIin();
            readName();
            readSureName();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getUserSurname(){
        return sureName.get(rndsurename.nextInt(sureName.size()));
    }

    public String getUserName(){
        return name.get(rndname.nextInt(name.size()));
    }

    public String getUserOrg(){
        String orgO = org.get(rndorg.nextInt(org.size()));
        return orgO.replaceAll("[,+]", "");
    }

    public String getUserSN(){
        return ((Integer) rndIIN1.nextInt(1000000)).toString() + ((Integer) rndIIN2.nextInt(1000000)).toString();
    }

    public String getDName(){
        String sn =((Integer) rndIIN1.nextInt(1000000)).toString() + ((Integer) rndIIN2.nextInt(1000000)).toString();
        String tmp1 = sureName.get(rndsurename.nextInt(sureName.size()));
        String tmp2 = name.get(rndname.nextInt(name.size()));
        String cn = tmp1.substring(0,1).toUpperCase() + tmp1.substring(1) + " " + tmp2.substring(0,1).toUpperCase() + tmp2.substring(1);
        String orgO = org.get(rndorg.nextInt(org.size()));
        orgO = orgO.replaceAll("[,+]", "");

        String dNameStr = "C=KZ, O=" + orgO + ", CN=" + cn + ", SERIALNUMBER=" + sn;
        return dNameStr;
    }

    public void readName() throws Exception {
        LineNumberReader lnr = new LineNumberReader(new FileReader("name.txt"));
        String line = lnr.readLine().trim();
        while(line != null) {
            if(line.equals("")) break;
            line = line.trim();
            name.add(line);
            line = lnr.readLine();
        }
    }
    public void readSureName() throws Exception {
        LineNumberReader lnr = new LineNumberReader(new FileReader("surname.txt"));
        String line = lnr.readLine().trim();
        while(line != null) {
            if(line.equals("")) break;
            line = line.trim();
            sureName.add(line);
            line = lnr.readLine();
        }
    }
    public void readOrgIin() throws Exception {
        LineNumberReader lnr = new LineNumberReader(new FileReader("orgIIN.txt"));
        String line = lnr.readLine().trim();
        int i;
        String orgN;
        while(line != null) {
            if(line.equals("")) break;
            line = line.trim();
            i = line.indexOf("||");
            if(i>0){
                orgN = line.substring(i+2);
                String[] orgTmp = orgN.split("\\s");
                if(orgTmp.length>=4){
                    orgN = orgTmp[0] + " " + orgTmp[1] + " " + orgTmp[2] + " " + orgTmp[3];
                }
                org.add(orgN);
            }
            line = lnr.readLine();
        }
    }
}
