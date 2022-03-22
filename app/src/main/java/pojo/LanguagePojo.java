package pojo;

import java.util.ArrayList;

public class LanguagePojo {


    public int status;
    public String message;
    public ArrayList<ResponseData> responsedata = new ArrayList<>();

    public class ResponseData{

        public int id;
        public String name;
        public String full_name;
        public int is_show;
        public int is_selected;

    }
}
