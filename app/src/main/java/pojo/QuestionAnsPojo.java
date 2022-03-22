package pojo;

import java.util.ArrayList;

public class QuestionAnsPojo {

    public int status;
    public String message;
    public ArrayList<ResponseData>  responsedata = new ArrayList<>();

    public class ResponseData{
        public String question;
        public String answer;
    }
}
